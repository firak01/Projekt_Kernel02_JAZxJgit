package use.jgit.config;

import java.io.File;

import basic.zBasic.ExceptionZZZ;

public interface IConfigRepositoryJGIT  extends IConfigJGIT{
	//##################################
	//Auslesen von Argumenten aus der Kommandozeile
	
	//das lokale Repository-Verzeichnis
	public String readRepositoryLocal() throws ExceptionZZZ;
	public String getRepositoryLocalBaseDefault() throws ExceptionZZZ;
	public File getRepositoryLocalBaseDirectoryDefault() throws ExceptionZZZ;
	
	//... daran kommt dann noch das Projektverzeichnis
	public String getRepositoryProjectNameDefault() throws ExceptionZZZ;
	public String readRepositoryProjectName() throws ExceptionZZZ;
		
	//... der zu verwendende Branch. Falls nicht gesetzt werden im Standard alle Branches genommen.
	public String getRepositoryBranchDefault() throws ExceptionZZZ;
	public String getRepositoryBranchAll() throws ExceptionZZZ;
	public String readRepositoryBranch() throws ExceptionZZZ;
	
	//Verwende das ueber diesen Alias definerte remote Repository
	public String readRepositoryRemoteAlias() throws ExceptionZZZ;
	public String getRepositoryRemoteAliasDefault() throws ExceptionZZZ;
}
