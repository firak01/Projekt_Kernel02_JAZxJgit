package use.jgit.tool.push;
import java.util.Collection;

//import org.eclipse.jgit.lib.RemoteRefUpdate; //falsch für JGit 4.5.x
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.PushResult;

public class GitPostPushAnalyse {

	public static ResultPostPushAnalysis analyzePushResult(PushResult pushResult) {

		ResultPostPushAnalysis objReturn = new ResultPostPushAnalysis();

		main:{
			if(pushResult == null) {
				objReturn.addProblem(
						"PushResult ist NULL.",
						"Prüfe, ob pushCommand.call() korrekt ausgeführt wurde."
				);
				break main;
			}

			Collection<RemoteRefUpdate> colRemoteUpdates =
					pushResult.getRemoteUpdates();

			if(colRemoteUpdates == null || colRemoteUpdates.isEmpty()) {

				objReturn.addProblem(
						"Keine Remote Updates vorhanden.",
						"Prüfe Remote Repository und Branch-Konfiguration."
				);

				break main;
			}

			for(RemoteRefUpdate update : colRemoteUpdates) {

				RemoteRefUpdate.Status status = update.getStatus();

				String sRemoteName = update.getRemoteName();

				// =====================================================
				if(status == RemoteRefUpdate.Status.OK) {

					System.out.println("OK: " + sRemoteName);

				}else if(status == RemoteRefUpdate.Status.UP_TO_DATE) {

					System.out.println("UP_TO_DATE: " + sRemoteName);

				}else if(status == RemoteRefUpdate.Status.REJECTED_NONFASTFORWARD) {

					objReturn.addProblem(
							"Push wurde abgelehnt (NONFASTFORWARD): " + sRemoteName,
							"Vorher git pull bzw. Merge/Rebase durchführen."
					);

				}else if(status == RemoteRefUpdate.Status.REJECTED_NODELETE) {

					objReturn.addProblem(
							"Löschen eines Remote-Branches wurde abgelehnt: " + sRemoteName,
							"Prüfe Remote-Rechte und Branch-Schutz."
					);

				}else if(status == RemoteRefUpdate.Status.REJECTED_REMOTE_CHANGED) {

					objReturn.addProblem(
							"Remote Branch wurde zwischenzeitlich geändert: " + sRemoteName,
							"Erneut pullen und Konflikte auflösen."
					);

				}else if(status == RemoteRefUpdate.Status.NON_EXISTING) {

					objReturn.addProblem(
							"Remote Referenz existiert nicht: " + sRemoteName,
							"Prüfe Branchname und Remote-Konfiguration."
					);

				}else if(status == RemoteRefUpdate.Status.AWAITING_REPORT) {

					objReturn.addProblem(
							"Push wartet noch auf Abschlussmeldung: " + sRemoteName,
							"Netzwerkverbindung und Remote-Server prüfen."
					);

				}else {

					objReturn.addProblem(
							"Unbekannter Push-Status: " + status + " bei " + sRemoteName,
							"Detailausgaben und Servermeldungen prüfen."
					);
				}
				// =====================================================
			}
		}// end main:

		return objReturn;
	}
}
