package exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public abstract class MyException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String message;
	
	public MyException() {
		super();
		this.message = "";
	}
	
	public MyException(String message){
		super(message);
		this.message = message;
	}
	
	public MyException(Exception e) {
		message = e.getMessage() +"\n"+e.getClass()+"\n"+e.getCause();
	}
	
	protected void displayMessage(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erreur !");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
