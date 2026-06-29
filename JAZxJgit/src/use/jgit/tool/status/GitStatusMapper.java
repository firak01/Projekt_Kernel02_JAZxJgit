package use.jgit.tool.status;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;

public class GitStatusMapper {

    public GitStatusMap map(Git git) throws Exception {

        Status status = git.status().call();

        GitStatusMap map = new GitStatusMap();

        // 1. Klassische Änderungen
        map.getModified().addAll(status.getModified());
        map.getModified().addAll(status.getChanged());

        // 2. Neue Dateien
        map.getAdded().addAll(status.getAdded());

        // 3. Gelöschte Dateien
        map.getRemoved().addAll(status.getRemoved());
        map.getRemoved().addAll(status.getMissing());

        // 4. Untracked Dateien
        map.getUntracked().addAll(status.getUntracked());

        return map;
    }
}
