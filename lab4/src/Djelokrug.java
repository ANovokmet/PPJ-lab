import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class Djelokrug {
	public ArrayList<Djelokrug> djecaDjelokrug;//nepotrebno? potrebno je
	public Djelokrug roditeljDjelokrug;//ugnjezdujuci blok
	public ArrayList<Cvor> cvorovi;
	
	public String uFunkciji;
	public boolean uPetlji;
	
	public String naziv;
	
	public HashMap<String, Informacija> tablica_lokalnih_imena;
	public HashMap<String, Integer> lokacije_lokalnih_imena;
	public ArrayList<String> globalna_imena;
	
	public int odmak;
	public int velTablice;
	public static int varOdmak; //odmak zbog varijabli
	
	public static ArrayList<String> deklFjeImena;
	public static ArrayList<String> deklFjeTipovi;
	public static ArrayList<ArrayList<String>> deklFjeTipoviPar;
	static {
		deklFjeImena = new ArrayList<String>();
		deklFjeTipovi = new ArrayList<String>();
		deklFjeTipoviPar = new ArrayList<ArrayList<String>>();
		varOdmak = 0;
		
	}
	
	
	public Djelokrug(Djelokrug roditeljDjelokrug){
		this.tablica_lokalnih_imena = new HashMap<String, Informacija>();
		this.lokacije_lokalnih_imena = new HashMap<String, Integer>();
		this.roditeljDjelokrug = roditeljDjelokrug;
		this.djecaDjelokrug = new ArrayList<Djelokrug>();
		this.uPetlji = false;
		odmak = 0;
		velTablice = 0;
		globalna_imena = new ArrayList<String>();
		
		if(roditeljDjelokrug!=null){//ako nije korijen
			roditeljDjelokrug.djecaDjelokrug.add(this);
		}
		
	}
	
	public boolean uPetlji(){
		if(roditeljDjelokrug==null){
			return uPetlji;
		}
		else if(!uPetlji){
			return roditeljDjelokrug.uPetlji();
		}
		else{
			return uPetlji;
		}
	}
	
	public void dodajIdentifikatorUTablicu(String tip, String naziv){
		Informacija novaInf = new Informacija(tip);
		
		if(tip.equals("int") || tip.equals("char")){
			novaInf.l_izraz=true;
		}
		else{
			novaInf.l_izraz=false;
		}
		
		tablica_lokalnih_imena.put(naziv, novaInf);
		
		if(roditeljDjelokrug!=null){
			sveLokalneUvecaj(1);
			
			lokacije_lokalnih_imena.put(naziv, 0);
			
			
			velTablice+= 1;
		}
		else{
			globalna_imena.add(naziv);
		}
		
	}
	
	private void sveLokalneUvecaj(int i){
		for(String key: lokacije_lokalnih_imena.keySet()){
			lokacije_lokalnih_imena.put(key, lokacije_lokalnih_imena.get(key)+i*4);
		}
	}
	
	
	public void dodajNizUTablicu(String tip, String naziv, int br_elem){
		tablica_lokalnih_imena.put(naziv, new Informacija(tip, br_elem));
		
		if(roditeljDjelokrug!=null){
			sveLokalneUvecaj(br_elem+1);
			
			lokacije_lokalnih_imena.put(naziv, 0);
			
			velTablice+= (br_elem+1);
		}
		else{
			globalna_imena.add(naziv);
		}
	}
	
	public Informacija dodajFunkcijuUTablicu(String tip, String naziv, ArrayList<String> tipovi){
		Informacija novaFunkcija = new Informacija(tip, tipovi);
		tablica_lokalnih_imena.put(naziv, novaFunkcija);
		
		deklFjeImena.add(naziv);
		deklFjeTipovi.add(tip);
		deklFjeTipoviPar.add(tipovi);
		
		return novaFunkcija;
	}
	
	public boolean sadrziIdn(String ime){
		if(tablica_lokalnih_imena.containsKey(ime)){
			return true;
		}
		else{
			if(roditeljDjelokrug!=null){
				return roditeljDjelokrug.sadrziIdn(ime);
			}
			else{
				return false;
			}
		}
	}
	
	public boolean samoUGlobalnom(String ime){
		if(roditeljDjelokrug==null){
			return globalna_imena.contains(ime);
		}
		else{
			if(tablica_lokalnih_imena.containsKey(ime)){
				return false;
			}
			else{
				return roditeljDjelokrug.samoUGlobalnom(ime);
			}
		}
	}
	
	public Informacija getIdentifikator(String ime){
		if(tablica_lokalnih_imena.containsKey(ime)){
			return tablica_lokalnih_imena.get(ime);
		}
		else{
			if(roditeljDjelokrug!=null){
				return roditeljDjelokrug.getIdentifikator(ime);
			}
			else{
				return null;
			}
		}
	}
	
	public String lokacija(String ime){
		if(samoUGlobalnom(ime)){
			return "G_"+ime;
		}
		else{
			return "R7+0"+Integer.toHexString(getOdmak(ime));
		}
	}
	
	
	
	//samo za one ne u globalnom
	
	public Integer getOdmak(String ime){//TODO
		if(lokacije_lokalnih_imena.containsKey(ime)){
			return lokacije_lokalnih_imena.get(ime)+varOdmak;
		}
		else{
			if(roditeljDjelokrug!=null){
				return roditeljDjelokrug.getOdmak(ime)+velTablice*4;
			}
			else{
				return null;
			}
		}
	}
	
	
	
	
	public boolean sadrziFju(String ime){
		if(tablica_lokalnih_imena.containsKey(ime)){
			if(tablica_lokalnih_imena.get(ime).isFunkcija == true){
				return true;
			}
		}
		
		if(roditeljDjelokrug!=null){
			return roditeljDjelokrug.sadrziFju(ime);
		}
		else{
			return false;
		}
		
	}


	public boolean lokalniSadrziIdn(String ime) {
		if(tablica_lokalnih_imena.containsKey(ime)){
			return true;
		}
		return false;
	}
	
	public HashMap<String, Informacija> vratiDeklariraneFje(){//nevalja zbog drugih parametara a istih imena
		HashMap<String, Informacija> tablica_deklariranih_fja = new HashMap<String, Informacija>();
		for(Entry<String, Informacija> deklaracija:tablica_lokalnih_imena.entrySet()){
			if(deklaracija.getValue().isFunkcija == true){
				tablica_deklariranih_fja.put(deklaracija.getKey(), tablica_lokalnih_imena.get(deklaracija.getKey()));
			}
		}
		
		if(!djecaDjelokrug.isEmpty()){
			for(Djelokrug dijete:djecaDjelokrug){
				tablica_deklariranih_fja.putAll(dijete.vratiDeklariraneFje());
			}
		}
		
		return tablica_deklariranih_fja;
		
	}
	
	
	
}
