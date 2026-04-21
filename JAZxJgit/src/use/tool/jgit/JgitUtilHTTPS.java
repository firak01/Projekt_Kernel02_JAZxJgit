package use.tool.jgit;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.tool.jgit.merge.GitPreMergeCheck;
import use.tool.jgit.merge.ResultPreMergeCheck;

public class JgitUtilHTTPS implements IConstantZZZ{
	public static final String sPROTOCOL_PART = "https" + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL;
	
	public static String addProtocolToUrl(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("UrlRepo", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
		
			//1. Prüfen, ob das Protokol (mit Separatoren) schon da ist.
			String sProtocolPartFound = JgitUtil.getProtocolPart(sUrlRepo);
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
			
			
			sReturn = "https" + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL  + sHost + UrlLogicZZZ.sURL_SEPARATOR_PATH + sAccount;
		}//end main:
		return sReturn;
	}
	
	
		
		
	/** Z.B.  von https://github.com/firak01
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getAccountFromUrl(String sRepositoryRemoteUrlHTTPS) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlHTTPS)) break main;
			
			sReturn = StringZZZ.right(UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryRemoteUrlHTTPS, UrlLogicZZZ.sURL_SEPARATOR_PATH);	
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
		return JgitUtil.getProjectFromUrl(sRepositoryRemoteUrlHTTPS);
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
			
			sReturn = UrlLogicZZZ.getProtocol(sRepositoryRemoteUrlHTTPS);
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
	
	
	/** Für den HTTPS Weg:
	 * Merke: Bei Pull mit HTTPS ist es notwendig den pull in fetch und merge zu zerlegen
	 * 
	 *  Eine robuste Utility-Methode, die:
	
		pull() ausführt
		CheckoutConflictException gezielt abfängt
		nur die konfliktbehafteten Dateien zurücksetzt
		danach den Pull automatisch erneut versucht
		
		s. ChatGPT 20260323
	 * @param git
	 * @throws GitAPIException
	 * @author Fritz Lindhauer, 23.03.2026, 18:17:59
	 * @throws ExceptionZZZ 
	 */
	public static MergeResult pullIgnoreCheckoutConflictsHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
	        try {
	
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
	        
				PullCommand pullCommand = git.pull();
				String sUrlPartFromRepo = JgitUtil.computeRepositoryUrlPartFromUrlRepo(sRepoRemote);
				
				System.out.println("HTTPS-Loesung: Zerlege pull in fetch und merge");
							
				String sUrl = "https://firak01:" + sPAT + "@" + sUrlPartFromRepo;
				System.out.println("Url fuer Fetch: '" + sUrl + "'");
				
				//Aber wenn nichts zu fetchen ist, gibt es einen Fehler
				FetchResult fetchResult = JgitUtilHTTPS.fetchIgnoreNothingToFetch(git, sUrl, credentialsProvider);
				if(fetchResult==null) break main;
					
				//+++ Auswerten eines Fetch
				String sFetchResultMessages = fetchResult.getMessages();
				if(sFetchResultMessages!=null) {				
					System.out.println("Fetch-Result: " + sFetchResultMessages);
				}
					
				
				//++++++++++++++++++++++++++++++++						
				String sFetchRefs = "refs/heads/master";
				Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs); //ohne das im Folgenden einzubinden, kommt die Fehlermeldung:    org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
				
				/*Minierklaerung:
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
				 */
				
					
				//+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
				System.out.println("Starte Merge:");
				try {
					//den richtigen Branch ansteuern
					String branch = "master"; // oder dynamisch

					String localRef = "refs/remotes/origin/" + branch;
					String remoteRef = "refs/heads/" + branch;
					
					//ObjectId remoteMaster = git.getRepository().resolve("refs/remotes/origin/master");
					ObjectId remoteMaster = git.getRepository().resolve(remoteRef);
					System.out.println("Verwende remoteMaster= '" + remoteMaster.getName() + "'");
					System.out.println("Verwende remoteMaster= '" + remoteMaster.toString() + "'");
					
					MergeCommand mergeCommand = git.merge();
					//geht hier nicht, da nur lokal, mergeCommand.setRemote(sUrl);
					//Also so versuchen.
					//mergeCommand.include(git.getRepository().resolve("FETCH_HEAD")); //ABER: Da hier 2 HEADs sind Fehler : org.eclipse.jgit.api.errors.InvalidMergeHeadsException: merge strategy recursive does not support 2 heads to be merged into HEAD
					//Lösungsansatz: direkt den richtigen Branch verwenden
					//mergeCommand.include(git.getRepository().resolve("refs/remotes/origin/master"));					
					mergeCommand.include(remoteMaster);
					mergeCommand.include(objRef); //ohne das kommt die Fehlermeldung:                 org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
					mergeCommand.setStrategy(MergeStrategy.RECURSIVE);
					 
					objReturn = mergeCommand.call();
					//System.out.println("Merge-Status:" + mergeResult.getMergeStatus());
																					
					//###############################################################
		        } catch (CheckoutConflictException cce) {
		
		            Collection<String> conflictingPaths = cce.getConflictingPaths();
		
		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
		                // Kein konkreter Pfad bekannt → weiterwerfen
		            	ExceptionZZZ ez = new ExceptionZZZ(cce);
		    			throw ez;
		            }
	
		            //Konfliktdateien gezielt zurücksetzen
		            for (String path : conflictingPaths) {
		                git.checkout()
		                   .addPath(path)
		                   .call();
		            }
		
		            //Pull erneut versuchen
		            git.pull().call();
		        }
								
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
	
	
	//++++++++++++++++++++++++++++++++++++++++
	public static MergeResult pullSingleBranchHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch) throws ExceptionZZZ {
		return pullSingleBranchHTTPS_(git, credentialsProvider, sPAT, remoteUrl, branch, true);
	}
	
	public static MergeResult pullSingleBranchHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch, boolean bSuppressExceptionOnMergeFail) throws ExceptionZZZ {
		return pullSingleBranchHTTPS_(git, credentialsProvider, sPAT, remoteUrl, branch, bSuppressExceptionOnMergeFail);
    }
	
	private static MergeResult pullSingleBranchHTTPS_(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch, boolean bSuppressExceptionOnMergeFail) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
	        try {
	
		        if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
		        if (remoteUrl == null || remoteUrl.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        if (branch == null || branch.trim().isEmpty()) {
		            branch = "master"; // Default für Java 1.7 Projekte 😉
		        }
		
		        Repository repo = git.getRepository();
		
		        String remoteRef = "refs/heads/" + branch;
		        String localTrackingRef = "refs/remotes/origin/" + branch;
		
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();
		        fetchCommand.setRemote(remoteUrl);
		        fetchCommand.setRefSpecs(new RefSpec(remoteRef + ":" + localTrackingRef));
		
		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		
		        fetchCommand.call();
		
		        // =========================
		        // 2. MERGE (gezielt!)
		        // =========================
		        ObjectId remoteBranchObjectId = repo.resolve(localTrackingRef);
		
		        if (remoteBranchObjectId == null) {
		            throw new IllegalStateException("Remote branch not found after fetch: " + localTrackingRef);
		        }
		
		        
		        //!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
		        //             ohne eine Exception durchlaufen kann
//		        Status status = git.status().call();
//
//		        if (!status.isClean()) {
//		            System.out.println("Working directory not clean!");
//		            if(status.hasUncommittedChanges()) {
//		            	System.out.println("Has uncommited changes:");
//		            	Set<String> setUncommittedChanges = status.getUncommittedChanges();
//		            	for(String sUncommittedChange : setUncommittedChanges) {
//		            		System.out.println("- " + sUncommittedChange);
//		            	}
//		            }
//		        }
		        
		        //20260421: 2a) Vorprüfung per eigener, gekapselter Routine
		        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
		        if (!check.isClean()) {
		            check.printReport();
		            break main; // Merge abbrechen
		        }
		        
		        //2b) Den Merge durchführen, er sollte nach erfolgreicher Vorprüfung nicht abbrechen.
		        MergeCommand mergeCommand = git.merge();
		        mergeCommand.include(remoteBranchObjectId);
		        mergeCommand.setStrategy(MergeStrategy.RECURSIVE);
		
		        objReturn = mergeCommand.call();
		
		        // =========================
		        // 3. Ergebnis prüfen
		        // =========================
		        if (!objReturn.getMergeStatus().isSuccessful()) {
		
		            switch (objReturn.getMergeStatus()) {
		
		                case CONFLICTING:
		                    System.out.println("Merge conflicts detected!");		                    
		                    break;
		
		                case FAILED:
		                	System.out.println("Merge conflicts detected!");	
		                    if(!bSuppressExceptionOnMergeFail) throw new IllegalStateException("Merge failed: " + objReturn.toString());
		                    break;
		
		                case ALREADY_UP_TO_DATE:
		                    System.out.println("Already up-to-date.");
		                    break;
		
		                default:
		                    System.out.println("Merge status: " + objReturn.getMergeStatus());
		            }
		        }

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
					
				String sUrlPartFromRepo = JgitUtil.computeRepositoryUrlPartFromUrlRepo(sRepoRemote);
				
				//Also zerlegen des pull in fetch und merge.								
				System.out.println("HTTPS-Loesung: Zerlege pull in fetch und merge");
				
				//original url mit Token, wie beim push arbeiten
				String sUrl = "https://firak01:" + sPAT + "@" + sUrlPartFromRepo;
				System.out.println("Url fuer Fetch: '" + sUrl + "'");
				
				//Aber wenn nichts zu fetchen ist, gibt es einen Fehler
				FetchResult fetchResult = JgitUtilHTTPS.fetchIgnoreNothingToFetch(git, sUrl, credentialsProvider);
				if(fetchResult==null) break main;
					
				String sFetchResultMessages = fetchResult.getMessages();
				if(sFetchResultMessages!=null) {				
					System.out.println("Fetch-Result: " + sFetchResultMessages);
				}
					
				//++++++++++++++++++++++++++++++++
				//siehe .git\config Datei, Zeile:
				//fetch = +refs/heads/*:refs/remotes/origin/*
				//Minierklaerung: 
		/*
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
	
	//Z.B. HTTPS Version: 	https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlHTTPS(String sUrlBaseHTTPSin, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlBaseHTTPSin)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBaseHTTPS = sUrlBaseHTTPSin;
			String sRepositoryProject = sRepositoryProjectIn;
			
			sReturn = sUrlBaseHTTPS + UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryProject + ".git";
		}//end main:
		return sReturn;
	}
	
	
	public static String computeRepositoryUrlHTTPS(String sHostIn, String sAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlBaseHTTPS = JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sHostIn, sAccountIn);		
			sReturn = JgitUtilHTTPS.computeRepositoryUrlHTTPS(sUrlBaseHTTPS, sRepositoryProjectIn);
		}//end main:
		return sReturn;
	}

	//Z.B. HTTPS Version: 	https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlPartFromUrlHTTPS(String sUrlHTTPS) throws ExceptionZZZ {
		return JgitUtilHTTPS.getUrlPartFromUrl(sUrlHTTPS);
	}

	//######################
	//Wenn nicht zu fetchen ist, wird eine Exception geworfen. Das ist unschoen.
	//von ChatGPT 20260320, aber für meine einfachen zwecke brauch ich kein FetchResult, also nur die ExceptionHandling uebernommen
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,
	        String sUrlRemote,
	        CredentialsProvider credentialsProvider
	) throws ExceptionZZZ {
		FetchResult objReturn = null;
		main:{
		    try {
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();
	
		        if (sUrlRemote != null && sUrlRemote.trim().length() > 0) {
		            fetchCommand.setRemote(sUrlRemote); // kann Alias ODER URL sein
		        }
	
		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		        
		       
		       
		       
		        
		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*
		        String branch = "master";
		        String remoteRef = "refs/heads/" + branch;
		        String localTrackingRef = "refs/remotes/origin/" + branch;
		        
		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		        //+ für "fast forward"
		        fetchCommand.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));

		        objReturn = fetchCommand.call();
	
		        
		        
		        // Optional: Logging / Prüfung
		        if (objReturn.getTrackingRefUpdates().isEmpty()) {
		            System.out.println("Fetch erfolgreich, aber keine Änderungen vorhanden.");
		        } else {
		            System.out.println("Fetch erfolgreich, Änderungen empfangen.");
		        }
	
		    } catch (TransportException te) {
	
		        String msg = te.getMessage();
	
		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
		            System.out.println("Nothing to fetch - Repository ist aktuell.");
		            return null; // bewusst null zurückgeben als Signal
		        }
	
		        // alle anderen Fehler weiterwerfen!
		        ExceptionZZZ ez = new ExceptionZZZ(te);
		        throw ez;
		    }catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			} 
		}//end main:
		 return objReturn;
	}
	
	public static MergeResult pullSingleBranchWithAutoResolveHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch) throws ExceptionZZZ {
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
		
		                System.out.println("CheckoutConflict erkannt – versuche automatische Bereinigung...");
		
		                Collection<String> paths = cce.getConflictingPaths();
		
		                if (paths == null || paths.isEmpty()) {
		                    throw cce;
		                }
		
		                // =========================
		                // Konfliktdateien zurücksetzen
		                // =========================
		                for (String path : paths) {
		                    System.out.println("Bereinige Datei: " + path);
		
		                    git.checkout()
		                       .addPath(path)
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

}//end class
