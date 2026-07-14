package use.jgit.tool.resolve;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEditor.PathEdit;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.system.Syso;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;
import use.jgit.resolve.JgitResolverLocal;
import use.jgit.util.JgitUtilZZZ;

public class JgitResolverDeletedUtilZZZ implements IConstantZZZ {
	private static final boolean DEBUG_DELETE_THEIRS = true;

	/**
	 * false = kompletten Cache ausgeben
	 * true  = nur den Cache-Eintrag der betreffenden Datei ausgeben
	 */
	private static final boolean DEBUG_CACHE_ONLY_TARGET = true;
	
	private static boolean commitMergeResolved(Git git) throws GitAPIException {
	    git.commit()
	       .setMessage("Merge resolved")
	       .call();
	    return true;
	}
	
	private static void debugRepositoryState(Git git, String sLabel, String sFilePathInRepository) throws GitAPIException, IOException {
		debugRepositoryState(git, sLabel, sFilePathInRepository, true);
	}
	
	private static void debugRepositoryState(Git git, String sLabel, String sFilePathInRepository, boolean bPrintOutput) throws GitAPIException, IOException {
		
		if(!DEBUG_DELETE_THEIRS) return;
		if(!bPrintOutput) return;
		

	    try {
		    Repository repository = git.getRepository();
		    File file = new File(repository.getWorkTree(), sFilePathInRepository);
	
			Syso.println("\n========== " + sLabel + " ==========");
			
		    boolean bExists = file.exists();
		    Syso.println("WorkTree file.exists(): " + bExists);
		    if (!bExists) {
		        Syso.println("\tAuch wenn die Datei nicht mehr da ist, weitermachen und sie aus dem Index entfernen.");
		    }
	
		    RepositoryState state = repository.getRepositoryState();
		    Syso.println("RepositoryState: " + state);
	
		    Status status = git.status().call();
		    Syso.println("conflicts: " + status.getConflicting());
		    Syso.println("removed:   " + status.getRemoved());
		    Syso.println("missing:   " + status.getMissing());
		    Syso.println("changed:   " + status.getChanged());
		    Syso.println("added:     " + status.getAdded());
	
		    DirCache cache = repository.readDirCache();
		    Syso.println("CACHE: HasUnmergedPaths = " + cache.hasUnmergedPaths());
	
		    // Git verwendet grundsätzlich '/', hier normierung notwendig falls Windows Pfade mit Backslash übergeben werden.
		    String sNormalized = JgitUtilZZZ.computeGitPath(sFilePathInRepository);
	
		    boolean bFound = false;
		    for(int i = 0; i < cache.getEntryCount(); i++) {
		        DirCacheEntry e = cache.getEntry(i);
	
		        if(DEBUG_CACHE_ONLY_TARGET) {
		            if(!e.getPathString().equals(sNormalized)) {
		                continue;
		            }
		            bFound = true;
		        }
	
		        Syso.println(
		                e.getPathString()
		                + " stage=" + e.getStage());
		    }
	
		    if(DEBUG_CACHE_ONLY_TARGET && !bFound) {
		        Syso.println("Cacheeintrag NICHT gefunden: " + sNormalized);
		    }
	    
	    } catch (ExceptionZZZ e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	 /** Bei der Strategie THEIRS soll die als Konflikt vorhandene Löschung sich im loklen Repository wiederfinden,
     *  sprich... LÖSCHEN 
     * @param git
     * @param sFilePathInRepository
     *        Hier den Dateipfad als String übergeben. Er wird so der Methode ..addFilepattern(...) übergeben.
     * @return
     * @throws ExceptionZZZ
     */
    public static boolean resolveDeletedTHEIRS(Git git, String sFilePathInRepositoryIn) throws ExceptionZZZ{
    	return resolveDeletedTHEIRS(git, sFilePathInRepositoryIn, true);
    }
	
    /** Bei der Strategie THEIRS soll die als Konflikt vorhandene Löschung sich im loklen Repository wiederfinden,
     *  sprich... LÖSCHEN 
     * @param git
     * @param sFilePathInRepository
     *        Hier den Dateipfad als String übergeben. Er wird so der Methode ..addFilepattern(...) übergeben.
     * @return
     * @throws ExceptionZZZ
     */
    public static boolean resolveDeletedTHEIRS(Git git, String sFilePathInRepositoryIn, boolean bPrintOutput) throws ExceptionZZZ{
        boolean bReturn = false;
        main:{
     	   try {
 	    	 
 	    	   if(StringZZZ.isEmptyTrimmed(sFilePathInRepositoryIn)) {
 	    		  ExceptionZZZ ez = new ExceptionZZZ("sFilePathInRepository", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
 	    	   }
 	    	   
 	    	   
 	    	  File fD=null;
 	    	  Repository repository = git.getRepository();
 	    	   
 	    	 //wichtig, normiere den Pfad
	    	 String sFilePathInRepository =  JgitUtilZZZ.computeGitPath(sFilePathInRepositoryIn);
	    	  
 	    	   
 	    	 //############################################### 				
 	    	 //### A
	    	 debugRepositoryState(git, "A) VOR git.rm()", sFilePathInRepository, bPrintOutput);
	    	 Syso.println("\nA) VERSUCH: Entferne aus dem Index per git.rm().addFilepattern(...)", bPrintOutput);
	    	 
 	    	  
 	    	  git.rm()
 	    	  .addFilepattern(sFilePathInRepository)
 	    	  .call();
				
				
 	    	  //### B  	    	  
 	    	  debugRepositoryState(git, "A) NACH git.rm()", sFilePathInRepository, bPrintOutput);
 	    	  
 	    	 
 	    	  fD = new File(repository.getWorkTree(), sFilePathInRepository);
 	    	  if (!fD.exists()) { 	    		
 	    		Syso.println("A) NACHHER. Erfolgreich mit rm aus dem Index entfert.", bPrintOutput); 	    			 	    			
 	    		
 	    		//Hier keinen commit machen, das ggfs. state des Repository=merged. Das würde Fehler werfen.
 	    	    //bReturn = commitMergeResolved(git);
 	    		bReturn = true;
 	    	    break main;
 	    	  }
 	    	  
 	    	  Syso.println("\nB) VERSUCH: Entferne aus dem Index per Cache.editor", bPrintOutput);
 	    	  
 	    	  DirCache cache = repository.lockDirCache();

 	    	  try {
 	    		  DirCacheEditor editor = cache.editor();
 	    		  PathEdit objPathEdit = new DirCacheEditor.DeletePath(sFilePathInRepository);

 	    		  editor.add(objPathEdit);
 	    		  editor.finish();

 	    		  cache.write();
 	    		  cache.commit();

 	    	  } finally {
 	    		  try {
 	    	        cache.unlock();
 	    		  } catch (Exception e) {
 	    			  Syso.println(e.getMessage());
 	    		  }
 	    	  }
				 
 	    	  debugRepositoryState(git, "B) NACH Cache.editor()", sFilePathInRepository, bPrintOutput);
 	   
 	    	  boolean bNoConflicts = git.status().call().getConflicting().isEmpty();
 	    	  
 	    	  Syso.println("B) NACHHER. Konflikt frei? '" + bNoConflicts + "'", bPrintOutput);
 	    	  
 	    	  
 	    	  if (bNoConflicts) {
 	    		  Syso.println("B) NACHHER. Datei erfolgreich aus Index entfernt.", bPrintOutput);
 	    		   	    		  
 	    		  //Hier keinen commit machen, das ggfs. state des Repository=merged. Das würde Fehler werfen.
 	    		  //bReturn = commitMergeResolved(git);
 	    		  bReturn = true;
 	    		  break main;

 	    	  } else {
 	    		  Syso.println("B) NACHHER. Mache Hardreset", bPrintOutput);
 	    		  
 	    		  git.reset()
 	    		  .setMode(ResetCommand.ResetType.HARD)
 	    		  .setRef("HEAD")
 	    		  .call();
 	    	  }
		

 	    	  //########### C
 	    	  fD = new File(repository.getWorkTree(), sFilePathInRepository);
 	    	  if (!fD.exists()) {
	    		Syso.println("B) NACHHER. Datei erfolgreich entfernt.", bPrintOutput);
 	    	 	    		
 	    		//Hier keinen commit machen, das ggfs. state des Repository=merged. Das würde Fehler werfen.
 	    	    //bReturn = commitMergeResolved(git);
 	    		bReturn = true;
 	    	    break main;
 	    	  }
 	    	  
 	    	 //### E
 	    	 Syso.println("C) VORHER. Vom Worktree, file.exists(): " + fD.exists(), bPrintOutput);
 	    	
 	    	 
 	    	 //Sicherstellen, dass die Datei auch wirklich gelöscht wird.
 	    	 boolean bDeletedExplizit = fD.delete(); 	    	 
 	    	 Syso.println("C) Ergebnis des expliziten Löschens: " + bDeletedExplizit, bPrintOutput); 	    	 
 	    	 
 	    	 if(bDeletedExplizit) { 	    	
 	    		Syso.println("C) NACHHER. Datei erfolgreich entfernt.", bPrintOutput);
 	    		
 	    		//Hier keinen commit machen, das ggfs. state des Repository=merged. Das würde Fehler werfen.
				//bReturn = commitMergeResolved(git);
 	    		bReturn = true;
				break main;					
 	    	  }else {
 	    		bReturn = false;
 	    	  }
			} catch (NoFilepatternException nfe) {
				ExceptionZZZ ez = new ExceptionZZZ(nfe);
				throw ez;
			} catch (GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}catch(Exception e) {
				ExceptionZZZ ez = new ExceptionZZZ(e);
				throw ez;
			}
        }//end main:
        return bReturn;
     }
    
    
    /** Hier den Dateipfad als String übergeben. Er wird so der Methode ..addFilepattern(...) übergeben.
     * @param git
     * @param sFilePathInRepository
     * @param strategy
     * @return
     * @throws ExceptionZZZ
     */
    public static boolean resolveDeletedOURS(Git git, String sFilePathInRepositoryIn) throws ExceptionZZZ{
    	return resolveDeletedOURS(git, sFilePathInRepositoryIn, true);
    }
    /** Hier den Dateipfad als String übergeben. Er wird so der Methode ..addFilepattern(...) übergeben.
     * @param git
     * @param sFilePathInRepository
     * @param strategy
     * @return
     * @throws ExceptionZZZ
     */
    public static boolean resolveDeletedOURS(Git git, String sFilePathInRepositoryIn, boolean bPrintOutput) throws ExceptionZZZ{
        boolean bReturn = false;
        main:{
     	   try {
 	    	   if(StringZZZ.isEmptyTrimmed(sFilePathInRepositoryIn)) {
 	    		  ExceptionZZZ ez = new ExceptionZZZ("sFilePathInRepository", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
 	    		  throw ez;
 	    	   }
 	    	   
 	    	   
 	    	   //###############################################
 	    	   //wichtig, normiere den Pfad
 	    	   String sFilePathInRepository =  JgitUtilZZZ.computeGitPath(sFilePathInRepositoryIn);
 	    	   
 	    	  debugRepositoryState(git, "A) VOR git.add("+sFilePathInRepository+")", sFilePathInRepository, bPrintOutput);
 	    	  Syso.println("\nA) VERSUCH: Hinzufügen zum Index per git.add().addFilepattern(...)", bPrintOutput);
 	    	   
 				//Code Snippet:
 				//Merke: das ist Strategieabhängig und StageState abhängig, was passieren soll.					
 				
				//Die Lokale Datei soll erhalten bleiben.
				git.add()
				   .addFilepattern(sFilePathInRepository)
				   .call();	
 					 				 
 			} catch (NoFilepatternException nfe) {
 				ExceptionZZZ ez = new ExceptionZZZ(nfe);
 				throw ez;
 			} catch (GitAPIException gae) {
 				ExceptionZZZ ez = new ExceptionZZZ(gae);
 				throw ez;
     	   }catch(Exception e) {
 		    	ExceptionZZZ ez = new ExceptionZZZ(e);
 		    	throw ez;
     	   }
     	   
     	   bReturn = true;
        }//end main:
        return bReturn;
     }
    
    public static boolean resolveDeleted(Git git, File objFile, STRATEGYMERGECONFLICT strategy) throws ExceptionZZZ{
    	return resolveDeleted(git, objFile, strategy, true);
    }
    
    public static boolean resolveDeleted(Git git, File objFile, STRATEGYMERGECONFLICT strategy, boolean bPrintOutput) throws ExceptionZZZ{
       boolean bReturn = false;
       main:{
    	   try {
	    	   if(objFile==null) {
					ExceptionZZZ ez = new ExceptionZZZ("FileObject", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
	    	   
	    	   if(strategy==null) {
					ExceptionZZZ ez = new ExceptionZZZ("StrategyObject", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
	    	   
	    	   boolean bFileExists = FileEasyZZZ.exists(objFile);
				if(!bFileExists) {
					ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePathTotal='" + objFile.getAbsolutePath() + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
				if(!bIsFile) {
					ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePathTotal='" + objFile.getAbsolutePath() + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//###############################################
				
				
				String sFilePathTotal = objFile.getAbsolutePath();
				String sFileName = objFile.getName();
				
				//TODOGOON20260630;//hier den relativen Pfad im Repository errechnen.
				String sFilePathInRepository = null;
				
				//Code Snippet:
				//Merke: das ist Strategieabhängig und StageState abhängig, was passieren soll.					
				//Fazit: Wg. der obigen Prüfungen, ob die Datei existiert... Lokal ist immer da!!!
				 if (strategy == STRATEGYMERGECONFLICT.OURS) {
					//Die Lokale Datei soll erhalten bleiben.
						git.add()
						   .addFilepattern(sFilePathInRepository)
						   .call();				
				 }else if( strategy == STRATEGYMERGECONFLICT.THEIRS) {
					//Aus Debuggründen: Ist die Datei im WorkTree
					
					//A) Vorher
					Repository repositoryA = git.getRepository();
					File fA = new File(repositoryA.getWorkTree(), sFileName);
					Syso.println("A) VORHER. Vom Worktree, file.exists(): " + fA.exists(), bPrintOutput);
					 
					//Die Löschung soll gewinnen (rm)
					DirCache objCache = git.rm()
					   .addFilepattern(sFilePathInRepository)
					   .call();
								
					//B) Nachher
					Repository repositoryB = git.getRepository();					
					File fB = new File(repositoryB.getWorkTree(), sFilePathTotal);
					Syso.println("B) NACHHER. Vom Worktree, file.exists(): " + fB.exists(), bPrintOutput);
					
					//DAS IST KEINE GUTE IDEE, HAUT ALLES KAPUTT
					//Sicherstellen, dass die Datei auch wirklich gelöscht wird.
					if (fB.exists()) {
					    bReturn = fB.delete();
					    Syso.println("\tErgebnis des Löschens: " + bReturn, bPrintOutput);
					}
				 }else {
					 Syso.println(ReflectCodeZZZ.getPositionCurrent() + ": Unerwartetet Strategy: '" + strategy.getName() + "'", bPrintOutput);
					 ExceptionZZZ ez = new ExceptionZZZ("Unerwartete Strategy: '" +strategy.getName() + "'", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					 throw ez;
				 }	
				 
				 
			} catch (NoFilepatternException nfe) {
				ExceptionZZZ ez = new ExceptionZZZ(nfe);
				throw ez;
			} catch (GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
    	   }catch(Exception e) {
		    	ExceptionZZZ ez = new ExceptionZZZ(e);
		    	throw ez;
    	   }
    	   
    	   bReturn = true;
       }//end main:
       return bReturn;
    }
}
