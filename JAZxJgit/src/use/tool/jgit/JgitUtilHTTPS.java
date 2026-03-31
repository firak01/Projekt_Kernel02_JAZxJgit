package use.tool.jgit;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;

public class JgitUtilHTTPS {

	/** Für den HTTPS Weg:
	 * Merke: Bei Pull mit HTTPS ist es notwendig den pull in fetch und merge zu zerlegen
	 * 
	 *  Eine robuste Utility-Methode, die:
	
		pull() ausführt
		CheckoutConflictException gezielt abfängt
		nur die konfliktbehafteten Dateien zurücksetzt
		danach den Pull automatisch erneut versucht
		
		s. ChatGPT 20260323
	 * @param git
	 * @throws GitAPIException
	 * @author Fritz Lindhauer, 23.03.2026, 18:17:59
	 * @throws ExceptionZZZ 
	 */
	public static boolean pullIgnoreCheckoutConflictsHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
	        try {
	
				/*
				Frage:
				Wenn ich git.pull().setRemote(...) verwenden möchte und nicht einen in der .git\config verwendeten Namen angeben möchte.
				Kann ich dann auch eine URL mitgeben? Kann solch eine mitgegebene URL auch den "Personal Access Token" beinhalten?
				
				Antwort:
				Kurz gesagt: Nein, so wie du es dir vorstellst funktioniert es mit pull() nicht.
				VARIANTE 1. setRemote(...) erwartet keine URL
	
				In JGit ist:
				git.pull().setRemote("origin")
	
				👉 kein URL-Parameter, sondern der Name eines konfigurierten Remotes aus der .git/config.
	
				Also z. B.:
				[remote "origin"]
					url = https://github.com/user/repo.git
	
				➡️ setRemote("origin") = Referenz auf diesen Eintrag
				➡️ Direkte URL ist hier nicht vorgesehen
	
				VARIANTE 2. URL direkt übergeben? → Nur über fetch()
	
				Wenn du eine URL direkt verwenden willst, musst du den Pull zerlegen:
				👉 pull = fetch + merge
	
				Beispiel (HTTPS mit URL + Token)
				FetchResult fetchResult = git.fetch()
				.setRemote("https://<token>@github.com/user/repo.git")
				.call();
	
				git.merge()
				.include(fetchResult.getAdvertisedRef("refs/heads/main"))
				.call();
				 */
				
	        									
				//TODOGOON20260321; // Die Variante mit sPAT in der URL hat den Nachteil, das dies irgendwo im Log etc. auftauchen koennte
				//Darum versuchen dies ohne sPAT in URL zu realisieren
				//                  //Variante A) mit sPAT in URL
				//                  https://firak01:" + sPAT + "@github.com/firak01/Projekt_Kernel02_JAZDummy.git
				//
				//                  //Variante B) ohne sPAT in URL
				//                  https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	        
				PullCommand pullCommand = git.pull();
				String sUrlPartFromRepo = JgitUtil.computeRepositoryUrlPartFromUrlRepo(sRepoRemote);
				
				System.out.println("HTTPS-Loesung: Zerlege pull in fetch und merge");
							
				String sUrl = "https://firak01:" + sPAT + "@" + sUrlPartFromRepo;
				System.out.println("Url fuer Fetch: '" + sUrl + "'");
				
				//Aber wenn nichts zu fetchen ist, gibt es einen Fehler
				FetchResult fetchResult = JgitUtilHTTPS.fetchIgnoreNothingToFetch(git, sUrl, credentialsProvider);
				if(fetchResult==null) break main;
					
				//+++ Auswerten eines Fetch
				String sFetchResultMessages = fetchResult.getMessages();
				if(sFetchResultMessages!=null) {				
					System.out.println("Fetch-Result: " + sFetchResultMessages);
				}
					
				
				//++++++++++++++++++++++++++++++++						
				String sFetchRefs = "refs/heads/master";
				Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs); //ohne das im Folgenden einzubinden, kommt die Fehlermeldung:    org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
				
				/*Minierklaerung:
				siehe .git\config Datei, entsprechende Zeile.
				 
				Das ist ein sogenannter RefSpec (Reference Specification).
				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
				
				Aufbau allgemein:
				[+]<Quelle>:<Ziel>
				
				Also:
				Quelle (Remote-Seite)
				refs/heads/ = alle Branches im Remote-Repository
				 * = Wildcard → alle Branch-Namen
	
				➡️ Bedeutet:
				Hole alle Branches vom Remote
				
				
				Ziel (lokal)
				refs/remotes/origin/ = Remote-Tracking-Branches
				* = gleicher Name wie Quelle
	
				➡️ Bedeutet:
				Speichere sie lokal als origin/branchname
				
				------------
				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
				Mit + sagst du:
				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
				 */
				
					
				//+++ Ausfuehren des merge, und Auffangen ggfs. vorhandener Konflikte
				System.out.println("Starte Merge:");
				try {
					//den richtigen Branch ansteuern
					String branch = "master"; // oder dynamisch

					String localRef = "refs/remotes/origin/" + branch;
					String remoteRef = "refs/heads/" + branch;
					
					//ObjectId remoteMaster = git.getRepository().resolve("refs/remotes/origin/master");
					ObjectId remoteMaster = git.getRepository().resolve(remoteRef);
					System.out.println("Verwende remoteMaster= '" + remoteMaster.getName() + "'");
					System.out.println("Verwende remoteMaster= '" + remoteMaster.toString() + "'");
					
					MergeCommand mergeCommand = git.merge();
					//geht hier nicht, da nur lokal, mergeCommand.setRemote(sUrl);
					//Also so versuchen.
					//mergeCommand.include(git.getRepository().resolve("FETCH_HEAD")); //ABER: Da hier 2 HEADs sind Fehler : org.eclipse.jgit.api.errors.InvalidMergeHeadsException: merge strategy recursive does not support 2 heads to be merged into HEAD
					//Lösungsansatz: direkt den richtigen Branch verwenden
					//mergeCommand.include(git.getRepository().resolve("refs/remotes/origin/master"));					
					mergeCommand.include(remoteMaster);
					mergeCommand.include(objRef); //ohne das kommt die Fehlermeldung:                 org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
					mergeCommand.setStrategy(MergeStrategy.RECURSIVE);
					 
					MergeResult mergeResult = mergeCommand.call();
					System.out.println("Merge-Status:" + mergeResult.getMergeStatus());
																					
					//###############################################################
		        } catch (CheckoutConflictException cce) {
		
		            Collection<String> conflictingPaths = cce.getConflictingPaths();
		
		            if (conflictingPaths == null || conflictingPaths.isEmpty()) {
		                // Kein konkreter Pfad bekannt → weiterwerfen
		            	ExceptionZZZ ez = new ExceptionZZZ(cce);
		    			throw ez;
		            }
	
		            //Konfliktdateien gezielt zurücksetzen
		            for (String path : conflictingPaths) {
		                git.checkout()
		                   .addPath(path)
		                   .call();
		            }
		
		            //Pull erneut versuchen
		            git.pull().call();
		        }
				
				bReturn = true;	
	        }catch(IOException ioe) {
	        	ExceptionZZZ ez = new ExceptionZZZ(ioe);
	        	throw ez;
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}       
	    }//end main:
	    return bReturn;
	}
	
	
	public static boolean pullSingleBranchHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
	        try {
	
		        if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
		        if (remoteUrl == null || remoteUrl.trim().isEmpty()) {
		            throw new IllegalArgumentException("remoteUrl must not be empty");
		        }
		        if (branch == null || branch.trim().isEmpty()) {
		            branch = "master"; // Default für Java 1.7 Projekte 😉
		        }
		
		        Repository repo = git.getRepository();
		
		        String remoteRef = "refs/heads/" + branch;
		        String localTrackingRef = "refs/remotes/origin/" + branch;
		
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();
		        fetchCommand.setRemote(remoteUrl);
		        fetchCommand.setRefSpecs(new RefSpec(remoteRef + ":" + localTrackingRef));
		
		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		
		        fetchCommand.call();
		
		        // =========================
		        // 2. MERGE (gezielt!)
		        // =========================
		        ObjectId remoteBranchObjectId = repo.resolve(localTrackingRef);
		
		        if (remoteBranchObjectId == null) {
		            throw new IllegalStateException("Remote branch not found after fetch: " + localTrackingRef);
		        }
		
		        MergeCommand mergeCommand = git.merge();
		        mergeCommand.include(remoteBranchObjectId);
		        mergeCommand.setStrategy(MergeStrategy.RECURSIVE);
		
		        MergeResult result = mergeCommand.call();
		
		        // =========================
		        // 3. Ergebnis prüfen
		        // =========================
		        if (!result.getMergeStatus().isSuccessful()) {
		
		            switch (result.getMergeStatus()) {
		
		                case CONFLICTING:
		                    System.out.println("Merge conflicts detected!");
		                    // Hier kannst du später automatische Konfliktlösung einbauen
		                    break;
		
		                case FAILED:
		                    throw new IllegalStateException("Merge failed: " + result.toString());
		
		                case ALREADY_UP_TO_DATE:
		                    System.out.println("Already up-to-date.");
		                    break;
		
		                default:
		                    System.out.println("Merge status: " + result.getMergeStatus());
		            }
		        }

        		bReturn = true;
	        }catch(IOException ioe) {
	        	ExceptionZZZ ez = new ExceptionZZZ(ioe);
	        	throw ez;
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}       
		}//end main:
		return bReturn;
    }

	public static boolean pullHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {	
				// aber mal explizit als pullCommand
				PullCommand pullCommand = git.pull();
					
				String sUrlPartFromRepo = JgitUtil.computeRepositoryUrlPartFromUrlRepo(sRepoRemote);
				
				//Also zerlegen des pull in fetch und merge.								
				System.out.println("HTTPS-Loesung: Zerlege pull in fetch und merge");
				
				//original url mit Token, wie beim push arbeiten
				String sUrl = "https://firak01:" + sPAT + "@" + sUrlPartFromRepo;
				System.out.println("Url fuer Fetch: '" + sUrl + "'");
				
				//Aber wenn nichts zu fetchen ist, gibt es einen Fehler
				FetchResult fetchResult = JgitUtilHTTPS.fetchIgnoreNothingToFetch(git, sUrl, credentialsProvider);
				if(fetchResult==null) break main;
					
				String sFetchResultMessages = fetchResult.getMessages();
				if(sFetchResultMessages!=null) {				
					System.out.println("Fetch-Result: " + sFetchResultMessages);
				}
					
				//++++++++++++++++++++++++++++++++
				//siehe .git\config Datei, Zeile:
				//fetch = +refs/heads/*:refs/remotes/origin/*
				//Minierklaerung: 
		/*
				Das ist ein sogenannter RefSpec (Reference Specification).
				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
				
				Aufbau allgemein:
				[+]<Quelle>:<Ziel>
				
				Also:
				Quelle (Remote-Seite)
				refs/heads/ = alle Branches im Remote-Repository
				 * = Wildcard → alle Branch-Namen
	
				➡️ Bedeutet:
				Hole alle Branches vom Remote
				
				
				Ziel (lokal)
				refs/remotes/origin/ = Remote-Tracking-Branches
				* = gleicher Name wie Quelle
	
				➡️ Bedeutet:
				Speichere sie lokal als origin/branchname
				
				------------
				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
				Mit + sagst du:
				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
	 */
				
				//String sFetchRefs = "refs/heads/main";
				String sFetchRefs = "refs/heads/master";
				Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs);
					
				//++++++++++++++++++++++++++++++++				
				MergeCommand mergeCommand = git.merge();
				mergeCommand.include(objRef);
					
				MergeResult mergeResult = mergeCommand.call();
				System.out.println("Merge-Status:" + mergeResult.getMergeStatus());//pullResult.getMergeResult());
																				
				bReturn = true;
				//###############################################################		
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}

	//Z.B. HTTPS Version: 	https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	public static String computeRepositoryUrlPartFromUrlHTTPS(String sUrlHTTPS) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			
			
			//String sUrlPartDomainFromHttpsRepo =StringZZZ.right("@" + sUrlHTTPS, "@");
			
			
			/////sUrlPartDomainFromHttpsRepo = StringZZZ.left(sUrlPartDomainFromHttpsRepo + ":", ":");
			
			////String sUrlPartRepoFromHttpsRepo = StringZZZ.right(":" + sUrlHTTPS, ":");
			
			////sReturn = sUrlPartDomainFromHttpsRepo + "/" + sUrlPartRepoFromHttpsRepo;
			
			sReturn = UrlLogicZZZ.getUrlWithoutParameter(sUrlHTTPS);
			
			String sUrlPartDomainFromHttpsRepo = UrlLogicZZZ.getHost(sUrlHTTPS); 
			String sUrlPartRepoFromHttpsRepo = UrlLogicZZZ.getPath(sReturn); 
			sReturn = sUrlPartDomainFromHttpsRepo + sUrlPartRepoFromHttpsRepo;
		}//end main:
		return sReturn;
	}

	//######################
	//Wenn nicht zu fetchen ist, wird eine Exception geworfen. Das ist unschoen.
	//von ChatGPT 20260320, aber für meine einfachen zwecke brauch ich kein FetchResult, also nur die ExceptionHandling uebernommen
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,
	        String sUrlRemote,
	        CredentialsProvider credentialsProvider
	) throws ExceptionZZZ {
		FetchResult objReturn = null;
		main:{
		    try {
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();
	
		        if (sUrlRemote != null && sUrlRemote.trim().length() > 0) {
		            fetchCommand.setRemote(sUrlRemote); // kann Alias ODER URL sein
		        }
	
		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		        
		       
		       
		       
		        
		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*
		        String branch = "master";
		        String remoteRef = "refs/heads/" + branch;
		        String localTrackingRef = "refs/remotes/origin/" + branch;
		        
		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		        //+ für "fast forward"
		        fetchCommand.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));

		        objReturn = fetchCommand.call();
	
		        
		        
		        // Optional: Logging / Prüfung
		        if (objReturn.getTrackingRefUpdates().isEmpty()) {
		            System.out.println("Fetch erfolgreich, aber keine Änderungen vorhanden.");
		        } else {
		            System.out.println("Fetch erfolgreich, Änderungen empfangen.");
		        }
	
		    } catch (TransportException te) {
	
		        String msg = te.getMessage();
	
		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
		            System.out.println("Nothing to fetch - Repository ist aktuell.");
		            return null; // bewusst null zurückgeben als Signal
		        }
	
		        // alle anderen Fehler weiterwerfen!
		        ExceptionZZZ ez = new ExceptionZZZ(te);
		        throw ez;
		    }catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			} 
		}//end main:
		 return objReturn;
	}
	
	public static MergeResult pullSingleBranchWithAutoResolveHTTPS(Git git, CredentialsProvider credentialsProvider, String sPAT, String remoteUrl, String branch) throws ExceptionZZZ {		
		main:{
	        try {
		        if (branch == null || branch.trim().isEmpty()) {
		            branch = "master";
		        }
		
		        Repository repo = git.getRepository();
		
		        String remoteRef = "refs/heads/" + branch;
		        String localRef = "refs/remotes/origin/" + branch;
		
		        int retry = 0;
		        int maxRetry = 2;
		
		        while (retry < maxRetry) {
		            try {
		
		                // =========================
		                // 1. FETCH (nur ein Branch!)
		                // =========================
		                FetchCommand fetch = git.fetch()
		                        .setRemote(remoteUrl)
		                        .setRefSpecs(new RefSpec(remoteRef + ":" + localRef));
		
		                if (credentialsProvider != null) {
		                    fetch.setCredentialsProvider(credentialsProvider);
		                }
		
		                fetch.call();
		
		                // =========================
		                // 2. MERGE (genau 1 Head!)
		                // =========================
		                ObjectId remoteObject = repo.resolve(localRef);
		
		                if (remoteObject == null) {
		                    throw new IllegalStateException("Remote branch not found: " + localRef);
		                }
		
		                MergeCommand merge = git.merge();
		                merge.include(remoteObject);
		                merge.setStrategy(MergeStrategy.RECURSIVE);
		
		                MergeResult result = merge.call();
		
		                System.out.println("Merge-Status: " + result.getMergeStatus());
		
		                // =========================
		                // 3. Ergebnis prüfen
		                // =========================
		                if (result.getMergeStatus().isSuccessful()) {
		                    return result;
		                }
		
		                if (result.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
		                    System.out.println("Merge conflicts detected (content-level).");
		                    return result; // normale Merge-Konflikte (nicht Checkout)
		                }
		
		                if (result.getMergeStatus().equals(MergeResult.MergeStatus.ALREADY_UP_TO_DATE)) {
		                    return result;
		                }
		
		                // andere Fälle
		                throw new IllegalStateException("Merge failed: " + result.getMergeStatus());
		
		            } catch (CheckoutConflictException cce) {
		
		                System.out.println("CheckoutConflict erkannt – versuche automatische Bereinigung...");
		
		                Collection<String> paths = cce.getConflictingPaths();
		
		                if (paths == null || paths.isEmpty()) {
		                    throw cce;
		                }
		
		                // =========================
		                // Konfliktdateien zurücksetzen
		                // =========================
		                for (String path : paths) {
		                    System.out.println("Bereinige Datei: " + path);
		
		                    git.checkout()
		                       .addPath(path)
		                       .setForce(true)   // wichtig!
		                       .call();
		                }
		
		                retry++;
		
		                if (retry >= maxRetry) {
		                    throw new IllegalStateException("Max retries reached after CheckoutConflict", cce);
		                }
		
		                System.out.println("Retry Merge (" + retry + ")...");
		            }
		        }
 
        		throw new IllegalStateException("Unexpected end of method");
        
	        }catch(IOException ioe) {
	        	ExceptionZZZ ez = new ExceptionZZZ(ioe);
	        	throw ez;
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}       
		}//end main:		
    }

}//end class
