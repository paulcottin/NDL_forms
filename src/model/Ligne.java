package model;

import java.util.ArrayList;
import java.util.HashMap;

import exceptions.MessageException;

public class Ligne {

	private ArrayList<String> header;
	private HashMap<String, String> map;
	private String primaryKey, primaryValue;

	public Ligne(ArrayList<String> header) {
		this.header = header;
		this.map = new HashMap<String, String>();
		this.primaryKey = null;
		for (String string : header) {
			if (string.equalsIgnoreCase("horodateur"))
				this.primaryKey = string;
		}
		this.primaryValue = null;
	}

	public Ligne(String[] header) {
		this.header = new ArrayList<String>();
		for (int i = 0; i < header.length; i++) {
			this.header.add(header[i]);
		}
		this.map = new HashMap<String, String>();
		this.primaryKey = null;
		for (String string : header) {
			if (string.equalsIgnoreCase("horodateur"))
				this.primaryKey = string;
		}
		this.primaryValue = null;
	}

	public Ligne(ArrayList<String> data, ArrayList<String> header) throws MessageException {
		this.header = header;
		this.map = new HashMap<String, String>();
		if (header.size() != data.size()) throw new MessageException("Les données et le header n'ont pas la même taille");
		for (int i = 0; i < header.size(); i++) 
			map.put(header.get(i), data.get(i));
		this.primaryKey = null;
		for (String string : header) {
			if (string.equalsIgnoreCase("horodateur"))
				this.primaryKey = string;
		}
		this.primaryValue = map.get(primaryKey);
	}

	public Ligne(String[] data, ArrayList<String> header) throws MessageException  {
		this.header = header;
		this.map = new HashMap<String, String>();
		if (header.size() != data.length) throw new MessageException("Les données et le header n'ont pas la même taille");
		for (int i = 0; i < data.length; i++) 
			map.put(header.get(i), data[i]);
		this.primaryKey = null;
		for (String string : header) {
			if (string.equalsIgnoreCase("horodateur"))
				this.primaryKey = string;
		}
		this.primaryValue = map.get(primaryKey);
	}

	public ArrayList<String> toArray() {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < map.size(); i++) {
			result.add(map.get(header.get(i)));
		}
		return result;
	}

	public String[] toTab() {
		String[] tab = new String[map.values().size()];
		tab = (String[]) map.values().toArray();
		return tab;
	}

	public String get(String key) {
		if (!header.contains(key)) {
//			System.out.println("no key ("+key+"); header get : "+header.toString());
			throw new NullPointerException("Clé inexistante !");
		}
		return map.get(key);
	}

	public void add(String key, String value) {
		if (!header.contains(key)) throw new NullPointerException("Clé \""+key+"\" inexistante !");
		if (key.equalsIgnoreCase("horodateur")) if (!value.equalsIgnoreCase("horodateur")) primaryValue = value;
		map.put(key, value);
	}

	public void set(String key, String value) {
		if (!header.contains(key)) 
			throw new NullPointerException("Clé inexistante !");
		map.put(key, value);
	}

	public ArrayList<String> getHeader() {
		return header;
	}

	public void setHeader(ArrayList<String> header) {
		this.header = header;
	}
	
	public ArrayList<String> getData(){
		ArrayList<String> data = new ArrayList<String>();
		for (String key : map.keySet()) {
			data.add(map.get(key));
		}
		return data;
	}

	public int size() {
		return map.size();
	}

	public String toString() {
		String s = "";
		String[] keys = (String[]) map.keySet().toArray(new String[map.keySet().size()]);
		String[] values = (String[]) map.values().toArray(new String[map.keySet().size()]);
		for (int i = 0; i < map.size(); i++) {
			s += keys[i]+" => "+values[i] + "\n";
		}
		return s;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Ligne) {
			Ligne o = (Ligne) obj;
			if (o.getPrimaryValue().equals(this.primaryValue))
				return true;
			return false;
		}
		return false;
	}

	public String getPrimaryValue() {
		return primaryValue;
	}

	public void setPrimaryValue(String primaryValue) {
		this.primaryValue = primaryValue;
	}

	public HashMap<String, String> getMap() {
		return map;
	}

}
