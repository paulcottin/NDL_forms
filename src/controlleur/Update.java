package controlleur;

import java.io.IOException;

import exceptions.MessageException;
import exceptions.MyException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.Formulaire;

public class Update implements EventHandler<ActionEvent>{
	
	Formulaire form;
	
	public Update(Formulaire form) {
		this.form = form;
	}

	@Override
	public void handle(ActionEvent event) {
		try {
			form.updateAccess();
		} catch (MessageException e) {
			e.printMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
