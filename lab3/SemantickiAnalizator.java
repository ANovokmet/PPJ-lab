import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class SemantickiAnalizator {
	
	static Djelokrug globalniDjelokrug;//može i bez
	static Djelokrug trenutniDjelokrug;
	static HashMap<String, Informacija> definiraneFunkcije;
	
	public static void main(String[] args) throws IOException {
		definiraneFunkcije = new HashMap<String, Informacija>();
		
		BufferedReader bf = new BufferedReader(new FileReader("primjeri/14_lval2/test.in"));
		Cvor glavni = Cvor.stvori_stablo_iz_filea(bf);
		System.out.println(glavni);
		System.out.println(glavni.trenutacna_produkcija());
		
		
		globalniDjelokrug = new Djelokrug(null);
		trenutniDjelokrug = globalniDjelokrug;
		
		for(Cvor djete: glavni.djeca){
			
		}
		
	}
	
	public static void provjeri(Cvor cvor){
		
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= IDN")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= BROJ")){
					
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= ZNAK")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= NIZ_ZNAKOVA")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA")){
			
		}
		
		
		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <primarni_izraz>")){
			
		}		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA")){
			
		}		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA D_ZAGRADA")){
			
		}		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA")){
			
		}		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> OP_INC")){
			
		}		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> OP_DEC")){
			
		}
		
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_argumenata> ::= <izraz_pridruzivanja>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<lista_argumenata> ::= <lista_argumenata> ZAREZ <izraz_pridruzivanja>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= <postfiks_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= OP_INC <unarni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= OP_DEC <unarni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= <unarni_operator> <cast_izraz>")){//za <unarni_operator> nist
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<cast_izraz> ::= <unarni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<cast_izraz> ::= L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<ime_tipa> ::= <specifikator_tipa>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<ime_tipa> ::= KR_CONST <specifikator_tipa>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_VOID")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_CHAR")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_INT")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <cast_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_PUTA <cast_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_DIJELI <cast_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_MOD <cast_izraz>")){
			
		}
		
		
		
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <multiplikativni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <aditivni_izraz> PLUS <multiplikativni_izraz>")){
			
		}		
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <aditivni_izraz> MINUS <multiplikativni_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <aditivni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_LT <aditivni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_GT <aditivni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_LTE <aditivni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_GTE <aditivni_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <odnosni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <jednakosni_izraz> OP_EQ <odnosni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <jednakosni_izraz> OP_NEQ <odnosni_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_i_izraz> ::= <jednakosni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_xili_izraz> ::= <bin_i_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_ili_izraz> ::= <bin_xili_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<log_i_izraz> ::= <bin_ili_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<log_ili_izraz> ::= <log_i_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izraz_pridruzivanja> ::= <log_ili_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izraz_pridruzivanja> ::= <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izraz> ::= <izraz_pridruzivanja>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izraz> ::= <izraz> ZAREZ <izraz_pridruzivanja>")){
			
		}
		
		//naredbena struktura programa
		if(cvor.trenutacna_produkcija().equals("<slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<slozena_naredba> ::= L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_naredbi> ::= <naredba>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<lista_naredbi> ::= <lista_naredbi> <naredba>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<izraz_naredba> ::= TOCKAZAREZ")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izraz_naredba> ::= <izraz> TOCKAZAREZ")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_CONTINUE TOCKAZAREZ")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_BREAK TOCKAZAREZ")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_RETURN TOCKAZAREZ")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_RETURN <izraz> TOCKAZAREZ")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<prijevodna_jedinica> ::= <vanjska_deklaracija>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<prijevodna_jedinica> ::= <prijevodna_jedinica> <vanjska_deklaracija>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<vanjska_deklaracija> ::= <definicija_funkcije>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<vanjska_deklaracija> ::= <deklaracija>")){
			provjeri(cvor.djeca.get(0));
		}
		
		if(cvor.trenutacna_produkcija().equals("<definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>")){
			provjeri(cvor.djeca.get(0));
			
			Cvor ime_tipa = cvor.djeca.get(0);
			if(ime_tipa.tip.startsWith("const ")){
				System.out.print("<definicija_funkcije> ::= <ime_tipa> IDN "+"("+cvor.djeca.get(0).getClass()+","+cvor.djeca.get(0).samo_ovaj_cvor()+")"+" L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>");
			}
			
			Cvor IDN = cvor.djeca.get(1);
			if(definiraneFunkcije.containsKey(IDN.ime)){
				System.out.print("funkcija moze biti najvise jednom definirana");
			}
			
			if(globalniDjelokrug.sadrziFju(IDN.ime)){
				Informacija funkcija = globalniDjelokrug.getIdentifikator(IDN.ime);
				
				if(funkcija.tip.equals(ime_tipa.tip) && funkcija.argumenti==null){
					
				}
				else{//tip funkcije mora biti void-><ime_tipa>.tip
					System.out.print("<definicija_funkcije> ::= <ime_tipa> IDN "+"("+cvor.djeca.get(0).getClass()+","+cvor.djeca.get(0).samo_ovaj_cvor()+")"+" L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>");
				}
			}
			//zabilježi definiciju i deklaraciju fje
			definiraneFunkcije.put(IDN.ime, new Informacija(ime_tipa.tip, null));
			
			trenutniDjelokrug.dodajFunkcijuUTablicu(ime_tipa.tip, IDN.ime, null);
			
			Djelokrug djelokrug = new Djelokrug(trenutniDjelokrug);
			trenutniDjelokrug = djelokrug;
			
			provjeri(cvor.djeca.get(5));
			
		}
		if(cvor.trenutacna_produkcija().equals("<definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_parametara> ::= <deklaracija_parametra>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<lista_parametara> ::= <lista_parametara> ZAREZ <deklaracija_parametra>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<deklaracija_parametra> ::= <ime_tipa> IDN")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<deklaracija_parametra> ::= <ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_deklaracija> ::= <deklaracija>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<lista_deklaracija> ::= <lista_deklaracija> <deklaracija>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<lista_init_deklaratora> ::= <init_deklarator>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<lista_init_deklaratora> ::= <lista_init_deklaratora> ZAREZ <init_deklarator>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<init_deklarator> ::= <izravni_deklarator>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<init_deklarator> ::= <izravni_deklarator> OP_PRIDRUZI <inicijalizator>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<inicijalizator> ::= <izraz_pridruzivanja>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<inicijalizator> ::= L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_izraza_pridruzivanja> ::= <izraz_pridruzivanja>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<lista_izraza_pridruzivanja> ::= <lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja>")){
			
		}
		
	}
	
	
}
