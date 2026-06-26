package use.jgit.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.jgit.common.IMergeResultResolvedZZZ;
import use.jgit.common.MergeResultResolvedZZZ;
import use.jgit.protcol.git.JgitStarterGIT;
import use.jgit.resolve.EnumSetMappedStrategyMergeConflictUtilZZZ;
import use.jgit.resolve.IJgitResolverEnabled;
import use.jgit.resolve.JgitResolverLocal;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;
import use.jgit.resolve.JgitResolverUtilZZZ;
import use.jgit.tool.fetch.GitPostFetchAnalyse;
import use.jgit.tool.merge.GitPreMergeCheck;
import use.jgit.tool.merge.ResultPreMergeCheck;

/** Für den GIT Weg:
	 *  Merke: Bei Pull mit HTTPS ist es notwendig den pull in fetch und merge zu zerlegen
	 *         Hier ist ein fetch nicht notwendig.
	 * 
	 *  Eine robuste Utility-Methode, die:
	
		pull() ausführt
		CheckoutConflictException gezielt abfängt und danach den Pull automatisch erneut versucht
		oder
		den Merge-Status analysiert... FAILED ... CONFLICTING bearbeitet		
		nur die konfliktbehafteten Dateien zurücksetzt: OURS bleibt erhalten
		
		
		s. ChatGPT 20260323, 20260508ff
		
		
		Anders als bei HTTPS kann hier ein Pull direkt gemacht werden, also ohne Zerlegung in Fetch und Merge.
	 * 
	 * ABER: Achtung sUrlRepoRemote ist eine Url. Aber eine Url darf beim GIT Weg nicht direkt 
	 *       beim PullCommand.setRemote(s) für s verwendet werden. Das geht nur beim HTTPS Weg.
	 *        
	 * ERGO: Wir suchen anhand der übergebenen URL den (zuvor konfigurierten) Eintrag und nehmen den "Alias".        
	 * 
	 * ABER2: Beim Auflösen eines Konflikts müsste man erneut einen MERGE machen. 
  	 *        Da intern der PULL eh die Verwendung von FETCH und MERGE ist, wäre das ein unnoetiger doppelter MERGE Schritt.
  	 *        Darum eher pullGIT_by_FetchMerge verwenden.
  	     
  	Für die Konfliktauflösung:
  				bCheckRepositoryState        : AUCH BEIM IGNORIEREN WIRD DIESE VORBEDINGUNG WICHTIG, 
	        	                               - Es ist immer ein COMMIT notwendig. Ohne diesen bekommen wir beim PULL keine "CheckoutException".
	        	                                 Diese können wir wir wg. "Konflikt Ignorieren" aber gezielt behandeln.								
				
				ABER ohne einen commit gibt es im Bedarfsfall keinen Conflict, etc. 
				
				
				* s. Merke: ChatGPT vom 2026-622:
				Ein Konflikt entsteht nur, wenn dieselben Zeilen bzw. überlappende Bereiche geändert wurden.

				Dein Szenario sieht dann vermutlich so aus:
				
				Rechner 1:
				Datei A wird geändert.
				Änderung wird committet und ins Remote-Repository gepusht.
				Rechner 2:
				Dieselbe Datei A wurde lokal geändert, aber noch nicht committet.
				Die Änderung betrifft jedoch andere Zeilen.
				git pull auf Rechner 2:
				Der lokale Branch hat keine eigenen Commits → Fast-Forward ist möglich.
				JGit aktualisiert den Branch-Zeiger.
				Anschließend versucht Git, die Änderungen des Fast-Forward-Commits mit den lokalen Änderungen im Arbeitsverzeichnis zusammenzuführen.
				
				Da sich die Änderungen nicht überschneiden, kann Git beide Änderungen automatisch kombinieren.
				
				Deshalb erhältst du:
				
				keinen MergeResult
				keine CheckoutConflictException
				keinen Konfliktmarker (<<<<<<<)
				weiterhin einen FAST_FORWARD-Status
				
				Das ist normales Git-Verhalten.
				
				Wichtig zu verstehen:
				
				Fast-Forward bezieht sich nur auf die Commit-Historie.
				Konflikte beziehen sich auf den Dateiinhalt.
				
				Beides sind unabhängige Aspekte.
				
				Obwohl ein Fast-Forward stattfindet, prüft Git anschließend trotzdem, ob die lokalen, nicht committeten Änderungen erhalten bleiben können.
				
				Wenn du möchtest, dass bei jeglicher lokaler Änderung deine Strategie OURS gewinnt, reicht die Merge-Strategie nicht aus. Sie greift nur bei Konflikten zwischen Commits.
				
				Dann musst du vor dem Pull selbst entscheiden, wie mit lokalen Änderungen umgegangen werden soll, zum Beispiel:
				
				lokale Änderungen verwerfen (reset --hard)
				lokale Änderungen stashen und später wieder anwenden
				lokale Änderungen automatisch committen
				den Pull abbrechen, sobald status.getUncommittedChanges() nicht leer ist
				
				Eine automatische "immer OURS"-Behandlung für uncommittete Änderungen im Arbeitsverzeichnis gibt es in Git und JGit nicht.
 * @author Fritz Lindhauer
 *
 */
public class JgitUtilGIT implements IConstantZZZ{
	public static final String sPROTOCOL_PART = JgitStarterGIT.sPROTOCOL + "@";
	public static String addProtocolToUrl(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("UrlRepo", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
		
			//1. Prüfen, ob das Protokol (mit Separatoren) schon da ist.			
			String sProtocolPartFound = JgitUtilZZZ.getProtocolPart(sUrlRepo);

			if(StringZZZ.isEmpty(sProtocolPartFound)) {
				//dann einfach davorhängen
				sReturn = JgitUtilGIT.sPROTOCOL_PART + sUrlRepo;
			}else {
				//das gefundene Protokol entfernen
				String sUrlRepoWithoutProtocol = StringZZZ.stripLeft(sUrlRepo, sProtocolPartFound);
				sReturn = JgitUtilGIT.sPROTOCOL_PART + sUrlRepoWithoutProtocol;
			}
		}//end main:
		return sReturn;
	}
	
	//Z.B. GIT Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryAccountFromUrlGIT(String sUrlRepo) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) break main;
			
			//klappt vielleicht nicht immer... sReturn = StringZZZ.right(":"+ sRepositoryRemoteUrlGIT, ":");
			
			//Neben dem Host steht der Account
			String sHost = JgitUtilGIT.getHostFromUrl(sUrlRepo);	
		
			//sReturn = StringZZZ.mid(sRepositoryRemoteUrlGIT+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
			//sReturn = StringZZZ.midLeftRight(sRepositoryRemoteUrlGIT+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = StringZZZ.midRightLeft(sUrlRepo+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
		}//end main:
		return sReturn;
	}

	//Z.B. GIT Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryHostFromUrlGIT(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilGIT.getHostFromUrl(sUrlRepo);
	}

	//Z.B. GIT Version: 	git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryProjectFromUrlGIT(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilGIT.getProjectFromUrl(sUrlRepo);
	}
	
	/** Z.B.  von Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlGIT
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlPartFromUrlGIT(String sUrlGIT) throws ExceptionZZZ {
		return JgitUtilGIT.getUrlPartFromUrl(sUrlGIT);
	}


	/** Berechne die RemoteUrl - auch wenn eine ssh Url uebergeben worden ist - passend fuer HTTPS
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalGIT(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlPartFromRepo = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
			
			//String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl(JgitStarterHTTPS.sPROTOCOL, sUrlPartFromRepo);
			String sRepositoryHostIn = JgitUtilZZZ.computeRepositoryHostFromUrlRepo(sUrlRepoRemoteIn);
			
			String sRepositoryAccountIn = JgitUtilZZZ.computeRepositoryAccountFromUrlRepo(sUrlRepoRemoteIn);
			String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);			
			
			String sUrlRepoRemote = JgitUtilGIT.computeRepositoryUrlTotalGIT(sRepositoryHostIn, sRepositoryAccountIn, sRepositoryProjectIn);
			sReturn = sUrlRepoRemote;
		}//end main:
		return sReturn;		
	}
	
	//Z.B. GIT Version: 	git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlTotalGIT(String sUrlBaseGitWithAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlBaseGitWithAccountIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBaseGIT = sUrlBaseGitWithAccountIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			sReturn = sUrlBaseGIT + UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryProject + ".git";
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlTotalGIT(String sHostIn, String sAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlBaseGIT = JgitUtilGIT.computeRepositoryUrlBaseGIT(sHostIn, sAccountIn);		
			sReturn = JgitUtilGIT.computeRepositoryUrlTotalGIT(sUrlBaseGIT, sRepositoryProjectIn);
		}//end main:
		return sReturn;
	}
	
	/** Berechne die Remote Url - auch wenn eine https Url uebergeben worden ist - passend fuer HTTPS	 	
	 * Zum Einsatz beim FETCH
	 * @param sUrlRepoRemoteIn
	 * @param sPAT
	 * @return z.B.: https://firak01:<sPAT>@github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalGIT_forFetch(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		return JgitUtilGIT.computeRepositoryUrlTotalGIT(sUrlRepoRemoteIn);	
	}
	
	/** Berechne die Remote Url - auch wenn eine https Url uebergeben worden ist - passend fuer HTTPS	 	
	 * Zum Einsatz beim PUSCH
	 * @param sUrlRepoRemoteIn
	 * @param sPAT
	 * @return z.B.: https://firak01:<sPAT>@github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalGIT_forPush(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			sReturn = computeRepositoryUrlTotalGIT_forFetch(sUrlRepoRemoteIn);
		}//end main:
		return sReturn;
	}
	//###########################
	//Z.B. GIT Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryUrlBaseGIT(String sHostIn, String sAccountIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sHostIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Hostname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sAccountIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Account für das Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sHost = sHostIn;
			String sAccount = sAccountIn;
			
			sReturn = JgitStarterGIT.sPROTOCOL + "@" + sHost + ":" + sAccount;
		}//end main:
		return sReturn;
	}

	//#######################################################
	/** Z.B.  von git@github.com:firak01 --> github.com
	 * @param sRepositoryRemoteUrlGIT
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getHostFromUrl(String sRepositoryRemoteUrlGIT) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlGIT)) break main;
			
			sReturn = StringZZZ.mid(sRepositoryRemoteUrlGIT, "@", ":");
		}//end main:
		return sReturn;
	}
	
	
	/** Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git --> Projekt_Kernel02_JAZDummy
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProjectFromUrl(String sRepositoryRemoteUrl) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrl)) break main;
			
			String sUrlWithoutEnding = StringZZZ.stripRight(sRepositoryRemoteUrl, ".git");
			String sProject = StringZZZ.right(sUrlWithoutEnding, UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = sProject;
		}//end main:
		return sReturn;
	}
	

	/** Z.B.  von git@github.com:firak01 --> git
	 * @param sRepositoryRemoteUrlGIT
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocolFromUrl(String sRepositoryRemoteUrlGIT) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlGIT)) break main;
			
			sReturn = StringZZZ.left(sRepositoryRemoteUrlGIT+"@", "@");
		}//end main:
		return sReturn;
	}
	

	/** Z.B.  von git@github.com:firak01
	 *       oder git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlGIT
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getAccountFromUrl(String sRepositoryRemoteUrlGIT) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlGIT)) break main;
			
			//klappt vielleicht nicht immer... sReturn = StringZZZ.right(":"+ sRepositoryRemoteUrlGIT, ":");
			
			//Neben dem Host steht der Account
			String sHost = JgitUtilGIT.getHostFromUrl(sRepositoryRemoteUrlGIT);	
	
			sReturn = StringZZZ.midRightLeft(sRepositoryRemoteUrlGIT+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
		}//end main:
		return sReturn;
	}
	
	/** Z.B.  von Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git --> github.com:firak01
	 * @param sRepositoryRemoteUrlGIT
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getUrlPartFromUrl(String sRepositoryRemoteUrl) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrl)) break main;
				
				String sUrlPartDomainFromHttpsRepo =StringZZZ.right("@" + sRepositoryRemoteUrl, "@");				
				/////sUrlPartDomainFromHttpsRepo = StringZZZ.left(sUrlPartDomainFromHttpsRepo + ":", ":");				
				////String sUrlPartRepoFromHttpsRepo = StringZZZ.right(":" + sUrlHTTPS, ":");				
				////sReturn = sUrlPartDomainFromHttpsRepo + "/" + sUrlPartRepoFromHttpsRepo;
				
			sReturn = UrlLogicZZZ.getUrlWithoutParameter(sUrlPartDomainFromHttpsRepo);
			sReturn = StringZZZ.left(sReturn + "/", "/");
		}//end main:
		return sReturn;
	}
	
	//######################################################
	//###  PULL
	
	/** 
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 * @throws CheckoutConflictException 
	 * @throws TransportException 
	 */
	public static MergeResult pullGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		return JgitUtilGIT.pullGIT_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, null, true);
	}
	
	/** 
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 * @throws CheckoutConflictException 
	 * @throws TransportException 
	 */
	public static MergeResult pullGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		return pullGIT_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, true, true);
	}
	
	public static MergeResult pullGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bByFetchMerge) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		return pullGIT_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, bByFetchMerge, true);
		
	}
	
	public static MergeResult pullGIT_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bByFetchMerge, boolean bCheckRepositoryState) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		if(bByFetchMerge) {
			return JgitUtilGIT.pullGIT_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, bCheckRepositoryState);
		}else {
			return JgitUtilGIT.pullGIT_by_Direct_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, bCheckRepositoryState);
		}
		
	}
	
	/** 
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static MergeResult pullGIT_by_Direct_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bCheckRepositoryState) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
			try {	
				
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				 //!!! Z.B. BEIM IGNORIEREN WIRD DIESE VORBEDINGUNG NICHT WICHTIG, 
	        	//    - Sonst ist immer ein COMMIT notwendig. Ohne diesen bekommen wir beim PULL eine "CheckoutException".
	        	//      Diese können wir wir wg. "Konflikt Ignorieren" aber gezielt behandeln.	        	
	        	if(bCheckRepositoryState) {
	        		//wg. der fast-forward Problematik (s. ChatGPT vom 20260622) kann es eine immer "OURS" Behandlung nicht geben.
					//Will man, dass die lokale Änderung auf jeden Fall überlebt, muss erste ein commit erzwungen werden.
					System.out.println("PULL: bCheckRepositoryState=" + bCheckRepositoryState);
	        							
					//!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
			        //             ohne eine Exception durchlaufen kann
			        //Vorprüfung per eigener, gekapselter Routine
			        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
			        if (!check.isClean()) {
			            check.printReport();
			            break main; // Merge abbrechen
			        }
	        	}
		        
		        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		        if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        
		        String sBranch="master";
		        if (!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch = sBranchIn;
		
		        //wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("GIT-Loesung: Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
				
//				//Die URL neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach GIT umgewandelt werden.				
//				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
				String sUrlBaseIn = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl("git", sUrlBaseIn);
				String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlRepoRemoteALT = JgitUtilZZZ.computeRepositoryUrlTotalFor("git", sUrlBaseWithProtocolIn, sRepositoryProjectIn);
				
				//Die URL neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach GIT umgewandelt werden.				
		        //In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
		        //String sUrlRepoRemote = JgitUtilHTTPS.computeRepositoryUrlTotalHTTPS_forFetch(sUrlRepoRemoteIn, sPAT);
				String sUrlRepoRemote = JgitUtilGIT.computeRepositoryUrlTotalGIT(sUrlRepoRemoteIn);
				System.out.println("Url für die Suche nach dem RepositoryAlias. Remote: " + sUrlRepoRemote);
				
				//Debug only
				if(sUrlRepoRemote.equals(sUrlRepoRemoteALT)) {
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": neuer Berechnungweg = alter Berechnungsweg...");
				}else {
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ACHTUNG !!!");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": neuer Berechnungweg != alter Berechnungsweg...");
				}
				
				
				//Da wir den Aliasnamen übergeben müssen, aber eine Url reinbekommen.
				//Müssen wir aus der Url den Aliasnamen errechnen.
				//denn hier in der static Methode geht ja leider nicht: this.getRepositoryRemoteAlias(); 
				
				String sRemoteRepositoryAlias = JgitUtilZZZ.findRemoteNameByUrl(git, sUrlRepoRemote);
				System.out.println("Verwendete RepositoryAlias für Remote: " + sRemoteRepositoryAlias);
				
				//==========================================
				// 1. PULL 
				//==========================================
				
				// aber mal explizit als pullCommand
				PullCommand pullCommand = git.pull();		
				
				//pullCommand.setRemote(sUrlRepoRemote); //Aber: Anders als beim HTTPS Weg, darf die URL nicht direkt übergeben werden.
                //      Statt dessen den "Aliasnamen" übergeben.
				pullCommand.setRemote(sRemoteRepositoryAlias);

				// pull from remote, hier mit Auswertung des Ergebnisses	
				PullResult pullResult = pullCommand.call();
				
				if (pullResult.isSuccessful()) {
				    System.out.println("Pull erfolgreich");
				} else {
				    System.out.println("Pull fehlgeschlagen");
				}

				objReturn = pullResult.getMergeResult();
																					
				//###############################################################		
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return objReturn;
	}
	
	/** Hier wird nun der PULL-COMMAND nicht direkt ausgeführt, 
	 *  sondern wie bei der HTTPS Loesung in Fetch und Merge aufgeteilt.
	 *  
	 *  Merke: Intern wird der PULL-Command sowieso in Fetch und Merge aufgeilt.
	 *         Wenn ich das selber mache, habe ich mehr Einfluss und brauche nicht ein zusätzliches Merge,
	 *         um Fehler abzufangen.
	 *         
	 /*
				Frage:
				Wenn ich git.pull().setRemote(...) verwenden möchte und nicht einen in der .git\config verwendeten Namen angeben möchte.
				Kann ich dann auch eine URL mitgeben? Kann solch eine mitgegebene URL auch den "Personal Access Token" beinhalten?
				
				Antwort:
				Kurz gesagt: Nein, so wie du es dir vorstellst funktioniert es mit pull() nicht.
				VARIANTE 1. setRemote(...) erwartet keine URL
	
				In JGit ist:
				git.pull().setRemote("origin")
	
				👉 kein URL-Parameter, sondern der Name eines konfigurierten Remotes aus der .git/config.
	
				Also z. B.:
				[remote "origin"]
					url = https://github.com/user/repo.git
	
				➡️ setRemote("origin") = Referenz auf diesen Eintrag
				➡️ Direkte URL ist hier nicht vorgesehen
	
				
	 * @param git
	 * @param credentialsProvider
	 * @param remoteUrl
	 * @param branch	
	 * @return
	 * @throws ExceptionZZZ
	 * @throws CheckoutConflictException 
	 * @throws TransportException 
	 */
	private static MergeResult pullGIT_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bCheckRepositoryState) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		MergeResult objReturn = null;
		main:{	      
		        if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
		        
		        if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        
		        String sBranch="master";
		        if(!StringZZZ.isEmptyNull(sBranchIn)) sBranch = sBranchIn;
		        
		        //!!! Z.B. BEIM IGNORIEREN WIRD DIESE VORBEDINGUNG NICHT WICHTIG, 
	        	//    - Sonst ist immer ein COMMIT notwendig. Ohne diesen bekommen wir beim PULL eine "CheckoutException".
	        	//      Diese können wir wir wg. "Konflikt Ignorieren" aber gezielt behandeln.	        	
	        	if(bCheckRepositoryState) {
	        		//wg. der fast-forward Problematik (s. ChatGPT vom 20260622) kann es eine immer "OURS" Behandlung nicht geben.
					//Will man, dass die lokale Änderung auf jeden Fall überlebt, muss erste ein commit erzwungen werden.
					System.out.println("PULL: bCheckRepositoryState=" + bCheckRepositoryState);

	        			        	
			        //!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
			        //             ohne eine Exception durchlaufen kann
			        //Vorprüfung per eigener, gekapselter Routine
			        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
			        if (!check.isClean()) {
			            check.printReport();
			            break main; // Merge abbrechen
			        }
	        	}
		        
		        
		        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		       
		       		        
		        //Zwar geht Pull auch direkt, aber: Pull ist eh eine Anwendung von fetch und merge. Wenn ich das mache spart es einen Merge am Schluss.							
				System.out.println("GIT-Loesung: Spare einen MERGE. Zerlege deshalb PULL in FETCH und MERGE");
				
		        //++++++++++++
		        //Die URL neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach GIT umgewandelt werden.				
				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
		        String sUrlRepoRemote = JgitUtilGIT.computeRepositoryUrlTotalGIT_forFetch(sUrlRepoRemoteIn);
		        System.out.println("Url fuer Fetch (neu ausgerechnet): '" + sUrlRepoRemote + "'");
				
		        //Aber: Anders als beim HTTPS Weg, darf die URL nicht direkt übergeben werden.
		        //      Statt dessen den "Aliasnamen" übergeben.
				//pullCommand.setRemote(sUrlRepoRemote);
				                                         
				//Da wir den Aliasnamen übergeben müssen, aber eine Url reinbekommen.
				//Müssen wir aus der Url den Aliasnamen errechnen.
				//denn hier in der static Methode geht ja leider nicht: this.getRepositoryRemoteAlias(); 
		        
				String sRemoteRepositoryAlias = JgitUtilZZZ.findRemoteNameByUrl(git, sUrlRepoRemote);
				System.out.println("RepositoryAlias fuer Fetch (GIT Weg, neu gesucht per Url): '" + sRemoteRepositoryAlias + "'");
						        
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // ========================= 
		        //Aber wenn nichts zu fetchen ist, gibt es einen Fehler, darum
				FetchResult fetchResult = JgitUtilZZZ.fetchIgnoreNothingToFetch(git, credentialsProvider, sRemoteRepositoryAlias, sBranchIn);
				if(fetchResult==null) break main;
		        GitPostFetchAnalyse.logFetchResult(fetchResult);
		
		        // =========================
		        // 2. MERGE (gezielt!)
		        // =========================
		      
		        //Den Merge durchführen, er sollte nach erfolgreicher Vorprüfung nicht abbrechen.
		       objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);			       
	        
		}//end main:
		return objReturn;
    }

	/** 
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static boolean pullIgnoreCheckoutConflictsGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bUseFetchMerge, boolean bCheckRepositoryState) throws ExceptionZZZ {
		if(bUseFetchMerge) {
			return JgitUtilGIT.pullIgnoreCheckoutConflictsGIT_by_FetchMerge_(git,credentialsProvider, sUrlRepoRemoteIn, sBranchIn, bCheckRepositoryState);
		}else {
			return JgitUtilGIT.pullIgnoreCheckoutConflictsGIT_by_Direct_(git,credentialsProvider, sUrlRepoRemoteIn, sBranchIn, bCheckRepositoryState);
		}
	}
	
	/**
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static boolean pullIgnoreCheckoutConflictsGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bUseFetchMerge) throws ExceptionZZZ {
		return JgitUtilGIT.pullIgnoreCheckoutConflictsGIT(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn,bUseFetchMerge, true);
	}
	
	
	
	/** Für den GIT Weg:
	 * 
		Minierklaerung:
				siehe .git\config Datei, entsprechende Zeile.
				 
				Das ist ein sogenannter RefSpec (Reference Specification).
				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
				
				Aufbau allgemein:
				[+]<Quelle>:<Ziel>
				
				Also:
				Quelle (Remote-Seite)
				refs/heads/ = alle Branches im Remote-Repository
				 * = Wildcard → alle Branch-Namen
	
				➡️ Bedeutet:
				Hole alle Branches vom Remote
				
				
				Ziel (lokal)
				refs/remotes/origin/ = Remote-Tracking-Branches
				* = gleicher Name wie Quelle
	
				➡️ Bedeutet:
				Speichere sie lokal als origin/branchname
				
				------------
				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
				Mit + sagst du:
				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
		
	 * @param git
	 * @throws GitAPIException
	 * @author Fritz Lindhauer, 23.03.2026, 18:17:59
	 * @throws ExceptionZZZ 
	 */
	private static boolean pullIgnoreCheckoutConflictsGIT_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bCheckRepositoryState) throws ExceptionZZZ {
		boolean bReturn = false;
		IMergeResultResolvedZZZ objReturn = new MergeResultResolvedZZZ();
		IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy = IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS;
		main:{
	        try {
	        	if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
	        	
	        	String sBranch = "master";
	        	if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
		       
	        	if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
			           throw new IllegalArgumentException("remoteUrl must not be empty");
			    }
	        	    
	        	//+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
	        	//    Weil wir konflikte Ignorieren gibt es hier die Strategie OURS, zwingend.
				System.out.println("PULL: Startet");
				try {											        
					//Mache hier den Pull durch einen FETCH gefolgt von einem MERGE					
					MergeResult objMergeResult = pullGIT_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch, bCheckRepositoryState);								
					if(objMergeResult==null) { System.out.println("PULL: MergeResult: Null."); break main; }

					//Mit dem ersten Merge - Ergebnis weiterarbeiten. 
					//Das ist der Vorteil gegenüber einem "direkten" PULL
					//Merke: Bei HTTPS gibt es nur FetchMerge, keinen direkten PULL
					objReturn.setOriginalResult(objMergeResult);
										
					//TODO: Die Stage ausserhalb der Schleife holen und dann übergeben.
					//resolveConflicts als Methode mit Stage anbieten:
					//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
					//CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
					
					//Wenn aber keine Exception geworfen wird, den Status direkt abfragen
					//Mache eine Schleife um diese Fehler zu beheben, statt eine verschachtelte if Struktur...						
					boolean bGoon=true; int iCount=0; boolean bAnyResolved=false;
					
					MergeStatus status = objMergeResult.getMergeStatus();
					System.out.println("PULL: Merge-Status ("+iCount+"): " + status.toString());
					if(status.equals(MergeStatus.ALREADY_UP_TO_DATE)) bGoon = false;
					
					while((status.equals(MergeStatus.CONFLICTING)
							| status.equals(MergeStatus.FAILED))
							& bGoon){
						boolean bMyAutoResolve=false;
						iCount++; 					
						
						if(status.equals(MergeStatus.CONFLICTING)) {
							 System.out.println("PULL: Konflikte erkannt ("+iCount+"). IgnoreConflicts. Strategy: " + objEnumStrategy.getName());
							
							 //Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
							 bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objMergeResult, objEnumStrategy);
							 bMyAutoResolve = true;
							 
						}//end STATUS "CONFLICTING"
						
						if(status.equals(MergeStatus.FAILED)) {
						    System.out.println("PULL: Failed erkannt ("+iCount+")");

						    bAnyResolved= JgitResolverUtilZZZ.resolveFailed(git, objMergeResult, objEnumStrategy);
						    bMyAutoResolve = true;
						}//end status FAILED 
												
						if(bAnyResolved) {
						    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" oder "Fail" behaftet.
						    //Also noch ein weiteres Mal versuchen einen sauberen Result zu bekommen
							MergeResult objMergeResult02 = JgitUtilZZZ.mergeWithResult(git, sBranch, false); //also den 2ten Merge nicht debuggen
						    
						    status = objMergeResult02.getMergeStatus();
							System.out.println("PULL: Merge-Status ("+iCount+"): " + status.toString());
					    }else {
					    	bGoon=false;
					    }
					}//end while
					
					if(iCount>=1) {
						System.out.println("\nPULL: Ergebnis des Konfliktignorierens:");
						
						//Erinnerung ausgeben, das die lokalen Änderungen zwar "ueberlebt" haben, aber noch nicht im Remote sind.
						System.out.println("PULL: " + objEnumStrategy.getDescriptionShort() +". Die behaltenen lokalen Versionen müssen noch gepusht werden, damit sie im Remote ist.");
						
						objReturn.isConflictsResolved(true);
					}
					
					if(bAnyResolved) {					
						//Prüfen ob, noch Konflikte vorhanden sind
						Status statusGit = git.status().call();
						objReturn.setGitStatus(statusGit);
						
						//Prüfen, ob der State des Repositories SAFE ist.
						RepositoryState stateRepo = git.getRepository().getRepositoryState();
						objReturn.setRepositoryState(stateRepo);
					}
			        
			        bReturn = true;
					//###############################################################
		        } catch (CheckoutConflictException cce) {
		        	System.out.println("Pull PreMerge Konflikte: CheckoutConflictException...");
		            System.out.println("Pull PreMerge Konflikte: Können mit MergeStrategy nicht aufgeloest werden. Wg. 'ignorieren' Strategy über STAGE trotzdem versuchen.");
		        	
		            Collection<String> conflictingPaths = cce.getConflictingPaths();
		
		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
		                // Kein konkreter Pfad bekannt → weiterwerfen
		            	System.out.println("Pull PreMerge Konflikte: Problem: Keine konkreten Dateien erkannt.");
		            	ExceptionZZZ ez = new ExceptionZZZ(cce);
		    			throw ez;
		            }
	
		          //Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
					System.out.println("Pull PreMerge Konflikte: Meine Strategy '" + objEnumStrategy.getName() + "' in STAGE umsetzen.");
					CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
					
		            //Konfliktdateien gezielt zurücksetzen
		            System.out.println("Pull PreMerge Konflikte: Setze Dateien gezielt zurueck:");		        	
		            for (String path : conflictingPaths) {
		                git.checkout()
		                	.setStage(objStage)
		                	.setForce(true)   // wichtig!
		                	.call();
		                System.out.println("* " + path);			        	
		            }
		
		            //Pull erneut versuchen
		            System.out.println("Pull PreMerge Konflikte: Pull erneut versuchen.");		            
		            bReturn = pullIgnoreCheckoutConflictsGIT_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch, false);
		        }
								
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}   
	    }//end main:
	    return bReturn;
	}
	
	
	/**
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @param sBranchIn
	 * @param bIgnoreRepositoryState
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static boolean pullIgnoreCheckoutConflictsGIT_by_Direct_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bCheckRepositoryState) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{	    
			 if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
		        
		        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		        if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        
		        String sBranch="master";
		        if(!StringZZZ.isEmptyNull(sBranchIn)) sBranch = sBranchIn;
		       		     
			
//			try {				
	        	
				       									
	        	
	        	//+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
				System.out.println("PULL: Startet");
													
				//Mache hier den Pull direkt, keine Konfliktbehandlung
				bReturn = pullIgnoreCheckoutConflictsGIT_by_Direct_(git, bCheckRepositoryState);
				
				
//			}catch(InvalidRemoteException ire) {
//				ExceptionZZZ ez = new ExceptionZZZ(ire);
//				throw ez;
//			}catch(TransportException te) {
//				ExceptionZZZ ez = new ExceptionZZZ(te);
//				throw ez;
//			}catch(GitAPIException gae) {
//				ExceptionZZZ ez = new ExceptionZZZ(gae);
//				throw ez;
//			}    
				
			bReturn = true;
	    }//end main:
	    return bReturn;
	}
	
	
	/** Damit sollte OURS immer gewinnen
	 * @param git
	 * @throws GitAPIException
	 * @author Fritz Lindhauer, 23.03.2026, 18:17:59
	 * @throws ExceptionZZZ 
	 */	
private static boolean pullIgnoreCheckoutConflictsGIT_by_Direct_(Git git) throws ExceptionZZZ {
	System.out.println("PULL: Wg. OURS, soll zwingend ein fast-forward vermieden werden, damit die lokale Datei bleibt. Daher bCheckRepositoryState=true und somit ggf. den commit erzwingen. ");
	return pullIgnoreCheckoutConflictsGIT_by_Direct_(git, true);
}
	
	/** Damit sollte OURS immer gewinnen
	 * @param git
	 * @throws GitAPIException
	 * @author Fritz Lindhauer, 23.03.2026, 18:17:59
	 * @throws ExceptionZZZ 
	 */				
private static boolean pullIgnoreCheckoutConflictsGIT_by_Direct_(Git git, boolean bCheckRepositoryState) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {

				 //!!! Z.B. BEIM IGNORIEREN WIRD DIESE VORBEDINGUNG NICHT WICHTIG, 
	        	//    - Sonst ist immer ein COMMIT notwendig. Ohne diesen bekommen wir beim PULL eine "CheckoutException".
	        	//      Diese können wir wir wg. "Konflikt Ignorieren" aber gezielt behandeln.	        	
	        	if(bCheckRepositoryState) {
	        		//wg. der fast-forward Problematik (s. ChatGPT vom 20260622) kann es eine immer "OURS" Behandlung nicht geben.
					//Will man, dass die lokale Änderung auf jeden Fall überlebt, muss erste ein commit erzwungen werden.
					System.out.println("PULL: bCheckRepositoryState=" + bCheckRepositoryState);

	        		
			        //!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
			        //             ohne eine Exception durchlaufen kann
			        //Vorprüfung per eigener, gekapselter Routine
			        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
 			        if (!check.isClean()) {
			            check.printReport();
			            break main; // VOR dem Merge abbrechen
			        }
	        	}
				
	        	try {
	        	    git.pull()
	        	       .setStrategy(MergeStrategy.OURS)
	        	       .call();
		        } catch (CheckoutConflictException e) {
		
		            Collection<String> conflictingPaths = e.getConflictingPaths();
		
		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
		                // Kein konkreter Pfad bekannt → weiterwerfen
		                throw e;
		            }
		
		            //Konfliktdateien gezielt zurücksetzen
		            for (String path : conflictingPaths) {
		                git.checkout()
		                   .setStage(CheckoutCommand.Stage.OURS)
		                   .addPath(path)
		                   .setForce(true)   // wichtig!
		                   .call();

		                git.add().addFilepattern(path).call();
		            }
		
		          //Pull erneut versuchen
		            git.pull()
		            .setStrategy(MergeStrategy.OURS)
		            .call();
		          

		          //!!! HINWEIS AUF NOTWENDIGE WEITER AKTIONEN				
		          //Noch haben wir hier keine Strategie...  //+ "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
				  String sLog="Konflikte wurden erfolgreich ignoriert.\n"
						  + "\tKonfliktdateien wurden gezielt zurückgesetzt."									 
						  + "HINWEIS: \t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Ein Commit und PUSH muss  noch gemacht werden.";
				  System.out.println(sLog);													
		        }
		        
		        bReturn = true;
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			} 
		}//end main
		return bReturn;
	}
		
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/** 
	 * @param git
	 * @throws GitAPIException
	 * @author Fritz Lindhauer, 23.03.2026, 18:17:59
	 * @throws ExceptionZZZ 
	 */
	public static IMergeResultResolvedZZZ pullResolveCheckoutConflictsGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ {		
		return pullResolveCheckoutConflictsGIT(git,credentialsProvider, sUrlRepoRemoteIn, null, objEnumStrategyMergeConflict, true);
	}
	
	/** 
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static IMergeResultResolvedZZZ pullResolveCheckoutConflictsGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict, boolean bUseFetchMerge) throws ExceptionZZZ {
		if(bUseFetchMerge) {
			return JgitUtilGIT.pullResolveCheckoutConflictsGIT_by_Direct_(git,credentialsProvider, sUrlRepoRemoteIn, null, objEnumStrategyMergeConflict, true);
		}else {
			return JgitUtilGIT.pullResolveCheckoutConflictsGIT_by_FetchMerge_(git,credentialsProvider, sUrlRepoRemoteIn, null, objEnumStrategyMergeConflict, true);
		}
	}
				
	public static IMergeResultResolvedZZZ pullResolveCheckoutConflictsGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy) throws ExceptionZZZ {
		return pullResolveCheckoutConflictsGIT(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, objEnumstrategy, true);
	}
	
	/**
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static IMergeResultResolvedZZZ pullResolveCheckoutConflictsGIT(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy, boolean bUseFetchMerge) throws ExceptionZZZ {
		if(bUseFetchMerge) {
			//das soll eigentlich verwendet werden
			return JgitUtilGIT.pullResolveCheckoutConflictsGIT_by_FetchMerge_(git,credentialsProvider, sUrlRepoRemoteIn, sBranchIn, objEnumstrategy, true);
		}else {
			//das ist dann irgendwie ein doppelter Merge
			return JgitUtilGIT.pullResolveCheckoutConflictsGIT_by_Direct_(git,credentialsProvider, sUrlRepoRemoteIn, sBranchIn, objEnumstrategy, true);			
		}				
	}
	
	
	/** 
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static IMergeResultResolvedZZZ pullResolveCheckoutConflictsGIT_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy, boolean bIgnoreRepositoryState) throws ExceptionZZZ {
		IMergeResultResolvedZZZ objReturn = new MergeResultResolvedZZZ();
		main:{	    
			try {	
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
	        	boolean bCheckRepositoryState = !bIgnoreRepositoryState;
	        									
	        	String sBranch = "master";
	        	if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
		       
	        	 if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
			            throw new IllegalArgumentException("remoteUrl must not be empty");
			     }
			        
	        	 
	        	//+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
				System.out.println("PULL: Startet");
				try {					
					MergeResult objMergeResult = null;
					
					//wg. der fast-forward Problematik (s. ChatGPT vom 20260622) kann es eine immer "OURS" Behandlung nicht geben.
					//Will man, dass die lokale Änderung auf jeden Fall überlebt, muss erste ein commit erzwungen werden.
					if(objEnumStrategy!=null){
						System.out.println("PULL: Verwendete Strategie: '" + objEnumStrategy.name() + "'");
					}
					if(objEnumStrategy.equals(STRATEGYMERGECONFLICT.OURS)){					
						System.out.println("PULL: Wg. OURS, soll zwingend ein fast-forward vermieden werden, damit die lokale Datei bleibt. Daher bCheckRepositoryState=true und somit ggf. den commit erzwingen. ");
						bCheckRepositoryState = true;
					}else {
						System.out.println("PULL: bCheckRepositoryState=" + bCheckRepositoryState);
					}
					
					//Mache hier den Pull durch einen FETCH gefolgt von einem MERGE
					objMergeResult = pullGIT_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch, bCheckRepositoryState);
					if(objMergeResult==null) { System.out.println("PULL: MergeResult: Null."); break main; }

					//Mit dem ersten Merge - Ergebnis weiterarbeiten. 
					//Das ist der Vorteil gegenüber einem "direkten" PULL
					//Merke: Bei HTTPS gibt es nur FetchMerge, keinen direkten PULL
					objReturn.setOriginalResult(objMergeResult);
									
					//TODO: Die Stage ausserhalb der Schleife holen
					//resolveConflicts als Methode mit Stage anbieten:
					//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
					//CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
					
					//Wenn aber keine Exception geworfen wird, den Status direkt abfragen
					//Mache eine Schleife um diese Fehler zu beheben, statt eine verschachtelte if Struktur...						
					boolean bGoon=true; int iCount=0; boolean bAnyResolved=false;
					
					MergeStatus status = objMergeResult.getMergeStatus();
					System.out.println("PULL: Merge-Status ("+iCount+"): " + status.toString());
					if(status.equals(MergeStatus.ALREADY_UP_TO_DATE)) bGoon = false;
					
					while((status.equals(MergeStatus.CONFLICTING)
							| status.equals(MergeStatus.FAILED))
							& bGoon){
						boolean bMyAutoResolve=false;
						iCount++;
						
						if(status.equals(MergeStatus.CONFLICTING)) {
						    System.out.println("PULL: Konflikte erkannt ("+iCount+"). IgnoreConflicts. Strategy: " + objEnumStrategy.getName());
						    
						    //Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
						    bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objMergeResult, objEnumStrategy);
						    bMyAutoResolve = true;
						}//end STATUS "CONFLICTING"
					
					
						if(status.equals(MergeStatus.FAILED)) {
						    System.out.println("PULL: Failed erkannt ("+iCount+")");
		
						    bAnyResolved= JgitResolverUtilZZZ.resolveFailed(git, objMergeResult);
						    bMyAutoResolve = true;
						}//end status FAILED 
				
						if(bAnyResolved) {
						    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" oder "Fail" behaftet.
						    //Also noch ein weiteres Mal versuchen einen sauberen Result zu bekommen
							MergeResult objMergeResult02 = JgitUtilZZZ.mergeWithResult(git, sBranch);
						    
						    status = objMergeResult02.getMergeStatus();
							System.out.println("PULL: Merge-Status ("+iCount+"): " + status.toString());
					    }else {
					    	bGoon=false;
					    }
					}//end while
			
					if(iCount>=1) {
						System.out.println("\nPULL: Ergebnis der Konfliktbehandlung:");
						if(objEnumStrategy.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
							//Erinnerung ausgeben, das die lokalen Änderungen zwar "ueberlebt" haben, aber noch nicht im Remote sind.
						    System.out.println("PULL: " + objEnumStrategy.getDescriptionShort() +". Die behaltenen lokalen Versionen müssen noch gepusht werden, damit sie im Remote ist.");
						}else {
							System.out.println("PULL: " + objEnumStrategy.getDescriptionShort());
						}
						
						objReturn.isConflictsResolved(true);
					}
											
					if(bAnyResolved) {
						
						//Prüfen ob, noch Konflikte vorhanden sind
						Status statusGit = git.status().call();
						objReturn.setGitStatus(statusGit);
						
						//Prüfen, ob der State des Repositories SAFE ist.
						RepositoryState stateRepo = git.getRepository().getRepositoryState();
						objReturn.setRepositoryState(stateRepo);
					}
					//###############################################################
		        } catch (CheckoutConflictException cce) {
		        	System.out.println("Pull PreMerge Konflikte: CheckoutConflictException...");
		            System.out.println("Pull PreMerge Konflikte: Können mit MergeStrategy nicht aufgeloest werden. Wg. 'ignorieren' Strategy über STAGE trotzdem versuchen.");
		        	
		            Collection<String> conflictingPaths = cce.getConflictingPaths();
		
		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
		                // Kein konkreter Pfad bekannt → weiterwerfen
		            	System.out.println("Pull PreMerge Konflikte: Problem: Keine konkreten Dateien erkannt.");
		            	ExceptionZZZ ez = new ExceptionZZZ(cce);
		    			throw ez;
		            }
	
		            //Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
					System.out.println("Pull PreMerge Konflikte: Meine Strategy '" + objEnumStrategy.getName() + "' in STAGE umsetzen.");
					CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
					
		            //Konfliktdateien gezielt zurücksetzen
		            System.out.println("Pull PreMerge Konflikte: Setze Dateien gezielt zurueck:");		        	
		            for (String path : conflictingPaths) {
		                git.checkout()
		                	.setStage(objStage)
		                	.addPath(path)
		                	.call();
		                System.out.println("* " + path);			        	
		            }
		
		            //Pull erneut versuchen
		            System.out.println("Pull PreMerge Konflikte: Pull erneut versuchen.");
		            objReturn = pullResolveCheckoutConflictsGIT_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch, objEnumStrategy, false);
		        }
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}       
	    }//end main:
	    return objReturn;
	}
	
	
	
	/**
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @param sBranchIn
	 * @param objEnumStrategy
	 * @param bIgnoreRepositoryState
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static IMergeResultResolvedZZZ pullResolveCheckoutConflictsGIT_by_Direct_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy, boolean bIgnoreRepositoryState) throws ExceptionZZZ {
		IMergeResultResolvedZZZ objReturn = new MergeResultResolvedZZZ();
		main:{
	        try {
	        	if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }	        
	        	
	        	boolean bCheckRepositoryState = !bIgnoreRepositoryState;
        	
	        	 String sBranch = "master";
			     if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
			     
			     if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
			            throw new IllegalArgumentException("remoteUrl must not be empty");
			     }
			     
			     //+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
				System.out.println("PULL: Startet");
				try {					
					MergeResult objMergeResult = null;
					
					//wg. der fast-forward Problematik (s. ChatGPT vom 20260622) kann es eine immer "OURS" Behandlung nicht geben.
					//Will man, dass die lokale Änderung auf jeden Fall überlebt, muss erste ein commit erzwungen werden.
					if(objEnumStrategy!=null){
						System.out.println("PULL: Verwendete Strategie: '" + objEnumStrategy.getAbbreviation() + "'");
					}
					if(objEnumStrategy.equals(STRATEGYMERGECONFLICT.OURS)){					
						System.out.println("PULL: Wg. OURS, soll zwingend ein fast-forward vermieden werden, damit die lokale Datei bleibt. Daher bCheckRepositoryState=true und somit ggf. den commit erzwingen. ");
						bCheckRepositoryState = true;
					}else {
						System.out.println("PULL: bCheckRepositoryState=" + bCheckRepositoryState);
					}
										
					//Mache hier den Pull direkt durch PullCommand
					objMergeResult = pullGIT_by_Direct_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch, bCheckRepositoryState);
					if(objMergeResult==null) { System.out.println("PULL: MergeResult: Null."); break main; }

					//Mit dem ersten Merge - Ergebnis weiterarbeiten. 
					//Das ist der Vorteil gegenüber einem "direkten" PULL
					//Merke: Bei HTTPS gibt es nur FetchMerge, keinen direkten PULL					
					objReturn.setOriginalResult(objMergeResult);
		      
					//TODO: Die Stage ausserhalb der Schleife holen
					//resolveConflicts als Methode mit Stage anbieten:
					//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
					//CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
					
					//Wenn aber keine Exception geworfen wird, den Status direkt abfragen
					//Mache eine Schleife um diese Fehler zu beheben, statt eine verschachtelte if Struktur...						
					boolean bGoon=true; int iCount=0; boolean bAnyResolved=false;
				
					MergeStatus status = objMergeResult.getMergeStatus();
					System.out.println("PULL: Merge-Status ("+iCount+"): " + status.toString());
					if(status.equals(MergeStatus.ALREADY_UP_TO_DATE)) bGoon = false;
				
					while((status.equals(MergeStatus.CONFLICTING)
							| status.equals(MergeStatus.FAILED))
							& bGoon){					
						boolean bMyAutoResolved=false;
						iCount++; 
						
						if(status.equals(MergeStatus.CONFLICTING)) {
							System.out.println("PULL: Konflikte erkannt ("+iCount+"). IgnoreConflicts. Strategy: " + objEnumStrategy.getName());
					    
							//Mit dem Merge - Ergebnis weiterarbeiten.
							bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objMergeResult, objEnumStrategy);
							bMyAutoResolved = true;
						}//end STATUS "CONFLICTING"
				
				
						if(status.equals(MergeStatus.FAILED)) {
							System.out.println("PULL: Failed erkannt ("+iCount+")");

							bAnyResolved= JgitResolverUtilZZZ.resolveFailed(git, objMergeResult);
							bMyAutoResolved = true;
					}//end status FAILED 
					
					if(bAnyResolved) {
					    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" oder "Fail" behaftet.
					    //Also noch ein weiteres Mal versuchen einen sauberen Result zu bekommen
						MergeResult objMergeResult02 = JgitUtilZZZ.mergeWithResult(git, sBranch);
					    
					    status = objMergeResult02.getMergeStatus();
						System.out.println("PULL: Merge-Status ("+iCount+"): " + status.toString());
				    }else {
				    	bGoon=false;
				    }
				}//end while
				
				if(iCount>=1) {
					System.out.println("\nPULL: Ergebnis der Konfliktbehandlung:");
					if(objEnumStrategy.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
						//Erinnerung ausgeben, das die lokalen Änderungen zwar "ueberlebt" haben, aber noch nicht im Remote sind.
						System.out.println("PULL: " + objEnumStrategy.getDescriptionShort() +". Die behaltenen lokalen Versionen müssen noch gepusht werden, damit sie im Remote ist.");
					}else {
						System.out.println("PULL: " + objEnumStrategy.getDescriptionShort());
					}
				}
				
				if(bAnyResolved) {
					
					//Prüfen ob, noch Konflikte vorhanden sind
					Status statusGit = git.status().call();
					objReturn.setGitStatus(statusGit);
					
					//Prüfen, ob der State des Repositories SAFE ist.
					RepositoryState stateRepo = git.getRepository().getRepositoryState();
					objReturn.setRepositoryState(stateRepo);
				}
				//###############################################################
	        } catch (CheckoutConflictException cce) {
	        	System.out.println("Pull PreMerge Konflikte: CheckoutConflictException...");
	            System.out.println("Pull PreMerge Konflikte: Können mit MergeStrategy nicht aufgeloest werden. Wg. 'ignorieren' Strategy über STAGE trotzdem versuchen.");
	        	
	            Collection<String> conflictingPaths = cce.getConflictingPaths();
	
	            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
	                // Kein konkreter Pfad bekannt → weiterwerfen
	            	System.out.println("Pull PreMerge Konflikte: Problem: Keine konkreten Dateien erkannt.");
	            	ExceptionZZZ ez = new ExceptionZZZ(cce);
	    			throw ez;
	            }

	            //Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
				System.out.println("Pull PreMerge Konflikte: Meine Strategy '" + objEnumStrategy.getName() + "' in STAGE umsetzen.");
				CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
				
	            //Konfliktdateien gezielt zurücksetzen
	            System.out.println("Pull PreMerge Konflikte: Setze Dateien gezielt zurueck:");		        	
	            for (String path : conflictingPaths) {
	                git.checkout()
	                	.setStage(objStage)
	                	.addPath(path)
	                	.call();
	                System.out.println("* " + path);			        	
	            }
	
	            //Pull erneut versuchen
	            System.out.println("Pull PreMerge Konflikte: Pull erneut versuchen.");
	            objReturn = pullResolveCheckoutConflictsGIT_by_Direct_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch, objEnumStrategy, false);
	        }
		}catch(InvalidRemoteException ire) {
			ExceptionZZZ ez = new ExceptionZZZ(ire);
			throw ez;
		}catch(TransportException te) {
			ExceptionZZZ ez = new ExceptionZZZ(te);
			throw ez;
		}catch(GitAPIException gae) {
			ExceptionZZZ ez = new ExceptionZZZ(gae);
			throw ez;
		}       
    }//end main:
    return objReturn;
	}

	
}
