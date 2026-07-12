package use.jgit.resolve;

public class GitConflictInfoZZZ {

    private String repositoryPath;

    private EnumGitConflictTypeZZZ conflictType;

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public EnumGitConflictTypeZZZ getConflictType() {
        return conflictType;
    }

    public void setConflictType(EnumGitConflictTypeZZZ conflictType) {
        this.conflictType = conflictType;
    }

    @Override
    public String toString() {
        return conflictType + " : " + repositoryPath;
    }
}
