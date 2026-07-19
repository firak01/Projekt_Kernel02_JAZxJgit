package use.jgit.config;

import basic.zBasic.ExceptionZZZ;

public class ConfigRepositoryManager4TestJGIT  extends AbstractConfigRepositoryManagerJGIT{
	private static final long serialVersionUID = 662451230649662545L;

	public ConfigRepositoryManager4TestJGIT() throws ExceptionZZZ {
		super();		
	}
	
	//Merke: Für die JUnit Tests werden die Argumente nicht über die Kommandozeile übergeben, sondern sind hier "hart" verdrahtet.
	//       Darum sind die ...Default... Methoden hier überflüssig.
	
	//##########################################
	//### aus IConfigStarterLocalJGIT	
	@Override
	public boolean isRepositoryBare() throws ExceptionZZZ {
			return true;
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
	//    LOKALE KONFIGURATION
	//++++++++++++++++++++++++++++++++++++++++++
	
	//### aus IConfigStarterLocalJGIT	
	@Override
	public String readRepositoryLocal() throws ExceptionZZZ {				
		return "C:\\1fgl\\repo\\EclipseOxygen_V02\\1fgl_Test_repo_JAZxJgit";
	}		

	//++++++++++++++++++++++++++++++++++++++
	@Override
	public String readRepositoryProjectName() throws ExceptionZZZ {
		return "Test_repo_JAZxJgit";
	}
	
	
	//++++++++++++++++++++++++++++++++++++++
	@Override
	public String readRepositoryBranch() throws ExceptionZZZ {
		return "master";
	}

	
	//++++++++++++++++++++++++++++++++++++++++++++++++	
	@Override
	public String readRepositoryRemoteAlias() throws ExceptionZZZ {
		return null;
	}
		
	
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
}
