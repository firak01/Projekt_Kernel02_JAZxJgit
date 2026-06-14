package use.jgit.protcol.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitStarterRemote;
import use.jgit.resolve.IJgitResolverEnabled;

public interface IJgitStarterGIT extends IJgitStarterGITEnabled, IJgitStarterRemote{
	
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote) throws ExceptionZZZ;
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch) throws ExceptionZZZ;
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy) throws ExceptionZZZ;
	//public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ;
}
