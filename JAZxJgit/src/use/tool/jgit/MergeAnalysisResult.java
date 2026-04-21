package use.tool.jgit;

import java.util.Map;

import org.eclipse.jgit.api.MergeResult;

public class MergeAnalysisResult {

    private MergeResult.MergeStatus status;

    private Map<String, int[][]> conflicts;
    
    //Merke: Aus Versionssicherheitsgründen MergeResult.MergeFailureReason angepasst mit generischem ?
    //private Map<String, MergeResult.MergeFailureReason> failingPaths;
    private Map<String, ?> failingPaths;

    private boolean hasConflicts;
    private boolean hasFailures;
    private boolean isDirtyWorktree;

    // =====================
    // Getter
    // =====================
    public MergeResult.MergeStatus getStatus() {
        return status;
    }
    public void setStatus(MergeResult.MergeStatus status) {
    	this.status = status;
    }

    public Map<String, int[][]> getConflicts() {
        return conflicts;
    }
    public void setConflicts(Map<String, int[][]> conflicts) {
    	this.conflicts = conflicts;
    }

    public Map<String, ?> getFailingPaths() {
        return failingPaths;
    }
    
    public void setFailingPaths(Map<String, ?>failingPaths) {
    	this.failingPaths = failingPaths;
    }

    public boolean hasConflicts() {
        return hasConflicts;
    }
    
    public void hasConflicts(boolean hasConflicts) {
    	this.hasConflicts = hasConflicts;
    }
    

    public boolean hasFailures() {
        return hasFailures;
    }
    
    public void hasFailures(boolean hasFailures) {
    	this.hasFailures = hasFailures;
    }

    public boolean isDirtyWorktree() {
        return isDirtyWorktree;
    }
    
    public void isDirtyWorktree(boolean isDirtyWorktree) {
    	this.isDirtyWorktree = isDirtyWorktree;
    }

    public boolean isSuccessful() {
        return status != null && status.isSuccessful();
    }
    
   

    @Override
    public String toString() {
        return "MergeAnalysisResult [status=" + status +
               ", hasConflicts=" + hasConflicts +
               ", hasFailures=" + hasFailures +
               ", isDirtyWorktree=" + isDirtyWorktree + "]";
    }
}
