package use.jgit.protcol.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitStarterRemote;
import use.jgit.resolve.IJgitResolverEnabled;

public interface IJgitStarterGIT extends IJgitStarterGITEnabled, IJgitStarterRemote{
	
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote) throws ExceptionZZZ, TransportException, CheckoutConflictException;
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch) throws ExceptionZZZ, TransportException, CheckoutConflictException;
	public boolean pullitResolveCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy) throws ExceptionZZZ;
	//public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ;
}
