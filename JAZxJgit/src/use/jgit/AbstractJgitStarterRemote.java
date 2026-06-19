package use.jgit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
import org.eclipse.jgit.transport.SshSessionFactory;

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
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.protocol.ssh.JGitSshConfigZZZ;
import use.jgit.protocol.ssh.JgitStarterSSH;
import use.jgit.util.JgitUtilHTTPS;
import use.jgit.util.JgitUtilSSH;
import use.jgit.util.JgitUtilZZZ;

public abstract class AbstractJgitStarterRemote<T> extends AbstractJgitStarterLocal<T> implements IJgitStarterRemote{
	private static final long serialVersionUID = -1998325674945232389L;
	
	protected volatile CredentialsProvider credentialsProviderObject = null;
	
	protected volatile String sConnectionType=null;

	
	protected volatile String sRepositoryRemoteHost=null;
	protected volatile String sRepositoryRemoteAccount=null;
	protected volatile String sRepositoryBaseRemote=null; //Basis URL	
		

	//### aus IJgitStarterLocal
	@Override
	public boolean configureGit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{		
			bReturn = super.configureGit((IConfigStarterLocalJGIT) objConfig);
			
			//+++ Prüfe, ob https oder ssh in der .git\config Datei steht
			//Stelle sicher, dass das gewünschte Protokoll passt. Also: Die URL in die Konfiguration eintragen.
			String sDirectoryRepositoryTotalLocal = this.getRepositoryLocalTotal();			
			if(StringZZZ.isEmpty(sDirectoryRepositoryTotalLocal)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Alias nicht vorhanden für Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
													
			//#######################################################
			//### Remote Konfigurationsdaten aus der Lokalen Konfiguration wiederverwenden
			String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Alias nicht vorhanden für Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//#######################################################
			//### Remote Konfigurationen erstmalig in der Remote Konfiguration auslesen
			String sRepositoryRemoteHost = objConfig.readRepositoryRemoteHost();
			if(StringZZZ.isEmpty(sRepositoryRemoteHost)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Host nicht vorhanden für Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryRemoteHost(sRepositoryRemoteHost);
			
			String sRepositoryRemoteAccount = objConfig.readRepositoryRemoteAccount();
			if(StringZZZ.isEmpty(sRepositoryRemoteAccount)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Account nicht vorhanden für Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryRemoteAccount(sRepositoryRemoteAccount);
			
			//########################################################
			//### Remote Konfigurationswerte ausrechnen, wichtig, da wir sie ja neu in die Konfiguration schreiben wollen.
			//Problem: Wenn hier die GesamtRepositoryURL nur ausgelesen wird, dann passt das Protokol ggfs. nicht (https URL geht nicht beim ssh Weg.
//			//         Darum hier die remote Repository URL neu ausrechnen... 
			//  Merke: Für HTTPS ist hier auch ein URL ohne SPAT erlaubt: url = https://github.com/firak01/1fgl_Test_repo_readwrite.git
			//                                                            url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
			//Wenn es leer ist, wird dann auch (indirekt) neu berechnet
			//String sRepositoryRemoteUrl = this.getRepositoryTotalRemote();
			//Aber transparenter ist das direkte Berechnen
			String sRepositoryRemoteUrl = this.computeRepositoryRemoteUrl();
			if(StringZZZ.isEmpty(sRepositoryRemoteUrl)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote URL nicht vorhanden für Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteBranch = this.getRepositoryBranch();
			if(StringZZZ.isEmpty(sRepositoryRemoteBranch)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Branch nicht vorhanden für Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			//In das lokale Repository soll nun unbedingt der passende Eintrag in die GIT-Konfigurationsdatei 'config' gemacht werden
			Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryTotalLocal, true);
			JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, sRepositoryRemoteBranch, true);
			
			//##################################################
			//### Wieder auslesen zur Ausgabe.....							
			String sRepositoryRemoteUrlByAlias = repo.getConfig()
					       .getString("remote",sRepositoryRemoteAlias,"url");
			if(StringZZZ.isEmpty(sRepositoryRemoteUrlByAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Keine Remote Repository URL bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteFetchByAlias = repo.getConfig()
				       .getString("remote",sRepositoryRemoteAlias,"fetch");
			if(StringZZZ.isEmpty(sRepositoryRemoteFetchByAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Kein Remote Repository FETCH bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarterRemote.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Git-Repository verwendet folgende Remote URL (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteUrlByAlias +"'");
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Git-Repository verwendet folgende Remote FETCH (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteFetchByAlias +"'");
			
			this.setRepositoryTotalRemote(sRepositoryRemoteUrlByAlias);
			
			//########################################################
			//### Weitere Werte füllen
			//+++ Folgende Konfiguration könnten aus dem Alias und dem Repository geholt werden
			String sConnectionTypeIn = objConfig.readConnectionType();
			if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
				//Diese Detail aus der .git\config Datei unter dem Alias auslesen.
				String sDirectoryRepositoryLocalRemote = this.getRepositoryTotalRemote();
				if(StringZZZ.isEmpty(sDirectoryRepositoryLocalRemote)) {
					ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				sConnectionTypeIn = JgitUtilZZZ.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);
			}
			//Falls immer noch leer, Fehler!
			if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
				ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
			if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
				ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote SSH Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryBaseRemote(sRepositoryRemoteIn);

			//#######################################
			//### Eintragen von github.com in die C:\Users\<User>\.ssh\known_hosts Datei ist normalerweise notwendig.
			//### Es sollte aber mit der JschConfigSessionFactoryZZZ diese Prüfung verhindert werden.
			//### 
			//### Falls das nicht klappt oder doch eingetragen werden soll, können die notwendigen Einträge hier gefunden werden:
			//### https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/githubs-ssh-key-fingerprints?utm_source=chatgpt.com
			//###
			//######################################
			
			//+++ Zugriff sicherstellen
			//0) SshSessionFactory ... mit den verwendeten Ids, Pfaden, etc.
			//Das müsste eigentlich für HTTPS nicht gemacht werden.
			if(sConnectionTypeIn.equalsIgnoreCase("https")) {
				System.out.println("Bei HTTPS wird keine SSH Session Factory benötigt");
			}else {
				JGitSshConfigZZZ.configure();
				System.out.println("Verwendete Ssh Session Factory: " + SshSessionFactory.getInstance().getClass());
			}
			
			
			//######################################
		}//end main:
		return bReturn;
	}
	
	//### aus IJgitStarter	
	@Override
	public abstract String getRepositoryRemoteProtocol() throws ExceptionZZZ;
	
	@Override
	public String getRepositoryRemoteAccount() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryRemoteAccount)) {
			String sUrlRepo = this.searchRepositoryRemote();			
			String sRepositoryRemoteAccount = JgitUtilZZZ.computeRepositoryAccountFromUrlRepo(sUrlRepo);
			this.setRepositoryRemoteAccount(sRepositoryRemoteAccount);
		}		
		return this.sRepositoryRemoteAccount;
	}
	
	@Override
	public void setRepositoryRemoteAccount(String sRepositoryRemoteAccount) throws ExceptionZZZ {
		this.sRepositoryRemoteAccount = sRepositoryRemoteAccount;
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
			
			String sRepositoryRemoteHost = JgitUtilZZZ.computeRepositoryHostFromUrlRepo(sUrlRepo);
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
				if(JgitUtilZZZ.isUrlHTTPS(sRepositoryTotalRemote)){
					JgitUtilHTTPS.computeRepositoryUrlBaseFromUrlHTTPS(sRepositoryTotalRemote);
				}else if(JgitUtilZZZ.isUrlSSH(sRepositoryTotalRemote)){
					JgitUtilSSH.computeRepositoryUrlBaseFromUrlSSH(sRepositoryTotalRemote);
				}else {
					ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sRepositoryTotalRemote + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
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
			String sRepositoryBaseRemote; 
			if(StringZZZ.isEmpty(sRepositoryBaseRemoteIn)) {
				sRepositoryBaseRemote = this.computeRepositoryBaseRemote();
				if(StringZZZ.isEmpty(sRepositoryBaseRemote)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryBaseRemote", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sRepositoryBaseRemote = sRepositoryBaseRemoteIn;
			}
			
			
			String sRepositoryProject;
			if(StringZZZ.isEmpty(sRepositoryProjectIn)) {
				sRepositoryProject = this.getRepositoryProject();
				if(StringZZZ.isEmpty(sRepositoryProject)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryProject", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sRepositoryProject = sRepositoryProjectIn;				
			}
			
			String sProtocol = JgitUtilZZZ.getProtocol(sRepositoryBaseRemoteIn);
			sReturn = JgitUtilZZZ.computeRepositoryUrlTotalFor(sProtocol, sRepositoryBaseRemote, sRepositoryProject);			
			
		}//end main:
		return sReturn;
	}
	
	@Override
	public String computeRepositoryRemoteUrl(String sRepositoryRemoteHostIn, String sRepositoryRemoteAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sRepositoryRemoteHost; 
			if(StringZZZ.isEmpty(sRepositoryRemoteHostIn)) {
				sRepositoryRemoteHost = this.getRepositoryRemoteHost();
				if(StringZZZ.isEmpty(sRepositoryRemoteHost)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryRemoteHost", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sRepositoryRemoteHost = sRepositoryRemoteHostIn;
			}
			
			String sRepositoryRemoteAccount;
			if(StringZZZ.isEmpty(sRepositoryRemoteAccountIn)) {
				sRepositoryRemoteAccount = this.getRepositoryRemoteAccount();
				if(StringZZZ.isEmpty(sRepositoryRemoteAccount)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryRemoteAccount", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sRepositoryRemoteAccount = sRepositoryRemoteAccountIn;
			}
			
			
			String sRepositoryProject;
			if(StringZZZ.isEmpty(sRepositoryProjectIn)) {
				sRepositoryProject = this.getRepositoryProject();
				if(StringZZZ.isEmpty(sRepositoryProject)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryProject", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sRepositoryProject = sRepositoryProjectIn;				
			}
			//############################################################################
			
			//Die jeweilige Klasse kennt ihr Protokoll, das dann vorangestellt wird.
			String sRepositoryBaseRemote = this.computeRepositoryBaseRemote(sRepositoryRemoteHost, sRepositoryRemoteAccount);
			String sProtocol = JgitUtilZZZ.getProtocol(sRepositoryBaseRemote);
			sReturn = JgitUtilZZZ.computeRepositoryUrlTotalFor(sProtocol, sRepositoryBaseRemote, sRepositoryProject);			
			
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
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Git-Repository verwendet folgendes Remote URL (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteByAlias +"'");											
			sReturn = sRepositoryRemoteByAlias;
		}//end main:
		return sReturn;
	}
	
	
	//######################################	
	@Override
	public boolean fetchit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				//Finde geaenderte und neue Dateien fuer den Commit			
				System.out.println("STATUS BEFORE FETCH");		
				this.printStatus(git);
				
				System.out.println("### DEGUB START");				
				try {
					JgitUtilZZZ.debugForFetch(git);
				} catch (URISyntaxException e) {
					ExceptionZZZ ez = new ExceptionZZZ(e);
					throw ez;
				}
		    	System.out.println("### DEBUG ENDE");
		        //##################################################################
		        
				//s. ChatGPT vom 20260313
		        //Problem: Eclipse "registriert/bemerkt" den Push nicht (also Pfeil nach oben mit 1 dahinter wird angezeigt).
		        //Damit in Eclipse auch der Push "registriert/bemerkt wird" muss noch ein Fetch gemacht werden.
		        //Der letzte fetch() sorgt dafür, dass lokale Remote-Tracking-Branches synchron bleiben, 
		        //was besonders hilfreich ist, wenn gleichzeitig ein Tool wie Eclipse auf das gleiche Repository schaut.
		        	        
		        //aber manchmal ist nichts zu fetchen, darum Fehler abfangen 
		        String sDirectoryRepositoryLocalTotal = this.getRepositoryLocalTotal();
		        File objFileDir = new File(sDirectoryRepositoryLocalTotal);

		        String sRepositoryRemote = this.getRepositoryTotalRemote();
		        
		        String sBranch = this.getRepositoryBranch();
		        JgitUtilZZZ.fetchIgnoreNothingToFetch(objFileDir, sRepositoryRemote, sBranch);
			    System.out.println(("FETCH DONE"));
			  	
				
				//##################################################################		        
		        System.out.println("STATUS AFTER FETCH");
		        this.printStatus(git);
		        
		        bReturn = true;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
//	//#######################################
//	@Override
//	public boolean configureGit() throws ExceptionZZZ{
//		boolean bReturn = false;
//		main:{
//			bReturn = super.configureGit();
////			try {
//				//A) Lokal
//				//a) Lokales Basis Verzeichnis
//				String sDirectoryRepositoryLocal = this.getRepositoryLocalBase();
//				if(StringZZZ.isEmpty(sDirectoryRepositoryLocal)) {
//					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Basis Verzeichnis, Angabe fehlt: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;
//				}
//				
//				File objFileDir = new File(sDirectoryRepositoryLocal);
//				if(!objFileDir.exists()) {
//					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Basis Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;				
//				}
////				
////				//b) Lokales Repository-Verzeichnis des Projekts
////				String sRepositoryProjectLocal = this.getRepositoryProject();
////				if(StringZZZ.isEmpty(sDirectoryRepositoryLocal)) {
////					ExceptionZZZ ez = new ExceptionZZZ("Projektname des lokalen Repositories, Angabe fehlt: '" + sRepositoryProjectLocal + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
////					throw ez;
////				}
////				
////				String sDirectoryRepositoryLocalTotal = FileEasyZZZ.joinFilePathName(sDirectoryRepositoryLocal, sRepositoryProjectLocal);
//				String sDirectoryRepositoryLocalTotal = this.getRepositoryLocalTotal();
//				File objFileDirTotal = new File(sDirectoryRepositoryLocalTotal);
//				if(!objFileDirTotal.exists()) {
//					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Projekt Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocalTotal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;				
//				}
//				//this.setRepositoryTotalLocal(sDirectoryRepositoryLocalTotal);
//				
//				//String sDirectoryRepositoryLocalTotal = this.getRepositoryTotalLocal();
//				Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
////				
////				//++++++++++ Erst das lokale Git-Repository initialisieren
////				//           Dann kann dort ggfs. auch etwas fehlendes nachgelesen werden.				
////				InitCommand gitCommandInit = Git.init();
////				gitCommandInit.setDirectory(objFileDirTotal);
////				
////				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
////				this.setGitObject(git);
////				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());
//				
////				bReturn = super.configureGit();
//				
//				//##############################################
//												
//				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
//				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH)
//				//Problem: Wenn hier dier GesamtRepositoryURL nur ausgelesen wird, dann passt das Protokol ggfs. nicht (https URL geht nicht beim ssh Weg.
//				//         Darum hier die remote Repository URL neu ausrechnen... String sRepositoryRemoteUrl = this.getRepositoryTotalRemote();	
//				String sRepositoryRemoteUrl = this.computeRepositoryRemoteUrl();
//				String sRepositoryRemoteBranch = this.getRepositoryBranch();//"master"; //TODOGOON20260515; Stelle das zur Vergügung this.getRepositoryRemoteBranch();
//				if(!StringZZZ.isEmpty(sRepositoryRemoteUrl)) {
//					String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
//					JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, sRepositoryRemoteBranch, true);
//				}
//				System.out.println("Remote Git-Repository init done and ensured alias und url exist in file .git/config. Alias: '" + sRepositoryRemoteAlias + "', Url: '" + sRepositoryRemoteUrl + "'");
//				
//				
//				bReturn = true;
//				//######################################
////			}catch(GitAPIException gae) {
////				ExceptionZZZ ez = new ExceptionZZZ(gae);
////				throw ez;
////			}
//		}//end main:
//		return bReturn;
//	}
	
//	/* (non-Javadoc)
//	 * @see use.jgit.IJgitStarter#configureRepositoryLocal(use.jgit.config.IConfigStarterJGIT)
//	 * 
//	 * 
//	 * Fehlerhaft wäre
//				
//					[remote "origin"]
//						url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
//						fetch = +refs/heads/*:refs/remotes/origin/*
//					[branch "master"]
//						remote = origin
//						merge = refs/heads/master
//	    Weil es remotes/origin nicht gibt.... vermutlich wurde hier fehlerhaft der Section-Alias als Branch verwendet.
//	    
//	    
//	   Minierklaerung:
//				siehe .git\config Datei, entsprechende Zeile.
//				 
//				Das ist ein sogenannter RefSpec (Reference Specification).
//				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
//				
//				Aufbau allgemein:
//				[+]<Quelle>:<Ziel>
//				
//				Also:
//				Quelle (Remote-Seite)
//				refs/heads/ = alle Branches im Remote-Repository
//				 * = Wildcard → alle Branch-Namen
//	
//				➡️ Bedeutet:
//				Hole alle Branches vom Remote
//				
//				
//				Ziel (lokal)
//				refs/remotes/origin/ = Remote-Tracking-Branches
//				* = gleicher Name wie Quelle
//	
//				➡️ Bedeutet:
//				Speichere sie lokal als origin/branchname
//				
//				------------
//				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
//				Mit + sagst du:
//				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
//			
//	 */
//	@Override
//	//public boolean configureRepositoryLocal(IConfigStarterJGIT objConfig) throws ExceptionZZZ{
//	public boolean configureRepositoryLocal(IConfigJGIT objConfig) throws ExceptionZZZ{
//		boolean bReturn = false;
//		main:{
//			if(objConfig==null) {
//				ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
//			
//			String sRepositoryRemoteAlias = objConfig.readRepositoryRemoteAlias();
//			boolean bRemoteAliasAvailable = !StringZZZ.isEmpty(sRepositoryRemoteAlias);
////			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
////				ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
////				throw ez;
////			}
//			this.setRepositoryRemoteAlias(sRepositoryRemoteAlias);
//			
//			
//			String sRepositoryLocal = objConfig.readRepositoryLocal();
//			if(StringZZZ.isEmpty(sRepositoryLocal)){
//				ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
//			this.setRepositoryLocalBase(sRepositoryLocal);
//			
//			
//			String sRepositoryProject = objConfig.readRepositoryProjectName();
//			if(StringZZZ.isEmpty(sRepositoryProject) & !bRemoteAliasAvailable){
//				ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
//			this.setRepositoryProject(sRepositoryProject);
//			
//			//Merke: Branch darf leer sein
//			String sRepositoryBranch = objConfig.readRepositoryBranch();
//			this.setRepositoryBranch(sRepositoryBranch);
//			
//			String sDirectoryRepositoryTotalLocal = FileEasyZZZ.joinFilePathName(sRepositoryLocal, sRepositoryProject);
//			File objDirectoryRepositoryLocalTotal = new File(sDirectoryRepositoryTotalLocal);
//			if(!objDirectoryRepositoryLocalTotal.exists()){
//				ExceptionZZZ ez = new ExceptionZZZ("Verzeichnis des Repositories existiert nicht '" + sDirectoryRepositoryTotalLocal + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
//			this.setRepositoryTotalLocal(sDirectoryRepositoryTotalLocal);
//			
//			String sRepositoryRemoteUrlByAlias = null; String sRepositoryRemoteFetchByAlias = null;
//			if(!bRemoteAliasAvailable) {
//				ExceptionZZZ ez = new ExceptionZZZ("Remote Alias nicht vorhanden '" + sRepositoryRemoteAlias + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}else {
//				//+++ Prüfe, ob https oder ssh in der .git\config Datei steht	
//				Repository repo = JgitUtilZZZ.getRepositoryObject(sDirectoryRepositoryTotalLocal, true);
//				sRepositoryRemoteUrlByAlias = repo.getConfig()
//						       .getString("remote",sRepositoryRemoteAlias,"url");
//				if(StringZZZ.isEmpty(sRepositoryRemoteUrlByAlias)){
//					ExceptionZZZ ez = new ExceptionZZZ("Keine Remote Repository URL bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;
//				}
//				
//				sRepositoryRemoteFetchByAlias = repo.getConfig()
//					       .getString("remote",sRepositoryRemoteAlias,"fetch");
//				if(StringZZZ.isEmpty(sRepositoryRemoteFetchByAlias)){
//					ExceptionZZZ ez = new ExceptionZZZ("Kein Remote Repository FETCH bei Verwendung des Alias '" + sRepositoryRemoteAlias, iERROR_PARAMETER_MISSING, AbstractJgitStarter.class, ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;
//				}
//				
//				
//				System.out.println("Git-Repository verwendet folgende Remote URL (gemaess Alias '"+ sRepositoryRemoteAlias + "'): '" + sRepositoryRemoteUrlByAlias +"'");
//				System.out.println("Git-Repository verwendet folgende Remote FETCH (gemaess Alias '"+ sRepositoryRemoteFetchByAlias + "'): '" + sRepositoryRemoteFetchByAlias +"'");
//				
//				this.setRepositoryTotalRemote(sRepositoryRemoteUrlByAlias);
//			}
//			bReturn = true;
//		}//end main:
//		return bReturn;
//	}
	
	
	@Override
	public abstract boolean commitPushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;

	@Override
	public abstract boolean commitPushit(IConfigStarterRemoteJGIT objConfig, String sComment) throws ExceptionZZZ;

	
	@Override
	public abstract boolean pullit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;

	
	
	
	//############# STATIC METHODEN

	//############# FLAG HANDLIG
	//... in abstrakter Elternklasse
}
