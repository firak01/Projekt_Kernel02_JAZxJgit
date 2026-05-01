package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.crypt.code.ICryptZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.AbstractKernelConfigZZZ;
import basic.zKernel.GetOptZZZ;
import basic.zKernel.IKernelConfigZZZ;
import basic.zKernel.file.ini.IKernelEncryptionIniSolverZZZ;


/**Klasse enthaelt die Werte, die im Kernel als default angesehen werden.
	 *- ApplicationKey: FGL
	 * - SystemNumber: 01
	 * - Verzeichnis: c:\\fglKernel\\KernelConfig
	 * - Datei:		ZKernelConfigKernel_default.ini
	
	Verwende eine eigene Klasse, die KernelConfigZZZ erweitert, um für eine Spezielles Projekt andere Werte zu verwenden.
	
	Siehe IConfigDEV:
	final static String sPATTERN_DEFAULT="pull|push|ssh|https|rl:pat:rrh:rra:rrac:z:"; //ConnectionType: HTTPS oder SSH
	
	Beispiele für Kommandozeilenstrings:
	aa) -pull -https -pat -rra origin -rl C:\HIS-Workspace\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy\JAZDummy
	Problem mit dem Doppelpunkt in https: und im Dateipfad C: 
	ab) -pull -https -pat -rrh "github.com" -rrac=firak01 -project="Projekt_Kernel02_JAZDummy" -rl C:\HIS-Workspace\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy\JAZDummy
	
	ba) -pull -ssl -rra origin -rl C:\HIS-Workspace\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy\JAZDummy
	bb) -pull -ssl -rrh "github.com" -racc=firak01 -project=Projekt_Kernel02_JAZDummy -rl C:\HIS-Workspace\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy\JAZDummy
	
 * @author lindhauer
 *
 */
public class ConfigStarterJGIT extends AbstractKernelConfigZZZ implements IConfigStarterJGIT{
	private static String sPROJECT_PATH = "Projekt_Tool_DevEditor";
	private static String sPROJECT_NAME = "Projekt_Tool_DevEditor"; //normalerweise kuerzer, z.B. sPROJECT_NAME = "JAZKernel";
	//private static String sDIRECTORY_CONFIG_DEFAULT = "c:\\fglKernel\\KernelConfig";//Wenn der String absolut angegeben ist, so muss er auch vorhanden sein.
	private static String sDIRECTORY_CONFIG_DEFAULT = "<z:Null/>";//Merke: Ein Leerstring ist der Root vom Classpath, z.B. in Eclipse der src-Ordner. Ein "." oder ein NULL-Wert ist der Projektordner in Eclipse
	private static String sFILE_CONFIG_DEFAULT = "";                //wird hier nicht benutzt... z.B.: "ZKernelConfigKernel_default.ini";
	private static String sKEY_APPLICATION_DEFAULT = "DEV";
	private static String sNUMBER_SYSTEM_DEFAULT= "";               //wird hier nicht benutzt    z.B.: "01";
	
	

	
	public ConfigStarterJGIT() throws ExceptionZZZ{
		super();
	}
	public ConfigStarterJGIT(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
			
	@Override
	public String getPatternStringDefault() {
		return IConfigStarterJGIT.sPATTERN_DEFAULT;
	}
	
	@Override
	public String[] getArgumentArrayDefault() {
		String[] saArg = new String[8];
		saArg[0] = "-pull";
		saArg[1] = "-ssh";	//Merke: aus dem lokalen Repository, in der Datei .git\config kommt die remote URL 		 
		saArg[2] = "-rra";   //       dazu ist der Remote Alias wichtig, per Default ist das "origin", kann aber auch anders benannt werden.
		saArg[3] = "origin";
		saArg[4] = "-rl";
		saArg[5] = ConfigStarterJGIT.sPROJECT_PATH;
		saArg[6] = "-z";
		saArg[7] = this.getConfigFlagzJsonDefault();
	
		return saArg;
	}
	
	@Override
	public String getApplicationKeyDefault() {
		return ConfigStarterJGIT.sKEY_APPLICATION_DEFAULT;
	}
	@Override
	public String getConfigDirectoryNameDefault() {
		return ConfigStarterJGIT.sDIRECTORY_CONFIG_DEFAULT;
	}
	@Override
	public String getConfigFileNameDefault() {		
		return ConfigStarterJGIT.sFILE_CONFIG_DEFAULT;
	}	
	@Override
	public String getSystemNumberDefault() {
		return ConfigStarterJGIT.sNUMBER_SYSTEM_DEFAULT;
}
	@Override
	public String getProjectName() {
		return ConfigStarterJGIT.sPROJECT_NAME;
	}
	@Override
	public String getProjectDirectory() {
		return ConfigStarterJGIT.sPROJECT_PATH;
	}
	
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
	
	@Override
	public String readActionPull() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("pull");			
		}//end main:		
		return sReturn;
	}

	@Override
	public String readActionCommit() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("commit");			
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String readActionFetch() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("fetch");			
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String readActionPush() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("push");			
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String readActionCommitAndPush() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("commitAndPush");			
		}//end main:		
		return sReturn;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String getConnectionTypeDefault() {
		return "ssh";
	}
	@Override
	public String readConnectionType() throws ExceptionZZZ{
		String sReturn = null;
		main:{
			boolean bReturn = false;
			
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			bReturn = this.isConnectionTypeSSH();
			if(bReturn) {
				sReturn = "ssh";
				break main;
			}
			
			bReturn = this.isConnectionTypeHTTPS();
			if(bReturn) {
				sReturn = "https";
				break main;
			}
				
			sReturn = this.getConnectionTypeDefault();			
		}//end main:		
		return sReturn;
	}
	
	@Override
	public boolean isConnectionTypeSSH() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sReturn = objOpt.readValue("ssh");	
			if(!StringZZZ.isEmpty(sReturn)) bReturn = true;
			
		}//end main:		
		return bReturn;
	}
	
	@Override
	public boolean isConnectionTypeHTTPS() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sReturn = objOpt.readValue("https");
			if(!StringZZZ.isEmpty(sReturn)) bReturn = true;
			
		}//end main:		
		return bReturn;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++	
	@Override
	public String readRepositoryRemoteAlias() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("rra");
			if(sReturn==null){
				sReturn = this.getRepositoryRemoteAliasDefault();
			}
		}//end main:		
		return sReturn;
	}
	@Override
	public String getRepositoryRemoteAliasDefault() throws ExceptionZZZ {
		return IConfigStarterJGIT.sREPOSITORY_REMOTE_ALIAS_DEFAULT;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String getRepositoryProjectNameDefault() throws ExceptionZZZ {		
		return null;
	}
		
	//++++++++++++++++++++++++++++++++++++++
	@Override
	public String readRepositoryProjectName() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sProject = objOpt.readValue("project");
			if(StringZZZ.isEmpty(sProject)) {
				sProject = this.getRepositoryProjectNameDefault();				
			}
			
			sReturn = sProject;
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String readRepositoryRemoteHost() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sHost = objOpt.readValue("rrh");
			if(StringZZZ.isEmpty(sHost)) {
				sHost = this.getRepositoryRemoteHostDefault();				
			}
			
			sReturn = sHost;
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String getRepositoryRemoteHostDefault() throws ExceptionZZZ{
		return "github.com";
	}
	
	@Override
	public String readRepositoryRemoteAccount() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sHost = objOpt.readValue("rrac");
			if(StringZZZ.isEmpty(sHost)) {
				sHost = this.getRepositoryRemoteAccountDefault();				
			}
			
			sReturn = sHost;
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String getRepositoryRemoteAccountDefault() throws ExceptionZZZ{
		return "firak01";
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String getPersonalAccessTokenDefault() {
		return ""; //Merke: GitHub verweigert das PUSHEN eines PATs durch sein Regelwerk!!!
	}
	@Override
	public String readPersonalAccessToken() throws ExceptionZZZ{
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("pat");
			if(sReturn==null){
				sReturn = this.getPersonalAccessTokenDefault();
			}
		}//end main:		
		return sReturn;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String getRepositoryLocalBaseDefault() throws ExceptionZZZ {
		return ConfigStarterJGIT.sPROJECT_PATH; //Also das eigene Verzeichnis als Default
	}
	@Override
	public String readRepositoryLocal() throws ExceptionZZZ {		
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("rl");
			if(sReturn==null){
				sReturn = this.getRepositoryLocalBaseDefault();
			}
		}//end main:		
		return sReturn;
	}
}
