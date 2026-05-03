package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigJGIT extends IKernelConfigZZZ{
	public static String sPROJECT_PATH = "JAZxJgit";
	public  static String sPROJECT_NAME = "JAZxJgit"; //normalerweise kuerzer, z.B. sPROJECT_NAME = "JAZKernel";
	//private static String sDIRECTORY_CONFIG_DEFAULT = "c:\\fglKernel\\KernelConfig";//Wenn der String absolut angegeben ist, so muss er auch vorhanden sein.
	public  static String sDIRECTORY_CONFIG_DEFAULT = "<z:Null/>";//Merke: Ein Leerstring ist der Root vom Classpath, z.B. in Eclipse der src-Ordner. Ein "." oder ein NULL-Wert ist der Projektordner in Eclipse
	public  static String sFILE_CONFIG_DEFAULT = "";                //wird hier nicht benutzt... z.B.: "ZKernelConfigKernel_default.ini";
	public  static String sKEY_APPLICATION_DEFAULT = "JGIT";
	public  static String sNUMBER_SYSTEM_DEFAULT= "";               //wird hier nicht benutzt    z.B.: "01";
	
	//das lokale Repository-Verzeichnis
	public String readRepositoryLocal() throws ExceptionZZZ;
	public String getRepositoryLocalBaseDefault() throws ExceptionZZZ;
	
	//... daran kommt dann noch das Projektverzeichnis
	public String getRepositoryProjectNameDefault() throws ExceptionZZZ;
	public String readRepositoryProjectName() throws ExceptionZZZ;
	
	//... ein moeglicher Kommentar, z.B. für einen (notwendigen) Commit, auch nach dem Aufloesen des Merge-Konflikts
	public String getCommentDefault() throws ExceptionZZZ;
	public String readComment() throws ExceptionZZZ;
}
