package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import use.jgit.manage.protocol.https.JgitRepositoryManagerHTTPS;

/** Hole die Informationen aus den Umgebungsvariablen beim Eclipse Start.
 *  Dies ist u.a. wichtig, weil sPAT (Personal Access Token) hier nicht hart coded stehen darf,
 *  dies wird beim Hochladen nach GitHub verboten und führt zu Probleme.
 * @author Fritz Lindhauer
 *
 */
public class ConfigRepositoryManager4TestHTTPS_onAnyB  extends ConfigRepositoryManager4TestGIT_onAny{
	private static final long serialVersionUID = 662451230649662545L;

	public ConfigRepositoryManager4TestHTTPS_onAnyB() throws ExceptionZZZ {
		super();		
	}
	
	public ConfigRepositoryManager4TestHTTPS_onAnyB(String[] saArg) throws ExceptionZZZ {
		super(saArg); 
	} 
	
	//Merke: Für die JUnit Tests werden die Argumente nicht über die Kommandozeile übergeben, sondern sind hier "hart" verdrahtet.
	//  	 Darum sind die Pattern, Argument und ...Default... Methoden hier überflüssig.
	
	@Override
	public String getPatternStringDefault() throws ExceptionZZZ {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getArgumentArrayDefault() throws ExceptionZZZ {
		// TODO Auto-generated method stub
		return null;
	}

	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
	//    LOKALE KONFIGURATION
	//++++++++++++++++++++++++++++++++++++++++++
	

	//### IConfigProjectZZZ

	
	//### aus IConfigStarterLocalJGIT	

	
	//### aus IConfigWithAuthentificationJGIT
	@Override
	public String readConnectionType() throws ExceptionZZZ {
		return JgitRepositoryManagerHTTPS.sPROTOCOL;
	}
	
	
	//### aus IConfigRepositoryManagerJGIT
	
}
