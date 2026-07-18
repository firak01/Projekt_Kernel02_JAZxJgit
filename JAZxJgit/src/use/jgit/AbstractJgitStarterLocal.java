package use.jgit;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IObjectWithExpressionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.machine.EnvironmentZZZ;
import use.jgit.IJgitStarterEnabledZZZ.FLAGZLOCAL;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.protocol.ssh.IJgitStarterSSHEnabled;
import use.jgit.protocol.ssh.JgitStarterSSH;
import use.jgit.tool.status.GitAutoStageService;
import use.jgit.util.JgitUtilHTTPS;
import use.jgit.util.JgitUtilSSH;
import use.jgit.util.JgitUtilZZZ;

/** Abstrakte Klasse, die alles enthält um in einem lokalen Repository einen commit zu machen.
 *  Merke: Das Remote Repository kennt sie nicht
 *         Also muss eine Klasse, die etwas mit dem remote Repository machen will von einen anderen, erweiterten abstrakten Klasse erben.
 * @author Fritz Lindhauer
 *
 * @param <T>
 */
public abstract class AbstractJgitStarterLocal<T> extends AbstractJgitRepository<T> implements IJgitStarterLocal, IJgitStarterEnabledZZZ{
	private static final long serialVersionUID = -1998325674945232389L;

    //Für COMMIT:
	//Merke: Das sind ergänzende Kommentare. Der Rechnername, etc. wird immer übergeben.
	public static String sCOMMENT_COMMIT_DEFAULT=" "; //Wenn nix angegeben wurde
	protected volatile String sCommentCommitDefault=null; //Ggfs. in überschreibender Klasse ein besonderer Wert.
	protected volatile String sCommentCommit=null; //Der per ArgumentString übergebene Kommentar sollte hier rein.
	
	//### aus IJgitStarterCommit	
	@Override 
	public String getCommentCommitDefault() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sCommentCommitDefault)) {
			return sCOMMENT_COMMIT_DEFAULT;
		}else {
			return this.sCommentCommitDefault;
		}
	}
	
	@Override 
	public void setCommentCommitDefault(String sCommentCommitDefault) throws ExceptionZZZ {
		this.sCommentCommitDefault = sCommentCommitDefault;
	}
	
	@Override 
	public String getCommentCommit() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sCommentCommit)) {
			return this.getCommentCommitDefault();
		}else {
			String sCommentCommitDefault=this.getCommentCommitDefault();
			if(!StringZZZ.isEmptyTrimmed(sCommentCommitDefault)) {
				return this.sCommentCommit + " " + this.getCommentCommitDefault();
			}else {
				return this.sCommentCommit;
			}
		}
	}
	
	@Override 
	public void setCommentCommit(String sCommentCommit) throws ExceptionZZZ {
		this.sCommentCommit = sCommentCommit;
	}
	
	

	
	
	//##############################################################################
	
	//### aus IJgitStarterCommit
	
	
	//####################################################################
	//###### STATUS ######################################################
	
	//### aus IJgitStarterLocal
	@Override
	public boolean statusit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				//Finde geaenderte und neue Dateien fuer den Commit			
				System.out.println("STATUS: ");		
				this.printStatus(git);
				
				bReturn = true;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean statusit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try{
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen

				//+++++++++++++++++++++++++++++++
				//Konfiguriere JGit
				//braucht man das hier, man ruft doch nur den Status ab???
				//Ja, zum initialisieren des Git-Objects				
				boolean bSuccess = this.configureGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
			
				
				//+++++++++++++++++++++++++++++++
				//Finde geaenderte und neue Dateien fuer den commit
				Git git = this.getGitObject();
				boolean bSuccessStatus = this.statusit(git);
				if(bSuccessStatus) {
					System.out.println("STATUS REQUEST: SUCCESSFUL");				
					bReturn = true;
				}else {
					System.out.println("STATUS REQUEST: FAILED");				
					bReturn = false;
				}
			
			    git.close();
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;		
			}
		}//end main:
		return bReturn;
	}
	
	//####################################################################
	//###### COMMIT ######################################################
	 
	//### aus IJgitStarterCommit
	@Override
	public boolean commitit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ {
		return this.commitit(objConfig, null);
	}
	
	@Override
	public boolean commitit(IConfigStarterLocalJGIT objConfig, String sCommentIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		
			//Konfiguriere JGit
			boolean bSuccess = this.configureGit(objConfig);
			if(bSuccess) {
				System.out.println("Git erfolgreich konfiguriert");
			}else {
				System.out.println("Git NICHT erfolgreich konfiguriert");
				break main;
			}
			
			String sComment = objConfig.readComment(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben 
			this.setCommentCommit(sComment);
						
			//+++++++++++++++++++++++++++++++
			//Finde geaenderte und neue Dateien fuer den commit
			Git git = this.getGitObject();
			bReturn = this.commitit(git, sCommentIn);
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean commitit(Git git) throws ExceptionZZZ {
		return this.commitit(git, null);
	}
	
	@Override
	public boolean commitit(Git git, String sCommentIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if (git == null) {
		            throw new IllegalArgumentException("git must not be null");
		        }
				
				//Finde geaenderte und neue Dateien fuer den Commit			
				System.out.println("\nSTATUS BEFORE COMMIT");		
				this.printStatus(git);
		        //##################################################################
		        
				//Fuege geänderte Dateien, die schon im Repository sind, hinzu.
				//steuere den Weg per Flag
				boolean bIgnoreDeletes = this.getFlagLocal(IJgitStarterEnabledZZZ.FLAGZLOCAL.STAGING_IGNORE_DELETES);
				if(bIgnoreDeletes) {
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//!!ABER Verwendet die Eigenschaft org.eclipse.jgit.api.RebaseResult.Status.UNCOMMITTED_CHANGES
					this.addFileTrackedChanged(git);
					
					//Fuege neue Dateien hinzu, die noch nicht im Repository sind.
			        this.addFileUntracked(git);
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				}else {
			        //Alternativer Ansatz, der auch Deletes berücksichtigt und auch sofort die untracked Dateien.
			        this.addFileStageAll(git);
				}
		        
		        
		        //Mache einen commit (mit aktuellem Datum/Uhrzeit) & Namen der Maschine
		        String sCommentByProperty = this.getCommentCommit();
		        String sComment = StringZZZ.coalesce(sCommentIn, sCommentByProperty);
		        sComment = JgitUtilZZZ.createCommentCommit(sComment);
		        		        
				CommitCommand gitCommandCommit = git.commit();
				
				System.out.println("COMMIT MESSAGE: '" + sComment + "'");
				gitCommandCommit.setMessage(sComment);
				gitCommandCommit.call();
		        
		        System.out.println("STATUS AFTER COMMIT");
		        this.printStatus(git);
		        
		        bReturn = true;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	
	@Override
	public void addFileTrackedChanged() throws ExceptionZZZ {		
		Git git = this.getGitObject();
		this.addFileTrackedChanged(git);       
	}
	
	/* Verwendet UncommittedChanges(), ABER:
	 * 
	 *status.getUncommittedChanges() ist in JGit eher eine Bequemlichkeitsmenge, aber keine semantisch saubere Kategorie für Git-Operationen.

🔎 Was getUncommittedChanges() wirklich ist

Das ist im Kern eine Vereinigung mehrerer Statusgruppen, z. B.:

modified
added
removed
changed (je nach JGit-Version etwas unterschiedlich interpretiert)

Also eher ein „alles was irgendwie unstaged/staged geändert ist“-Sammelbehälter.

🧠 Wann das Aufteilen sinnvoll ist
Dein aktueller Use Case ist entscheidend.
👍 Sinnvoll, wenn du unterschiedliche Aktionen brauchst
Du hast z. B. jetzt:

neue Dateien → addFileUntracked() → git add <file>
geänderte Dateien → addFileTrackedChanged() → git add <file>
(optional) gelöschte Dateien → git add -u / setUpdate(true)

Dann brauchst du explizit getrennte Statusmengen, weil:

git add ≠ git add -u
Deletes brauchen andere Behandlung als Modifications
Renames sind Kombination aus beidem

➡️ Dann ist Aufteilung absolut sinnvoll.
	 * 
	 * (non-Javadoc)
	 * @see use.jgit.IJgitStarterLocal#addFileTrackedChanged(org.eclipse.jgit.api.Git)
	 */
	@Override
	public void addFileTrackedChanged(Git git) throws ExceptionZZZ {		
		try {
			StatusCommand gitCommandStatus = git.status();
			Status status = gitCommandStatus.call();
	
			//DEBUG
	        System.out.println("Removed  : " + status.getRemoved());
	        System.out.println("Missing  : " + status.getMissing());
	        System.out.println("Changed  : " + status.getChanged());
	        System.out.println("Modified : " + status.getModified());
	        System.out.println("Untracked: " + status.getUntracked());
	        System.out.println("Uncommitted: " + status.getUncommittedChanges());
	        
			
			
			Set<String> uncommittedChanges = status.getUncommittedChanges();
			Set<String> untracked          = status.getUntracked();
			ArrayList<String> listasUncommitedChanges = new ArrayList<String>();
			
				
	        for (String uncommitted : uncommittedChanges) {
	        	if(!untracked.contains(uncommitted)) {
	        		listasUncommitedChanges.add(uncommitted);
	        	}
	        }
	        
	        // run the add-call 
	        for(String uncommitted : listasUncommitedChanges) {
	        	System.out.println("uncommitted to add: '" + uncommitted + "'");
	        	try {
	        		//JGit-Commands sind One-Shot-Objekte. Nach... ist es verbraucht. 
	    			//Darum in der Schleife mehrmals erstellen. Das ist wichtig bei mehreren Dateien.
	        		AddCommand gitCommandAdd = git.add();
	        		gitCommandAdd.addFilepattern(uncommitted);
	        		gitCommandAdd.call();
	        	}catch(java.lang.IllegalStateException isex) {
	        		System.out.println(isex.getMessage());
	        		
	        		ExceptionZZZ ez = new ExceptionZZZ(isex);
	        		throw ez;
	        	}
	        }
		}catch (NoWorkTreeException nwte) {
			System.out.println(nwte.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(nwte);
    		throw ez;
		}catch( GitAPIException gae) {
			System.out.println(gae.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(gae);
    		throw ez;
		}
       
	}
	
	@Override
	public void addFileUntracked() throws ExceptionZZZ {	
		Git git = this.getGitObject();
		this.addFileUntracked(git);
	}
	
	@Override
	public void addFileUntracked(Git git) throws ExceptionZZZ {
	
		try {
			Status status = git.status().call();
	
			Set<String> setUntracked = status.getUntracked();
			ArrayList<String> listasUntracked = new ArrayList<String>();
	        for (String  sUntracked : setUntracked ) {
	        	listasUntracked.add(sUntracked);
	        }
	        
	        for(String sUntracked : listasUntracked) {
	        	git.add().addFilepattern(sUntracked).call();
	        }
		}catch (NoWorkTreeException nwte) {
			System.out.println(nwte.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(nwte);
    		throw ez;
		}catch( GitAPIException gae) {
			System.out.println(gae.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(gae);
    		throw ez;
		}
	
	}
	
	@Override
	public void addFileStageAll(Git git) throws ExceptionZZZ{
		
		//Alternative Variante, die feiner die Git-Statuswerte analysiert
		//und so Löschungen mitbekommt
		GitAutoStageService objStageService = new GitAutoStageService();
		objStageService.stage(git);
	}

	
	
	//#######################################
//	@Override
//	public boolean configureGit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ{
//		boolean bReturn = false;
//		main:{
//			try {			
//				//### Soll das lokale Repository konfiguriert haben.			
//				//    Die benoetigten Parameter aus dem Argumenten des Aufrufs holen. Wiederverwendbare Methode nutzen.					
//				boolean bLocalRepositoryConfigured = this.configureRepositoryLocal(objConfig);
//				if(bLocalRepositoryConfigured) {
//					System.out.println("Lokales Repository erfolgreich konfiguriert");
//				}else {
//					System.out.println("Lokales Repository NICHT einzeln erfolgreich konfiguriert");
//					//Wenn das so nicht geklappt hat, dann wurden die Details ggfs. einzeln übergeben... wir werden sehen.
//				}
//					
//				//++++++++++ Erst das lokale Git-Repository initialisieren
//				//           Dann kann dort ggfs. auch etwas fehlendes nachgelesen werden.				
//				String sDirectoryRepositoryLocalTotal = this.getRepositoryLocalTotal();				
//				if(StringZZZ.isEmpty(sDirectoryRepositoryLocalTotal)) {
//					String sProject = this.getRepositoryProject();					
//					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Verzeichnis für das Projekt '" + sProject + "'nicht definiert.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;				
//				}else {
//					System.out.println("Lokales Repository als Gesamtstring vorhanden: '" + sDirectoryRepositoryLocalTotal + "'");
//				}
//				
//				//Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
//				//hier könnte man noch den RefNamen auf Gültigkeit prüfen.	
//				
//				File objFileDirTotal = new File(sDirectoryRepositoryLocalTotal);
//				if(!objFileDirTotal.exists()) {
//					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Projekt Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocalTotal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;				
//				}
//				
//				InitCommand gitCommandInit = Git.init();
//				gitCommandInit.setDirectory(objFileDirTotal);
//				
//				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
//				this.setGitObject(git);
//			
//				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
//				
//				///##############################################
//				//Weil das was mit dem Wunsch-Protocol zu tun hat, hier nicht machen
//				//... JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);
//
//				bReturn = true;
//				//######################################
//			}catch(GitAPIException gae) {
//				ExceptionZZZ ez = new ExceptionZZZ(gae);
//				throw ez;
//			}
//		}//end main:
//		return bReturn;
//	}
	
	
	//##################################################
	public void printStatus(Git git) throws NoWorkTreeException, GitAPIException {
		main:{
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			StatusCommand statusCommand = git.status();
			if(statusCommand==null) {
				System.out.println("Kein StatusCommand erzeugt.");
				break main;
			}
			
			Status status = statusCommand.call();
			if(status==null) {
				System.out.println("Kein Status vorhanden.");
				break main;
			}
			
	        Set<String> added = status.getAdded();
	        for (String add : added) {
	            System.out.println("Added: " + add);
	        }
	        Set<String> uncommittedChanges = status.getUncommittedChanges();
	        for (String uncommitted : uncommittedChanges) {
	            System.out.println("Uncommitted: " + uncommitted);
	        }
	
	        Set<String> untracked = status.getUntracked();
	        for (String untrack : untracked) {
	            System.out.println("Untracked: " + untrack);
	        }
		}//end main:
	}
	
	
	//############# STATIC METHODEN

	//###############################################
	//### FLAG HANDLING
	//###############################################
	
	//### aus IJgitEnabledZZZ
	@Override
	public boolean getFlag(IJgitStarterEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.getFlag(objEnumFlag.name());
	}

	@Override
	public boolean setFlag(IJgitStarterEnabledZZZ.FLAGZ objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlag(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlag(IJgitStarterEnabledZZZ.FLAGZ[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitStarterEnabledZZZ.FLAGZ objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagExists(IJgitStarterEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagSetBefore(IJgitStarterEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}
	
	//###################################
	//### FLAGLOCAL Handling

	//### aus JgitEnabledZZZ	
	@Override
	public boolean getFlagLocal(IJgitStarterEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.getFlagLocal(objEnumFlag.name());
	}

	@Override
	public boolean setFlagLocal(IJgitStarterEnabledZZZ.FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagLocal(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagLocal(IJgitStarterEnabledZZZ.FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitStarterEnabledZZZ.FLAGZLOCAL objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlagLocal(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagLocalExists(IJgitStarterEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagLocalExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagLocalSetBefore(IJgitStarterEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}

	


	//###################################
	//### FLAG CUSTOM Handling
		
	@Override
	public boolean getFlagCustom(IJgitStarterEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.getFlagCustom(objEnumFlag.name());
	}

	@Override
	public boolean setFlagCustom(IJgitStarterEnabledZZZ.FLAGZCUSTOM objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagCustom(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagCustom(IJgitStarterEnabledZZZ.FLAGZCUSTOM[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitStarterEnabledZZZ.FLAGZCUSTOM objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagCustomExists(IJgitStarterEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagCustomSetBefore(IJgitStarterEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomSetBefore(objEnumFlag.name());
	}
		
}
