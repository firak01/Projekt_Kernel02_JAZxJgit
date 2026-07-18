package use.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;

public interface IJgitStarterLocal extends IJgitRepository{
	
	
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
