package use.jgit.resolve;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.measure.spi.SystemOfUnits;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.abstractList.ArrayListUtilZZZ;
import basic.zBasic.util.abstractList.ArrayListZZZ;
import basic.zBasic.util.datatype.string.StringArrayZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.FileTextReaderZZZ;
import basic.zBasic.util.file.FileTextWriterZZZ;
import use.jgit.AbstractJgitStarterLocal;
import use.jgit.JgitStarterMain;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigResolverJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.tool.resolve.JgitResolverDeletedUtilZZZ;
import use.jgit.tool.resolve.JgitResolverConflictPostUtilZZZ;
import use.jgit.util.JgitUtilZZZ;


//Die Ausgangsklasse konnte zwar Konflikte auflösen, aber nicht diese Änderung committen.
//public class JgitResolver<T> extends AbstractObjectWithFlagZZZ<T> implements IJgitResolver, IJgitResolverEnabled{

//Zwar muss nun auch ein Commit gemacht werden, aber das ist zuviel
//public class JgitResolver<T> extends AbstractJgitStarter<T> implements IJgitResolver, IJgitResolverEnabled{

//Also nutze daraus alles was für den Commit wichtig ist.
public class JgitResolverLocal<T> extends AbstractJgitStarterLocal<T> implements IJgitResolver, IJgitResolverEnabled{
	private static final long serialVersionUID = 521157607363069534L;	
	private List<File> listFile=null; //Liste von Dateien, hier die Dateien mit Konfliktmarker
	private List<String> listasRepositoryPaths=null; //Liste von Strings, die Dateipfaden im Repository entsprechen.
	
	
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
		return listFile;
	}
	
	@Override
	public void setFiles(List<File>listFile) throws ExceptionZZZ{
		this.listFile = listFile;
	}
	
	
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
	public boolean resolveByStageStateit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		// TODO Auto-generated method stub
		
		//Code Snippet
//		Status status = git.status().call();
//		IndexDiff.StageState stageState = status.getConflictingStageState().get(path);
		
		
		return false;
	}

	@Override
	public boolean resolveByStageStateit(Git git, String sFilepathTotal) throws ExceptionZZZ {
		// TODO Auto-generated method stub
		
		
		return false;
	}
	
	
	//##################################################
	//###### RESOLVEDELETED #######################################	
	@Override
	public boolean resolveConflictDeletedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
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
					
				
								
				if(bUseList && listasPathInRepository!=null) {
					//Liste von Dateien verarbeiten
					ArrayList<String>listasFileSuccess = new ArrayList<String>();
					ArrayList<String>listasFileFailed = new ArrayList<String>();
					for(String  sPathInRepository : listasPathInRepository) {
						boolean bSuccessConflict = this.resolveConflictDeletedit(git, sPathInRepository);
						if(bSuccessConflict) {
							listasFileSuccess.add(sPathInRepository);
						}else {
							listasFileFailed.add(sPathInRepository);
						}
					}//end for
					
					System.out.println("\nSTATUS AFTER RESOLVING DELETED: SUCCESSFUL");
					if(listasFileSuccess.isEmpty()) {
						System.out.println("* NO FILE");
						bReturn = true;
					}else {						
						String[] saFile = ArrayListUtilZZZ.toStringArray(listasFileSuccess);
						saFile = StringArrayZZZ.plusString( "* ", saFile);
						String sPrint = StringArrayZZZ.implode(saFile, StringZZZ.crlf());
						System.out.println(sPrint);
						bReturn = false;
					}								
											
					System.out.println("\nSTATUS AFTER RESOLVING DELETED: FAILED");
					if(listasFileFailed.isEmpty()) {
						System.out.println("* NO FILE");
						bReturn = true;
					}else {						
						String[] saFile = ArrayListUtilZZZ.toStringArray(listasFileFailed);
						saFile = StringArrayZZZ.plusString( "* ", saFile);
						String sPrint = StringArrayZZZ.implode(saFile, StringZZZ.crlf());
						System.out.println(sPrint);
						bReturn = false;	
					}								
						

				}else {
					//Einzelne Datei verarbeiten.
					boolean bSuccessConflict = this.resolveConflictDeletedit(git, sFilePath);
					if(bSuccessConflict) {
						System.out.println("STATUS AFTER RESOLVING DELETED: SUCCESSFUL");
						System.out.println("* " + sFilePath);					
						bReturn = true;
					}else {
						System.out.println("STATUS AFTER RESOLVING DELETED: FAILED");
						System.out.println("* " + sFilePath);					
						bReturn = false;
					}
				}
			
		}//end main:
		return bReturn;
	}

	@Override
	public boolean resolveConflictDeletedit(Git git, String sFilePathInRepository) throws ExceptionZZZ {				
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
			
			
			//DAS KANN MAN NOCH OPTIMIEREN.
			//STRATEGIE UND VORHANDENSEIN DER DATEI
//			String sFilePathTotal = null;
//			boolean bPathRelative = FileEasyZZZ.isPathRelative(sFilePathInRepository);
//			if(bPathRelative) {
//				String sDirectoryRepoProjectTotal = this.getRepositoryLocalTotal();
//				sFilePathTotal = FileEasyZZZ.joinFilePathName(sDirectoryRepoProjectTotal, sFilePathInRepository);
//			}else {
//				sFilePathTotal = sFilePathInRepository;
//			}
			
			
//			File objFile = new File(sFilePathTotal);
//			boolean bFileExists = FileEasyZZZ.exists(objFile);
//			if(!bFileExists) {
//				ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePathTotal='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
//			
//			boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
//			if(!bIsFile) {
//				ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePathTotal='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
			
			boolean bResolvedSuccess=false;			
			if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {						
				bResolvedSuccess = JgitResolverDeletedUtilZZZ.resolveDeletedTHEIRS(git, sFilePathInRepository);				
			}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {			
				bResolvedSuccess = JgitResolverDeletedUtilZZZ.resolveDeletedOURS(git, sFilePathInRepository);
			}else {
				//Default
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
				ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
				
			}			
			bReturn = bResolvedSuccess;
			
			//!!! HINWEIS AUF NOTWENDIGE WEITER AKTIONEN
			if(bReturn=true) {
				if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
				//if(!bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
							  + "HINWEIS: \t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt. Ein zusätzlicher Commit muss ggfs. noch gemacht werden.";
					System.out.println(sLog);
				}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
				//}else if(bUseMergeStrategOURS & !bUseMergeStrategTHEIRS) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
							  + "HINWEIS: \t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Ein Commit und PUSH muss  noch gemacht werden.";
					System.out.println(sLog);							
				}else {
					//Default
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
					ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
		}//end main:
		return bReturn;
	}
	
	//##################################################
	//###### RESOLVECONFLICT ######################################
	@Override
	public boolean resolveConflictit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
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
							
				if(bUseListFile && listFile!=null) {
					//Liste von Dateien verarbeiten
					ArrayList<String>listasFileSuccess = new ArrayList<String>();
					ArrayList<String>listasFileFailed = new ArrayList<String>();
					for(File objFile : listFile) {
						boolean bSuccessConflict = this.resolveConflictit(sFilePath);
						if(bSuccessConflict) {
							listasFileSuccess.add(sFilePath);
						}else {
							listasFileFailed.add(sFilePath);
						}
					}//end for
					
					System.out.println("\nSTATUS AFTER RESOLVING CONFLICT: SUCCESSFUL");
					if(listasFileSuccess.isEmpty()) {
						System.out.println("* NO FILE");
						bReturn = true;
					}else {						
						String[] saFile = ArrayListUtilZZZ.toStringArray(listasFileSuccess);
						saFile = StringArrayZZZ.plusString( "* ", saFile);
						String sPrint = StringArrayZZZ.implode(saFile, StringZZZ.crlf());
						System.out.println(sPrint);
						bReturn = false;
					}								
											
					System.out.println("\nSTATUS AFTER RESOLVING CONFLICT: FAILED");
					if(listasFileFailed.isEmpty()) {
						System.out.println("* NO FILE");
						bReturn = true;
					}else {						
						String[] saFile = ArrayListUtilZZZ.toStringArray(listasFileFailed);
						saFile = StringArrayZZZ.plusString( "* ", saFile);
						String sPrint = StringArrayZZZ.implode(saFile, StringZZZ.crlf());
						System.out.println(sPrint);
						bReturn = false;	
					}								
						

				}else {
					//Einzelne Datei verarbeiten.
					boolean bSuccessConflict = this.resolveConflictit(sFilePath);
					if(bSuccessConflict) {
						System.out.println("STATUS AFTER RESOLVING CONFLICT: SUCCESSFUL");
						System.out.println("* " + sFilePath);					
						bReturn = true;
					}else {
						System.out.println("STATUS AFTER RESOLVING CONFLICT: FAILED");
						System.out.println("* " + sFilePath);					
						bReturn = false;
					}
				}
			
		}//end main:
		return bReturn;
	}
	
		
	@Override
	public boolean resolveConflictit(String sFilePathTotalIn) throws ExceptionZZZ {
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
			//if(!bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
				//sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS);				
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS);				
			}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
				//}else if(bUseMergeStrategOURS & !bUseMergeStrategTHEIRS) {
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS);
			}else {
				//Default
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
				ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
				
			}
			FileTextWriterZZZ objWriter = new FileTextWriterZZZ(objFile);
			bReturn = objWriter.write(sResolved);
			
			//!!! HINWEIS AUF NOTWENDIGE WEITER AKTIONEN
			if(bReturn=true) {
				if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
				//if(!bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
							  + "HINWEIS: \t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt. Ein Commit muss noch gemacht werden.";
					System.out.println(sLog);
				}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
				//}else if(bUseMergeStrategOURS & !bUseMergeStrategTHEIRS) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
							  + "HINWEIS: \t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Ein Commit und PUSH muss  noch gemacht werden.";
					System.out.println(sLog);							
				}else {
					//Default
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
					ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
		}//end main:
		return bReturn;
	}	
	//###########################################################
	//Normalerweise reicht ein einfacher commit nicht. Anschliessend muss aber ein Commit gemacht werden... 
	//Damit Eclipse das Aufloesen des Konflikts auch merkt.
	@Override
	public boolean resolveConflictCommitit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			//try {
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
				
				String sComment = objConfig.readComment();
				this.setCommentCommit(sComment);
				
				Git git = this.getGitObject();
				
				if(bUseListFile && listFile!=null) {
					//Liste von Dateien verarbeiten
					ArrayList<String>listasFileSuccess = new ArrayList<String>();
					ArrayList<String>listasFileFailed = new ArrayList<String>();
					for(File objFile : listFile) {
						sFilePath = objFile.getAbsolutePath();
						boolean bSuccessConflict = this.resolveConflictit(sFilePath);
						if(bSuccessConflict) {
							listasFileSuccess.add(sFilePath);
						}else {
							listasFileFailed.add(sFilePath);
						}
					}//end for
					
					System.out.println("\nSTATUS AFTER RESOLVING CONFLICT: SUCCESSFUL");
					if(listasFileSuccess.isEmpty()) {
						System.out.println("* NO FILE");
						bReturn = false;
					}else {						
						String[] saFile = ArrayListUtilZZZ.toStringArray(listasFileSuccess);
						saFile = StringArrayZZZ.plusString( "* ", saFile);
						String sPrint = StringArrayZZZ.implode(saFile, StringZZZ.crlf());
						System.out.println(sPrint);
						bReturn = true;
					}								
											
					System.out.println("\nSTATUS AFTER RESOLVING CONFLICT: FAILED");
					if(listasFileFailed.isEmpty()) {
						System.out.println("* NO FILE");
						bReturn = true;
					}else {						
						String[] saFile = ArrayListUtilZZZ.toStringArray(listasFileFailed);
						saFile = StringArrayZZZ.plusString( "* ", saFile);
						String sPrint = StringArrayZZZ.implode(saFile, StringZZZ.crlf());
						System.out.println(sPrint);
						bReturn = false;	
					}		
					if(!bReturn) break main;
					
					bReturn = this.commitit(git, sComment);//Alle Dateien auf einmal committen						
				}else {
					//Einzelne Datei verarbeiten.					
					String sRepositoryLocalBase = this.getRepositoryLocalBase();
					String sFilePathTotal = JgitUtilZZZ.computeRepositoryLocalFilePath(sRepositoryLocalBase, sRepositoryProject, sFilePath);
					boolean bSuccessConflict = this.resolveCommitit(git, sFilePathTotal, sComment);
					if(bSuccessConflict) {
						System.out.println("STATUS AFTER RESOLVING CONFLICT: SUCCESSFUL");
						System.out.println("* " + sFilePath);					
						bReturn = true;
					}else {
						System.out.println("STATUS AFTER RESOLVING CONFLICT: FAILED");
						System.out.println("* " + sFilePath);					
						bReturn = false;
					}
				}
				
//				//Finde geaenderte und neue Dateien fuer den commit
////				//Wenn der Filepath nicht absolut ist... baseRepository und Projekt holen und voranstellen
//				String sRepositoryLocalBase = this.getRepositoryLocalBase();
//				String sFilePathTotal = JgitUtilZZZ.computeRepositoryLocalFilePath(sRepositoryLocalBase, sRepositoryProject, sFilePath);
//				String sComment = objConfig.readComment();
//				this.setCommentCommit(sComment);
//				
//				Git git = this.getGitObject();				
//				boolean bSuccessConflict = this.resolveCommitit(git, sFilePathTotal, sComment);
//				if(bSuccessConflict) {
//					System.out.println("STATUS AFTER RESOLVING CONFLICT: SUCCESSFUL ('" + sFilePathTotal + "')");					
//					bReturn = true;
//				}else {
//					System.out.println("STATUS AFTER RESOLVING CONFLICT: FAILED ('" + sFilePathTotal + "')");					
//					bReturn = false;
//				}
				
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean resolveCommitit(Git git, String sFilePath) throws ExceptionZZZ {
		return this.resolveCommitit(git, sFilePath, null);
	}
	
	@Override
	public boolean resolveCommitit(Git git, String sFilePathTotal, String sComment) throws ExceptionZZZ {
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
			//if(!bUseMergeStrategyOur & bUseMergeStrategyTheir) {
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS);
			}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
			//}else if(bUseMergeStrategyOur & !bUseMergeStrategyTheir) {
				sResolved = JgitResolverConflictPostUtilZZZ.resolveConflicts(sContent, IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS);						
			}else {
				//Default
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
				System.out.println("STATUS AFTER COMMIT: SUCCESSFUL");
				this.printStatus(git);
				bReturn = true;
			}else {
				System.out.println("STATUS AFTER COMMIT: FAILED");
				this.printStatus(git);	
				bReturn = false;
			}
		
		    git.close();
			
			
			//!!! HINWEIS AUF NOTWENDIGE WEITER AKTIONEN, absichtlich hier nicht den PUSH durchführen, 
			//                                            das wären für den Resolver zuviele weitere Parameter/Methoden
			if(bReturn=true) {
				if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
				//if(!bUseMergeStrategyOur & bUseMergeStrategyTheir) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie:\t " + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
							  + "HINWEIS:\t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt.";
					System.out.println(sLog);
				}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
				//}else if(bUseMergeStrategyOur & !bUseMergeStrategyTheir) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie:\t " + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
							  + "HINWEIS:\t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Erst noch einen PUSH machen.";
					System.out.println(sLog);					
				}else {
					//Default
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
					ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
	
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
	@Override
	public boolean searchConflictFilesit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
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
							
				//+++++++++++++++++++++++++++++++
				//Finde Dateien mit Konfliktmarker
				String sProjectName = objConfig.readRepositoryProjectName(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben 				
				Git git = this.getGitObject();
				boolean bSuccessSearch = this.searchConflictFilesit(git, sProjectName);
				if(bSuccessSearch) {
					List<File>listaFile=this.getFiles();
					System.out.println("\nDateien mit Konfliktmarker:");
					if(listaFile==null || listaFile.size()==0) {					
						System.out.println("* Keine Dateien gefunden");
					}else {
						for(File objFile : listaFile) {
							System.out.println("* " + objFile.getAbsolutePath());
						}
					}
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
	public boolean searchConflictFilesit(Git git, String sProjectName) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			if(StringZZZ.isEmptyTrimmed(sProjectName)) {
				ExceptionZZZ ez = new ExceptionZZZ("Name des Projekts im Repository, Argument aus der Kommandozeile, oder '*' für alle.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
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
	public boolean searchConflictFilesByScanit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
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
							
				//+++++++++++++++++++++++++++++++
				//Finde Dateien mit Konfliktmarker
				String sProjectName = objConfig.readRepositoryProjectName(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben 				
				Git git = this.getGitObject();
				boolean bSuccessSearch = this.searchConflictFilesByScanit(git, sProjectName);
				if(bSuccessSearch) {
					List<File>listaFile=this.getFiles();
					System.out.println("\nDateien mit Konfliktmarker:");
					if(listaFile==null || listaFile.size()==0) {					
						System.out.println("* Keine Dateien gefunden");
					}else {
						for(File objFile : listaFile) {
							System.out.println("* " + objFile.getAbsolutePath());
						}
					}
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
	public boolean searchConflictFilesDeletedit(Git git, String sProjectName) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
				bReturn = this.searchConflictFilesByScanit(git, sProjectName);					
		}//end main:
		return bReturn;
	}
	
	
		@Override
		public boolean searchConflictFilesDeletedit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
			boolean bReturn = false;
			main:{
				try {
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
					
					
					Git git = this.getGitObject();
					if (git == null) {
			            throw new IllegalArgumentException("git must not be null");
			        }
					
					String sProjectName = objConfig.readRepositoryProjectName(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben
					boolean bSuccessSearch = this.searchConflictFilesByScanit(git, sProjectName);	
					if(bSuccessSearch) {
						List<String>listaPathInRepository=this.getRepositoryPathStrings();
						System.out.println("\nDateien mit Deleted Konfliktmarker:");
						if(listaPathInRepository==null || listaPathInRepository.size()==0) {					
							System.out.println("* Keine Dateien gefunden");
						}else {
							for(String  sPath : listaPathInRepository) {
								System.out.println("* " + sPath );
							}
						}
						bReturn = true;
					}else {
						System.out.println("\nSuche nach Dateien mit Deleted Konfliktmarker: FAILED");
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
		public boolean searchConflictFilesByScanit(Git git, String sProjectName) throws ExceptionZZZ {
			boolean bReturn = false;
			main:{
				try {
					if (git == null) {
			            throw new IllegalArgumentException("git must not be null");
			        }
					
					if(StringZZZ.isEmptyTrimmed(sProjectName)) {
						ExceptionZZZ ez = new ExceptionZZZ("Name des Projekts im Repository, Argument aus der Kommandozeile, oder '*' für alle.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					File objFileRepository = git.getRepository().getDirectory();
					File objFileDirectory = objFileRepository.getParentFile();
					
					String sFileDirectoryName = FileEasyZZZ.getNameOnly(objFileDirectory);
					
					List<File> listFile = null; List<String> listRepositoryPath = null; File objFileProject = null;
					if(sProjectName.equals(sFileDirectoryName) | sProjectName.equals("*")) {
						objFileProject = objFileDirectory;
					}else {
						String sFileDirectory = FileEasyZZZ.joinFilePathName(objFileDirectory, sProjectName);
						objFileProject = new File(sFileDirectory);				
					}
					//listFile = JgitResolverConflictPostUtilZZZ.findFilesWithConflictMarkers(objFileProject);
			        //this.setFiles(listFile);
					
					List<GitConflictInfoZZZ> conflicts = GitConflictScannerZZZ.scan(git.getRepository());
					
	
					for (GitConflictInfoZZZ info : conflicts) {
	
					    switch (info.getConflictType()) {
	
					    case DELETE_BY_THEIRS:
					        System.out.println("Remote gelöscht: "
					                + info.getRepositoryPath());
	
					        //vorhandene Methode aufrufen
					        //resolveDeleteConflictTheirs(...);
					        
					        //oder in die Liste aufnehmen
					        this.getRepositoryPathStrings().add(info.getRepositoryPath());	
					        break;
	
					    case DELETE_BY_OURS:
					        System.out.println("Lokal gelöscht: "
					                + info.getRepositoryPath());
					        break;
	
					    case CONTENT:
					        System.out.println("Textkonflikt: "
					                + info.getRepositoryPath());
					        break;
	
					    default:
					        break;
					    }
					}
				
				} catch (IOException ioe) {
					ExceptionZZZ ez = new ExceptionZZZ(ioe);
					throw ez;
				}
				
		        bReturn = true;							
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
					System.out.println("STATUS AFTER COMMIT: SUCCESSFUL");
					this.printStatus(git);
					  bReturn = true;
				}else {
					System.out.println("STATUS AFTER COMMIT: FAILED");
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

