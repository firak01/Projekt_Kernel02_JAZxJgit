package use.tool.jgit.https;

public interface IJgitStarterHTTPSEnabled {
	// #############################################################
	// ### FLAGZ und FLAG - BASIS METHODEN
	// #############################################################

	public enum FLAGZ {
		IGNORE_CHECKOUT_CONFLICTS; // beim PULL / MERGE werden Konflikte unterdrückt, ggs. Verlust lokaler
									// Änderungen.
	}
}
