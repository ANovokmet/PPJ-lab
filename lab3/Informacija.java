import java.util.ArrayList;
import java.util.HashMap;


public class Informacija {
	private String vrijednost;
	public String tip;
	public String ime;
	public boolean isFunkcija;
	public boolean isDefinirana;
	//public HashMap<String,String> parametri;//parametri-Map<String, String>
	public boolean l_izraz;
	
	public ArrayList<String> tipovi;
	public ArrayList<String> imena;
	
	public int br_elem;//za niz
	
		
	public Informacija(String tip, ArrayList<String> tipovi, ArrayList<String> imena){
		this.tip = tip;
		if(tipovi != null){
			this.tipovi = new ArrayList<String>(tipovi);
		}
		else{
			this.tipovi = null;//mozda i bez init
		}
		
		if(imena != null){
			this.imena = new ArrayList<String>(imena);
		}
		else{
			this.imena = null;
		}
		this.isFunkcija = true;
		this.l_izraz = false;
	}
	
	public Informacija(String tip, ArrayList<String> tipovi){
		this.tip = tip;
		if(tipovi != null){
			this.tipovi = new ArrayList<String>(tipovi);
		}
		else{
			this.tipovi = null;//mozda i bez init
		}
		
		this.imena = null;
		this.isFunkcija = true;
		this.l_izraz = false;
	}
	
	public Informacija(String tip) {
		this.tip = tip;
		this.isFunkcija = false;
		
		//cudno-moguce da netreba
		if(tip!=null){
			if(tip.equals("int") || tip.equals("char")){
				l_izraz=true;
			}
		}
	}

	public Informacija(String tip, int br_elem) {
		this.tip = tip;
		this.isFunkcija = false;
		this.br_elem = br_elem;
	}

	public void setVrijednost(String vrijednost){
		this.vrijednost = vrijednost;
	}
}
