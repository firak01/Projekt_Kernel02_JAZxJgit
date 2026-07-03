package use.jgit.tool.status;

import java.util.HashSet;
import java.util.Set;

public class GitStatusMap {

    private final Set<String> modified = new HashSet<>();
    private final Set<String> added = new HashSet<>();
    private final Set<String> removed = new HashSet<>();
    private final Set<String> untracked = new HashSet<>();

    public Set<String> getModified() { return modified; }
    public Set<String> getAdded() { return added; }
    public Set<String> getRemoved() { return removed; }
    public Set<String> getUntracked() { return untracked; }
}
