package vues;

import controlleur.Update;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Formulaire;
import model.Principale;

public class Fenetre extends Scene{

	private Principale principale;
	private ObservableList<Button> update;
	private ObservableList<Label> labels;
	
	public Fenetre(Principale principale, Parent root, int x, int y) {
		super(root, x, y);
		this.principale = principale;
		this.update = FXCollections.observableArrayList();
		this.labels = FXCollections.observableArrayList();
		createWin();
	}
	
	private void createWin() {
		for (Formulaire form : principale.getForms()) {
			labels.add(new Label(form.getNom()));
			Button b = new Button("Mettre à jour");
			b.addEventHandler(ActionEvent.ACTION, new Update(form));
			update.add(b);
		}
		
		VBox vb = new VBox(5);
		for (int i = 0; i < labels.size(); i++) {
			BorderPane fp = new BorderPane();
			fp.setLeft(labels.get(i));
			HBox b = new HBox(5);
			b.getChildren().add(update.get(i));
			fp.setRight(b);
			vb.getChildren().add(fp);
		}
		
		((BorderPane) this.getRoot()).setCenter(vb);
		((BorderPane) this.getRoot()).setTop(new MenuBar(principale));
	}
	
	public void updateFenetre() {
		update.clear();
		labels.clear();
		createWin();
	}
}
