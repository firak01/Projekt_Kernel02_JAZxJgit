package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigJGIT extends IKernelConfigZZZ{
	public static String sPROJECT_PATH = "JAZxJgit";
	public  static String sPROJECT_NAME = "JAZxJgit"; //normalerweise kuerzer, z.B. sPROJECT_NAME = "JAZKernel";
	
	final static String sBRANCH_DEFAULT = "master";
	final static String sBRANCH_ALL = "*";
	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = "origin";
	
	
	//private static String sDIRECTORY_CONFIG_DEFAULT = "c:\\fglKernel\\KernelConfig";//Wenn der String absolut angegeben ist, so muss er auch vorhanden sein.
	public  static String sDIRECTORY_CONFIG_DEFAULT = "<z:Null/>";//Merke: Ein Leerstring ist der Root vom Classpath, z.B. in Eclipse der src-Ordner. Ein "." oder ein NULL-Wert ist der Projektordner in Eclipse
	public  static String sFILE_CONFIG_DEFAULT = "";                //wird hier nicht benutzt... z.B.: "ZKernelConfigKernel_default.ini";
	public  static String sKEY_APPLICATION_DEFAULT = "JGIT";
	public  static String sNUMBER_SYSTEM_DEFAULT= "";               //wird hier nicht benutzt    z.B.: "01";
}
