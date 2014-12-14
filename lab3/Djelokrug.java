import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class Djelokrug {
	public ArrayList<Djelokrug> djecaDjelokrug;//nepotrebno? potrebno je
	public Djelokrug roditeljDjelokrug;//ugnjezdujuci blok
	public ArrayList<Cvor> cvorovi;
	
	public String uFunkciji;
	public boolean uPetlji;
	
	public HashMap<String, Informacija> tablica_lokalnih_imena;
	
	public Djelokrug(Djelokrug roditeljDjelokrug){
		this.tablica_lokalnih_imena = new HashMap<String, Informacija>();
		this.roditeljDjelokrug = roditeljDjelokrug;
		this.djecaDjelokrug = new ArrayList<Djelokrug>();
		this.uPetlji = false;
		
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
		tablica_lokalnih_imena.put(naziv, new Informacija(tip));
	}
	
	public Informacija dodajFunkcijuUTablicu(String tip, String naziv, ArrayList<String> tipovi){
		Informacija novaFunkcija = new Informacija(tip, tipovi);
		tablica_lokalnih_imena.put(naziv, novaFunkcija);
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
	
	public HashMap<String, Informacija> vratiDeklariraneFje(){
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
