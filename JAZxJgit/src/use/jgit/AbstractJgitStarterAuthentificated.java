package use.jgit;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.config.IConfigZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.start.protocol.ssh.JGitSshConfigZZZ;
import use.jgit.start.protocol.ssh.JgitStarterSSH;
import use.jgit.util.JgitUtilGIT;
import use.jgit.util.JgitUtilHTTPS;
import use.jgit.util.JgitUtilSSH;
import use.jgit.util.JgitUtilZZZ;

public abstract class AbstractJgitStarterAuthentificated <T> extends AbstractJgitStarterLocal<T> implements IJgitStarterAuthentificated, IJgitStarterAuthentificatedEnabledZZZ{
	//++++++++++++++++++++++++
	//EIGENTLICH NUR FÜR HTTPS, wie Kapseln oder für jedes Protokol eine eigene abstrakte Klasse???
	protected volatile CredentialsProvider credentialsProviderObject = null;	
	
	//Zugang per ACCESS TOKEN ( PAT ) in github: Account, ganz unten im Navigator "Developer Settings"
	protected String sPAT = ""; //Merke: GitHub verweigert das PUSHEN eines PAT-Werts durch sein Regelwerk, hier kann also keine statische Variable final definiert sein!!!
	//+++++++++++++++++++++++	
	
	protected volatile String sConnectionType=null;
	
	protected volatile String sRepositoryRemoteHost=null;
	protected volatile String sRepositoryRemoteAccount=null;
	protected volatile String sRepositoryBaseRemote=null; //Basis URL	
	
	
	//############ GETTER  / SETTER	

	@Override 
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ{
		return this.credentialsProviderObject;
	}
	
	@Override
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ{
		this.credentialsProviderObject = objCredentialsProvider;
	}
	
	@Override
	public void setPersonalAccessToken(String sPat) throws ExceptionZZZ {
		this.sPAT = sPat;
	}

	@Override
	public String getPersonalAccessToken() throws ExceptionZZZ {		
		if(StringZZZ.isEmpty(this.sPAT)) {
			IConfigWithAuthentificationJGIT objConfig = (IConfigWithAuthentificationJGIT) this.getConfiguration();			
			return objConfig.readPersonalAccessToken();
		}else {
			return this.sPAT;
		}
	}
	
	//############# METHODEN
	@Override
	public String getRepositoryRemoteAccount() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryRemoteAccount)) {
			IConfigWithAuthentificationJGIT objConfig = (IConfigWithAuthentificationJGIT) this.getConfiguration();			
			String sReturn = objConfig.readRepositoryRemoteAccount();
			if(StringZZZ.isEmpty(sReturn)) {
				String sUrlRepo = this.searchRepositoryRemote();
				sReturn = JgitUtilZZZ.computeRepositoryAccountFromUrlRepo(sUrlRepo);
			}
			this.sRepositoryRemoteAccount = sReturn;		
		}
		return this.sRepositoryRemoteAccount;

	}
	
	@Override
	public void setRepositoryRemoteAccount(String sRepositoryRemoteAccount) throws ExceptionZZZ {
		this.sRepositoryRemoteAccount = sRepositoryRemoteAccount;
	}
	
	@Override
	public String getConnectionType() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sConnectionType)) {
			IConfigWithAuthentificationJGIT objConfig = (IConfigWithAuthentificationJGIT) this.getConfiguration();			
			this.sConnectionType = objConfig.readConnectionType();			
		}
		return this.sConnectionType;		
	}

	@Override
	public void setConnectionType(String sConnectionType) throws ExceptionZZZ {
		this.sConnectionType = sConnectionType;
	}
	
	@Override
	public String getRepositoryRemoteHost() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryRemoteHost)) {
			IConfigWithAuthentificationJGIT objConfig = (IConfigWithAuthentificationJGIT) this.getConfiguration();			
			String sReturn = objConfig.readRepositoryRemoteHost();
			if(StringZZZ.isEmpty(sReturn)) {
				String sUrlRepo = this.searchRepositoryRemote();
				sReturn = JgitUtilZZZ.computeRepositoryHostFromUrlRepo(sUrlRepo);				
			}
			this.sRepositoryRemoteHost = sReturn;
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
					this.sRepositoryBaseRemote = JgitUtilHTTPS.computeRepositoryUrlBaseFromUrlHTTPS(sRepositoryTotalRemote);
				}else if(JgitUtilZZZ.isUrlSSH(sRepositoryTotalRemote)){
					this.sRepositoryBaseRemote = JgitUtilSSH.computeRepositoryUrlBaseFromUrlSSH(sRepositoryTotalRemote);
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
		String sProtocolIn = this.getConnectionType();
		String sRepositoryRemoteHostIn = this.getRepositoryRemoteHost();
		String sRepositoryRemoteAccountIn = this.getRepositoryRemoteAccount();
		String sRepositoryProjectIn = this.getRepositoryProject();
		return this.computeRepositoryRemoteUrl(sProtocolIn, sRepositoryRemoteHostIn, sRepositoryRemoteAccountIn, sRepositoryProjectIn);
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
			sReturn = JgitUtilZZZ.computeRepositoryUrlTotalFor(sProtocol, sRepositoryRemoteHost, sRepositoryRemoteAccount, sRepositoryProject);			
			
		}//end main:
		return sReturn;
	}
	
	@Override
	public String computeRepositoryRemoteUrl(String sProtocolIn, String sRepositoryRemoteHostIn, String sRepositoryRemoteAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sProtocol;
			if(StringZZZ.isEmpty(sProtocolIn)) {
				sProtocol = this.getConnectionType();
				if(StringZZZ.isEmpty(sProtocol)) {
					ExceptionZZZ ez = new ExceptionZZZ("Protocol / ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;	
				}
			}else {
				sProtocol = sProtocolIn;
			}
			
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
			//String sRepositoryBaseRemote = this.computeRepositoryBaseRemote(sRepositoryRemoteHost, sRepositoryRemoteAccount);
			String sRepositoryBaseRemote = null;
			if(sProtocol.equalsIgnoreCase("git")) {
				sRepositoryBaseRemote = JgitUtilGIT.computeRepositoryUrlBaseGIT(sRepositoryRemoteHost, sRepositoryRemoteAccount);
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sRepositoryBaseRemote = JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sRepositoryRemoteHost, sRepositoryRemoteAccount);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("computeRepositoryUrlBase. Noch nicht behandeltes Protokol: '" + sProtocol + "'", iERROR_PARAMETER_VALUE, AbstractJgitStarterAuthentificated.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
						
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
	
		
	//Das hängt vom jeweiligen Protokol der Klasse ab, HTTPS / GIT und wird dort definiert
	@Override
	public abstract String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ;

	@Override
	public abstract String getRepositoryRemoteProtocol() throws ExceptionZZZ;
	
	
	//###################################################################
	//#######################################
	@Override
	public boolean configureRepositoryLocal(IConfigWithAuthentificationJGIT objConfig) throws ExceptionZZZ {
		return super.configureRepositoryLocal(objConfig);
	}

	@Override
	public boolean configureGitCustom(IConfigWithAuthentificationJGIT objConfig) throws ExceptionZZZ {
		return super.configureGitCustom(objConfig);
	}
	
	@Override
	public boolean configureGit(IConfigWithAuthentificationJGIT objConfig) throws ExceptionZZZ{
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
			
			//wird separat gemacht... bReturn = this.createGit();
			
			
			//###################
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
			this.setRepositoryRemoteAlias(sRepositoryRemoteAlias);
			
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
//				//         Darum hier die remote Repository URL neu ausrechnen... 
			//  Merke: Für HTTPS ist hier auch ein URL ohne SPAT erlaubt: url = https://github.com/firak01/1fgl_Test_repo_readwrite.git
			//                                                            url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
			//Wenn es leer ist, wird dann auch (indirekt) neu berechnet
			//String sRepositoryRemoteUrl = this.getRepositoryTotalRemote();
			//Aber transparenter ist das direkte Berechnen
			String sRepositoryRemoteUrl = this.computeRepositoryRemoteUrl();
			if(StringZZZ.isEmpty(sRepositoryRemoteUrl)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote URL nicht berechnet für lokales Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteBranch = this.getRepositoryBranch();
			if(StringZZZ.isEmpty(sRepositoryRemoteBranch)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Branch nicht vorhanden für lokales Verzeichnis '" + sDirectoryRepositoryTotalLocal + "'" , iERROR_PARAMETER_MISSING, JgitStarterSSH.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryBranch(sRepositoryRemoteBranch);
			
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
					ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und remote Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
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
			//### IDEE: Das in eigener Methode pro Klasse machen lassen, da z.B. der Credentialprovider nur bei HTTPS benötigt wird
			//         .configureGitAuthentification(...)
			
			
			//0) SshSessionFactory ... mit den verwendeten Ids, Pfaden, etc.
			//Das müsste eigentlich für HTTPS nicht gemacht werden.
			String sConnectionType = sConnectionTypeIn.toLowerCase();			
			if(sConnectionType.equalsIgnoreCase("https")) {
				System.out.println("Bei HTTPS wird keine SSH Session Factory benötigt");
				
				//+++ HTTPS Zugriff sicherstellen
				CredentialsProvider credentialsProvider = JgitUtilHTTPS.createCredentialsProviderByToken(this.getPersonalAccessToken());
				System.out.println("Git Credentials Provider created done.");
				this.setCredentialsProviderObject(credentialsProvider);
			}else {
				JGitSshConfigZZZ.configure();
				System.out.println("Verwendete Ssh Session Factory: " + SshSessionFactory.getInstance().getClass());
			}			
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	//############# STATIC METHODEN

	//###############################################
	//### FLAG HANDLING
	//###############################################
	
	//### aus IJgitEnabledZZZ
	@Override
	public boolean getFlag(IJgitStarterAuthentificatedEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.getFlag(objEnumFlag.name());
	}

	@Override
	public boolean setFlag(IJgitStarterAuthentificatedEnabledZZZ.FLAGZ objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlag(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlag(IJgitStarterAuthentificatedEnabledZZZ.FLAGZ[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitStarterAuthentificatedEnabledZZZ.FLAGZ objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagExists(IJgitStarterAuthentificatedEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagSetBefore(IJgitStarterAuthentificatedEnabledZZZ.FLAGZ objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}
	
	//###################################
	//### FLAGLOCAL Handling

	//### aus JgitEnabledZZZ	
	@Override
	public boolean getFlagLocal(IJgitStarterAuthentificatedEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.getFlagLocal(objEnumFlag.name());
	}

	@Override
	public boolean setFlagLocal(IJgitStarterAuthentificatedEnabledZZZ.FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagLocal(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagLocal(IJgitStarterAuthentificatedEnabledZZZ.FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitStarterAuthentificatedEnabledZZZ.FLAGZLOCAL objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlagLocal(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagLocalExists(IJgitStarterAuthentificatedEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagLocalExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagLocalSetBefore(IJgitStarterAuthentificatedEnabledZZZ.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}

	


	//###################################
	//### FLAG CUSTOM Handling
		
	@Override
	public boolean getFlagCustom(IJgitStarterAuthentificatedEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.getFlagCustom(objEnumFlag.name());
	}

	@Override
	public boolean setFlagCustom(IJgitStarterAuthentificatedEnabledZZZ.FLAGZCUSTOM objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagCustom(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagCustom(IJgitStarterAuthentificatedEnabledZZZ.FLAGZCUSTOM[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitStarterAuthentificatedEnabledZZZ.FLAGZCUSTOM objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlag(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagCustomExists(IJgitStarterAuthentificatedEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagCustomSetBefore(IJgitStarterAuthentificatedEnabledZZZ.FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagCustomSetBefore(objEnumFlag.name());
	}
}
