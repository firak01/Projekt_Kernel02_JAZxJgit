package use.jgit.tool.merge;

import java.util.ArrayList;
import java.util.List;

public class ResultPreMergeCheck {

    private boolean clean = true;

    private List<String> problems = new ArrayList<String>();
    private List<String> solutions = new ArrayList<String>();

    public void addProblem(String problem, String solution) {
        this.clean = false;
        problems.add(problem);
        solutions.add(solution);
    }

    public boolean isClean() {
        return clean;
    }
    
    public void isClean(boolean clean) {
    	this.clean = clean;
    }

    public List<String> getProblems() {
        return problems;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public void printReport() {
        if (clean) {
            System.out.println("Repository ist sauber. Merge kann durchgeführt werden.");
            System.out.println("-------------------------------\n");
            return;
        }

        System.out.println("Repository ist NICHT sauber:");
        for (int i = 0; i < problems.size(); i++) {
        	//zusätzliche Trennzeilen zwischen den Lösungen
            if(i>=1) System.out.println("  -------------------------------\n");
            
            System.out.println("- Problem: " + problems.get(i));
            System.out.println("- Lösung: " + solutions.get(i));                      
        }
        System.out.println("-------------------------------\n");
    }
}
