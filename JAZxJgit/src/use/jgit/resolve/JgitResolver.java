package use.jgit.resolve;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.FileTextParserZZZ;
import basic.zBasic.util.file.FileTextReaderZZZ;
import basic.zBasic.util.file.FileTextWriterZZZ;
import basic.zKernel.file.ini.IKernelZFormulaIni_VariableZZZ;
import use.jgit.AbstractJgitStarter;
import use.jgit.AbstractJgitStarterCommit;
import use.jgit.IJgitEnabledZZZ;
import use.jgit.JgitStarterMain;
import use.jgit.config.IConfigResolverJGIT;
import use.jgit.config.IConfigStarterJGIT;
import use.jgit.tool.resolve.GitConflictResolverUtil;
import use.jgit.util.JgitUtilZZZ;


//Die Ausgangsklasse konnte zwar Konflikte auflösen, aber nicht diese Änderung committen.
//public class JgitResolver<T> extends AbstractObjectWithFlagZZZ<T> implements IJgitResolver, IJgitResolverEnabled{

//Zwar muss nun auch ein Commit gemacht werden, aber das ist zuviel
//public class JgitResolver<T> extends AbstractJgitStarter<T> implements IJgitResolver, IJgitResolverEnabled{

//Also nutze daraus alles was für den Commit wichtig ist.
public class JgitResolver<T> extends AbstractJgitStarterCommit<T> implements IJgitResolver, IJgitResolverEnabled{
	private static final long serialVersionUID = 521157607363069534L;
	
	//### Konstruktor
	public JgitResolver() {	
		super();			
	}
	
	//### aus IJgitResolver
	@Override 
	public String getCommentCommitDefault() throws ExceptionZZZ{
		String sReturn="";
		main:{
			String sCommentCommitDefault = this.sCommentCommitDefault; 
			if(StringZZZ.isEmptyTrimmed(sCommentCommitDefault)) {
				//Es soll halt erkennbar sein, dass ein Konflikt aufgelöst worden ist.
				//und welche Strategie gewonnen hat.
				boolean bUseMergeStrategOURS=this.getFlagLocal(IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
				boolean bUseMergeStrategTHEIRS=this.getFlagLocal(IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
				
				String sStrategy = null;
				if(!bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
					sStrategy = IJgitResolverEnabled.ConflictStrategy.THEIRS.name();				
				}else if(bUseMergeStrategOURS & !bUseMergeStrategTHEIRS) {
					sStrategy = IJgitResolverEnabled.ConflictStrategy.OURS.name();
				}else if (bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
					//Fehler, was nun nehmen?
					ExceptionZZZ ez = new ExceptionZZZ("Widerspruechliche Stategien. Sowohl Flag für 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als auch Flag fuer 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEURS' sind gesetzt.", iERROR_PARAMETER_VALUE, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}else {
					//Default
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine Strategy per Flag gesetzt. Verwende Flagwert von 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als Default.");
					sStrategy = IJgitResolverEnabled.ConflictStrategy.OURS.name();
				}
				
				sReturn = "Conflict autoresolved. Strategy: " + sStrategy;			
			}else {
				sReturn = this.sCommentCommitDefault;
			}
		}//end main:
		return sReturn;
	}
	
	//##################################################
	//###### CONFLICT ######################################
	@Override
	public boolean conflictit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			//try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFilePath = objConfig.readFilePath();
				if(StringZZZ.isEmpty(sFilePath)) {
					ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sComment = objConfig.readComment();
				this.setCommentCommit(sComment);
				
				boolean bSuccessConflict = this.conflictit(sFilePath, sComment);
				if(bSuccessConflict) {
					System.out.println("STATUS AFTER RESOLVING CONFLICT: SUCCESSFUL ('" + sFilePath + "')");					
					bReturn = true;
				}else {
					System.out.println("STATUS AFTER RESOLVING CONFLICT: FAILED ('" + sFilePath + "')");					
					bReturn = false;
				}
			
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean conflictit(String sFilePathTotal) throws ExceptionZZZ {
		return this.conflictit(sFilePathTotal, null);
	}
	
	@Override
	public boolean conflictit(String sFilePathTotal, String sComment) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sFilePathTotal)) {
				ExceptionZZZ ez = new ExceptionZZZ("FilePath", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			File objFile = new File(sFilePathTotal);
			boolean bFileExists = FileEasyZZZ.exists(objFile);
			if(!bFileExists) {
				ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePathTotal='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
			if(!bIsFile) {
				ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePathTotal='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			FileTextReaderZZZ objReader = new FileTextReaderZZZ(objFile);
			String sContent = objReader.read();
			
			//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
			boolean bUseMergeStrategOURS=this.getFlagLocal(IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
			boolean bUseMergeStrategTHEIRS=this.getFlagLocal(IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
			String sResolved = null;
			if(!bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
				sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.THEIRS);				
			}else if(bUseMergeStrategOURS & !bUseMergeStrategTHEIRS) {
				sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.OURS);
			}else if (bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
				//Fehler, was nun nehmen?
				ExceptionZZZ ez = new ExceptionZZZ("Widerspruechliche Stategien. Sowohl Flag für 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als auch Flag fuer 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEURS' sind gesetzt.", iERROR_PARAMETER_VALUE, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}else {
				//Default
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine Strategy per Flag gesetzt. Verwende Flagwert von 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als Default.");
				sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.OURS);
			}
			FileTextWriterZZZ objWriter = new FileTextWriterZZZ(objFile);
			bReturn = objWriter.write(sResolved);
			
			//!!! HINWEIS AUF NOTWENDIGE WEITER AKTIONEN
			if(bReturn=true) {
				if(!bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie:\t " + IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
							  + "HINWEIS:\t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt. Ein Commit muss noch gemacht werden.";
					System.out.println(sLog);				
				}else if(bUseMergeStrategOURS & !bUseMergeStrategTHEIRS) {
					String sLog="Erfolgreiche Konfliktauflösung.\n"
							  + "Verwendete Stategie:\t " + IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
							  + "HINWEIS:\t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Ein Commit und PUSH muss  noch gemacht werden.";
					System.out.println(sLog);
				}else if (bUseMergeStrategOURS & bUseMergeStrategTHEIRS) {
					//Fehler, was nun nehmen?
					ExceptionZZZ ez = new ExceptionZZZ("Widerspruechliche Stategien. Sowohl Flag für 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als auch Flag fuer 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEURS' sind gesetzt.", iERROR_PARAMETER_VALUE, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}else {
					//Default
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine Strategy per Flag gesetzt. Verwende Flagwert von 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als Default.");
					sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.OURS);
				}
			}
		}//end main:
		return bReturn;
	}	
	//###########################################################
	//Normalerweise reicht ein einfacher commit nicht. Anschliessend muss aber ein Commit gemacht werden... 
	//Damit Eclipse das Aufloesen des Konflikts auch merkt.
	@Override
	public boolean conflictCommitit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			//try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFilePath = objConfig.readFilePath();
				if(StringZZZ.isEmpty(sFilePath)) {
					ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sRepositoryLocalBase = objConfig.readRepositoryLocal();//darf theoretisch leer sein
				this.setRepositoryLocalBase(sRepositoryLocalBase);
				String sRepositoryProject = objConfig.readRepositoryProjectName(); //darf theoretisch leer sein
				this.setRepositoryProject(sRepositoryProject);
				
				//Wenn der Filepath nicht absolut ist... baseRepository und Projekt holen und voranstellen
				String sFilePathTotal = JgitUtilZZZ.computeRepositoryLocalFilePath(sRepositoryLocalBase, sRepositoryProject, sFilePath);
				String sComment = objConfig.readComment();
				this.setCommentCommit(sComment);
				
				
				//Konfiguriere JGit für HTTPS							
				boolean bSuccess = this.configureGit();
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				//Finde geaenderte und neue Dateien fuer den commit
				Git git = this.getGitObject();				
				boolean bSuccessConflict = this.conflictCommitit(git, sFilePathTotal, sComment);
				if(bSuccessConflict) {
					System.out.println("STATUS AFTER RESOLVING CONFLICT: SUCCESSFUL ('" + sFilePathTotal + "')");					
					bReturn = true;
				}else {
					System.out.println("STATUS AFTER RESOLVING CONFLICT: FAILED ('" + sFilePathTotal + "')");					
					bReturn = false;
				}
				
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean conflictCommitit(Git git, String sFilePath) throws ExceptionZZZ {
		return this.conflictCommitit(git, sFilePath, null);
	}
	
	@Override
	public boolean conflictCommitit(Git git, String sFilePathTotal, String sComment) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		try {
			if(StringZZZ.isEmpty(sFilePathTotal)) {
				ExceptionZZZ ez = new ExceptionZZZ("FilePath", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			File objFile = new File(sFilePathTotal);
			boolean bFileExists = FileEasyZZZ.exists(objFile);
			if(!bFileExists) {
				ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePath='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
			if(!bIsFile) {
				ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePath='" + sFilePathTotal + "'", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			FileTextReaderZZZ objReader = new FileTextReaderZZZ(objFile);
			String sContent = objReader.read();
			
			//Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
			boolean bUseMergeStrategyOur=this.getFlagLocal(IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
			boolean bUseMergeStrategyTheir=this.getFlagLocal(IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
			String sResolved = null;
			if(!bUseMergeStrategyOur & bUseMergeStrategyTheir) {
				sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.THEIRS);
			}else if(bUseMergeStrategyOur & !bUseMergeStrategyTheir) {
				sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.OURS);
			}else if (bUseMergeStrategyOur & bUseMergeStrategyTheir) {
				//Fehler, was nun nehmen?
				ExceptionZZZ ez = new ExceptionZZZ("Widerspruechliche Stategien. Sowohl Flag für 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als auch Flag fuer 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEURS' sind gesetzt.", iERROR_PARAMETER_VALUE, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}else {
				//Default
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine Strategy per Flag gesetzt. Verwende Flagwert von 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als Default.");
				sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.OURS);
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
			if(!bUseMergeStrategyOur & bUseMergeStrategyTheir) {
				String sLog="Erfolgreiche Konfliktauflösung.\n"
						  + "Verwendete Stategie:\t " + IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
						  + "HINWEIS:\t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt.";
				System.out.println(sLog);				
			}else if(bUseMergeStrategyOur & !bUseMergeStrategyTheir) {
				String sLog="Erfolgreiche Konfliktauflösung.\n"
						  + "Verwendete Stategie:\t " + IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
						  + "HINWEIS:\t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Erst noch einen PUSH machen.";
				System.out.println(sLog);
			}else if (bUseMergeStrategyOur & bUseMergeStrategyTheir) {
				//Fehler, was nun nehmen?
				ExceptionZZZ ez = new ExceptionZZZ("Widerspruechliche Stategien. Sowohl Flag für 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als auch Flag fuer 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEURS' sind gesetzt.", iERROR_PARAMETER_VALUE, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}else {
				//Default
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine Strategy per Flag gesetzt. Verwende Flagwert von 'IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS' als Default.");
				sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.OURS);
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

	
	//### aus IJgitStarterCommit
	//##############################################################################
	@Override
	public boolean commitit(IConfigStarterJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try{
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
			//+++++++++++++++++++++++++++++++
			//Konfiguriere JGit für HTTPS
			boolean bSuccess = this.configureGit();
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
