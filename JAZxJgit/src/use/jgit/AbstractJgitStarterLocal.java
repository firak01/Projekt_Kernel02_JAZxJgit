package use.jgit;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.config.IConfigUserZZZ;
import basic.zBasic.config.IConfigZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.start.protocol.ssh.JgitStarterSSH;
import use.jgit.tool.status.GitAutoStageService;
import use.jgit.util.JgitUtilZZZ;
import use.jgit.util.JgitUtilXmlZZZ;

/** Abstrakte Klasse, die alles enthält um in einem lokalen Repository einen commit zu machen.
 *  Merke: Das Remote Repository kennt sie nicht
 *         Also muss eine Klasse, die etwas mit dem remote Repository machen will von einen anderen, erweiterten abstrakten Klasse erben.
 * @author Fritz Lindhauer
 *
 * @param <T>
 */
public abstract class AbstractJgitStarterLocal<T> extends AbstractObjectWithFlagZZZ<T> implements IJgitStarterLocal, IJgitStarterEnabledZZZ, IConfigUserZZZ{
	private static final long serialVersionUID = -1998325674945232389L;

	protected volatile Git gitObject = null;
	protected volatile IConfigZZZ objConfig = null;

	protected volatile String sProjectName=null;
	protected volatile String sProjectStartingName = null; //der Name für das Projekt, das den Code ausführt (also nicht ein Projekt unterhalb des Repo-Projekts)M
	
	protected volatile String sRepositoryProject=null;//Der Name des Projekt, wie er hinter die Basis Verzeichnis/Url kommt.
	protected volatile String sRepositoryBranch=null; //Der Name des Branch, wenn man es nicht auf alle Branches beziehen will.
	
	protected volatile String sRepositoryLocalBase=null;  //Basis Verzeichnis
	protected volatile String sRepositoryLocalTotal=null; //Geamt Verzeichnis
	
	//Remoteinformatione, die in die lokale GIT-Konfiguration geschrieben werden 
	protected volatile String sRepositoryTotalRemote=null; //Gesamt URL	
	protected volatile String sRepositoryRemoteAlias=null;//die Section in der ini z.B. [origin]
	
	//Für den STATUS:
	protected volatile String sStatusXml=null;
	
    //Für COMMIT:
	//Merke: Das sind ergänzende Kommentare. Der Rechnername, etc. wird immer übergeben.
	public static String sCOMMENT_COMMIT_DEFAULT=" "; //Wenn nix angegeben wurde
	protected volatile String sCommentCommitDefault=null; //Ggfs. in überschreibender Klasse ein besonderer Wert.
	protected volatile String sCommentCommit=null; //Der per ArgumentString übergebene Kommentar sollte hier rein.
	
	
	//############ GETTER  / SETTER	
	//### aus IJgitStarterLocal		
	
	@Override 
	public Git getGitObject() throws ExceptionZZZ{
		return this.gitObject; //Hier nicht, wg. Endlosschleifengefahr this.createGitObject();
	}
	
	@Override
	public void setGitObject(Git objGit) throws ExceptionZZZ{
		this.gitObject = objGit;
	}
	
	//Name des Projekts, das den Starter nutzt, nicht etwa das Repo-Projekt, etc.
	@Override
	public String getProjectStartingName() throws ExceptionZZZ {
		return this.sProjectStartingName;
	}
	
	@Override
	public void setProjectStartingName(String sProjectStartingName) throws ExceptionZZZ{
		this.sProjectStartingName = sProjectStartingName;
	}
	
	//Projekt aus der Konfiguration und damit ggfs. unterhalb des Repo-Projekts liegend
	@Override
	public String getProjectName() throws ExceptionZZZ {
		if(this.sProjectName==null) {
			IConfigZZZ objConfig = this.getConfiguration();			
			this.sProjectName = objConfig.getProjectName();
		}
		return this.sProjectName;		
	}
	
	@Override
	public void setProjectName(String sProjectName) throws ExceptionZZZ {
		this.sProjectName = sProjectName;
	}

	@Override
	public String getRepositoryTotalRemote() throws ExceptionZZZ {
		//wird bei der lokalen Konfiguration ausgerechnet und nicht per Konfiguration übergeben
		return this.sRepositoryTotalRemote;
	}

	@Override
	public void setRepositoryTotalRemote(String sRepositoryTotalRemote) throws ExceptionZZZ {
		this.sRepositoryTotalRemote = sRepositoryTotalRemote;
	}
	
	@Override
	public String getRepositoryRemoteAlias() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryRemoteAlias)) {
			IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
			this.sRepositoryRemoteAlias = objConfig.readRepositoryRemoteAlias();
		}
		return this.sRepositoryRemoteAlias;		
	}

	@Override
	public void setRepositoryRemoteAlias(String sRepositoryRemoteAlias) throws ExceptionZZZ {
		this.sRepositoryRemoteAlias = sRepositoryRemoteAlias;
	}
	
	
	//+++++++++++++++++++++++
	@Override
	public String getRepositoryProjectName() throws ExceptionZZZ {		
		if(StringZZZ.isEmpty(this.sRepositoryProject)) {
			IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
			this.sRepositoryProject = objConfig.readRepositoryProjectName();
		}
		return this.sRepositoryProject;
		
	}
	
	@Override 
	public void setRepositoryProjectName(String sRepositoryProject) throws ExceptionZZZ {
		this.sRepositoryProject = sRepositoryProject;
	}
	
	@Override
	public String getRepositoryBranch() throws ExceptionZZZ {		
		if(StringZZZ.isEmpty(this.sRepositoryBranch)) {
			IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
			this.sRepositoryBranch = objConfig.readRepositoryBranch();
		}
		return this.sRepositoryBranch;		
	}
	
	@Override 
	public void setRepositoryBranch(String sRepositoryBranch) throws ExceptionZZZ {
		this.sRepositoryBranch = sRepositoryBranch;
	}
	
	@Override
	public String getRepositoryLocalBase() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryLocalBase)) {
			IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
			this.sRepositoryLocalBase =  objConfig.readRepositoryLocalBaseDirectory();
		}
		return this.sRepositoryLocalBase;
	}
	
	@Override
	public void setRepositoryLocalBase(String sRepositoryLocalBase) throws ExceptionZZZ {
		this.sRepositoryLocalBase = sRepositoryLocalBase;
	}
	
	@Override
	public String getRepositoryLocalTotal() throws ExceptionZZZ {
		//wird bei der lokalen Konfiguration ausgerechnet und nicht per Konfiguration übergeben
		return this.sRepositoryLocalTotal;	  	
	}
	
	@Override
	public void setRepositoryTotalLocal(String sRepositoryLocalTotal) throws ExceptionZZZ {
		this.sRepositoryLocalTotal = sRepositoryLocalTotal;
	}
	
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
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String getStatusStringXml() throws ExceptionZZZ{
		if(StringZZZ.isEmpty(this.sStatusXml)){
			this.sStatusXml = this.createStatusXml();			
		}
		return this.sStatusXml;
	}
	
	
	//#######################################################################
	//### aus IConfigUserZZZ
	@Override 
	public IConfigZZZ getConfiguration() throws ExceptionZZZ{
		return this.objConfig;
	}
	
	@Override
	public void setConfiguration(IConfigZZZ objConfig) throws ExceptionZZZ{
		this.objConfig = objConfig;
	}
	
	//##########################
	//### GIT Konfiguration
	//### aus IJgitStarterLocal
	@Override
	public boolean configureGitCustom() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
			bReturn = this.configureGitCustom(objConfig);
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean configureGitCustom(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			
		}//end main:
		return bReturn;
	}

	
	
	//###############################################
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
	@Override
	public boolean configureRepositoryLocal(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(objConfig==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente, ggfs aus der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteAlias = objConfig.readRepositoryRemoteAlias();
			boolean bRemoteAliasAvailable = !StringZZZ.isEmpty(sRepositoryRemoteAlias);
//				if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
//					ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;
//				}
			this.setRepositoryRemoteAlias(sRepositoryRemoteAlias);
			
			
			String sRepositoryLocalBase = objConfig.readRepositoryLocalBaseDirectory();
			if(StringZZZ.isEmpty(sRepositoryLocalBase)){
				ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Basis Repository", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryLocalBase(sRepositoryLocalBase);
			
			
			String sRepositoryProjectName = objConfig.readRepositoryProjectName();
			if(StringZZZ.isEmpty(sRepositoryProjectName) & !bRemoteAliasAvailable){
				ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryProjectName(sRepositoryProjectName);
			
			String sDirectoryRepositoryTotalLocal = FileEasyZZZ.joinFilePathName(sRepositoryLocalBase, sRepositoryProjectName);
			File objDirectoryRepositoryLocalTotal = new File(sDirectoryRepositoryTotalLocal);
			if(!objDirectoryRepositoryLocalTotal.exists()){
				ExceptionZZZ ez = new ExceptionZZZ("Verzeichnis des Repositories existiert nicht '" + sDirectoryRepositoryTotalLocal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryTotalLocal(sDirectoryRepositoryTotalLocal);
			
			//Merke: Branch darf leer sein
			String sRepositoryBranch = objConfig.readRepositoryBranch();
			this.setRepositoryBranch(sRepositoryBranch);
		
			//Hier gibt es keine RemoteURL, daher nicht
			//In das lokale Repository soll nun unbedingt der passende Eintrag in die GIT-Konfigurationsdatei 'config' gemacht werden
			//Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryTotalLocal, true);
			//JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, sRepositoryRemoteBranch, true);
			
			//Der Vollständigkeit halber: Eclipse Projektname
			String sProjectNameLocal = objConfig.readProjectName();
			if(StringZZZ.isEmpty(sProjectNameLocal)){
				ExceptionZZZ ez = new ExceptionZZZ("Projektname des Eclipse Projekts", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setProjectName(sProjectNameLocal);
			
			
			
			bReturn = true;
		}//end main:
		return bReturn;
	}
		
	/** Ohne ein IConfig - Objekt als Argument, muss alles aus den Properties des Objekts gelesen werden.
	 * @return
	 * @throws ExceptionZZZ
	 */
	@Override
	public boolean configureRepositoryLocal() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		
			String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
			boolean bRemoteAliasAvailable = !StringZZZ.isEmpty(sRepositoryRemoteAlias);
//				if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
//					ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;
//				}
			
			String sRepositoryLocal = this.getRepositoryLocalBase();
			if(StringZZZ.isEmpty(sRepositoryLocal)){
				ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryLocalBase(sRepositoryLocal);
			
			
			String sRepositoryProject = this.getRepositoryProjectName();
			if(StringZZZ.isEmpty(sRepositoryProject) & !bRemoteAliasAvailable){
				ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryProjectName(sRepositoryProject);
			
			//Merke: Branch darf leer sein
			String sRepositoryBranch = this.getRepositoryBranch();
			
			//String sDirectoryRepositoryTotalLocal = FileEasyZZZ.joinFilePathName(sRepositoryLocal, sRepositoryProject);
			File objDirectoryRepositoryLocal = new File(sRepositoryLocal);
			if(!objDirectoryRepositoryLocal.exists()){
				ExceptionZZZ ez = new ExceptionZZZ("Basis Verzeichnis des Repositories existiert nicht '" + sRepositoryLocal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryLocalTotal = FileEasyZZZ.joinFilePathName(objDirectoryRepositoryLocal, sRepositoryProject);
			File objRepositoryTotalLocal = new File(sRepositoryLocalTotal);
			if(!objRepositoryTotalLocal.exists()){
				ExceptionZZZ ez = new ExceptionZZZ("Verzeichnis des Repositorie-Projekts existiert nicht '" + sRepositoryLocalTotal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryTotalLocal(sRepositoryLocalTotal);
			
			String sRepositoryRemoteUrlByAlias = null; String sRepositoryRemoteFetchByAlias = null;
			if(!bRemoteAliasAvailable & StringZZZ.isEmpty(this.getRepositoryTotalRemote())) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Alias nicht vorhanden und Pfad für RepositoryTotalRemote auch nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else {
				//+++ Prüfe, ob https oder ssh in der .git\config Datei steht	
				Repository repo = JgitUtilZZZ.getRepositoryObject(sRepositoryLocalTotal, true);
				sRepositoryRemoteUrlByAlias = repo.getConfig()
						       .getString("remote",sRepositoryRemoteAlias,"url");
				if(StringZZZ.isEmpty(sRepositoryRemoteUrlByAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Keine Remote Repository URL bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				sRepositoryRemoteFetchByAlias = repo.getConfig()
					       .getString("remote",sRepositoryRemoteAlias,"fetch");
				if(StringZZZ.isEmpty(sRepositoryRemoteFetchByAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Kein Remote Repository FETCH bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
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
	
	//#######################################
	/* (non-Javadoc)
	 * @see use.jgit.IJgitStarterCommit#configureGit()
	 */
	@Override
	public boolean configureGit() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
//			try {			
				//### Soll das lokale Repository konfiguriert haben.			
				//    Die benoetigten Parameter aus dem Argumenten des Aufrufs holen. Wiederverwendbare Methode nutzen.					
				boolean bLocalRepositoryConfigured = this.configureRepositoryLocal();
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
					String sProject = this.getRepositoryProjectName();					
					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Verzeichnis für das Projekt '" + sProject + "'nicht definiert.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}else {
					System.out.println("Lokales Repository als Gesamtstring vorhanden: '" + sDirectoryRepositoryLocalTotal + "'");
				}
				
				//Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
				//hier könnte man noch den RefNamen auf Gültigkeit prüfen, etc.
				
				File objFileDirTotal = new File(sDirectoryRepositoryLocalTotal);
				if(!objFileDirTotal.exists()) {
					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Projekt Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocalTotal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
								
				this.configureGitCustom();
				System.out.println("Lokale Gesamtkonfiguration fertig für: '" + objFileDirTotal.getAbsolutePath() + "'");
				
				//wird jetzt als createGitObject() gemacht....
//				InitCommand gitCommandInit = Git.init();
//				gitCommandInit.setDirectory(objFileDirTotal);
//				this.configureGitCustom(gitCommandInit);
//				
//				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
//				this.setGitObject(git);
//				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
				//##############################################
				//Weil das was mit dem Wunsch-Protocol zu tun hat, hier nicht machen
				//... JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);

				
				//######################################
//			}catch(GitAPIException gae) {
//				ExceptionZZZ ez = new ExceptionZZZ(gae);
//				throw ez;
//			}
				
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	//#######################################
	@Override
	public boolean configureGit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{				
			//### Soll das lokale Repository konfiguriert haben.			
			//    Die benoetigten Parameter aus dem Argumenten des Aufrufs holen. Wiederverwendbare Methode nutzen.					
			boolean bLocalRepositoryConfigured = this.configureRepositoryLocal(objConfig);
			if(bLocalRepositoryConfigured) {
				System.out.println("Lokales Repository erfolgreich konfiguriert");
			}else {
				System.out.println("Lokales Repository NICHT einzeln erfolgreich konfiguriert");
				//Wenn das so nicht geklappt hat, dann wurden die Details ggfs. einzeln übergeben... wir werden sehen.
			}
			
			bReturn = this.configureGitCustom(objConfig);
			this.setConfiguration(objConfig);
			
			bReturn = true;
		}//end main:
		return bReturn;
	}

	//#################################################################################
	@Override 
	public Git createGitObject(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ {
		Git objReturn = null;
		main:{
			boolean bReturn = this.configureGit(objConfig);			
			bReturn = this.configureGitCustom(objConfig);
			
			bReturn = this.createGit();
			objReturn = this.getGitObject();
		}//end main:
		return objReturn;
	}
	
	@Override 
	public Git createGitObject() throws ExceptionZZZ {
		Git objReturn = null;
		main:{			
			boolean bReturn = this.configureGit(); 
			
			bReturn = this.createGit();
			objReturn = this.getGitObject();
		}//end main:
		return objReturn;
	}
	
	@Override
	public boolean createGit(IConfigStarterLocalJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{			
			bReturn = this.configureGit(objConfig);			
			bReturn = this.configureGitCustom(objConfig);
				
			bReturn = this.createGit();			
		}//end main:
		return bReturn;		
	}
	
	@Override
	public boolean createGit() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try {
				//++++++++++ Erst das lokale Git-Repository initialisieren
				//           Dann kann dort ggfs. auch etwas fehlendes nachgelesen werden.				
				String sDirectoryRepositoryLocalTotal = this.getRepositoryLocalTotal();				
				if(StringZZZ.isEmpty(sDirectoryRepositoryLocalTotal)) {
					String sProject = this.getRepositoryProjectName();					
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
				
				//Ergänze custom-Eigenschaften
				this.createGitCustom(gitCommandInit);
				
				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
				this.setGitObject(git);	
				
				System.out.println("Git Objekt erstellt für das lokale Repository: '" + objFileDirTotal.getAbsolutePath() + "'");				
			} catch (GitAPIException e) {
				ExceptionZZZ ez = new ExceptionZZZ(e);
				throw ez;
			} 
			
			bReturn = true;
		}//end main:
		return bReturn;		
	}
	
	@Override
	public boolean createGitCustom(InitCommand objInitCommand) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			
		}//end main:
		return bReturn;
	}
	
	
	
	

	
	
	//##############################################################################
	
	
	
	//####################################################################
	//###### STATUS ######################################################
	
	//### aus IJgitStarterLocal
	@Override
	public boolean statusit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			if (git == null) {
	            throw new IllegalArgumentException("git must not be null");
	        }
			
			//Sichere den Status in einem XML strukturierten Sting. Die Angaben gehen über die Daten der späteren Konsolenausgabe hinaus.
			String sStatusXml = this.createStatusXml(git);
			this.sStatusXml = sStatusXml;
						
			//Finde geaenderte und neue Dateien fuer den Commit			
			System.out.println("STATUS: ");		
			this.printStatus(git);
			
			bReturn = true;
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
				boolean bSuccess = this.createGit(objConfig);
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert und erstellt.");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert und erstellt.");
					break main;
				}
			
				
				//+++++++++++++++++++++++++++++++
				//Finde geaenderte und neue Dateien fuer den commit
				Git git = this.getGitObject();
				bReturn = this.statusit(git);
				if(bReturn) {
					System.out.println("STATUS REQUEST: SUCCESSFUL");				
				}else {
					System.out.println("STATUS REQUEST: FAILED");				
				}
			
			    git.close();
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;		
			}
			//bReturn = true;
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean statusit() throws ExceptionZZZ {
		IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
		return this.statusit(objConfig);
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
			boolean bSuccess = this.createGit(objConfig);
			if(bSuccess) {
				System.out.println("Git erfolgreich konfiguriert und erstellt.");
			}else {
				System.out.println("Git NICHT erfolgreich konfiguriert und erstellt.");
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
		        String sProjectStartingName = this.getProjectStartingName();
		        sComment = JgitUtilZZZ.createCommentCommit(sComment, sProjectStartingName);
		        		        
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
	public boolean commitit() throws ExceptionZZZ {
		IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
		return this.commitit(objConfig);
	}
	
	@Override
	public boolean commitit(String sComment) throws ExceptionZZZ {
		IConfigStarterLocalJGIT objConfig = (IConfigStarterLocalJGIT) this.getConfiguration();
		return this.commitit(objConfig, sComment);
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		
	//##################################################
	@Override
	public void printStatus() throws ExceptionZZZ {
		Git git = this.getGitObject();
		this.printStatus(git);
	}
	
	@Override
	public void printStatus(Git git) throws ExceptionZZZ {
		main:{
		try {
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
	        
		}catch (NoWorkTreeException nwte) {
			System.out.println(nwte.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(nwte);
    		throw ez;
		}catch( GitAPIException gae) {
			System.out.println(gae.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(gae);
    		throw ez;
		}
		}//end main:
	}
	
	@Override
	public String createStatusXml() throws ExceptionZZZ {
		Git git = this.getGitObject();
		return this.createStatusXml(git);
	}
	
	@Override
	public String createStatusXml(Git git) throws ExceptionZZZ {
	    StringBuilder sb = new StringBuilder();

	    main:{
	        try {
	            if (git == null) {
	                throw new IllegalArgumentException("git must not be null");
	            }

	            StatusCommand statusCommand = git.status();
	            if (statusCommand == null) {
	                sb.append("<gitStatus/>");
	                break main;
	            }

	            Status status = statusCommand.call();
	            if (status == null) {
	                sb.append("<gitStatus/>");
	                break main;
	            }

	            sb.append("<gitStatus>\n");

	            JgitUtilXmlZZZ.appendSet(sb, "added", status.getAdded());
	            JgitUtilXmlZZZ.appendSet(sb, "changed", status.getChanged());
	            JgitUtilXmlZZZ.appendSet(sb, "conflicting", status.getConflicting());
	            JgitUtilXmlZZZ.appendSet(sb, "ignoredNotInIndex", status.getIgnoredNotInIndex());
	            JgitUtilXmlZZZ.appendSet(sb, "missing", status.getMissing());
	            JgitUtilXmlZZZ.appendSet(sb, "modified", status.getModified());
	            JgitUtilXmlZZZ.appendSet(sb, "removed", status.getRemoved());
	            JgitUtilXmlZZZ.appendSet(sb, "uncommitted", status.getUncommittedChanges());
	            JgitUtilXmlZZZ.appendSet(sb, "untracked", status.getUntracked());
	            JgitUtilXmlZZZ.appendSet(sb, "untrackedFolders", status.getUntrackedFolders());

	            sb.append("</gitStatus>");

	        } catch (NoWorkTreeException nwte) {
	            throw new ExceptionZZZ(nwte);

	        } catch (GitAPIException gae) {
	            throw new ExceptionZZZ(gae);
	        }
	    }//end main

	    return sb.toString();
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
