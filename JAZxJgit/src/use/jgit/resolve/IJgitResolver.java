package use.jgit.resolve;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitStarter;
import use.jgit.config.IConfigResolverJGIT;
import use.jgit.config.IConfigStarterJGIT;

public interface IJgitResolver {
	
	public boolean conflictit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	public boolean conflictit(String sFilepath) throws ExceptionZZZ;	
}
