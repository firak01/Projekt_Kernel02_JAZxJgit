package use.tool.jgit.protocol.https;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.tool.jgit.IJgitStarter;

public interface IJgitStarterHTTPS extends IJgitStarterHTTPSEnabled, IJgitStarter{
	public void setPersonalAccessToken(String sPat) throws ExceptionZZZ;
	public String getPersonalAccessToken() throws ExceptionZZZ;
	
	public boolean pushit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	
	
	
	//+++++++ PULL: Welche Methode verwendet wird, wird über ein Flag gesteuert
	//a) pull ohne Mergekonflikte abzufangen, über alle Branches (wird aber nicht verwendet)
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	
	//b) pull ohne MergeKonflikte abzufangen, über einen Branch
	public boolean pullitSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ;
	
	//c) pull, die Konfliktdateien werden gezielt zurückgesetzt
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ;
	
	//d) pull mit dem Automatuschen auflösen von Mergekonflikten, ggfs. mit Strategie, die auch per Flag gesteuert wird
	public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ;	               
}
