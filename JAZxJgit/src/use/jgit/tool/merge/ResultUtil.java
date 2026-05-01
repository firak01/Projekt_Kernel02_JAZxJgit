package use.jgit.tool.merge;

import java.util.Set;

public class ResultUtil {
	
	// Hilfsmethode zur schönen Ausgabe
    protected static String formatSet(Set<String> files) {
        StringBuilder sb = new StringBuilder();
        int icount=0;
        for (String file : files) {
        	icount++;
        	if(icount>=2) sb.append("\n");
            sb.append("  * ").append(file);
        }
        return sb.toString();
    }
}
