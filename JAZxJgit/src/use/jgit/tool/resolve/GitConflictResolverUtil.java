package use.jgit.tool.resolve;

import java.io.File;

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
 					bReturn = fA.exists();
 					System.out.println("A) VORHER. Vom Worktree, file.exists(): " + bReturn);
 					if(bReturn) { 						
 					}else { 						
 						System.out.println("\tAuch wenn die Datei nicht mehr da ist, weitermachen und sie aus dem Index entfernen.");
 					}
 					
 					//DEBUG:
 					RepositoryState stateA = git.getRepository().getRepositoryState();
 					System.out.println(stateA);

 					Status statusA = git.status().call();
 					System.out.println("conflicts: " + statusA.getConflicting());
 					System.out.println("removed: " + statusA.getRemoved());
 					System.out.println("missing: " + statusA.getMissing());
 					System.out.println("changed: " + statusA.getChanged());
 					System.out.println("added: "   + statusA.getAdded());
 					
  					DirCache objCacheA = git.getRepository().readDirCache(); 					
 					System.out.println("\nCACHE A: HasUnmergedPaths = " + objCacheA.hasUnmergedPaths());
 					for (int i = 0; i < objCacheA.getEntryCount(); i++) {
 					    DirCacheEntry e = objCacheA.getEntry(i);
//hier fehlt eine Normierung \ nach / 
// 					    if(e.getPathString().equals(sFilePathInRepository)) {
 					        System.out.println(
 					            e.getPathString()
 					            + " stage=" + e.getStage()
 					        );
// 					    }
 					}
 					
 					//Lösungsansatz, da und JGit 4.5 das high-Leve. .rm nicht so funktioniert:
 					//Direkt im cache löschen.
 					//aber nicht vergessen den index "locked" zu machen, sonst Fehlermeldung wie:
 					//java.lang.IllegalStateException: DirCache C:\1fgl\repo\EclipseOxygen_V02\Projekt_Kernel02_JAZDummy\.git\index not locked
// 					DirCache cache = git.getRepository().readDirCache();
// 					DirCacheEditor editor = cache.editor();
// 					editor.add(new DirCacheEditor.DeletePath(sFilePathInRepository));
// 					editor.commit();
 					
 					//Versuchen den Konflikt als gelöst zu markieren
 					//Die Löschung soll gewinnen (rm)
 					//.setCached(true) // Wichtig: Entfernt die Datei nur aus dem Index, nicht vom Dateisystem
 					//wir wollen die Datei aber auch aus dem Dateisystem weg haben.
 					System.out.println("\nVERSUCH A: Entferne aus dem Index per git.rm");
 					git.rm()
 					   .addFilepattern(sFilePathInRepository) 					   
 				       .call();
 					
 					
 					//######## B 
 					Repository repositoryB = git.getRepository();
 					File fB = new File(repositoryB.getWorkTree(), sFilePathInRepository);
 					bReturn = fB.exists();
 					System.out.println("B) VORHER. Vom Worktree, file.exists(): " + bReturn);
 					if(bReturn) { 						
 					}else { 						
 						System.out.println("\tAuch wenn die Datei nicht mehr da ist, weitermachen und sie aus dem Index entfernen.");
 					}
 					
 					//DEBUG B:
 					RepositoryState stateB = git.getRepository().getRepositoryState();
 					System.out.println(stateB);

 					Status statusB = git.status().call();
 					System.out.println("conflicts: " + statusB.getConflicting());
 					System.out.println("removed: " + statusB.getRemoved());
 					System.out.println("missing: " + statusB.getMissing());
 					System.out.println("changed: " + statusB.getChanged());
 					System.out.println("added: "   + statusB.getAdded());
 					 	
 					//Debug 2b:
 					//Erneut das staging im Cache
 					DirCache objCacheB = git.getRepository().readDirCache();
 					System.out.println("\nCACHE B: HasUnmergedPaths = " + objCacheB.hasUnmergedPaths());
 					for (int i = 0; i < objCacheB.getEntryCount(); i++) {
 					    DirCacheEntry e = objCacheB.getEntry(i);
//hier fehlt eine Normierung \ nach / 
// 					    if(e.getPathString().equals(sFilePathInRepository)) {
 					        System.out.println(
 					            e.getPathString()
 					            + " stage=" + e.getStage()
 					        );
// 					    }
 					}	
 					
 					
 					System.out.println("\nVERSUCH B: Entferne aus dem Index per Cache.editor");
 					DirCache cache = git.getRepository().lockDirCache(); //damit Änderungen gemacht werden können, muss der Index gelocked werden.
 					try {
 					    DirCacheEditor editor = cache.editor(); 
 					    PathEdit objPathEdit = new DirCacheEditor.DeletePath(sFilePathInRepository);
 					    editor.add(objPathEdit); 					    
 					    editor.finish();
 					    //editor.commit(); //macht schon den unlock auf den index. danach ist alles kaputt...
 					    cache.write();
 					    cache.commit();
 					} finally {
 						try {
 					    cache.unlock();
 						}catch(Exception e) {
 							System.out.println(e.getMessage());
 						}
 					}
 					
 					
 					//########### C
 					Repository repositoryC = git.getRepository();
 					File fC = new File(repositoryC.getWorkTree(), sFilePathInRepository);
 					bReturn = fC.exists();
 					System.out.println("C) VORHER. Vom Worktree, file.exists(): " + bReturn);
 					if(bReturn) { 						
 					}else { 						
 						System.out.println("\tAuch wenn die Datei nicht mehr da ist, weitermachen und sie aus dem Index entfernen.");
 					}
 					
 					//DEBUG C:
 					RepositoryState stateC = git.getRepository().getRepositoryState();
 					System.out.println(stateC);

 					Status statusC = git.status().call();
 					System.out.println("conflicts: " + statusC.getConflicting());
 					System.out.println("removed: " + statusC.getRemoved());
 					System.out.println("missing: " + statusC.getMissing());
 					System.out.println("changed: " + statusC.getChanged());
 					System.out.println("added: "   + statusC.getAdded());
 					 	
 					

 					DirCache objCacheC = git.getRepository().readDirCache();
 					System.out.println("\nCACHE C: HasUnmergedPaths = " + objCacheC.hasUnmergedPaths());

 					for (int i = 0; i < objCacheC.getEntryCount(); i++) {
 					    DirCacheEntry e = objCacheC.getEntry(i);
//hier fehlt eine Normierung \ nach / 
// 					    if(e.getPathString().equals(sFilePathInRepository)) {
 					        System.out.println(
 					            e.getPathString()
 					            + " stage=" + e.getStage()
 					        );
// 					    }
 					}
 					
 									
 					boolean bNoConflicts = git.status().call().getConflicting().isEmpty();
 					System.out.println("C) NACHHER. Konflikt frei? '" + bNoConflicts +"'" );
 					if(bNoConflicts) {
 						System.out.println("C) NACHHER. Mache commit" );
 						git.commit()
 					   .setMessage("Merge resolved")
 					   .call();
 					}else {
 						//Immer noch Konflikt
 						System.out.println("C) NACHHER. Mache Hardreset" );
 						git.reset()
 	 				   .setMode(ResetCommand.ResetType.HARD)
 	 				   .setRef("HEAD")
 	 				   .call();
 	 					
 					}
 					
			
 					//D) Vorher
 					Repository repositoryD = git.getRepository();					
 					File fD = new File(repositoryD.getWorkTree(), sFilePathInRepository);
 					System.out.println("D) VORHER. Vom Worktree, file.exists(): " + fD.exists());
 					
 					//Sicherstellen, dass die Datei auch wirklich gelöscht wird.				
 					if (fD.exists()) {
 					    bReturn = fD.delete();
 					    System.out.println("\tErgebnis des Löschens: " + bReturn); 					     					   					     					
 					}else {
 						bReturn = true;
 					}
 					
 					
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
					if (fB.exists()) {
					    bReturn = fB.delete();
					    System.out.println("\tErgebnis des Löschens: " + bReturn);
					}
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
