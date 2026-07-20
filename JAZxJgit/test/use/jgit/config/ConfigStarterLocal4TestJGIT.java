package use.jgit.config;

import basic.zBasic.ExceptionZZZ;

public class ConfigStarterLocal4TestJGIT  extends AbstractConfigStarterAuthentificatedJGIT{
	
	public ConfigStarterLocal4TestJGIT() throws ExceptionZZZ {
		super();		
	}
	
	//Merke: Für die JUnit Tests werden die Argumente nicht über die Kommandozeile übergeben, sondern sind hier "hart" verdrahtet.
	//       Darum sind die ...Default... Methoden hier überflüssig.
	
	//### aus IConfigZZZ
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
	
	//++++++++++++++++++++++++++++++++++++++++++++++++
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
		return "C:\\1fgl\\repo\\EclipseOxygen_V02";
	}		

	//++++++++++++++++++++++++++++++++++++++
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

	//++++++++++++++++++++++++++++++++++++++++++				

	@Override
	public String readComment() throws ExceptionZZZ {
		return "Comment by JUnitTest";
	}
			
	//##########################################
	//### aus IConfigStarterLocalJGIT	
	
	//+++++++++++++++++++++++++++++++++++++
	@Override
	public String readActionStatus() throws ExceptionZZZ {
		return null;
	}
	
	
	@Override
	public String readActionCommit() throws ExceptionZZZ {
		return null;
	}
	
	@Override
	public String readActionFetch() throws ExceptionZZZ {
		return null;
	}
}
