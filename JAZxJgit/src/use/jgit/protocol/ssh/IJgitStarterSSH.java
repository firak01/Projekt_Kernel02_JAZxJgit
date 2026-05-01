package use.jgit.protocol.ssh;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitStarter;

public interface IJgitStarterSSH extends IJgitStarterSSHEnabled, IJgitStarter{
	
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote) throws ExceptionZZZ;
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch) throws ExceptionZZZ;
	//public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ;
}
