package use.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;

public interface IJgitStarterRemote extends IJgitStarterLocal {
	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = IConfigStarterRemoteJGIT.sREPOSITORY_REMOTE_ALIAS_DEFAULT;
	
	//+++ In der Klasse definerte Werte
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
	
	//+++ Arbeiten mit dem REMOTE Repository-Object, etc.
	public boolean configureGit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;	
	
	//+++ Arbeit mit dem GitObject, etc.	
	//nur HTTPS+SSH Objekte
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ;
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ;
	
	public boolean commitPushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;
	public boolean commitPushit(IConfigStarterRemoteJGIT objConfig, String sComment) throws ExceptionZZZ;
	
	public boolean fetchit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;	
	public boolean fetchit(Git git) throws ExceptionZZZ;
	
	public boolean pushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;
	public boolean pushit(Git git) throws ExceptionZZZ;
	
	public boolean pullit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ, TransportException, CheckoutConflictException;
	public boolean pullit(Git git) throws ExceptionZZZ, TransportException, CheckoutConflictException;	
}
