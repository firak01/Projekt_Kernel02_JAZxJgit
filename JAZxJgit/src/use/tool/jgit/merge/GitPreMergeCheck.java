package use.tool.jgit.merge;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;

import basic.zBasic.IConstantZZZ;

public class GitPreMergeCheck implements IConstantZZZ{

    public static ResultPreMergeCheck checkRepositoryState(Git git) {
        ResultPreMergeCheck result = new ResultPreMergeCheck();

        try {
            Status status = git.status().call();

            // 1. Uncommitted Changes
            if (!status.getUncommittedChanges().isEmpty()) {
                result.addProblem("Uncommitted changes vorhanden", 
                    "Bitte committen oder stashen (git stash).");
            }

            // 2. Untracked Files
            if (!status.getUntracked().isEmpty()) {
                result.addProblem("Untracked files vorhanden", 
                    "Diese könnten beim Merge überschrieben werden. Prüfen oder hinzufügen.");
            }

            // 3. Conflicting (z. B. abgebrochener Merge)
            if (!status.getConflicting().isEmpty()) {
                result.addProblem("Es existieren ungelöste Merge-Konflikte", 
                    "Bitte Konflikte manuell auflösen und committen.");
            }

            // 4. Missing (gelöschte, aber nicht committed Dateien)
            if (!status.getMissing().isEmpty()) {
                result.addProblem("Gelöschte Dateien nicht committed", 
                    "Bitte Änderungen committen.");
            }

            // 5. Added aber nicht committed
            if (!status.getAdded().isEmpty()) {
                result.addProblem("Neue Dateien staged aber nicht committed", 
                    "Bitte committen.");
            }

            // 6. Changed aber nicht committed
            if (!status.getChanged().isEmpty()) {
                result.addProblem("Geänderte Dateien staged aber nicht committed", 
                    "Bitte committen.");
            }

            // Gesamtstatus
            result.isClean(status.isClean());

        } catch (Exception e) {
            result.addProblem("Fehler beim Status prüfen", e.getMessage());
        }

        return result;
    }
}
