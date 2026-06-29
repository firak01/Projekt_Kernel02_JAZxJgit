package use.jgit.tool.status;

import org.eclipse.jgit.api.Git;

public class GitStagingService {

    public void stageAll(Git git, GitStatusMap map) throws Exception {

        // 1. Modified / Changed
        for (String path : map.getModified()) {
            git.add().addFilepattern(path).call();
        }

        // 2. New files
        for (String path : map.getUntracked()) {
            git.add().addFilepattern(path).call();
        }

        // 3. Deletes (wichtig!)
        if (!map.getRemoved().isEmpty()) {
            git.add()
               .setUpdate(true)
               .addFilepattern(".")
               .call();
        }
    }
}
