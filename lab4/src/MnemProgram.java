import java.util.ArrayList;
import java.util.HashMap;


public class MnemProgram {
	ArrayList<String> linije;
	ArrayList<String> kraj;
	public HashMap<String, Integer> tablica_labeli;
	
	public MnemProgram(){
		linije = new ArrayList<String>();
		kraj = new ArrayList<String>();
		tablica_labeli = new HashMap<String,Integer>();
		linije.add("MOVE 40000, R7");
		linije.add("CALL F_main");
		linije.add("HALT");
	}
	
		
	
	public void dodajLiniju(String linija){
		linije.add(linija);
	}
	
	public void ispisi(){
		for(String linija:linije){
			System.out.println(linija);
		}
		for(String linija:kraj){
			System.out.println(linija);
		}
	}



	public void dodajNaKraj(String string) {
		// TODO Auto-generated method stub
		kraj.add(string);
	}
}
