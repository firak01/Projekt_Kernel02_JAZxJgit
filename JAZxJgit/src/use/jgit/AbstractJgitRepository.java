package use.jgit;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.config.IConfigRepositoryJGIT;
import use.jgit.protocol.ssh.JgitStarterSSH;
import use.jgit.util.JgitUtilZZZ;

public abstract class AbstractJgitRepository <T> extends AbstractObjectWithFlagZZZ<T> implements IJgitRepository, IJgitRepositoryEnabledZZZ{
	protected volatile Git gitObject = null;
	
	protected volatile String sRepositoryProject=null;//Der Name des Projekt, wie er hinter die Basis Verzeichnis/Url kommt.
	protected volatile String sRepositoryBranch=null; //Der Name des Branch, wenn man es nicht auf alle Branches beziehen will.
	
	protected volatile String sRepositoryLocalBase=null;  //Basis Verzeichnis
	protected volatile String sRepositoryLocalTotal=null; //Geamt Verzeichnis
	
	//Remoteinformatione, die in die lokale GIT-Konfiguration geschrieben werden 
	protected volatile String sRepositoryTotalRemote=null; //Gesamt URL	
	protected volatile String sRepositoryRemoteAlias=null;//die Section in der ini z.B. [origin]
	
	
	
	//############ GETTER  / SETTER	
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
	
	
	//+++++++++++++++++++++++
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
	public boolean configureRepositoryLocal(IConfigRepositoryJGIT objConfig) throws ExceptionZZZ{
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
				ExceptionZZZ ez = new ExceptionZZZ("Verzeichnis des Repositories existiert nicht '" + sDirectoryRepositoryTotalLocal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryTotalLocal(sDirectoryRepositoryTotalLocal);
			
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
//			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
//				ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
			
			String sRepositoryLocal = this.getRepositoryLocalBase();
			if(StringZZZ.isEmpty(sRepositoryLocal)){
				ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryLocalBase(sRepositoryLocal);
			
			
			String sRepositoryProject = this.getRepositoryProject();
			if(StringZZZ.isEmpty(sRepositoryProject) & !bRemoteAliasAvailable){
				ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//Merke: Branch darf leer sein
			String sRepositoryBranch = this.getRepositoryBranch();
			
			String sDirectoryRepositoryTotalLocal = FileEasyZZZ.joinFilePathName(sRepositoryLocal, sRepositoryProject);
			File objDirectoryRepositoryLocalTotal = new File(sDirectoryRepositoryTotalLocal);
			if(!objDirectoryRepositoryLocalTotal.exists()){
				ExceptionZZZ ez = new ExceptionZZZ("Verzeichnis des Repositories existiert nicht '" + sDirectoryRepositoryTotalLocal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
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
			try {			
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
					String sProject = this.getRepositoryProject();					
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
				
				InitCommand gitCommandInit = Git.init();
				gitCommandInit.setDirectory(objFileDirTotal);
				
				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
				this.setGitObject(git);
				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
				//##############################################
				//Weil das was mit dem Wunsch-Protocol zu tun hat, hier nicht machen
				//... JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);

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
	public boolean configureGit(IConfigRepositoryJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try {			
				//### Soll das lokale Repository konfiguriert haben.			
				//    Die benoetigten Parameter aus dem Argumenten des Aufrufs holen. Wiederverwendbare Methode nutzen.					
				boolean bLocalRepositoryConfigured = this.configureRepositoryLocal(objConfig);
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
				
				//Ergänze custom-Eigenschaften
				this.configureGitCustom(gitCommandInit);
				
				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
				this.setGitObject(git);
			
				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
				
				///##############################################
				//Weil das was mit dem Wunsch-Protocol zu tun hat, hier nicht machen
				//... JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);

				bReturn = true;
				//######################################
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}

	public abstract boolean configureGitCustom(InitCommand objInitCommand) throws ExceptionZZZ;
	
	//############# STATIC METHODEN

	//###############################################
	//### FLAG HANDLING
	//###############################################
	
	//### aus IJgitEnabledZZZ
	@Override
	public boolean getFlag(IJgitRepositoryEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.getFlag(objEnumFlag.name());
	}

	@Override
	public boolean setFlag(IJgitRepositoryEnabledZZZ.FLAGZ objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlag(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlag(IJgitRepositoryEnabledZZZ.FLAGZ[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitRepositoryEnabledZZZ.FLAGZ objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagExists(IJgitRepositoryEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagSetBefore(IJgitRepositoryEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}
	
	//###################################
	//### FLAGLOCAL Handling

	//### aus JgitEnabledZZZ	
	@Override
	public boolean getFlagLocal(IJgitRepositoryEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.getFlagLocal(objEnumFlag.name());
	}

	@Override
	public boolean setFlagLocal(IJgitRepositoryEnabledZZZ.FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagLocal(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagLocal(IJgitRepositoryEnabledZZZ.FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitRepositoryEnabledZZZ.FLAGZLOCAL objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlagLocal(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagLocalExists(IJgitRepositoryEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagLocalExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagLocalSetBefore(IJgitRepositoryEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}

	


	//###################################
	//### FLAG CUSTOM Handling
		
	@Override
	public boolean getFlagCustom(IJgitRepositoryEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.getFlagCustom(objEnumFlag.name());
	}

	@Override
	public boolean setFlagCustom(IJgitRepositoryEnabledZZZ.FLAGZCUSTOM objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagCustom(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagCustom(IJgitRepositoryEnabledZZZ.FLAGZCUSTOM[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitRepositoryEnabledZZZ.FLAGZCUSTOM objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagCustomExists(IJgitRepositoryEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagCustomSetBefore(IJgitRepositoryEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomSetBefore(objEnumFlag.name());
	}
}
