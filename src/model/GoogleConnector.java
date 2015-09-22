package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import exceptions.MessageException;
import exceptions.MyException;
import model.interfaces.AbstractConnector;

public class GoogleConnector extends AbstractConnector{

	private String googleID;
	private DriveQuickstart drive;
	private String fileName;

	public GoogleConnector(String googleID) throws MyException {
		super();
		this.googleID = googleID;
	}

	private void waitCSV(){
		boolean pending = true;
		while (pending) {
			if (drive.getFileOutputStream() != null)
				pending = false;
		}
		try {
			drive.getFileOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse le fichier tmp.csv qui contient les données de la feuille Google
	 * @throws MyException
	 */
	@Override
	public void queryLignes() throws MyException {
		BufferedReader br = null;
		Reader in;
		Iterable<CSVRecord> records = null;
		String[] headerTab = null;
		try {
			in = new InputStreamReader(new FileInputStream(new File("tmp.csv")), "utf-8");
			br = new BufferedReader(in);
			String header = br.readLine();
			br.close();
			headerTab = securiseHeader(updateHeader(header));
			in = new InputStreamReader(new FileInputStream(new File("tmp.csv")), "utf-8");
			records = CSVFormat.EXCEL.withHeader(headerTab).parse(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (CSVRecord record : records) {
			Ligne ligne = new Ligne(headerTab);
			for (int i = 0; i < record.size(); i++) {
				ligne.add(headerTab[i], record.get(i));
			}
			lignes.add(ligne);
		}
	}

	private String[] updateHeader(String header) {
		String[] old = header.split(",");
		ArrayList<String> l = new ArrayList<String>();
		for (int i = 0; i < old.length; i++) {
			if (!l.contains(old[i]))
				l.add(old[i]);
			else {
				int cpt = 1;
				while (l.contains(old[i]+cpt))
					cpt++;
				l.add(old[i]+cpt);
			}
		}

		String[] tab = new String[old.length];
		for (int i = 0; i < tab.length; i++) {
			tab[i] = l.get(i);
		}
		
		tab = securiseHeader(tab);

		String newHeader = String.join(",", tab);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("tmp.csv"), "utf-8"));
			String ligne = "";
			ArrayList<String> content = new ArrayList<String>();

			while ((ligne = br.readLine()) != null)
				content.add(ligne);
			br.close();
			content.set(0, newHeader);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tmp.csv"), "utf-8"));
			for (String string : content)
				bw.write(string+"\r\n");
			bw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tab;
	}

	@Override
	protected void refresh() throws MyException {
		// TODO Auto-generated method stub

	}
	
	public void connect() throws MessageException {
		try {
			this.drive = new DriveQuickstart(googleID);
		} catch (IOException e) {
			throw new MessageException("Erreur de connexion à Google !");
		}
		fileName = drive.getFileName();
		waitCSV();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getGoogleID() {
		return googleID;
	}

	public void setGoogleID(String googleID) {
		this.googleID = googleID;
	}

}