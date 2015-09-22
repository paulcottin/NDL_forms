package vues;

import controlleur.NouvelleTable;
import controlleur.Quitter;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.Principale;

public class MenuBar extends javafx.scene.control.MenuBar{

	private Principale principale;
	private Menu fichier, formulaires;
	private MenuItem quitter, creerTable;
	
	public MenuBar(Principale principale) {
		super();
		this.principale = principale;
		init();
		construct();
	}
	
	private void init() {
		fichier = new Menu("Fichier");
		quitter = new MenuItem("Quitter");
		quitter.addEventHandler(ActionEvent.ACTION, new Quitter());
		
		formulaires = new Menu("Actions");
		creerTable = new MenuItem("Créer une table Access à partir d'un formulaire Google");
		creerTable.addEventHandler(ActionEvent.ACTION, new NouvelleTable(principale));
	}
	
	private void construct() {
		fichier.getItems().add(quitter);
		
		formulaires.getItems().add(creerTable);
		
		this.getMenus().addAll(fichier, formulaires);
	}
}
