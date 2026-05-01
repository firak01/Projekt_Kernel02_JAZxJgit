package use.tool.jgit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.tool.jgit.merge.GitPreMergeCheck;
import use.tool.jgit.merge.ResultPreMergeCheck;

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
	
	
	/** Z.B.  von git@github.com:firak01
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getAccountFromUrl(String sRepositoryRemoteUrlSSH) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlSSH)) break main;
			
			sReturn = StringZZZ.right(":"+ sRepositoryRemoteUrlSSH, ":");
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
	 * @param git
	 * @param credentialsProvider
	 * @param sUrlRepoRemoteIn
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static MergeResult pullSSH(Git git, CredentialsProvider credentialsProvider, String sUrlRepoRemoteIn) throws ExceptionZZZ {
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

	//Z.B.: SSH VERSION:     git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlPartFromUrlSSH(String sUrlSSH) throws ExceptionZZZ {
		return JgitUtilSSH.getUrlPartFromUrl(sUrlSSH);
	}

}
