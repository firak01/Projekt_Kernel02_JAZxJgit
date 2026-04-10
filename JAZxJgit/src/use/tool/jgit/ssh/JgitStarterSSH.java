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
import use.tool.jgit.IConfigJGIT;
import use.tool.jgit.AbstractJgitStarter;
import use.tool.jgit.JgitStarterMain;
import use.tool.jgit.JgitUtil;
import use.tool.jgit.JgitUtilHTTPS;
import use.tool.jgit.JgitUtilSSH;
import use.tool.jgit.https.JgitStarterHTTPS;



public class JgitStarterSSH extends AbstractJgitStarter implements IJgitStarterSSH{
	
	//### aus IJgitStarter
	@Override
	public boolean configureGit() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		
			//Konfiguriere JGit für SSH
			
			//+++ Zugriff sicherstellen
			//0) SshSessionFactory ... mit den verwendeten Ids, Pfaden, etc.
			JGitSshConfigZZZ.configure();
			System.out.println("Verwendete SSH Session Factory: " + SshSessionFactory.getInstance().getClass());
				
			
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
			
			String sRepositoryTotalRemoteSSH = JgitUtilSSH.computeRepositoryUrlSSH(sRepositoryBaseRemote, sRepositoryProjectRemote);
			this.setRepositoryTotalRemote(sRepositoryTotalRemoteSSH);
				
			//B) Konfiguriere das lokale Repository und init Git-Object (nach demm Remote Repository, da die Daten des Remote Repository ggfs. in das Lokale Repository uebernommen werden)
			//a) + b)
			bReturn = super.configureGit();

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
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen
				String sRepositoryRemoteAliasIn = objConfig.readRepositoryRemoteAlias();
	//			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
	//				ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
	//				throw ez;
	//			}
				
				String sRepositoryRemoteIn = objConfig.readRepositoryRemoteBaseSSH();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn) && StringZZZ.isEmpty(sRepositoryRemoteAliasIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote SSH Repository und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				String sRepositoryLocalIn = objConfig.readRepositoryLocal();
				if(StringZZZ.isEmpty(sRepositoryLocalIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sRepositoryProjectIn = objConfig.readRepositoryProjectName();
				if(StringZZZ.isEmpty(sRepositoryProjectIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
				//+++++++++++++++++++++++
				this.setRepositoryBaseLocal(sRepositoryLocalIn);
				this.setRepositoryBaseRemote(sRepositoryRemoteIn);
				this.setRepositoryProject(sRepositoryProjectIn);
				this.setRepositoryRemoteAlias(sRepositoryRemoteAliasIn);					
				//#####################################################################
				
				//+++++++++++++++++++++++++++++++++++++++++++++++++
				//Konfiguriere JGit für SSH				
				boolean bSuccess = this.configureGit();
				if(bSuccess) {
					System.out.println("Git erfolgrech konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				//Mache den pull	
				Git git = this.getGitObject();
		        this.pullit(git);
		        git.close();
		        bReturn = true;
	        //###############################################################	  
			
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
			try {
				//wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
				
				// aber mal explizit als pullCommand
				PullCommand pullCommand = git.pull();
				
				String sRemoteRepositoryAlias = this.getRepositoryRemoteAlias();
				System.out.println("Verwendete RepositoryAlias für Remote: " + sRemoteRepositoryAlias);
				pullCommand.setRemote(sRemoteRepositoryAlias);

				
				
				// pull from remote, hier mit Auswertung des Ergebnisses	
				PullResult pullResult = pullCommand.call();
				
				
				if (pullResult.isSuccessful()) {
				    System.out.println("Pull erfolgreich");
				    bReturn = true;
				} else {
				    System.out.println("Pull fehlgeschlagen");
				    bReturn = false;
				}

				MergeResult mergeResult = pullResult.getMergeResult();
				if(mergeResult!=null) {
					System.out.println("MergeResult: " + mergeResult.getMergeStatus());
				}else {
					System.out.println("MergeResult: Kein Status zurueckgegeben.");
				}
				
				FetchResult fetchResult = pullResult.getFetchResult();
				if(fetchResult!=null) {
					System.out.println("FetchResult: " + fetchResult.getMessages());
				}else {
					System.out.println("FetchResult: Keine Meldung zurueckgegeben.");
				}
								
				
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
				String sRepositoryRemoteAliasIn = objConfig.readRepositoryRemoteAlias();
	//			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
	//				ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
	//				throw ez;
	//			}
				
				String sRepositoryRemoteIn = objConfig.readRepositoryRemoteBaseSSH();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn) && StringZZZ.isEmpty(sRepositoryRemoteAliasIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote SSH Repository und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				String sRepositoryLocalIn = objConfig.readRepositoryLocal();
				if(StringZZZ.isEmpty(sRepositoryLocalIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sRepositoryProjectIn = objConfig.readRepositoryProjectName();
				if(StringZZZ.isEmpty(sRepositoryProjectIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Projektname der Repositories", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++++++++++++++++++++++
				this.setRepositoryBaseLocal(sRepositoryLocalIn);
				this.setRepositoryBaseRemote(sRepositoryRemoteIn);
				this.setRepositoryProject(sRepositoryProjectIn);
				this.setRepositoryRemoteAlias(sRepositoryRemoteAliasIn);					
				//#####################################################################
				
				//+++++++++++++++++++++++++++++++++++++++++++++++++
				//Konfiguriere JGit für SSH
				boolean bSuccess = this.configureGit();
				if(bSuccess) {
					System.out.println("Git erfolgrech konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
								
				//+++ SSL Zugriff sicherstellen
				//Merke: Das Vorhandensein der notwendigen Dateien in .ssh wird vorausgesetzt
				//
				//+++++++++++++++++++++++++++++++++		
				//Finde geaenderte und neue Dateien fuer den Push
				Git git = this.getGitObject();				
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
		
				TODOGOON20260410;//Hier den Namen des Rechners einfügen
				CommitCommand gitCommandCommit = git.commit();
				gitCommandCommit.setMessage(sDateFormated + " - Commit by Java-Class from a module of Projekt_Tool_DevEditor");
				gitCommandCommit.call();
		        
		        System.out.println("STATUS AFTER COMMIT");
		        this.printStatus(git);
				 
		        //Mache den push	
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
		        JgitStarterHTTPS.fetchIgnoreNothingToFetch(objFileDir, sRepositoryRemote);
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
	
	
	
	public void addFileTrackedChanged(Git git) throws NoWorkTreeException, GitAPIException {
		
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
        	}
        }
       
	}
	
	public void addFileUntracked(Git git) throws NoWorkTreeException, GitAPIException {
		Status status = git.status().call();

		Set<String> setUntracked = status.getUntracked();
		ArrayList<String> listasUntracked = new ArrayList<String>();
        for (String  sUntracked : setUntracked ) {
        	listasUntracked.add(sUntracked);
        }
        
        for(String sUntracked : listasUntracked) {
        	git.add().addFilepattern(sUntracked).call();
        }
	
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
}
