
package use.jgit.tool.merge;

import java.util.Set;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;

import basic.zBasic.IConstantZZZ;

public class GitPreMergeCheck implements IConstantZZZ {

    public static ResultPreMergeCheck checkRepositoryState(Git git) {
        ResultPreMergeCheck result = new ResultPreMergeCheck();

        try {
            Status status = git.status().call();

            // 1. Uncommitted Changes
            if (!status.getUncommittedChanges().isEmpty()) {
                result.addProblem(
                    "Uncommitted changes vorhanden:\n" + ResultUtil.formatSet(status.getUncommittedChanges()),
                    "Bitte committen oder stashen (git stash)."
                );
            }

            // 2. Untracked Files
            if (!status.getUntracked().isEmpty()) {
                result.addProblem(
                    "Untracked files vorhanden:\n" + ResultUtil.formatSet(status.getUntracked()),
                    "Diese könnten beim Merge überschrieben werden. Prüfen oder hinzufügen."
                );
            }

            // 3. Conflicting
            if (!status.getConflicting().isEmpty()) {
                result.addProblem(
                    "Es existieren ungelöste Merge-Konflikte:\n" + ResultUtil.formatSet(status.getConflicting()),
                    "Bitte Konflikte manuell auflösen und committen."
                );
            }

            // 4. Missing
            if (!status.getMissing().isEmpty()) {
                result.addProblem(
                    "Gelöschte Dateien nicht committed:\n" + ResultUtil.formatSet(status.getMissing()),
                    "Bitte Änderungen committen."
                );
            }

            // 5. Added aber nicht committed
            if (!status.getAdded().isEmpty()) {
                result.addProblem(
                    "Neue Dateien staged aber nicht committed:\n" + ResultUtil.formatSet(status.getAdded()),
                    "Bitte committen."
                );
            }

            // 6. Changed aber nicht committed
            if (!status.getChanged().isEmpty()) {
                result.addProblem(
                    "Geänderte Dateien staged aber nicht committed:\n" + ResultUtil.formatSet(status.getChanged()),
                    "Bitte committen."
                );
            }

            // Gesamtstatus
            result.isClean(status.isClean());

        } catch (Exception e) {
            result.addProblem("Fehler beim Status prüfen", e.getMessage());
        }

        return result;
    }
}