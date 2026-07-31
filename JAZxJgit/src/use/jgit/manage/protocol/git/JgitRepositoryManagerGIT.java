package use.jgit.manage.protocol.git;

import basic.zBasic.ExceptionZZZ;
import use.jgit.AbstractJgitRepositoryManagerZZZ;
import use.jgit.IJgitRepositoryManagerJGIT;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.start.protocol.git.JgitStarterGIT;
import use.jgit.util.JgitUtilGIT;

public class JgitRepositoryManagerGIT extends AbstractJgitRepositoryManagerZZZ implements IJgitRepositoryManagerJGIT{
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
		return sPROTOCOL;
	}
	//!!! Kein Setter
	
	@Override
	public String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ{
		return JgitUtilGIT.computeRepositoryUrlBaseGIT(sHost, sAccount);
	}
}
