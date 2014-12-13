import java.util.ArrayList;


public class Informacija {
	private String vrijednost;
	public String tip;
	public boolean isFunkcija;
	public boolean isDefinirana;
	public ArrayList<Informacija> argumenti;//parametri
	public boolean l_izraz;
	
	
	public ArrayList<String> tipovi;//za niz
	public int br_elem;
	
	
	public Informacija(String tip, ArrayList<Informacija> argumenti){
		this.tip = tip;
		this.argumenti = argumenti;
		this.isFunkcija = true;
		this.l_izraz = false;
		
	}
	
	public Informacija(String tip) {
		this.tip = tip;
		this.isFunkcija = false;
		
		if(tip.equals("int") || tip.equals("char")){
			l_izraz=true;
		}
	}

	public void setVrijednost(String vrijednost){
		this.vrijednost = vrijednost;
	}
}
