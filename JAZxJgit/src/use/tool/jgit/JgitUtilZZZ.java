package use.tool.jgit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.merge.ResolveMerger.MergeFailureReason;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;

public class JgitUtilZZZ implements IConstantZZZ {
	
	public static String addProtocolToUrl(String sProtocol, String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocol)) {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("UrlRepo", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = JgitUtilSSH.addProtocolToUrl(sUrlRepo);
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = JgitUtilHTTPS.addProtocolToUrl(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol für Git Repository. Unbekannter Typ: '" + sProtocol + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryConnectionTypeFromProtocol(String sProtocol) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocol)) {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = "SSH";
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = "HTTPS";
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol für Git Repository. Unbekannter Typ: '" + sProtocol + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryConnectionTypeFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtilZZZ.computeRepositoryProtocolFromUrlRepo(sUrlRepo);			
			sReturn = JgitUtilZZZ.computeRepositoryConnectionTypeFromProtocol(sReturn);
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryProtocolFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryProtocolFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryProtocolFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryHostFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryHostFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryHostFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryProjectFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryProjectFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryProjectFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlPartFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryUrlPartFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryUrlPartFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrl(String sUrlBaseIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlBaseIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBase = sUrlBaseIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlBase)) {
				sReturn = JgitUtilHTTPS.computeRepositoryUrlHTTPS(sUrlBase, sRepositoryProject);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlBase)) {
				sReturn = JgitUtilSSH.computeRepositoryUrlSSH(sUrlBase, sRepositoryProject);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlBase + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	
	/** Ueberpruefe, ob unter dem Alias des remote Repositories auch eine URL gefunden wird.
	 *  Falls, nein, setzte es ggfs. bei bOverwrite = true;
	 * 
	 * z.B.:
	 * 
	 * [remote "origin"]
			url = https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
			fetch = +refs/heads/*:refs/remotes/origin/*
	 * 
	 * @param repo
	 * @param sRepositoryRemoteAlias
	 * @param sRepositoryRemoteUrl
	 * @param bOverwrite
	 * @throws IOException
	 */
	public static void ensureRemoteExists(Repository repo, String sRepositoryRemoteAlias, String sRepositoryRemoteUrl, boolean bOverwrite) throws ExceptionZZZ {
		try {
			StoredConfig config = repo.getConfig();
	
		    String existingUrl = config.getString("remote", sRepositoryRemoteAlias, "url");
	
		    if (existingUrl == null || existingUrl.trim().isEmpty() || bOverwrite) {
	
		        if (bOverwrite && existingUrl != null && !existingUrl.equals(sRepositoryRemoteUrl)) {
		            System.out.println("Remote '" + sRepositoryRemoteAlias + "' wird überschrieben:");
		            System.out.println("  alt: " + existingUrl);
		            System.out.println("  neu: " + sRepositoryRemoteUrl);
		        }
	
		        config.setString("remote", sRepositoryRemoteAlias, "url", sRepositoryRemoteUrl);
	
		        config.setStringList(
		            "remote",
		            sRepositoryRemoteAlias,
		            "fetch",
		            Collections.singletonList("+refs/heads/*:refs/remotes/" + sRepositoryRemoteAlias + "/*")
		        );
	
		        config.save();
		    }	  
		} catch(IOException ioe) {
			ExceptionZZZ ez = new ExceptionZZZ("IOException: " +ioe.getMessage(), iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	
	  /**
     * Findet den Remote-Namen (z.B. "origin") anhand einer URL.
     * Hintergrund: 
     * 
     *  Ich bekomme folgende JGit Fehlermeldung: 
     *  org.eclipse.jgit.api.errors.InvalidConfigurationException: 
     *  No value for key remote.git@github.com:firak01/Projekt_Kernel02_JAZDummy.git.url found in configuration
     *  
     *  Es wird unter "origin" nachgesehen. 
     *  In dem lokalen Repository gibt es in der Datei .git\config den Eintrag: [remote "origin"] url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git 
     *  Der Grund für den Fehler liegt daran, dass ich in pullCommand.setRemote("einString")
     *  für "einString" die Url angeben. Das ist aber bei SSH falsch und funktioniert nur bei HTTPS.
     *  Ich benötige nun eine statische Methode, mit der ich den Einrag für remote bekomme, wenn ich die URL als Suchwert verwende:  
     */
    public static String findRemoteNameByUrl(Git git, String url) {
        if (git == null || url == null) {
            return null;
        }

        Repository repo = git.getRepository();
        Config config = repo.getConfig();

        // Alle Remote-Namen holen (origin, upstream, etc.)
        Set<String> remotes = config.getSubsections("remote");

        for (String remoteName : remotes) {
            String remoteUrl = config.getString("remote", remoteName, "url");

            if (remoteUrl != null && remoteUrl.equals(url)) {
                return remoteName;
            }
        }

        return null; // nichts gefunden
    }
	
	/* https://git-scm.com/book/de/v2/Anhang-B:-Git-in-deine-Anwendungen-einbetten-JGit
	// Create a new repository
Repository newlyCreatedRepo = FileRepositoryBuilder.create(
new File("/tmp/new_repo/.git"));
newlyCreatedRepo.create();

//Open an existing repository
Repository existingRepo = new FileRepositoryBuilder()
.setGitDir(new File("my_repo/.git"))
.build();
	 */
	public static Repository getRepositoryObject(String sRepositoryDirectoryTotal, boolean bCreateMissing) throws ExceptionZZZ{
		Repository objReturn = null;
		main:{
			try {
				if(StringZZZ.isEmpty(sRepositoryDirectoryTotal)){
					ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				File objDirectoryRepo = new File(sRepositoryDirectoryTotal);
				if(!FileEasyZZZ.exists(objDirectoryRepo)) {
					if(!bCreateMissing) {
						ExceptionZZZ ez = new ExceptionZZZ("Projektverzeichnis des Remote Repository existiert nicht.", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						System.out.println("Erstelle fehlendes Repository Verzeichnis: '" + sRepositoryDirectoryTotal + "'");
						FileEasyZZZ.createDirectory(objDirectoryRepo);
					}
				}
				
				//s. https://git-scm.com/book/de/v2/Anhang-B:-Git-in-deine-Anwendungen-einbetten-JGit
				String sRepositoryFileTotal = FileEasyZZZ.joinFilePathName(objDirectoryRepo, ".git");
				File objFileRepo = new File(sRepositoryFileTotal);
				if(!FileEasyZZZ.exists(objFileRepo)) {
					if(!bCreateMissing) {
						ExceptionZZZ ez = new ExceptionZZZ("Projektverzeichnis '" + sRepositoryDirectoryTotal + "' ist kein Git Repository. Es fehlt Datei .git", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						// Create a new repository
						objReturn = FileRepositoryBuilder.create(objFileRepo);					
						objReturn.create();											
					}				
				}else {
					//Open an existing repository
					FileRepositoryBuilder objRepoBuilder = new FileRepositoryBuilder();
					objRepoBuilder.setGitDir(objFileRepo);
					objReturn = objRepoBuilder.build();
				}		
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			}
		}//end main:
		return objReturn;
	}
	
	/** z.B.: https://github.com/firak01   oder  git@github.com:firak01
	 *  liefert nur das Protokoll zurück.
	 * @param sUrlRepo
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocol(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) break main;
			
			String sProtocol = StringZZZ.left(sUrlRepo + "@", "@"); 
			if(sProtocol!=null) {
				if(sProtocol.equalsIgnoreCase("git")) {
					sReturn = sProtocol;
					break main;
				}
			}
			
			sProtocol = UrlLogicZZZ.getProtocol(sUrlRepo);
			if(sProtocol!=null) {
				sReturn = sProtocol;
				break main;
			}
									
		}//end main:
		return sReturn;
	}
	
	/** z.B.: https://github.com/firak01   oder  git@github.com:firak01
	 *  liefert das Protokoll PLUS die Protokol-Separatorzeichen zurück.
	 * @param sUrlRepo
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocolPart(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) break main;
			
			String sProtocol = JgitUtilZZZ.getProtocol(sUrlRepo);
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = sProtocol + "@";
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = sProtocol + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL;
			}											
		}//end main:
		return sReturn;
	}

	
	/** Z.B. von git@github.com:firak01/Projekt_Kernel02_JAZDummy.git 
	 *       von https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlHTTPS
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProjectFromUrl(String sRepositoryRemoteUrl) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sRepositoryRemoteUrl)) break main;
			
			String sUrlWithoutEnding = StringZZZ.stripRight(sRepositoryRemoteUrl, ".git");
			String sProject = StringZZZ.right(sUrlWithoutEnding, UrlLogicZZZ.sURL_SEPARATOR_PATH);
			sReturn = sProject;
		}//end main:
		return sReturn;
	}
	
		
	public static boolean isUrlSSH(String sUrlRepo) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtilZZZ.getProtocol(sUrlRepo);
			if(sProtocol.equals("git")) {
				bReturn = true;
				break main;
			}
		}//end main:
		return bReturn;
	}
	
	public static boolean isUrlGit(String sUrlRepo) throws ExceptionZZZ{
		return JgitUtilZZZ.isUrlSSH(sUrlRepo);
	}
	
	public static boolean isUrlHTTPS(String sUrlRepo) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = UrlLogicZZZ.getProtocol(sUrlRepo);
			if(sProtocol==null) break main;
			
			if(sProtocol.equals("https")) bReturn = true;
		}//end main:
		return bReturn;
	}
		
	//#####################################
	//######## MERGE BETREFFEND
	public static boolean logConflicts(MergeResult mergeResult) throws ExceptionZZZ {
	    boolean bReturn = false;
	    main:{
	        if(mergeResult==null) {
	            ExceptionZZZ ez = new ExceptionZZZ("MergeResult", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
	            throw ez;                
	        }

	        MergeResult.MergeStatus status = mergeResult.getMergeStatus();
	        System.out.println("MergeStatus: " + status);

	        // =========================================
	        // 1. Erfolgsfälle (kein Problem)
	        // =========================================
	        if(status.isSuccessful()) {

	            switch(status) {
	                case FAST_FORWARD:
	                case FAST_FORWARD_SQUASHED:
	                    System.out.println("Fast-forward merge performed.");
	                    break;

	                case ALREADY_UP_TO_DATE:
	                    System.out.println("Already up-to-date.");
	                    break;

	                case MERGED:
	                case MERGED_SQUASHED:
	                case MERGED_NOT_COMMITTED:
	                    System.out.println("Merge successful.");
	                    break;

	                default:
	                    System.out.println("Successful merge with status: " + status);
	            }

	            break main; // kein Fehler
	        }

	        // =========================================
	        // 2. Echte Konflikte (Merge wurde durchgeführt)
	        // =========================================
	        if(status == MergeResult.MergeStatus.CONFLICTING) {

	            System.out.println("Merge conflicts detected!");

	            Map<String, int[][]> conflicts = mergeResult.getConflicts();

	            if (conflicts != null && !conflicts.isEmpty()) {
	                System.out.println("Conflicting files:");

	                for (Map.Entry<String, int[][]> entry : conflicts.entrySet()) {
	                    String filePath = entry.getKey();
	                    System.out.println(" - " + filePath);

	                    // Detailinfos
	                    //z.B.: baseStart=0, baseEnd=19, oursStart=10
	                    //Bedeutet:
	                    /*
	                    Diese Zahlen beschreiben die Positionen im Vergleich der drei Versionen:

						base = gemeinsamer Vorfahr (Common Ancestor)
						ours = dein lokaler Stand
						theirs = Remote-Stand (fehlt hier in deinem Ausschnitt)
						
						Konkret:
						
						baseStart=0, baseEnd=19
						→ Im gemeinsamen Vorfahr liegt der betroffene Bereich in den Zeilen 0 bis 19
						oursStart=10
						→ In deiner lokalen Version beginnt der entsprechende Bereich bei Zeile 10
						
						👉 Das bedeutet:
						Der gleiche logische Codeblock wurde zwischen base → yours (und vermutlich auch base → theirs) unterschiedlich verändert → klassischer Mergekonflikt
	                     */
	                    int[][] chunks = entry.getValue();
	                    if (chunks != null) {
	                        for (int i = 0; i < chunks.length; i++) {
	                            int[] c = chunks[i];
	                            
	                            System.out.println("   conflict chunk " + i + "[");
	                            if(c.length>=1) {
	                            	System.out.print("baseStart=" + c[0]);
	                            };
	                            if(c.length>=2) {
	                            	System.out.print(", baseEnd=" + c[1]);
	                            };
	                            if(c.length>=3) {
	                            	System.out.print(", oursStart=" + c[2]);
	                            };
	                            if(c.length>=4) {
	                            	System.out.print(", oursEnd=" + c[3]);
	                            };
	                            if(c.length>=5) {
	                            	System.out.print(", theirsStart=" + c[4]);
	                            };
	                            if(c.length>=6) {
	                            	System.out.print(", theirsEnd=" + c[5]);
	                            };
	                            System.out.print("]\n");
	                        }//end for
	                    }//end   if (chunks != null) {
	                }//end for

	                bReturn = true;
	                break main;
	            } else {
	                System.out.println("Conflict status but no detailed conflict info available.");
	                bReturn = true;
	                break main;
	            }
	        }

	        // =========================================
	        // 3. FAILED → häufig Dirty Worktree oder andere Ursachen
	        // =========================================
	        if(status == MergeResult.MergeStatus.FAILED) {

	            System.out.println("Merge FAILED.");

	            Map<String, MergeFailureReason> failingPaths = mergeResult.getFailingPaths();

	            if (failingPaths != null && !failingPaths.isEmpty()) {

	                System.out.println("Failing paths:");

	                for (Map.Entry<String, MergeFailureReason> entry : failingPaths.entrySet()) {
	                    String filePath = entry.getKey();
	                    MergeFailureReason reason = entry.getValue();

	                    System.out.println(" - " + filePath + " : " + reason);

	                    // Spezifische Diagnose
	                    if(reason == MergeFailureReason.DIRTY_WORKTREE) {
	                        System.out.println("   -> Local changes would be overwritten (DIRTY_WORKTREE).");
	                    } else if(reason == MergeFailureReason.COULD_NOT_DELETE) {
	                        System.out.println("   -> File could not be deleted.");
	                   // } else if(reason == MergeFailureReason.COULD_NOT_RENAME) {
	                   //     System.out.println("   -> File could not be renamed.");
	                    }
	                }

	                bReturn = true;
	                break main;

	            } else {
	                System.out.println("FAILED but no failing paths information available.");
	                bReturn = true;
	                break main;
	            }
	        }

	        // =========================================
	        // 4. Sonstige Fälle
	        // =========================================
	        System.out.println("Unhandled merge status: " + status);
	        bReturn = true;

	    }//end main:
	    System.out.println(); //Leerzeile zum optischen Trennen der weiteren Ausgaben.
	    return bReturn;    
	}
	
	
	//######################################################
	//####### PUSH BETREFFEND
	public static boolean logPushResults(Iterable<PushResult> pushResults) throws ExceptionZZZ {
	    boolean bReturn = false;

	    main:{
	        if(pushResults == null) {
	            ExceptionZZZ ez = new ExceptionZZZ(
	                    "PushResults",
	                    iERROR_PARAMETER_MISSING,
	                    JgitUtilZZZ.class,
	                    ReflectCodeZZZ.getMethodCurrentName());

	            throw ez;
	        }

	        boolean bAnyProblem = false;

	        // #############################################################
	        for(PushResult pushResult : pushResults) {

	            if(pushResult == null) {
	                continue;
	            }

	            System.out.println("=================================================");
	            System.out.println("PushResult for remote: "
	                    + pushResult.getURI());
	            System.out.println("=================================================");

	            Collection<RemoteRefUpdate> updates =
	                    pushResult.getRemoteUpdates();

	            if(updates == null || updates.isEmpty()) {

	                System.out.println("No remote updates available.");
	                bAnyProblem = true;
	                continue;
	            }

	            // #########################################################
	            for(RemoteRefUpdate update : updates) {

	                String sRemoteName = update.getRemoteName();

	                RemoteRefUpdate.Status status =
	                        update.getStatus();

	                String sMessage = update.getMessage();

	                System.out.println("-----------------------------------------");
	                System.out.println("Remote Ref : " + sRemoteName);
	                System.out.println("Status     : " + status);

	                if(sMessage != null) {
	                    System.out.println("Message    : " + sMessage);
	                }

	                // =========================================
	                // Erfolgsfälle
	                // =========================================
	                if(status == RemoteRefUpdate.Status.OK) {

	                    System.out.println("Push successful.");

	                }else if(status == RemoteRefUpdate.Status.UP_TO_DATE) {

	                    System.out.println("Already up-to-date.");

	                // =========================================
	                // Problemfälle
	                // =========================================
	                }else if(status == RemoteRefUpdate.Status.REJECTED_NONFASTFORWARD) {

	                    System.out.println("Push rejected: NONFASTFORWARD");
	                    System.out.println("-> Remote branch contains newer commits.");
	                    System.out.println("-> Execute pull/merge/rebase first.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.REJECTED_NODELETE) {

	                    System.out.println("Push rejected: NODELETE");
	                    System.out.println("-> Remote branch deletion denied.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.REJECTED_REMOTE_CHANGED) {

	                    System.out.println("Push rejected: REMOTE_CHANGED");
	                    System.out.println("-> Remote changed during push.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.NON_EXISTING) {

	                    System.out.println("Remote ref does not exist.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.AWAITING_REPORT) {

	                    System.out.println("Awaiting remote report.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.NOT_ATTEMPTED) {

	                    System.out.println("Push not attempted.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.REJECTED_OTHER_REASON) {

	                    System.out.println("Push rejected: OTHER_REASON");

	                    if(sMessage != null) {
	                        System.out.println("Server message:");
	                        System.out.println("-> " + sMessage);
	                    }

	                    bAnyProblem = true;

	                }else {

	                    System.out.println("Unhandled push status: " + status);

	                    bAnyProblem = true;
	                }

	                // Zusatzinfos
	                Object objExpectedOldObjectId =
	                        update.getExpectedOldObjectId();

	                Object objNewObjectId =
	                        update.getNewObjectId();

	                if(objExpectedOldObjectId != null) {
	                    System.out.println("ExpectedOldObjectId: "
	                            + objExpectedOldObjectId.toString());
	                }

	                if(objNewObjectId != null) {
	                    System.out.println("NewObjectId         : "
	                            + objNewObjectId.toString());
	                }

	            }//end for updates
	        }//end for pushResults

	        bReturn = bAnyProblem;

	    }//end main:
	    System.out.println(); //Leerzeile zum optischen Trennen der weiteren Ausgaben.
	    return bReturn;
	}
	
	
	//######################################################
	//######### FETCH	
	//Manchmal ist nichts zu fetchen, dann wird ein Fehler geworfen.
	//Das ist unschoen, darum Fehler abfangen
	public static boolean fetchIgnoreNothingToFetch(File objFileDir, String sRepositoryRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			 try {
				Git git4Fetch = Git.open(objFileDir); 
				System.out.println("Git-Repository 4 Fetch repository opened.");
					
			    FetchCommand gitCommandFetch = git4Fetch.fetch();
			    
			    //Laut chat gpt nicht immer die URL notwendig, da die Remote Daten schon im .git/config stehen, wuerde auch ein Alias funktionieren
			    //aber, die RemoteUrl - einmal ermittelt - geht auch.
			    gitCommandFetch.setRemote(sRepositoryRemote); 
			    gitCommandFetch.call();
	        }catch(TransportException tex) {
		        String msg = tex.getMessage();
		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
		            System.out.println("Nothing to fetch - Repository ist aktuell.");	           
		        }else {
		        	// alle anderen Fehler weiterwerfen!
		        	ExceptionZZZ ez = new ExceptionZZZ(tex);
					throw ez;
		        }
	        }catch (IOException ioe) {
					ExceptionZZZ ez = new ExceptionZZZ(ioe);
					throw ez;
			} catch (InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			} catch (GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}			
			bReturn = true;
		}//end main:
		return bReturn;
	}		
}
