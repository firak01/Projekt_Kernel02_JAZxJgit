package use.jgit.protocol.https;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitStarterRemote;
import use.jgit.resolve.IJgitResolverEnabled;
import use.jgit.util.JgitUtilHTTPS;

public interface IJgitStarterHTTPS extends IJgitStarterHTTPSEnabled, IJgitStarterRemote{
	public void 	setPersonalAccessToken(String sPat) throws ExceptionZZZ;
	public String getPersonalAccessToken() throws ExceptionZZZ;
	
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ;
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ;
	
	public boolean pushit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	
	
	
	//+++++++ PULL: Welche Methode verwendet wird, wird über ein Flag gesteuert
	//a) pull ohne Mergekonflikte abzufangen, über alle Branches (wird aber nicht verwendet)
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	
	//b) pull ohne MergeKonflikte abzufangen, über einen ganz konkreten Branch
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ, TransportException, CheckoutConflictException;
	
	//c) pull, die Konfliktdateien werden gezielt zurückgesetzt
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy) throws ExceptionZZZ;
	
	//d) pull mit dem Automatischen auflösen von Mergekonflikten, ggfs. mit Strategie, die auch per Flag gesteuert wird
	public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumstrategy) throws ExceptionZZZ;	               
}
