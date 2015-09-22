package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import exceptions.MessageException;
import exceptions.MyException;
import model.interfaces.AbstractConnector;

public class AccessConnector extends AbstractConnector{

	private File databaseFile;
	private String tableName;
	private Database db;
	private Table table;

	public AccessConnector(File database, String table) throws MyException {
		super();
		this.databaseFile = database;
		this.tableName = table;
		this.db = null;
		this.table = null;
	}

	@Override
	public void queryLignes() throws MyException {
		connect();
		ArrayList<String> header = new ArrayList<String>();
		for (Column col : table.getColumns()) {
			header.add(col.getName());
		}
		
		ArrayList<String> data = new ArrayList<String>();
		for (Row row : table) {
			for (String string : header) {
				if (row.get(string) == null) data.add("");
				else data.add(String.valueOf(row.get(string)));
			}
			lignes.add(new Ligne(data, header));
			data.clear();
		}
	}
	
	@Override
	public void connect() throws MessageException {
		try {
			db = DatabaseBuilder.open(databaseFile);
		} catch (IOException e) {
			throw new MessageException("Le fichier de connexion est introuvable ! \n"
					+ "(Il est peut être ouvert dans Access, dans ce cas, il faut fermer le programme Access)");
		}
		if (db == null) throw new MessageException("Impossible de se connecter à la base de données !");
		
		try {
			table = db.getTable(tableName);
		} catch (IOException e) {
			throw new MessageException("Impossible d'ouvrir la table \""+tableName+"\"");
		}
		if (table == null) throw new MessageException("L'ouverture de la table \""+tableName+"\" a échoué !");
	}

	@Override
	protected void refresh() throws MyException {
		// TODO Auto-generated method stub

	}

	public void deleteTable(String tableName) throws MessageException {
		Database db = null;
		try {
			db = DatabaseBuilder.open(databaseFile);

		} catch (IOException e) {
			throw new MessageException("Le fichier de connexion est introuvable ! \n"
					+ "(Il est peut être ouvert dans Access, dans ce cas, il faut fermer le programme Access)");
		}
		if (db == null) throw new MessageException("Impossible de se connecter à la base de données !");
		
		

	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public File getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(File databaseFile) {
		this.databaseFile = databaseFile;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
