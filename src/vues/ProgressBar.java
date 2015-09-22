package vues;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.interfaces.MyTask;

public class ProgressBar extends Stage {
	
	private javafx.scene.control.ProgressBar bar;
	
	public ProgressBar(MyTask task) {
		super();
		this.setWidth(450);
		this.setHeight(80);
		this.titleProperty().bind(task.titleProperty());
		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				close();
				success(task.getTitle(), task.getNbLignesModif());
			}
		});
		task.setOnRunning(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				show();
			}
		});
		bar = new javafx.scene.control.ProgressBar();
		bar.setPrefWidth(420);
		
		AnchorPane pane = new AnchorPane(bar);
		this.setScene(new Scene(pane));
	}
	
	private void success(String message, int nbLignes) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Mise à jour effectuée !");
		alert.setHeaderText(null);
		alert.setContentText(message+"\n "+nbLignes+" lignes ajoutée(s)");
		alert.showAndWait();
	}
}
