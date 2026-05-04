package use.jgit.tool.fetch;

import java.util.ArrayList;
import java.util.List;

public class ResultPostFetchAnalysis {

    private List<String> listaInfo = new ArrayList<String>();
    private List<String> listaProblem = new ArrayList<String>();


    public void addInfo(String sInfo) {
        if(sInfo != null) {
            listaInfo.add(sInfo);
        }
    }

    public void addProblem(String sProblem, String sLoesung) {

        String sEntry = sProblem;

        if(sLoesung != null && sLoesung.length() > 0) {
            sEntry += " | Lösung: " + sLoesung;
        }

        listaProblem.add(sEntry);
    }

    public boolean hasProblems() {
        return !listaProblem.isEmpty();
    }

    public String computeDebugString() {

        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append("#############################\n");
        sb.append("POST FETCH ANALYSIS\n");
        sb.append("#############################\n");

        sb.append("\nINFOS:\n");

        for(String s : listaInfo) {
            sb.append("- ").append(s).append("\n");
        }

        sb.append("\nPROBLEME:\n");

        if(listaProblem.isEmpty()) {
            sb.append("- Keine Probleme erkannt.\n");
        } else {

            for(String s : listaProblem) {
                sb.append("- ").append(s).append("\n");
            }
        }

        return sb.toString();
    }
}
