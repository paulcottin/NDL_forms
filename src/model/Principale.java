package model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import exceptions.MessageException;
import exceptions.MyException;
import exceptions.NoBDDException;
import exceptions.SilentException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;
import vues.Fenetre;
import vues.ProgressBar;

public class Principale extends Application{

	private ObservableList<Formulaire> forms;
	private File dbFile;
	private ProgressBar progressBar;

	public Principale() {
		this.forms = FXCollections.observableArrayList();
		try {
			parseXML();
		} catch (MessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Fenetre(this, new BorderPane(),400,forms.size()*30+50);
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(e -> Platform.exit());
		primaryStage.show();
	}

	@SuppressWarnings("unchecked")
	private void parseXML() throws NumberFormatException, MyException {
		SAXBuilder sax = new SAXBuilder();
		Document doc = null;
		try {
			doc = sax.build(new File("data.xml"));
		} catch (JDOMException e) {
			throw new MessageException("Erreur lors de la lecture du fichier \"data.xml\" !");
		} catch (IOException e) {
			throw new MessageException("Fichier \"data.xml\" corrompu ou introuvable !");
		}
		Element root = doc.getRootElement();
		List<Element> forms = root.getChild("forms").getChildren("form");

		for (Element element : forms) {
			this.forms.add(new Formulaire(Integer.valueOf(element.getAttributeValue("id"))));
		}

		dbFile = new File(root.getChildText("dbName"));
		try {
			if (!dbFile.exists()) selectDBFile();
		} catch (NoBDDException e) {
			System.exit(-1);
		}
	}

	private void selectDBFile() throws MessageException {
		//Demande du fichier de base de donnée
		FileChooser fchooser = new FileChooser();
		fchooser.setTitle("Sélection du fichier de base de donnée");
		ExtensionFilter filter = new ExtensionFilter("BDD Access", ".accdb");
		fchooser.setSelectedExtensionFilter(filter);
		dbFile = fchooser.showOpenDialog(null);
		if ( dbFile == null) throw new NoBDDException("Il faut sélectionner une base de donnée !");
	}

	public void createForm() throws IOException, MyException {
		//Demande de l'URL et du nom de la table
		Dialog<Pair<String, String>> bddDialog = new Dialog<>();
		bddDialog.setTitle("Connexion à la base de donnée");
		bddDialog.setHeaderText("Informations de connexion");

		ButtonType loginButtonType = new ButtonType("Valider", ButtonData.OK_DONE);
		bddDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		TextField urlField = new TextField();
		TextField nomTable = new TextField();
		grid.add(new Label("URL complète de la feuille de réponse du formulaire:"), 0, 0);
		grid.add(urlField, 1, 0);
		grid.add(new Label("Nom de la nouvelle table dans la base de donnée:"), 0, 1);
		grid.add(nomTable, 1, 1);

		bddDialog.getDialogPane().setContent(grid);
		Platform.runLater(() -> urlField.requestFocus());

		bddDialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(urlField.getText(), nomTable.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = bddDialog.showAndWait();

		if(!result.isPresent()) throw new SilentException();

		String url = result.get().getKey();
		String nom = result.get().getValue().replace(" ", "_");

		//Vérification de l'URL		
		try {
			URL ur = new URL(url);
			ur.openConnection();
		} catch (IOException e) {
			throw new MessageException("L'URL donnée n'existe pas !");
		}

		//Récupération de l'id à partir de l'URL
		//Partie du tab (=split(/)) la plus longue
		String[] tab = url.split("/");
		int max = 0, index = 0;
		for (int i = 0; i < tab.length; i++) {
			if (tab[i].length() > max) {
				max = tab[i].length();
				index = i;
			}
		}
		String code = tab[index];
		try {
			Formulaire f = new Formulaire(dbFile, nom, code);
			f.createTable();
			progressBar = f.getProgressBar();
			forms.add(f);

		} catch (MessageException e) {
			e.printMessage();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public ObservableList<Formulaire> getForms() {
		forms.clear();
		try {
			parseXML();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MyException e) {
			e.printStackTrace();
		}
		return forms;
	}

	public void setForms(ObservableList<Formulaire> forms) {
		this.forms = forms;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
}
