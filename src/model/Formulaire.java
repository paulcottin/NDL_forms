package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import exceptions.MessageException;
import exceptions.MyException;
import javafx.scene.control.Alert;
import model.tasks.CreateTable;
import model.tasks.UpdateTable;
import vues.ProgressBar;
import javafx.scene.control.Alert.AlertType;

public class Formulaire {

	private GoogleConnector google;
	private AccessConnector access;
	private String googleKey, nomTable, nom, dbName;
	private int id;
	private File dbFile;
	private ProgressBar progressBar;

	/**
	 * Utilisation du formulaire
	 * @param id ID de formulaire du fichier XML;
	 * @throws MyException
	 */
	public Formulaire(int id) throws MyException {
		this.id = id;
		try {
			readXML();
		} catch (MessageException e) {
			e.printMessage();
		}
		initConnectors();
	}

	/**
	 * Création d'un nouveau formulaire (nouvelle Table Access)
	 * @param googleKey
	 * @throws MyException 
	 * @throws IOException 
	 */
	public Formulaire(File bdd, String nomTable, String googleKey) throws MyException, IOException {
		this.dbFile = bdd;
		this.nomTable = nomTable;
		this.googleKey = googleKey;
		initConnectors();
	}

	public void createTable() {
		CreateTable create = new CreateTable(id, google, access);
		progressBar = new ProgressBar(create);
		Thread th = new Thread(create);
		th.start();
	}
	
	public void updateAccess() throws IOException, MyException {
		UpdateTable update = new UpdateTable(id, google, access);
		progressBar = new ProgressBar(update);
		Thread th = new Thread(update);
		th.start();
	}

	@SuppressWarnings("unchecked")
	private void readXML() throws MessageException {
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
			if (element.getAttributeValue("id").equals(""+id)) {
				nom = element.getChildText("nom");
				googleKey = element.getChildText("googleKey");
				nomTable = element.getChildText("nomTable");
			}
		}

		dbName = root.getChildText("dbName");
		dbFile = new File(dbName);
	}

	private void initConnectors() throws MyException {
		initGoogleConnector();
		initAccessConnector();
	}

	private void initGoogleConnector() throws MyException {
		this.google = new GoogleConnector(googleKey);
	}

	private void initAccessConnector() throws MyException {
		this.access = new AccessConnector(dbFile, nomTable);
	}

	public String getGoogleKey() {
		return googleKey;
	}

	public void setGoogleKey(String googleKey) {
		this.googleKey = googleKey;
	}

	public String getNomTable() {
		return nomTable;
	}

	public void setNomTable(String nomTable) {
		this.nomTable = nomTable;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public File getDbFile() {
		return dbFile;
	}

	public void setDbFile(File dbFile) {
		this.dbFile = dbFile;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

}
