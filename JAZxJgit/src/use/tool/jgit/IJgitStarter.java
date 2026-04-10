package use.tool.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.tool.jgit.IConfigJGIT;

public interface IJgitStarter {

	public String getRepositoryProject() throws ExceptionZZZ;
	public void setRepositoryProject(String sRepositoryProject) throws ExceptionZZZ;
	
	public String getRepositoryBaseLocal() throws ExceptionZZZ;
	public void setRepositoryBaseLocal(String sRepositoryBaseLocal) throws ExceptionZZZ;
	
	public String getRepositoryBaseRemote() throws ExceptionZZZ;
	public void setRepositoryBaseRemote(String sRepositoryBaseRemote) throws ExceptionZZZ;
	
	public String getRepositoryRemoteAlias() throws ExceptionZZZ;
	public void setRepositoryRemoteAlias(String sRepositoryRemoteAlias) throws ExceptionZZZ;
	
	public String searchRepositoryRemote() throws ExceptionZZZ;
	public String searchRepositoryRemote(String sRepositoryRemoteAlias) throws ExceptionZZZ;
	
	public String getRepositoryTotalRemote() throws ExceptionZZZ;
	public void setRepositoryTotalRemote(String sRepositoryTotalRemote) throws ExceptionZZZ;
	
	public String getRepositoryTotalLocal() throws ExceptionZZZ;
	public void setRepositoryTotalLocal(String sRepositoryTotalLocal) throws ExceptionZZZ;
	
	public String computeRepositoryRemoteUrl(String sRepositoryBaseRemote, String sRepositoryProject) throws ExceptionZZZ;
	
	public boolean configureGit() throws ExceptionZZZ;	
	public boolean pushit(IConfigJGIT objConfig) throws ExceptionZZZ;
	public boolean pullit(IConfigJGIT objConfig) throws ExceptionZZZ;
	
	//+++ Arbeit mit dem GitObject, etc.
	public Git getGitObject() throws ExceptionZZZ;
	public void setGitObject(Git git) throws ExceptionZZZ;
	
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ;
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ;
	
	
	
}
