package use.jgit.manager.protocol.git;

import basic.zBasic.ExceptionZZZ;
import use.jgit.AbstractJgitRepositoryManagerZZZ;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.starter.protocol.git.JgitStarterGIT;
import use.jgit.util.JgitUtilGIT;

public class JgitRepositoryManagerGIT extends AbstractJgitRepositoryManagerZZZ{
	public static final String sPROTOCOL="git";
	
	public JgitRepositoryManagerGIT() {
		super();
	}
	
	//### aus IJgitStarterAuthentificated
	@Override 
	public String getRepositoryRemoteProtocol() throws ExceptionZZZ {
		return JgitStarterGIT.sPROTOCOL;
	}
	//!!! Kein Setter
	
	@Override
	public String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ{
		return JgitUtilGIT.computeRepositoryUrlBaseGIT(sHost, sAccount);
	}
}
