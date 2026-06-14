package use.jgit.util;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.jgit.protocol.https.JgitStarterHTTPS;
import use.jgit.protocol.ssh.JgitStarterSSH;

public class JgitUtilGIT implements IConstantZZZ{
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
				sReturn = JgitUtilGIT.sPROTOCOL_PART + sUrlRepo;
			}else {
				//das gefundene Protokol entfernen
				String sUrlRepoWithoutProtocol = StringZZZ.stripLeft(sUrlRepo, sProtocolPartFound);
				sReturn = JgitUtilSSH.sPROTOCOL_PART + sUrlRepoWithoutProtocol;
			}
		}//end main:
		return sReturn;
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryAccountFromUrlGIT(String sUrlRepo) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) break main;
			
			//klappt vielleicht nicht immer... sReturn = StringZZZ.right(":"+ sRepositoryRemoteUrlSSH, ":");
			
			//Neben dem Host steht der Account
			String sHost = JgitUtilGIT.getHostFromUrl(sUrlRepo);	
		
			//sReturn = StringZZZ.mid(sRepositoryRemoteUrlSSH+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
			//sReturn = StringZZZ.midLeftRight(sRepositoryRemoteUrlSSH+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = StringZZZ.midRightLeft(sUrlRepo+UrlLogicZZZ.sURL_SEPARATOR_PATH, sHost+":", UrlLogicZZZ.sURL_SEPARATOR_PATH);
		}//end main:
		return sReturn;
	}

	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
	public static String computeRepositoryHostFromUrlGIT(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilGIT.getHostFromUrl(sUrlRepo);
	}

	//Z.B. SSH Version: 	git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryProjectFromUrlGIT(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilGIT.getProjectFromUrl(sUrlRepo);
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
	
	//Z.B. SSH Version: 	git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
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
			
			String sUrlBaseSSH = sUrlBaseGitWithAccountIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			sReturn = sUrlBaseSSH + UrlLogicZZZ.sURL_SEPARATOR_PATH + sRepositoryProject + ".git";
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlTotalGIT(String sHostIn, String sAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sUrlBaseSSH = JgitUtilGIT.computeRepositoryUrlBaseGIT(sHostIn, sAccountIn);		
			sReturn = JgitUtilSSH.computeRepositoryUrlTotalSSH(sUrlBaseSSH, sRepositoryProjectIn);
		}//end main:
		return sReturn;
	}
	//###########################

	/** Z.B.  von Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlPartFromUrlGIT(String sUrlRepoRemoteGITIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepoRemoteGITIn)) break main;
			
			String sUrlGitWithoutProtocol = StringZZZ.right("@" + sUrlRepoRemoteGITIn, "@");
			String sUrlWithoutProtocolAndProject = StringZZZ.left(sUrlGitWithoutProtocol + UrlLogicZZZ.sURL_SEPARATOR_PATH,UrlLogicZZZ.sURL_SEPARATOR_PATH );
			String sUrlWithoutAccount = StringZZZ.left(sUrlWithoutProtocolAndProject + UrlLogicZZZ.sURL_SEPARATOR_PORT,UrlLogicZZZ.sURL_SEPARATOR_PORT );			
			sReturn = sUrlWithoutAccount;			
		}//end main:
		return sReturn;	
	}
	
	//Z.B. SSH Version: 	git@github.com:firak01   also ohne das Projekt
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
			
			sReturn = JgitStarterSSH.sPROTOCOL + "@" + sHost + ":" + sAccount;
		}//end main:
		return sReturn;
	}

	/** Z.B.  von git@github.com:firak01 --> github.com
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
}
