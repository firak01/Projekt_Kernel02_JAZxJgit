package use.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigStarterJGIT;

public interface IJgitStarterCommit {
//	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = IConfigStarterJGIT.sREPOSITORY_REMOTE_ALIAS_DEFAULT;
	
	//+++ Per Argument übergebene Werte
	public String getRepositoryProject() throws ExceptionZZZ;
	public void setRepositoryProject(String sRepositoryProject) throws ExceptionZZZ;
	
	public String getRepositoryLocalBase() throws ExceptionZZZ;
	public void setRepositoryLocalBase(String sRepositoryBaseLocal) throws ExceptionZZZ;

	//+++ Errechnete Werte
	public String getRepositoryLocalTotal() throws ExceptionZZZ;
	public void setRepositoryTotalLocal(String sRepositoryTotalLocal) throws ExceptionZZZ;

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
	public boolean commitit(Git git, String sComment) throws ExceptionZZZ;
	
	//+++ Defaultkommentar, wenn keiner übergeben wurde. Ggfs. mit Besonderheit, z.B. beim Auflösen von Konflikten
	public String getCommentCommit() throws ExceptionZZZ;
	public void setCommentCommit(String sCommentCommit) throws ExceptionZZZ;
	
	public String getCommentCommitDefault() throws ExceptionZZZ;
	public void setCommentCommitDefault(String sCommentCommitDefault) throws ExceptionZZZ;
}
