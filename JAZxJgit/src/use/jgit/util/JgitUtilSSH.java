package use.jgit.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.jgit.protocol.ssh.JgitStarterSSH;
import use.jgit.resolve.EnumSetMappedStrategyMergeConflictUtilZZZ;
import use.jgit.resolve.IJgitResolverEnabled;
import use.jgit.resolve.JgitResolverUtilZZZ;
import use.jgit.tool.fetch.GitPostFetchAnalyse;
import use.jgit.tool.merge.GitPreMergeCheck;
import use.jgit.tool.merge.ResultPreMergeCheck;

public class JgitUtilSSH implements IConstantZZZ{
	public static final String sPROTOCOL_PART = JgitStarterSSH.sPROTOCOL + "@";
	
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
			
			sReturn = JgitStarterSSH.sPROTOCOL + "@" + sHost + ":" + sAccount;
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlTotalSSH(String sHostIn, String sAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlBaseSSH = JgitUtilSSH.computeRepositoryUrlBaseSSH(sHostIn, sAccountIn);		
			sReturn = JgitUtilSSH.computeRepositoryUrlTotalSSH(sUrlBaseSSH, sRepositoryProjectIn);
		}//end main:
		return sReturn;
	}
	//####################################
	
	/** Berechne die Remote Url - auch wenn eine https Url uebergeben worden ist - passend fuer HTTPS	 	
	 * Zum Einsatz beim FETCH
	 * @param sUrlRepoRemoteIn
	 * @param sPAT
	 * @return z.B.: https://firak01:<sPAT>@github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalSSH_forFetch(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		return JgitUtilSSH.computeRepositoryUrlTotalSSH(sUrlRepoRemoteIn);	
	}
	
	/** Berechne die Remote Url - auch wenn eine https Url uebergeben worden ist - passend fuer HTTPS	 	
	 * Zum Einsatz beim PUSCH
	 * @param sUrlRepoRemoteIn
	 * @param sPAT
	 * @return z.B.: https://firak01:<sPAT>@github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalSSH_forPush(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			sReturn = computeRepositoryUrlTotalSSH_forFetch(sUrlRepoRemoteIn);
		}//end main:
		return sReturn;
	}
	
	
	/** Berechne dir RemoteUrl - auch wenn eine https Url uebergeben worden ist - passend fuer SSH
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlTotalSSH(String sUrlRepoRemoteIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{		
			String sUrlPartFromRepo = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
			
			//String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl(JgitStarterSSH.sPROTOCOL, sUrlPartFromRepo);
			String sRepositoryHostIn = JgitUtilZZZ.computeRepositoryHostFromUrlRepo(sUrlRepoRemoteIn);
			
			String sRepositoryAccountIn = JgitUtilZZZ.computeRepositoryAccountFromUrlRepo(sUrlRepoRemoteIn);
			String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
			
			String sUrlRepoRemote = JgitUtilSSH.computeRepositoryUrlTotalSSH(sRepositoryHostIn, sRepositoryAccountIn, sRepositoryProjectIn);
			sReturn = sUrlRepoRemote;
		}//end main:
		return sReturn;		
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlTotalSSH(String sUrlBaseSshWithAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlBaseSshWithAccountIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBaseSSH = sUrlBaseSshWithAccountIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			sReturn = sUrlBaseSSH + UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryProject + ".git";
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
	
	/**
	 * @param git
	 * @param credentialsProvider
	 * @param sRemoteRepositoryAlias
	 * @param sBranchIn
	 * @return
	 * @throws ExceptionZZZ
	 * 
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
				 
	 */
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,	        
	        CredentialsProvider credentialsProvider,
	        String sRemoteRepositoryAlias,
	        String sBranchIn
	) throws ExceptionZZZ{
		FetchResult objReturn = null;
		main:{
		    try {
		    	try {
					JgitUtilZZZ.debugForFetch(git);
				} catch (URISyntaxException e) {
					ExceptionZZZ ez = new ExceptionZZZ(e);
					throw ez;
				}
		    	
		    	
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand gitCommandFetch = git.fetch();
		        
		        //SSH-Weg: Ohne URL!
		        //dafuer mit Alias
		        gitCommandFetch.setRemote(sRemoteRepositoryAlias);
		        
		        if (credentialsProvider != null) {
		            gitCommandFetch.setCredentialsProvider(credentialsProvider);
		        }
		        
		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*		        		       
		        String sBranch = "master";
		        if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
		        
		        String remoteRef = "refs/heads/" + sBranch;
		        String localTrackingRef = "refs/remotes/origin/" + sBranch;
		        
		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		        //+ für "fast forward"
		        gitCommandFetch.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));

		        
		        
//		        //aber: vermutlich wird auf dem falschen Branch gearbeitet.
//			    gitCommandFetch.setRefSpecs(
//			    	   //TransportException: Remote does not have refs/heads/main available for fetch.
//			    		//new RefSpec("+refs/heads/main:refs/remotes/origin/main")
//
//			    		//Nach obiger minierklärung ist der erste Teil aber der lokale
//			    		//der zweite Teil ist remote... 
//			    		//das Wort orign taucht nur als Section auf
//			    		//TODOGOON 20260615: Setze diesen String auch korrekt, wie auch die URL
//			    		//                   Bei der Konfiguration
//			    		new RefSpec("+refs/heads/" + sBranch + ":refs/remotes/origin/"+sBranch);	
//			    		
//			    	);
		        
		        
		        objReturn = gitCommandFetch.call();
	
		     
	
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
	
	//########################################################
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
	
	/** Z.B.  von ssh://git@github.com/firak01/repo.git
	 * @param sRepositoryRemoteUrlSSH
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
	
	/** Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git 
	 *       von https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProjectFromUrl(String sRepositoryRemoteUrlSSH) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlSSH)) break main;
			
			String sUrlWithoutEnding = StringZZZ.stripRight(sRepositoryRemoteUrlSSH, ".git");
			String sProject = StringZZZ.right(sUrlWithoutEnding, UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = sProject;
		}//end main:
		return sReturn;
	}
	
	/** Z.B.  von git@github.com:firak01
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocolFromUrl(String sRepositoryRemoteUrl) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrl)) break main;
			
			sReturn = UrlLogicZZZ.getProtocol(sRepositoryRemoteUrl);
		
		}//end main:
		return sReturn;
	}
	
	
	/** Z.B.  von Z.B. von ssh://git@github.com/firak01/repo.git
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getUrlPartFromUrl(String sRepositoryRemoteUrl) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrl)) break main;
				
				//String sUrlPartDomainFromHttpsRepo =StringZZZ.right("@" + sUrlHTTPS, "@");				
				/////sUrlPartDomainFromHttpsRepo = StringZZZ.left(sUrlPartDomainFromHttpsRepo + ":", ":");				
				////String sUrlPartRepoFromHttpsRepo = StringZZZ.right(":" + sUrlHTTPS, ":");				
				////sReturn = sUrlPartDomainFromHttpsRepo + "/" + sUrlPartRepoFromHttpsRepo;
				
			sReturn = UrlLogicZZZ.getUrlWithoutParameter(sRepositoryRemoteUrl);
				
			String sUrlPartDomainFromHttpsRepo = UrlLogicZZZ.getHost(sRepositoryRemoteUrl); 
			String sUrlPartRepoFromHttpsRepo = UrlLogicZZZ.getPath(sReturn); 
			sReturn = sUrlPartDomainFromHttpsRepo + sUrlPartRepoFromHttpsRepo;
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
		return JgitUtilSSH.pullSSH_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, null, true);
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
		return pullSSH_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, true, true);
	}
	
	public static MergeResult pullSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bSuppressExceptionOnMergeFail) throws ExceptionZZZ {
		return pullSSH_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, true, bSuppressExceptionOnMergeFail);
	}
	
	public static MergeResult pullSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bByFetchMerge, boolean bSuppressExceptionOnMergeFail) throws ExceptionZZZ {
		return pullSSH_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, bByFetchMerge, bSuppressExceptionOnMergeFail);
		
	}
	
	public static MergeResult pullSSH_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bByFetchMerge, boolean bSuppressExceptionOnMergeFail) throws ExceptionZZZ {
		if(bByFetchMerge) {
			return JgitUtilSSH.pullSSH_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, bSuppressExceptionOnMergeFail);
		}else {
			return JgitUtilSSH.pullSSH_by_PullDirect_(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn);
		}
		
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
	private static MergeResult pullSSH_by_PullDirect_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn) throws ExceptionZZZ {
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
		        
		        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		        if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        
		        String sBranch="master";
		        if (!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch = sBranchIn;
		
		        
		        //wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("SSH-Loesung: Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
				
				//+++++++++++++++
				//Die URL neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach SSH umgewandelt werden.				
				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
				String sUrlBaseIn = JgitUtilZZZ.computeRepositoryUrlPartFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlBaseWithProtocolIn = JgitUtilZZZ.addProtocolToUrl("git", sUrlBaseIn);
				String sRepositoryProjectIn = JgitUtilZZZ.computeRepositoryProjectFromUrlRepo(sUrlRepoRemoteIn);
				String sUrlRepoRemote = JgitUtilZZZ.computeRepositoryUrlTotalFor("git", sUrlBaseWithProtocolIn, sRepositoryProjectIn);
				System.out.println("Url für die Suche nach dem RepositoryAlias. Remote: " + sUrlRepoRemote);
				
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
	 * @param bSuppressExceptionOnMergeFail
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static MergeResult pullSSH_by_FetchMerge_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, boolean bSuppressExceptionOnMergeFail) throws ExceptionZZZ {
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
		        if (sUrlRepoRemoteIn == null || sUrlRepoRemoteIn.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        
		        String sBranch="master";
		        if(!StringZZZ.isEmptyNull(sBranchIn)) sBranch = sBranchIn;
		       		        
		        //Zwar geht Pull auch direkt, aber: Pull ist eh eine Anwendung von fetch und merge. Wenn ich das mache spart es einen Merge am Schluss.							
				System.out.println("SSH-Loesung: Spare einen MERGE. Zerlege deshalb PULL in FETCH und MERGE");
				
		        
		        Repository repo = git.getRepository();
		
		        //++++++++++++
		        //Das neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach SSH umgewandelt werden.				
				//In der der zuvor gemachten Git Konfiguration wurde sichergestellt "ensureRemoteExists", das solch ein Eintrag existiert.
		        String sUrlRepoRemote = JgitUtilSSH.computeRepositoryUrlTotalSSH_forFetch(sUrlRepoRemoteIn);
		        System.out.println("Url fuer Fetch (neu ausgerechnet): '" + sUrlRepoRemote + "'");
				
		        //Aber: Anders als beim HTTPS Weg, darf die URL nicht direkt übergeben werden.
		        //      Statt dessen den "Aliasnamen" übergeben.
				//pullCommand.setRemote(sUrlRepoRemote);
				                                         
				//Da wir den Aliasnamen übergeben müssen, aber eine Url reinbekommen.
				//Müssen wir aus der Url den Aliasnamen errechnen.
				//denn hier in der static Methode geht ja leider nicht: this.getRepositoryRemoteAlias(); 
		        
				String sRemoteRepositoryAlias = JgitUtilZZZ.findRemoteNameByUrl(git, sUrlRepoRemote);
				System.out.println("RepositoryAlias fuer Fetch (SSH Weg, neu gesucht per Url): '" + sRemoteRepositoryAlias + "'");
						        
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
	
	public static MergeResult pullIgnoreCheckoutConflictsSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ {		
		return pullIgnoreCheckoutConflictsSSH(git,credentialsProvider, sUrlRepoRemoteIn, null, objEnumStrategyMergeConflict, true);
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
	public static MergeResult pullIgnoreCheckoutConflictsSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict, boolean bUseFetchMerge) throws ExceptionZZZ {
		if(bUseFetchMerge) {
			return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_PullDirect_(git,credentialsProvider, sUrlRepoRemoteIn, null, objEnumStrategyMergeConflict);
		}else {
			return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_FetchMerge_(git,credentialsProvider, sUrlRepoRemoteIn, null, objEnumStrategyMergeConflict);
		}
	}
				
	public static MergeResult pullIgnoreCheckoutConflictsSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy) throws ExceptionZZZ {
		return pullIgnoreCheckoutConflictsSSH(git, credentialsProvider, sUrlRepoRemoteIn, sBranchIn, objEnumstrategy, true);
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
	public static MergeResult pullIgnoreCheckoutConflictsSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy, boolean bUseFetchMerge) throws ExceptionZZZ {
		if(bUseFetchMerge) {
			//das soll eigentlich verwendet werden
			return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_FetchMerge_(git,credentialsProvider, sUrlRepoRemoteIn, sBranchIn, objEnumstrategy);
		}else {
			//das ist dann irgendwie ein doppelter Merge
			return JgitUtilSSH.pullIgnoreCheckoutConflictsSSH_by_PullDirect_(git,credentialsProvider, sUrlRepoRemoteIn, sBranchIn, objEnumstrategy);			
		}				
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
        	objReturn = pullSSH_by_FetchMerge_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch, true);
        	
        	
        	//Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
			if(objReturn==null) {					
				System.out.println("MergeResult: Null.");
				break main;
			}

			//TODOGOON20260514: Momentan wird noch jedes Mal in den Aufrufen innerhalb der Schleife die Stage geholt
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
				    System.out.println("Konflikte erkannt ("+iCount+"). IgnoreConflicts. Strategy: " + objEnumStrategy.getName());
				    
				    //Mit dem ersten Merge - Ergebnis weiterarbeiten. Das ist der Vorteil gegenüber einem normalen PULL
				    bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objReturn, objEnumStrategy);					    
				}//end STATUS "CONFLICTING"
			
			
				if(status.equals(MergeStatus.FAILED)) {
				    System.out.println("Failed erkannt ("+iCount+")");

				    bAnyResolved= JgitResolverUtilZZZ.resolveFailed(git, objReturn);

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
			
			if(iCount>=1) {
				System.out.println("\nErgebnis der Konfliktbehandlung:");
				if(objEnumStrategy.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
					//Erinnerung ausgeben, das die lokalen Änderungen zwar "ueberlebt" haben, aber noch nicht im Remote sind.
				    System.out.println(objEnumStrategy.getDescriptionShort() +". Die behaltene lokale Version muss noch gepusht werden, damit sie im Remote ist.");
				}else {
					System.out.println(objEnumStrategy.getDescriptionShort());
				}
			}
											

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
	private static MergeResult pullIgnoreCheckoutConflictsSSH_by_PullDirect_(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn, String sBranchIn, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		MergeResult objReturn = null;
		main:{
	        //try {
	        	 String sBranch = "master";
			     if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
			     
			    //Mache hier den Pull direkt durch PullCommand
			    objReturn = pullSSH_by_PullDirect_(git, credentialsProvider, sUrlRepoRemoteIn, sBranch);
		        	
			    //MERGERESULT PRÜFEN
			    //Mit dem Merge - Ergebnis weiterarbeiten vom PULL weiterarbeiten.
				if(objReturn==null) {					
					System.out.println("MergeResult: Null.");
					break main;
				}
		      
				//TODOGOON20260514: Momentan wird noch jedes Mal in den Aufrufen innerhalb der Schleife die Stage geholt
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
					    System.out.println("Konflikte erkannt ("+iCount+"). IgnoreConflicts. Strategy: " + objEnumStrategy.getName());
					    
					    //Mit dem Merge - Ergebnis weiterarbeiten.
					    bAnyResolved = JgitResolverUtilZZZ.resolveConflicts(git, objReturn, objEnumStrategy);					    
					}//end STATUS "CONFLICTING"
				
				
					if(status.equals(MergeStatus.FAILED)) {
					    System.out.println("Failed erkannt ("+iCount+")");

					    bAnyResolved= JgitResolverUtilZZZ.resolveFailed(git, objReturn);

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
				
				if(iCount>=1) {
					System.out.println("\nErgebnis der Konfliktbehandlung:");
					if(objEnumStrategy.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
						//Erinnerung ausgeben, das die lokalen Änderungen zwar "ueberlebt" haben, aber noch nicht im Remote sind.
					    System.out.println(objEnumStrategy.getDescriptionShort() +". Die behaltene lokale Version muss noch gepusht werden, damit sie im Remote ist.");
					}else {
						System.out.println(objEnumStrategy.getDescriptionShort());
					}
				}
												

			}//end main:
			return objReturn;
	}

	//Z.B.: SSH VERSION:     git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlPartFromUrlSSH(String sUrlSSH) throws ExceptionZZZ {
		return JgitUtilSSH.getUrlPartFromUrl(sUrlSSH);
	}

}
