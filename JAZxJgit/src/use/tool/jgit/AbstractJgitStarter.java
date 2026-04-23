package use.tool.jgit;

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
import use.tool.jgit.IJgitEnabledZZZ.FLAGZLOCAL;
import use.tool.jgit.protocol.ssh.JgitStarterSSH;

public abstract class AbstractJgitStarter<T> extends AbstractObjectWithFlagZZZ<T> implements IJgitStarter, IJgitEnabledZZZ{
	private static final long serialVersionUID = -1998325674945232389L;
	
	protected volatile Git gitObject = null;
	protected volatile CredentialsProvider credentialsProviderObject = null;
	
	protected volatile String sConnectionType=null;
	
	protected volatile String sRepositoryProject=null;//Der Name des Projekt, wie er hinter die Basis Verzeichnis/Url kommt.
	
	protected volatile String sRepositoryBaseLocal=null;  //Basis Verzeichnis
	protected volatile String sRepositoryTotalLocal=null;  //Geamt Verzeichnis

	protected volatile String sRepositoryRemoteAlias=null;
	
	protected volatile String sRepositoryRemoteHost=null;
	protected volatile String sRepositoryRemoteAccount=null;
	protected volatile String sRepositoryBaseRemote=null; //Basis URL	
	protected volatile String sRepositoryTotalRemote=null; //Gesamt URL
		

	//### aus IJgitStarter
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

	
	
	@Override 
	public Git getGitObject() throws ExceptionZZZ{
		return this.gitObject;
	}
	
	@Override
	public void setGitObject(Git objGit) throws ExceptionZZZ{
		this.gitObject = objGit;
	}
	
	@Override 
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ{
		return this.credentialsProviderObject;
	}
	
	@Override
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ{
		this.credentialsProviderObject = objCredentialsProvider;
	}
	
	@Override
	public String getRepositoryProject() throws ExceptionZZZ {
		return this.sRepositoryProject;
	}
	
	@Override 
	public void setRepositoryProject(String sRepositoryProject) throws ExceptionZZZ {
		this.sRepositoryProject = sRepositoryProject;
	}
	
	@Override
	public String getRepositoryBaseLocal() throws ExceptionZZZ {
		return this.sRepositoryBaseLocal;
	}
	
	@Override
	public void setRepositoryBaseLocal(String sRepositoryBaseLocal) throws ExceptionZZZ {
		this.sRepositoryBaseLocal = sRepositoryBaseLocal;
	}
	
	@Override
	public String getRepositoryTotalLocal() throws ExceptionZZZ {
		return this.sRepositoryTotalLocal;
	}
	
	@Override
	public void setRepositoryTotalLocal(String sRepositoryTotalLocal) throws ExceptionZZZ {
		this.sRepositoryTotalLocal = sRepositoryTotalLocal;
	}

	//++++++++++++++++++++++++++++
	@Override
	public String getRepositoryRemoteAlias() throws ExceptionZZZ {
		if(this.sRepositoryRemoteAlias==null) {
			this.sRepositoryRemoteAlias = IJgitStarter.sREPOSITORY_REMOTE_ALIAS_DEFAULT;
		}
		return this.sRepositoryRemoteAlias;
	}

	@Override
	public void setRepositoryRemoteAlias(String sRepositoryRemoteAlias) throws ExceptionZZZ {
		this.sRepositoryRemoteAlias = sRepositoryRemoteAlias;
	}
	
	@Override
	public String getRepositoryRemoteAccount() throws ExceptionZZZ {
		return this.sRepositoryRemoteAccount;
	}

	@Override
	public void setRepositoryRemoteAccount(String sRepositoryRemoteAccount) throws ExceptionZZZ {
		this.sRepositoryRemoteAccount = sRepositoryRemoteAccount;
	}

	@Override
	public String getConnectionType() throws ExceptionZZZ {
		return this.sConnectionType;
	}

	@Override
	public void setConnectionType(String sConnectionType) throws ExceptionZZZ {
		this.sConnectionType = sConnectionType;
	}
	
	@Override
	public String getRepositoryRemoteHost() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryRemoteHost)) {
			String sUrlRepo = this.searchRepositoryRemote();
			
			String sRepositoryRemoteHost = JgitUtil.computeRepositoryHostFromUrlRepo(sUrlRepo);
			this.setRepositoryRemoteHost(sRepositoryRemoteHost);
		}
		return this.sRepositoryRemoteHost;
	}

	@Override
	public void setRepositoryRemoteHost(String sRepositoryRemoteHost) throws ExceptionZZZ {
		this.sRepositoryRemoteHost = sRepositoryRemoteHost;
	}
	
	@Override
	public String getRepositoryBaseRemote() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryBaseRemote)) {
			String sHost=this.getRepositoryRemoteHost();
			String sAccount=this.getRepositoryRemoteAccount();
			if(!(StringZZZ.isEmpty(sHost) | StringZZZ.isEmpty(sAccount))){
				this.sRepositoryBaseRemote = this.computeRepositoryBaseRemote(sHost, sAccount);
			}
			
			//immer noch nix - weil z.B. kein Hostangaben, dann suchen im lokalen Git-Repository nach dem alias
			if(StringZZZ.isEmpty(this.sRepositoryBaseRemote)) {
				String sRepositoryTotalRemote = this.searchRepositoryRemote();
				if(JgitUtil.isUrlHTTPS(sRepositoryTotalRemote)){
					JgitUtilHTTPS.computeRepositoryUrlBaseFromUrlHTTPS(sRepositoryTotalRemote);
				}else if(JgitUtil.isUrlSSH(sRepositoryTotalRemote)){
					JgitUtilSSH.computeRepositoryUrlBaseFromUrlSSH(sRepositoryTotalRemote);
				}else {
					ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sRepositoryTotalRemote + "'", iERROR_PARAMETER_VALUE, JgitUtil.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
		}
		return this.sRepositoryBaseRemote;
	}

	@Override
	public void setRepositoryBaseRemote(String sRepositoryBaseRemote) throws ExceptionZZZ {
		this.sRepositoryBaseRemote = sRepositoryBaseRemote;
	}
	
	@Override
	abstract public String getRepositoryTotalRemote() throws ExceptionZZZ;		
		

	@Override
	public void setRepositoryTotalRemote(String sRepositoryTotalRemote) throws ExceptionZZZ {
		this.sRepositoryTotalRemote = sRepositoryTotalRemote;
	}
	
	@Override
	public String computeRepositoryBaseRemote() throws ExceptionZZZ{
		String sHost = this.getRepositoryRemoteHost();
		String sAccount = this.getRepositoryRemoteAccount();
		return this.computeRepositoryBaseRemote(sHost, sAccount);
	}
	
	@Override
	public String computeRepositoryRemoteUrl() throws ExceptionZZZ {
		String sRepositoryBaseRemoteIn = this.computeRepositoryBaseRemote();
		String sRepositoryProjectIn = this.getRepositoryProject();
		return this.computeRepositoryRemoteUrl(sRepositoryBaseRemoteIn, sRepositoryProjectIn);
	}
	
	@Override
	public String computeRepositoryRemoteUrl(String sRepositoryBaseRemoteIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sRepositoryBaseRemote=null; String sRepositoryProject=null;
			if(StringZZZ.isEmpty(sRepositoryBaseRemoteIn)) {
				sRepositoryBaseRemote = this.computeRepositoryBaseRemote();
				if(StringZZZ.isEmpty(sRepositoryBaseRemote)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryBaseRemote", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sRepositoryBaseRemote = sRepositoryBaseRemoteIn;
			}
			
			
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)) {
				sRepositoryProject = this.getRepositoryProject();
				if(StringZZZ.isEmpty(sRepositoryProject)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryProject", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sRepositoryProject = sRepositoryProjectIn;				
			}
			
			
			sReturn = JgitUtil.computeRepositoryUrl(sRepositoryBaseRemote, sRepositoryProject);			
			
		}//end main:
		return sReturn;
	}
	
	@Override
	public String searchRepositoryRemote() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();					
			sReturn = this.searchRepositoryRemote(sRepositoryRemoteAlias);
		}//end main:
		return sReturn;
	}
	
	@Override
	public String searchRepositoryRemote(String sRepositoryRemoteAlias) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			Git git = this.getGitObject();			
			if(git==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Git Object", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
			
			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)) {
				ExceptionZZZ ez = new ExceptionZZZ("RepositoryRemoteAlias", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;	
			}
			
			
			//+++ Prüfe, ob https oder ssh in der .git\config Datei steht	
			String sRepositoryRemoteByAlias = git.getRepository().getConfig()
					       .getString("remote",sRepositoryRemoteAlias,"url");
			System.out.println("Git-Repository verwendet folgendes Remote (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteByAlias +"'");											
			sReturn = sRepositoryRemoteByAlias;
		}//end main:
		return sReturn;
	}
	
	
	//##############################################################################	
	@Override
	public boolean commitit(Git git) throws ExceptionZZZ {
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
				
		        //Mache einen commit (mit aktuellem Datum/Uhrzeit)
				long lTimestamp = DateTimeZZZ.computeTimestamp();
				SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy_H:m");		
				String sDateFormated = dateFormater.format(lTimestamp);
		
				//Hier den Namen des Rechners einfügen
				String sHostname = EnvironmentZZZ.getHostName();
				
				CommitCommand gitCommandCommit = git.commit();
				gitCommandCommit.setMessage(sDateFormated + " (Host: '" + sHostname + "') by Java-Class from a module of Projekt_Tool_DevEditor");
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
	public boolean configureGit() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try {
				//A) Lokal
				//a) Lokales Basis Verzeichnis
				String sDirectoryRepositoryLocal = this.getRepositoryBaseLocal();
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
				Repository repo = JgitUtil.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
				
				//++++++++++ Erst das lokale Git-Repository initialisieren
				//           Dann kann dort ggfs. auch etwas fehlendes nachgelesen werden.				
				InitCommand gitCommandInit = Git.init();
				gitCommandInit.setDirectory(objFileDirTotal);
				
				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
				this.setGitObject(git);
				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
				//##############################################
												
				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH)
				//Problem: Wenn hier dier GesamtRepositoryURL nur ausgelesen wird, dann passt das Protokol ggfs. nicht (https URL geht nicht beim ssh Weg.
				//         Darum hier die remote Repository URL neu ausrechnen... String sRepositoryRemoteUrl = this.getRepositoryTotalRemote();	
				String sRepositoryRemoteUrl = this.computeRepositoryRemoteUrl();
				if(!StringZZZ.isEmpty(sRepositoryRemoteUrl)) {
					String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
					JgitUtil.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);
				}
				bReturn = true;
				//######################################
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean configureRepositoryLocal(IConfigJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(objConfig==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteAliasIn = objConfig.readRepositoryRemoteAlias();
			boolean bRemoteAliasAvailable = !StringZZZ.isEmpty(sRepositoryRemoteAliasIn);
//			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
//				ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
			this.setRepositoryRemoteAlias(sRepositoryRemoteAliasIn);
			
			
			String sRepositoryLocalIn = objConfig.readRepositoryLocal();
			if(StringZZZ.isEmpty(sRepositoryLocalIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryBaseLocal(sRepositoryLocalIn);
			
			
			String sRepositoryProjectIn = objConfig.readRepositoryProjectName();
			if(StringZZZ.isEmpty(sRepositoryProjectIn) & !bRemoteAliasAvailable){
				ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryProject(sRepositoryProjectIn);
			
			
			String sDirectoryRepositoryLocalTotal = FileEasyZZZ.joinFilePathName(sRepositoryLocalIn, sRepositoryProjectIn);
			File objDirectoryRepositoryLocalTotal = new File(sDirectoryRepositoryLocalTotal);
			if(!objDirectoryRepositoryLocalTotal.exists()){
				ExceptionZZZ ez = new ExceptionZZZ("Verzeichnis des Repositories existiert nicht '" + sDirectoryRepositoryLocalTotal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryTotalLocal(sDirectoryRepositoryLocalTotal);
			
			String sRepositoryRemoteByAliasIn = null;
			if(bRemoteAliasAvailable) {
				//+++ Prüfe, ob https oder ssh in der .git\config Datei steht	
				Repository repo = JgitUtil.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
				sRepositoryRemoteByAliasIn = repo.getConfig()
						       .getString("remote",sRepositoryRemoteAlias,"url");
				if(StringZZZ.isEmpty(sRepositoryRemoteByAliasIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Kein Remote Repository bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				System.out.println("Git-Repository verwendet folgendes Remote (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteByAliasIn +"'");
				
				
			}
			this.setRepositoryTotalRemote(sRepositoryRemoteByAliasIn);
			
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	
	@Override
	public abstract boolean commitAndPushit(IConfigJGIT objConfig) throws ExceptionZZZ;

	@Override
	public abstract boolean pullit(IConfigJGIT objConfig) throws ExceptionZZZ;

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
	
	
	//###################################
	//### FLAGLOCAL Handling
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
	//###################################			
	
	//### aus IJgitEnabledZZZ
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
	
	//###################################
	//### FLAG Handling
		
	
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
	
}
