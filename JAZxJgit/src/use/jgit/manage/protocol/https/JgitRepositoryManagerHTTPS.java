package use.jgit.manage.protocol.https;

import basic.zBasic.ExceptionZZZ;
import use.jgit.AbstractJgitRepositoryManagerZZZ;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.start.protocol.git.JgitStarterGIT;
import use.jgit.start.protocol.https.JgitStarterHTTPS;
import use.jgit.util.JgitUtilGIT;
import use.jgit.util.JgitUtilHTTPS;

public class JgitRepositoryManagerHTTPS extends AbstractJgitRepositoryManagerZZZ{
	public static final String sPROTOCOL="https";
	
	public JgitRepositoryManagerHTTPS() throws ExceptionZZZ{
		super();
	}
	
	public JgitRepositoryManagerHTTPS(IConfigRepositoryManagerJGIT objConfig) throws ExceptionZZZ {
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
		return JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sHost, sAccount);
	}
}
