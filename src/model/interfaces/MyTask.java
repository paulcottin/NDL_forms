package model.interfaces;

import java.io.IOException;

import exceptions.MessageException;
import exceptions.MyException;
import javafx.concurrent.Task;
import model.AccessConnector;
import model.GoogleConnector;

public abstract class MyTask extends Task<Void>{
	
	protected GoogleConnector google;
	protected AccessConnector access;
	protected int id;
	protected int nbLignesModif;
	protected String taskTitle;
	
	public MyTask(int id, GoogleConnector google, AccessConnector access) {
		this.id = id;
		this.google = google;
		this.access = access;
		this.nbLignesModif = 0;
		this.taskTitle = "";
	}

	@Override
	protected Void call() throws Exception {
		try {
			execute();
		} catch (MessageException e) {
			e.printMessage();
		} catch (MyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected abstract void execute() throws MessageException, MyException, IOException;

	public int getNbLignesModif() {
		return nbLignesModif;
	}

	public void setNbLignesModif(int nbLignesModif) {
		this.nbLignesModif = nbLignesModif;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

}
