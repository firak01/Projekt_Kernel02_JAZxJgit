package use.jgit.resolve;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigResolverJGIT;

public interface IJgitResolver {
	
	//#### Konflikte aufgrund des StageState auflösen, also entweder Deletedid... oder Conflictid...
	public boolean resolveConflictit(IConfigResolverJGIT objConfig, String sConflictType) throws ExceptionZZZ;	
	public boolean resolveConflictit(Git git, String sProjectName, String sFilepathTotal, String sConflictType) throws ExceptionZZZ;
	public boolean resolveConflictit(Git git, String sProjectName, String sFilepathTotal, String sConflictType, boolean bPrintOutput) throws ExceptionZZZ;
	
	
	public boolean resolveSearchedConflictit(Git git, String sProjectName, String sFilepath, String sConflictType) throws ExceptionZZZ;	
	public boolean resolveSearchedConflictit(Git git, String sProjectName, String sFilepath, String sConflictType, boolean bPrintOutput) throws ExceptionZZZ;	
		
	public boolean resolveConflictByScannerit(IConfigResolverJGIT objConfig, String sConflictType) throws ExceptionZZZ;
	public boolean resolveConflictitByScanner(Git git, String sConflictType) throws ExceptionZZZ;
	public boolean resolveConflictitByScanner(Git git, String sConflictType, boolean bPrintOutput) throws ExceptionZZZ;
	
	//### Konflikte aufgrund des Konfliktyps auflösen 
	public boolean resolveSearchedConflictit(IConfigResolverJGIT objConfig, String sConflictType) throws ExceptionZZZ;
	public boolean resolveSearchedConflictit(Git git, String sFilepath, String sConflictType) throws ExceptionZZZ;
	public boolean resolveSearchedConflictit(Git git, String sFilepath, String sConflictType, boolean bPrintOutput) throws ExceptionZZZ;
	
	
	//#### Konflikte aufgrund von Löschungen auflösen
	public boolean resolveSearchedConflictDeletedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;	
	public boolean resolveConflictFileDeletedit(Git git, String sFilepathTotal) throws ExceptionZZZ;
	public boolean resolveConflictFileDeletedit(Git git, String sFilepathTotal, boolean bPrintOutput) throws ExceptionZZZ;
	
	//#### Konflikte mit Konfliktmarkern auflösen	
	public boolean resolveSearchedConflictMarkedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;	
	public boolean resolveSearchedConflictMarkedit(String sFilepathTotal) throws ExceptionZZZ;//Zum Abarbeiten der Liste von FILES, die per Suche nach Dateimarkerdateien gefüllt wurden.
	public boolean resolveSearchedConflictMarkedit(String sFilepathTotal, boolean bPrintOutput) throws ExceptionZZZ;//Zum Abarbeiten der Liste von FILES, die per Suche nach Dateimarkerdateien gefüllt wurden.
	public boolean resolveSearchedConflictFileMarkedit(String sFilepathTotal) throws ExceptionZZZ;//Zum Abarbeiten der Liste von FILES, die per Suche nach Dateimarkerdateien gefüllt wurden.
	public boolean resolveSearchedConflictFileMarkedit(String sFilepathTotal, boolean bPrintOutput) throws ExceptionZZZ;//Zum Abarbeiten der Liste von FILES, die per Suche nach Dateimarkerdateien gefüllt wurden.
	
	//Normalerweise reicht es nicht aus den Konflikt aus der Datei zu entfernen.
	//Es muss auch noch ein Commit gemacht werden.
	public boolean resolveSearchedConflictMarkedCommitit(IConfigResolverJGIT objConfig, String sComment) throws ExceptionZZZ;
	public boolean resolveSearchedConflictMarkedCommitit(Git git, String sFilepathTotal) throws ExceptionZZZ;
	public boolean resolveSearchedConflictMarkedCommitit(Git git, String sFilepathTotal, String sComment) throws ExceptionZZZ;
	public boolean resolveSearchedConflictMarkedCommitit(Git git, String sFilepathTotal, String sComment, boolean bPrintOutput) throws ExceptionZZZ;
	

	
	
	
	//Suche in dem Repository nach Dateien, die Konflikmarker haben (DELETED haben keine Konfliktmarker in der Datei, werden damit also nicht erfasst)
	public boolean searchConflictFilesit(IConfigResolverJGIT objConfig, String sConflictType) throws ExceptionZZZ;
	public boolean searchConflictFilesit(Git git, String sProjectName, String sConflictType) throws ExceptionZZZ;
	
	public boolean searchConflictFilesMarkedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;	
	public boolean searchConflictFilesMarkedit(Git git, String sProjectName) throws ExceptionZZZ;
	
	public List<File>getFiles() throws ExceptionZZZ;
	public void setFiles(List<File>listFile) throws ExceptionZZZ;
	public List<String>getFilesResolved() throws ExceptionZZZ;
	public void setFilesResolved(List<String>listFilepath) throws ExceptionZZZ;
	public List<String>getFilesFailed() throws ExceptionZZZ;
	public void setFilesFailed(List<String>listFilepath) throws ExceptionZZZ;
	
	
	
	
	//Suche in dem Repository nach Dateien, die im Repository den entprechenden Konflikt-Typen haben.
	public boolean searchConflictFilesByScanit(IConfigResolverJGIT objConfig, String sConflictType) throws ExceptionZZZ;
	public boolean searchConflictFilesByScanit(Git git, String sProjectName, String sConflictType) throws ExceptionZZZ;
		
	public boolean searchConflictFilesDeletedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	public boolean searchConflictFilesDeletedit(Git git, String sProjectName) throws ExceptionZZZ; //Deleted_By_Theirs als default sConflictType
	public boolean searchConflictFilesDeletedit(Git git, String sProjectName, String sConflictType) throws ExceptionZZZ;
		
	public List<String>getRepositoryPathStrings() throws ExceptionZZZ;
	public void setRepositoryPathStrings(List<String>listasRepositoryPath) throws ExceptionZZZ;
	public List<String>getRepositoryPathStringsResolved() throws ExceptionZZZ;
	public void setRepositoryPathStringsResolved(List<String>listasRepositoryPath) throws ExceptionZZZ;
	public List<String>getRepositoryPathStringsFailed() throws ExceptionZZZ;
	public void setRepositoryPathStringsFailed(List<String>listasRepositoryPath) throws ExceptionZZZ;
	
	
	//IDEE 20260506;
	//Wurde die Strategie "OURS" umgesetzt, dann wurde der Konflikt zwar aufgelöst, 
	//aber die Änderung liegt nur lokal. Der benötigte zusätzliche PUSH wird dann wohl häufig vergessen.
	//
	//PROBLEM: JGitResolver erbt nur von AbstractJgitStarterCommit
	//         Darin soll aber nur für das lokale Commit alles sein.
	//         Es muss also eine Abstrakte Klasse geben, AbstractJgitStarterCommitAndPush
	//         darin dann alles für den push in ein remote Repository
	//ABER:    Das wird ja dann wohl wieder das passende Protokoll benötigen SSH oder HTTPS.
	//         Dazu kommt also noch, das die Startparameter sich auch deutlich erhöhen werden.
	//  
	//ENTSCHEIDUNG: Im Resolver also keinen PUSH. Gib nur eine entsprechende Hinweismeldung aus.  
	//public boolean conflictCommitAndPushit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	//public boolean conflictCommitAndPushit(Git git, String sFilepathTotal, String sComment) throws ExceptionZZZ;	
	//public boolean conflictCommitAndPushit(Git git, String sFilepathTotal) throws ExceptionZZZ;
	
}
