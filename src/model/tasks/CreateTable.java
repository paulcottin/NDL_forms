package model.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;

import exceptions.MessageException;
import exceptions.MyException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import model.AccessConnector;
import model.GoogleConnector;
import model.Ligne;
import model.interfaces.MyTask;

public class CreateTable extends MyTask{

	private String nom;
	
	public CreateTable(int id, GoogleConnector google, AccessConnector access) {
		super(id, google, access);
		this.nom = null;
		taskTitle = "Création d'une table";
		updateTitle(taskTitle);
	}

	@Override
	protected void execute() throws MessageException, MyException, IOException {
		google.connect();
		google.queryLignes();
		//On récupère les données de Google
		ArrayList<Ligne> ajout = new ArrayList<Ligne>();
		for (Ligne l : google.getLignes())
			if (l.getPrimaryValue() != null) {
				ajout.add(l);
			}
		ArrayList<String> gHeader = google.getLignes().get(0).getHeader();

		//On vérifie que la table n'existe pas déjà
		System.out.println("check");
		Database db = DatabaseBuilder.open(access.getDatabaseFile());
		for (String string : db.getTableNames()) {
			if (string.equals(access.getTableName())) {
				System.out.println("match");
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Table existante !");
				alert.setHeaderText("Une table portant le même nom existe déjà !");
				alert.setContentText("Ecraser la table ?");
				Optional<ButtonType> result = alert.showAndWait();
				System.out.println("show");
				if (result.get() == ButtonType.OK)
					deleteTableWithoutConfirmation(db, string);
				else
					throw new MessageException("Impossible d'ajouter une table du même nom !");
			}
		}
		//On crée la table dans Access
		TableBuilder tableBuilder = new TableBuilder(access.getTableName());
		for (String string : gHeader) {
			//					tableBuilder.addColumn(new ColumnBuilder(securiseHeader(string), DataType.TEXT));
			tableBuilder.addColumn(new ColumnBuilder(string, DataType.TEXT));
		}
		Table table = tableBuilder.toTable(db);
		//On ajoute les lignes provenant de Google
		for (Ligne ligne : ajout) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < gHeader.size(); i++) {
				map.put(gHeader.get(i), ligne.get(gHeader.get(i)));
			}

			table.addRowFromMap(map);
		}
		db.close();

		//mise à jour des constantes de la classe
		nom = google.getFileName();
		nbLignesModif = ajout.size();
		createFormXML();
	}
	
	private void deleteTableWithoutConfirmation(Database db, String tableName) throws IOException {
		Table table = db.getTable(tableName);
		Row row = null;
		while ((row = table.getNextRow()) != null) 
			table.deleteRow(row);
		table.reset();
	}
	
	@SuppressWarnings("unchecked")
	public void createFormXML() throws MessageException {
		//On récupère le plus grand ID de formulaire
		SAXBuilder sax = new SAXBuilder();
		Document doc = null;
		File data = new File("data.xml");
		try {
			doc = sax.build(data);
		} catch (JDOMException e) {
			throw new MessageException("Erreur lors de la lecture du fichier \"data.xml\" !");
		} catch (IOException e) {
			throw new MessageException("Fichier \"data.xml\" corrompu ou introuvable !");
		}
		Element root = doc.getRootElement();
		List<Element> forms = root.getChild("forms").getChildren("form");
		int max = 0;
		for (Element element : forms) {
			if (Integer.valueOf(element.getAttributeValue("id")) > max)
				max = Integer.valueOf(element.getAttributeValue("id"));
		}
		id = max+1;

		//Création des infos
		Element form = new Element("form");
		form.setAttribute(new Attribute("id", id+""));
		Element nom = new Element("nom");
		nom.setText(this.nom);
		Element googleKey = new Element("googleKey");
		googleKey.setText(google.getGoogleID());
		Element nomTable = new Element("nomTable");
		nomTable.setText(access.getTableName());

		form.getChildren().add(nom);
		form.getChildren().add(googleKey);
		form.getChildren().add(nomTable);
		root.getChild("forms").getChildren().add(form);

		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		try {
			sortie.output(doc, new FileOutputStream(data));
		} catch (FileNotFoundException e) {
			throw new MessageException("Fichier \"data.xml\" non trouvé !");
		} catch (IOException e) {
			throw new MessageException("Erreur d'écriture dans \"data.xml\" !");
		}
	}
}
