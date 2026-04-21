package use.tool.jgit.merge;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import use.tool.jgit.JgitUtil;

public class GitPostMergeAnalyse_BACKUP implements IConstantZZZ{

	public static ResultPostMergeAnalysis analyzeMergeResult(MergeResult mergeResult) throws ExceptionZZZ {
		    ResultPostMergeAnalysis result = new ResultPostMergeAnalysis();
	
		    main:{
		        if(mergeResult==null) {
		            ExceptionZZZ ez = new ExceptionZZZ("MergeResult", iERROR_PARAMETER_MISSING, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
		            throw ez;                
		        }
	
		        MergeResult.MergeStatus status = mergeResult.getMergeStatus();
		        result.setStatus(status);//.status = status;
	
		        // =========================
		        // 1. Erfolgsfall
		        // =========================
		        if(status != null && status.isSuccessful()) {
		            break main;
		        }
	
		        // =========================
		        // 2. Konflikte
		        // =========================
		        Map<String, int[][]> conflicts = mergeResult.getConflicts();
		        if(conflicts != null && !conflicts.isEmpty()) {
		            result.setConflicts(conflicts);//result.conflicts = conflicts;
		            result.hasConflicts(true);//result.hasConflicts = true;
		        }
	
		        // =========================
		        // 3. Failing Paths
		        // =========================
		        //Merke: Aus Versionssicherheitsgründen MergeResult.MergeFailureReason angepasst mit generischem ?
		        Map<String, ?> failingPaths = mergeResult.getFailingPaths();
		        if(failingPaths != null && !failingPaths.isEmpty()) {
		            result.setFailingPaths(failingPaths);
		            result.hasFailures(true);
	
		            // Speziell prüfen auf DIRTY_WORKTREE
	//	            for(Map.Entry<String, ?> entry : failingPaths.entrySet()) {
	//	                if(entry.getValue() == MergeResult.MergeFailureReason.DIRTY_WORKTREE) {
	//	                    result.isDirtyWorktree = true;
	//	                    break;
	//	                }
	//	            }
		            
		            for (Map.Entry<String, ?> entry : failingPaths.entrySet()) {
		                String filePath = entry.getKey();
		                Object reason = entry.getValue();
	
		                System.out.println(" - " + filePath + " : " + reason);
	
		                if (reason != null && "DIRTY_WORKTREE".equals(reason.toString())) {
		                	 result.isDirtyWorktree(true);
			                 break;
		                }
		            }
		        }
	
		    }//end main
	
		    return result;
		}

}
