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
	
	//### aus IJgitResolver
	
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
				
				//TODOGOON20260505; //Wenn der Filepath nicht absulut ist... baseRepository und Projekt holen und voranstellen
				                  //Am besten eine utility Methode bauen  ... createFilePathLocalUsed(baseRepo, Project, Filepath)
				                  //dann ist das an den verschiedenen Stellen flexibel.
				String sFilePath = objConfig.readFilePath();
				if(StringZZZ.isEmpty(sFilePath)) {
					ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sRepositoryLocalBase = objConfig.readRepositoryLocal();//darf theoretisch leer sein
				this.setRepositoryLocalBase(sRepositoryLocalBase);
				String sRepositoryProject = objConfig.readRepositoryProjectName(); //darf theoretisch leer sein
				this.setRepositoryProject(sRepositoryProject);
				
				String sFilePathTotal = JgitUtilZZZ.computeRepositoryLocalFilePath(sRepositoryLocalBase, sRepositoryProject, sFilePath);
				String sComment = objConfig.readComment();
				
				
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
			boolean bSuccessCommit = this.commitit(git, sComment);
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
			
			
			//TODOGOON20260503
			//IDEE: Wenn THEIRS gewinnt, dann bleibt alles lokal
			//      Wenn OURS gewinnt, dann muss das noch gepusht werden.

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
		
			//+++++++++++++++++++++++++++++++
			//Finde geaenderte und neue Dateien fuer den commit
			Git git = this.getGitObject();
			boolean bSuccessCommit = this.commitit(git, sComment);
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
	
	@Override
	public boolean commitit(Git git) throws ExceptionZZZ {
		return this.commitit(git, null);
	}
	
	@Override
	public boolean commitit(Git git, String sCommentIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				//Finde geaenderte und neue Dateien fuer den Commit			
				System.out.println("STATUS BEFORE COMMIT");		
				this.printStatus(git);
		        //##################################################################
		        
				//Fuege geänderte Dateien, die schon im Repository sind, hinzu.
				this.addFileTrackedChanged(git);
				
				//Fuege neue Dateien hinzu, die noch nicht im Repository sind.
		        this.addFileUntracked(git);
				
		        //Mache einen commit (mit aktuellem Datum/Uhrzeit) & Namen der Maschine
		        String sComment = JgitUtilZZZ.createCommentCommit(sCommentIn);
		        
				CommitCommand gitCommandCommit = git.commit();
				gitCommandCommit.setMessage(sComment);
				gitCommandCommit.call();
		        
		        System.out.println("STATUS AFTER COMMIT");
		        this.printStatus(git);
		        
		        bReturn = true;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
		

	@Override
	public void addFileTrackedChanged() throws ExceptionZZZ {		
		Git git = this.getGitObject();
		this.addFileTrackedChanged(git);       
	}
	
	@Override
	public void addFileTrackedChanged(Git git) throws ExceptionZZZ {		
		try {
			StatusCommand gitCommandStatus = git.status();
			Status status = gitCommandStatus.call();
	
			Set<String> uncommittedChanges = status.getUncommittedChanges();
			Set<String> untracked          = status.getUntracked();
			ArrayList<String> listasUncommitedChanges = new ArrayList<String>();
			
			AddCommand gitCommandAdd = git.add();		
	        for (String uncommitted : uncommittedChanges) {
	        	if(!untracked.contains(uncommitted)) {
	        		listasUncommitedChanges.add(uncommitted);
	        	}
	        }
	        
	        // run the add-call 
	        for(String uncommitted : listasUncommitedChanges) {
	        	System.out.println("uncommitted to add: '" + uncommitted + "'");
	        	try {
	        		gitCommandAdd.addFilepattern(uncommitted);
	        		gitCommandAdd.call();
	        	}catch(java.lang.IllegalStateException isex) {
	        		System.out.println(isex.getMessage());
	        		
	        		ExceptionZZZ ez = new ExceptionZZZ(isex);
	        		throw ez;
	        	}
	        }
		}catch (NoWorkTreeException nwte) {
			System.out.println(nwte.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(nwte);
    		throw ez;
		}catch( GitAPIException gae) {
			System.out.println(gae.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(gae);
    		throw ez;
		}
       
	}
	
	@Override
	public void addFileUntracked() throws ExceptionZZZ {	
		Git git = this.getGitObject();
		this.addFileUntracked(git);
	}
	
	@Override
	public void addFileUntracked(Git git) throws ExceptionZZZ {
	
		try {
			Status status = git.status().call();
	
			Set<String> setUntracked = status.getUntracked();
			ArrayList<String> listasUntracked = new ArrayList<String>();
	        for (String  sUntracked : setUntracked ) {
	        	listasUntracked.add(sUntracked);
	        }
	        
	        for(String sUntracked : listasUntracked) {
	        	git.add().addFilepattern(sUntracked).call();
	        }
		}catch (NoWorkTreeException nwte) {
			System.out.println(nwte.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(nwte);
    		throw ez;
		}catch( GitAPIException gae) {
			System.out.println(gae.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(gae);
    		throw ez;
		}
	
	}


	


	
	//###############################################
	//### FLAG HANDLING
	//###############################################
			
	//aus IJgitResolverEnabled
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
	
	//++++++++++++++++++++++++++++++++++++++++++++++
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

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
