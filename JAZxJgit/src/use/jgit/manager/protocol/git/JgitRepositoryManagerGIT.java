package use.jgit.manager.protocol.git;

import basic.zBasic.ExceptionZZZ;
import use.jgit.AbstractJgitRepositoryManagerZZZ;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.starter.protocol.git.JgitStarterGIT;
import use.jgit.util.JgitUtilGIT;

public class JgitRepositoryManagerGIT extends AbstractJgitRepositoryManagerZZZ{
	public static final String sPROTOCOL="git";
	
	public JgitRepositoryManagerGIT() throws ExceptionZZZ{
		super();
	}
	
	public JgitRepositoryManagerGIT(IConfigRepositoryManagerJGIT objConfig) throws ExceptionZZZ {
		super();
		this.setConfiguration(objConfig);
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
