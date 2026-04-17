package use.tool.jgit;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;

public class JgitUtil implements IConstantZZZ {
	
	public static String addProtocolToUrl(String sProtocol, String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocol)) {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("UrlRepo", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = JgitUtilSSH.addProtocolToUrl(sUrlRepo);
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = JgitUtilHTTPS.addProtocolToUrl(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol für Git Repository. Unbekannter Typ: '" + sProtocol + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryConnectionTypeFromProtocol(String sProtocol) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocol)) {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = "SSH";
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = "HTTPS";
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol für Git Repository. Unbekannter Typ: '" + sProtocol + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryConnectionTypeFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtil.computeRepositoryProtocolFromUrlRepo(sUrlRepo);			
			sReturn = JgitUtil.computeRepositoryConnectionTypeFromProtocol(sReturn);
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryProtocolFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtil.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryProtocolFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtil.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryProtocolFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryHostFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtil.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryHostFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtil.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryHostFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryProjectFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtil.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryProjectFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtil.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryProjectFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlPartFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtil.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryUrlPartFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtil.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryUrlPartFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrl(String sUrlBaseIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlBaseIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBase = sUrlBaseIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			if(JgitUtil.isUrlHTTPS(sUrlBase)) {
				sReturn = JgitUtilHTTPS.computeRepositoryUrlHTTPS(sUrlBase, sRepositoryProject);
			}else if(JgitUtil.isUrlSSH(sUrlBase)) {
				sReturn = JgitUtilSSH.computeRepositoryUrlSSH(sUrlBase, sRepositoryProject);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlBase + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	
	/** Ueberpruefe, ob unter dem Alias des remote Repositories auch eine URL gefunden wird.
	 *  Falls, nein, setzte es ggfs. bei bOverwrite = true;
	 * 
	 * z.B.:
	 * 
	 * [remote "origin"]
			url = https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
			fetch = +refs/heads/*:refs/remotes/origin/*
	 * 
	 * @param repo
	 * @param sRepositoryRemoteAlias
	 * @param sRepositoryRemoteUrl
	 * @param bOverwrite
	 * @throws IOException
	 */
	public static void ensureRemoteExists(Repository repo, String sRepositoryRemoteAlias, String sRepositoryRemoteUrl, boolean bOverwrite) throws ExceptionZZZ {
		try {
			StoredConfig config = repo.getConfig();
	
		    String existingUrl = config.getString("remote", sRepositoryRemoteAlias, "url");
	
		    if (existingUrl == null || existingUrl.trim().isEmpty() || bOverwrite) {
	
		        if (bOverwrite && existingUrl != null && !existingUrl.equals(sRepositoryRemoteUrl)) {
		            System.out.println("Remote '" + sRepositoryRemoteAlias + "' wird überschrieben:");
		            System.out.println("  alt: " + existingUrl);
		            System.out.println("  neu: " + sRepositoryRemoteUrl);
		        }
	
		        config.setString("remote", sRepositoryRemoteAlias, "url", sRepositoryRemoteUrl);
	
		        config.setStringList(
		            "remote",
		            sRepositoryRemoteAlias,
		            "fetch",
		            Collections.singletonList("+refs/heads/*:refs/remotes/" + sRepositoryRemoteAlias + "/*")
		        );
	
		        config.save();
		    }	  
		} catch(IOException ioe) {
			ExceptionZZZ ez = new ExceptionZZZ("IOException: " +ioe.getMessage(), iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	/* https://git-scm.com/book/de/v2/Anhang-B:-Git-in-deine-Anwendungen-einbetten-JGit
	// Create a new repository
Repository newlyCreatedRepo = FileRepositoryBuilder.create(
new File("/tmp/new_repo/.git"));
newlyCreatedRepo.create();

//Open an existing repository
Repository existingRepo = new FileRepositoryBuilder()
.setGitDir(new File("my_repo/.git"))
.build();
	 */
	public static Repository getRepositoryObject(String sRepositoryDirectoryTotal, boolean bCreateMissing) throws ExceptionZZZ{
		Repository objReturn = null;
		main:{
			try {
				if(StringZZZ.isEmpty(sRepositoryDirectoryTotal)){
					ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				File objDirectoryRepo = new File(sRepositoryDirectoryTotal);
				if(!FileEasyZZZ.exists(objDirectoryRepo)) {
					if(!bCreateMissing) {
						ExceptionZZZ ez = new ExceptionZZZ("Projektverzeichnis des Remote Repository existiert nicht.", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						System.out.println("Erstelle fehlendes Repository Verzeichnis: '" + sRepositoryDirectoryTotal + "'");
						FileEasyZZZ.createDirectory(objDirectoryRepo);
					}
				}
				
				//s. https://git-scm.com/book/de/v2/Anhang-B:-Git-in-deine-Anwendungen-einbetten-JGit
				String sRepositoryFileTotal = FileEasyZZZ.joinFilePathName(objDirectoryRepo, ".git");
				File objFileRepo = new File(sRepositoryFileTotal);
				if(!FileEasyZZZ.exists(objFileRepo)) {
					if(!bCreateMissing) {
						ExceptionZZZ ez = new ExceptionZZZ("Projektverzeichnis '" + sRepositoryDirectoryTotal + "' ist kein Git Repository. Es fehlt Datei .git", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						// Create a new repository
						objReturn = FileRepositoryBuilder.create(objFileRepo);					
						objReturn.create();											
					}				
				}else {
					//Open an existing repository
					FileRepositoryBuilder objRepoBuilder = new FileRepositoryBuilder();
					objRepoBuilder.setGitDir(objFileRepo);
					objReturn = objRepoBuilder.build();
				}		
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			}
		}//end main:
		return objReturn;
	}
	
	/** z.B.: https://github.com/firak01   oder  git@github.com:firak01
	 *  liefert nur das Protokoll zurück.
	 * @param sUrlRepo
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocol(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) break main;
			
			String sProtocol = StringZZZ.left(sUrlRepo + "@", "@"); 
			if(sProtocol!=null) {
				if(sProtocol.equalsIgnoreCase("git")) {
					sReturn = sProtocol;
					break main;
				}
			}
			
			sProtocol = UrlLogicZZZ.getProtocol(sUrlRepo);
			if(sProtocol!=null) {
				sReturn = sProtocol;
				break main;
			}
									
		}//end main:
		return sReturn;
	}
	
	/** z.B.: https://github.com/firak01   oder  git@github.com:firak01
	 *  liefert das Protokoll PLUS die Protokol-Separatorzeichen zurück.
	 * @param sUrlRepo
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocolPart(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) break main;
			
			String sProtocol = JgitUtil.getProtocol(sUrlRepo);
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = sProtocol + "@";
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = sProtocol + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL;
			}											
		}//end main:
		return sReturn;
	}

	
	/** Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git 
	 *       von https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
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
	
		
	public static boolean isUrlSSH(String sUrlRepo) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtil.getProtocol(sUrlRepo);
			if(sProtocol.equals("git")) {
				bReturn = true;
				break main;
			}
		}//end main:
		return bReturn;
	}
	
	public static boolean isUrlGit(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtil.isUrlSSH(sUrlRepo);
	}
	
	public static boolean isUrlHTTPS(String sUrlRepo) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = UrlLogicZZZ.getProtocol(sUrlRepo);
			if(sProtocol==null) break main;
			
			if(sProtocol.equals("https")) bReturn = true;
		}//end main:
		return bReturn;
	}
}
