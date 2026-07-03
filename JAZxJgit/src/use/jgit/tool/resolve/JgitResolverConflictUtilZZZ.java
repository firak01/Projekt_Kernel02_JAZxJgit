package use.jgit.tool.resolve;

import java.util.Map;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.merge.ResolveMerger.MergeFailureReason;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import use.jgit.JgitStarterMain;
import use.jgit.resolve.EnumSetMappedStrategyMergeConflictUtilZZZ;
import use.jgit.resolve.IJgitResolverEnabled;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;
import use.jgit.util.JgitUtilZZZ;

public class JgitResolverConflictUtilZZZ implements IConstantZZZ{
	public static boolean resolveConflicts(Git git, MergeResult objMergeResult, STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {	
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				if(objMergeResult==null) {
					ExceptionZZZ ez = new ExceptionZZZ("MergeResult-Object", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(objEnumStrategy==null) {
					ExceptionZZZ ez = new ExceptionZZZ("EnumStrategy-Object", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				boolean bAnyConflictsResolved = false;
													
				//Konflikte holen und auflösen
			    Map<String, int[][]> conflicts = objMergeResult.getConflicts();
			    if(conflicts != null) {
			
			    	//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
					CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
						    
			        for(String path : conflicts.keySet()) {
			        	bReturn = true;
			            System.out.println(objEnumStrategy.getDescriptionShort() + ": " + path);
		
			            // Lokale Version wiederherstellen (= OURS)
			            //ohne setStage... Fehler: org.eclipse.jgit.api.errors.JGitInternalException: Unmerged path: JAZDummy/Arbeit_mit_Git/test.txt
			            git.checkout()
			               .setStage(objStage) //z.B. CheckoutCommand.Stage.OURS
			               .addPath(path)      //hier erst einmal weggelassen: .setForce(true)   // wichtig!
			               .call();
			            bAnyConflictsResolved=true;
			        }	        	        
			    }
		
			    if(bAnyConflictsResolved) {
			    	// Konfliktzustand beenden:
			    	git.add().addFilepattern(".").call();
		
			    	git.commit()
			    	.setMessage("Konflikte automatisch mit '" + objEnumStrategy.getName() + "' aufgelöst")
			    	.call();
			    }			
			    
				bReturn = bAnyConflictsResolved;
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	/** Variante ohne eine Strategie die Failed-Dateien auf den HEAD zurückzusetzen.
	 * @param git
	 * @param objMergeResult
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static boolean resolveFailed(Git git, MergeResult objMergeResult) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				if(objMergeResult==null) {
					ExceptionZZZ ez = new ExceptionZZZ("MergeResult-Object", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				boolean bAnyFailingResolved = false;
				
				Map<String, MergeFailureReason> failingPaths = objMergeResult.getFailingPaths();
			    if(failingPaths != null) {
			    	for(Map.Entry<String, MergeFailureReason> entry : failingPaths.entrySet()) {

			            String path = entry.getKey();
			            MergeFailureReason reason = entry.getValue();

			            System.out.println(path + " -> " + reason);

			            if(reason == MergeFailureReason.DIRTY_INDEX
			               || reason == MergeFailureReason.DIRTY_WORKTREE) {

			            	System.out.println("Behandle Datei: " + path);

			                //wirkt aber nicht zuverlässig bei failed:
			                //git.checkout().addPath(path).call();
			                
			                //darum:
			                //reicht aber nicht 
			                //git.reset().addPath(path).call();
			                
			                //darum:
			                git.checkout().setStartPoint("HEAD").addPath(path).call();
			                bAnyFailingResolved=true;
			            }     
			               
			    	}
			    }
			    bReturn = bAnyFailingResolved;
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
	    return bReturn;
	}
	
	public static boolean resolveFailed(Git git, MergeResult objMergeResult, STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				if(objMergeResult==null) {
					ExceptionZZZ ez = new ExceptionZZZ("MergeResult-Object", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
					    
				//... kann man hier das gleiche machen wie bei Konflikten? Nein ... es gibt keine Liste von Conflicts.
				
				//Unabhängig vom Status... hole die Jgit-Konfliktstrategie, abhängig von der ZKernel-Konfliktstartegie (, die durch FLAGZLOCAL definiert worden ist)
			    System.out.println("Failed: Meine Strategy '" + objEnumStrategy.getName() + "' in STAGE umsetzen.");
				CheckoutCommand.Stage objStage = EnumSetMappedStrategyMergeConflictUtilZZZ.getJgitStageAccordingStrategy(objEnumStrategy);
									    
			    Map<String, ResolveMerger.MergeFailureReason> faileds = objMergeResult.getFailingPaths();

			    if(faileds != null) {
			        for(String path : faileds.keySet()) {

			        	System.out.println(objEnumStrategy.getDescriptionShort() + ": " + path);

			            // Lokale Version wiederherstellen (z.B. OURS)
			            git.checkout()
			            	.setStage(objStage) //z.B. OURS
			            	.addPath(path)
			            	.call();
			        }
			    }

			    // Konfliktzustand beenden:
			    git.add().addFilepattern(".").call();

			    git.commit()
			       .setMessage("Failed: Automatisch mit '" + objEnumStrategy.getName() + "' aufgelöst")
			       .call();
			    
			    bReturn = true;				
		}catch(InvalidRemoteException ire) {
			ExceptionZZZ ez = new ExceptionZZZ(ire);
			throw ez;
		}catch(TransportException te) {
			ExceptionZZZ ez = new ExceptionZZZ(te);
			throw ez;
		}catch(GitAPIException gae) {
			ExceptionZZZ ez = new ExceptionZZZ(gae);
			throw ez;
		}
		}//end main:
	    return bReturn;
	}
}
