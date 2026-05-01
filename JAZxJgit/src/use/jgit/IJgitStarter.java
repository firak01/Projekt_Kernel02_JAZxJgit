package use.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigStarterJGIT;

public interface IJgitStarter {
	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = IConfigStarterJGIT.sREPOSITORY_REMOTE_ALIAS_DEFAULT;
	
	//+++ Per Argument übergebene Werte
	public String getRepositoryProject() throws ExceptionZZZ;
	public void setRepositoryProject(String sRepositoryProject) throws ExceptionZZZ;
	
	public String getRepositoryBaseLocal() throws ExceptionZZZ;
	public void setRepositoryBaseLocal(String sRepositoryBaseLocal) throws ExceptionZZZ;

	public String getRepositoryRemoteAlias() throws ExceptionZZZ;
	public void setRepositoryRemoteAlias(String sRepositoryRemoteAlias) throws ExceptionZZZ;
	
	public String getRepositoryRemoteHost() throws ExceptionZZZ;
	public void setRepositoryRemoteHost(String sRepositoryRemoteHost) throws ExceptionZZZ;
	
	public String getRepositoryRemoteAccount() throws ExceptionZZZ;
	public void setRepositoryRemoteAccount(String sRepositoryRemoteAccount) throws ExceptionZZZ;
	
	public String getConnectionType() throws ExceptionZZZ;
	public void setConnectionType(String sConnectionType) throws ExceptionZZZ;
	
	//+++ Errechnete Werte
	public String getRepositoryBaseRemote() throws ExceptionZZZ;
	public void setRepositoryBaseRemote(String sRepositoryBaseRemote) throws ExceptionZZZ;

	public String searchRepositoryRemote() throws ExceptionZZZ;
	public String searchRepositoryRemote(String sRepositoryRemoteAlias) throws ExceptionZZZ;
	
	public String getRepositoryTotalRemote() throws ExceptionZZZ;
	public void setRepositoryTotalRemote(String sRepositoryTotalRemote) throws ExceptionZZZ;
	
	public String getRepositoryTotalLocal() throws ExceptionZZZ;
	public void setRepositoryTotalLocal(String sRepositoryTotalLocal) throws ExceptionZZZ;
	
	
	public String computeRepositoryBaseRemote() throws ExceptionZZZ;
	public String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ;
	
	public String computeRepositoryRemoteUrl() throws ExceptionZZZ;
	public String computeRepositoryRemoteUrl(String sRepositoryBaseRemote, String sRepositoryProject) throws ExceptionZZZ;
	
	//+++ Arbeiten mit dem Repository-Object, etc.
	public boolean configureRepositoryLocal(IConfigStarterJGIT objConfig) throws ExceptionZZZ;
	
	
	//+++ Arbeit mit dem GitObject, etc.
	public Git getGitObject() throws ExceptionZZZ;
	public void setGitObject(Git git) throws ExceptionZZZ;
		
	public void addFileTrackedChanged() throws ExceptionZZZ;
	public void addFileTrackedChanged(Git git) throws ExceptionZZZ;
	public void addFileUntracked() throws ExceptionZZZ;
	public void addFileUntracked(Git git) throws ExceptionZZZ;
	
	public boolean configureGit() throws ExceptionZZZ;	
	
	public boolean commitit(IConfigStarterJGIT objConfig) throws ExceptionZZZ;	
	public boolean commitit(Git git) throws ExceptionZZZ;
	
	public boolean commitAndPushit(IConfigStarterJGIT objConfig) throws ExceptionZZZ;
	
	public boolean fetchit(IConfigStarterJGIT objConfig) throws ExceptionZZZ;	
	public boolean fetchit(Git git) throws ExceptionZZZ;
	
	
	public boolean pushit(IConfigStarterJGIT objConfig) throws ExceptionZZZ;
	public boolean pushit(Git git) throws ExceptionZZZ;
	
	public boolean pullit(IConfigStarterJGIT objConfig) throws ExceptionZZZ;
	public boolean pullit(Git git) throws ExceptionZZZ;
	
	
	
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ;
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ;
	
	
	
}
