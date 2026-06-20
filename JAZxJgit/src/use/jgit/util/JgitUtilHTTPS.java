package use.jgit.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.jgit.common.IMergeResultResolvedZZZ;
import use.jgit.common.MergeResultResolvedZZZ;
import use.jgit.protocol.https.JgitStarterHTTPS;
import use.jgit.resolve.EnumSetMappedStrategyMergeConflictUtilZZZ;
import use.jgit.resolve.IJgitResolverEnabled;
import use.jgit.resolve.JgitResolverUtilZZZ;
import use.jgit.tool.fetch.GitPostFetchAnalyse;
import use.jgit.tool.merge.GitPreMergeCheck;
import use.jgit.tool.merge.ResultPreMergeCheck;

public class JgitUtilHTTPS implements IConstantZZZ{
	public static final String sPROTOCOL_PART = JgitStarterHTTPS.sPROTOCOL + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL;
	
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
				sReturn = JgitUtilHTTPS.sPROTOCOL_PART + sUrlRepo;
			}else {
				//das gefundene Protokol entfernen
				String sUrlRepoWithoutProtocol = StringZZZ.stripLeft(sUrlRepo, sProtocolPartFound);
				sReturn = JgitUtilHTTPS.sPROTOCOL_PART +  sUrlRepoWithoutProtocol;
			}
		}//end main:
		return sReturn;
	}

	//Z.B. HTTPS Version: 	https://github.com/firak01   also ohne das Projekt
	public static String computeRepositoryProtocolFromUrlHTTPS(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilHTTPS.getProtocolFromUrl(sUrlRepo);
	}
	
	//Z.B. HTTPS Version: 	https://github.com/firak01   also ohne das Projekt
	public static String computeRepositoryAccountFromUrlHTTPS(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilHTTPS.getAccountFromUrl(sUrlRepo);
	}
	
	//Z.B. HTTPS Version: 	https://github.com/firak01   also ohne das Projekt
	public static String computeRepositoryHostFromUrlHTTPS(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilHTTPS.getHostFromUrl(sUrlRepo);
	}
	
	//Z.B. HTTPS Version:	https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryProjectFromUrlHTTPS(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilHTTPS.getProjectFromUrl(sUrlRepo);
	}
	
	//Z.B. HTTPS Version: 	https://github.com/firak01   also ohne das Projekt
	public static String computeRepositoryUrlBaseFromUrlHTTPS(String sUrlRepo) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)){
				ExceptionZZZ ez = new ExceptionZZZ("Url des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
								
			String sHost = JgitUtilHTTPS.computeRepositoryHostFromUrlHTTPS(sUrlRepo);			
			String sAccount = JgitUtilHTTPS.computeRepositoryAccountFromUrlHTTPS(sUrlRepo);
			sReturn = JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sHost, sAccount);			
		}//end main:
		return sReturn;
	}
	
	//Z.B. HTTPS Version: 	https://github.com/firak01   also ohne das Projekt
	public static String computeRepositoryUrlBaseHTTPS(String sHostIn, String sAccountIn) throws ExceptionZZZ{
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
			
			
			sReturn = JgitStarterHTTPS.sPROTOCOL + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL  + sHost + UrlLogicZZZ.sURL_SEPARATOR_PATH + sAccount;
		}//end main:
		return sReturn;
	}
	
	/*Ziel ist es für HTTPS eine PAT - String zu nutzen.
	  Aus Eclipse-Push Konfiguration:
	  Github - Projekt - HTTPS URL
	  https://github.com/firak01/HIS_QISSERVER_FGL.git
	
	  entspricht Github - Projekt - SSH
	  git@github.com:firak01/HIS_QISSERVER_FGL.git
	
	
	### ABER ###############
	Authentifizierung mit https
	https://medium.com/autotrader-engineering/working-with-git-in-java-part-1-a-jgit-tutorial-bc03b404a517
	
	Authenticating with a remote
	Most remote repos will require authentication (at least for the push command). In this tutorial, we’ll be working with remote repositories hosted on GitHub, which has two common authentication methods:
	Using a personal access token (PAT) for authentication over HTTPS
	Using SSH keys for authentication over SSH
	To keep things simple in this tutorial, we’ll only be covering HTTPS authentication; SSH is more complex and will be covered in part 2 of this two-part blog post.

	So in the following examples, we’ll be using a personal access token (PAT) for authentication via HTTPS. For more information on creating a PAT token, see the GitHub docs.
	Providing Credentials for Authentication

	The JGit command objects for operations such as git push, git pull, and git clone all share a setCredentialsProvider method that allows us to provide credentials to authenticate with the remote repository.

	The setCredentialsProvider method takes a CredentialsProvider instance as its parameter. This interface has many implementations, the one we need to use for a PAT token is the UsernamePasswordCredentialsProvider (more commonly used for basic authentication).
	Constructing a CredentialsProvider for a PAT token

	The UsernamePasswordCredentialsProvider 's constructor requires a username and password. When using a PAT token, we pass the token as the username and an empty string as the password:

	 * @param sPAT
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static CredentialsProvider createCredentialsProviderByToken(String sPAT) throws ExceptionZZZ {
		CredentialsProvider credentialsProvider = null;
		main:{
			if(StringZZZ.isEmpty(sPAT)){
				ExceptionZZZ ez = new ExceptionZZZ("sPAT", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
					
			credentialsProvider = new UsernamePasswordCredentialsProvider(sPAT, ""); //irgendwie empfohlen
			//CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("firak01", sPAT); //so funktioniert es auch nicht			
			/*Fehler:
			Exception in thread "main" org.eclipse.jgit.errors.UnsupportedCredentialItem: ssh://git@github.com:22: org.eclipse.jgit.transport.CredentialItem$YesNoType:The authenticity of host 'github.com' can't be established.
			RSA key fingerprint is.... .
			Are you sure you want to continue connecting?
			at org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider.get(UsernamePasswordCredentialsProvider.java:119)
			*/

		}//end main
		return credentialsProvider;
	}

		
		
	/** Z.B.  von  https://github.com/firak01
	 *        oder https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 *        
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getAccountFromUrl(String sRepositoryRemoteUrlHTTPS) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlHTTPS)) break main;
			
			//Neben dem Host steht der Account
			String sHost = JgitUtilHTTPS.getHostFromUrl(sRepositoryRemoteUrlHTTPS);	
			
			
			//sReturn = StringZZZ.mid(sRepositoryRemoteUrlHTTPS+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+UrlLogicZZZ.sURL_SEPARATOR_PATH, UrlLogicZZZ.sURL_SEPARATOR_PATH);
			//sReturn = StringZZZ.midLeftRight(sRepositoryRemoteUrlHTTPS+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+UrlLogicZZZ.sURL_SEPARATOR_PATH, UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = StringZZZ.midRightLeft(sRepositoryRemoteUrlHTTPS+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+UrlLogicZZZ.sURL_SEPARATOR_PATH, UrlLogicZZZ.sURL_SEPARATOR_PATH);
		}//end main:
		return sReturn;
	}
	
	/** Z.B.  von https://github.com/firak01
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getHostFromUrl(String sRepositoryRemoteUrlHTTPS) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlHTTPS)) break main;
			
			sReturn = UrlLogicZZZ.getHost(sRepositoryRemoteUrlHTTPS);
		}//end main:
		return sReturn;
	}
	
	/** Z.B. von https://github.com/firak01/Projekt_Kernel02_JAZDummy.git 
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProjectFromUrl(String sRepositoryRemoteUrlHTTPS) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlHTTPS)) break main;
			
			String sUrlWithoutEnding = StringZZZ.stripRight(sRepositoryRemoteUrlHTTPS, ".git");
			String sProject = StringZZZ.right(sUrlWithoutEnding, UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = sProject;
		}//end main:
		return sReturn;
	}
	
	
	/** Z.B.  von https://github.com/firak01
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocolFromUrl(String sRepositoryRemoteUrlHTTPS) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlHTTPS)) break main;
			
			try {
				sReturn = UrlLogicZZZ.getProtocol(sRepositoryRemoteUrlHTTPS);
			}catch(ExceptionZZZ urle) {
				if(urle.getMessage().startsWith("MalformedUrlException")) {
					//Mache nix, da es wohl durchaus beim Überprüfen einer anderen art z.B. git:// zu Fehlern kommen kann
				}else {
					throw urle;
				}
			}
		}//end main:
		return sReturn;
	}
	
	/** Z.B.  von https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getUrlPartFromUrl(String sRepositoryRemoteUrlHTTPS) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlHTTPS)) break main;
				
				//String sUrlPartDomainFromHttpsRepo =StringZZZ.right("@" + sUrlHTTPS, "@");				
				/////sUrlPartDomainFromHttpsRepo = StringZZZ.left(sUrlPartDomainFromHttpsRepo + ":", ":");				
				////String sUrlPartRepoFromHttpsRepo = StringZZZ.right(":" + sUrlHTTPS, ":");				
				////sReturn = sUrlPartDomainFromHttpsRepo + "/" + sUrlPartRepoFromHttpsRepo;
				
			sReturn = UrlLogicZZZ.getUrlWithoutParameter(sRepositoryRemoteUrlHTTPS);
				
			String sUrlPartDomainFromHttpsRepo = UrlLogicZZZ.getHost(sRepositoryRemoteUrlHTTPS); 
			String sUrlPartRepoFromHttpsRepo = UrlLogicZZZ.getPath(sReturn); 
			sReturn = sUrlPartDomainFromHttpsRepo + sUrlPartRepoFromHttpsRepo;
		}//end main:
		return sReturn;
	}
	
	public static IMergeResultResolvedZZZ pullIgnoreCheckoutConflictsHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		//Merke: Bei HTTPS ist der FetchMerge Ansatz der einzig mögliche. Es gibt keine direkte Pull-Lösung. 
				
		return JgitUtilHTTPS.pullIgnoreCheckoutConflictsHTTPS_by_FetchMerge_(git,credentialsProvider, sPAT, sRepoRemote, sBranch, objEnumStrategy, true); //true, d.h. kein RepostoryStateCheck notwendig. Also auch kein COMMIT Voraussetzung.
	}
	
	
	/** Für den HTTPS Weg:
	 * Merke: Bei Pull mit HTTPS ist es notwendig den pull in fetch und merge zu zerlegen
	 * 
	 *  Eine robuste Utility-Methode, die:
	
		pull() ausführt
		CheckoutConflictException gezielt abfängt
		nur die konfliktbehafteten Dateien zurücksetzt
		danach den Pull automatisch erneut versucht
		
		s. ChatGPT 20260323
		
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
	
				VARIANTE 2. URL direkt übergeben? → Nur über fetch()
	
				Wenn du eine URL direkt verwenden willst, musst du den Pull zerlegen:
				👉 pull = fetch + merge
	
				Beispiel (HTTPS mit URL + Token)
				FetchResult fetchResult = git.fetch()
				.setRemote("https://<token>@github.com/user/repo.git")
				.call();
	
				git.merge()
				.include(fetchResult.getAdvertisedRef("refs/heads/main"))
				.call();
		
		
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
	private static IMergeResultResolvedZZZ pullIgnoreCheckoutConflictsHTTPS_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy, boolean bIgnoreRepositoryState) throws ExceptionZZZ {
		IMergeResultResolvedZZZ objReturn = new MergeResultResolvedZZZ();
		main:{
	        try {
	        	
	        	//bSuppressExceptionOnMergeFail: wir wollen die Exception auf jeden Fall bekommen und dann mit der Strategie auflösen.
	        	//bCheckRepositoryState        : BEIM IGNORIEREN WIRD DIESE VORBEDINGUNG NICHT WICHTIG, 
	        	//                               - Sonst ist immer ein COMMIT notwendig. Ohne diesen bekommen wir beim PULL eine "CheckoutException".
	        	//                                 Diese können wir wir wg. "Konflikt Ignorieren" aber gezielt behandeln.	
	        	boolean bSuppressExceptionOnMergeFail = false;
	        	boolean bCheckRepositoryState = !bIgnoreRepositoryState;
	        				
	        	String sBranch = "master";
	        	if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
		       
				//+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
				System.out.println("Pull: Startet");
				try {
					MergeResult objMergeResult = pullHTTPS_by_FetchMerge_(git, credentialsProvider, sPAT, sRepoRemote, sBranch, bSuppressExceptionOnMergeFail, bCheckRepositoryState);								
					if(objMergeResult==null) { System.out.println("MergeResult: Null."); break main; }

					
					
					
					//Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
					objReturn.setOriginalResult(objMergeResult);
										
					//TODO: Die Stage ausserhalb der Schleife holen und dann übergeben.
					//resolveConflicts als Methode mit Stage anbieten:
					//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
					//CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
					
					//Wenn aber keine Exception geworfen wird, den Status direkt abfragen
					//Mache eine Schleife um diese Fehler zu beheben, statt eine verschachtelte if Struktur...						
					boolean bGoon=true; int iCount=0; boolean bAnyResolved=false;
					
					MergeStatus status = objMergeResult.getMergeStatus();
					System.out.println("Merge-Status ("+iCount+"): " + status.toString());
					if(status.equals(MergeStatus.ALREADY_UP_TO_DATE)) bGoon = false;
					
					while((status.equals(MergeStatus.CONFLICTING)
							| status.equals(MergeStatus.FAILED))
							& bGoon){
						boolean bMyAutoResolve=false;
						iCount++; 					
						
						if(status.equals(MergeStatus.CONFLICTING)) {
							 System.out.println("Konflikte erkannt ("+iCount+"). IgnoreConflicts. Strategy: " + objEnumStrategy.getName());
							
							 //Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
							 bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objMergeResult, objEnumStrategy);
							 bMyAutoResolve = true;
							 
						}//end STATUS "CONFLICTING"
						
						if(status.equals(MergeStatus.FAILED)) {
						    System.out.println("Failed erkannt ("+iCount+")");

						    bAnyResolved= JgitResolverUtilZZZ.resolveFailed(git, objMergeResult, objEnumStrategy);
						    bMyAutoResolve = true;
						}//end status FAILED 
						
					  					    
//					    Map<String, int[][]> conflicts = objMergeResult.getConflicts();
//
//					    if(conflicts != null) {
//					        for(String path : conflicts.keySet()) {
//
//					        	System.out.println(objEnumStrategy.getDescriptionShort() + ": " + path);
//
//					            // Lokale Version wiederherstellen (z.B. OURS)
//					            git.checkout()
//					            	.setStage(objStage) //z.B. OURS
//					               .addPath(path)
//					               .call();
//					        }
//					    }
//
//					    // Konfliktzustand beenden:
//					    git.add().addFilepattern(".").call();
//
//					    git.commit()
//					       .setMessage("Konflikte: Automatisch mit '" + objEnumStrategy.getName() + "' aufgelöst")
//					       .call();
//					    					
//					    bMyAutoResolve=true;
//					} else if(status.equals(MergeStatus.FAILED)) {
//						System.out.println("Failed: Erkannt.");
//						//... kann man hier das gleiche machen wie bei Konflikten? Nein ... es gibt keine Liste von Conflicts.
//						
//						//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
//					    System.out.println("Failed: Meine Strategy '" + objEnumStrategy.getName() + "' in STAGE umsetzen.");
//						CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
//											    
//					    Map<String, ResolveMerger.MergeFailureReason> faileds = objMergeResult.getFailingPaths();
//
//					    if(faileds != null) {
//					        for(String path : faileds.keySet()) {
//
//					        	System.out.println(objEnumStrategy.getDescriptionShort() + ": " + path);
//
//					            // Lokale Version wiederherstellen (z.B. OURS)
//					            git.checkout()
//					            	.setStage(objStage) //z.B. OURS
//					               .addPath(path)
//					               .call();
//					        }
//					    }
//
//					    // Konfliktzustand beenden:
//					    git.add().addFilepattern(".").call();
//
//					    git.commit()
//					       .setMessage("Failed: Automatisch mit '" + objEnumStrategy.getName() + "' aufgelöst")
//					       .call();
//					    
//					    bMyAutoResolve=true;
//					}else {
//						//mache nix
//					}

//						if(bMyAutoResolve) {
//						
//							//Prüfen ob, noch Konflikte vorhanden sind
//							Status statusGit = git.status().call();
//							objReturn.setGitStatus(statusGit);
//							
//							//Prüfen, ob der State des Repositories SAFE ist.
//							RepositoryState stateRepo = git.getRepository().getRepositoryState();
//							objReturn.setRepositoryState(stateRepo);
//						
//							objReturn.isConflictsResolved(bMyAutoResolve);
//						}
						
						if(bAnyResolved) {
						    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" oder "Fail" behaftet.
						    //Also noch ein weiteres Mal versuchen einen sauberen Result zu bekommen
							MergeResult objMergeResult02 = JgitUtilZZZ.mergeWithResult(git, sBranch, false); //also den 2ten Merge nicht debuggen
						    
						    status = objMergeResult02.getMergeStatus();
							System.out.println("Merge-Status ("+iCount+"): " + status.toString());
					    }else {
					    	bGoon=false;
					    }
					}//end while
					
					if(iCount>=1) {
						System.out.println("\nErgebnis der Konfliktbehandlung:");
						if(objEnumStrategy.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
							//Erinnerung ausgeben, das die lokalen Änderungen zwar "ueberlebt" haben, aber noch nicht im Remote sind.
							System.out.println(objEnumStrategy.getDescriptionShort() +". Die behaltenen lokalen Versionn müssen noch gepusht werden, damit sie im Remote ist.");
						}else {
							System.out.println(objEnumStrategy.getDescriptionShort());
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
		            //System.out.println("Pull PreMerge Konflikte: Pull erneut versuchen.");
		            //git.pull().call();
		            objReturn = pullIgnoreCheckoutConflictsHTTPS_by_FetchMerge_(git, credentialsProvider, sPAT, sRepoRemote, sBranch, objEnumStrategy, false);
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
	
	
	
	
	//++++++++++++++++++++++++++++++++++++++++
	public static MergeResult pullHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch) throws ExceptionZZZ {
		return pullHTTPS_by_FetchMerge_(git, credentialsProvider, sPAT, remoteUrl, branch, true, true);
	}
	
	public static MergeResult pullHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch, boolean bSuppressExceptionOnMergeFail) throws ExceptionZZZ {
		return pullHTTPS_by_FetchMerge_(git, credentialsProvider, sPAT, remoteUrl, branch, bSuppressExceptionOnMergeFail, true);
    }
	
	private static MergeResult pullHTTPS_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sPAT, String sUrlRepoRemoteIn, String sBranchIn, boolean bSuppressExceptionOnMergeFail, boolean bCheckRepositoryState) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
		        if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
		        
		        //!!! Z.B. BEIM IGNORIEREN WIRD DIESE VORBEDINGUNG NICHT WICHTIG, 
	        	//    - Sonst ist immer ein COMMIT notwendig. Ohne diesen bekommen wir beim PULL eine "CheckoutException".
	        	//      Diese können wir wir wg. "Konflikt Ignorieren" aber gezielt behandeln.	        	
	        	if(bCheckRepositoryState) {
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
	
				VARIANTE 2. URL direkt übergeben? → Nur über fetch()
	
				Wenn du eine URL direkt verwenden willst, musst du den Pull zerlegen:
				👉 pull = fetch + merge
	
				Beispiel (HTTPS mit URL + Token)
				FetchResult fetchResult = git.fetch()
				.setRemote("https://<token>@github.com/user/repo.git")
				.call();
	
				git.merge()
				.include(fetchResult.getAdvertisedRef("refs/heads/main"))
				.call();
				 */
				
	        									
				//TODOGOON20260321; // Die Variante mit sPAT in der URL hat den Nachteil, das dies irgendwo im Log etc. auftauchen koennte
				//Darum versuchen dies ohne sPAT in URL zu realisieren
				//                  //Variante A) mit sPAT in URL
				//                  https://firak01:" + sPAT + "@github.com/firak01/Projekt_Kernel02_JAZDummy.git
				//
				//                  //Variante B) ohne sPAT in URL
				//                  https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	        
		        
		        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		        if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        
		        String sBranch="master";
		        if (!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch = sBranchIn;
		
		        //Pull bei HTTPS geht nicht direkt, sondern ueber Zerlegen des pull in fetch und merge.								
				System.out.println("HTTPS-Loesung: Zerlege pull in fetch und merge");
						
		        //++++++++++++++
		        //Die URL neu auszurechnen macht Sinn, wenn z.B. eine SSH Adresse übergeben wird. Dann muss das nach HTTPS umgewandelt werden.				
		        //In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
		        String sUrlRepoRemote = JgitUtilHTTPS.computeRepositoryUrlTotalHTTPS_forFetch(sUrlRepoRemoteIn, sPAT);
		        System.out.println("Url fuer Fetch (neu ausgerechnet): '" + sUrlRepoRemote + "'");
		        
		        //Aber: Anders als beim SSH Weg, wird hier kein "RemoteAlias" verwendet,
		        //      sondern die URL direkt angegeben. Wir müssen also diesen "RemoteAlias" nicht suchen.
		       
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================		
		        //Aber wenn nichts zu fetchen ist, gibt es einen Fehler, darum
				FetchResult fetchResult = JgitUtilZZZ.fetchIgnoreNothingToFetch(git, sUrlRepoRemote, credentialsProvider, sBranch);
				if(fetchResult==null) { System.out.println("Fetch-Result: Nicht vorhanden. Abbruch!!!"); break main; }
		        GitPostFetchAnalyse.logFetchResult(fetchResult);
		
		        // =========================
		        // 2. MERGE (gezielt!)
		        // =========================
		        //Den Merge durchführen, er sollte nach erfolgreicher Vorprüfung nicht abbrechen.
			    objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);			       
		          
		}//end main:
		return objReturn;
    }
	
	
	
	public static MergeResult pullSingleBranchWithAutoResolveHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
	        try {
		        if (branch == null || branch.trim().isEmpty()) {
		            branch = "master";
		        }
		
		        Repository repo = git.getRepository();
		
		        String remoteRef = "refs/heads/" + branch;
		        String localRef = "refs/remotes/origin/" + branch;
		
		        int retry = 0;
		        int maxRetry = 2;
		
		        while (retry < maxRetry) {
		            try {
		
		                // =========================
		                // 1. FETCH (nur ein Branch!)
		                // =========================
		                FetchCommand fetch = git.fetch()
		                        .setRemote(remoteUrl)
		                        .setRefSpecs(new RefSpec(remoteRef + ":" + localRef));
		
		                if (credentialsProvider != null) {
		                    fetch.setCredentialsProvider(credentialsProvider);
		                }
		
		                fetch.call();
		
		                // =========================
		                // 2. MERGE (genau 1 Head!)
		                // =========================
		                ObjectId remoteObject = repo.resolve(localRef);
		
		                if (remoteObject == null) {
		                    throw new IllegalStateException("Remote branch not found: " + localRef);
		                }
		
		                MergeCommand merge = git.merge();
		                merge.include(remoteObject);
		                merge.setStrategy(MergeStrategy.RECURSIVE);
		
		                objReturn = merge.call();
		
		                System.out.println("Merge-Status: " + objReturn.getMergeStatus());
		
		                // =========================
		                // 3. Ergebnis prüfen
		                // =========================
		                if (objReturn.getMergeStatus().isSuccessful()) {
		                	System.out.println("Merge SUCCESSFUL.");
		                    
		                }else if (objReturn.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
		                	// normale Merge-Konflikte (nicht Checkout)
		                    System.out.println("Merge conflicts detected (content-level).");		                   
		                
		                }else if (objReturn.getMergeStatus().equals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE)) {
		                	System.out.println("Merge ALREADY UP TO DATE.");		                   
		                }else {
		                	// 	andere Fälle
		                	throw new IllegalStateException("Merge failed: " + objReturn.getMergeStatus());
		                }
		            } catch (CheckoutConflictException cce) {
		
		            	//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
						CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
						
		            	
		                System.out.println("CheckoutConflict erkannt – versuche automatische Bereinigung... Strategy: " +objEnumStrategy.getName());
		
		                Collection<String> paths = cce.getConflictingPaths();
		
		                if (paths == null || paths.isEmpty()) {
		                    throw cce;
		                }
		
		                // =========================
		                // Konfliktdateien zurücksetzen
		                // =========================
		                for (String path : paths) {
		                    System.out.println(objEnumStrategy.getDescriptionShort() + ": " + path);
		
		                    git.checkout()
		                       .addPath(path)
		                       .setStage(objStage) //z.B OURS
		                       .setForce(true)   // wichtig!
		                       .call();
		                }
		
		                retry++;
		
		                if (retry >= maxRetry) {
		                    throw new IllegalStateException("Max retries reached after CheckoutConflict", cce);
		                }
		
		                System.out.println("Retry Merge (" + retry + ")...");
		            }
		        }
 
        		//throw new IllegalStateException("Unexpected end of method");
        
	        }catch(IOException ioe) {
	        	ExceptionZZZ ez = new ExceptionZZZ(ioe);
	        	throw ez;
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

	/** Anders als bei SSH kann hier ein Pull nur durch Zerlegung in Fetch und Merge gemacht werden.
	 * @param git
	 * @param credentialsProvider
	 * @param sPAT
	 * @param sRepoRemote
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static MergeResult pullHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
			try {	
				// aber mal explizit als pullCommand
				PullCommand pullCommand = git.pull();
					
				String sUrlPartFromRepo = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sRepoRemote);
				
				//Also zerlegen des pull in fetch und merge.								
				System.out.println("HTTPS-Loesung: Zerlege pull in fetch und merge");
				
				//original url mit Token, wie beim push arbeiten
				String sUrl = "https://firak01:" + sPAT + "@" + sUrlPartFromRepo;
				System.out.println("Url fuer Fetch: '" + sUrl + "'");
				
				//Aber wenn nichts zu fetchen ist, gibt es einen Fehler
				FetchResult fetchResult = JgitUtilZZZ.fetchIgnoreNothingToFetch(git, sUrl, credentialsProvider);
				if(fetchResult==null) break main;
		        GitPostFetchAnalyse.logFetchResult(fetchResult);
		        					
				//++++++++++++++++++++++++++++++++
				//Minierklaerung: DOKU BITTE STEHEN LASSEN				
				/*
				siehe .git\config Datei, Zeile:
				fetch = +refs/heads/*:refs/remotes/origin/*

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
	 */
				
				//String sFetchRefs = "refs/heads/main";
				String sFetchRefs = "refs/heads/master";
				Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs);
					
				//++++++++++++++++++++++++++++++++				
				MergeCommand mergeCommand = git.merge();
				mergeCommand.include(objRef);
					
				objReturn = mergeCommand.call();
				System.out.println("Merge-Status:" + objReturn.getMergeStatus());//pullResult.getMergeResult());
																				
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
	
	/** Berechne die Remote Url - auch wenn eine ssh Url uebergeben worden ist - passend fuer HTTPS	 	
	 * Zum Einsatz beim FETCH
	 * @param sUrlRepoRemoteIn
	 * @param sPAT
	 * @return z.B.: https://firak01:<sPAT>@github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalHTTPS_forFetch(String sUrlRepoRemoteIn, String sPAT) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sHostIn = JgitUtilZZZ.computeRepositoryHostFromUrlRepo(sUrlRepoRemoteIn);//.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
			String sAccountFromRepo = JgitUtilZZZ.computeRepositoryAccountFromUrlRepo(sUrlRepoRemoteIn);
			String sUrlAccount = sAccountFromRepo+":" + sPAT;
			
			String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl(JgitStarterHTTPS.sPROTOCOL, sUrlAccount);
			String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
			String sUrlRepoRemote = JgitUtilHTTPS.computeRepositoryUrlTotalHTTPS(sUrlBaseWithProtocolIn, sHostIn, sAccountFromRepo, sRepositoryProjectIn);
			sReturn = sUrlRepoRemote;
		}//end main
		return sReturn;
		
		
	}
	
	/** Berechne die Remote Url - auch wenn eine ssh Url uebergeben worden ist - passend fuer HTTPS	 	
	 * Zum Einsatz beim PUSCH
	 * @param sUrlRepoRemoteIn
	 * @param sPAT
	 * @return z.B.: https://firak01:<sPAT>@github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalHTTPS_forPush(String sUrlRepoRemoteIn, String sPAT) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			sReturn = computeRepositoryUrlTotalHTTPS_forFetch(sUrlRepoRemoteIn, sPAT);
		}//end main:
		return sReturn;
	}
	
	
	
	/** Berechne die RemoteUrl - auch wenn eine ssh Url uebergeben worden ist - passend fuer HTTPS
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalHTTPS(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{			
			String sRepositoryHostIn = JgitUtilZZZ.computeRepositoryHostFromUrlRepo(sUrlRepoRemoteIn);
			
			String sRepositoryAccountIn = JgitUtilZZZ.computeRepositoryAccountFromUrlRepo(sUrlRepoRemoteIn);
			String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);			
			
			String sUrlRepoRemote = JgitUtilHTTPS.computeRepositoryUrlTotalHTTPS(sRepositoryHostIn, sRepositoryAccountIn, sRepositoryProjectIn);
			sReturn = sUrlRepoRemote;
		}//end main:
		return sReturn;		
	}
	
	//Z.B. HTTPS Version: 	https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlTotalHTTPS(String sUrlBaseHttpsWithAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlBaseHttpsWithAccountIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBaseHTTPS = sUrlBaseHttpsWithAccountIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			sReturn = sUrlBaseHTTPS + UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryProject + ".git";
		}//end main:
		return sReturn;
	}
	
	
	public static String computeRepositoryUrlTotalHTTPS(String sHostIn, String sAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlBaseHTTPS = JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sHostIn, sAccountIn);		
			sReturn = JgitUtilHTTPS.computeRepositoryUrlTotalHTTPS(sUrlBaseHTTPS, sRepositoryProjectIn);
		}//end main:
		return sReturn;
	}
	
	//Wenn die UrlBase für den Fetch übergeben wird, dann sieht das so aus:
	//Z.B.: https://firak01:<sPAT>@github.com/firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlTotalHTTPS(String sUrlBaseIn, String sHostIn, String sAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{			
			if(StringZZZ.isEmpty(sUrlBaseIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sHostIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Host", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sHostIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Host", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sAccountIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Account", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("RepositoryProject", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBaseHTTPS = sUrlBaseIn;
			String sHost = sHostIn;
			String sAccount = sAccountIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			
			sReturn = sUrlBaseHTTPS + "@" + sHost + UrlLogicZZZ.sURL_SEPARATOR_PATH  + sAccount + UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryProject + ".git";
		}//end main:
		return sReturn;
	}

	//Z.B. HTTPS Version: 	https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlPartFromUrlHTTPS(String sUrlHTTPS) throws ExceptionZZZ {
		return JgitUtilHTTPS.getUrlPartFromUrl(sUrlHTTPS);
	}

	
//	//######################################################
//	//######### FETCH	
//	//Wenn nicht zu fetchen ist, wird eine Exception geworfen. Das ist unschoen.
//	//von ChatGPT 20260320, aber für meine einfachen zwecke brauch ich kein FetchResult, also nur die ExceptionHandling uebernommen
//	public static FetchResult fetchIgnoreNothingToFetch(
//	        Git git,
//	        String sUrlRemote,
//	        CredentialsProvider credentialsProvider
//	) throws ExceptionZZZ {
//		return JgitUtilHTTPS.fetchIgnoreNothingToFetch(git, sUrlRemote, credentialsProvider, null);
//	}
//	
//	public static FetchResult fetchIgnoreNothingToFetch(
//	        Git git,
//	        String sUrlRemote,
//	        CredentialsProvider credentialsProvider,
//	        String sBranchIn
//	) throws ExceptionZZZ {
//		FetchResult objReturn = null;
//		main:{
//		    try {
//		    	try {
//					JgitUtilZZZ.debugForFetch(git);
//				} catch (URISyntaxException e) {
//					ExceptionZZZ ez = new ExceptionZZZ(e);
//					throw ez;
//				}
//		    	
//		    	
//		        // =========================
//		        // 1. FETCH (nur ein Branch!)
//		        // =========================
//		        FetchCommand fetchCommand = git.fetch();
//	
//		        if (sUrlRemote != null && sUrlRemote.trim().length() > 0) {
//		            fetchCommand.setRemote(sUrlRemote); // kann Alias ODER URL sein
//		        }
//	
//		        if (credentialsProvider != null) {
//		            fetchCommand.setCredentialsProvider(credentialsProvider);
//		        }
//		        
//
//		        //aus .git\config Datei:
//		        //      fetch = +refs/heads/*:refs/remotes/origin/*		        		       
//		        String branch = "master";
//		        if(!StringZZZ.isEmpty(sBranchIn)) branch = sBranchIn;
//		        
//		        String remoteRef = "refs/heads/" + branch;
//		        String localTrackingRef = "refs/remotes/origin/" + branch;
//		        
//		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
//		        //+ für "fast forward"
//		        fetchCommand.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));
//
//		        objReturn = fetchCommand.call();
//	
//		    } catch (TransportException te) {
//	
//		        String msg = te.getMessage();
//	
//		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
//		            System.out.println("Nothing to fetch - Repository ist aktuell.");
//		            return null; // bewusst null zurückgeben als Signal
//		        }
//	
//		        // alle anderen Fehler weiterwerfen!
//		        ExceptionZZZ ez = new ExceptionZZZ(te);
//		        throw ez;
//		    }catch(GitAPIException gae) {
//				ExceptionZZZ ez = new ExceptionZZZ(gae);
//				throw ez;
//		    } catch (IOException ioe) {
//				ExceptionZZZ ez = new ExceptionZZZ(ioe);
//				throw ez;
//			} 
//		}//end main:
//		 return objReturn;
//	}

}//end class
