package use.jgit.config;

import java.io.File;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.config.ConfigUtilZZZ;
import basic.zBasic.util.abstractList.ListUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.GetOptZZZ;
import basic.zKernel.config.help.IKernelConfigHelpLineZZZ;


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
public abstract class AbstractConfigRepositoryManagerJGIT extends AbstractConfigRepositoryJGIT implements IConfigRepositoryManagerJGIT{
	private static final long serialVersionUID = 1229381223690096548L;
	
	protected boolean bRepositoryBare = false;
	
	
	
	public AbstractConfigRepositoryManagerJGIT() throws ExceptionZZZ{
		super();
	}
	public AbstractConfigRepositoryManagerJGIT(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
			
	
	
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
	
	//### aus 
	//Gib die Hilfsinfos als String zurück
	@Override
	public String createHelp() throws ExceptionZZZ{
		String sReturn = "";
		main:{
			//Hier gibt es keine Elternklasse mit solch einer Methode... 
			List<IKernelConfigHelpLineZZZ> listaHelpLineSuper = super.getHelpList();											
			List<IKernelConfigHelpLineZZZ> listaHelpLineTotal = this.getHelpList();
			
		    listaHelpLineTotal = ListUtilZZZ.join(listaHelpLineSuper, listaHelpLineTotal);
		    
		    sReturn = ConfigUtilZZZ.createHelp(listaHelpLineTotal);
		}//end main
		return sReturn;
	}

	
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
	//    LOKALE KONFIGURATION
	//++++++++++++++++++++++++++++++++++++++++++

	//### aus IConfigJGIT
	
	//### aus IConfigStarterLocalJGIT	
	
	//siehe dort...
	@Override
	public void isRepositoryBare(boolean bRepositoryBare) throws ExceptionZZZ {
		this.bRepositoryBare = bRepositoryBare;
	}
	@Override
	public boolean isRepositoryBare() throws ExceptionZZZ {
		return this.bRepositoryBare;
	}
	
	//+++++++++++++++++++++++++++++++++++++
	
	
//	//### aus IConfigStarterLocalJGIT	
//	@Override
//	public String getRepositoryLocalBaseDefault() throws ExceptionZZZ {
//		return "."; //Das eigene Projekt-Verzeichnis als Default
//	}
//	
//	@Override
//	public File getRepositoryLocalBaseDirectoryDefault() throws ExceptionZZZ {
//		return new File(this.getRepositoryLocalBaseDefault()); //Also das eigene Projekt-Verzeichnis als Default
//	}
//	
//	@Override
//	public String readRepositoryLocal() throws ExceptionZZZ {		
//		String sReturn = null;
//		main:{
//			GetOptZZZ objOpt = this.getOptObject();
//			if(objOpt==null) break main;
//			if(objOpt.getFlag("isLoaded")==false) break main;
//			
//			sReturn = objOpt.readValue("rl");
//			if(sReturn==null){
//				sReturn = this.getRepositoryLocalBaseDefault();
//			}
//		}//end main:		
//		return sReturn;
//	}		
//	
//	//++++++++++++++++++++++++++++++++++++++++++++
//	@Override
//	public String getRepositoryProjectNameDefault() throws ExceptionZZZ {		
//		return this.getProjectName(); //Einfach als Default, kann ja überschrieben werden.
//	}
//		
//	//++++++++++++++++++++++++++++++++++++++
//	@Override
//	public String readRepositoryProjectName() throws ExceptionZZZ {
//		String sReturn = null;
//		main:{
//			GetOptZZZ objOpt = this.getOptObject();
//			if(objOpt==null) break main;
//			if(objOpt.getFlag("isLoaded")==false) break main;
//			
//			String sProject = objOpt.readValue("project");
//			if(StringZZZ.isEmpty(sProject)) {
//				sProject = this.getRepositoryProjectNameDefault();				
//			}
//			
//			sReturn = sProject;
//		}//end main:		
//		return sReturn;
//	}
//	
//	//++++++++++++++++++++++++++++++++++++++++++++
//	@Override
//	public String getRepositoryBranchDefault() throws ExceptionZZZ {		
//		return IConfigJGIT.sBRANCH_DEFAULT;
//	}
//
//	@Override
//	public String getRepositoryBranchAll() throws ExceptionZZZ {		
//		return IConfigJGIT.sBRANCH_ALL;
//	}
//	
//	//++++++++++++++++++++++++++++++++++++++
//	@Override
//	public String readRepositoryBranch() throws ExceptionZZZ {
//		String sReturn = null;
//		main:{
//			GetOptZZZ objOpt = this.getOptObject();
//			if(objOpt==null) break main;
//			if(objOpt.getFlag("isLoaded")==false) break main;
//			
//			String sBranch = objOpt.readValue("branch");
//			if(StringZZZ.isEmpty(sBranch)) {
//				sBranch = this.getRepositoryBranchAll();				
//			}
//			sReturn = sBranch;
//		}//end main:		
//		return sReturn;
//	}
//		
//	//++++++++++++++++++++++++++++++++++++++++++++++++	
//	@Override
//	public String readRepositoryRemoteAlias() throws ExceptionZZZ {
//		String sReturn = null;
//		main:{
//			GetOptZZZ objOpt = this.getOptObject();
//			if(objOpt==null) break main;
//			if(objOpt.getFlag("isLoaded")==false) break main;
//			
//			sReturn = objOpt.readValue("rra");
//			if(sReturn==null){
//				sReturn = this.getRepositoryRemoteAliasDefault();
//			}
//		}//end main:		
//		return sReturn;
//	}
//	@Override
//	public String getRepositoryRemoteAliasDefault() throws ExceptionZZZ {
//		return IConfigJGIT.sREPOSITORY_REMOTE_ALIAS_DEFAULT;
//	}
}
