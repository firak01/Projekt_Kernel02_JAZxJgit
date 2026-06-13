package use.jgit;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;

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
import use.jgit.IJgitEnabledZZZ.FLAGZLOCAL;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigStarterCommitJGIT;
import use.jgit.config.IConfigStarterJGIT;
import use.jgit.protocol.ssh.IJgitStarterSSHEnabled;
import use.jgit.protocol.ssh.JgitStarterSSH;
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
public abstract class AbstractJgitStarterCommit<T> extends AbstractObjectWithFlagZZZ<T> implements IJgitStarterCommit, IJgitEnabledZZZ{
	private static final long serialVersionUID = -1998325674945232389L;
	
	protected volatile Git gitObject = null;
	
	protected volatile String sRepositoryProject=null;//Der Name des Projekt, wie er hinter die Basis Verzeichnis/Url kommt.
	protected volatile String sRepositoryBranch=null; //Der Name des Branch, wenn man es nicht auf alle Branches beziehen will.
	
	protected volatile String sRepositoryLocalBase=null;  //Basis Verzeichnis
	protected volatile String sRepositoryLocalTotal=null;  //Geamt Verzeichnis
	
	protected volatile String sRepositoryTotalRemote=null; //Gesamt URL
	
	protected volatile String sRepositoryRemoteAlias=null;//die Section in der ini z.B. [origin]
	

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
	
	//++++++++++++++++++++
	@Override
	public String getRepositoryProject() throws ExceptionZZZ {
		return this.sRepositoryProject;
	}
	
	@Override 
	public void setRepositoryProject(String sRepositoryProject) throws ExceptionZZZ {
		this.sRepositoryProject = sRepositoryProject;
	}
	
	@Override
	public String getRepositoryBranch() throws ExceptionZZZ {
		return this.sRepositoryBranch;
	}
	
	@Override 
	public void setRepositoryBranch(String sRepositoryBranch) throws ExceptionZZZ {
		this.sRepositoryBranch = sRepositoryBranch;
	}
	
	@Override
	public String getRepositoryLocalBase() throws ExceptionZZZ {
		return this.sRepositoryLocalBase;
	}
	
	@Override
	public void setRepositoryLocalBase(String sRepositoryLocalBase) throws ExceptionZZZ {
		this.sRepositoryLocalBase = sRepositoryLocalBase;
	}
	
	@Override
	public String getRepositoryLocalTotal() throws ExceptionZZZ {
		return this.sRepositoryLocalTotal;
	}
	
	@Override
	public void setRepositoryTotalLocal(String sRepositoryLocalTotal) throws ExceptionZZZ {
		this.sRepositoryLocalTotal = sRepositoryLocalTotal;
	}

	/* (non-Javadoc)
	 * @see use.jgit.IJgitStarter#configureRepositoryLocal(use.jgit.config.IConfigStarterJGIT)
	 * 
	 * 
	 * Fehlerhaft wäre
				
					[remote "origin"]
						url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
						fetch = +refs/heads/*:refs/remotes/origin/*
					[branch "master"]
						remote = origin
						merge = refs/heads/master
	    Weil es remotes/origin nicht gibt.... vermutlich wurde hier fehlerhaft der Section-Alias als Branch verwendet.
	    
	    
	   inierklaerung:
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
	
		
	/* (non-Javadoc)
	 * @see use.jgit.IJgitStarter#configureRepositoryLocal(use.jgit.config.IConfigStarterJGIT)
	 * 
	 * 
	 * Fehlerhaft wäre
				
					[remote "origin"]
						url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
						fetch = +refs/heads/*:refs/remotes/origin/*
					[branch "master"]
						remote = origin
						merge = refs/heads/master
	    Weil es remotes/origin nicht gibt.... vermutlich wurde hier fehlerhaft der Section-Alias als Branch verwendet.
	    
	    
	   Minierklaerung:
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
	//public boolean configureRepositoryLocal(IConfigStarterJGIT objConfig) throws ExceptionZZZ{
	@Override
	public boolean configureRepositoryLocal(IConfigJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(objConfig==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteAlias = objConfig.readRepositoryRemoteAlias();
			boolean bRemoteAliasAvailable = !StringZZZ.isEmpty(sRepositoryRemoteAlias);
//			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
//				ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
			this.setRepositoryRemoteAlias(sRepositoryRemoteAlias);
			
			
			String sRepositoryLocal = objConfig.readRepositoryLocal();
			if(StringZZZ.isEmpty(sRepositoryLocal)){
				ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryLocalBase(sRepositoryLocal);
			
			
			String sRepositoryProject = objConfig.readRepositoryProjectName();
			if(StringZZZ.isEmpty(sRepositoryProject) & !bRemoteAliasAvailable){
				ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryProject(sRepositoryProject);
			
			//Merke: Branch darf leer sein
			String sRepositoryBranch = objConfig.readRepositoryBranch();
			this.setRepositoryBranch(sRepositoryBranch);
			
			String sDirectoryRepositoryTotalLocal = FileEasyZZZ.joinFilePathName(sRepositoryLocal, sRepositoryProject);
			File objDirectoryRepositoryLocalTotal = new File(sDirectoryRepositoryTotalLocal);
			if(!objDirectoryRepositoryLocalTotal.exists()){
				ExceptionZZZ ez = new ExceptionZZZ("Verzeichnis des Repositories existiert nicht '" + sDirectoryRepositoryTotalLocal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryTotalLocal(sDirectoryRepositoryTotalLocal);
			
			String sRepositoryRemoteUrlByAlias = null; String sRepositoryRemoteFetchByAlias = null;
			if(!bRemoteAliasAvailable) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Alias nicht vorhanden '" + sRepositoryRemoteAlias + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else {
				//+++ Prüfe, ob https oder ssh in der .git\config Datei steht	
				Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryTotalLocal, true);
				sRepositoryRemoteUrlByAlias = repo.getConfig()
						       .getString("remote",sRepositoryRemoteAlias,"url");
				if(StringZZZ.isEmpty(sRepositoryRemoteUrlByAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Keine Remote Repository URL bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				sRepositoryRemoteFetchByAlias = repo.getConfig()
					       .getString("remote",sRepositoryRemoteAlias,"fetch");
				if(StringZZZ.isEmpty(sRepositoryRemoteFetchByAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Kein Remote Repository FETCH bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				System.out.println("Git-Repository verwendet folgende Remote URL (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteUrlByAlias +"'");
				System.out.println("Git-Repository verwendet folgende Remote FETCH (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteFetchByAlias +"'");
				
				this.setRepositoryTotalRemote(sRepositoryRemoteUrlByAlias);
			}
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	
	//##############################################################################
	
	//### aus IJgitStarterCommit
	@Override 
	public Git getGitObject() throws ExceptionZZZ{
		return this.gitObject;
	}
	
	@Override
	public void setGitObject(Git objGit) throws ExceptionZZZ{
		this.gitObject = objGit;
	}
	
	@Override
	public String getRepositoryTotalRemote() throws ExceptionZZZ {
		return this.sRepositoryTotalRemote;
	}

	@Override
	public void setRepositoryTotalRemote(String sRepositoryTotalRemote) throws ExceptionZZZ {
		this.sRepositoryTotalRemote = sRepositoryTotalRemote;
	}
	
	@Override
	public String getRepositoryRemoteAlias() throws ExceptionZZZ {
		return this.sRepositoryRemoteAlias;
	}

	@Override
	public void setRepositoryRemoteAlias(String sRepositoryRemoteAlias) throws ExceptionZZZ {
		this.sRepositoryRemoteAlias = sRepositoryRemoteAlias;
	}
	
	
	//####################################################################
	//###### STATUS ######################################################
	
	//### aus IJgitStarterCommit
	@Override
	public boolean statusit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
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
	public boolean statusit(IConfigStarterCommitJGIT objConfig) throws ExceptionZZZ {
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
	public boolean commitit(IConfigStarterCommitJGIT objConfig) throws ExceptionZZZ {
		return this.commitit(objConfig, null);
	}
	
	@Override
	public boolean commitit(IConfigStarterCommitJGIT objConfig, String sCommentIn) throws ExceptionZZZ {
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
				//Finde geaenderte und neue Dateien fuer den Commit			
				System.out.println("STATUS BEFORE COMMIT");		
				this.printStatus(git);
		        //##################################################################
		        
				//Fuege geänderte Dateien, die schon im Repository sind, hinzu.
				this.addFileTrackedChanged(git);
				
				//Fuege neue Dateien hinzu, die noch nicht im Repository sind.
		        this.addFileUntracked(git);
				
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
	
	@Override
	public void addFileTrackedChanged(Git git) throws ExceptionZZZ {		
		try {
			StatusCommand gitCommandStatus = git.status();
			Status status = gitCommandStatus.call();
	
			Set<String> uncommittedChanges = status.getUncommittedChanges();
			Set<String> untracked          = status.getUntracked();
			ArrayList<String> listasUncommitedChanges = new ArrayList<String>();
			
			AddCommand gitCommandAdd = git.add();		
	        for (String uncommitted : uncommittedChanges) {
	        	if(!untracked.contains(uncommitted)) {
	        		listasUncommitedChanges.add(uncommitted);
	        	}
	        }
	        
	        // run the add-call 
	        for(String uncommitted : listasUncommitedChanges) {
	        	System.out.println("uncommitted to add: '" + uncommitted + "'");
	        	try {
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

	//#######################################
	@Override
	public boolean configureGit() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try {
				//Verwende ggfs. dies wieder...				
				//ABER: HIER GIBT ES KEIN OBJEKT IConfigGIT
				
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen							
				/*
				boolean bLocalRepositoryConfigured = this.configureRepositoryLocal((IConfigJGIT)objConfig);
				if(bLocalRepositoryConfigured) {
					System.out.println("Lokales Repository erfolgreich konfiguriert");
				}else {
					System.out.println("Lokales Repository NICHT erfolgreich konfiguriert");
					//Wenn das so nicht geklappt hat, dann wurden die Details ggfs. einzeln übergeben... wir werden sehen.
				}
				*/				
				
				//A) Lokal
				//a) Lokales Basis Verzeichnis
				String sDirectoryRepositoryLocal = this.getRepositoryLocalBase();
				if(StringZZZ.isEmpty(sDirectoryRepositoryLocal)) {
					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Basis Verzeichnis, Angabe fehlt: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				File objFileDir = new File(sDirectoryRepositoryLocal);
				if(!objFileDir.exists()) {
					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Basis Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
				
				//b) Lokales Repository-Verzeichnis des Projekts
				String sRepositoryProjectLocal = this.getRepositoryProject();
				if(StringZZZ.isEmpty(sDirectoryRepositoryLocal)) {
					ExceptionZZZ ez = new ExceptionZZZ("Projektname des lokalen Repositories, Angabe fehlt: '" + sRepositoryProjectLocal + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sDirectoryRepositoryLocalTotal = FileEasyZZZ.joinFilePathName(sDirectoryRepositoryLocal, sRepositoryProjectLocal);
				File objFileDirTotal = new File(sDirectoryRepositoryLocalTotal);
				if(!objFileDirTotal.exists()) {
					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Projekt Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocalTotal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
				this.setRepositoryTotalLocal(sDirectoryRepositoryLocalTotal);
				
//				Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
				//hier könnte man noch den RefNamen auf Gültigkeit prüfen.	
				
				
				//++++++++++ Erst das lokale Git-Repository initialisieren
				//           Dann kann dort ggfs. auch etwas fehlendes nachgelesen werden.				
				InitCommand gitCommandInit = Git.init();
				gitCommandInit.setDirectory(objFileDirTotal);
				
				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
				this.setGitObject(git);
				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
				//##############################################
				//Weil das was mit dem Protocol zu tun hat, hier nicht machen
//												
//				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
//				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH)
//				//Problem: Wenn hier dier GesamtRepositoryURL nur ausgelesen wird, dann passt das Protokol ggfs. nicht (https URL geht nicht beim ssh Weg.
//				//         Darum hier die remote Repository URL neu ausrechnen... String sRepositoryRemoteUrl = this.getRepositoryTotalRemote();	
//				String sRepositoryRemoteUrl = this.computeRepositoryRemoteUrl();
//				if(!StringZZZ.isEmpty(sRepositoryRemoteUrl)) {
//					String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
//					JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);
//				}
				bReturn = true;
				//######################################
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	//#######################################
		@Override
		public boolean configureGit(IConfigStarterCommitJGIT objConfig) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				try {			
					//### Soll das lokale Repository konfiguriert haben.			
					//    Die benoetigten Parameter aus dem Argumenten des Aufrufs holen. Wiederverwendbare Methode nutzen.					
					boolean bLocalRepositoryConfigured = this.configureRepositoryLocal((IConfigJGIT)objConfig);
					if(bLocalRepositoryConfigured) {
						System.out.println("Lokales Repository erfolgreich konfiguriert");
					}else {
						System.out.println("Lokales Repository NICHT einzeln erfolgreich konfiguriert");
						//Wenn das so nicht geklappt hat, dann wurden die Details ggfs. einzeln übergeben... wir werden sehen.
					}
								
					//++++++++++ Erst das lokale Git-Repository initialisieren
					//           Dann kann dort ggfs. auch etwas fehlendes nachgelesen werden.				
					String sDirectoryRepositoryLocalTotal = this.getRepositoryLocalTotal();				
					if(StringZZZ.isEmpty(sDirectoryRepositoryLocalTotal)) {
						String sProject = this.getRepositoryProject();					
						ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Verzeichnis für das Projekt '" + sProject + "'nicht definiert.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;				
					}else {
						System.out.println("Lokales Repository als Gesamtstring vorhanden: '" + sDirectoryRepositoryLocalTotal + "'");
					}
					
					//Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
					//hier könnte man noch den RefNamen auf Gültigkeit prüfen.	
					
					File objFileDirTotal = new File(sDirectoryRepositoryLocalTotal);
					if(!objFileDirTotal.exists()) {
						ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Projekt Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocalTotal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;				
					}
					
					InitCommand gitCommandInit = Git.init();
					gitCommandInit.setDirectory(objFileDirTotal);
					
					Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
					this.setGitObject(git);
					System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
					//##############################################
					//Weil das was mit dem Protocol zu tun hat, hier nicht machen
//													
//					//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
//					//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH)
//					//Problem: Wenn hier dier GesamtRepositoryURL nur ausgelesen wird, dann passt das Protokol ggfs. nicht (https URL geht nicht beim ssh Weg.
//					//         Darum hier die remote Repository URL neu ausrechnen... String sRepositoryRemoteUrl = this.getRepositoryTotalRemote();	
//					String sRepositoryRemoteUrl = this.computeRepositoryRemoteUrl();
//					if(!StringZZZ.isEmpty(sRepositoryRemoteUrl)) {
//						String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
//						JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);
//					}
					bReturn = true;
					//######################################
				}catch(GitAPIException gae) {
					ExceptionZZZ ez = new ExceptionZZZ(gae);
					throw ez;
				}
			}//end main:
			return bReturn;
		}
	
	//##################################################
	public void printStatus(Git git) throws NoWorkTreeException, GitAPIException {
		
		Status status = git.status().call();

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
	}
	
	
	//############# STATIC METHODEN

	//###############################################
	//### FLAG HANDLING
	//###############################################
	
	//### aus IJgitEnabledZZZ
	@Override
	public boolean getFlag(IJgitEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.getFlag(objEnumFlag.name());
	}

	@Override
	public boolean setFlag(IJgitEnabledZZZ.FLAGZ objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlag(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlag(IJgitEnabledZZZ.FLAGZ[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitEnabledZZZ.FLAGZ objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagExists(IJgitEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagSetBefore(IJgitEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}
	
	//###################################
	//### FLAGLOCAL Handling

	//### aus JgitEnabledZZZ	
	@Override
	public boolean getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.getFlagLocal(objEnumFlag.name());
	}

	@Override
	public boolean setFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagLocal(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitEnabledZZZ.FLAGZLOCAL objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlagLocal(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagLocalExists(IJgitEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagLocalExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagSetBefore(IJgitEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}

	


	//###################################
	//### FLAG CUSTOM Handling
		
	@Override
	public boolean getFlagCustom(IJgitEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.getFlagCustom(objEnumFlag.name());
	}

	@Override
	public boolean setFlagCustom(IJgitEnabledZZZ.FLAGZCUSTOM objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagCustom(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagCustom(IJgitEnabledZZZ.FLAGZCUSTOM[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitEnabledZZZ.FLAGZCUSTOM objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagCustomExists(IJgitEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagCustomSetBefore(IJgitEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomSetBefore(objEnumFlag.name());
	}
		
}
