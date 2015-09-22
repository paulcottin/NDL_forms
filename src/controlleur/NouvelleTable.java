package controlleur;

import java.io.IOException;

import exceptions.MessageException;
import exceptions.MyException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.Principale;

public class NouvelleTable implements EventHandler<ActionEvent>{
	
	private Principale principale;
	
	public NouvelleTable(Principale principale) {
		this.principale = principale;
	}

	@Override
	public void handle(ActionEvent event) {
		try {
			principale.createForm();
		} catch (MessageException e) {
			e.printMessage();
		} catch (IOException e) {
			(new MessageException(e)).printMessage();
		} catch (MyException e) {
			e.printStackTrace();
		}
	}

}
