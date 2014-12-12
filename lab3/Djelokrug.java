import java.util.ArrayList;
import java.util.HashMap;


public class Djelokrug {
	public ArrayList<Djelokrug> djecaDjelokrug;//nepotrebno?
	public Djelokrug roditeljDjelokrug;//ugnjezdujuci blok
	public ArrayList<Cvor> cvorovi;
	
	public String uFunkciji;
	
	public HashMap<String, Informacija> tablica_lokalnih_imena;
	
	public Djelokrug(Djelokrug roditeljDjelokrug){
		this.tablica_lokalnih_imena = new HashMap<String, Informacija>();
		this.roditeljDjelokrug = roditeljDjelokrug;
	}
	
	public void dodajIdentifikatorUTablicu(String tip, String naziv){
		tablica_lokalnih_imena.put(naziv, new Informacija(tip));
	}
	
	public void dodajFunkcijuUTablicu(String tip, String naziv, ArrayList<Informacija> argumenti){
		tablica_lokalnih_imena.put(naziv, new Informacija(tip, argumenti));
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
}
