package use.jgit.tool.fetch;

import java.util.Collection;

import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.ReceiveCommand.Result;
import org.eclipse.jgit.transport.TrackingRefUpdate;


import basic.zBasic.ExceptionZZZ;

public class GitPostFetchAnalyse {

    public static ResultPostFetchAnalysis analyzeFetchResult(FetchResult fetchResult) {

        ResultPostFetchAnalysis result = new ResultPostFetchAnalysis();

        main:{
            if(fetchResult == null) {
                result.addProblem(
                        "FetchResult ist NULL.",
                        "Prüfe die Rückgabe des fetch()-Aufrufs.");
                break main;
            }

            // #############################################################
            // Messages analysieren
            // #############################################################
            String sMessages = fetchResult.getMessages();

            if(sMessages != null && sMessages.trim().length() > 0) {

                result.addInfo("Fetch Message: " + sMessages);

                String sLower = sMessages.toLowerCase();

                if(sLower.contains("error")) {
                    result.addProblem(
                            "Fetch liefert Fehlermeldung.",
                            sMessages);
                }

                if(sLower.contains("reject")) {
                    result.addProblem(
                            "Fetch enthält Rejected-Hinweise.",
                            sMessages);
                }
            }

            // #############################################################
            // TrackingRefUpdates analysieren
            // #############################################################
            Collection<TrackingRefUpdate> colUpdates = fetchResult.getTrackingRefUpdates();

            if(colUpdates == null || colUpdates.isEmpty()) {

                result.addInfo("Keine TrackingRefUpdates vorhanden.");

            } else {

                for(TrackingRefUpdate update : colUpdates) {

                    String sLocalName = update.getLocalName();
                    String sRemoteName = update.getRemoteName();

                    //TrackingRefUpdate.Result updateResult = update.getResult();
                    org.eclipse.jgit.lib.RefUpdate.Result updateResult = update.getResult();

                    String sInfo = "TrackingRefUpdate: "
                            + sRemoteName
                            + " -> "
                            + sLocalName
                            + " | Result="
                            + updateResult;

                    result.addInfo(sInfo);

                    // #################################################
                    // Kritische Situationen
                    // #################################################

                    //if(updateResult == TrackingRefUpdate.Result.REJECTED) {                    
                    if(updateResult == org.eclipse.jgit.lib.RefUpdate.Result.REJECTED) {
                        result.addProblem(
                                "TrackingRefUpdate REJECTED für: " + sRemoteName,
                                "Lokalen Stand prüfen und erneut fetchen/pullen.");
                    }

                    //if(updateResult == TrackingRefUpdate.Result.IO_FAILURE) {
                    if(updateResult == org.eclipse.jgit.lib.RefUpdate.Result.IO_FAILURE) {

                        result.addProblem(
                                "IO_FAILURE bei: " + sRemoteName,
                                "Netzwerk- oder Repository-Zugriff prüfen.");
                    }                  
                }
            }
        }

        return result;
    }


    public static boolean logFetchResult(FetchResult fetchResult) throws ExceptionZZZ {

        boolean bReturn = false;

        main:{
            ResultPostFetchAnalysis analysis =
                    analyzeFetchResult(fetchResult);

            if(analysis == null) break main;

            System.out.println(analysis.computeDebugString());

            bReturn = true;
        }

        return bReturn;
    }
}
