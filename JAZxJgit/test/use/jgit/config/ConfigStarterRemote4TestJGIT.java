package use.jgit.config;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.GetOptZZZ;
import use.jgit.config.AbstractConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;

public class ConfigStarterRemote4TestJGIT  extends AbstractConfigStarterRemoteJGIT{
	
	public ConfigStarterRemote4TestJGIT() throws ExceptionZZZ {
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
		return "1fgl_Test_repo_JAZxJgit";
	}
	
	
	//++++++++++++++++++++++++++++++++++++++
	@Override
	public String readRepositoryBranch() throws ExceptionZZZ {
		return "master";
	}

	//++++++++++++++++++++++++++++++++++++++++++				

	@Override
	public String readComment() throws ExceptionZZZ {
		return "Comment by JUnitTest";
	}
		
	//++++++++++++++++++++++++++++++++++++++++++++++++	
	@Override
	public String readRepositoryRemoteAlias() throws ExceptionZZZ {
		return null;
	}
		
	//########################################
	//### aus IConfigStarterRemoteJGIT
	@Override
	public String readActionPull() throws ExceptionZZZ {
		return null;
	}

	@Override
	public String readActionPush() throws ExceptionZZZ {
		return null;
	}

	@Override
	public String readActionCommitPush() throws ExceptionZZZ {
		return null;
	}

	@Override
	public String getConnectionTypeDefault() throws ExceptionZZZ {
		return null;
	}

	@Override
	public String readConnectionType() throws ExceptionZZZ {
		return null;
	}

	@Override
	public boolean isConnectionTypeSSH() throws ExceptionZZZ {
		return false;
	}

	@Override
	public boolean isConnectionTypeHTTPS() throws ExceptionZZZ {
		return false;
	}

	@Override
	public boolean isConnectionTypeGIT() throws ExceptionZZZ {
		return false;
	}

	@Override
	public String readPersonalAccessToken() throws ExceptionZZZ {
		return null;
	}

	@Override
	public String readRepositoryRemoteHost() throws ExceptionZZZ {
		return null;
	}

	@Override
	public String readRepositoryRemoteAccount() throws ExceptionZZZ {
		return null;
	}
}
