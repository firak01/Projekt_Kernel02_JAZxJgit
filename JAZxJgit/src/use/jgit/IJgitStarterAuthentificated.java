package use.jgit;

import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;

public interface IJgitStarterAuthentificated  extends IJgitStarterLocal{
	
	//+++ Arbeit mit dem GitObject, etc.	
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ;
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ;	
	
	public void setPersonalAccessToken(String sPat) throws ExceptionZZZ;
	public String getPersonalAccessToken() throws ExceptionZZZ;
	
	
	//+++ In der Klasse definierte Werte
	public String getRepositoryRemoteProtocol() throws ExceptionZZZ;
		
	//+++ Per Argument übergebene Werte	
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
		
	public String computeRepositoryBaseRemote() throws ExceptionZZZ;
	public String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ;
	
	public String computeRepositoryRemoteUrl() throws ExceptionZZZ;
	public String computeRepositoryRemoteUrl(String sRepositoryBaseRemote, String sRepositoryProject) throws ExceptionZZZ;
	public String computeRepositoryRemoteUrl(String sRepositoryRemoteHost, String sRepositoryRemoteAccount, String sRepositoryProject) throws ExceptionZZZ;
	
	
	//+++ Konfiguration mit Konfigurationsobjekt
	public boolean configureGit(IConfigWithAuthentificationJGIT objConfig) throws ExceptionZZZ;
	public boolean configureRepositoryLocal(IConfigWithAuthentificationJGIT objConfig) throws ExceptionZZZ;
	
	//+++ Zusätzliche Konfiguration, z.B isBareRepository
	//    Diese wird pro Klasse überschreibbar gemacht.
	public boolean configureGitCustom(IConfigWithAuthentificationJGIT objConfig) throws ExceptionZZZ;	
}
