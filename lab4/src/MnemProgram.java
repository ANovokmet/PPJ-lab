import java.util.ArrayList;
import java.util.HashMap;


public class MnemProgram {
	ArrayList<String> linije;
	ArrayList<String> kraj;
	public HashMap<String, Integer> tablica_labeli;
	boolean imadjeljenje;
	boolean imamnozenje;
	boolean imamodiranje;
	public MnemProgram(){
		imadjeljenje = false;
		imamnozenje = false;
		imamodiranje= false;
		
		
		linije = new ArrayList<String>();
		kraj = new ArrayList<String>();
		tablica_labeli = new HashMap<String,Integer>();
		linije.add(" MOVE 40000, R7");
		linije.add(" CALL F_main");
		linije.add(" HALT");
	}
	
	public void mnozenje(){
		System.out.println("");
	}
	
	public void djeljenje(){
		System.out.println("");
	}
	
	public void modiranje(){
		System.out.println("");
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

	public void dodajNaPocetak(String string) {
		// TODO Auto-generated method stub
		linije.add(0, string);
	}
	
	public void dodajNaPocFje(String string) {//TODO oznaciti djelokrug sa D_xxx u programu
		// TODO Auto-generated method stub
		for(int i=linije.size()-1;i>=0;i--){
			if(linije.get(i).startsWith("F_")){
				linije.add(i+1, string);
				break;
			}
		}
		
	}
	
	public void dodajNaPocDje(String string, int id) {//TODO oznaciti djelokrug sa D_xxx u programu
		// TODO Auto-generated method stub
		for(int i=linije.size()-1;i>=0;i--){
			if(linije.get(i).equals("U_D_"+id)){
				linije.add(i+1, string);
				break;
			}
		}
		
	}

	public void dodajNaKraj(String string) {
		// TODO Auto-generated method stub
		kraj.add(string);
	}



	public void dodajPredRet(String string) {
		for(int i=linije.size()-1;i>=0;i--){
			if(linije.get(i).startsWith(" RET")){
				linije.add(i, string);
				break;
			}
		}
	}
	
	public void dodajPredLiniju(String string, String linija) {
		for(int i=linije.size()-1;i>=0;i--){
			if(linije.get(i).equals(linija)){
				linije.add(i, string);
				break;
			}
		}
	}
}
