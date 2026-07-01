package use.jgit.tool.resolve;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.resolve.JgitResolverLocal;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;

public class GitConflictResolverUtil implements IConstantZZZ {
    public static String resolveConflicts(String content, STRATEGYMERGECONFLICT strategy) {
        StringBuilder result = new StringBuilder();

        String[] lines = content.split("\\r?\\n");

        boolean inConflict = false;
        boolean isOursPart = false;
        boolean isTheirsPart = false;

        StringBuilder oursBuffer = new StringBuilder();
        StringBuilder theirsBuffer = new StringBuilder();

        for (String line : lines) {

            if (line.startsWith("<<<<<<<")) {
                inConflict = true;
                isOursPart = true;
                isTheirsPart = false;
                oursBuffer.setLength(0);
                theirsBuffer.setLength(0);
                continue;
            }

            if (inConflict && line.startsWith("=======")) {
                isOursPart = false;
                isTheirsPart = true;
                continue;
            }

            if (inConflict && line.startsWith(">>>>>>>")) {
                // Konfliktblock endet → entscheiden
                if (strategy == STRATEGYMERGECONFLICT.OURS) {
                    result.append(oursBuffer);
                } else {
                    result.append(theirsBuffer);
                }

                inConflict = false;
                isOursPart = false;
                isTheirsPart = false;
                continue;
            }

            if (inConflict) {
                if (isOursPart) {
                    oursBuffer.append(line).append("\n");
                } else if (isTheirsPart) {
                    theirsBuffer.append(line).append("\n");
                }
            } else {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }
    
    
    /** Hier den Dateipfad als String übergeben. Er wird so der Methode ..addFilepattern(...) übergeben.
     * @param git
     * @param sFilePathInRepository
     * @param strategy
     * @return
     * @throws ExceptionZZZ
     */
    public static boolean resolveDeleted(Git git, String sFilePathInRepository, STRATEGYMERGECONFLICT strategy) throws ExceptionZZZ{
        boolean bReturn = false;
        main:{
     	   try {
 	    	 
 	    	   if(strategy==null) {
 					ExceptionZZZ ez = new ExceptionZZZ("StrategyObject", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
 					throw ez;
 	    	   }
 	    	   
 	    	   if(StringZZZ.isEmptyTrimmed(sFilePathInRepository)) {
 	    		  ExceptionZZZ ez = new ExceptionZZZ("sFilePathInRepository", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
 	    	   }
 	    	   
 	    	   
 	    	   //###############################################
 				
 				
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
 					File fA = new File(repositoryA.getWorkTree(), sFilePathInRepository);
 					System.out.println("A) VORHER. Vom Worktree, file.exists(): " + fA.exists());
 					 
 					//DEBUG:
 					RepositoryState stateA = git.getRepository().getRepositoryState();
 					System.out.println(stateA);

 					Status statusA = git.status().call();
 					System.out.println(statusA.getConflicting());
 					System.out.println(statusA.getRemoved());
 					System.out.println(statusA.getMissing());
 					System.out.println(statusA.getChanged());
 					System.out.println(statusA.getAdded());
 					
 					//Die Löschung soll gewinnen (rm)
 					DirCache objCache = git.rm()
 					   .addFilepattern(sFilePathInRepository)
 					   .call();
 					
 					//DEBUG 2:
 					System.out.println("HasUnmergedPaths = " + objCache.hasUnmergedPaths());
 					for (int i = 0; i < objCache.getEntryCount(); i++) {
 					    DirCacheEntry e = objCache.getEntry(i);

// 					    if(e.getPathString().equals(sFilePathInRepository)) {
 					        System.out.println(
 					            e.getPathString()
 					            + " stage=" + e.getStage()
 					        );
// 					    }
 					}
 					
 						
 					//Versuchen den Konflikt als gelöst zu markieren
 					git.add()
 				   .addFilepattern(sFilePathInRepository)
 				   .call();
 					
 					
 					
 					//DEBUG:
 					RepositoryState stateB = git.getRepository().getRepositoryState();
 					System.out.println(stateB);

 					Status statusB = git.status().call();
 					System.out.println(statusB.getConflicting());
 					System.out.println(statusB.getRemoved());
 					System.out.println(statusB.getMissing());
 					System.out.println(statusB.getChanged());
 					System.out.println(statusB.getAdded());
 					
 					//DEBUG:
 					DirCache cache = git.getRepository().readDirCache();
 					System.out.println("HasUnmergedPaths = " + cache.hasUnmergedPaths());

 					for (int i = 0; i < cache.getEntryCount(); i++) {
 					    DirCacheEntry e = cache.getEntry(i);

// 					    if(e.getPathString().equals(sFilePathInRepository)) {
 					        System.out.println(
 					            e.getPathString()
 					            + " stage=" + e.getStage()
 					        );
// 					    }
 					}
 					
 					boolean bNoConflicts = git.status().call().getConflicting().isEmpty();
 					System.out.println("B) NACHHER. Konflikt frei? '" + bNoConflicts +"'" );
 					if(bNoConflicts) {
 						git.commit()
 					   .setMessage("Merge resolved")
 					   .call();
 					}
 					
 									
 					//B) Nachher
 					Repository repositoryB = git.getRepository();					
 					File fB = new File(repositoryB.getWorkTree(), sFilePathInRepository);
 					System.out.println("B) NACHHER. Vom Worktree, file.exists(): " + fB.exists());
 					
 					//Sicherstellen, dass die Datei auch wirklich gelöscht wird.
 					//DAS IST KEINE GUTE IDEE, HAUT ALLES KAPUTT
// 					boolean bDeleteSuccess = false;
// 					if (fB.exists()) {
// 					    bDeleteSuccess = fB.delete();
// 					    System.out.println("\tErgebnis des Löschens: " + bDeleteSuccess); 					     					   					     					
// 					}
 				 }else {
 					 System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Unerwartetet Strategy: '" + strategy.getName() + "'");
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
    
    public static boolean resolveDeleted(Git git, File objFile, STRATEGYMERGECONFLICT strategy) throws ExceptionZZZ{
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
					System.out.println("A) VORHER. Vom Worktree, file.exists(): " + fA.exists());
					 
					//Die Löschung soll gewinnen (rm)
					DirCache objCache = git.rm()
					   .addFilepattern(sFilePathInRepository)
					   .call();
								
					//B) Nachher
					Repository repositoryB = git.getRepository();					
					File fB = new File(repositoryB.getWorkTree(), sFilePathTotal);
					System.out.println("B) NACHHER. Vom Worktree, file.exists(): " + fB.exists());
					
					//DAS IST KEINE GUTE IDEE, HAUT ALLES KAPUTT
					//Sicherstellen, dass die Datei auch wirklich gelöscht wird.
//					boolean bDeleteSuccess = false;
//					if (fB.exists()) {
//					    bDeleteSuccess = fB.delete();
//					    System.out.println("\tErgebnis des Löschens: " + bDeleteSuccess);
//					}
				 }else {
					 System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Unerwartetet Strategy: '" + strategy.getName() + "'");
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
