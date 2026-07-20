package use.jgit.config;

import java.io.File;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.config.ConfigUtilZZZ;
import basic.zBasic.util.abstractList.ListUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.GetOptZZZ;
import basic.zKernel.config.help.IKernelConfigHelpLineZZZ;
import use.jgit.starter.protocol.git.JgitStarterGIT;
import use.jgit.starter.protocol.https.JgitStarterHTTPS;
import use.jgit.starter.protocol.ssh.JgitStarterSSH;


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
public abstract class AbstractConfigStarterAuthentificatedJGIT extends AbstractConfigStarterLocalJGIT implements IConfigWithAuthentificationJGIT{
	private static final long serialVersionUID = 1229381223690096548L;
		
	public AbstractConfigStarterAuthentificatedJGIT() throws ExceptionZZZ{
		super();
	}
	public AbstractConfigStarterAuthentificatedJGIT(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
			

	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
	
	//### aus IConfigJGIT
	
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

	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
	//    LOKALE KONFIGURATION
	//++++++++++++++++++++++++++++++++++++++++++
		
	@Override
	public String readConnectionType() throws ExceptionZZZ{
		String sReturn = null;
		main:{
			boolean bReturn = false;
			
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			bReturn = this.isConnectionTypeGIT();
			if(bReturn) {
				sReturn = JgitStarterGIT.sPROTOCOL;
				break main;
			}
			
			bReturn = this.isConnectionTypeHTTPS();
			if(bReturn) {
				sReturn = JgitStarterHTTPS.sPROTOCOL;
				break main;
			}
			
			bReturn = this.isConnectionTypeSSH();
			if(bReturn) {
				sReturn = JgitStarterSSH.sPROTOCOL;
				break main;
			}
			
			sReturn = this.getConnectionTypeDefault();			
		}//end main:		
		return sReturn;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++
		@Override
		public String getConnectionTypeDefault() throws ExceptionZZZ{
			return "ssh";
		}
		
		
		@Override
		public boolean isConnectionTypeSSH() throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				GetOptZZZ objOpt = this.getOptObject();
				if(objOpt==null) break main;
				if(objOpt.getFlag("isLoaded")==false) break main;
				
				String sReturn = objOpt.readValue(JgitStarterGIT.sPROTOCOL);	
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
				
				String sReturn = objOpt.readValue(JgitStarterHTTPS.sPROTOCOL);
				if(!StringZZZ.isEmpty(sReturn)) bReturn = true;
				
			}//end main:		
			return bReturn;
		}
		
		@Override
		public boolean isConnectionTypeGIT() throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				GetOptZZZ objOpt = this.getOptObject();
				if(objOpt==null) break main;
				if(objOpt.getFlag("isLoaded")==false) break main;
				
				String sReturn = objOpt.readValue(JgitStarterGIT.sPROTOCOL);	
				if(!StringZZZ.isEmpty(sReturn)) bReturn = true;
				
			}//end main:		
			return bReturn;
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
		
		//######################################
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
}
