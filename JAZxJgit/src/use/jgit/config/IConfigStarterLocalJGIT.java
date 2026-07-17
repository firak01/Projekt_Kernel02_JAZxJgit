package use.jgit.config;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigStarterLocalJGIT extends IConfigJGIT{
	
	//##################################
	//ist das Repository "bare", das wird für die JUnit-Tests gebraucht
	//und nicht aus einem Kommandozeilen-Argument ausgelesen
	public void isRepositoryBare(boolean bRepositoryBare) throws ExceptionZZZ;
	public boolean isRepositoryBare() throws ExceptionZZZ;
	
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
	
	//... ein moeglicher Kommentar, z.B. für einen (notwendigen) Commit, auch nach dem Aufloesen des Merge-Konflikts
	public String getCommentDefault() throws ExceptionZZZ;
	public String readComment() throws ExceptionZZZ;
	
	//Verwende das ueber diesen Alias definerte remote Repository
	public String readRepositoryRemoteAlias() throws ExceptionZZZ;
	public String getRepositoryRemoteAliasDefault() throws ExceptionZZZ;
	
	//#####################################################################
	//Methoden, die vom Resolver und von den Startern verwendet werden
	public String readActionStatus() throws ExceptionZZZ;	
	public String readActionCommit() throws ExceptionZZZ;
	public String readActionFetch() throws ExceptionZZZ;
}
