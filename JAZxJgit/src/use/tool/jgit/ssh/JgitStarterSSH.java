package use.tool.jgit.ssh;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.machine.EnvironmentZZZ;
import use.tool.jgit.IConfigJGIT;
import use.tool.jgit.IJgitEnabledZZZ;
import use.tool.jgit.AbstractJgitStarter;
import use.tool.jgit.JgitStarterMain;
import use.tool.jgit.JgitUtil;
import use.tool.jgit.JgitUtilHTTPS;
import use.tool.jgit.JgitUtilSSH;
import use.tool.jgit.https.JgitStarterHTTPS;



public class JgitStarterSSH<T> extends AbstractJgitStarter<T> implements IJgitStarterSSH{
	private static final long serialVersionUID = 521157607363069534L;

	//### aus IJgitStarter
	@Override
	public String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ{
		return JgitUtilSSH.computeRepositoryUrlBaseSSH(sHost, sAccount);
	}
	
	@Override
	public String getRepositoryTotalRemote() throws ExceptionZZZ {		
		if( this.sRepositoryTotalRemote==null) {
			String sHost = this.getRepositoryRemoteHost();
			String sAccount = this.getRepositoryRemoteAccount();						
			String sRepositoryProjectRemote = this.getRepositoryProject();	
			if(StringZZZ.isEmpty(sHost) || StringZZZ.isEmpty(sAccount) || StringZZZ.isEmpty(sRepositoryProjectRemote)) return null;
			this.sRepositoryTotalRemote = JgitUtilSSH.computeRepositoryUrlSSH(sHost, sAccount, sRepositoryProjectRemote);			
		}
		return this.sRepositoryTotalRemote;
	}
	
	@Override
	public boolean configureGit() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		
			//Konfiguriere JGit für SSH
			
			//+++ Zugriff sicherstellen
			//0) SshSessionFactory ... mit den verwendeten Ids, Pfaden, etc.
			JGitSshConfigZZZ.configure();
			System.out.println("Verwendete SSH Session Factory: " + SshSessionFactory.getInstance().getClass());
				
			
			//B) Konfiguriere das lokale Repository und init Git-Object (nach demm Remote Repository, da die Daten des Remote Repository ggfs. in das Lokale Repository uebernommen werden)
			//a) + b)
			bReturn = super.configureGit();

			
			//Die Remote Repository Einstellungen in der Jeweiligen Klasse des Protokolls machen
			//A) Remote (zuerst, weil die Einstellungen in die Konfiguration des Lokalen Repositories uebenommen werden.
			//a) Remote Basis Url
			String sDirectoryRepositoryRemote = this.getRepositoryBaseRemote();
			if(StringZZZ.isEmpty(sDirectoryRepositoryRemote)) {
				//ExceptionZZZ ez = new ExceptionZZZ("Remote Repository Basis URL, Angabe fehlt: '" + sDirectoryRepositoryRemote + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				//throw ez;
				
				//Versuch dies über den Alias zu ermitteln
				String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
				if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				String sUrlSSHorHTTPS = this.searchRepositoryRemote(sRepositoryRemoteAlias);
				sDirectoryRepositoryRemote = JgitUtilSSH.computeRepositoryUrlPartFromUrlSSH(sUrlSSHorHTTPS);
			}
			if(StringZZZ.isEmpty(sDirectoryRepositoryRemote)) {
				ExceptionZZZ ez = new ExceptionZZZ("Weder Url direkt angegeben noch per Alias '" + sRepositoryRemoteAlias + "' ermittelbar.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryBaseRemote(sDirectoryRepositoryRemote);	
			
			//b) Remote Repository-Verzeichnis des Projekts
			String sRepositoryProjectRemote = this.getRepositoryProject(); //momentan identisch mit lokal)
			if(StringZZZ.isEmpty(sRepositoryProjectRemote)) {
				ExceptionZZZ ez = new ExceptionZZZ("Projektname der remote Repositories, Angabe fehlt: '" + sRepositoryProjectRemote + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			//Das ist umso wichtiger, weil mit HTTPS Url wird ein Credentials Provider erwartet.
			//Den gibt es für SSH aber nicht... 
			//Darum muss die URL zum verwendeten Protokol stimmen.
			String sRepositoryBaseRemote = null;
			if(JgitUtil.isUrlHTTPS(sDirectoryRepositoryRemote)) {
				String sAccount = JgitUtilHTTPS.getAccountFromUrl(sDirectoryRepositoryRemote);
				String sHost = JgitUtilHTTPS.getHostFromUrl(sDirectoryRepositoryRemote);	
				sRepositoryBaseRemote = JgitUtilSSH.computeRepositoryUrlBaseSSH(sHost, sAccount);				
			}else {
				sRepositoryBaseRemote = sDirectoryRepositoryRemote;
			}
			this.setRepositoryBaseRemote(sRepositoryBaseRemote);
			
			String sRepositoryTotalRemote = JgitUtilSSH.computeRepositoryUrlSSH(sRepositoryBaseRemote, sRepositoryProjectRemote);
			this.setRepositoryTotalRemote(sRepositoryTotalRemote);
				
			
			//+++ SSH Zugriff sicherstellen
			//Merke: Es gibt keinen Credentials Provider für SSH.
			//Bei SSH muss man sich auf die korrekte ssh URL verlassen
			//Übergibt man eine HTTPS URL kommt die Fehlermeldung:
			//basic.zBasic.ExceptionZZZ: org.eclipse.jgit.api.errors.TransportException: https://github.com/firak01/Projekt_Kernel02_JAZDummy.git: Authentication is required but no CredentialsProvider has been registered

		}//end main:
		return bReturn;
	}
	
	@Override 
	public boolean pullit(IConfigJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
							
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen
						
				boolean bLocalRepositoryConfigured = this.configureRepositoryLocal(objConfig);
				if(bLocalRepositoryConfigured) {
					System.out.println("Lokales Repository erfolgreich konfiguriert");
				}else {
					System.out.println("Lokales Repository NICHT erfolgreich konfiguriert");
					//Wenn das so nicht geklappt hat, dann wurden die Details ggfs. einzeln übergeben... wir werden sehen.
				}
				
				
				//######################################################################################
				//+++ Folgende Konfiguration könnten aus dem Alias und dem Repository geholt werden
				String sConnectionTypeIn = objConfig.readConnectionType();
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
					if(bLocalRepositoryConfigured) {
						//Diese Detail aus der .git\config Datei unter dem Alias auslesen.
						String sDirectoryRepositoryLocalRemote = this.getRepositoryTotalRemote();
						if(StringZZZ.isEmpty(sDirectoryRepositoryLocalRemote)) {
							ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
						
						sConnectionTypeIn = JgitUtil.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);
					}else {
						ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
				}
				
				String sRepositoryRemoteHostIn = objConfig.readRepositoryRemoteHost();
				if(StringZZZ.isEmpty(sRepositoryRemoteHostIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote Host und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
								
				String sRepositoryRemoteAccountIn = objConfig.readRepositoryRemoteAccount();
				if(sConnectionTypeIn.equalsIgnoreCase("https")) {
					ExceptionZZZ ez = new ExceptionZZZ("Kein Account für ConnectionType '"+sConnectionType+"'", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
								
				//+++++++++++++++++++++++
								
				this.setConnectionType(sConnectionTypeIn);
				this.setRepositoryRemoteHost(sRepositoryRemoteHostIn);
				this.setRepositoryRemoteAccount(sRepositoryRemoteAccountIn);
								
				String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote SSH Repository und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setRepositoryBaseRemote(sRepositoryRemoteIn);
				
				//######################################################
				//Konfiguriere JGit für SSH				
				boolean bSuccess = this.configureGit();
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++
				//Mache den pull	
				Git git = this.getGitObject();
		        boolean bSuccessPull = this.pullit(git);
		        if(bSuccessPull) {
					System.out.println("pull erfolgreich");
				}else {
					System.out.println("pull NICHT erfolgreich");
					break main;
				}
		        git.close();
		        bReturn = true;
		        //#######################################################	  
			
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;			
			}
		}//end main:
		return bReturn;
	}
	
//	@Override
//	public boolean pullit(Git git) throws ExceptionZZZ {
//		boolean bReturn = false;
//		main:{
//			try {
//				//wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
//				System.out.println("Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
//				
//				// aber mal explizit als pullCommand
//				PullCommand pullCommand = git.pull();
//				
//				String sRemoteRepositoryAlias = this.getRepositoryRemoteAlias();
//				pullCommand.setRemote(sRemoteRepositoryAlias);
//		
//				
//				// pull from remote, hier mit Auswertung des Ergebnisses	
//				PullResult pullResult = pullCommand.call();
//				
//				
//				if (pullResult.isSuccessful()) {
//				    System.out.println("Pull erfolgreich");
//				    bReturn = true;
//				} else {
//				    System.out.println("Pull fehlgeschlagen");
//				    bReturn = false;
//				}
//
//				MergeResult mergeResult = pullResult.getMergeResult();
//				if(mergeResult!=null) {
//					System.out.println("MergeResult: " + mergeResult.getMergeStatus());
//				}else {
//					System.out.println("MergeResult: Kein Status zurueckgegeben.");
//				}
//				
//				FetchResult fetchResult = pullResult.getFetchResult();
//				if(fetchResult!=null) {
//					System.out.println("FetchResult: " + fetchResult.getMessages());
//				}else {
//					System.out.println("FetchResult: Keine Meldung zurueckgegeben.");
//				}
//								
//				
//				//###############################################################
//			}catch(InvalidRemoteException ire) {
//				ExceptionZZZ ez = new ExceptionZZZ(ire);
//				throw ez;
//			}catch(TransportException te) {
//				ExceptionZZZ ez = new ExceptionZZZ(te);
//				throw ez;
//			}catch(GitAPIException gae) {
//				ExceptionZZZ ez = new ExceptionZZZ(gae);
//				throw ez;
//			}
//		}//end main:
//		return bReturn;
//	}
	
	@Override
	public boolean pullit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			CredentialsProvider credentialsProvider = this.getCredentialsProviderObject();			
			String sRepositoryRemoteTotal = this.getRepositoryTotalRemote();		
			boolean bIgnoreConflicts = this.getFlag(IJgitEnabledZZZ.FLAGZ.MERGE_IGNORE_CHECKOUT_CONFLICTS);	
			if(bIgnoreConflicts) {
				//bReturn = this.pullitIgnoreCheckoutConflicts(git, credentialsProvider, sPAT, sRepositoryRemote);
				
				TODOGOON20260418;//Nicht nur einfach komplett ignorieren, sondern per Strategie auflösen
				                 //1) hier THEIRS oder OURS übergeben als Strategie
				
				                
				String sBranch = "master";
				bReturn = this.pullitIgnoreCheckoutConflicts(git, credentialsProvider, sRepositoryRemoteTotal, sBranch);
				
								//2) es muss aber wie beim HTTPS Weg eine Methode geben, 
				                //   in der erst versucht wird zu und danach 
				                //   nur Konflikte per THEIRS oder OURS aufgelöst werden.
			}else {
				bReturn = this.pullit(git, credentialsProvider, sRepositoryRemoteTotal);
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			bReturn = JgitUtilSSH.pullSSH(git, credentialsProvider, sRepoRemote);				
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			bReturn = JgitUtilSSH.pullIgnoreCheckoutConflictsSSH(git);
		}//end main:
		return bReturn;
	}

	//##############################
	//###### PUSH #################
	@Override
	public boolean pushit(IConfigJGIT objConfig) throws ExceptionZZZ {	
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
							
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen						
				boolean bLocalRepositoryConfigured = this.configureRepositoryLocal(objConfig);
				if(bLocalRepositoryConfigured) {
					System.out.println("Lokales Repository erfolgreich konfiguriert");
				}else {
					System.out.println("Lokales Repository NICHT erfolgreich konfiguriert");
					//Wenn das so nicht geklappt hat, dann wurden die Details ggfs. einzeln übergeben... wir werden sehen.
				}
								
				//######################################################################################
				//+++ Folgende Konfiguration könnten aus dem Alias und dem Repository geholt werden
				String sConnectionTypeIn = objConfig.readConnectionType();
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
					if(bLocalRepositoryConfigured) {
						//Diese Detail aus der .git\config Datei unter dem Alias auslesen.
						String sDirectoryRepositoryLocalRemote = this.getRepositoryTotalRemote();
						if(StringZZZ.isEmpty(sDirectoryRepositoryLocalRemote)) {
							ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
						
						sConnectionTypeIn = JgitUtil.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);
					}else {
						ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
				}
				
			
				String sRepositoryRemoteHost = objConfig.readRepositoryRemoteHost();
				if(StringZZZ.isEmpty(sRepositoryRemoteHost)){
					ExceptionZZZ ez = new ExceptionZZZ("Hostname des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sRepositoryRemoteAccount = objConfig.readRepositoryRemoteAccount();
				if(StringZZZ.isEmpty(sRepositoryRemoteAccount)){
					ExceptionZZZ ez = new ExceptionZZZ("Account des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
	
				
				//+++++++++++++++++++++++								
				this.setConnectionType(sConnectionTypeIn);
				this.setRepositoryRemoteHost(sRepositoryRemoteHost);
				this.setRepositoryRemoteAccount(sRepositoryRemoteAccount);
								
				
				String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote SSH Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setRepositoryBaseRemote(sRepositoryRemoteIn);
				
				//######################################################
				//Konfiguriere JGit für SSH
				boolean bSuccessConfigureGit = this.configureGit();
				if(bSuccessConfigureGit) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
							
				//+++++++++++++++++++++++++++++++
				//Finde geaenderte und neue Dateien fuer den commit
				boolean bSuccessCommit = this.commitit();
				if(bSuccessCommit) {
					System.out.println("commit erfolgreich");
				}else {
					System.out.println("commit NICHT erfolgreich");
					break main;
				}
 
				//+++++++++++++++++++++++++++++++++++
		        //Führe den Push durch
		        Git git = this.getGitObject();
		        
		        //a) Zugriff sicherstellen
		        //   Das passiert durch die lokalen ssh-id Dateien
		        
		        //b) Mache den push	
		        bReturn = this.pushit(git);
		        if(bReturn) {
		        	System.out.println("STATUS AFTER PUSH: SUCCESSFULL");
		        	this.printStatus(git);
		        }else {
		        	System.out.println("STATUS AFTER PUSH: FAILED");
		        	this.printStatus(git);
		        }
		       
		        
		        //s. ChatGPT vom 20260313
		        //Problem: Eclipse "registriert/bemerkt" den Push nicht (also Pfeil nach oben mit 1 dahinter wird angezeigt).
		        //Damit in Eclipse auch der Push "registriert/bemerkt wird" muss noch ein Fetch gemacht werden.
		        //Der letzte fetch() sorgt dafür, dass lokale Remote-Tracking-Branches synchron bleiben, 
		        //was besonders hilfreich ist, wenn gleichzeitig ein Tool wie Eclipse auf das gleiche Repository schaut.
		        	        
		        //aber manchmal ist nichts zu fetchen, darum Fehler abfangen 
		        String sDirectoryRepositoryLocalTotal = this.getRepositoryTotalLocal();
		        File objFileDir = new File(sDirectoryRepositoryLocalTotal);
		        
		        String sRepositoryRemote = this.getRepositoryTotalRemote();
		        JgitStarterSSH.fetchIgnoreNothingToFetch(objFileDir, sRepositoryRemote);
			    System.out.println(("FETCH DONE"));
			  	
			    git.close();
			    bReturn = true;
	        //###############################################################	  
			}catch(TransportException tex) {
				ExceptionZZZ ez = new ExceptionZZZ(tex);
				throw ez;	
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	
	
		
	
	@Override
	public boolean pushit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				//wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
				
				// aber mal explizit als pushCommand
				PushCommand pushCommand = git.push();
				
				String sRemoteRepositoryAlias = this.getRepositoryRemoteAlias();
				pushCommand.setRemote(sRemoteRepositoryAlias);
		
				// push to remote:	
				pushCommand.call();
				
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

	
}
