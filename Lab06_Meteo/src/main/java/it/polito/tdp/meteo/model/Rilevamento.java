package it.polito.tdp.meteo.model;

import java.util.Date;

public class Rilevamento {
	
	private String localita;
	private Date data;
	private int giorno;
	private int umidita;
	private float umiditaMedia;

	public Rilevamento(String localita, Date data, int umidita) {
		this.localita = localita;
		this.data = data;
		this.umidita = umidita;
		this.umiditaMedia = 0;
	}
	
	public Rilevamento(String localita,int giorno, int umidita) {
		this.localita = localita;
		this.giorno = giorno;
		this.umidita = umidita;
	}

	public int getGiorno() {
		return giorno;
	}

	public void setGiorno(int giorno) {
		this.giorno = giorno;
	}

	public Rilevamento(String string, float int1) {
		// TODO Auto-generated constructor stub
		this.localita = string;
		this.umiditaMedia = int1;
	}

	public String getLocalita() {
		return localita;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getUmidita() {
		return umidita;
	}

	public void setUmidita(int umidita) {
		this.umidita = umidita;
	}

	// @Override
	// public String toString() {
	// return localita + " " + data + " " + umidita;
	// }

	@Override
	public String toString() {
		return giorno+" "+localita+" "+umidita+"\n";
	}

	public float getUmiditaMedia() {
		return umiditaMedia;
	}

	public void setUmiditaMedia(float umiditaMedia) {
		this.umiditaMedia = umiditaMedia;
	}

	

}
