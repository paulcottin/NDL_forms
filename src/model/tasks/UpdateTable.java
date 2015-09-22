package model.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

import exceptions.MessageException;
import exceptions.MyException;
import model.AccessConnector;
import model.GoogleConnector;
import model.Ligne;
import model.interfaces.MyTask;

public class UpdateTable extends MyTask{

	public UpdateTable(int id, GoogleConnector google, AccessConnector access) {
		super(id, google, access);
		taskTitle = "Mise à jour d'un formulaire";
		updateTitle(taskTitle);
	}

	@Override
	protected void execute() throws MessageException, MyException, IOException {
		google.connect();
		google.queryLignes();
		access.queryLignes();
		//On détermine le dernier ID Access
		Database db = access.getDb();
		Table table = access.getTable();
		//On détermine les lignes de Google qui ne sont pas dans Access
		ArrayList<Ligne> diff = new ArrayList<Ligne>();
		for (Ligne ligne : google.getLignes()) 
			if (ligne.getPrimaryValue() != null && !isInAccessLignes(access.getLignes(), ligne)) {
				ArrayList<String> data = new ArrayList<String>();
				ArrayList<String> header = new ArrayList<String>();
				data.add("0");
				data.addAll(ligne.getData());
				header.add("ID");
				header.addAll(ligne.getHeader());
				diff.add(new Ligne(data, header));
			}
		ArrayList<String> aHeader = access.getLignes().get(0).getHeader();
		//On ajoute ces lignes
		for (Ligne ligne : diff) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < aHeader.size(); i++) {
				map.put(aHeader.get(i), ligne.get(aHeader.get(i)));
			}
			table.addRowFromMap(map);
		}
		db.close();
		nbLignesModif = diff.size();
	}
	
	private boolean isInAccessLignes(ArrayList<Ligne> list, Ligne ligne) {
		boolean find = false;
		for (Ligne l : list) {
			if (l.getPrimaryValue().equals(ligne.getPrimaryValue()))
				find = true;
		}
		return find;
	}

}
