package use.jgit.config;

import java.util.ArrayList;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.GetOptZZZ;
import basic.zKernel.config.help.IKernelConfigHeaderLineZZZ;
import basic.zKernel.config.help.IKernelConfigHelpLineZZZ;
import basic.zKernel.config.help.KernelConfigHeaderLineZZZ;
import basic.zKernel.config.help.KernelConfigHelpLineZZZ;


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
public class ConfigResolverLocalJGIT extends AbstractConfigStarterLocalJGIT implements IConfigResolverJGIT{
	private static final long serialVersionUID = 5176350334837106190L;

	public ConfigResolverLocalJGIT() throws ExceptionZZZ{
		super();
	}
	public ConfigResolverLocalJGIT(String[] saArg) throws ExceptionZZZ {
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
	
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
		
	//### aus IConfigZZZ
	//Merke 20260615: Besser eine Liste von Hilf-Objekt-Zeilen auch kein Enum, der Ansatz mit der einfachen Liste der Objekte läßt sich einfacher 
	//                über mehrere Projekte und Vererbungstrukturen umsetzen
	//Also nicht so etwas nutzen wie:
	//public enum LOGSTRINGFORMAT implements IEnumSetMappedStringFormatZZZ{		
	//            und darin:    STRINGTYPE01_STRING_BY_STRING("stringtype01",IStringFormatZZZ.iFACTOR_STRINGTYPE01_STRING_BY_STRING, IStringFormatZZZ.sSEPARATOR_PREFIX_DEFAULT + "[A01]", "%s",IStringFormatZZZ.iARG_STRING,  "[/A01]" + IStringFormatZZZ.sSEPARATOR_POSTFIX_DEFAULT, "Gib den naechsten Log String - sofern vorhanden - in diesem Format aus."),			
	@Override
	public List<IKernelConfigHelpLineZZZ>getHelpList() throws ExceptionZZZ{
		ArrayList<IKernelConfigHelpLineZZZ>listaReturn=new ArrayList<IKernelConfigHelpLineZZZ>();
		main:{
			//Berücksichtige dabei die Paramter aus den "Pattern" Strings
			//final static String sPATTERN4GIT_RESOLVER_DEFAULT="help|?|status|conflict|commit|conflictCommit|rl:project:filepath:comment.";	
			IKernelConfigHeaderLineZZZ objHeaderLine=null;
			objHeaderLine= new KernelConfigHeaderLineZZZ(1, "Argumente für: " + this.getProjectName());
			
			IKernelConfigHelpLineZZZ objHelp=null;
			objHelp = new KernelConfigHelpLineZZZ();
			objHelp.setHeaderLine(objHeaderLine);
			listaReturn.add(objHelp);
			
			//Diese werden auch im Starter genutzt
			objHeaderLine= new KernelConfigHeaderLineZZZ("Allgemeine Argumente aus: " + IConfigResolverJGIT.sPROJECT_NAME);						
			objHelp = new KernelConfigHelpLineZZZ("comment:","Kommentar","Kommentar für den Commit");
			objHelp.setHeaderLine(objHeaderLine);
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("project:","Projekt","Name des Projekts im Repository");		
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("rl:","Dateipfad","Pfad zum lokalen Repository");
			listaReturn.add(objHelp);
			
			objHeaderLine= new KernelConfigHeaderLineZZZ(1,"Aktionen aus: " + IConfigResolverJGIT.sPROJECT_NAME);
			objHelp = new KernelConfigHelpLineZZZ("commit","Commit","Änderungen an das lokale Repository übertragen");		
			objHelp.setHeaderLine(objHeaderLine);
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("resolveConflict","Konfliktauflösung","Löse den Konflikt in der angegebenen Datei auf, brücksichtige dabei per Flag übergebene Strategien.");
			listaReturn.add(objHelp);			
			objHelp = new KernelConfigHelpLineZZZ("resolveConflictCommit","Konflikt und Commit","Löse den Konflikt in der angegebenen Datei auf, brücksichtige dabei per Flag übergebene Strategien UND sofort die Änderungen an das lokale Repository übertragen");
			listaReturn.add(objHelp);	
			objHelp = new KernelConfigHelpLineZZZ("searcConflictFiles","Suche nach Konfliktdateien","Suche nach Dateien, die Konfliktmarkierungen enthalten");
			listaReturn.add(objHelp);	
			objHelp = new KernelConfigHelpLineZZZ("searcConflictFilesDeleted","Suche nach 'Deleted' Konfliktdateien","Suche nach Dateien, die den Status 'DELTED' im Repository haben.");
			listaReturn.add(objHelp);	
			objHelp = new KernelConfigHelpLineZZZ("status","Status","Status des rl: Repositories");		
			listaReturn.add(objHelp);
			
			
			//Nur fuer den ConflictResolver
			objHelp = new KernelConfigHelpLineZZZ("filepath:","Dateipfad","Pfad zur Datei mit dem Konflikt");
			listaReturn.add(objHelp);
		}//end main:
		return listaReturn;
	}
	
	//### aus IConfigJGIT
	@Override
	public String getCommentDefault() throws ExceptionZZZ {
		return "Konflikt-Resolver.";
	}
		
	//### aus IConfigResolverGIT
	@Override
	public String readActionResolveByStageState() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("resolveByStageState");			
		}//end main:		
		return sReturn;
	}	
	
	@Override
	public String readActionResolveConflictDeleted() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("resolveConflictDeleted");			
		}//end main:		
		return sReturn;
	}	
	
	@Override
	public String readActionResolveConflict() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("resolveConflict");			
		}//end main:		
		return sReturn;
	}	
	
	@Override
	public String readActionResolveConflictCommit() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("resolveConflictCommit");			
		}//end main:		
		return sReturn;
	}	
	
	@Override
	public String readActionSearchConflictFiles() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("searchConflictFiles");			
		}//end main:		
		return sReturn;
	}	
	
	@Override
	public String readActionSearchConflictFilesDeleted() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("searchConflictFilesDeleted");			
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
