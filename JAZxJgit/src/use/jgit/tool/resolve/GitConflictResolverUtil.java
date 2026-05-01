package use.jgit.tool.resolve;

import use.jgit.resolve.IJgitResolverEnabled.ConflictStrategy;

public class GitConflictResolverUtil {
    public static String resolveConflicts(String content, ConflictStrategy strategy) {
        StringBuilder result = new StringBuilder();

        String[] lines = content.split("\\r?\\n");

        boolean inConflict = false;
        boolean isOursPart = false;
        boolean isTheirsPart = false;

        StringBuilder oursBuffer = new StringBuilder();
        StringBuilder theirsBuffer = new StringBuilder();

        for (String line : lines) {

            if (line.startsWith("<<<<<<<")) {
                inConflict = true;
                isOursPart = true;
                isTheirsPart = false;
                oursBuffer.setLength(0);
                theirsBuffer.setLength(0);
                continue;
            }

            if (inConflict && line.startsWith("=======")) {
                isOursPart = false;
                isTheirsPart = true;
                continue;
            }

            if (inConflict && line.startsWith(">>>>>>>")) {
                // Konfliktblock endet → entscheiden
                if (strategy == ConflictStrategy.OURS) {
                    result.append(oursBuffer);
                } else {
                    result.append(theirsBuffer);
                }

                inConflict = false;
                isOursPart = false;
                isTheirsPart = false;
                continue;
            }

            if (inConflict) {
                if (isOursPart) {
                    oursBuffer.append(line).append("\n");
                } else if (isTheirsPart) {
                    theirsBuffer.append(line).append("\n");
                }
            } else {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }
}
