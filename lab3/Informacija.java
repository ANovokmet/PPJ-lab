import java.util.ArrayList;


public class Informacija {
	private String vrijednost;
	public String tip;
	public boolean isFunkcija;
	public boolean isDefinirana;
	public ArrayList<Informacija> argumenti;
	
	
	public Informacija(String tip, ArrayList<Informacija> argumenti){
		this.tip = tip;
		this.argumenti = argumenti;
		this.isFunkcija = true;
	}
	
	public Informacija(String tip) {
		this.tip = tip;
		this.isFunkcija = false;
	}

	public void setVrijednost(String vrijednost){
		this.vrijednost = vrijednost;
	}
}
