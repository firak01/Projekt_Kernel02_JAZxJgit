package use.jgit.config;

import basic.zBasic.config.IConfigZZZ;

public interface IConfigJGIT extends IConfigZZZ{
	//#################################################
	//Merke: Die Konstanten sind meist nicht final, damit sie von der konkreten Konfiguration
	//       ueberschrieben werden koennen.
	//       Final sind die fuer den Kernel selbst wichtige Konstanten
    static String sPROJECT_DIRECTORY = "Projekt_Kernel02_JAZxJgit";
    static String sPROJECT_NAME = "JAZxJgit";
    
    
	
	final static String sBRANCH_DEFAULT = "master";
	final static String sBRANCH_ALL = "*";
	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = "origin";
	
	
	//private static String sDIRECTORY_CONFIG_DEFAULT = "c:\\fglKernel\\KernelConfig";//Wenn der String absolut angegeben ist, so muss er auch vorhanden sein.
//	public  static String sDIRECTORY_CONFIG_DEFAULT = "<z:Null/>";//Merke: Ein Leerstring ist der Root vom Classpath, z.B. in Eclipse der src-Ordner. Ein "." oder ein NULL-Wert ist der Projektordner in Eclipse
//	public  static String sFILE_CONFIG_DEFAULT = "";                //wird hier nicht benutzt... z.B.: "ZKernelConfigKernel_default.ini";
//	public  static String sKEY_APPLICATION_DEFAULT = "JGIT";
//	public  static String sNUMBER_SYSTEM_DEFAULT= "";               //wird hier nicht benutzt    z.B.: "01";
}
