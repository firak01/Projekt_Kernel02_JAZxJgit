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
public class ConfigResolverJGIT extends AbstractKernelConfigZZZ implements IConfigResolverJGIT{
	private static String sPROJECT_PATH = "Projekt_Tool_DevEditor";
	private static String sPROJECT_NAME = "Projekt_Tool_DevEditor"; //normalerweise kuerzer, z.B. sPROJECT_NAME = "JAZKernel";
	//private static String sDIRECTORY_CONFIG_DEFAULT = "c:\\fglKernel\\KernelConfig";//Wenn der String absolut angegeben ist, so muss er auch vorhanden sein.
	private static String sDIRECTORY_CONFIG_DEFAULT = "<z:Null/>";//Merke: Ein Leerstring ist der Root vom Classpath, z.B. in Eclipse der src-Ordner. Ein "." oder ein NULL-Wert ist der Projektordner in Eclipse
	private static String sFILE_CONFIG_DEFAULT = "";                //wird hier nicht benutzt... z.B.: "ZKernelConfigKernel_default.ini";
	private static String sKEY_APPLICATION_DEFAULT = "DEV";
	private static String sNUMBER_SYSTEM_DEFAULT= "";               //wird hier nicht benutzt    z.B.: "01";
	
	

	
	public ConfigResolverJGIT() throws ExceptionZZZ{
		super();
	}
	public ConfigResolverJGIT(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
			
	@Override
	public String getPatternStringDefault() {
		return IConfigResolverJGIT.sPATTERN_DEFAULT;
	}
	
	@Override
	public String[] getArgumentArrayDefault() {
		String[] saArg = new String[8];
		saArg[0] = "-conflict";
		saArg[1] = "-filepath:";	//Merke: aus dem lokalen Repository, in der Datei .git\config kommt die remote URL 		 
		saArg[6] = "-z";
		saArg[7] = this.getConfigFlagzJsonDefault();
	
		return saArg;
	}
	
	@Override
	public String getApplicationKeyDefault() {
		return ConfigResolverJGIT.sKEY_APPLICATION_DEFAULT;
	}
	@Override
	public String getConfigDirectoryNameDefault() {
		return ConfigResolverJGIT.sDIRECTORY_CONFIG_DEFAULT;
	}
	@Override
	public String getConfigFileNameDefault() {		
		return ConfigResolverJGIT.sFILE_CONFIG_DEFAULT;
	}	
	@Override
	public String getSystemNumberDefault() {
		return ConfigResolverJGIT.sNUMBER_SYSTEM_DEFAULT;
}
	@Override
	public String getProjectName() {
		return ConfigResolverJGIT.sPROJECT_NAME;
	}
	@Override
	public String getProjectDirectory() {
		return ConfigResolverJGIT.sPROJECT_PATH;
	}
	
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben

	@Override
	public String readActionConflict() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("conflict");			
		}//end main:		
		return sReturn;
	}	
	
	
	@Override
	public String readFilePath() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sFilePath = objOpt.readValue("filepath");
			if(StringZZZ.isEmpty(sFilePath)) {
				sFilePath = this.getFilePathDefault();				
			}
			
			sReturn = sFilePath;
		}//end main:		
		return sReturn;
	}
	
	@Override 
	public String getFilePathDefault() throws ExceptionZZZ{
		return "";
	}
}
