package use.jgit.util;

import java.io.File;
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
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ResolveMerger.MergeFailureReason;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.jgit.resolve.EnumSetMappedStrategyMergeConflictUtilZZZ;
import use.jgit.resolve.IJgitResolverEnabled;
import use.jgit.resolve.JgitResolverUtilZZZ;
import use.jgit.tool.fetch.GitPostFetchAnalyse;
import use.jgit.tool.merge.GitPreMergeCheck;
import use.jgit.tool.merge.ResultPreMergeCheck;

public class JgitUtilSSH implements IConstantZZZ{
	public static final String sPROTOCOL_PART = "git@";
	
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
				sReturn = JgitUtilSSH.sPROTOCOL_PART + sUrlRepo;
			}else {
				//das gefundene Protokol entfernen
				String sUrlRepoWithoutProtocol = StringZZZ.stripLeft(sUrlRepo, sProtocolPartFound);
				sReturn = JgitUtilSSH.sPROTOCOL_PART + sUrlRepoWithoutProtocol;
			}
		}//end main:
		return sReturn;
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryProtocolFromUrlSSH(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilSSH.getProtocolFromUrl(sUrlRepo);
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryAccountFromUrlSSH(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilSSH.getAccountFromUrl(sUrlRepo);
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryHostFromUrlSSH(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilSSH.getHostFromUrl(sUrlRepo);
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
		public static String computeRepositoryProjectFromUrlSSH(String sUrlRepo) throws ExceptionZZZ{
			return JgitUtilSSH.getProjectFromUrl(sUrlRepo);
		}
	
	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
		public static String computeRepositoryUrlBaseFromUrlSSH(String sUrlRepo) throws ExceptionZZZ{
			String sReturn = null;
			main:{
				if(StringZZZ.isEmpty(sUrlRepo)){
					ExceptionZZZ ez = new ExceptionZZZ("Url des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
									
				String sHost = JgitUtilSSH.computeRepositoryHostFromUrlSSH(sUrlRepo);			
				String sAccount = JgitUtilSSH.computeRepositoryAccountFromUrlSSH(sUrlRepo);				
				sReturn = JgitUtilSSH.computeRepositoryUrlBaseSSH(sHost, sAccount);
			}//end main:
			return sReturn;
		}
		
	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryUrlBaseSSH(String sHostIn, String sAccountIn) throws ExceptionZZZ{
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
			
			sReturn = "git@" + sHost + ":" + sAccount;
		}//end main:
		return sReturn;
	}
	
	/** Berechne dir RemoteUrl - auch wenn eine https Url uebergeben worden ist - passend fuer SSH
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlSSH(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlBaseIn = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
			String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl("git", sUrlBaseIn);
			String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
			String sUrlRepoRemote = JgitUtilZZZ.computeRepositoryUrl(sUrlBaseWithProtocolIn, sRepositoryProjectIn);
			sReturn = sUrlRepoRemote;
		}//end main:
		return sReturn;		
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlSSH(String sUrlBaseSSHin, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlBaseSSHin)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBaseSSH = sUrlBaseSSHin;
			String sRepositoryProject = sRepositoryProjectIn;
			
			sReturn = sUrlBaseSSH + UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryProject + ".git";
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlSSH(String sHostIn, String sAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlBaseSSH = JgitUtilSSH.computeRepositoryUrlBaseSSH(sHostIn, sAccountIn);		
			sReturn = JgitUtilSSH.computeRepositoryUrlSSH(sUrlBaseSSH, sRepositoryProjectIn);
		}//end main:
		return sReturn;
	}
	
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,	        
	        CredentialsProvider credentialsProvider,
	        String sBranchIn
	) throws ExceptionZZZ {
		FetchResult objReturn = null;
		main:{
		    try {
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();

		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		        
		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*		        		       
		        String branch = "master";
		        if(!StringZZZ.isEmpty(sBranchIn)) branch = sBranchIn;
		        
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
	
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,	        
	        CredentialsProvider credentialsProvider,
	        String sRemoteRepositoryAlias,
	        String sBranchIn
	) throws ExceptionZZZ {
		FetchResult objReturn = null;
		main:{
		    try {
		    	System.out.println("### DEGUB START");
		    	Repository repository = git.getRepository();
		    	String branch = repository.getBranch();
		    	BranchConfig config = new BranchConfig(repository.getConfig(), branch);

		    	System.out.println(config.getRemote());
		    	System.out.println(config.getMerge());
		    	System.out.println("### DEBUG ENDE");
		    	
		    	
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();

		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		        
		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*		        		       
		        String sBranch = "master";
		        if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
		        
		        String remoteRef = "refs/heads/" + sBranch;
		        String localTrackingRef = "refs/remotes/origin/" + sBranch;
		        
		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		        //+ für "fast forward"
		        fetchCommand.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));

		        fetchCommand.setRemote(sRemoteRepositoryAlias);
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
		            System.out.println("Nothing to fetch - Lokales Repository ist aktuell bzg. Remore Repository.");
		            return null; // bewusst null zurückgeben als Signal
		        }
	
		        // alle anderen Fehler weiterwerfen!
		        ExceptionZZZ ez = new ExceptionZZZ(te);
		        throw ez;
		    }catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			} 
		}//end main:
		 return objReturn;
	}
	
	
	/** Z.B.  von git@github.com:firak01
	 *       oder git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getAccountFromUrl(String sRepositoryRemoteUrlSSH) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlSSH)) break main;
			
			//klappt vielleicht nicht immer... sReturn = StringZZZ.right(":"+ sRepositoryRemoteUrlSSH, ":");
			
			//Neben dem Host steht der Account
			String sHost = JgitUtilSSH.getHostFromUrl(sRepositoryRemoteUrlSSH);	
		
			//sReturn = StringZZZ.mid(sRepositoryRemoteUrlSSH+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
			//sReturn = StringZZZ.midLeftRight(sRepositoryRemoteUrlSSH+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = StringZZZ.midRightLeft(sRepositoryRemoteUrlSSH+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
		}//end main:
		return sReturn;
	}
	
	/** Z.B.  von git@github.com:firak01
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getHostFromUrl(String sRepositoryRemoteUrlSSH) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlSSH)) break main;
			
			sReturn = StringZZZ.mid(sRepositoryRemoteUrlSSH, "@", ":");
		}//end main:
		return sReturn;
	}
	
	/** Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git 
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProjectFromUrl(String sRepositoryRemoteUrlSSH) throws ExceptionZZZ{
		return JgitUtilZZZ.getProjectFromUrl(sRepositoryRemoteUrlSSH);
	}
	
	/** Z.B.  von git@github.com:firak01
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocolFromUrl(String sRepositoryRemoteUrlSSH) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlSSH)) break main;
			
			sReturn = StringZZZ.left(sRepositoryRemoteUrlSSH+"@", "@");
		}//end main:
		return sReturn;
	}
	
	
	/** Z.B.  von Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getUrlPartFromUrl(String sRepositoryRemoteUrlSSH) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlSSH)) break main;
			
			String sUrlSSHWithoutProtocol = StringZZZ.right("@" + sRepositoryRemoteUrlSSH, "@");
			String sUrlSSHWithoutProtocolAndProject = StringZZZ.left(sUrlSSHWithoutProtocol + UrlLogicZZZ.sURL_SEPARATOR_PATH,UrlLogicZZZ.sURL_SEPARATOR_PATH );
			sReturn = sUrlSSHWithoutProtocolAndProject;			
		}//end main:
		return sReturn;
	}
	
	//######################################################
	//###  PULL
	
	/** Anders als bei HTTPS kann hier ein Pull direkt gemacht werden, also ohne Zerlegung in Fetch und Merge.
	 * 
	 * ABER: Achtung sUrlRepoRemote ist eine Url. Aber eine Url darf beim SSH Weg nicht direkt 
	 *       beim PullCommand.setRemote(s) für s verwendet werden. Das geht nur beim HTTPS Weg.
	 *        
	 * ERGO: Wir suchen anhand der übergebenen URL den (zuvor konfigurierten) Eintrag und nehmen den "Alias".        
	 * 
	 * ABER2: Beim Auflösen eines Konflikts müsste man erneut einen MERGE machen. 
  	 *        Da intern der PULL eh die Verwendung von FETCH und MERGE ist, wäre das ein unnoetiger doppelter MERGE Schritt.
  	 *        Darum eher pullSSH_by_FetchMerge verwenden.
  	 *        
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static MergeResult pullSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn) throws ExceptionZZZ {
		return JgitUtilSSH.pullSSH_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, null);
	
	

//		MergeResult objReturn = null;
//		main:{
//			try {	
//				
//				if (git == null) {
//		            throw new IllegalArgumentException("git must not be null");
//		        }
//				
//				//!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
//		        //             ohne eine Exception durchlaufen kann
//		        //Vorprüfung per eigener, gekapselter Routine
//		        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
//		        if (!check.isClean()) {
//		            check.printReport();
//		            break main; // Merge abbrechen
//		        }
//		        
//		        //+++++++++++++++++++++++++
//		        //wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
//				System.out.println("SSH-Loesung: Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
//				
//				
//				// aber mal explizit als pullCommand
//				PullCommand pullCommand = git.pull();
//			
//				//In der Utility - Klasse das so machen wie in HTTPS und die Url berechnen:
//				
//				//Das neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach SSH umgewandelt werden.				
//				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
//				String sUrlBaseIn = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
//				String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl("git", sUrlBaseIn);
//				String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
//				String sUrlRepoRemote = JgitUtilZZZ.computeRepositoryUrl(sUrlBaseWithProtocolIn, sRepositoryProjectIn);
//				//pullCommand.setRemote(sUrlRepoRemote); //Aber: Anders als beim HTTPS Weg, darf die URL nicht direkt übergeben werden.
//				                                         //      Statt dessen den "Aliasnamen" übergeben.
//				System.out.println("Verwendete, neu ausgerechnete Url für Remote: " + sUrlRepoRemote);
//				
//				//Da wir den Aliasnamen übergeben müssen, aber eine Url reinbekommen.
//				//Müssen wir aus der Url den Aliasnamen errechnen.
//				//denn hier in der static Methode geht ja leider nicht: this.getRepositoryRemoteAlias(); 
//				
//				String sRemoteRepositoryAlias = JgitUtilZZZ.findRemoteNameByUrl(git, sUrlRepoRemote);
//				System.out.println("Verwendete RepositoryAlias für Remote: " + sRemoteRepositoryAlias);
//				pullCommand.setRemote(sRemoteRepositoryAlias);
//
//				// pull from remote, hier mit Auswertung des Ergebnisses	
//				PullResult pullResult = pullCommand.call();
//				
//				if (pullResult.isSuccessful()) {
//				    System.out.println("Pull erfolgreich");
//				} else {
//				    System.out.println("Pull fehlgeschlagen");
//				}
//
//				objReturn = pullResult.getMergeResult();
//				if(objReturn!=null) {
//					System.out.println("MergeResult: " + objReturn.getMergeStatus());
//				}else {
//					System.out.println("MergeResult: Kein Status zurueckgegeben.");
//				}
//				
//				//20260428 wofuer braucht es den fetchResult
////				FetchResult fetchResult = pullResult.getFetchResult();
////				if(fetchResult!=null) {
////					System.out.println("FetchResult: " + fetchResult.getMessages());
////				}else {
////					System.out.println("FetchResult: Keine Meldung zurueckgegeben.");
////				}																				
//				//###############################################################		
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
//		}//end main:
//		return objReturn;
				
	}
	
	/** Anders als bei HTTPS kann hier ein Pull direkt gemacht werden, also ohne Zerlegung in Fetch und Merge.
	 * 
	 * ABER: Achtung sUrlRepoRemote ist eine Url. Aber eine Url darf beim SSH Weg nicht direkt 
	 *       beim PullCommand.setRemote(s) für s verwendet werden. Das geht nur beim HTTPS Weg.
	 *        
	 * ERGO: Wir suchen anhand der übergebenen URL den (zuvor konfigurierten) Eintrag und nehmen den "Alias".        
	 * 
  	 * ABER2: Beim Auflösen eines Konflikts müsste man erneut einen MERGE machen. 
  	 *        Da intern der PULL eh die Verwendung von FETCH und MERGE ist, wäre das ein unnoetiger doppelter MERGE Schritt.
  	 *        Darum eher pullSSH_by_FetchMerge verwenden.
  	 *        
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static MergeResult pullSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn) throws ExceptionZZZ {
		//return JgitUtilSSH.pullSSH_by_PullDirect_(git, credentialsProvider, sUrlRepoRemoteIn);
		
		
		return JgitUtilSSH.pullSSH_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn);
	}
	
	/** Anders als bei HTTPS kann hier ein Pull direkt gemacht werden, also ohne Zerlegung in Fetch und Merge.
	 * 
	 * ABER: Achtung sUrlRepoRemote ist eine Url. Aber eine Url darf beim SSH Weg nicht direkt 
	 *       beim PullCommand.setRemote(s) für s verwendet werden. Das geht nur beim HTTPS Weg.
	 *        
	 * ERGO: Wir suchen anhand der übergebenen URL den (zuvor konfigurierten) Eintrag und nehmen den "Alias".        
	 * 
	 * ABER2: Beim Auflösen eines Konflikts müsste man erneut einen MERGE machen. 
	 *        Da intern der PULL eh die Verwendung von FETCH und MERGE ist, wäre das ein unnoetiger doppelter MERGE Schritt.
	 *        Darum eher pullSSH_by_FetchMerge_ verwenden.
	 * 
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static MergeResult pullSSH_by_PullDirect_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
			try {	
				
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				//!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
		        //             ohne eine Exception durchlaufen kann
		        //Vorprüfung per eigener, gekapselter Routine
		        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
		        if (!check.isClean()) {
		            check.printReport();
		            break main; // Merge abbrechen
		        }
		        
		        //+++++++++++++++++++++++++
		        //wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("SSH-Loesung: Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
				
				
				// aber mal explizit als pullCommand
				PullCommand pullCommand = git.pull();
			
				//In der Utility - Klasse das so machen wie in HTTPS und die Url berechnen:
				
				//Das neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach SSH umgewandelt werden.				
				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
				String sUrlBaseIn = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl("git", sUrlBaseIn);
				String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlRepoRemote = JgitUtilZZZ.computeRepositoryUrl(sUrlBaseWithProtocolIn, sRepositoryProjectIn);
				//pullCommand.setRemote(sUrlRepoRemote); //Aber: Anders als beim HTTPS Weg, darf die URL nicht direkt übergeben werden.
				                                         //      Statt dessen den "Aliasnamen" übergeben.
				System.out.println("Verwendete, neu ausgerechnete Url für Remote: " + sUrlRepoRemote);
				
				//Da wir den Aliasnamen übergeben müssen, aber eine Url reinbekommen.
				//Müssen wir aus der Url den Aliasnamen errechnen.
				//denn hier in der static Methode geht ja leider nicht: this.getRepositoryRemoteAlias(); 
				
				String sRemoteRepositoryAlias = JgitUtilZZZ.findRemoteNameByUrl(git, sUrlRepoRemote);
				System.out.println("Verwendete RepositoryAlias für Remote: " + sRemoteRepositoryAlias);
				pullCommand.setRemote(sRemoteRepositoryAlias);

				// pull from remote, hier mit Auswertung des Ergebnisses	
				PullResult pullResult = pullCommand.call();
				
				if (pullResult.isSuccessful()) {
				    System.out.println("Pull erfolgreich");
				} else {
				    System.out.println("Pull fehlgeschlagen");
				}

				objReturn = pullResult.getMergeResult();
				if(objReturn!=null) {
					System.out.println("MergeResult: " + objReturn.getMergeStatus());
				}else {
					System.out.println("MergeResult: Kein Status zurueckgegeben.");
				}
				
				//20260428 wofuer braucht es den fetchResult
//				FetchResult fetchResult = pullResult.getFetchResult();
//				if(fetchResult!=null) {
//					System.out.println("FetchResult: " + fetchResult.getMessages());
//				}else {
//					System.out.println("FetchResult: Keine Meldung zurueckgegeben.");
//				}																				
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
	 *  
	 * @param git
	 * @param credentialsProvider
	 * @param remoteUrl
	 * @param branch
	 * @param bSuppressExceptionOnMergeFail
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static MergeResult pullSSH_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{	      
		        if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
		        
		        //!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
		        //             ohne eine Exception durchlaufen kann
		        //Vorprüfung per eigener, gekapselter Routine
		        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
		        if (!check.isClean()) {
		            check.printReport();
		            break main; // Merge abbrechen
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
	
				*/
		        
		        
		        //Pull ist eh eine Anwendung von fetch und merge. Wenn ich das mache spart es einen Merge am schluss.							
				System.out.println("SSH-Loesung: Spare einen MERGE. Zerlege deshalb PULL in FETCH und MERGE");
				
		        
		        Repository repo = git.getRepository();
		
		        //++++++++++++
		        //Das neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach SSH umgewandelt werden.				
				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
		        String sUrlRepoRemote = JgitUtilSSH.computeRepositoryUrlSSH(sUrlRepoRemoteIn);
		        
		        //Aber: Anders als beim HTTPS Weg, darf die URL nicht direkt übergeben werden.
		        //      Statt dessen den "Aliasnamen" übergeben.
				//pullCommand.setRemote(sUrlRepoRemote);
				                                         
				//Da wir den Aliasnamen übergeben müssen, aber eine Url reinbekommen.
				//Müssen wir aus der Url den Aliasnamen errechnen.
				//denn hier in der static Methode geht ja leider nicht: this.getRepositoryRemoteAlias(); 
		        System.out.println("Verwendete, neu ausgerechnete Url für Remote: " + sUrlRepoRemote);
				
				String sRemoteRepositoryAlias = JgitUtilZZZ.findRemoteNameByUrl(git, sUrlRepoRemote);
				System.out.println("Verwendete RepositoryAlias für Remote: '" + sRemoteRepositoryAlias + "'");
				
		        //++++++++++++
		        
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // ========================= 
		        //Aber wenn nichts zu fetchen ist, gibt es einen Fehler, darum
				FetchResult fetchResult = JgitUtilSSH.fetchIgnoreNothingToFetch(git, credentialsProvider, sRemoteRepositoryAlias, sBranchIn);
				if(fetchResult==null) break main;
		        GitPostFetchAnalyse.logFetchResult(fetchResult);
		
		        // =========================
		        // 2. MERGE (gezielt!)
		        // =========================
		      
		        //Den Merge durchführen, er sollte nach erfolgreicher Vorprüfung nicht abbrechen.
		       objReturn = JgitUtilZZZ.mergeWithResult(git, sBranchIn);			       
	        
		}//end main:
		return objReturn;
    }

	
	
	/** Für den SSH Weg:
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
	public static boolean pullIgnoreCheckoutConflictsSSH(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
	
		        try {
		            git.pull().call();
		        } catch (CheckoutConflictException e) {
		
		            Collection<String> conflictingPaths = e.getConflictingPaths();
		
		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
		                // Kein konkreter Pfad bekannt → weiterwerfen
		                throw e;
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
	
	/** Für den SSH Weg:
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
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static MergeResult pullIgnoreCheckoutConflictsSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ {
		//return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_PullDirect_(git,credentialsProvider, sUrlRepoRemoteIn, objEnumstrategy);
		
		return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_FetchMerge_(git,credentialsProvider, sUrlRepoRemoteIn, null, objEnumStrategyMergeConflict);
	}
	
	/** Für den SSH Weg:
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
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static MergeResult pullIgnoreCheckoutConflictsSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy) throws ExceptionZZZ {
		//return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_PullDirect_(git,credentialsProvider, sUrlRepoRemoteIn, objEnumstrategy);
		
		return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_FetchMerge_(git,credentialsProvider, sUrlRepoRemoteIn, sBranchIn, objEnumstrategy);
	}
	
	
	/** Für den SSH Weg:
	 *  Merke: Bei Pull mit HTTPS ist es notwendig den pull in fetch und merge zu zerlegen
	 *         Hier ist ein fetch nicht notwendig.
	 * 
	 *  Eine robuste Utility-Methode, die:
	
		pull() ausführt
		CheckoutConflictException gezielt abfängt und danach den Pull automatisch erneut versucht
		oder
		den Merge-Status analysiert... FAILED ... CONFLICTING bearbeitet		
		nur die konfliktbehafteten Dateien zurücksetzt: OURS bleibt erhalten
		
	 * ABER2: Beim Auflösen eines Konflikts müsste man erneut einen MERGE machen. 
	 *        Da intern der PULL eh die Verwendung von FETCH und MERGE ist, wäre das ein unnoetiger doppelter MERGE Schritt.
	 *        Darum eher pullIgnoreCheckoutConflictsSSH_by_FetchMerge_ verwenden.
		
		s. ChatGPT 20260323, 20260508ff
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static MergeResult pullIgnoreCheckoutConflictsSSH_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{	        
        	 String sBranch = "master";
		     if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
		       
        	
        	//Mache hier den Pull durch einen FETCH gefolgt von einem MERGE
        	objReturn = pullSSH_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch);
        	
        	
        	//Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
			if(objReturn==null) {					
				System.out.println("MergeResult: Null.");
				break main;
			}

			//TODOGOON20260514: Momentan wird noch jedes mal die Stage geholt
			//                  Also hier holen und resolveConflicts als Methode mit Stage anbieten:
			//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
			CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
			
			//Wenn aber keine Exception geworfen wird, den Status direkt abfragen
			//Mache eine Schleife um diese Fehler zu beheben, statt eine verschachtelte if Struktur...						
			boolean bGoon=true; int iCount=0; boolean bAnyResolved=false;
			
			MergeStatus status = objReturn.getMergeStatus();
			System.out.println("Merge-Status ("+iCount+"): " + status.toString());
			if(status.equals(MergeStatus.ALREADY_UP_TO_DATE)) bGoon = false;
			
			while((status.equals(MergeStatus.CONFLICTING)
					| status.equals(MergeStatus.FAILED))
					& bGoon){
				
				iCount++; bAnyResolved=false;
				if(status.equals(MergeStatus.CONFLICTING)) {
				    System.out.println("Konflikte erkannt ("+iCount+")");
				    
				    //Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
				    bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objReturn, objEnumStrategy);					    
				}//end STATUS "CONFLICTING"
			
			
				if(status.equals(MergeStatus.FAILED)) {
				    System.out.println("Failed erkannt ("+iCount+")");

				    bAnyResolved= JgitResolverUtilZZZ.resolveFailed(git, objReturn);
//					    if(bAnyResolved) {
//					    	//Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
//					    	//Also noch ein weiteres Mal:	
//					    	objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
//						    
//							MergeStatus status2 = objReturn.getMergeStatus();
//							System.out.println("Merge-Status2:" + status2.toString());
//							if(status2.equals(MergeStatus.CONFLICTING)) {
//							    System.out.println("Konflikte2 erkannt.");
//							    
//							    boolean bAnyResolved3 = JgitResolverUtilZZZ.resolveConflicts(git, objReturn, objEnumStrategy);
//							    if(bAnyResolved3) {
//								    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
//								    //Also noch ein weiteres Mal:
//								   objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
//							    }
//							}
//					    }
				}//end status FAILED 
				
				if(bAnyResolved) {
				    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" oder "Fail" behaftet.
				    //Also noch ein weiteres Mal versuchen einen sauberen Result zu bekommen
				    objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
				    
				    status = objReturn.getMergeStatus();
					System.out.println("Merge-Status ("+iCount+"): " + status.toString());
			    }else {
			    	bGoon=false;
			    }
			}//end while
			
											
			//###############################################################
//	        } catch (CheckoutConflictException cce) {
//	        	System.out.println("Konflikte: CheckoutConflictException...");
//	        	
//		        	System.out.println("Konflikte: CheckoutConflictException... Meine gewaehlte Konfliktstrategie 'ignorieren'");
//		            Collection<String> conflictingPaths = cce.getConflictingPaths();
//		
//		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
//		                // Kein konkreter Pfad bekannt → weiterwerfen
//		            	ExceptionZZZ ez = new ExceptionZZZ(cce);
//		    			throw ez;
//		            }
//	
//		            //Konfliktdateien gezielt zurücksetzen
//		            System.out.println("Konflikte: Setze Pfade gezielt zurueck:");		        	
//		            for (String path : conflictingPaths) {
//		                git.checkout()
//		                   .addPath(path)
//		                   .call();
//		                System.out.println("* " + path);			        	
//		            }
//		
//		            //Pull erneut versuchen
//		            System.out.println("Konflikte: Pull erneut versuchen.");
//		            git.pull().call();
//		            objReturn = pullResult.getMergeResult();
//		           
//		            //Das wäre der Ansatz ohne diese Exception
//		            /*
//		            // Konfliktzustand beenden durch "Markieren der Konfliktauflösung":
//				    git.add().addFilepattern(".").call();
//
//				    git.commit()
//				       .setMessage("Konflikte2 automatisch mit OURS aufgelöst")
//				       .call();
//				    			
//				    
//				    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
//				    //Also noch ein 3. Mal:
//				    MergeCommand mergeCommand = git.merge();
//	                mergeCommand.include(objRef);
//	                mergeCommand.setStrategy(MergeStrategy.RECURSIVE);									 
//					objReturn = mergeCommand.call();
//				    */
		            
		       
						
				//###############################################################	
//	        }catch(IOException ioe) {
//	        	ExceptionZZZ ez = new ExceptionZZZ(ioe);
//	        	throw ez;	
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
		}//end main:
		return objReturn;
	}
	
	private static MergeResult pullIgnoreCheckoutConflictsSSH_by_FetchMerge_BACKUP_GGFS_LOESCHEN_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
//	        try {
	        	 String sBranch = "master";
			     if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
			       
	        	
	        	//Mache hier den Pull durch einen FETCH gefolgt von einem MERGE
	        	objReturn = pullSSH_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch);
	        	
	        	
	        	//Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
				if(objReturn!=null) {
					System.out.println("MergeResult: " + objReturn.getMergeStatus());
				}else {
					System.out.println("MergeResult: Null.");
					break main;
				}
				
			
				
////				//++++++++++++++++++++++++++++++++
////				//den richtigen Branch ansteuern
//				String branch = "master"; // oder dynamisch
//				String sFetchRefs = "refs/heads/" + branch;
//				Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs); //ohne das im Folgenden einzubinden, kommt die Fehlermeldung:    org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
//				System.out.println("Merge Ref = " + objRef.getName());
//				System.out.println("ObjectId  = " + objRef.getObjectId().getName());
//				
//				/*Minierklaerung:
//				siehe .git\config Datei, entsprechende Zeile.
//				 
//				Das ist ein sogenannter RefSpec (Reference Specification).
//				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
//				
//				Aufbau allgemein:
//				[+]<Quelle>:<Ziel>
//				
//				Also:
//				Quelle (Remote-Seite)
//				refs/heads/ = alle Branches im Remote-Repository
//				 * = Wildcard → alle Branch-Namen
//	
//				➡️ Bedeutet:
//				Hole alle Branches vom Remote
//				
//				
//				Ziel (lokal)
//				refs/remotes/origin/ = Remote-Tracking-Branches
//				* = gleicher Name wie Quelle
//	
//				➡️ Bedeutet:
//				Speichere sie lokal als origin/branchname
//				
//				------------
//				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
//				Mit + sagst du:
//				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
//				 */
//									
				
				//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
				CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);				
				
				//Wenn aber keine Exception geworfen wird, den Status direkt abfragen
				MergeStatus status = objReturn.getMergeStatus();
				System.out.println("Merge-Status:" + status.toString());
				if(status.equals(MergeStatus.CONFLICTING)) {
				    System.out.println("Konflikte erkannt.");
				    
				    //Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
				    boolean bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objReturn, objEnumStrategy);
				    if(bAnyResolved) {
					    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
					    //Also noch ein weiteres Mal:
					    objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
				    }
				}//end STATUS "CONFLICTING"
				
				if(status.equals(MergeStatus.FAILED)) {
				    System.out.println("Failed erkannt.");

				    boolean bAnyResolved2= JgitResolverUtilZZZ.resolveFailed(git, objReturn);
				    if(bAnyResolved2) {
				    	//Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
				    	//Also noch ein weiteres Mal:	
				    	objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
					    
						MergeStatus status2 = objReturn.getMergeStatus();
						System.out.println("Merge-Status2:" + status2.toString());
						if(status2.equals(MergeStatus.CONFLICTING)) {
						    System.out.println("Konflikte2 erkannt.");
						    
						    boolean bAnyResolved3 = JgitResolverUtilZZZ.resolveConflicts(git, objReturn, objEnumStrategy);
						    if(bAnyResolved3) {
							    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
							    //Also noch ein weiteres Mal:
							   objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
						    }
						}
				    }
				}//end status FAILED 
				  

				
																				
			//###############################################################
//	        } catch (CheckoutConflictException cce) {
//	        	System.out.println("Konflikte: CheckoutConflictException...");
//	        	
//		        	System.out.println("Konflikte: CheckoutConflictException... Meine gewaehlte Konfliktstrategie 'ignorieren'");
//		            Collection<String> conflictingPaths = cce.getConflictingPaths();
//		
//		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
//		                // Kein konkreter Pfad bekannt → weiterwerfen
//		            	ExceptionZZZ ez = new ExceptionZZZ(cce);
//		    			throw ez;
//		            }
//	
//		            //Konfliktdateien gezielt zurücksetzen
//		            System.out.println("Konflikte: Setze Pfade gezielt zurueck:");		        	
//		            for (String path : conflictingPaths) {
//		                git.checkout()
//		                   .addPath(path)
//		                   .call();
//		                System.out.println("* " + path);			        	
//		            }
//		
//		            //Pull erneut versuchen
//		            System.out.println("Konflikte: Pull erneut versuchen.");
//		            git.pull().call();
//		            objReturn = pullResult.getMergeResult();
//		           
//		            //Das wäre der Ansatz ohne diese Exception
//		            /*
//		            // Konfliktzustand beenden durch "Markieren der Konfliktauflösung":
//				    git.add().addFilepattern(".").call();
//
//				    git.commit()
//				       .setMessage("Konflikte2 automatisch mit OURS aufgelöst")
//				       .call();
//				    			
//				    
//				    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
//				    //Also noch ein 3. Mal:
//				    MergeCommand mergeCommand = git.merge();
//	                mergeCommand.include(objRef);
//	                mergeCommand.setStrategy(MergeStrategy.RECURSIVE);									 
//					objReturn = mergeCommand.call();
//				    */
		            
		       
						
				//###############################################################	
//	        }catch(IOException ioe) {
//	        	ExceptionZZZ ez = new ExceptionZZZ(ioe);
//	        	throw ez;	
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
		}//end main:
		return objReturn;
	}
	
	/** Für den SSH Weg:
	 *  Merke: Bei Pull mit HTTPS ist es notwendig den pull in fetch und merge zu zerlegen
	 *         Hier ist ein fetch nicht notwendig.
	 * 
	 *  Eine robuste Utility-Methode, die:
	
		pull() ausführt
		CheckoutConflictException gezielt abfängt und danach den Pull automatisch erneut versucht
		oder
		den Merge-Status analysiert... FAILED ... CONFLICTING bearbeitet		
		nur die konfliktbehafteten Dateien zurücksetzt: OURS bleibt erhalten
		
	 * ABER2: Beim Auflösen eines Konflikts müsste man erneut einen MERGE machen. 
	 *        Da intern der PULL eh die Verwendung von FETCH und MERGE ist, wäre das ein unnoetiger doppelter MERGE Schritt.
	 *        Darum eher pullIgnoreCheckoutConflictsSSH_by_FetchMerge_ verwenden.
		
		s. ChatGPT 20260323, 20260508ff
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static MergeResult pullIgnoreCheckoutConflictsSSH_by_PullDirect_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
	        try {
	
	        	if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
	        	//!!! Wichtig: Saubere Vorprüfung, damit der Merge (auch mit ggfs. vorhandenen Konflikten)
		        //             ohne eine Exception durchlaufen kann
		        //Vorprüfung per eigener, gekapselter Routine
		        ResultPreMergeCheck check = GitPreMergeCheck.checkRepositoryState(git);
		        if (!check.isClean()) {
		            check.printReport();
		            break main; // Merge abbrechen
		        }
		        
		        //+++++++++++++++++++++++++
		        //wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("SSH-Loesung: Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
								
				// aber mal explizit als pullCommand
				PullCommand pullCommand = git.pull();
				
				//In der Utility - Klasse das so machen wie in HTTPS und die Url berechnen:
				
				//Das neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach SSH umgewandelt werden.				
				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
				String sUrlBaseIn = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl("git", sUrlBaseIn);
				String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlRepoRemote = JgitUtilZZZ.computeRepositoryUrl(sUrlBaseWithProtocolIn, sRepositoryProjectIn);
				//pullCommand.setRemote(sUrlRepoRemote); //Aber: Anders als beim HTTPS Weg, darf die URL nicht direkt übergeben werden.
				                                         //      Statt dessen den "Aliasnamen" übergeben.
				System.out.println("Verwendete, neu ausgerechnete Url für Remote: " + sUrlRepoRemote);
				
				//Da wir den Aliasnamen übergeben müssen, aber eine Url reinbekommen.
				//Müssen wir aus der Url den Aliasnamen errechnen.
				//denn hier in der static Methode geht ja leider nicht: this.getRepositoryRemoteAlias(); 
				
				String sRemoteRepositoryAlias = JgitUtilZZZ.findRemoteNameByUrl(git, sUrlRepoRemote);
				System.out.println("Verwendete RepositoryAlias für Remote: " + sRemoteRepositoryAlias);
				pullCommand.setRemote(sRemoteRepositoryAlias);

				// pull from remote, hier mit Auswertung des Ergebnisses	
				PullResult pullResult = pullCommand.call();
				
				if (pullResult.isSuccessful()) {
				    System.out.println("Pull erfolgreich");
				} else {
				    System.out.println("Pull fehlgeschlagen");
				}

				objReturn = pullResult.getMergeResult();	   
				if(objReturn!=null) {
					System.out.println("MergeResult: " + objReturn.getMergeStatus());
				}else {
					System.out.println("MergeResult: Kein Status zurueckgegeben.");
				}
				
				//20260428 wofuer braucht es den fetchResult
				FetchResult fetchResult = pullResult.getFetchResult();
				if(fetchResult!=null) {
					System.out.println("FetchResult: " + fetchResult.getMessages());
				}else {
					System.out.println("FetchResult: Keine Meldung zurueckgegeben.");
				}																				
				
				//Hier HTTPS Lösung:
//				//Aber wenn nichts zu fetchen ist, gibt es einen Fehler
//				FetchResult fetchResult = JgitUtilHTTPS.fetchIgnoreNothingToFetch(git, sUrl, credentialsProvider);
//				if(fetchResult==null) break main;
//					
//				//+++ Auswerten eines Fetch
//				String sFetchResultMessages = fetchResult.getMessages();
//				if(sFetchResultMessages!=null) {				
//					System.out.println("Fetch-Result: " + sFetchResultMessages);
//				}
				
//				//++++++++++++++++++++++++++++++++
//				//den richtigen Branch ansteuern
				String sBranch = "master"; // oder dynamisch
				String sFetchRefs = "refs/heads/" + sBranch;
				Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs); //ohne das im Folgenden einzubinden, kommt die Fehlermeldung:    org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
				System.out.println("Merge Ref = " + objRef.getName());
				System.out.println("ObjectId  = " + objRef.getObjectId().getName());
				
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
				
				//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
				CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
								
				//+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
				System.out.println("Starte Merge:");
				try {
					String localRef = "refs/remotes/origin/" + sBranch;
					String remoteRef = "refs/heads/" + sBranch;       //Merke: Ist gleich sFetchRefs weiter oben
					
					ObjectId remoteMaster = git.getRepository().resolve(remoteRef);
					System.out.println("Verwende objRef. Nicht Verwender remoteMaster= '" + remoteMaster.getName() + "'");
										
					MergeCommand mergeCommand = git.merge();
					//geht hier nicht, da nur lokal, mergeCommand.setRemote(sUrl);
					//Also so versuchen.
					//mergeCommand.include(git.getRepository().resolve("FETCH_HEAD")); //ABER: Da hier 2 HEADs sind Fehler : org.eclipse.jgit.api.errors.InvalidMergeHeadsException: merge strategy recursive does not support 2 heads to be merged into HEAD
					//Lösungsansatz: direkt den richtigen Branch verwenden
					//also statt... mergeCommand.include(git.getRepository().resolve("refs/remotes/origin/master"));					
					//mergeCommand.include(remoteMaster);
					//mergeCommand.include(objRef); //ohne das kommt die Fehlermeldung:                 org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
					
					//ABER mit 2 verschiedenen .includes(...) gibt es eine Fehlermeldung wie:
					//Verwende remoteMaster= '56cabdc4169eeb600177b05b8540f5bde4ca3533'
					//Verwende remoteMaster= 'AnyObjectId[56cabdc4169eeb600177b05b8540f5bde4ca3533]'
					//basic.zBasic.ExceptionZZZ: org.eclipse.jgit.api.errors.InvalidMergeHeadsException: merge strategy recursive does not support 2 heads to be merged into HEAD
					
					//Die Lösung ist dann nur 1x das .include(...) aufzurufen.
					//Wenn du nur eine nackte ObjectId übergibst:
					//mergeCommand.include(objectId);
					//kennt JGit keinen Branchnamen mehr. Dann fehlen Informationen wie:
					//welcher Remote?
					//welcher Tracking-Branch?
					//welche Reflog-Namen?
					//
					//Darum ist die Ref-Variante sauberer.
					mergeCommand.include(objRef); //ohne das kommt die Fehlermeldung:                 org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
					
					
					//Folgender DEBUG Code geht nur mit neueren JGIT Versionen:
					//System.out.println("Merge includes:");
					//for(Ref r : mergeCommand.getRefsToMerge()) {
					//    System.out.println(r.getName());
					//}
					
					mergeCommand.setStrategy(MergeStrategy.RECURSIVE);
					 
					objReturn = mergeCommand.call();
					
					//Wenn aber keine Exception geworfen wird, den Status direkt abfragen
					MergeStatus status = objReturn.getMergeStatus();
					System.out.println("Merge-Status:" + status.toString());
					if(status.equals(MergeStatus.CONFLICTING)) {
					    System.out.println("Konflikte erkannt.");

					    Map<String, int[][]> conflicts = objReturn.getConflicts();

					    if(conflicts != null) {
					        for(String path : conflicts.keySet()) {

					        	System.out.println(objEnumStrategy.getDescriptionShort() + ": " + path);

					            // Lokale Version wiederherstellen (z.B. OURS)
					            git.checkout()
					               .setStage(objStage)
					               .addPath(path)
					               .call();
					        }
					        					        					        
					        // Konfliktzustand beenden:
						    git.add().addFilepattern(".").call();

						    System.out.println("Alle Konflikte automatisch mit '" + objEnumStrategy.getName() + "' aufgelöst.");
						    
						    git.commit()
						       .setMessage("Konflikte automatisch mit '" + objEnumStrategy.getName() + "' aufgelöst")
						       .call();
						    
						    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
						    //Also noch ein 2. Mal:
						    objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch); 
					    }					   
					}//end STATUS CONFLICTING
					
					if(status.equals(MergeStatus.FAILED)) {
					    System.out.println("Failed erkannt.");

					    Map<String, MergeFailureReason> failingPaths = objReturn.getFailingPaths();
					    if(failingPaths != null) {
					        for(Map.Entry<String, MergeFailureReason> entry : failingPaths.entrySet()) {

					            String path = entry.getKey();
					            MergeFailureReason reason = entry.getValue();

					            System.out.println(path + " -> " + reason);

					            if(reason == MergeFailureReason.DIRTY_INDEX
					               || reason == MergeFailureReason.DIRTY_WORKTREE) {

					            	System.out.println(objEnumStrategy.getDescriptionShort() + ": " + path);

					                //wirkt aber nicht zuverlässig bei failed:
					                //git.checkout().addPath(path).call();
					                
					                //darum:
					                //reicht aber nicht 
					                //git.reset().addPath(path).call();
					                
					                //darum:
					                git.checkout().setStartPoint("HEAD").addPath(path).call();
					                
					                
					                //Merge result Objekt (ist nur ein Snapshot) neu holen 
					                System.out.println("Starte Merge2:");
					                objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
									
									MergeStatus status2 = objReturn.getMergeStatus();
									System.out.println("Merge-Status2:" + status2.toString());
									
									//Wenn aber keine Exception geworfen wird, den Status direkt abfragen									
					                //Aber nun gibt es den Merge-Status.CONFLICTING
					                if(status2.equals(MergeStatus.CONFLICTING)) {
									    System.out.println("Konflikte2 erkannt.");

									    Map<String, int[][]> conflicts = objReturn.getConflicts();

									    if(conflicts != null) {
									        for(String path2 : conflicts.keySet()) {

									        	System.out.println(objEnumStrategy.getDescriptionShort() + "2: " + path);

									            // Lokale Version wiederherstellen (= OURS)
									            //Besonderheit, nun ist man wirklich im UNMERGED Staus und bekommt folgenden Fehler
									            //org.eclipse.jgit.api.errors.JGitInternalException: Unmerged path: JAZDummy/Arbeit_mit_Git/test.txt
									            //
									            //Darum ist ein normaler Checkout nicht erlaubt.
									            //Es braucht noch die explizite Angabe OURS oder THEIRS
									            
									            
									            git.checkout()
									               .setStage(objStage) //z.B. CheckoutCommand.Stage.OURS
									               .addPath(path2)
									               .call();
									        }
									        
									        
									        // Konfliktzustand beenden durch "Markieren der Konfliktauflösung":
										    git.add().addFilepattern(".").call();

										    System.out.println("Alle Konflikte automatisch mit  '" + objEnumStrategy.getName() + "'  aufgelöst.");
											   										    
										    git.commit()
										       .setMessage("Konflikte2 automatisch mit  '" + objEnumStrategy.getName() + "'  aufgelöst")
										       .call();
										    			
										    
										    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
										    //Also noch ein 3. Mal:
										   objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
										    
									    }									   
									}//end konflikte2					                					               
					            }
					        }
					    }
					}//end status failed  
					  															
					//###############################################################
		        } catch (CheckoutConflictException cce) {
		        	System.out.println("Konflikte: CheckoutConflictException... Meine gewaehlte Konfliktstrategie 'ignorieren'");
		            Collection<String> conflictingPaths = cce.getConflictingPaths();
		
		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
		                // Kein konkreter Pfad bekannt → weiterwerfen
		            	ExceptionZZZ ez = new ExceptionZZZ(cce);
		    			throw ez;
		            }
			           
		            //Konfliktdateien gezielt zurücksetzen
		            System.out.println("Konflikte: Setze Pfade gezielt zurueck:");		        	
		            for (String path : conflictingPaths) {
		                git.checkout()
		                   .setStage(objStage)
		                   .addPath(path)
		                   .call();
		                System.out.println("* " + path);			        	
		            }
		
		            //Pull erneut versuchen
		            System.out.println("Konflikte: Pull erneut versuchen.");
		            git.pull().call();
		            objReturn = pullResult.getMergeResult();
		           
		            //Das wäre der Ansatz ohne diese Exception
		            /*
		            // Konfliktzustand beenden durch "Markieren der Konfliktauflösung":
				    git.add().addFilepattern(".").call();

				    git.commit()
				       .setMessage("Konflikte2 automatisch mit OURS aufgelöst")
				       .call();
				    			
				    
				    //Der Rückgabewert ist aber immer noch mit dem Status "Konflikte" behaftet.
				    //Also noch ein 3. Mal:
				    MergeCommand mergeCommand = git.merge();
	                mergeCommand.include(objRef);
	                mergeCommand.setStrategy(MergeStrategy.RECURSIVE);									 
					objReturn = mergeCommand.call();
				    */
		            
		        }
						
				//###############################################################	
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

	//Z.B.: SSH VERSION:     git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlPartFromUrlSSH(String sUrlSSH) throws ExceptionZZZ {
		return JgitUtilSSH.getUrlPartFromUrl(sUrlSSH);
	}

}
