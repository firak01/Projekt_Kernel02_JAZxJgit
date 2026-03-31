package use.tool.jgit.https;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.tool.jgit.IJgitStarter;

public interface IJgitStarterHTTPS extends IJgitStarterHTTPSEnabled, IJgitStarter{
	public void setPersonalAccessToken(String sPat) throws ExceptionZZZ;
	public String getPersonalAccessToken() throws ExceptionZZZ;
	
	public boolean pushit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ;	               
}
