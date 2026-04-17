package use.tool.jgit;

import java.util.Collection;

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

public class JgitUtilSSH implements IConstantZZZ{
	public static final String sPROTOCOL_PART = "git@";
	
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
		return JgitUtil.getProjectFromUrl(sRepositoryRemoteUrlSSH);
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
	
	
	//######################################################
	//###  PULL
	
	
	/** Anders als bei HTTPS kann hier ein Pull direkt gemacht werden, also ohne Zerlegung in Fetch und Merge.
	 * @param git
	 * @param credentialsProvider
	 * @param sRepoRemote
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static boolean pullSSH(Git git, CredentialsProvider credentialsProvider, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {	
				//wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
				
				// aber mal explizit als pullCommand
				PullCommand pullCommand = git.pull();

				 //Original, Aber jetzt in der Util mal so machen wie in HTTPS und die Url berechnen.
//				String sRemoteRepositoryAlias = this.getRepositoryRemoteAlias();
//				System.out.println("Verwendete RepositoryAlias für Remote: " + sRemoteRepositoryAlias);
//				pullCommand.setRemote(sRemoteRepositoryAlias);

				//Das neu auszurechnen macht Sinn, wenn z.B. eine HTTPS Adresse übergeben wird. Dann muss das nach SSH umgewandelt werden.
				TODOGOON20260417;//Das liefert aber noch ein Projekt mit. Rauskommen soll aber github.com:firak01
				String sUrlBaseIn = JgitUtil.computeRepositoryUrlPartFromUrlRepo(sRepoRemote);
				String sUrlBaseWithProtocolIn = JgitUtil.addProtocolToUrl("git", sUrlBaseIn);
				String sRepositoryProjectIn = JgitUtil.computeRepositoryProjectFromUrlRepo(sRepoRemote);
				String sUrlRepoRemote = JgitUtil.computeRepositoryUrl(sUrlBaseWithProtocolIn, sRepositoryProjectIn);
				pullCommand.setRemote(sUrlRepoRemote);
				
				// pull from remote, hier mit Auswertung des Ergebnisses	
				PullResult pullResult = pullCommand.call();
				
				
				if (pullResult.isSuccessful()) {
				    System.out.println("Pull erfolgreich");
				    bReturn = true;
				} else {
				    System.out.println("Pull fehlgeschlagen");
				    bReturn = false;
				}

				MergeResult mergeResult = pullResult.getMergeResult();
				if(mergeResult!=null) {
					System.out.println("MergeResult: " + mergeResult.getMergeStatus());
				}else {
					System.out.println("MergeResult: Kein Status zurueckgegeben.");
				}
				
				FetchResult fetchResult = pullResult.getFetchResult();
				if(fetchResult!=null) {
					System.out.println("FetchResult: " + fetchResult.getMessages());
				}else {
					System.out.println("FetchResult: Keine Meldung zurueckgegeben.");
				}																				
				bReturn = true;
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
		return bReturn;
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
		String sReturn = null;
		main:{
			String sUrlPartDomainFromSshRepo = StringZZZ.right("@" + sUrlSSH, "@");
			sUrlPartDomainFromSshRepo = StringZZZ.left(sUrlPartDomainFromSshRepo + ":", ":");
			
			String sUrlPartRepoFromSshRepo = StringZZZ.right(":" + sUrlSSH, ":");
			
			sReturn = sUrlPartDomainFromSshRepo + "/" + sUrlPartRepoFromSshRepo;			
		}//end main:
		return sReturn;
	}

}
