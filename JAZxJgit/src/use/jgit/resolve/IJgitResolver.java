package use.jgit.resolve;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitStarter;
import use.jgit.config.IConfigResolverJGIT;
import use.jgit.config.IConfigStarterJGIT;

public interface IJgitResolver {
	
	public boolean conflictit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	public boolean conflictit(String sFilepath, String sComment) throws ExceptionZZZ;	
	public boolean conflictit(String sFilepath) throws ExceptionZZZ;
	
	//Normalerweise reicht es nicht aus den Konflikt aus der Datei zu entfernen.
	//Es muss auch noch ein Commit gemacht werden.
	public boolean conflictCommitit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	public boolean conflictCommitit(String sFilepath, String sComment) throws ExceptionZZZ;	
	public boolean conflictCommitit(String sFilepath) throws ExceptionZZZ;
}
