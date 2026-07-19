package use.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;

public interface IJgitStarterLocal{
	//+++ Argumente, ggfs. aus einer Kommandozeilenübergabe
	public String getRepositoryProject() throws ExceptionZZZ;
	public void setRepositoryProject(String sRepositoryProject) throws ExceptionZZZ;
	
	public String getRepositoryBranch() throws ExceptionZZZ;
	public void setRepositoryBranch(String sRepositoryBranch) throws ExceptionZZZ;
	
	public String getRepositoryLocalBase() throws ExceptionZZZ;
	public void setRepositoryLocalBase(String sRepositoryBaseLocal) throws ExceptionZZZ;

	public String getRepositoryRemoteAlias() throws ExceptionZZZ; //zwar REMOTE, wird aber zum Setzen von den Einträgen in der lokalen config-Datei gebraucht
	public void setRepositoryRemoteAlias(String sRepositoryRemoteAlias) throws ExceptionZZZ;
		
	//+++ Errechnete Werte
	public String getRepositoryLocalTotal() throws ExceptionZZZ;
	public void setRepositoryTotalLocal(String sRepositoryTotalLocal) throws ExceptionZZZ;

	public String getRepositoryTotalRemote() throws ExceptionZZZ; //zwar REMOTE, wird aber zum Setzen von den Einträgen in der lokalen config-Datei gebraucht	
	public void setRepositoryTotalRemote(String sRepositoryTotalRemote) throws ExceptionZZZ;
	
	
	
	//+++ Arbeit mit dem GitObject, etc.
	public boolean createGitObject() throws ExceptionZZZ;
	public Git getGitObject() throws ExceptionZZZ;
	public void setGitObject(Git git) throws ExceptionZZZ;
		
	//+++++++++++++++++++++++++++++++++++
	//+++ sollte eine Konfiguration ohne Konfigurationsobjekt gewünscht sein.
	//    dann muss alles aus den Properties des Objekts selbst ausgelsen werden.
	public boolean configureGit() throws ExceptionZZZ;
	public boolean configureRepositoryLocal() throws ExceptionZZZ;

	//+++ Konfiguration mit Konfigurationsobjekt
	public boolean configureGit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ;
	public boolean configureRepositoryLocal(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ;
	
	//+++ Zusätzliche Konfiguration, z.B isBareRepository
	//    Diese wird pro Klasse überschreibbar gemacht.
	public boolean configureGitCustom(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ;
	public boolean configureGitCustom(InitCommand objInitCommand) throws ExceptionZZZ;
	

	//Alternative 1: Verwendet unvollständiges status.getUncommittedChanges()
	public void addFileTrackedChanged() throws ExceptionZZZ;
	public void addFileTrackedChanged(Git git) throws ExceptionZZZ;
	public void addFileUntracked() throws ExceptionZZZ;
	public void addFileUntracked(Git git) throws ExceptionZZZ;
	
	//Alternative 2: Verwendet detailierteres. Auch für gelöschte Dateien, eigene ServiceKlassen
	public void addFileStageAll(Git git) throws ExceptionZZZ;
	
	
	//+++ Arbeiten mit dem LOCALEN Repository-Object, etc.
	//jetzt in IJgitRepository im dem entsprechenden Interface public boolean configureGit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ;	
	//jetzt in IJgitRepository im dem entsprechenden Interface public boolean configureRepositoryLocal(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ;
	
    //+++ Arbeiten mit dem GIT-Objekt
	public boolean statusit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ;
	public boolean statusit(Git git) throws ExceptionZZZ;
	
	public boolean commitit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ;	
	public boolean commitit(IConfigStarterLocalJGIT objConfig, String sComment) throws ExceptionZZZ;
	public boolean commitit(Git git) throws ExceptionZZZ;
	public boolean commitit(Git git, String sComment) throws ExceptionZZZ;
	
	//+++ Für Commit: Defaultkommentar, wenn keiner übergeben wurde. Ggfs. mit Besonderheit, z.B. beim Auflösen von Konflikten
	public String getCommentCommit() throws ExceptionZZZ;
	public void setCommentCommit(String sCommentCommit) throws ExceptionZZZ;
		
	public String getCommentCommitDefault() throws ExceptionZZZ;
	public void setCommentCommitDefault(String sCommentCommitDefault) throws ExceptionZZZ;
}
