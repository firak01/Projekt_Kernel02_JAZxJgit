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
import org.eclipse.jgit.api.errors.CheckoutConflictException;
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
import use.jgit.IJgitStarterEnabledZZZ.FLAGZLOCAL;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.start.protocol.ssh.JGitSshConfigZZZ;
import use.jgit.start.protocol.ssh.JgitStarterSSH;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.util.JgitUtilHTTPS;
import use.jgit.util.JgitUtilSSH;
import use.jgit.util.JgitUtilZZZ;

public abstract class AbstractJgitStarterRemote<T> extends AbstractJgitStarterAuthentificated<T> implements IJgitStarterRemote{
	private static final long serialVersionUID = -1998325674945232389L;
	

	//### aus IJgitStarterLocal
	@Override
	public boolean configureGit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{		
			//bReturn = super.configureGit((IConfigStarterLocalJGIT) objConfig);
			bReturn = super.configureGit((IConfigWithAuthentificationJGIT) objConfig);
			
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
			String sConnectionType = sConnectionTypeIn.toLowerCase();
			
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
			if(sConnectionType.equalsIgnoreCase("https")) {
				System.out.println("Bei HTTPS wird keine SSH Session Factory benötigt");
			}else {
				JGitSshConfigZZZ.configure();
				System.out.println("Verwendete Ssh Session Factory: " + SshSessionFactory.getInstance().getClass());
			}
			
			
			//######################################
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	//### aus IJgitStarter	

	
	
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
	
	@Override
	public abstract boolean commitPushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;

	@Override
	public abstract boolean commitPushit(IConfigStarterRemoteJGIT objConfig, String sComment) throws ExceptionZZZ;

	
	@Override
	public abstract boolean pullit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ, TransportException, CheckoutConflictException;

	
	
	
	//############# STATIC METHODEN

	//############# FLAG HANDLIG
	//... in abstrakter Elternklasse
}
