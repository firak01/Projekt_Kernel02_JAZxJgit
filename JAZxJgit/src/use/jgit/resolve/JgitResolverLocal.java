package use.jgit.resolve;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.config.IConfigZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.FileTextReaderZZZ;
import basic.zBasic.util.file.FileTextWriterZZZ;
import basic.zBasic.util.system.Syso;
import basic.zBasic.util.system.SystemZZZ;
import use.jgit.AbstractJgitStarterLocal;
import use.jgit.JgitResolverMain;
import use.jgit.JgitStarterMain;
import use.jgit.config.IConfigResolverJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.tool.resolve.JgitResolverConflictPostUtilZZZ;
import use.jgit.tool.resolve.JgitResolverDeletedUtilZZZ;
import use.jgit.util.JgitUtilZZZ;


//Die Ausgangsklasse konnte zwar Konflikte auflösen, aber nicht diese Änderung committen.
//public class JgitResolver<T> extends AbstractObjectWithFlagZZZ<T> implements IJgitResolver, IJgitResolverEnabled{

//Zwar muss nun auch ein Commit gemacht werden, aber das ist zuviel
//public class JgitResolver<T> extends AbstractJgitStarter<T> implements IJgitResolver, IJgitResolverEnabled{

//Also nutze daraus alles was für den Commit wichtig ist.
public class JgitResolverLocal<T> extends AbstractJgitStarterLocal<T> implements IJgitResolver, IJgitResolverEnabled{
	private static final long serialVersionUID = 521157607363069534L;	
	private List<File> listFile=null; //Liste von Dateien, hier die Dateien mit Konfliktmarker
	private List<String> listasFileSuccess=null; //Liste von Strings, als absoluter Dateipfad
	private List<String> listasFileFailed=null; //Liste von Strings, als absoluter Dateipfad	
	
	private List<String> listasRepositoryPaths=null; //Liste von Strings, die Dateipfaden im Repository entsprechen.
	private List<String> listasRepositoryPathsSuccess=null; //Liste von Strings, die Dateipfaden im Repository entsprechen.
	private List<String> listasRepositoryPathsFailed=null; //Liste von Strings, die Dateipfaden im Repository entsprechen.	
	
	//### Konstruktor
	public JgitResolverLocal() {	
		super();			
	}
	
	//#########################################################################
	//### aus IJgitResolver
	@Override
	public List<File> getFiles() throws ExceptionZZZ{
		if(this.listFile == null) {
			this.listFile = new ArrayList<File>();
		}
		return this.listFile;
	}
	
	@Override
	public void setFiles(List<File>listFile) throws ExceptionZZZ{
		this.listFile = listFile;
	}
	
	@Override
	public List<String> getFilesResolved() throws ExceptionZZZ {
		return this.listasFileSuccess;
	}

	@Override
	public void setFilesResolved(List<String> listFilepath) throws ExceptionZZZ {
		this.listasFileSuccess = listFilepath;
	}

	@Override
	public List<String> getFilesFailed() throws ExceptionZZZ {
		return this.listasRepositoryPathsFailed;
	}

	@Override
	public void setFilesFailed(List<String> listFilepath) throws ExceptionZZZ {
		this.listasRepositoryPathsFailed = listFilepath;
	}

	
	//###########################################
	@Override
	public List<String> getRepositoryPathStrings() throws ExceptionZZZ{
		if(this.listasRepositoryPaths == null) {
			this.listasRepositoryPaths = new ArrayList<String>();
		}
		return listasRepositoryPaths;
	}
	
	@Override
	public void setRepositoryPathStrings(List<String>listasRepositoryPaths) throws ExceptionZZZ{
		this.listasRepositoryPaths = listasRepositoryPaths;
	}
	
	@Override
	public List<String> getRepositoryPathStringsResolved() throws ExceptionZZZ {
		return this.listasRepositoryPathsSuccess;
	}

	@Override
	public void setRepositoryPathStringsResolved(List<String> listasRepositoryPath) throws ExceptionZZZ {
		this.listasRepositoryPathsSuccess = listasRepositoryPath;
	}

	@Override
	public List<String> getRepositoryPathStringsFailed() throws ExceptionZZZ {
		return this.listasRepositoryPathsFailed;
	}

	@Override
	public void setRepositoryPathStringsFailed(List<String> listasRepositoryPath) throws ExceptionZZZ {
		this.listasRepositoryPathsFailed = listasRepositoryPath;
	}
	
	
	@Override 
	public String getCommentCommitDefault() throws ExceptionZZZ{
		String sReturn="";
		main:{
			String sCommentCommitDefault = this.sCommentCommitDefault; 
			if(StringZZZ.isEmptyTrimmed(sCommentCommitDefault)) {
				//Es soll halt erkennbar sein, dass ein Konflikt aufgelöst worden ist.
				//und welche Strategie gewonnen hat.
				
				//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
				//Statt so etwas zu machen, das Flag übergeben:
				//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
				//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
				STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
				
				if(objEnumStrategyMergeConflict==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
				String sStrategy = objEnumStrategyMergeConflict.getName();
				
				sReturn = "(Conflict autoresolved. Strategy: '" + sStrategy + "')";			
			}else {
				sReturn = this.sCommentCommitDefault;
			}
		}//end main:
		return sReturn;
	}
	
	//##################################################
	//###### RESOLVE BY STAGESTATE ################################
	/* Typische Werte des StageState sind:
	 * 
			BOTH_MODIFIED   --->CONFLICTIT
			DELETED_BY_THEM --->DELETEDIT
			DELETED_BY_US   --->DELETEDIT
			BOTH_ADDED      --->????
			ADDED_BY_US     --->????
			ADDED_BY_THEM   --->????
	 * 
	 * (non-Javadoc)
	 * @see use.jgit.resolve.IJgitResolver#resolveByStageStateit(use.jgit.config.IConfigResolverJGIT)
	 */
	@Override
	public boolean resolveConflictit(IConfigResolverJGIT objConfig, String sConflictTypeIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(objConfig==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("ConflictType, ggfs. als Argument der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sConflictType = sConflictTypeIn.toUpperCase();
			
			//################################################
			//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen												
			
			//Für das Auflösen, ob in dem Remote-Repository Dateien gelöscht wurden,
			//brauchen wir das git - Objekt, darum reicht reines lokales Repository nicht.										
			boolean bSuccess = this.configureGit(objConfig);
			if(bSuccess) {
				System.out.println("Git erfolgreich konfiguriert");
			}else {
				System.out.println("Git NICHT erfolgreich konfiguriert");
				break main;
			}
							
			Git git = this.getGitObject();
			
			//Ggfs. ist der Projektname, als Dateiverzeichnisname für die Suche wichtig.
			String sProjectName = objConfig.readRepositoryProjectName();
			
			//ggfs. eine einzelne Datei
			String sFilePath = objConfig.readFilePath();
			
			boolean bPrintOutput = SystemZZZ.getInstance().getPrintLevel()>=IConfigZZZ.iPRINT_LEVEL_ALL;
			bReturn = this.resolveConflictit(git, sProjectName, sFilePath, sConflictType, bPrintOutput);
			if(!bReturn) break main;
			
			List<String> listasRepositoryPathSuccess = this.getRepositoryPathStringsResolved();
			List<String> listasRepositoryPathFailed = this.getRepositoryPathStringsFailed();
			JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BYSTAGE: SUCCESSFUL FILES...", listasRepositoryPathSuccess);				
			JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BYSTAGE: FAILED FILES...", listasRepositoryPathFailed);				
			
			List<String> listasFileSuccess = this.getFilesResolved();
			List<String> listasFileFailed = this.getFilesFailed();
			JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BYMARKED: SUCCESSFUL FILES...", listasFileSuccess);				
			JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BYMARDED: FAILED FILES...", listasFileFailed);
			
			
			if((listasFileFailed!=null && !listasFileFailed.isEmpty()) && (listasRepositoryPathFailed!=null && !listasRepositoryPathFailed.isEmpty())) {					
				bReturn = false;
			}else {
				//!!! HINWEIS AUF NOTWENDIGE WEITERE AKTIONEN				
				if((listasFileSuccess!=null && !listasFileSuccess.isEmpty()) || (listasRepositoryPathSuccess!=null && !listasRepositoryPathSuccess.isEmpty())) {
					//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
					//Statt so etwas zu machen, das Flag übergeben:
					//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
					//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
					STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);

					String sTitle = "";
					JgitResolverLocalUI.printResolveStrategyHint(sTitle, objEnumStrategyMergeConflict);					
				}
				
				bReturn = true;
			}	
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveConflictit(Git git, String sProjectName, String sFilePath, String sConflictTypeIn) throws ExceptionZZZ {
		return this.resolveConflictit(git, sProjectName, sFilePath, sConflictTypeIn, true);
	}
	
	@Override
	public boolean resolveConflictit(Git git, String sProjectName, String sFilePath, String sConflictTypeIn, boolean bPrintOutput) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			if(StringZZZ.isEmptyTrimmed(sProjectName)) {
				ExceptionZZZ ez = new ExceptionZZZ("Name des Projekts im Repository, Argument aus der Kommandozeile, oder '*' für alle.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("Name des Konflikt-Typs, Argument aus der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sConflictType = sConflictTypeIn.toUpperCase();
						
			if(StringZZZ.isEmpty(sFilePath)) {
				//Falls keine einzelne Datei vorgegeben ist... suchen. Erst einmal alle Listen füllen.			
				bReturn = this.searchConflictFilesit(git, sProjectName, sConflictType);				
				if(!bReturn) break main;	
				
				if((this.getFiles()==null || this.getFiles().isEmpty()) && (this.getRepositoryPathStrings()==null || this.getRepositoryPathStrings().isEmpty())) break main;
					
			}
			
			//Nun die gefüllten Listen/oder die einzelne Datei zum Auflösen der Konflikte nutzen
			bReturn = this.resolveSearchedConflictit(git, sProjectName, sFilePath, sConflictType, bPrintOutput);			
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveSearchedConflictit(Git git, String sProjectName, String sFilePath, String sConflictTypeIn) throws ExceptionZZZ {
		return this.resolveSearchedConflictit(git, sFilePath, sConflictTypeIn, true);
	}
	
	@Override
	public boolean resolveSearchedConflictit(Git git, String sProjectName, String sFilePath, String sConflictTypeIn, boolean bPrintOutput) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
				
			if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("ConflictType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sConflictType = sConflictTypeIn.toUpperCase();
				
			//################################################
				
			if(sConflictType.equals("MARKED")) {
				bReturn = this.resolveSearchedConflictMarkedit(sFilePath, bPrintOutput);
			}else if(sConflictType.equals("ALL_MARKED")) {
				bReturn = this.resolveSearchedConflictMarkedit(sFilePath, bPrintOutput);
				bReturn = this.resolveConflictitByScanner(git, "ALL", bPrintOutput);
			}else{
				bReturn = this.resolveConflictitByScanner(git, sConflictType, bPrintOutput);
			}//end if sConflictType))
		}//end main:
		return bReturn;
	}


	//####################################################
	//###### RESOLVEBYSTAGE
	@Override
	public boolean resolveConflictByScannerit(IConfigResolverJGIT objConfig, String sConflictTypeIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(objConfig==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("ConflictType, ggfs. als Argument der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sConflictType = sConflictTypeIn.toUpperCase();
			
			//################################################
			//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen												
			
			//Für das Auflösen, ob in dem Remote-Repository Dateien gelöscht wurden,
			//brauchen wir das git - Objekt, darum reicht reines lokales Repository nicht.										
			boolean bSuccess = this.configureGit(objConfig);
			if(bSuccess) {
				System.out.println("Git erfolgreich konfiguriert");
			}else {
				System.out.println("Git NICHT erfolgreich konfiguriert");
				break main;
			}
							
			Git git = this.getGitObject();
			
			boolean bPrintOutput = SystemZZZ.getInstance().getPrintLevel()>=IConfigZZZ.iPRINT_LEVEL_ALL;			
			bReturn = this.resolveConflictitByScanner(git, sConflictType, bPrintOutput);

			List<String> listasRepositoryPathSuccess = this.getRepositoryPathStringsResolved();
			List<String> listasRepositoryPathFailed = this.getRepositoryPathStringsFailed();
			JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BYSTAGE: SUCCESSFUL FILES...", listasRepositoryPathSuccess);				
			JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BYSTAGE: FAILED FILES...", listasRepositoryPathFailed);				
				
			if(listasRepositoryPathFailed!=null && !listasRepositoryPathFailed.isEmpty()) {					
				bReturn = false;
			}else {
				//!!! HINWEIS AUF NOTWENDIGE WEITERE AKTIONEN				
				if(listasRepositoryPathSuccess!=null && !listasRepositoryPathSuccess.isEmpty()) {

					//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
					//Statt so etwas zu machen, das Flag übergeben:
					//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
					//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
					STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
										
					String sTitle = "";
					JgitResolverLocalUI.printResolveStrategyHint(sTitle, objEnumStrategyMergeConflict);					
				}
				
				bReturn = true;									
			}	
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveConflictitByScanner(Git git, String sConflictTypeIn) throws ExceptionZZZ {
		return this.resolveConflictitByScanner(git, sConflictTypeIn, true);
	}
	
	@Override
	public boolean resolveConflictitByScanner(Git git, String sConflictTypeIn, boolean bPrintOutput) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
					ExceptionZZZ ez = new ExceptionZZZ("ConflictType, ggfs. als Argument der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				String sConflictType = sConflictTypeIn.toUpperCase();
				
				//################################################
								
				ArrayList<String>listasRepositoryPathSuccess = new ArrayList<String>();
				ArrayList<String>listasRepositoryPathFailed = new ArrayList<String>();	
				boolean bResolveSuccess = false;
				
				//Hole alle Konflikte im Repository
				
				//Code Snippet
				//Status status = git.status().call();
				//IndexDiff.StageState stageState = status.getConflictingStageState().get(path);
				//
				//hier aber über:
				//DirCacheEntry entry = cache.getEntry(i);
				List<GitConflictInfoZZZ> conflicts = GitConflictScannerZZZ.scan(git.getRepository());
				for (GitConflictInfoZZZ info : conflicts) {

				    switch (info.getConflictType()) {				    
				    case DELETED_BY_THEIRS:	
				    	if(sConflictType.equals("ALL") || sConflictType.equals(info.getConflictType().name())) {
					    	System.out.println("\nVerarbeite Remote gelöscht: " + info.getRepositoryPath());
					    	bResolveSuccess = this.resolveConflictFileDeletedit(git, info.getRepositoryPath(), bPrintOutput);
					    	if(bResolveSuccess) {
					    		listasRepositoryPathSuccess.add(info.getRepositoryPath());
					    	}else {
					    		listasRepositoryPathFailed.add(info.getRepositoryPath());
					    	}
				    	}
				    	break;
				    case DELETED_BY_OURS:
				    	if(sConflictType.equals("ALL") || sConflictType.equals(info.getConflictType().name())) {
					    	System.out.println("\nVerarbeite Lokal gelöscht: " + info.getRepositoryPath());
					    	bResolveSuccess = this.resolveConflictFileDeletedit(git, info.getRepositoryPath(), bPrintOutput);
					    	if(bResolveSuccess) {
					    		listasRepositoryPathSuccess.add(info.getRepositoryPath());
					    	}else {
					    		listasRepositoryPathFailed.add(info.getRepositoryPath());
					    	}
				    	}
				    	break;
				    case CONTENT:
				    	if(sConflictType.equals("ALL") || sConflictType.equals(info.getConflictType().name())) {
						    System.out.println("\nVerarbeite Textkonflikt: " + info.getRepositoryPath());
						    bResolveSuccess = this.resolveSearchedConflictFileMarkedit(info.getRepositoryPath(), bPrintOutput);
						    if(bResolveSuccess) {
					    		listasRepositoryPathSuccess.add(info.getRepositoryPath());
					    	}else {
					    		listasRepositoryPathFailed.add(info.getRepositoryPath());
					    	}
				    	}
				    	break;
				    default:
				    	ExceptionZZZ ez = new ExceptionZZZ("Noch nicht impelmentierter Konflikt-Typ '"+ info.getConflictType().name(), iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
				    }
				} //end for
				
				//###############################################
				//### Ausgabe der Rückmeldung, nur falls gewuenscht								
				
				//Rückgabe der Werte für die aufrufende Methode
				this.setRepositoryPathStringsResolved(listasRepositoryPathSuccess);
				this.setRepositoryPathStringsFailed(listasRepositoryPathFailed);
									
				if(listasRepositoryPathFailed!=null && !listasRepositoryPathFailed.isEmpty()) {					
					bReturn = false;
				}else {
					bReturn = true;
				}
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			}
		}//end main:
		return bReturn;
	}

	//##################################################
	//###### RESOLVEDELETED #######################################	
	@Override
	public boolean resolveSearchedConflictDeletedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			//try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen												
				
				//Für das Auflösen, ob in dem Remote-Repository Dateien gelöscht wurden,
				//brauchen wir das git - Objekt, darum reicht reines lokales Repository nicht.										
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				Git git = this.getGitObject();
				
				boolean bUseList = false;
				List<String> listasPathInRepository = null;
				String sFilePath = objConfig.readFilePath();
				if(StringZZZ.isEmpty(sFilePath)) {
					listasPathInRepository = this.getRepositoryPathStrings();
					if(listasPathInRepository.isEmpty()) {
						ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						bUseList = true;
					}
				}
					
				
				boolean bPrintOutput = SystemZZZ.getInstance().getPrintLevel()>=IConfigZZZ.iPRINT_LEVEL_ALL;				
				if(bUseList && listasPathInRepository!=null) {
					//Liste von Dateien verarbeiten
					ArrayList<String>listasFileSuccess = new ArrayList<String>();
					ArrayList<String>listasFileFailed = new ArrayList<String>();
					for(String  sPathInRepository : listasPathInRepository) {
						boolean bSuccessConflict = this.resolveConflictFileDeletedit(git, sPathInRepository, bPrintOutput);
						if(bSuccessConflict) {
							listasFileSuccess.add(sPathInRepository);
						}else {
							listasFileFailed.add(sPathInRepository);
						}
					}//end for
					
					//Auf oberster Ebene immer Ausgabe
					JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT DELETED: SUCCESSFUL FILES...", listasFileSuccess);				
					JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT DELETED: FAILED FILES...", listasFileFailed);													
				}else {
					//Einzelne Datei verarbeiten.
					boolean bSuccessConflict = this.resolveConflictFileDeletedit(git, sFilePath, bPrintOutput);
					if(bSuccessConflict) {
						listasFileSuccess.add(sFilePath);
						
						//Auf oberster Ebene immer Ausgabe
						JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING CONFLICT DELETED: SUCCESSFUL FILE...", sFilePath);
						
					}else {
						listasFileFailed.add(sFilePath);
						
						//Auf oberster Ebene immer Ausgabe
						JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING CONFLICT DELETED: FAILED FILE...", sFilePath);						
					}
				}
				
				if(listasFileFailed!=null && !listasFileFailed.isEmpty()) {// && (listasRepositoryPathFailed!=null && !listasRepositoryPathFailed.isEmpty())) {					
					bReturn = false;
				}else {
					//!!! HINWEIS AUF NOTWENDIGE WEITERE AKTIONEN									
					if(listasFileSuccess!=null && !listasFileSuccess.isEmpty()) {
						//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
						//Statt so etwas zu machen, das Flag übergeben:
						//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
						//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
						STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
						
						String sTitle = "";
						JgitResolverLocalUI.printResolveStrategyHint(sTitle, objEnumStrategyMergeConflict);					
					}
					
					bReturn = true;
				}		
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveConflictFileDeletedit(Git git, String sFilePathInRepository) throws ExceptionZZZ {
		return this.resolveConflictFileDeletedit(git, sFilePathInRepository, true);
	}


	@Override
	public boolean resolveConflictFileDeletedit(Git git, String sFilePathInRepository, boolean bPrintOutput) throws ExceptionZZZ {				
		boolean bReturn = false;
		main:{
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			if(StringZZZ.isEmpty(sFilePathInRepository)) {
				ExceptionZZZ ez = new ExceptionZZZ("sFilePathInRepository", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			

			//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
			//Statt so etwas zu machen, das Flag übergeben:
			//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
			//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
			STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);

			boolean bResolvedSuccess=false;			
			if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {						
				bResolvedSuccess = JgitResolverDeletedUtilZZZ.resolveDeletedTHEIRS(git, sFilePathInRepository, bPrintOutput);				
			}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {			
				bResolvedSuccess = JgitResolverDeletedUtilZZZ.resolveDeletedOURS(git, sFilePathInRepository, bPrintOutput);
			}else {
				//Default
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
				ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
				
			}			
			bReturn = bResolvedSuccess;
		}//end main:
		return bReturn;
	}
	
	//##################################################
	//###### RESOLVECONFLICT ######################################
	@Override
	public boolean resolveSearchedConflictit(IConfigResolverJGIT objConfig, String sConflictTypeIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			//try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
					ExceptionZZZ ez = new ExceptionZZZ("ConflictType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen
				
				//Für das Auflösen, ob in dem Remote-Repository Dateien gelöscht wurden,
				//brauchen wir das git - Objekt, darum reicht reines lokales Repository nicht.										
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				Git git = this.getGitObject();
				
				//ggfs. eine einzelne Datei
				String sFilePath = objConfig.readFilePath();
				
				//Auf unterer Ebene nur Ausgabe der Dateilisten machen, wenn gewünscht
				boolean bPrintOutput = SystemZZZ.getInstance().getPrintLevel()>=IConfigZZZ.iPRINT_LEVEL_ALL;
				boolean bSearchsuccess = this.resolveSearchedConflictit(git, sFilePath, sConflictTypeIn, bPrintOutput); 	
				if(!bSearchsuccess) break main;
				
				
				//Holen der Dateilisten und Ausgabe der Dateilisten und Ausgabe des Strategiehiweis.
				List<String> listasRepositoryPathSuccess = this.getRepositoryPathStringsResolved();
				List<String> listasRepositoryPathFailed = this.getRepositoryPathStringsFailed();
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BY STAGE: SUCCESSFUL FILES...", listasRepositoryPathSuccess);				
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BY STAGE: FAILED FILES...", listasRepositoryPathFailed);				
				
				List<String> listasFileSuccess = this.getFilesResolved();
				List<String> listasFileFailed = this.getFilesFailed();
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BY MARKED: SUCCESSFUL FILES...", listasFileSuccess);				
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT BY MARKED: FAILED FILES...", listasFileFailed);
				
																
				if((listasFileFailed!=null && !listasFileFailed.isEmpty()) && (listasRepositoryPathFailed!=null && !listasRepositoryPathFailed.isEmpty())) {					
					bReturn = false;
				}else {
					//!!! HINWEIS AUF NOTWENDIGE WEITERE AKTIONEN					
					if(listasFileSuccess!=null && !listasFileSuccess.isEmpty()) {

						//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
						//Statt so etwas zu machen, das Flag übergeben:
						//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
						//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
						STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
						
						String sTitle = "";
						JgitResolverLocalUI.printResolveStrategyHint(sTitle, objEnumStrategyMergeConflict);					
					}
					
					bReturn = true;
				}			
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveSearchedConflictit(Git git, String sFilePath, String sConflictTypeIn) throws ExceptionZZZ {
		return this.resolveSearchedConflictit(git, sFilePath, sConflictTypeIn, true);
	}
	
	@Override
	public boolean resolveSearchedConflictit(Git git, String sFilePath, String sConflictTypeIn, boolean bPrintOutput) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
				
			if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("ConflictType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
			//################################################
				
			boolean bUseList = false;
			List<File> listFile = null;
								
			//ggfs. mehrere Dateien aus einem Suchergebnis
			if(StringZZZ.isEmpty(sFilePath)) {
				listFile = this.getFiles();
				if(listFile.isEmpty()) {
//						ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
//						throw ez;
				}else {
					bUseList = true;
				}
			}
				
			//ggfs. mehrere Dateien aus einem Suchergebnis
			List<String> listasPathInRepository = null;
			if(StringZZZ.isEmpty(sFilePath)) {
				listasPathInRepository = this.getRepositoryPathStrings();
				if(listasPathInRepository.isEmpty()) {
//						ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
//						throw ez;
				}else {
					bUseList = true;
				}
			}
				
			
			//Die Konflikttyp-Anweisung ggfs. auf mehrere aufteilen
			String sConflictType = sConflictTypeIn.toLowerCase();
			String[]saConflictType=null;
			if(sConflictType.equals("all")) {
				saConflictType = new String[2];
				saConflictType[0]="marked";
				saConflictType[1]="deleted";
			}else {
				saConflictType = new String[1];
				saConflictType[0]=sConflictType;				
			}
			
			ArrayList<String>listasFileSuccess = new ArrayList<String>();
			ArrayList<String>listasFileFailed = new ArrayList<String>();	
											
			boolean bSuccessConflict=false;			
			for(String sConflictTypeTemp : saConflictType) {
				switch (sConflictTypeTemp){
				case "marked":
					if((bUseList && listFile!=null)||(bUseList && listasPathInRepository!=null)) {
						if(listFile!=null) {
							//Liste von Dateien aus dem Filesystem verarbeiten						
							for(File objFile : listFile) {
								String sFilePathTemp = objFile.getAbsolutePath();
								bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sFilePathTemp);
								if(bSuccessConflict) {
									listasFileSuccess.add(sFilePathTemp);
								}else {
									listasFileFailed.add(sFilePathTemp);
								}
							}//end for
						}
	
						if(listasPathInRepository!=null) {
							//Liste von Dateien mit Kennzeichnung im GIT verarbeiten						
							for(String sPathInRepository : listasPathInRepository) {
								bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sPathInRepository);
								if(bSuccessConflict) {
									listasFileSuccess.add(sPathInRepository);
								}else {
									listasFileFailed.add(sPathInRepository);
								}
							}//end for
						}
					}else {
						//Einzelne Datei verarbeiten
						if(!StringZZZ.isEmptyTrimmed(sFilePath)) {
							bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sFilePath);
							if(bSuccessConflict) {
								listasFileSuccess.add(sFilePath);
							}else {
								listasFileFailed.add(sFilePath);
							}
						}
					}
					break;
				case "deleted":
				case "deleted_by_theirs":
				case "deleted_by_ours":
					if(bUseList && listasPathInRepository!=null) {
						//Liste von Repositorypfaden verarbeiten						
						for(String sPathInRepository : listasPathInRepository) {
							bSuccessConflict = this.resolveConflictFileDeletedit(git, sPathInRepository);
							if(bSuccessConflict) {
								listasFileSuccess.add(sPathInRepository);
							}else {
								listasFileFailed.add(sPathInRepository);
							}
						}//end for
					}else {
						//Einzelne Datei verarbeiten
						if(!StringZZZ.isEmptyTrimmed(sFilePath)) {
							bSuccessConflict = this.resolveConflictFileDeletedit(git, sFilePath);
							if(bSuccessConflict) {
								listasFileSuccess.add(sFilePath);
							}else {
								listasFileFailed.add(sFilePath);
							}
						}
					}
					break;
					
					
				default:
					ExceptionZZZ ez = new ExceptionZZZ("Unexpected ActionType '" + sConflictTypeIn + "'", iERROR_PARAMETER_VALUE, JgitResolverMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}//end for	
				
			
			//###############################################			
			//Rückgabe der Werte für die aufrufende Methode
			this.setFilesResolved(listasFileSuccess);
			this.setFilesFailed(listasFileFailed);
						
			if(listasFileFailed!=null && !listasFileFailed.isEmpty()) {					
				bReturn = false;
			}else {
				bReturn = true;
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveSearchedConflictMarkedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			//try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen
				//JgitResolver braucht nur das lokale Repository zu konfigurieren, kein GIT-Objekt, komplett
				//Konfiguriere JGit lokal
				boolean bLocalRepositoryConfigured = this.configureRepositoryLocal((IConfigStarterLocalJGIT)objConfig);
				if(bLocalRepositoryConfigured) {
					System.out.println("Lokales Repository erfolgreich konfiguriert");
				}else {
					System.out.println("Lokales Repository NICHT erfolgreich konfiguriert");
					//Wenn das so nicht geklappt hat, dann wurden die Details ggfs. einzeln übergeben... wir werden sehen.
				}
				
				boolean bUseListFile = false;
				List<File> listFile = null;
				String sFilePath = objConfig.readFilePath();
				if(StringZZZ.isEmpty(sFilePath)) {
					listFile = this.getFiles();
					if(listFile.isEmpty()) {
						ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						bUseListFile = true;
					}
				}
							
				boolean bPrintOutput = SystemZZZ.getInstance().getPrintLevel()>=IConfigZZZ.iPRINT_LEVEL_ALL;
				if(bUseListFile && listFile!=null) {
					//Liste von Dateien verarbeiten
					ArrayList<String>listasFileSuccess = new ArrayList<String>();
					ArrayList<String>listasFileFailed = new ArrayList<String>();
					for(File objFile : listFile) {
						boolean bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sFilePath, bPrintOutput);
						if(bSuccessConflict) {
							listasFileSuccess.add(sFilePath);
						}else {
							listasFileFailed.add(sFilePath);
						}
					}//end for
					
					//Auf oberster Ebene ausgeben										
					JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT: SUCCESSFUL FILES...", listasFileSuccess);				
					JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT: FAILED FILES...", listasFileFailed);									
				}else {
					//Einzelne Datei verarbeiten.
					boolean bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sFilePath, bPrintOutput);
					if(bSuccessConflict) {
						listasFileSuccess.add(sFilePath);
						
						//Auf oberster Ebene Ausgeben.
						JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING CONFLICT: SUCCESSFUL FILE...", sFilePath);
						
					}else {
						listasFileFailed.add(sFilePath);
						
						//Auf oberster Ebene Ausgeben.
						JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING CONFLICT: FAILED FILE...", sFilePath);						
					}					
				}		
				
				//Rückgabe der Werte für die aufrufende Methode
				this.setFilesResolved(listasFileSuccess);
				this.setFilesFailed(listasFileFailed);
				
				if(listasFileFailed!=null && !listasFileFailed.isEmpty()) {					
					bReturn = false;
				}else {
					bReturn = true;
				}
				
				
				if(listasFileFailed!=null && !listasFileFailed.isEmpty()) {// && (listasRepositoryPathFailed!=null && !listasRepositoryPathFailed.isEmpty())) {					
					bReturn = false;
				}else {
					//!!! HINWEIS AUF NOTWENDIGE WEITERE AKTIONEN					
					if(listasFileSuccess!=null && !listasFileSuccess.isEmpty()) {

						//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
						//Statt so etwas zu machen, das Flag übergeben:
						//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
						//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
						STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
						
						String sTitle = "";
						JgitResolverLocalUI.printResolveStrategyHint(sTitle, objEnumStrategyMergeConflict);					
					}
					
					bReturn = true;
				}		
				

		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveSearchedConflictMarkedit(String sFilePathTotalIn) throws ExceptionZZZ {
		return this.resolveSearchedConflictMarkedit(sFilePathTotalIn, true);
	}
	
	@Override
	public boolean resolveSearchedConflictMarkedit(String sFilePathIn, boolean bPrintOutput) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			boolean bUseListFile = false;
			List<File> listFile = null;
			String sFilePath=null;
			if(StringZZZ.isEmpty(sFilePathIn)) {
				listFile = this.getFiles();
				if(listFile.isEmpty()) {
					//ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					//throw ez;
					bReturn = true;
					break main; //Keine Dateien mit lokalen Konfliktmarkern, also exit.
				}else {
					bUseListFile = true;
				}
			}else {
				sFilePath = sFilePathIn;
			}
						
			if(bUseListFile && listFile!=null) {
				//Liste von Dateien verarbeiten
				ArrayList<String>listasFileSuccess = new ArrayList<String>();
				ArrayList<String>listasFileFailed = new ArrayList<String>();
				for(File objFile : listFile) {
					String sFilePathTemp = objFile.getAbsolutePath();
					boolean bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sFilePathTemp, bPrintOutput);
					if(bSuccessConflict) {
						listasFileSuccess.add(sFilePathTemp);
					}else {
						listasFileFailed.add(sFilePathTemp);
					}
				}//end for
								
				//Die Ausgabe der Dateilisten erfolgt hier nur wenn gewünscht
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING MARKER CONFLICTS: SUCCESSFUL FILES...", listasFileSuccess, bPrintOutput);				
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING MARKER CONFLICTS: FAILED FILES...", listasFileFailed, bPrintOutput);
			}else {
				//Einzelne Datei verarbeiten.
				boolean bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sFilePath, bPrintOutput);
				if(bSuccessConflict) {
					listasFileSuccess.add(sFilePath);
					
					//Die Ausgabe der Dateilisten erfolgt hier nur wenn gewünscht
					JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING MARKER CONFLICT: SUCCESSFUL FILE...", sFilePath, bPrintOutput);
				}else {
					listasFileFailed.add(sFilePath);
					
					//Die Ausgabe der Dateilisten erfolgt hier nur wenn gewünscht
					JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING MARKER CONFLICT: FAILED FILE...", sFilePath, bPrintOutput);
				}
			}		
			
			//Rückgabe der Werte
			this.setFilesResolved(listasFileSuccess);
			this.setFilesFailed(listasFileFailed);			
			if(listasFileFailed!=null && !listasFileFailed.isEmpty()) {					
				bReturn = false;
			}else {
				bReturn = true;
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveSearchedConflictFileMarkedit(String sFilePathTotalIn) throws ExceptionZZZ {
		return this.resolveSearchedConflictFileMarkedit(sFilePathTotalIn, true);
	}
		
	@Override
	public boolean resolveSearchedConflictFileMarkedit(String sFilePathTotalIn, boolean bPrintOutput) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sFilePathTotalIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("FilePath", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sFilePathTotal = null;
			boolean bPathRelative = FileEasyZZZ.isPathRelative(sFilePathTotalIn);
			if(bPathRelative) {
				String sDirectoryRepoProjectTotal = this.getRepositoryLocalTotal();
				sFilePathTotal = FileEasyZZZ.joinFilePathName(sDirectoryRepoProjectTotal, sFilePathTotalIn);
			}else {
				sFilePathTotal = sFilePathTotalIn;
			}
			
			
			File objFile = new File(sFilePathTotal);
			boolean bFileExists = FileEasyZZZ.exists(objFile);
			if(!bFileExists) {
				ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePathTotal='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
			if(!bIsFile) {
				ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePathTotal='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			FileTextReaderZZZ objReader = new FileTextReaderZZZ(objFile);
			String sContent = objReader.read();
			
			//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
			//Statt so etwas zu machen, das Flag übergeben:
			//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
			//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
			STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);

			String sResolved = null;			
			if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS);				
			}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {			
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS);
			}else {
				Syso.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
				ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			FileTextWriterZZZ objWriter = new FileTextWriterZZZ(objFile);
			bReturn = objWriter.write(sResolved);
		}//end main:
		return bReturn;
	}	
	//###########################################################
	//Normalerweise reicht ein einfacher commit nicht. Anschliessend muss aber ein Commit gemacht werden... 
	//Damit Eclipse das Aufloesen des Konflikts auch merkt.
	@Override
	public boolean resolveSearchedConflictMarkedCommitit(IConfigResolverJGIT objConfig, String sCommentIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(objConfig==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//+++++++++++++++++++++++++++++++
			//Konfiguriere JGit lokal							
			boolean bSuccess = this.configureGit(objConfig);
			if(bSuccess) {
				System.out.println("Git erfolgreich konfiguriert");
			}else {
				System.out.println("Git NICHT erfolgreich konfiguriert");
				break main;
			}
			
			boolean bUseListFile = false;
			List<File> listFile = null;
			String sFilePath = objConfig.readFilePath();
			if(StringZZZ.isEmpty(sFilePath)) {
				listFile = this.getFiles();
				if(listFile.isEmpty()) {
					ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}else {
					bUseListFile = true;
				}
			}
			
			//Vielleicht noch einen weiteren Kommentar übergeben. Der wird (falls unterschiedlich zum Methodenparameter) ggfs. hinzugerechnet.
			String sComment = objConfig.readComment();
			this.setCommentCommit(sComment);
			
			Git git = this.getGitObject();
			
			boolean bPrintOutput = SystemZZZ.getInstance().getPrintLevel()>=IConfigZZZ.iPRINT_LEVEL_ALL;
			if(bUseListFile && listFile!=null) {
				//Liste von Dateien verarbeiten
				ArrayList<String>listasFileSuccess = new ArrayList<String>();
				ArrayList<String>listasFileFailed = new ArrayList<String>();
				for(File objFile : listFile) {
					sFilePath = objFile.getAbsolutePath();
					
					//auf der unteren Ebene nur Ausgabe der Dateilisten machen, etc. falls gewuenscht
					boolean bSuccessConflict = this.resolveSearchedConflictFileMarkedit(sFilePath, bPrintOutput);
					if(bSuccessConflict) {
						listasFileSuccess.add(sFilePath);
					}else {
						listasFileFailed.add(sFilePath);
					}
				}//end for
			
				//Auf dieser obersten Ebene die Ausgabe machen 
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT MARKED: SUCCESSFUL FILES...", listasFileSuccess);				
				JgitResolverLocalUI.printResolveResultListString("\nSTATUS AFTER RESOLVING CONFLICT MARKED: FAILED FILES...", listasFileFailed);														
			}else {
				//Einzelne Datei verarbeiten.					
				String sRepositoryLocalBase = this.getRepositoryLocalBase();
				String sFilePathTotal = JgitUtilZZZ.computeRepositoryLocalFilePath(sRepositoryLocalBase, sRepositoryProject, sFilePath);
				
				//auf der unteren Ebene nur Ausgabe der Dateilisten machen, etc. falls gewuenscht
				boolean bSuccessConflict = this.resolveSearchedConflictMarkedCommitit(git, sFilePathTotal, sComment, bPrintOutput);
				if(bSuccessConflict) {
					listasFileSuccess.add(sFilePath);
					
					//auf oberster Ebene die Ausgabe machen
					JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING CONFLICT MARKED: SUCCESSFUL FILE...", sFilePath);
				}else {
					listasFileFailed.add(sFilePath);
					
					//auf oberster Ebene die Ausgabe machen
					JgitResolverLocalUI.printResolveResultSingle("\nSTATUS AFTER RESOLVING CONFLICT MARKED: FAILED FILE...", sFilePath);
				}
				
				//Rückgabe der Werte für die aufrufende Methode
				this.setFilesResolved(listasFileSuccess);
				this.setFilesFailed(listasFileFailed);
				
				if(listasFileFailed!=null && !listasFileFailed.isEmpty()) {// && (listasRepositoryPathFailed!=null && !listasRepositoryPathFailed.isEmpty())) {					
					bReturn = false;
				}else {
					//!!! HINWEIS AUF NOTWENDIGE WEITERE AKTIONEN					
					if(listasFileSuccess!=null && !listasFileSuccess.isEmpty()) {

						//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
						//Statt so etwas zu machen, das Flag übergeben:
						//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
						//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
						STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
						
						String sTitle = "";
						JgitResolverLocalUI.printResolveStrategyHint(sTitle, objEnumStrategyMergeConflict);					
					}
					
					bReturn = true;
				}	
				if(!bReturn) break main;
				
				bReturn = this.commitit(git, sCommentIn);//Alle Dateien auf einmal committen, Kommentar aus dem Methodenparameter.
			}				
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveSearchedConflictMarkedCommitit(Git git, String sFilePathTotal) throws ExceptionZZZ {
		return this.resolveSearchedConflictMarkedCommitit(git, sFilePathTotal, null);
	}
	
	@Override
	public boolean resolveSearchedConflictMarkedCommitit(Git git, String sFilePathTotal, String sComment) throws ExceptionZZZ {
		return this.resolveSearchedConflictMarkedCommitit(git, sFilePathTotal, sComment, true);
	}
	
	@Override
	public boolean resolveSearchedConflictMarkedCommitit(Git git, String sFilePathTotal, String sComment, boolean bPrintOutput) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		try {
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			if(StringZZZ.isEmpty(sFilePathTotal)) {
				ExceptionZZZ ez = new ExceptionZZZ("FilePath", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			File objFile = new File(sFilePathTotal);
			boolean bFileExists = FileEasyZZZ.exists(objFile);
			if(!bFileExists) {
				ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePath='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
			if(!bIsFile) {
				ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePath='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			FileTextReaderZZZ objReader = new FileTextReaderZZZ(objFile);
			String sContent = objReader.read();
			
			//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
			//Statt so etwas zu machen, das Flag übergeben:
			//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
			//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
			STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
			
			
			String sResolved = null;
			if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS);
			}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS);						
			}else {
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
				ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			FileTextWriterZZZ objWriter = new FileTextWriterZZZ(objFile);
			bReturn = objWriter.write(sResolved);

			
			//+++++++++++++++++++++++++++++++
			//Nun muss der gewuenschte Commit gemacht werden.

			//Finde geaenderte und neue Dateien fuer den commit
			boolean bSuccessCommit = this.commitit(git);
			if(bSuccessCommit) {
				if(bPrintOutput) {
					System.out.println("\nSTATUS AFTER COMMIT: SUCCESSFUL");
					this.printStatus(git);
				}
				bReturn = true;
			}else {
				if(bPrintOutput) {
					System.out.println("\nSTATUS AFTER COMMIT: FAILED");
					this.printStatus(git);
				}
				bReturn = false;
			}
		
		    git.close();
			
			
		
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}

	//##############################################################################
	//##############################################################################
	@Override
	public boolean searchConflictFilesit(IConfigResolverJGIT objConfig, String sConflictType) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(StringZZZ.isEmptyTrimmed(sConflictType)) {
					ExceptionZZZ ez = new ExceptionZZZ("Name des Konflikt-Typs für den Scanner, Argument aus der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++++++++++++++++++++++++++++++
				//Konfiguriere JGit für HTTPS
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
							
				//+++++++++++++++++++++++++++++++
				//Finde Dateien mit Konfliktmarker
				String sProjectName = objConfig.readRepositoryProjectName(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben 				
				Git git = this.getGitObject();
				boolean bSuccessSearch = this.searchConflictFilesit(git, sProjectName, sConflictType);
				if(bSuccessSearch) {
					List<File>listaFile=this.getFiles(); //liste der Dateien, gefünden über die Suche im Dateisystem (MARKED)
					List<String>listasPathInRepository=this.getRepositoryPathStrings(); //liste der Dateien, entsprechend notiert im GIT.
									
					JgitResolverLocalUI.printResolveResultListAny("\nDateien mit Konfliktmarker, vom Typ: " + sConflictType, listaFile, listasPathInRepository);
					
					//Auch wenn nichts gefunden wurde, ist die Suche doch erfolgreich
					bReturn = true;
				}else {
					System.out.println("\nSuche nach Dateien mit Konfliktmarker: Fehlgeschlagen");
					this.printStatus(git);	
					bReturn = false;
				}
			
			    git.close();
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
	return bReturn;
	}
	
	@Override
	public boolean searchConflictFilesit(Git git, String sProjectName, String sConflictTypeIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			if(StringZZZ.isEmptyTrimmed(sProjectName)) {
				ExceptionZZZ ez = new ExceptionZZZ("Name des Projekts im Repository, Argument aus der Kommandozeile, oder '*' für alle.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("Name des Konflikt-Typs, Argument aus der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}			
			String sConflictType = sConflictTypeIn.trim().toUpperCase();
			
			if(sConflictType.equals("MARKED")) {
				bReturn = this.searchConflictFilesMarkedit(git, sProjectName);
			}else if(sConflictType.equals("ALL_MARKED")) {
				bReturn = this.searchConflictFilesMarkedit(git, sProjectName);
				bReturn = this.searchConflictFilesByScanit(git, sProjectName, "ALL");
			}else{
				bReturn = this.searchConflictFilesByScanit(git, sProjectName, sConflictType);
			}//end if sConflictType))						
		}//end main:
		return bReturn;
	}
		
	//##############################################################################
	@Override
	public boolean searchConflictFilesMarkedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++++++++++++++++++++++++++++++
				//Konfiguriere JGit für HTTPS
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
							
				//+++++++++++++++++++++++++++++++
				//Finde Dateien mit Konfliktmarker
				String sProjectName = objConfig.readRepositoryProjectName(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben 				
				Git git = this.getGitObject();
				boolean bSuccessSearch = this.searchConflictFilesMarkedit(git, sProjectName);
				if(bSuccessSearch) {
					List<File>listaFile=this.getFiles();
					JgitResolverLocalUI.printResolveResultListFile("\nDateien mit Konfliktmarker:", listaFile);											
										
					//Auch wenn nichts gefunden wurde ist die Suche doch erfolgreich.
					bReturn = true;
				}else {
					System.out.println("\nSuche nach Dateien mit Konfliktmarker: FAILED");
					this.printStatus(git);	
					bReturn = false;
				}
			
			    git.close();
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
	return bReturn;
	}
	
	@Override
	public boolean searchConflictFilesMarkedit(Git git, String sProjectName) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			if(StringZZZ.isEmptyTrimmed(sProjectName)) {
				ExceptionZZZ ez = new ExceptionZZZ("Name des Projekts im Repository, Argument aus der Kommandozeile, oder '*' für alle.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			File objFileRepository = git.getRepository().getDirectory();
			File objFileDirectory = objFileRepository.getParentFile();
			
			String sFileDirectoryName = FileEasyZZZ.getNameOnly(objFileDirectory);
			
			List<File> listFile = null; File objFileProject = null;
			if(sProjectName.equals(sFileDirectoryName) | sProjectName.equals("*")) {
				objFileProject = objFileDirectory;
			}else {
				String sFileDirectory = FileEasyZZZ.joinFilePathName(objFileDirectory, sProjectName);
				objFileProject = new File(sFileDirectory);				
			}
			listFile = JgitResolverConflictPostUtilZZZ.findFilesWithConflictMarkers(objFileProject);
	        this.setFiles(listFile);
			
	        bReturn = true;							
		}//end main:
		return bReturn;
	}
	
	//##############################################################################
	//##############################################################################
	@Override
	public boolean searchConflictFilesByScanit(IConfigResolverJGIT objConfig, String sConflictTypeIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
					ExceptionZZZ ez = new ExceptionZZZ("Name des Konflikt-Typs für den Scanner, Argument aus der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				String sConflictType = sConflictTypeIn.trim().toUpperCase();
				
				//+++++++++++++++++++++++++++++++
				//Konfiguriere JGit für HTTPS
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
							
				//+++++++++++++++++++++++++++++++
				//Finde Dateien mit Konfliktmarker
				String sProjectName = objConfig.readRepositoryProjectName(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben 				
				Git git = this.getGitObject();
				boolean bSuccessSearch = this.searchConflictFilesByScanit(git, sProjectName, sConflictType);
				if(bSuccessSearch) {
					List<String>listasPathInRepository=this.getRepositoryPathStrings();					
					JgitResolverLocalUI.printResolveResultListString("\nDateien mit Konfliktmarker vom Typ: " + sConflictType, listasPathInRepository);											
					
					//Auch wenn nichts gefunden wurde, ist die Suche doch erfolgreich
					bReturn = true;
				}else {
					System.out.println("\nSuche nach Dateien mit Konfliktmarker: FAILED");
					this.printStatus(git);	
					bReturn = false;
				}
			
			    git.close();
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
	return bReturn;
	}
	
	@Override
	public boolean searchConflictFilesByScanit(Git git, String sProjectName, String sConflictTypeIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				if(StringZZZ.isEmptyTrimmed(sProjectName)) {
					ExceptionZZZ ez = new ExceptionZZZ("Name des Projekts im Repository, Argument aus der Kommandozeile, oder '*' für alle.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(StringZZZ.isEmptyTrimmed(sConflictTypeIn)) {
					ExceptionZZZ ez = new ExceptionZZZ("Name des Konflikt-Typs, Argument aus der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				String sConflictType = sConflictTypeIn.trim().toUpperCase();
				boolean bEveryConflictType = sConflictType.equalsIgnoreCase("ALL");
				
				List<GitConflictInfoZZZ> conflicts = GitConflictScannerZZZ.scan(git.getRepository());
				for (GitConflictInfoZZZ info : conflicts) {

				    switch (info.getConflictType()) {				    
				    case DELETED_BY_THEIRS:			
				    	if(info.getConflictType().name().equals(sConflictType) | bEveryConflictType){ //filtere auf diesen Typen
					        System.out.println("Übernimm Remote gelöscht: " + info.getRepositoryPath());
					     					        
					        this.getRepositoryPathStrings().add(info.getRepositoryPath()); //in die Liste aufnehmen							
				    	}
				    	break;
				    case DELETED_BY_OURS:
				    	if(info.getConflictType().name().equals(sConflictType) | bEveryConflictType){ //filtere auf diesen Typen
					        System.out.println("Übernimm Lokal gelöscht: " + info.getRepositoryPath());
					        
					        this.getRepositoryPathStrings().add(info.getRepositoryPath()); //in die Liste aufnehmen							
				    	}
				    	break;
				    case CONTENT:
				    	if(info.getConflictType().name().equals(sConflictType) | bEveryConflictType){ //filtere auf diesen Typen
					        System.out.println("Übernimm Textkonflikt: " + info.getRepositoryPath());
					        
					        this.getRepositoryPathStrings().add(info.getRepositoryPath()); //in die Liste aufnehmen						        bReturn = true;
				    	}
				    	break;
				    default:
				    	ExceptionZZZ ez = new ExceptionZZZ("Noch nicht implementierter Konflikt-Typ '"+ info.getConflictType().name() +"', Argument aus der Kommandozeile.'" + sConflictTypeIn + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
				    }
				}//end for
			
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			}	
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	//########################################################################################################
	@Override
	public boolean searchConflictFilesDeletedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++++++++++++++++++++++++++++++
				//Konfiguriere JGit lokal							
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				
				Git git = this.getGitObject();
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				String sProjectName = objConfig.readRepositoryProjectName(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben
				boolean bSuccessSearch = this.searchConflictFilesDeletedit(git, sProjectName);	
				if(bSuccessSearch) {
					List<String>listaPathInRepository=this.getRepositoryPathStrings();
					JgitResolverLocalUI.printResolveResultListString("\nDateien mit Deleted Konfliktmarker:", listaPathInRepository);											
					
					//Auch wenn nichts gefunden wurde, ist die Suche doch erfolgreich
					bReturn = true;
				}else {
					System.out.println("\nSuche nach Dateien mit Deleted Konfliktmarker: Fehlgeschlagen");
					this.printStatus(git);	
					bReturn = false;
				}
				git.close();					
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
	return bReturn;
	}
	
	@Override
	public boolean searchConflictFilesDeletedit(Git git, String sProjectName) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
				bReturn = this.searchConflictFilesDeletedit(git, sProjectName, "Deleted_By_Theirs");					
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean searchConflictFilesDeletedit(Git git, String sProjectName, String sConflictType) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
				bReturn = this.searchConflictFilesByScanit(git, sProjectName, sConflictType);					
		}//end main:
		return bReturn;
	}
	
	
		
	
	
	//##############################################################################
	//### aus IJgitStarterCommit
	@Override
	public boolean commitit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try{
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++++++++++++++++++++++++++++++
				//Konfiguriere JGit für HTTPS
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				String sComment = objConfig.readComment();
				this.setCommentCommit(sComment);
			
				//+++++++++++++++++++++++++++++++
				//Finde geaenderte und neue Dateien fuer den commit
				Git git = this.getGitObject();
				boolean bSuccessCommit = this.commitit(git);
				if(bSuccessCommit) {
					System.out.println("\nSTATUS AFTER COMMIT: SUCCESSFUL");
					this.printStatus(git);
					  bReturn = true;
				}else {
					System.out.println("\nSTATUS AFTER COMMIT: FAILED");
					this.printStatus(git);	
					bReturn = false;
				}
			
			    git.close();
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}	
	
	//###############################################
	//### FLAG HANDLING
	//###############################################
			
	//### aus IJgitResolverEnabled	
	@Override
	public boolean getFlag(IJgitResolverEnabled.FLAGZ objEnum_IJgitResolverEnabled) throws ExceptionZZZ {
		return this.getFlag(objEnum_IJgitResolverEnabled.name());
	}
	
	@Override
	public boolean setFlag(IJgitResolverEnabled.FLAGZ objEnum_IJgitResolverEnabled, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlag(objEnum_IJgitResolverEnabled.name(), bFlagValue);
	}
	
	@Override
	public boolean[] setFlag(IJgitResolverEnabled.FLAGZ[] objaEnum_IJgitResolverEnabled, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnum_IJgitResolverEnabled)) {
				baReturn = new boolean[objaEnum_IJgitResolverEnabled.length];
				int iCounter=-1;
				for(IJgitResolverEnabled.FLAGZ objEnum_IJgitResolverEnabled:objaEnum_IJgitResolverEnabled) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnum_IJgitResolverEnabled, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}
	
	@Override
	public boolean proofFlagExists(IJgitResolverEnabled.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagExists(objEnumFlag.name());
	}
	
	@Override
	public boolean proofFlagSetBefore(IJgitResolverEnabled.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}
	
	//###################################
	//### FLAGLOCAL Handling
		
	@Override
	public boolean getFlagLocal(IJgitResolverEnabled.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.getFlagLocal(objEnumFlag.name());		
	}

	@Override
	public boolean setFlagLocal(IJgitResolverEnabled.FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagLocal(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagLocal(IJgitResolverEnabled.FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitResolverEnabled.FLAGZLOCAL objEnum_IJgitResolverEnabled:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlagLocal(objEnum_IJgitResolverEnabled, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagLocalExists(IJgitResolverEnabled.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagLocalExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagLocalSetBefore(IJgitResolverEnabled.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagLocalSetBefore(objEnumFlag.name());
	}

	//###################################
	//### FLAGCUSTOM Handling
		
	@Override
	public boolean getFlagCustom(IJgitResolverEnabled.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.getFlagCustom(objEnumFlag.name());		
	}

	@Override
	public boolean setFlagCustom(IJgitResolverEnabled.FLAGZCUSTOM objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagCustom(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagCustom(IJgitResolverEnabled.FLAGZCUSTOM[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitResolverEnabled.FLAGZCUSTOM objEnum_IJgitResolverEnabled:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlagCustom(objEnum_IJgitResolverEnabled, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagCustomExists(IJgitResolverEnabled.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagCustomSetBefore(IJgitResolverEnabled.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomSetBefore(objEnumFlag.name());
	}
}

