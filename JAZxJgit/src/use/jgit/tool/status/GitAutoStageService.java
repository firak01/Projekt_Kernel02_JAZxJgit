package use.jgit.tool.status;

import org.eclipse.jgit.api.Git;

public class GitAutoStageService {

    private final GitStatusMapper mapper = new GitStatusMapper();
    private final GitStagingService staging = new GitStagingService();

    public void stage(Git git) throws Exception {

        GitStatusMap map = mapper.map(git);

        staging.stageAll(git, map);
    }
}
