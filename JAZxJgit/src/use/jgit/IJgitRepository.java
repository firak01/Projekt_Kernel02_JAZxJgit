package use.jgit;

import org.eclipse.jgit.api.Git;

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
		public boolean configureGit() throws ExceptionZZZ;
		public boolean configureGit(IConfigRepositoryJGIT objConfig) throws ExceptionZZZ;
		public boolean configureRepositoryLocal() throws ExceptionZZZ;
		public boolean configureRepositoryLocal(IConfigRepositoryJGIT objConfig) throws ExceptionZZZ;
		
}
