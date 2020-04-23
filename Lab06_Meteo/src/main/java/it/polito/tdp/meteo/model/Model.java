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
	private int bestCosto = 152699;
	private List<Rilevamento> bestSoluzione = new ArrayList<>();
	
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
		
		
		List<Rilevamento> parziale = new ArrayList<Rilevamento>();
		
		cerca(parziale, 1);
		
		String s = "Costo totale: "+bestCosto+"\n";
		for(Rilevamento r : bestSoluzione) {
			s += r.getGiorno()+" "+r.getLocalita()+" "+r.getUmidita()+"\n";
		}
		return s;
	}

	private void cerca(List<Rilevamento> parziale, int L) {
		// casi terminali
		
		if(!contaGiornate(parziale)) {
			return; 
		}
		
		if(!contaGiorniMin(parziale)) {
			return;
		}

		if(parziale.size() == NUMERO_GIORNI_TOTALI) {
			int costo = calcolaCosto(parziale);
			if(costo < bestCosto) {
				bestCosto = costo;
				bestSoluzione = new ArrayList<>(parziale);
			}
		}
		
		if(partenza.get(L) == null) {
			return;
		}
		List<Rilevamento> sottoproblema = partenza.get(L);
		
		for(Rilevamento r : sottoproblema) {
			parziale.add(r);
			cerca(parziale, L+1);
			parziale.remove(parziale.size()-1);
		}
	}
	
	private boolean contaGiorniMin(List<Rilevamento> parziale) {
		String loc = "";
		int count = 0;
		boolean x = true;
		
		for(int i = 0; i<parziale.size(); i++) {
			if(x == false) {
				if(parziale.get(i).getLocalita().equals(loc)) {
					count++;
				} else {
					x = true;
					if (count < 3) {
						return false;
					}
				}
			}
			if(x == true) {
				loc = parziale.get(i).getLocalita();
				count = 1;
				x = false;
			}
		}
		if(parziale.size()==NUMERO_GIORNI_TOTALI && count<3) {
			return false;
		}
		
		return true;
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

	private int calcolaCosto(List<Rilevamento> parziale) {
		int costo = COST*(contaCitta(parziale));
		for(Rilevamento r : parziale) {
			costo += r.getUmidita();
		}
		return costo;
	}
	
	
	private int contaCitta(List<Rilevamento> parziale) {
		String loc = "";
		int count = 0;
		boolean x = true;
		
		loc = parziale.get(0).getLocalita();
		
		for(int i=1; i<parziale.size(); i++) {
			if(!parziale.get(i).getLocalita().equals(loc)){
				count++;
			}
			loc = parziale.get(i).getLocalita();
		}
		
		return count;
	}
	
	
	

}
