package use.tool.jgit.merge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.MergeResult;

public class ResultPostMergeAnalysis {

    private MergeResult.MergeStatus status;

    private Map<String, int[][]> conflicts;
    private Map<String, ?> failingPaths;

    private boolean hasConflicts;
    private boolean hasFailures;
    private boolean isDirtyWorktree;

    // === NEU: Problem-/Lösungslisten ===
    private List<String> problems = new ArrayList<String>();
    private List<String> solutions = new ArrayList<String>();

    // =====================
    // addProblem (analog PreMerge)
    // =====================
    public void addProblem(String problem, String solution) {
        problems.add(problem);
        solutions.add(solution);
    }

    // =====================
    // Getter / Setter
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
    public void setFailingPaths(Map<String, ?> failingPaths) {
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

    public List<String> getProblems() {
        return problems;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    // =====================
    // Report (analog PreMerge)
    // =====================
    public void printReport() {
        if (isSuccessful() && problems.isEmpty()) {
            System.out.println("Merge erfolgreich und ohne Probleme.");
            return;
        }

        System.out.println("Probleme nach dem Merge:");

        for (int i = 0; i < problems.size(); i++) {
            System.out.println("- Problem: " + problems.get(i));
            System.out.println("  Lösung: " + solutions.get(i));
        }
    }

    @Override
    public String toString() {
        return "MergeAnalysisResult [status=" + status +
               ", hasConflicts=" + hasConflicts +
               ", hasFailures=" + hasFailures +
               ", isDirtyWorktree=" + isDirtyWorktree + "]";
    }
}
