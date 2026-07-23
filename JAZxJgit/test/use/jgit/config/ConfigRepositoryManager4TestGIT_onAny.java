package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import use.jgit.manager.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.manager.protocol.https.JgitRepositoryManagerHTTPS;

/** Hole die Informationen aus den Umgebungsvariablen beim Eclipse Start.
 *  Dies ist u.a. wichtig, weil sPAT (Personal Access Token) hier nicht hart coded stehen darf,
 *  dies wird beim Hochladen nach GitHub verboten und führt zu Probleme.
 * @author Fritz Lindhauer
 *
 */
public class ConfigRepositoryManager4TestGIT_onAny  extends AbstractConfigRepositoryManagerJGIT{
	private static final long serialVersionUID = 662451230649662545L;

	public ConfigRepositoryManager4TestGIT_onAny() throws ExceptionZZZ {
		super();		
	}
	
	public ConfigRepositoryManager4TestGIT_onAny(String[] saArg) throws ExceptionZZZ {
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
//	@Override
//	public String getProjectName() throws ExceptionZZZ {
//		return "Test_repo_JAZxJgit";
//	}
	
	@Override
	public String readProjectName() throws ExceptionZZZ {
		return "Test_repo_JAZxJgit";
	}
	
	//### aus IConfigStarterLocalJGIT	
	@Override
	public String readRepositoryLocalBaseDirectory() throws ExceptionZZZ {		
		return System.getenv("sRLZZZ");
	}		

	@Override
	public String readRepositoryProjectName() throws ExceptionZZZ {
		return "1fgl_Test_repo_JAZxJgit";
	}
	
	
	//++++++++++++++++++++++++++++++++++++++
	@Override
	public String readRepositoryBranch() throws ExceptionZZZ {
		return "master";
	}

	
	//++++++++++++++++++++++++++++++++++++++++++++++++	
	@Override
	public String readRepositoryRemoteAlias() throws ExceptionZZZ {
		return "origin";
	}
	
	
	//### aus IConfigWithAuthentificationJGIT
	@Override
	public String readConnectionType() throws ExceptionZZZ {
		return JgitRepositoryManagerGIT.sPROTOCOL;
	}
	
	@Override
	public String readRepositoryRemoteHost() throws ExceptionZZZ {
		return System.getenv("sRRHZZZ");
	}
	
	@Override
	public String readRepositoryRemoteAccount() throws ExceptionZZZ {
		return System.getenv("sRRACZZZ");
	}
	
	//### aus IConfigRepositoryManagerJGIT
	@Override
	public boolean isRepositoryBare() throws ExceptionZZZ {
			return true;
	}
}
