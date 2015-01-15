import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.management.timer.Timer;

public class SemantickiAnalizator {
	
	static Djelokrug globalniDjelokrug;//može i bez
	static Djelokrug trenutniDjelokrug;
	
	static int idDjelokrug;
	static int idUsporedba;
	
	static HashMap<String, Informacija> definiraneFunkcije;
	static Informacija trenutnaFunkcija;
	static int unutarPetlji;
	static MnemProgram program;
	
	static HashMap<String, String> globalneVarijable;
	
	static boolean u_listi_parametara = false;
	
	
	public static void main(String[] args) throws IOException {
		definiraneFunkcije = new HashMap<String, Informacija>();
		trenutnaFunkcija = null;
		idDjelokrug = 0;
		idUsporedba = 0;
		
		program = new MnemProgram();
		
		//BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader bf = new BufferedReader(new FileReader("npr/26_niz4/test.in"));
		Cvor glavni = Cvor.stvori_stablo_iz_filea(bf);
		
		globalneVarijable = new HashMap<String, String>();
		
		globalniDjelokrug = new Djelokrug(null);
		trenutniDjelokrug = globalniDjelokrug;
		unutarPetlji = 0;
		
		provjeri(glavni);
		
		provjeraNakonObilaska();
		
		program.ispisi();
	}

	public static void provjeri(Cvor cvor){
		
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= IDN")){
			Cvor IDN = cvor.djeca.get(0);

			if(trenutniDjelokrug.getIdentifikator(IDN.ime_iz_koda)!=null){
				
				cvor.inf = new Informacija(trenutniDjelokrug.getIdentifikator(IDN.ime_iz_koda));
				cvor.inf.ime = IDN.ime_iz_koda;
				
				if(cvor.inf.isFunkcija!=true && !cvor.inf.tip.startsWith("niz")){
					String lokacija = trenutniDjelokrug.lokacija(cvor.inf.ime);
					if(trenutniDjelokrug.samoUGlobalnom(cvor.inf.ime)){
						program.dodajLiniju(" LOAD R0, ("+lokacija+")");
						program.dodajLiniju(" PUSH R0");
						Djelokrug.relodmak+=4;
					}
					else{
						program.dodajLiniju(" LOAD R0, ("+lokacija+")");
						program.dodajLiniju(" PUSH R0");
					}
				}
				else if(cvor.inf.tip.startsWith("niz")){
					Integer lokacija = trenutniDjelokrug.getOffset(cvor.inf.ime);
					if(!trenutniDjelokrug.samoUGlobalnom(cvor.inf.ime)){//lokalno deklarirani niz, pusha se adresa prvog clana
						program.dodajLiniju(" ADD R7, %D "+lokacija+", R0 ;odmak");
						program.dodajLiniju(" LOAD R0, (R0) ;lokacija adrese"+cvor.inf.tip.startsWith("niz"));
						program.dodajLiniju(" PUSH R0 ;adresa niza "+cvor.inf.ime);
						Djelokrug.relodmak+=4;
					}
				}
			}
			else{
				ispisiGresku(cvor);
			}
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= BROJ")){
			
			if(!isInteger(cvor.djeca.get(0).ime_iz_koda, cvor.ntip)){//za OP_NEG
				
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
			if(cvor.ntip!=null)//za unarni minus/plus
				cvor.inf.vrijednost = cvor.ntip+cvor.djeca.get(0).ime_iz_koda;
			else
				cvor.inf.vrijednost = cvor.djeca.get(0).ime_iz_koda;
			
			//pazi na broj od 20+bit
			if(trenutniDjelokrug.roditeljDjelokrug!=null){
				int broj = Integer.parseInt(cvor.inf.vrijednost,10);
				if(broj<=0x7ffff){
					program.dodajLiniju(" MOVE %D "+cvor.inf.vrijednost+", R0");
					program.dodajLiniju(" PUSH R0");
					Djelokrug.relodmak+=4;
				}
				else{
					//TODO nazivi konstanti
					program.dodajNaKraj("K_a DW %D "+cvor.inf.vrijednost);
					program.dodajLiniju(" LOAD R0, ("+"K_a"+")");
					program.dodajLiniju(" PUSH R0");
					Djelokrug.relodmak+=4;
				}
				
			}
			
			
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= ZNAK")){
			//znak je ispravan po 4.3.2
			if(!nizZnakovaIspravan(cvor.djeca.get(0).ime_iz_koda)){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("char");
			cvor.inf.l_izraz = false;
			cvor.inf.vrijednost = Integer.toString((int)cvor.djeca.get(0).ime_iz_koda.charAt(1));
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= NIZ_ZNAKOVA")){
			//niz mora bit ispravan po 4.3.2
			
			if(!nizZnakovaIspravan(cvor.djeca.get(0).ime_iz_koda)){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("niz const char");
			cvor.inf.l_izraz = false;
			cvor.inf.vrijednost = cvor.djeca.get(0).ime_iz_koda;
		}
		if(cvor.trenutacna_produkcija().equals("<primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA")){
			provjeri(cvor.djeca.get(1));
			
			cvor.inf = new Informacija(cvor.djeca.get(1).inf);
		}
		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <primarni_izraz>")){
			cvor.djeca.get(0).ntip = cvor.ntip;//za OP_NEG
			
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = new Informacija(cvor.djeca.get(0).inf);
			cvor.inf.vrijednost = cvor.djeca.get(0).inf.vrijednost;
			cvor.inf.ime = cvor.djeca.get(0).inf.ime;
		}		

		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA")){
			Cvor postfiks_izraz = cvor.djeca.get(0);
			Cvor izraz = cvor.djeca.get(2);
			
			provjeri(postfiks_izraz);
			if(!postfiks_izraz.getTip().startsWith("niz")){
				ispisiGresku(cvor);
			}
			provjeri(izraz);
			if(!implicitnoPretvoriva(izraz.inf, "int")){
				ispisiGresku(cvor);
			}
			String X = postfiks_izraz.getTip().substring(4);
			

			cvor.inf = new Informacija(X);
			cvor.inf.ime = postfiks_izraz.inf.ime;
			System.out.println(postfiks_izraz.inf.ime);
			cvor.inf.l_izraz=!X.startsWith("const");
			//TODO NIZ
			
			String lokacija = trenutniDjelokrug.lokacija(postfiks_izraz.inf.ime);
			if(trenutniDjelokrug.samoUGlobalnom(postfiks_izraz.inf.ime)){
				program.dodajLiniju(" POP R0 ;ovo nevaja mozd");
				program.dodajLiniju(" POP R1");
				program.dodajLiniju(" ADD R0, R1, R1");
				program.dodajLiniju(" ADD R0, R1, R1");
				program.dodajLiniju(" ADD R0, R1, R1");
				program.dodajLiniju(" ADD R0, R1, R0 ;R0=R1+R0*4");
				program.dodajLiniju(" PUSH R0 ;ref na R0lti elem "+postfiks_izraz.inf.ime);
				Djelokrug.relodmak+=4;
			}
			else{
				int offset = trenutniDjelokrug.getOffset(postfiks_izraz.inf.ime);
				program.dodajLiniju(" POP R0");
				program.dodajLiniju(" POP R1");
				program.dodajLiniju(" ADD R0, R1, R1");
				program.dodajLiniju(" ADD R0, R1, R1");
				program.dodajLiniju(" ADD R0, R1, R1");
				program.dodajLiniju(" ADD R0, R1, R0 ;R0=R1+R0*4");
				program.dodajLiniju(" PUSH R0 ;ref na R0lti elem "+postfiks_izraz.inf.ime);
			}
			

			
		}		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA D_ZAGRADA")){
			Cvor postfiks_izraz = cvor.djeca.get(0);
			
			provjeri(postfiks_izraz);
			
			if(!postfiks_izraz.inf.tipovi.isEmpty() || postfiks_izraz.inf.isFunkcija != true){//cudno, jel fja smije bit void?
				ispisiGresku(cvor);
			}
			
			
			//TODO sta ak ne vraca vrjednost return; ??
			program.dodajLiniju(" CALL F_"+postfiks_izraz.inf.ime);
			if(!postfiks_izraz.inf.tip.equals("void")){
				program.dodajLiniju(" PUSH R6");
			}
				
			
			
			
			cvor.inf = postfiks_izraz.inf;
			cvor.inf.l_izraz = false;
			cvor.inf.isFunkcija = false;//cudno-mozda bez ovog al treba bit false
		}		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA")){
			Cvor postfiks_izraz = cvor.djeca.get(0);
			Cvor lista_argumenata = cvor.djeca.get(2);
			//cudno doraditi
			provjeri(postfiks_izraz);
			
			provjeri(lista_argumenata);
			//cudno, jel fja smije bit void?
			
			if(!postfiks_izraz.inf.tipovi.isEmpty() && postfiks_izraz.inf.isFunkcija == true && lista_argumenata.inf.tipovi.size()==postfiks_izraz.inf.tipovi.size()){//cudno-postfix.tip=fja(params->pov)
				ArrayList<String> arg_tipovi= lista_argumenata.inf.tipovi;//jel tipovi il parametri?
				ArrayList<String> param_tipovi= postfiks_izraz.inf.tipovi;
				for(int i=0;i<arg_tipovi.size();i++){
					
					if(!implicitnoPretvoriva(arg_tipovi.get(i), param_tipovi.get(i))){
						ispisiGresku(cvor);
					}
				}
			}
			else{
				
				ispisiGresku(cvor);
			}
			
			program.dodajLiniju(" CALL F_"+postfiks_izraz.inf.ime);
			program.dodajLiniju(" ADD R7, "+4*lista_argumenata.inf.tipovi.size()+", R7 ;makni postavljene arg");
			
			if(!postfiks_izraz.inf.tip.equals("void")){
				program.dodajLiniju(" PUSH R6");
			}
			
			cvor.inf = postfiks_izraz.inf;
			cvor.inf.l_izraz = false;
			cvor.inf.isFunkcija = false;//cudno-opet
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> OP_INC") ||
			cvor.trenutacna_produkcija().equals("<postfiks_izraz> ::= <postfiks_izraz> OP_DEC")){
			
			Cvor postfiks_izraz = cvor.djeca.get(0);
			provjeri(postfiks_izraz);
			if(postfiks_izraz.inf.l_izraz!=true || !implicitnoPretvoriva(postfiks_izraz.inf, "int")){
				ispisiGresku(cvor);
			}
			//TODO frisc uveæa vrjednost
			cvor.inf = postfiks_izraz.inf;
			cvor.inf.tip = "int";
			cvor.inf.l_izraz=false;
			
		}		
		
		if(cvor.trenutacna_produkcija().equals("<lista_argumenata> ::= <izraz_pridruzivanja>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = new Informacija();//cudno nema inf
			cvor.inf.tipovi.add(cvor.djeca.get(0).inf.tip);
			cvor.inf.imena.add(cvor.djeca.get(0).inf.ime);
			cvor.inf.vrijednosti.add(cvor.djeca.get(0).inf.vrijednost);
		}
		if(cvor.trenutacna_produkcija().equals("<lista_argumenata> ::= <lista_argumenata> ZAREZ <izraz_pridruzivanja>")){
			Cvor lista_argumenata = cvor.djeca.get(0);
			Cvor izraz_pridruzivanja = cvor.djeca.get(2);
			
			provjeri(lista_argumenata);
			provjeri(izraz_pridruzivanja);
			
			cvor.inf = lista_argumenata.inf;//cudno nema inf
			cvor.inf.tipovi.addAll(lista_argumenata.inf.tipovi);
			cvor.inf.tipovi.add(izraz_pridruzivanja.inf.tip);
			cvor.inf.imena.addAll(cvor.djeca.get(0).inf.imena);
			cvor.inf.imena.add(cvor.djeca.get(0).inf.ime);
			cvor.inf.vrijednosti.add(cvor.djeca.get(0).inf.vrijednost);
			cvor.inf.vrijednosti.addAll(cvor.djeca.get(0).inf.vrijednosti);
		}
		
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= <postfiks_izraz>")){
			cvor.djeca.get(0).ntip = cvor.ntip;//za OP_NEG
			
			provjeri(cvor.djeca.get(0));

			cvor.inf = cvor.djeca.get(0).inf;
			
			if(cvor.inf.ime!=null){
				Informacija a = trenutniDjelokrug.getIdentifikator(cvor.inf.ime);
				System.out.println(cvor.inf.ime);
				if(a.tip.startsWith("niz")){
					program.dodajLiniju(" POP R0");
					program.dodajLiniju(" LOAD R0, (R0) ;dohvati vrijednost elem");
					program.dodajLiniju(" PUSH R0");
				}
			}
			
		}
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= OP_INC <unarni_izraz>") ||
				cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= OP_DEC <unarni_izraz>")){
			Cvor unarni_izraz = cvor.djeca.get(1);
			
			provjeri(unarni_izraz);
			
			
			if(unarni_izraz.inf.l_izraz!=true || !implicitnoPretvoriva(unarni_izraz.inf,"int")){
				ispisiGresku(cvor);
			}
			
			cvor.inf = unarni_izraz.inf;
			cvor.inf.tip = "int";
			cvor.inf.l_izraz = false;
		}
		
		if(cvor.trenutacna_produkcija().equals("<unarni_izraz> ::= <unarni_operator> <cast_izraz>")){//za <unarni_operator> nist
			provjeri(cvor.djeca.get(0));
			cvor.djeca.get(1).ntip = cvor.djeca.get(0).ntip;//ZA OP_NEG
			provjeri(cvor.djeca.get(1));
			if(!implicitnoPretvoriva(cvor.djeca.get(1).inf, "int")){
				ispisiGresku(cvor);
			}
			
			//TODO frisc promjeni vrjednost na osnovi UN_OP
			cvor.inf = cvor.djeca.get(1).inf;
			cvor.inf.tip = "int";
			cvor.inf.l_izraz = false;
		}
		
		if(cvor.trenutacna_produkcija().equals("<unarni_operator> ::= PLUS") ||
				cvor.trenutacna_produkcija().equals("<unarni_operator> ::= MINUS") ||
				cvor.trenutacna_produkcija().equals("<unarni_operator> ::= OP_TILDA") ||
				cvor.trenutacna_produkcija().equals("<unarni_operator> ::= OP_NEG")){
			//nista ne treba
			if(cvor.djeca.get(0).getImeCvora().equals("MINUS")){
				cvor.ntip = "-"; //posluzitzu se neiskoriztenim svojstvom
			}
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<cast_izraz> ::= <unarni_izraz>")){
			cvor.djeca.get(0).ntip = cvor.ntip;//za OP_NEG
			
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<cast_izraz> ::= L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>")){
			Cvor ime_tipa = cvor.djeca.get(1);
			Cvor cast_izraz = cvor.djeca.get(3);
			
			provjeri(ime_tipa);
			provjeri(cast_izraz);
			
			//tip cast izraz se moze explicitno pretvorit u tip ime tipa, samo dopusteno int->char
			//cudno - dal dopusstam explicitno (int)int (char)char valjda da
			
			if(!eksplicitnoPretvoriva(cast_izraz.getTip(), ime_tipa.getTip()) || cast_izraz.isFunkcija() || ime_tipa.isFunkcija()){//cudno ime_tipa.isFunkcija()
				ispisiGresku(cvor);
			}
			cvor.inf = cast_izraz.inf;
			cvor.inf.tip = ime_tipa.inf.tip;
			cvor.inf.l_izraz = false;
		}
		
		if(cvor.trenutacna_produkcija().equals("<ime_tipa> ::= <specifikator_tipa>")){
			Cvor specifikator_tipa = cvor.djeca.get(0);
			provjeri(specifikator_tipa);
			cvor.inf = specifikator_tipa.inf;
		}
		if(cvor.trenutacna_produkcija().equals("<ime_tipa> ::= KR_CONST <specifikator_tipa>")){
			Cvor specifikator_tipa = cvor.djeca.get(1);
			
			provjeri(specifikator_tipa);
			if(specifikator_tipa.getTip().equals("void")){
				ispisiGresku(cvor);
			}
			
			cvor.inf = specifikator_tipa.inf;
			cvor.inf.tip ="const "+specifikator_tipa.inf.tip;
		}
		
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_VOID")){
			cvor.inf = new Informacija("void");
		}
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_CHAR")){
			cvor.inf = new Informacija("char");
		}
		if(cvor.trenutacna_produkcija().equals("<specifikator_tipa> ::= KR_INT")){
			cvor.inf = new Informacija("int");
		}
		
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <cast_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_PUTA <cast_izraz>")
				|| cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_DIJELI <cast_izraz>")
				|| cvor.trenutacna_produkcija().equals("<multiplikativni_izraz> ::= <multiplikativni_izraz> OP_MOD <cast_izraz>")){//% definiran samo za pozitivne brojeve i za 0%poz broj
			
			provjeri(cvor.djeca.get(0));
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			//TODO zbrajanje
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <multiplikativni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <aditivni_izraz> PLUS <multiplikativni_izraz>")
				|| cvor.trenutacna_produkcija().equals("<aditivni_izraz> ::= <aditivni_izraz> MINUS <multiplikativni_izraz>")){
			
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			//TODO izracunat
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
			
			program.dodajLiniju(" POP R1");
			program.dodajLiniju(" POP R0");
			if(cvor.djeca.get(1).ime_iz_koda.equals("+"))
				program.dodajLiniju(" ADD R0, R1, R0");
			else
				program.dodajLiniju(" SUB R0, R1, R0");// 1, - 2, = 3
			
			program.dodajLiniju(" PUSH R0");
		}		
		
		
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <aditivni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_LT <aditivni_izraz>") ||
				cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_GT <aditivni_izraz>")||
				cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_LTE <aditivni_izraz>")||
				cvor.trenutacna_produkcija().equals("<odnosni_izraz> ::= <odnosni_izraz> OP_GTE <aditivni_izraz>")){
			
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			program.dodajLiniju(" POP R1");
			program.dodajLiniju(" POP R0");
			program.dodajLiniju(" CMP R0, R1");
			program.dodajLiniju(" MOVE %D 1, R0");
			String znak = cvor.djeca.get(1).ime_iz_koda;
			if(znak.equals("<")){
				program.dodajLiniju(" JP_SLT TRUE_"+(++idUsporedba));
			}
			else if(znak.equals(">")){
				program.dodajLiniju(" JP_SGT TRUE_"+(++idUsporedba));
			}
			else if(znak.equals("<=")){
				program.dodajLiniju(" JP_SLE TRUE_"+(++idUsporedba));
			}
			else if(znak.equals(">=")){
				program.dodajLiniju(" JP_SGE TRUE_"+(++idUsporedba));
			}
			program.dodajLiniju(" MOVE %D 0, R0");
			program.dodajLiniju("TRUE_"+idUsporedba);
			program.dodajLiniju(" PUSH R0");
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		if(cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <odnosni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		//ista ko malopre
		if(cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <jednakosni_izraz> OP_EQ <odnosni_izraz>") ||
				cvor.trenutacna_produkcija().equals("<jednakosni_izraz> ::= <jednakosni_izraz> OP_NEQ <odnosni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_i_izraz> ::= <jednakosni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		//isto isti
		if(cvor.trenutacna_produkcija().equals("<bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			program.dodajLiniju(" POP R1");
			program.dodajLiniju(" POP R0");
			program.dodajLiniju(" AND R0, R1, R0");
			program.dodajLiniju(" PUSH R0");
			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_xili_izraz> ::= <bin_i_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		//isto isti
		if(cvor.trenutacna_produkcija().equals("<bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			
			program.dodajLiniju(" POP R1");
			program.dodajLiniju(" POP R0");
			program.dodajLiniju(" AND R0, R1, R0");
			program.dodajLiniju(" PUSH R0");
			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<bin_ili_izraz> ::= <bin_xili_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		//svi se ponavljaju wtf
		if(cvor.trenutacna_produkcija().equals("<bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			
			program.dodajLiniju(" POP R1");
			program.dodajLiniju(" POP R0");
			program.dodajLiniju(" OR R0, R1, R0");
			program.dodajLiniju(" PUSH R0");
			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<log_i_izraz> ::= <bin_ili_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>")){//0&&x = 0 bez evaluacije x, 1||x isto
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			

			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		if(cvor.trenutacna_produkcija().equals("<log_ili_izraz> ::= <log_i_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(0).inf, "int")){
				ispisiGresku(cvor);
			}
			
			provjeri(cvor.djeca.get(2));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, "int")){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("int");
			cvor.inf.l_izraz = false;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izraz_pridruzivanja> ::= <log_ili_izraz>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
			
			Informacija a = trenutniDjelokrug.getIdentifikator(cvor.inf.ime);
		}
		if(cvor.trenutacna_produkcija().equals("<izraz_pridruzivanja> ::= <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>")){
			Cvor postfiks_izraz = cvor.djeca.get(0);
			
			provjeri(postfiks_izraz);
			if(postfiks_izraz.inf.l_izraz!=true){
				ispisiGresku(cvor);
			}
			provjeri(cvor.djeca.get(2));
			//cvor.tip ~~ postfiks_izraz.tip
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf, postfiks_izraz.inf)){
				ispisiGresku(cvor);
			}
			
			
			Informacija a = trenutniDjelokrug.getIdentifikator(postfiks_izraz.inf.ime);
			if(a.tip.startsWith("niz")){
				program.dodajLiniju(" POP R0");
				program.dodajLiniju(" POP R1");
				program.dodajLiniju(" STORE R0, (R1)");
			}
			else{
				String lokacija = trenutniDjelokrug.getLokaciju(postfiks_izraz.inf.ime);//TODO nevalja
				program.dodajLiniju(" MOVE %D "+cvor.djeca.get(2).inf.vrijednost+", "+postfiks_izraz.inf.ime);
			}
			
			
			cvor.inf = postfiks_izraz.inf;
			cvor.inf.l_izraz = false;//0
			cvor.inf.vrijednost = cvor.djeca.get(2).inf.vrijednost;
			cvor.inf.vrijednosti = cvor.djeca.get(2).inf.vrijednosti;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izraz> ::= <izraz_pridruzivanja>")){
			//tip i l-izraz
			provjeri(cvor.djeca.get(0));
			
			Djelokrug.relodmak = 0;//reset relativnih lokacija
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<izraz> ::= <izraz> ZAREZ <izraz_pridruzivanja>")){
			provjeri(cvor.djeca.get(0));
			provjeri(cvor.djeca.get(2));
			
			cvor.inf = cvor.djeca.get(2).inf;
			cvor.inf.l_izraz = false;
		}
		
		//naredbena struktura programa
		if(cvor.trenutacna_produkcija().equals("<slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA")){
			// odvojeni djelokrug
			provjeri(cvor.djeca.get(1));
			
			
		}
		if(cvor.trenutacna_produkcija().equals("<slozena_naredba> ::= L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA")){
			// odvojeni djelokrug 
			provjeri(cvor.djeca.get(1));
			
			Cvor lista_deklaracija = cvor.djeca.get(1);
			
			int velicina = 0;
			int totvelicina = 0;
			int br = 0;
			for(int i = lista_deklaracija.inf.tipovi.size()-1;i>=0;i--){
				Informacija inf = trenutniDjelokrug.getIdentifikator(lista_deklaracija.inf.imena.get(i));
				if(inf.tip.startsWith("niz")){
					velicina+=inf.br_elem+1;
					totvelicina+=inf.br_elem+1;
					program.dodajNaPocDje(" PUSH R7",idDjelokrug);
					program.dodajNaPocDje(" SUB R7, %D "+inf.br_elem*4+", R7 ;lokalne var",idDjelokrug);
					br++;
				}
				else{
					velicina+=1;
					program.dodajNaPocDje(" SUB R7, %D 4, R7 ;lokalne var",idDjelokrug);
				}
				
			}
			
			provjeri(cvor.djeca.get(2));
			
			program.dodajPredRet(" ADD R7, %D "+(velicina)*4+", R7 ;maknute lok var");
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_naredbi> ::= <naredba>")){
			provjeri(cvor.djeca.get(0));
		}
		if(cvor.trenutacna_produkcija().equals("<lista_naredbi> ::= <lista_naredbi> <naredba>")){
			provjeri(cvor.djeca.get(0));
			provjeri(cvor.djeca.get(1));
		}
		
		if(cvor.trenutacna_produkcija().equals("<naredba> ::= <slozena_naredba>")){
			Djelokrug djelokrug = new Djelokrug(trenutniDjelokrug);//udji u djelokrug
			trenutniDjelokrug = djelokrug;
			
			int id = ++idDjelokrug;
			program.dodajLiniju("U_D_"+id);
			provjeri(cvor.djeca.get(0));
			
			program.dodajLiniju("IZ_D_"+id);
			trenutniDjelokrug.vratiOdmak();
			trenutniDjelokrug = trenutniDjelokrug.roditeljDjelokrug;//izadji
		}
		if(cvor.trenutacna_produkcija().equals("<naredba> ::= <izraz_naredba>")
				|| cvor.trenutacna_produkcija().equals("<naredba> ::= <naredba_grananja>")
				|| cvor.trenutacna_produkcija().equals("<naredba> ::= <naredba_petlje>")
				|| cvor.trenutacna_produkcija().equals("<naredba> ::= <naredba_skoka>")){
			provjeri(cvor.djeca.get(0));
		}

		if(cvor.trenutacna_produkcija().equals("<izraz_naredba> ::= TOCKAZAREZ")){
			cvor.inf = new Informacija("int");
		}
		if(cvor.trenutacna_produkcija().equals("<izraz_naredba> ::= <izraz> TOCKAZAREZ")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		
		if(cvor.trenutacna_produkcija().equals("<naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>")){
			
			provjeri(cvor.djeca.get(2));
			
			program.dodajLiniju(" POP R0");//if naredba
			program.dodajLiniju(" SUB R0, 0, R0");
			program.dodajLiniju(" JP_Z IZ_D_"+(idDjelokrug+1));
			
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf,"int")){//cudno al radi
				ispisiGresku(cvor);
			}
			provjeri(cvor.djeca.get(4));
			
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>")){
			provjeri(cvor.djeca.get(2));
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf,"int")){
				ispisiGresku(cvor);
			}
			program.dodajLiniju(" POP R0");//if naredba
			program.dodajLiniju(" SUB R0, 0, R0");
			
			String idDjel = "IZ_D_"+(idDjelokrug+1);
			program.dodajLiniju(" JP_Z "+idDjel);
			
			provjeri(cvor.djeca.get(4));
			
			program.dodajPredLiniju(" JP IZ_D_"+(idDjelokrug+1), idDjel);
			
			provjeri(cvor.djeca.get(6));
		}
		// ovdje ulaziti i izlaziti iz uPetlji
		if(cvor.trenutacna_produkcija().equals("<naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>")){
			provjeri(cvor.djeca.get(2));
			if(!implicitnoPretvoriva(cvor.djeca.get(2).inf,"int")){
				ispisiGresku(cvor);
			}
			trenutniDjelokrug.uPetlji=true;
			unutarPetlji++;
			provjeri(cvor.djeca.get(4));
			unutarPetlji--;
			trenutniDjelokrug.uPetlji=false;
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>")){
			provjeri(cvor.djeca.get(2));
			provjeri(cvor.djeca.get(3));
			
			
			if(!implicitnoPretvoriva(cvor.djeca.get(3).inf,"int")){
				ispisiGresku(cvor);
			}
			trenutniDjelokrug.uPetlji=true;
			unutarPetlji++;
			provjeri(cvor.djeca.get(5));
			unutarPetlji--;
			trenutniDjelokrug.uPetlji=false;
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>")){
			provjeri(cvor.djeca.get(2));
			provjeri(cvor.djeca.get(3));
			if(!implicitnoPretvoriva(cvor.djeca.get(3).inf,"int")){
				ispisiGresku(cvor);
			}
			provjeri(cvor.djeca.get(4));
			unutarPetlji++;
			trenutniDjelokrug.uPetlji=true;
			provjeri(cvor.djeca.get(6));
			unutarPetlji--;
			trenutniDjelokrug.uPetlji=false;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_CONTINUE TOCKAZAREZ")
			|| cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_BREAK TOCKAZAREZ")){
			//iskljucivo unutar petlje ili bloka koji je u petlji, bool uPetlji;
			
			
			
			
			if(unutarPetlji==0){
				ispisiGresku(cvor);
			}
		}
		
		if(cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_RETURN TOCKAZAREZ")){
			if(trenutnaFunkcija!=null && !trenutnaFunkcija.tip.equals("void")){
				ispisiGresku(cvor);
			}
		}
		if(cvor.trenutacna_produkcija().equals("<naredba_skoka> ::= KR_RETURN <izraz> TOCKAZAREZ")){
			Cvor izraz = cvor.djeca.get(1);
			provjeri(izraz);
			//naredba je unutar funkcije, i izraz~tipfje
			
			if(trenutnaFunkcija==null || !implicitnoPretvoriva(izraz.inf, trenutnaFunkcija.tip)){
				ispisiGresku(cvor);
			}
			
			program.dodajLiniju(" POP R6");
			//TODO ADD R6, 4*broj parametara, R6
			//skoèi do returna iz fje
			program.dodajLiniju(" RET");
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<prijevodna_jedinica> ::= <vanjska_deklaracija>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<prijevodna_jedinica> ::= <prijevodna_jedinica> <vanjska_deklaracija>")){
			provjeri(cvor.djeca.get(0));
			provjeri(cvor.djeca.get(1));
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<vanjska_deklaracija> ::= <definicija_funkcije>")
				|| cvor.trenutacna_produkcija().equals("<vanjska_deklaracija> ::= <deklaracija>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		
		//TODO deklaracije i definicije
		if(cvor.trenutacna_produkcija().equals("<definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>")){
			Cvor ime_tipa = cvor.djeca.get(0);
			
			provjeri(ime_tipa);
			
			if(ime_tipa.getTip().startsWith("const ")){
				ispisiGresku(cvor);
			}
			
			Cvor IDN = cvor.djeca.get(1);
			
			if(definiraneFunkcije.containsKey(IDN.ime_iz_koda)){
				ispisiGresku(cvor);
			}
			
			program.dodajLiniju("F_"+IDN.ime_iz_koda);
			
			if(globalniDjelokrug.sadrziIdn(IDN.ime_iz_koda)){
				Informacija deklaracija = globalniDjelokrug.getIdentifikator(IDN.ime_iz_koda);

				if(!deklaracija.tip.equals(ime_tipa.inf.tip) || !deklaracija.tipovi.isEmpty()
						|| deklaracija.isFunkcija!=true){
					
					ispisiGresku(cvor);
				}
			}
			
			
			//zabilježi definiciju i deklaraciju fje
			definiraneFunkcije.put(IDN.ime_iz_koda, new Informacija(ime_tipa.getTip(), null, null));
			
			trenutnaFunkcija = trenutniDjelokrug.dodajFunkcijuUTablicu(ime_tipa.getTip(), IDN.ime_iz_koda, null);
			trenutnaFunkcija.ime = IDN.ime_iz_koda;
			
			//mozda tu da udje i izadje iz djelokruga zbog ugradnje parametara
			Djelokrug djelokrug = new Djelokrug(trenutniDjelokrug);//udji u djelokrug
			trenutniDjelokrug = djelokrug;
			
			int id = ++idDjelokrug;
			program.dodajLiniju("U_D_"+id);
			
			provjeri(cvor.djeca.get(5));
			program.dodajLiniju(" RET");
			program.dodajLiniju("IZ_D_"+id);
			trenutniDjelokrug.vratiOdmak();
			
			trenutniDjelokrug = trenutniDjelokrug.roditeljDjelokrug;//izadji
			trenutnaFunkcija = null; //izaði izvan funkcije
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>")){
			Cvor ime_tipa = cvor.djeca.get(0);
			Cvor IDN = cvor.djeca.get(1);
			Cvor lista_parametara = cvor.djeca.get(3);
			
			provjeri(ime_tipa);
			
			if(ime_tipa.getTip().startsWith("const ")){
				ispisiGresku(cvor);
			}
			
			if(definiraneFunkcije.containsKey(IDN.ime_iz_koda)){
				ispisiGresku(cvor);
			}
			program.dodajLiniju("F_"+IDN.ime_iz_koda);

			provjeri(lista_parametara);
			
			
			
			if(globalniDjelokrug.sadrziIdn(IDN.ime_iz_koda)){
				Informacija deklaracija = globalniDjelokrug.getIdentifikator(IDN.ime_iz_koda);
				
				//slozena logika usporedjivanja tipova elemenata 2 liste
				if(deklaracija.tip.equals(ime_tipa.getTip()) && deklaracija.isFunkcija==true
						&& deklaracija.tipovi.size() == lista_parametara.inf.tipovi.size()){
					for(int i=0;i<deklaracija.tipovi.size();i++){
						//morat æu radit hashmapu argumenata(parametara)
						if(!deklaracija.tipovi.get(i).equals(lista_parametara.inf.tipovi.get(i))){
							ispisiGresku(cvor);
						}//cudno izbazdariti listom
						
						
						
					}
				}
				else{
					ispisiGresku(cvor);
				}
			}
			
			//int bar(int x); int bar (int a){return 0;} je ok
			//za parametre vazna imena->map<string,string> parametri, argumente ne->list tipovi
			definiraneFunkcije.put(IDN.ime_iz_koda, new Informacija(ime_tipa.getTip(), lista_parametara.inf.tipovi,  lista_parametara.inf.imena));
			
			trenutnaFunkcija = trenutniDjelokrug.dodajFunkcijuUTablicu(ime_tipa.getTip(), IDN.ime_iz_koda, lista_parametara.inf.tipovi);
			trenutnaFunkcija.ime = IDN.ime_iz_koda;
			
			Djelokrug djelokrug = new Djelokrug(trenutniDjelokrug);//udji u djelokrug
			
			trenutniDjelokrug.vratiOdmak();
			
			for(int i=0;i<lista_parametara.inf.tipovi.size();i++){//ovdje se samo imena parametara koriste
				//TODO ovdje onaj r7 4 r7 moze
				djelokrug.dodajIdentifikatorUTablicu(lista_parametara.inf.tipovi.get(i), lista_parametara.inf.imena.get(i));
				
				djelokrug.lokacije_lokalnih_imena.put(lista_parametara.inf.imena.get(i), (i+1)*4); //TODO R7+
			}
			trenutniDjelokrug = djelokrug;
			

			int id = ++idDjelokrug;
			program.dodajLiniju("U_D_"+id);
			provjeri(cvor.djeca.get(5));
			program.dodajLiniju(" RET");
			program.dodajLiniju("IZ_D_"+id);
			trenutniDjelokrug.vratiOdmak();
			//TODO a sta snizovima
			//program.dodajLiniju(" ADD R7, "+4*lista_parametara.inf.tipovi.size()+", R7");
			
			trenutniDjelokrug = trenutniDjelokrug.roditeljDjelokrug;
			trenutnaFunkcija = null;
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_parametara> ::= <deklaracija_parametra>")){
			Cvor deklaracija_parametra = cvor.djeca.get(0);
			
			provjeri(deklaracija_parametra);
			
			cvor.inf = new Informacija();//cudno nema inf
			cvor.inf.tipovi.add(deklaracija_parametra.inf.tip);
			cvor.inf.imena.add(deklaracija_parametra.inf.ime);
		}
		if(cvor.trenutacna_produkcija().equals("<lista_parametara> ::= <lista_parametara> ZAREZ <deklaracija_parametra>")){
			Cvor lista_parametara = cvor.djeca.get(0);
			Cvor deklaracija_parametra = cvor.djeca.get(2);
			
			provjeri(lista_parametara);
			
			provjeri(deklaracija_parametra);
			
			if(lista_parametara.inf.imena.contains(deklaracija_parametra.inf.ime)){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija();//cudno nema inf
			cvor.inf.tipovi.addAll(lista_parametara.inf.tipovi);
			cvor.inf.tipovi.add(deklaracija_parametra.inf.tip);
			cvor.inf.imena.addAll(lista_parametara.inf.imena);
			cvor.inf.imena.add(deklaracija_parametra.inf.ime);
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<deklaracija_parametra> ::= <ime_tipa> IDN")){
			Cvor ime_tipa = cvor.djeca.get(0);
			Cvor IDN = cvor.djeca.get(1);
			
			provjeri(ime_tipa);
			if(ime_tipa.getTip().equals("void")){
				ispisiGresku(cvor);
			}
			
			cvor.inf = ime_tipa.inf;
			cvor.inf.ime = IDN.ime_iz_koda;
		}
		if(cvor.trenutacna_produkcija().equals("<deklaracija_parametra> ::= <ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA")){
			Cvor ime_tipa = cvor.djeca.get(0);
			Cvor IDN = cvor.djeca.get(1);
			
			provjeri(ime_tipa);
			if(ime_tipa.getTip().equals("void")){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("niz "+ime_tipa.inf.tip);
			cvor.inf.ime = IDN.ime_iz_koda;
			
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_deklaracija> ::= <deklaracija>")){
			provjeri(cvor.djeca.get(0));
			
			cvor.inf = cvor.djeca.get(0).inf;
		}
		if(cvor.trenutacna_produkcija().equals("<lista_deklaracija> ::= <lista_deklaracija> <deklaracija>")){
			provjeri(cvor.djeca.get(0));
			provjeri(cvor.djeca.get(1));
			
			cvor.inf = cvor.djeca.get(0).inf;
			cvor.inf.imena.addAll(cvor.djeca.get(1).inf.imena);
			cvor.inf.tipovi.addAll(cvor.djeca.get(1).inf.tipovi);
		}
		
		if(cvor.trenutacna_produkcija().equals("<deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ")){
			provjeri(cvor.djeca.get(0));
			
			//vari-jable brojevnog tipa ntip
			cvor.djeca.get(1).ntip=cvor.djeca.get(0).getTip();
			provjeri(cvor.djeca.get(1));//uz nasljedno svojstvo <lista_init...><-<imetipa>.tip
			
			
			cvor.inf = new Informacija();
			for(String ime : cvor.djeca.get(1).inf.imena){
				cvor.inf.imena.add(ime);
				cvor.inf.tipovi.add(cvor.djeca.get(0).inf.tip);
			}
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<lista_init_deklaratora> ::= <init_deklarator>")){
			cvor.djeca.get(0).ntip=cvor.ntip;
			provjeri(cvor.djeca.get(0));
			cvor.inf = cvor.djeca.get(0).inf;
			cvor.inf.imena.add(cvor.djeca.get(0).inf.ime);
		}
		if(cvor.trenutacna_produkcija().equals("<lista_init_deklaratora> ::= <lista_init_deklaratora> ZAREZ <init_deklarator>")){
			
			cvor.djeca.get(0).ntip = cvor.ntip;
			provjeri(cvor.djeca.get(0));
			
			cvor.djeca.get(2).ntip = cvor.ntip;
			provjeri(cvor.djeca.get(2));
			
			cvor.inf = cvor.djeca.get(0).inf;
			//cvor.inf.imena.addAll(cvor.djeca.get(0).inf.imena);
			cvor.inf.imena.add(cvor.djeca.get(2).inf.ime);
			
			
		}
		
		if(cvor.trenutacna_produkcija().equals("<init_deklarator> ::= <izravni_deklarator>")){
			Cvor izravni_deklarator = cvor.djeca.get(0);
			
			izravni_deklarator.ntip = cvor.ntip;
			provjeri(izravni_deklarator);
			
			cvor.inf = izravni_deklarator.inf;
			
			if(izravni_deklarator.getTip().startsWith("const") || izravni_deklarator.getTip().startsWith("niz const")){
				ispisiGresku(cvor);
			}
			
			String lokacija;
			if(trenutniDjelokrug.roditeljDjelokrug!=null){//T0D0 apsolutno neprovjereno
				
			}
			else{
				if(izravni_deklarator.inf.tip.startsWith("niz")){
					
					program.dodajNaKraj("G_"+izravni_deklarator.inf.ime+" `DS "+Integer.toHexString(izravni_deklarator.inf.br_elem*4));
				}
			}
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<init_deklarator> ::= <izravni_deklarator> OP_PRIDRUZI <inicijalizator>")){
			//napisi ovo tu
			Cvor izravni_deklarator = cvor.djeca.get(0);
			Cvor inicijalizator = cvor.djeca.get(2);
			
			izravni_deklarator.ntip = cvor.ntip;
			provjeri(izravni_deklarator);
			
			provjeri(inicijalizator);
			//TODO neznam
			cvor.inf = new Informacija();
			cvor.inf.ime = izravni_deklarator.inf.ime;
			cvor.inf.vrijednost = inicijalizator.inf.vrijednost;
			
			
			String lokacija;
			if(trenutniDjelokrug.roditeljDjelokrug!=null){
				
				lokacija = trenutniDjelokrug.getLokaciju(izravni_deklarator.inf.ime);
				if(izravni_deklarator.inf.tip.startsWith("niz")){
					
					for(int i=0;i<inicijalizator.inf.vrijednosti.size();i++){
						program.dodajLiniju(" POP R0");
						program.dodajLiniju(" STORE R0, ("+lokacija+")");//TODO ovo mozda nebude radilo
					}
				}
				else{
					lokacija = trenutniDjelokrug.getLokaciju(izravni_deklarator.inf.ime);
					program.dodajLiniju(" POP R0");
					program.dodajLiniju(" STORE R0, ("+lokacija+")");
				}
			}
			else{
				
				if(izravni_deklarator.inf.tip.startsWith("niz")){
					String linija ="G_"+izravni_deklarator.inf.ime+" DW %D ";
					for(String i : inicijalizator.inf.vrijednosti){
						linija=linija+i+", %D ";
					}
					program.dodajNaKraj(linija.substring(0, linija.length()-5));//micanje zareza
				}
				else{
					globalneVarijable.put(izravni_deklarator.inf.ime, inicijalizator.inf.vrijednost);
					program.dodajNaKraj("G_"+izravni_deklarator.inf.ime+" DW %D "+inicijalizator.inf.vrijednost);
				}
				
			}
			
			
			
			String T = null;
			if(izravni_deklarator.getTip().contains("int")){//int, const int, niz const int
				T="int";
			}
			else if(izravni_deklarator.getTip().contains("char")){//char, const char, niz char
				T="char";
			}
			else{
				ispisiGresku(cvor);
			}
			
			if((izravni_deklarator.getTip().equals("const "+T) || izravni_deklarator.getTip().equals(T)) && inicijalizator.inf.tipovi.isEmpty()){//cudno mozda napravi vize grezaka
				
				if(!implicitnoPretvoriva(inicijalizator.inf, T)){
					ispisiGresku(cvor);
				}
			}
			else if(izravni_deklarator.getTip().equals("niz "+T) || izravni_deklarator.getTip().equals("niz const "+T)){
				if(inicijalizator.inf.br_elem <= izravni_deklarator.inf.br_elem){
					if(!inicijalizator.inf.tipovi.isEmpty())
						for(String U:inicijalizator.inf.tipovi){
							if(!implicitnoPretvoriva(U, T)){
								
								ispisiGresku(cvor);
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
			else{
				ispisiGresku(cvor);
			}
		}
		
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN")){
			Cvor IDN = cvor.djeca.get(0);
			
			if(cvor.ntip.equals("void")){
				
				ispisiGresku(cvor);
			}
			
			if(trenutniDjelokrug.lokalniSadrziIdn(IDN.ime_iz_koda)){
				
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija(cvor.ntip);
			cvor.inf.ime = IDN.ime_iz_koda;
			cvor.inf.isFunkcija=false;
			
			trenutniDjelokrug.dodajIdentifikatorUTablicu(cvor.getTip(), IDN.ime_iz_koda);//TODO
			
			program.dodajLiniju(" ADD R7, %D 4,R7 ;maknuta vrjednost idn");
		}
		
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA")){
			Cvor IDN = cvor.djeca.get(0);
			Cvor BROJ = cvor.djeca.get(2);
			
			if(cvor.ntip.equals("void")){
				ispisiGresku(cvor);
			}
			
			if(trenutniDjelokrug.lokalniSadrziIdn(IDN.ime_iz_koda)){
				ispisiGresku(cvor);
			}
			int broj = 0;
			try{
				if(BROJ.ime_iz_koda.startsWith("0x")){//cudno ne spominju se druge baze
					broj = Integer.parseInt(BROJ.ime_iz_koda.substring(2),16);
				}
				else if(BROJ.ime_iz_koda.startsWith("0") && BROJ.ime_iz_koda.length()>1){
					broj = Integer.parseInt(BROJ.ime_iz_koda.substring(1),8);
				}
				else{
					broj = Integer.parseInt(BROJ.ime_iz_koda);
				}
		    } 
			catch(NumberFormatException e){ 
		    	ispisiGresku(cvor);
		    }
			if(broj<=0 || broj>1024){
				ispisiGresku(cvor);
			}
			
			cvor.inf = new Informacija("niz "+cvor.ntip);
			cvor.inf.br_elem = broj;
			cvor.inf.ime = IDN.ime_iz_koda;
			trenutniDjelokrug.dodajNizUTablicu(cvor.getTip(), IDN.ime_iz_koda, broj);
			
			
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA")){
			Cvor IDN = cvor.djeca.get(0);
			
			
			
			if(trenutniDjelokrug.tablica_lokalnih_imena.containsKey(IDN.ime_iz_koda)){
				Informacija deklaracija = trenutniDjelokrug.tablica_lokalnih_imena.get(IDN.ime_iz_koda);
				if(deklaracija.isFunkcija!=true || !deklaracija.tip.equals(cvor.ntip) || !deklaracija.tipovi.isEmpty()){
					ispisiGresku(cvor);
				}
				
			}
			else{
				//zabiljezi deklaraciju
				trenutniDjelokrug.dodajFunkcijuUTablicu(cvor.ntip, IDN.ime_iz_koda, null);
			}
			
			
			
			cvor.inf = new Informacija();//mozda ovak nes za fje napravit
			cvor.inf.tip = cvor.ntip;
			cvor.inf.isFunkcija = true;
			cvor.inf.ime = IDN.ime_iz_koda;
		}
		
		if(cvor.trenutacna_produkcija().equals("<izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA")){
			Cvor IDN = cvor.djeca.get(0);
			Cvor lista_parametara = cvor.djeca.get(2);
			
			provjeri(lista_parametara);
			
			if(trenutniDjelokrug.tablica_lokalnih_imena.containsKey(IDN.ime_iz_koda)){
				Informacija deklaracija = trenutniDjelokrug.tablica_lokalnih_imena.get(IDN.ime_iz_koda);
				//cudno-usporediti duljine?
				
				if(deklaracija.tipovi.size()==lista_parametara.inf.tipovi.size() && deklaracija.isFunkcija==true
						&& deklaracija.tip.equals(cvor.ntip)){
					for(int i=0;i<deklaracija.tipovi.size();i++){
						if(!deklaracija.tipovi.get(i).equals(lista_parametara.inf.tipovi.get(i))){
							ispisiGresku(cvor);
						}
					}
				}
				else{
					ispisiGresku(cvor);
				}
				
			}
			else{
				
				trenutniDjelokrug.dodajFunkcijuUTablicu(cvor.ntip, IDN.ime_iz_koda, lista_parametara.inf.tipovi);
			}
			
			cvor.inf = new Informacija(cvor.ntip);
			cvor.inf.tipovi.addAll(lista_parametara.inf.tipovi);
			cvor.inf.isFunkcija = true;
			
		}
		if(cvor.trenutacna_produkcija().equals("<inicijalizator> ::= <izraz_pridruzivanja>")){
			provjeri(cvor.djeca.get(0));
			
			//ako se izraz_pridruzivanja=>NIZ_ZNAKOVA - napraviti funkciju za ovo u cvor.java
			Cvor provjera = cvor.djeca.get(0);
			while(!provjera.djeca.isEmpty()){
				provjera = provjera.djeca.get(0);
			}
			if(provjera.getImeCvora().equals("NIZ_ZNAKOVA")){
				
				cvor.inf = cvor.djeca.get(0).inf;
				cvor.inf.br_elem = duljinaNizaZnakova(provjera.ime_iz_koda)-2+1;
				for(int i=0;i<cvor.inf.br_elem;i++){
					cvor.inf.tipovi.add("char");				
				}
			}
			else{
				cvor.inf = cvor.djeca.get(0).inf;
				cvor.inf.vrijednost = cvor.djeca.get(0).inf.vrijednost;
			}
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<inicijalizator> ::= L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA")){
			Cvor lista_izraza_pridruzivanja = cvor.djeca.get(1);
			
			provjeri(lista_izraza_pridruzivanja);
			
			
			cvor.inf = new Informacija();//cudno fali tip
			cvor.inf.br_elem = lista_izraza_pridruzivanja.inf.br_elem;
			cvor.inf.tipovi.addAll(lista_izraza_pridruzivanja.inf.tipovi);
			
			cvor.inf.vrijednosti.addAll(lista_izraza_pridruzivanja.inf.vrijednosti);
		}
		
		
		if(cvor.trenutacna_produkcija().equals("<lista_izraza_pridruzivanja> ::= <izraz_pridruzivanja>")){
			Cvor izraz_pridruzivanja = cvor.djeca.get(0);
			
			provjeri(izraz_pridruzivanja);
			
			cvor.inf = new Informacija();//cudno fali tip
			cvor.inf.br_elem = 1;
			cvor.inf.tipovi.add(izraz_pridruzivanja.getTip());
			cvor.inf.vrijednosti.add(izraz_pridruzivanja.inf.vrijednost);
		}
		if(cvor.trenutacna_produkcija().equals("<lista_izraza_pridruzivanja> ::= <lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja>")){
			Cvor lista_izraza_pridruzivanja = cvor.djeca.get(0);
			Cvor izraz_pridruzivanja = cvor.djeca.get(2);
			
			provjeri(lista_izraza_pridruzivanja);
			provjeri(izraz_pridruzivanja);
			
			cvor.inf = new Informacija();//cudno fali tip
			cvor.inf.br_elem = lista_izraza_pridruzivanja.inf.br_elem+1;
			cvor.inf.tipovi.addAll(lista_izraza_pridruzivanja.inf.tipovi);
			cvor.inf.tipovi.add(izraz_pridruzivanja.getTip());

			cvor.inf.vrijednosti.addAll(lista_izraza_pridruzivanja.inf.vrijednosti);
			cvor.inf.vrijednosti.add(izraz_pridruzivanja.inf.vrijednost);
		}
		
	}
	//TODO kraj pravila
	private static boolean eksplicitnoPretvoriva(String a, String b) {
		//  !((cast_izraz.getTip().equals(ime_tipa.getTip()) && (ime_tipa.getTip().equals("int") || ime_tipa.getTip().equals("char")))
		//|| cast_izraz.getTip().equals("int") && ime_tipa.getTip().equals("char"))
		if(a.equals(b)){//T~T
			return true;
		}
		
		if(a.startsWith("const")){//const T~T
			String T =a.substring(6);
			return eksplicitnoPretvoriva(T, b);
		}
		
		if(a.equals("int") || a.equals("char")){//char~const char, int~const int
			if(b.startsWith("const")){
				String T =b.substring(6);
				return eksplicitnoPretvoriva(a, T);
			}
		}
		
		if(a.equals("char") && b.equals("int") || a.equals("int") && b.equals("char")){//char~int, int-char
			return true;
		}
		
		return false;
	}
	

	private static void provjeraNakonObilaska() {
		if(!definiraneFunkcije.containsKey("main")){
			System.out.println("main");
			System.exit(0);
		}
		else if(!definiraneFunkcije.get("main").tip.equals("int") || definiraneFunkcije.get("main").tipovi!=null){//void -> tip, argumenti == null
				System.out.println("main");
				System.exit(0);
		}
		//ako postoji (bilodi)deklarirana a ne definirana fja

		
		
		for(int i=0;i<globalniDjelokrug.deklFjeImena.size();i++){
			
			boolean postoji = false;
			String imeFje = globalniDjelokrug.deklFjeImena.get(i);
			
			
			
			if(!definiraneFunkcije.containsKey(imeFje)){
				System.out.println("funkcija");
				System.exit(0);
			}
			else{
				String tipFje = definiraneFunkcije.get(globalniDjelokrug.deklFjeImena.get(i)).tip;
				Informacija defFunkcija = definiraneFunkcije.get(imeFje);
				if(tipFje.equals(globalniDjelokrug.deklFjeTipovi.get(i))){//ako su jednakih tipova sad usporedi parametre
					if(defFunkcija.tipovi==null && globalniDjelokrug.deklFjeTipoviPar.get(i)==null){
						continue;
					}
					else if(defFunkcija.tipovi!=null && globalniDjelokrug.deklFjeTipoviPar.get(i)==null || defFunkcija.tipovi==null && globalniDjelokrug.deklFjeTipoviPar.get(i)!=null){
						System.out.println("funkcija");
						System.exit(0);
					} else if(defFunkcija.tipovi.size()!=globalniDjelokrug.deklFjeTipoviPar.get(i).size()){
						
						System.out.println("funkcija");
						System.exit(0);
					} else{
						for(int j=0;j<defFunkcija.tipovi.size();j++){
							if(!defFunkcija.tipovi.get(j).equals(globalniDjelokrug.deklFjeTipoviPar.get(i).get(j))){
								System.out.println("funkcija");
								System.exit(0);
							}
						}
					}
				}
				else{
					System.out.println("funkcija");
					System.exit(0);
				}
				
			}
		}
	}

	private static int duljinaNizaZnakova(String a){
		String s = new String(a);
		s = s.replaceAll("\\\\\\\\", "a");
		s = s.replaceAll("\\\\t", "a");
		s = s.replaceAll("\\\\n", "a");
		s = s.replaceAll("\\\\0", "a");
		s = s.replaceAll("\\\\'", "a");
		s = s.replaceAll("\\\\\"", "a");
		return s.length();
	}

	
	private static boolean nizZnakovaIspravan(String s) {
		
		if(s.length()>3){
			for (int i = 1; i < s.length()-1; i++){//bez navodnika
				char prvi = s.charAt(i);
				char drugi = s.charAt(i+1); // "\\""
				if(i==s.length()-2)
					drugi = '$';
			    if(prvi == '\\' && (
		    		drugi != 't' && 
					drugi != 'n' && 
					drugi != '0' && 
					drugi != '\'' && 
					drugi != '\"' && 
					drugi != '\\' )){
			    	return false;
			    } 
			    else if(prvi == '\\' && (
			    		drugi == 't' || 
						drugi == 'n' || 
						drugi == '0' || 
						drugi == '\'' || 
						drugi == '\"' || 
						drugi == '\\' )){
				    	i++;
				}
			    
			    if(prvi != '\\' && drugi == '\"' || prvi == '\"'){
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
			
		}
		
		return false;
	}
	
	public static boolean implicitnoPretvoriva(Informacija ia, String b){
		if(ia.isFunkcija==true){
			return false;
		}
		return implicitnoPretvoriva(ia.tip, b);
	}
	
	public static boolean implicitnoPretvoriva(Informacija ia, Informacija ib){
		if(ia.isFunkcija==true || ib.isFunkcija==true){
			return false;
		}		
		return implicitnoPretvoriva(ia.tip, ib.tip);
	}
	
	public static boolean isInteger(String string, String predznak){
		if(predznak==null){
			predznak="";
		}
		
		
		try {
			if(string.startsWith("0x")){//cudno ne spominju se druge baze
				Integer.parseInt(predznak+string.substring(2),16);
			}
			else if(string.startsWith("0") && string.length()>1){
				Integer.parseInt(predznak+string.substring(1),8);
			}
			else{
				Integer.parseInt(predznak+string);
			}
	    } catch(NumberFormatException e) { 
	        return false;
	    }
		return true;
	}
	
	public static void ispisiGresku(Cvor cvor){
		System.out.print(cvor.getImeCvora()+" ::=");
		for(Cvor dijete:cvor.djeca){
			System.out.print(" "+dijete.getImeCvora());
			
			if(dijete.djeca.isEmpty()){
				System.out.print("("+dijete.redak+","+dijete.ime_iz_koda+")");
			}
		}
		System.out.println();
		System.exit(0);
	}
}
