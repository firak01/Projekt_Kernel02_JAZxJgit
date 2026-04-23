package use.tool.jgit.merge;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import use.tool.jgit.JgitUtil;

public class GitPostMergeAnalyse implements IConstantZZZ{

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
		            // optional: Info hinzufügen
		            // result.addProblem("Merge erfolgreich", "Keine Aktion erforderlich");
		            break main;
		        }
	
		        // =========================
		        // 2. Konflikte
		        // =========================
		        Map<String, int[][]> conflicts = mergeResult.getConflicts();
		        if(conflicts != null && !conflicts.isEmpty()) {
		            result.setConflicts(conflicts);
		            result.hasConflicts(true);

		            String sFiles = ResultUtil.formatSet(conflicts.keySet());

		            result.addProblem(
		                "Merge-Konflikte in folgenden Dateien:\n" + sFiles,
		                "Konflikte manuell auflösen und anschließend committen."
		            );
		        }
	
		        // =========================
		        // 3. Failing Paths
		        // =========================
		        //Merke: Aus Versionssicherheitsgründen MergeResult.MergeFailureReason angepasst mit generischem ?
		        Map<String, ?> failingPaths = mergeResult.getFailingPaths();
		        if(failingPaths != null && !failingPaths.isEmpty()) {
		            result.setFailingPaths(failingPaths);
		            result.hasFailures(true);

		            // Nur Dateinamen
		            String sFiles = ResultUtil.formatSet(failingPaths.keySet());

		            result.addProblem(
		                "Fehler beim Merge in folgenden Dateien:\n" + sFiles,
		                "Details prüfen und betroffene Dateien korrigieren."
		            );

		            // Optional: Details inkl. Reason
		            StringBuilder sbDetails = new StringBuilder();
		            int icount = 0;
		            for (Map.Entry<String, ?> entry : failingPaths.entrySet()) {
		                icount++;
		                if (icount >= 2) sbDetails.append("\n");

		                String path = entry.getKey();
		                Object reason = entry.getValue();

		                sbDetails.append("  * ").append(path);
		                if (reason != null) {
		                    sbDetails.append(" (").append(reason.toString()).append(")");
		                }
		            }

		            result.addProblem(
		                "Details zu Failing Paths:\n" + sbDetails.toString(),
		                "Ursachen analysieren (z. B. DIRTY_WORKTREE)."
		            );

		            // Spezielle Prüfung auf DIRTY_WORKTREE
		            for (Map.Entry<String, ?> entry : failingPaths.entrySet()) {
		                Object reason = entry.getValue();

		                if (reason != null && "DIRTY_WORKTREE".equals(reason.toString())) {
		                    result.isDirtyWorktree(true);

		                    result.addProblem(
		                        "Working Tree ist nicht sauber (DIRTY_WORKTREE)",
		                        "Lokale Änderungen committen oder stashen, dann Merge erneut durchführen."
		                    );
		                    break;
		                }
		            }
		        }
		    }//end main
	
		    return result;
		}

}
