package use.jgit.tool.push;

import java.util.ArrayList;

public class ResultPostPushAnalysis {

	private boolean bSuccess = true;

	private ArrayList<String> listaProblem = new ArrayList<String>();
	private ArrayList<String> listaLoesung = new ArrayList<String>();


	public void addProblem(String sProblem, String sLoesung) {
		this.listaProblem.add(sProblem);
		this.listaLoesung.add(sLoesung);

		this.bSuccess = false;
	}

	public boolean isSuccess() {
		return this.bSuccess;
	}

	public void printReport() {

		System.out.println("=== PUSH ANALYSE REPORT ===");

		if(this.isSuccess()) {
			System.out.println("Keine Probleme erkannt.");
			return;
		}

		for(int i = 0; i < this.listaProblem.size(); i++) {

			System.out.println((i+1) + ". Problem:");
			System.out.println(this.listaProblem.get(i));

			System.out.println("   Lösung:");
			System.out.println(this.listaLoesung.get(i));

			System.out.println("-----------------------------------");
		}
	}
}
