import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;



public class SemantickiAnalizator {
	
	static Djelokrug globalniDjelokrug;//može i bez
	static Djelokrug trenutniDjelokrug;
	static HashMap<String, Informacija> definiraneFunkcije;
	static Informacija trenutnaFunkcija;
	
	public static void main(String[] args) throws IOException {
		definiraneFunkcije = new HashMap<String, Informacija>();
		trenutnaFunkcija = null;
		
		BufferedReader bf = new BufferedReader(new FileReader("primjeri/04_pogresan_main/test.in"));
		Cvor glavni = Cvor.stvori_stablo_iz_filea(bf);
		System.out.println(glavni);
		System.out.println(glavni.trenutacna_produkcija());
		
		
		globalniDjelokrug = new Djelokrug(null);
		trenutniDjelokrug = globalniDjelokrug;
		
		
		provjeri(glavni);
		
		provjeraNakonObilaska();
		
		String a="niz const char";
		String b="niz int";
		System.out.println(implicitnoPretvoriva(a, b));
	}

	
	private static void provjeraNakonObilaska() {
		// TODO Auto-generated method stub
		if(!definiraneFunkcije.containsKey("main")){
			System.out.println("main");
			System.exit(0);
		}
		else if(!definiraneFunkcije.get("main").tip.equals("int") || definiraneFunkcije.get("main").argumenti!=null){//void -> tip, argumenti == null
				System.out.println("main");
				System.exit(0);
		}
		//ako postoji (bilodi)deklarirana a ne definirana fja
		HashMap<String, Informacija> tablica_deklariranih_fja = globalniDjelokrug.vratiDeklariraneFje();
		for(Entry<String, Informacija> deklaracijaFje:tablica_deklariranih_fja.entrySet()){
			if(!definiraneFunkcije.containsKey(deklaracijaFje.getKey())){
				System.out.println("funkcija");
				System.exit(0);
			}
		}
	}


	public static void provjeri(Cvor cvor){
		
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= IDN")){
			Cvor IDN = cvor.djeca.get(0);
			if(trenutniDjelokrug.getIdentifikator(IDN.ime_iz_koda)!=null){
				cvor.tip = trenutniDjelokrug.getIdentifikator(IDN.ime_iz_koda).tip;
				cvor.l_izraz = trenutniDjelokrug.getIdentifikator(IDN.ime_iz_koda).l_izraz;//l_izraz je samo ako je char/int bez const
			}
			else{
				System.out.print("nije deklarirano ime");
				
			}
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= BROJ")){
			
			if(!isInteger(cvor.djeca.get(0).ime_iz_koda)){
				System.out.println(cvor.djeca.get(0).ime_iz_koda+" nije int");
				ispisiGresku(cvor);
			}
			
			
			cvor.tip = "int";
			cvor.l_izraz = false;
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= ZNAK")){
			//znak je ispravan po 4.3.2
			if(!nizZnakovaIspravan(cvor.djeca.get(0).ime_iz_koda)){
				ispisiGresku(cvor);
			}
			
			cvor.tip = "char";
			cvor.l_izraz = false;
			System.out.println(cvor.tip+"list="+ cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= NIZ_ZNAKOVA")){
			//niz mora bit ispravan po 4.3.2
			if(!nizZnakovaIspravan(cvor.djeca.get(0).ime_iz_koda)){
				ispisiGresku(cvor);
			}
			
			cvor.tip = "niz const char";
			cvor.l_izraz = false;
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA")){
			
		}
		
		
		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <primarni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" pi "+cvor.djeca.get(0).tip);
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
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" li "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= OP_INC <unarni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= OP_DEC <unarni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= <unarni_operator> <cast_izraz>")){//za <unarni_operator> nist
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<cast_izraz> ::= <unarni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			
			System.out.println(cvor.tip+" pki "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<cast_izraz> ::= L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<ime_tipa> ::= <specifikator_tipa>")){
			Cvor specifikator_tipa = cvor.djeca.get(0);
			provjeri(specifikator_tipa);
			cvor.tip = specifikator_tipa.tip;
		}
		if(cvor.trenutacna_produkcija().equals("<ime_tipa> ::= KR_CONST <specifikator_tipa>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_VOID")){
			cvor.tip = "void";
		}
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_CHAR")){
			cvor.tip = "char";
		}
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_INT")){
			cvor.tip = "int";
		}
		
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <cast_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" bi "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_PUTA <cast_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_DIJELI <cast_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_MOD <cast_izraz>")){
			
		}
		
		
		
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <multiplikativni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" ai "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <aditivni_izraz> PLUS <multiplikativni_izraz>")){
			cvor.tip = "int";
			cvor.l_izraz = false;
			
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.tip, "int") || !implicitnoPretvoriva(cvor.djeca.get(0).tip, "int")){//na koji aditivni izraz se misli??
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).tip, "int")){
				ispisiGresku(cvor);
			}
		}		
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <aditivni_izraz> MINUS <multiplikativni_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <aditivni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" oi "+cvor.djeca.get(0).tip);
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
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" ji "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <jednakosni_izraz> OP_EQ <odnosni_izraz>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <jednakosni_izraz> OP_NEQ <odnosni_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_i_izraz> ::= <jednakosni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" bii "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_xili_izraz> ::= <bin_i_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" bxi "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_ili_izraz> ::= <bin_xili_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" bili "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<log_i_izraz> ::= <bin_ili_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" lii "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<log_ili_izraz> ::= <log_i_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" lili "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izraz_pridruzivanja> ::= <log_ili_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
			System.out.println(cvor.tip+" ip "+cvor.djeca.get(0).tip);
		}
		if(cvor.trenutacna_produkcija().equals("<izraz_pridruzivanja> ::= <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>")){
			Cvor postfiks_izraz = cvor.djeca.get(0);
			
			provjeri(postfiks_izraz);
			postfiks_izraz.l_izraz=true;//1
			provjeri(cvor.djeca.get(2));
			//cvor.tip ~~ postfiks_izraz.tip
			
			if(!implicitnoPretvoriva(cvor.tip, postfiks_izraz.tip) || !implicitnoPretvoriva(cvor.djeca.get(2).tip, postfiks_izraz.tip)){
				ispisiGresku(cvor);
			}//neznam u uputi koji tocno, oba?
			
			cvor.tip = postfiks_izraz.tip;
			cvor.l_izraz = false;//0
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izraz> ::= <izraz_pridruzivanja>")){
			//tip i l-izraz
			provjeri(cvor.djeca.get(0));
			
			cvor.tip = cvor.djeca.get(0).tip;
			cvor.l_izraz = cvor.djeca.get(0).l_izraz;
		}
		if(cvor.trenutacna_produkcija().equals("<izraz> ::= <izraz> ZAREZ <izraz_pridruzivanja>")){
			
		}
		
		//naredbena struktura programa
		if(cvor.trenutacna_produkcija().equals("<slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA")){
			//odvojeni djelokrug
			Djelokrug djelokrug = new Djelokrug(trenutniDjelokrug);
			trenutniDjelokrug = djelokrug;
			
			provjeri(cvor.djeca.get(1));
			
			trenutniDjelokrug = trenutniDjelokrug.roditeljDjelokrug;
			
		}
		if(cvor.trenutacna_produkcija().equals("<slozena_naredba> ::= L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA")){
			//odvojeni djelokrug
			Djelokrug djelokrug = new Djelokrug(trenutniDjelokrug);
			trenutniDjelokrug = djelokrug;
			
			provjeri(cvor.djeca.get(1));
			provjeri(cvor.djeca.get(2));
			
			trenutniDjelokrug = trenutniDjelokrug.roditeljDjelokrug;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_naredbi> ::= <naredba>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<lista_naredbi> ::= <lista_naredbi> <naredba>")){
			provjeri(cvor.djeca.get(0));
			provjeri(cvor.djeca.get(1));
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<naredba> ::= <slozena_naredba>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<naredba> ::= <izraz_naredba>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<naredba> ::= <naredba_grananja>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<naredba> ::= <naredba_petlje>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<naredba> ::= <naredba_skoka>")){
			provjeri(cvor.djeca.get(0));
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izraz_naredba> ::= TOCKAZAREZ")){
			cvor.tip = "int";
		}
		if(cvor.trenutacna_produkcija().equals("<izraz_naredba> ::= <izraz> TOCKAZAREZ")){
			provjeri(cvor.djeca.get(0));
			cvor.tip = cvor.djeca.get(0).tip;
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
			Cvor izraz = cvor.djeca.get(1);
			provjeri(izraz);
			
			//naredba je unutar funkcije, i tipfje~izraz
			if(!implicitnoPretvoriva(izraz.tip, trenutnaFunkcija.tip)){
				System.out.println("ne nalazi se unutar fje ili ne vrjedi cast");
			}
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
			if(definiraneFunkcije.containsKey(IDN.ime_iz_koda)){
				System.out.print("funkcija moze biti najvise jednom definirana");
			}
			
			if(globalniDjelokrug.sadrziFju(IDN.ime_iz_koda)){
				Informacija funkcija = globalniDjelokrug.getIdentifikator(IDN.ime_iz_koda);
				
				if(funkcija.tip.equals(ime_tipa.tip) && funkcija.argumenti==null){
					
				}
				else{//tip funkcije mora biti void-><ime_tipa>.tip
					System.out.print("<definicija_funkcije> ::= <ime_tipa> IDN "+"("+cvor.djeca.get(0).getClass()+","+cvor.djeca.get(0).samo_ovaj_cvor()+")"+" L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>");
				}
			}
			//zabilježi definiciju i deklaraciju fje
			definiraneFunkcije.put(IDN.ime_iz_koda, new Informacija(ime_tipa.tip, null));
			
			trenutnaFunkcija = trenutniDjelokrug.dodajFunkcijuUTablicu(ime_tipa.tip, IDN.ime_iz_koda, null);
			
			//mozda tu da udje i izadje iz djelokruga
			provjeri(cvor.djeca.get(5));
			
			trenutnaFunkcija = null; //izaði izvan funkcije
			
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
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<lista_deklaracija> ::= <lista_deklaracija> <deklaracija>")){
			provjeri(cvor.djeca.get(0));
			provjeri(cvor.djeca.get(1));
		}
		
		if(cvor.trenutacna_produkcija().equals("<deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ")){
			provjeri(cvor.djeca.get(0));
			
			
			//vari-jable brojevnog tipa ntip
			System.out.println("ntip "+cvor.djeca.get(0).tip);
			cvor.djeca.get(1).ntip=cvor.djeca.get(0).tip;
			
			System.out.println("ntip2 "+cvor.djeca.get(1).ntip);
			
			provjeri(cvor.djeca.get(1));//uz nasljedno svojstvo <lista_init...><-<imetipa>.tip
		}
		
		if(cvor.trenutacna_produkcija().equals("<lista_init_deklaratora> ::= <init_deklarator>")){
			cvor.djeca.get(0).ntip=cvor.ntip;
			System.out.println(cvor.djeca.get(0).ntip+"="+cvor.ntip);
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<lista_init_deklaratora> ::= <lista_init_deklaratora> ZAREZ <init_deklarator>")){
			
			cvor.djeca.get(0).ntip = cvor.ntip;
			provjeri(cvor.djeca.get(0));
			
			cvor.djeca.get(2).ntip = cvor.ntip;
			provjeri(cvor.djeca.get(2));
		}
		
		if(cvor.trenutacna_produkcija().equals("<init_deklarator> ::= <izravni_deklarator>")){
			Cvor izravni_deklarator = cvor.djeca.get(0);
			
			izravni_deklarator.ntip = cvor.ntip;
			
			provjeri(izravni_deklarator);
			
			if(izravni_deklarator.tip.startsWith("const") || izravni_deklarator.tip.startsWith("niz const")){
				ispisiGresku(cvor);
			}
		}
		if(cvor.trenutacna_produkcija().equals("<init_deklarator> ::= <izravni_deklarator> OP_PRIDRUZI <inicijalizator>")){
			//napisi ovo tu
			Cvor izravni_deklarator = cvor.djeca.get(0);
			Cvor inicijalizator = cvor.djeca.get(2);
			
			System.out.println(cvor.djeca.get(0).ntip+"55="+cvor.ntip);
			izravni_deklarator.ntip = cvor.ntip;
			System.out.println(cvor.djeca.get(0).ntip+"55="+cvor.ntip);
			provjeri(izravni_deklarator);
			
			provjeri(inicijalizator);
			
			String T = null;
			if(izravni_deklarator.tip.contains("int")){//int, const int, niz const int
				T="int";
			}
			else if(izravni_deklarator.tip.contains("char")){//char, const char, niz char
				T="char";
			}
			else{
				ispisiGresku(cvor);
			}
			
			if(izravni_deklarator.tip.startsWith("const") || izravni_deklarator.tip.startsWith(T)){
				System.out.println(inicijalizator.tip+T);
				if(!implicitnoPretvoriva(inicijalizator.tip, T)){
					ispisiGresku(cvor);
				}
			}
			else if(izravni_deklarator.tip.startsWith("niz")){
				if(inicijalizator.br_elem <= izravni_deklarator.br_elem){
					for(String U:inicijalizator.tipovi){
						if(!implicitnoPretvoriva(U, T)){
							ispisiGresku(cvor);
						}
					}
				}
				else{
					ispisiGresku(cvor);
				}
			}
			else{
				ispisiGresku(cvor);
			}
		}
		
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN")){
			cvor.tip = cvor.ntip;//ntip
			
			Cvor IDN = cvor.djeca.get(0);
			
			
			if(cvor.ntip.equals("void")){
				ispisiGresku(cvor);
			}
			
			if(trenutniDjelokrug.lokalniSadrziIdn(IDN.ime_iz_koda)){
				ispisiGresku(cvor);
			}
			
			System.out.println(cvor.tip+" "+ IDN.ime_iz_koda);
			trenutniDjelokrug.dodajIdentifikatorUTablicu(cvor.tip, IDN.ime_iz_koda);
			
		}
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA")){
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<inicijalizator> ::= <izraz_pridruzivanja>")){
			provjeri(cvor.djeca.get(0));
			
			//ako se izraz_pridruzivanja=>NIZ_ZNAKOVA
			//br_elem<-duljina niza znakova+1
			//tipovi<-lista duljine br_elem tipova char
			//else
			cvor.tip = cvor.djeca.get(0).tip;
			System.out.println(cvor.tip+" init "+cvor.djeca.get(0).tip);
			
		}
		if(cvor.trenutacna_produkcija().equals("<inicijalizator> ::= L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA")){
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_izraza_pridruzivanja> ::= <izraz_pridruzivanja>")){
			
		}
		if(cvor.trenutacna_produkcija().equals("<lista_izraza_pridruzivanja> ::= <lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja>")){
			
		}
		
	}
	
	private static boolean nizZnakovaIspravan(String s) {
		if(s.length()>3){
			for (int i = 1; i < s.length()-2; i++){//bez navodnika
				char prvi = s.charAt(i);
				char drugi = s.charAt(i+1);
			    
			    if(prvi == '\\' && (
		    		drugi != 't' || 
					drugi != 'n' || 
					drugi != '0' || 
					drugi != '\'' || 
					drugi != '\"' || 
					drugi != '\\' )){
			    	return false;
			    }
			    
			    if(prvi != '\\' && drugi == '\"'){
			    	return false;
			    }
			    
			}
		}
		else{
			char znak = s.charAt(1);
			if(znak == '\\'){
				return false;
			}
		}
		return true;
	}
	

	public static boolean implicitnoPretvoriva(String a, String b){
		if(a.equals(b)){//T~T
			return true;
		}
		
		if(a.startsWith("const")){//const T~T
			String T =a.substring(6);
			
			return implicitnoPretvoriva(T, b);
			
		}
		
		if(a.equals("int") || a.equals("char")){//char~const char, int~const int
			if(b.startsWith("const")){
				String T =b.substring(6);
				
				return implicitnoPretvoriva(a, T);
				
			}
		}
		
		if(a.equals("char") && b.equals("int")){//char~int
			return true;
		}
		
		if(a.startsWith("niz")){//niz T~niz const T
			String T =a.substring(4);
			if(!T.startsWith("const")){
				if(b.equals("niz const "+T)){
					return true;
				}
			}
			/*else{//niz const T~niz T
				if(b.equals("niz "+T.substring(6))){
					return true;
				}
			}*/
		}
		
		return false;
	}
	
	public static boolean isInteger(String string){
		
		try {
			if(string.startsWith("0x")){
				Integer.parseInt(string.substring(2),16);
			}
			else if(string.startsWith("0") && string.length()>1){
				Integer.parseInt(string.substring(1),8);
			}
			else{
	        Integer.parseInt(string);
			}
	    } catch(NumberFormatException e) { 
	        return false;
	    }
		return true;
	}
	
	public static void ispisiGresku(Cvor cvor){
		System.out.print(cvor.ime+" ::=");
		for(Cvor dijete:cvor.djeca){
			System.out.print(" "+dijete.ime);
			
			if(dijete.djeca.isEmpty()){
				System.out.print("("+dijete.redak+","+dijete.ime_iz_koda+")");
			}
		}
		System.exit(0);
	}
}
