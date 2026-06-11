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
public abstract class AbstractConfigStarterCommitJGIT extends AbstractConfigJGIT implements IConfigStarterCommitJGIT{
	
	public AbstractConfigStarterCommitJGIT() throws ExceptionZZZ{
		super();
	}
	public AbstractConfigStarterCommitJGIT(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
			
	
	
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
		
	
	//### aus IConfigJGIT
	
		
	//### aus IConfigStarterGIT
	@Override
	public String readActionStatus() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("status");			
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
	
	//++++++++++++++++++++++++++++++++++++++++++++++++
}
