import java.io.PrintStream;
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
	

	
	public void dodajLiniju(String linija){
		linije.add(linija);
	}
	
	public void ispisi(PrintStream st){
		if(imadjeljenje){
			djeljenje();
		}
			
		if(imamnozenje){
			mnozenje();
		}
			
		if(imamodiranje){
			modiranje();
		}
		
		
		for(String linija:linije){
			st.println(linija);
		}
		
		
		
		for(String linija:kraj){
			st.println(linija);
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
	
	public void mnozenje(){
		dodajLiniju("pomnozi PUSH R0");
		dodajLiniju(" PUSH R1");
		dodajLiniju(" LOAD R0, (R7+0C)");
		dodajLiniju(" LOAD R1, (R7+10)");
		dodajLiniju(" MOVE 0, R6");
		dodajLiniju(" CMP R0,0");
		dodajLiniju(" JP_EQ kraj");
		dodajLiniju(" CMP R1,0");
		dodajLiniju(" JP_EQ kraj");
		dodajLiniju(" CMP R0,0");
		dodajLiniju(" JP_N manjiodnule");
		dodajLiniju("petlja ADD R6,R1,R6");
		dodajLiniju(" SUB R0,1,R0");
		dodajLiniju(" JP_NZ petlja");
		dodajLiniju("kraj LOAD R0,(R7+08)");
		dodajLiniju(" STORE R0,(R7+10)");
		dodajLiniju(" POP R1");
		dodajLiniju(" POP R0");
		dodajLiniju(" ADD R7,08,R7");
		dodajLiniju(" RET");
		dodajLiniju("manjiodnule CMP R1,0");
		dodajLiniju(" JP_NN dalje");
		dodajLiniju(" XOR R0,-1,R0");
		dodajLiniju(" ADD R0,1,R0");
		dodajLiniju(" XOR R1,-1,R1");
		dodajLiniju(" ADD R1,1,R1");
		dodajLiniju(" JP petlja");
		dodajLiniju("dalje PUSH R0");
		dodajLiniju(" PUSH R1");
		dodajLiniju(" POP R0");
		dodajLiniju(" POP R1");
		dodajLiniju(" JP petlja");
	}
	
	public void djeljenje(){
		dodajLiniju("podjeli PUSH R0");
		dodajLiniju(" PUSH R1");
		dodajLiniju(" PUSH R2");
		dodajLiniju(" LOAD R0, (R7+14) ;djelitelj");
		dodajLiniju(" LOAD R1, (R7+10) ;djeljenik");
		dodajLiniju(" MOVE 0, R6");
		dodajLiniju(" MOVE 0, R2");
		dodajLiniju(" CMP R0,0");
		dodajLiniju(" JP_EQ kraj_djeljenja");
		dodajLiniju(" CMP R1,0");
		dodajLiniju(" JP_EQ kraj_djeljenja");
		dodajLiniju(" CMP R0,0");
		dodajLiniju(" JP_NN preskoci");
		dodajLiniju(" ADD R2,1,R2");
		dodajLiniju(" XOR R0,-1,R0");
		dodajLiniju(" ADD R0,1,R0");
		dodajLiniju("preskoci CMP R1,0");
		dodajLiniju(" JP_NN djeljenje");
		dodajLiniju(" ADD R2,1,R2");
		dodajLiniju(" XOR R1,-1,R1");
		dodajLiniju(" ADD R1,1,R1");
		dodajLiniju("djeljenje ADD R6,1,R6");
		dodajLiniju(" SUB R0,R1,R0");
		dodajLiniju(" JP_NN djeljenje");
		dodajLiniju(" SUB R6,1,R6");
		dodajLiniju(" CMP R2,1");
		dodajLiniju(" JP_NE kraj_djeljenja");
		dodajLiniju(" XOR R6,-1,R6");
		dodajLiniju(" ADD R6,1,R6");
		dodajLiniju("kraj_djeljenja LOAD R0,(R7+0C)");
		dodajLiniju(" STORE R0,(R7+14)");
		dodajLiniju(" POP R2");
		dodajLiniju(" POP R1");
		dodajLiniju(" POP R0");
		dodajLiniju(" ADD R7,08,R7");
		dodajLiniju(" RET");
	}
	
	public void modiranje(){
		dodajLiniju("ostatak PUSH R0");
		dodajLiniju(" LOAD R6, (R7+0C) ;djelitelj");
		dodajLiniju(" LOAD R0, (R7+08) ;djeljenik");
		dodajLiniju(" CMP R0,0");
		dodajLiniju(" JP_SLE kraj_modiranja");
		dodajLiniju(" CMP R6,0");
		dodajLiniju(" JP_SLE kraj_modiranja");
		dodajLiniju("modiranje SUB R6,R0,R6");
		dodajLiniju(" JP_NN modiranje");
		dodajLiniju(" ADD R6,R0,R6");
		dodajLiniju("kraj_modiranja LOAD R0,(R7+04)");
		dodajLiniju(" STORE R0,(R7+0C)");
		dodajLiniju(" POP R0");
		dodajLiniju(" ADD R7,08,R7");
		dodajLiniju(" RET");
	}
}
