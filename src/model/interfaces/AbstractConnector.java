package model.interfaces;

import java.util.ArrayList;
import exceptions.MyException;
import model.Ligne;

public abstract class AbstractConnector {
	
	protected ArrayList<Ligne> lignes;
	
	public AbstractConnector() throws MyException {
		this.lignes = new ArrayList<Ligne>();
	}

	public abstract void queryLignes() throws MyException;
	protected abstract void refresh() throws MyException;
	public abstract void connect() throws MyException;

	public ArrayList<Ligne> getLignes() {
		return lignes;
	}

	public void setLignes(ArrayList<Ligne> lignes) {
		this.lignes = lignes;
	}
	
	protected ArrayList<String> securiseHeader(ArrayList<String> header) {
		for (String string : header) {
			if (string.length() > 60)
				string = secureString(string);
		}
		return header;
	}
	
	protected String[] securiseHeader(String[] header) {
		String[] ok = new String[header.length];
		for (int i = 0; i < ok.length; i++) {
			ok[i] = secureString(header[i]);
		}
		return ok;
	}
	
	private String secureString(String string) {
		if (string.length() > 60)
			string = string.substring(0, 60);
		if (string.contains("'"))
			string = string.replace("'", "_");
		if (string.contains("."))
			string = string.replace(".", "");
		while (string.length() > 1 && string.substring(0, 1).equals(" "))
			string = string.substring(1);
		return string;
	}
	
}
