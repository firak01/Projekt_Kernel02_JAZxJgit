package use.jgit.resolve;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitStarterRemote;
import use.jgit.config.IConfigResolverJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;

public interface IJgitResolver {
	
	public boolean resolveConflictit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	public boolean resolveit(String sFilepathTotal, String sComment) throws ExceptionZZZ;	
	public boolean resolveit(String sFilepathTotal) throws ExceptionZZZ;
	
	//Normalerweise reicht es nicht aus den Konflikt aus der Datei zu entfernen.
	//Es muss auch noch ein Commit gemacht werden.
	public boolean resolveConflictCommitit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	public boolean resolveCommitit(Git git, String sFilepathTotal, String sComment) throws ExceptionZZZ;	
	public boolean resolveCommitit(Git git, String sFilepathTotal) throws ExceptionZZZ;
	
	
	//Suche in dem Repository nach Dateien, die Konflikmarker haben
	public boolean searchConflictFilesit(IConfigResolverJGIT objConfig) throws ExceptionZZZ;
	public List<File>getFiles() throws ExceptionZZZ;
	public void setFiles(List<File>listFile) throws ExceptionZZZ;
	
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
