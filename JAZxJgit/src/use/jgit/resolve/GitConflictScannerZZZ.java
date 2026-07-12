package use.jgit.resolve;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Repository;

public class GitConflictScannerZZZ {

    public static List<GitConflictInfoZZZ> scan(Repository repository) throws IOException {

        List<GitConflictInfoZZZ> result = new ArrayList<GitConflictInfoZZZ>();

        DirCache cache = repository.readDirCache();

        // Repositorypfad -> Stages
        Map<String, StageInfo> map = new HashMap<String, StageInfo>();

        for (int i = 0; i < cache.getEntryCount(); i++) {

            DirCacheEntry entry = cache.getEntry(i);

            String path = entry.getPathString();

            StageInfo info = map.get(path);

            if (info == null) {
                info = new StageInfo();
                map.put(path, info);
            }

            switch (entry.getStage()) {

            case 1:
                info.stage1 = true;
                break;

            case 2:
                info.stage2 = true;
                break;

            case 3:
                info.stage3 = true;
                break;

            default:
                break;
            }
        }

        //--------------------------------------------------------
        // Klassifizieren
        //--------------------------------------------------------

        for (Map.Entry<String, StageInfo> me : map.entrySet()) {

            String path = me.getKey();
            StageInfo s = me.getValue();

            EnumGitConflictTypeZZZ type = determineType(s);

            if (type != EnumGitConflictTypeZZZ.NONE) {

                GitConflictInfoZZZ info = new GitConflictInfoZZZ();

                info.setRepositoryPath(path);
                info.setConflictType(type);

                result.add(info);
            }
        }

        return result;
    }

    private static EnumGitConflictTypeZZZ determineType(StageInfo s) {

        //---------------------------------------------------
        // Delete by THEM
        //---------------------------------------------------
        if (s.stage1 && s.stage2 && !s.stage3) {
            return EnumGitConflictTypeZZZ.DELETED_BY_THEIRS;
        }

        //---------------------------------------------------
        // Delete by US
        //---------------------------------------------------
        if (s.stage1 && !s.stage2 && s.stage3) {
            return EnumGitConflictTypeZZZ.DELETED_BY_OURS;
        }

        //---------------------------------------------------
        // Normal Merge
        //---------------------------------------------------
        if (s.stage1 && s.stage2 && s.stage3) {
            return EnumGitConflictTypeZZZ.CONTENT;
        }

        return EnumGitConflictTypeZZZ.NONE;
    }

    //====================================================
    // Hilfsklasse
    //====================================================

    private static class StageInfo {

        boolean stage1;
        boolean stage2;
        boolean stage3;
    }

}
