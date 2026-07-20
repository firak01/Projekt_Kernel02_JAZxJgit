package use.jgit.config;

import basic.zBasic.ExceptionZZZ;

public class ConfigRepositoryManager4TestJGIT_onTUBAF  extends AbstractConfigRepositoryManagerJGIT{
	private static final long serialVersionUID = 662451230649662545L;

	public ConfigRepositoryManager4TestJGIT_onTUBAF() throws ExceptionZZZ {
		super();		
	}
	
	public ConfigRepositoryManager4TestJGIT_onTUBAF(String[] saArg) throws ExceptionZZZ {
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
		return "C:\\HIS-Workspace\\1fgl\\repo\\EclipseOxygen";
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
		return "git";
	}
	
	@Override
	public String readRepositoryRemoteHost() throws ExceptionZZZ {
		return "github.com";
	}
	
	@Override
	public String readRepositoryRemoteAccount() throws ExceptionZZZ {
		return "firak01";
	}
	
	//### aus IConfigRepositoryManagerJGIT
	@Override
	public boolean isRepositoryBare() throws ExceptionZZZ {
			return true;
	}
}
