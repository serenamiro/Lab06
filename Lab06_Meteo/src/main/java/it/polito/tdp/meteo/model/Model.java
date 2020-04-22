package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	MeteoDAO dao;
	private Map<Integer, List<Rilevamento>> partenza;
	private int bestCosto = 0;
	private List<Rilevamento> bestSoluzione = null;
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		dao = new MeteoDAO();
		partenza = new HashMap<>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		List<String> localita = new LinkedList<String>();
		localita.add("Torino");
		localita.add("Genova");
		localita.add("Milano");
		List<Rilevamento> finali = new ArrayList<Rilevamento>();
		for (String  s : localita) {
			finali.addAll(dao.getAllRilevamentiLocalitaMese(mese, s));
		}
		String s = "";
		for(Rilevamento r : finali) {
			s += "\nLocalità: "+r.getLocalita()+"\nUmidità media: "+r.getUmiditaMedia()+"%\n";
		}
		return s;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		Citta Torino = new Citta("Torino");
		Citta Genova = new Citta("Genova");
		Citta Milano = new Citta("Milano");
		
		Torino.setRilevamenti(dao.getAllRilevamentiLocalita15GiorniMese(mese, "Torino"));
		Genova.setRilevamenti(dao.getAllRilevamentiLocalita15GiorniMese(mese, "Genova"));
		Milano.setRilevamenti(dao.getAllRilevamentiLocalita15GiorniMese(mese, "Milano"));
		
		List<Rilevamento> lista = new ArrayList<>();
		lista.addAll(Torino.getRilevamenti());
		lista.addAll(Genova.getRilevamenti());
		lista.addAll(Milano.getRilevamenti());
		
		for(Rilevamento r : lista) {
			List<Rilevamento> temp = partenza.get(r.getGiorno());
			if(temp == null) {
				// nuovo inserimento
				List<Rilevamento> nuova = new ArrayList<>();
				nuova.add(r);
				partenza.put(r.getGiorno(), nuova);
			} else {
				// esiste già la chiave
				partenza.get(r.getGiorno()).add(r);
			}
		}
		
		
		List<Rilevamento> parziale = new LinkedList<Rilevamento>();
		
		cerca(partenza, parziale, 1);
		
		String s = "Costo totale: "+bestCosto+"\n";
		for(Rilevamento r : bestSoluzione) {
			s += r.getGiorno()+" "+r.getLocalita()+" "+r.getUmidita()+"\n";
		}
		return s;
	}

	private void cerca(Map<Integer, List<Rilevamento>> partenza, List<Rilevamento> parziale, int L) {
		// casi terminali
		int contatore = contaCitta(parziale);
		if(contatore == 3 ) {
			if(contaGiornate(parziale)) {
				int costo = calcolaCosto(parziale, contatore);
				if(costo > bestCosto) {
					bestCosto = costo;
					bestSoluzione = new ArrayList<>(parziale);
				}
			}
		}
		
		// non ho ancora visitato tutte le città
		if(L == NUMERO_GIORNI_TOTALI) {
			return;
			// vuol dire che ho finito le giornate a disposizione ma
			// non ho visitato tutte le città
		}
		
		for(Rilevamento r : partenza.get(L)) {
			parziale.add(r);
			//parziale.add(rilevamento(partenza.get(L+1), r.getLocalita()));
			//parziale.add(rilevamento(partenza.get(L+2), r.getLocalita()));
			cerca(partenza, parziale, L+1);
			parziale.remove(r);
			//parziale.remove(rilevamento(partenza.get(L+1), r.getLocalita()));
			//parziale.remove(rilevamento(partenza.get(L+2), r.getLocalita()));
		}
	}
	
	private Rilevamento rilevamento(List<Rilevamento> lista, String Citta) {
		for(Rilevamento r : lista) {
			if (r.getLocalita().equals(Citta)) {
				return r;
			}
		}
		return null;
	}

	private boolean contaGiornate(List<Rilevamento> parziale) {
		int giorniTo = 0;
		int giorniGe = 0;
		int giorniMi = 0;
		for(Rilevamento r : parziale) {
			if (r.getLocalita().equals("Torino"))
				giorniTo++;
			else if (r.getLocalita().equals("Milano"))
				giorniMi++;
			else if(r.getLocalita().equals("Genova"))
				giorniGe++;
		}
		if(giorniTo>NUMERO_GIORNI_CITTA_MAX || giorniMi>NUMERO_GIORNI_CITTA_MAX || giorniGe>NUMERO_GIORNI_CITTA_MAX)
			return false;
		return true;
	}

	private int calcolaCosto(List<Rilevamento> parziale, int contatore) {
		int costo = COST*(contatore-1);
		for(Rilevamento r : parziale) {
			costo += r.getUmidita();
		}
		return costo;
	}

	private int contaCitta(List<Rilevamento> parziale) {
		Set<String> contatore = new HashSet<String>();
		for(Rilevamento r : parziale) {
			contatore.add(r.getLocalita());
		}
		return contatore.size();
	}
	
	
	

}
