package use.tool.jgit;

import java.util.Collection;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;

public class JgitUtilSSH implements IConstantZZZ{
	
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
	public static boolean pullIgnoreCheckoutConflictsSSH(Git git) throws GitAPIException, ExceptionZZZ {
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
