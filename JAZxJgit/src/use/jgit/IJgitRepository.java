package use.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigRepositoryJGIT;

public interface IJgitRepository {
		
	//+++ Per Argument übergebene Werte
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
	public Git getGitObject() throws ExceptionZZZ;
	public void setGitObject(Git git) throws ExceptionZZZ;
		
	//+++++++++++++++++++++++++++++++++++
	//+++ sollte eine Konfiguration ohne Konfigurationsobjekt gewünscht sein.
	//    dann muss alles aus den Properties des Objekts selbst ausgelsen werden.
	public boolean configureGit() throws ExceptionZZZ;
	public boolean configureRepositoryLocal() throws ExceptionZZZ;

	//+++ Konfiguration mit Konfigurationsobjekt
	public boolean configureGit(IConfigRepositoryJGIT objConfig) throws ExceptionZZZ;
	public boolean configureRepositoryLocal(IConfigRepositoryJGIT objConfig) throws ExceptionZZZ;
	
	//+++ Zusätzliche Konfiguration, z.B isBareRepository
	//    Diese wird pro Klasse überschreibbar gemacht.
	public boolean configureGitCustom(InitCommand objInitCommand) throws ExceptionZZZ;
	
	
		
}
