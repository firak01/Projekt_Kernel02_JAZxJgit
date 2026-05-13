package use.jgit.resolve;

import java.io.IOException;
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
import org.eclipse.jgit.merge.ResolveMerger.MergeFailureReason;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import use.jgit.JgitStarterMain;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;

public class JgitResolverUtilZZZ implements IConstantZZZ{
	public static boolean resolveConflicts(Git git, MergeResult objMergeResult, STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {				
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
			               .addPath(path)
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
	
	public static boolean resolveFailed(Git git, MergeResult objMergeResult) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
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
	
	public static boolean resolveFailed(Git git, MergeResult objMergeResult, STRATEGYMERGECONFLICT objEnumStrategy) throws Exception {
		boolean bReturn = false;
		main:{
			try {
				if(objMergeResult==null) {
					ExceptionZZZ ez = new ExceptionZZZ("MergeResult-Object", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				boolean bAnyFailingResolved = JgitResolverUtilZZZ.resolveFailed(git, objMergeResult);
			    if(bAnyFailingResolved) {            
			                //Hole das Ref-Objekt (jetzt direkt statt über das FetchResult-Objekt)
			                String sFetchRefs = "refs/heads/" + sBranch;
							//per fetchResult: Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs); //ohne das im Folgenden einzubinden, kommt die Fehlermeldung:    org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
			                //Vorschlag von chatGPT, direkt holen: Ref objRef = git.getRepository().exactRef("refs/remotes/origin/master");
			                Ref objRef = git.getRepository().exactRef(sFetchRefs);
			                
			                //Merge result Objekt (ist nur ein Snapshot) neu holen 
			                System.out.println("Starte Merge2:");
			                MergeCommand mergeCommand2 = git.merge();
			                mergeCommand2.include(objRef);
			                mergeCommand2.setStrategy(MergeStrategy.RECURSIVE);									 
							objReturn = mergeCommand2.call();
							
							MergeStatus status2 = objReturn.getMergeStatus();
							System.out.println("Merge-Status2:" + status2.toString());
							
							//Wenn aber keine Exception geworfen wird, den Status direkt abfragen									
			                //Aber nun gibt es den Merge-Status.CONFLICTING
			                if(status2.equals(MergeStatus.CONFLICTING)) {
							    System.out.println("Konflikte2 erkannt.");

							    Map<String, int[][]> conflicts = objReturn.getConflicts();

							    if(conflicts != null) {
							        for(String path2 : conflicts.keySet()) {

							        	System.out.println(objEnumStrategy.getDescriptionShort() + "2: " + path);

							            // Lokale Version wiederherstellen (= OURS)
							            //Besonderheit, nun ist man wirklich im UNMERGED Staus und bekommt folgenden Fehler
							            //org.eclipse.jgit.api.errors.JGitInternalException: Unmerged path: JAZDummy/Arbeit_mit_Git/test.txt
							            //
							            //Darum ist ein normaler Checkout nicht erlaubt.
							            //Es braucht noch die explizite Angabe OURS oder THEIRS
							            
							            
							            git.checkout()
							               .setStage(objStage)//z.B. CheckoutCommand.Stage.OURS
							               .addPath(path2)
							               .call();
							        }
							    }
			    
			    }
			
	    
	    
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
