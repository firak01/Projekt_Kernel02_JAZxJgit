package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.GetOptZZZ;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.manage.protocol.https.JgitRepositoryManagerHTTPS;

public class ConfigStarterRemote4TestHTTPS_BonAny  extends ConfigStarterRemote4TestJGIT_XonAny{
	
	public ConfigStarterRemote4TestHTTPS_BonAny() throws ExceptionZZZ {
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

	

	
	//### aus IConfigWithAuthentificationJGIT
	@Override
	public String readConnectionType() throws ExceptionZZZ {
		return JgitRepositoryManagerHTTPS.sPROTOCOL;
	}
	
	@Override
	public String readPersonalAccessToken() throws ExceptionZZZ{
		return System.getenv("sPATZZZ");
	}
	
	//### aus IConfigStarterLocalJGIT	

}
