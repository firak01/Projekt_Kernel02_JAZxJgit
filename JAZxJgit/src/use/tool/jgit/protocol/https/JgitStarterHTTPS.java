package use.tool.jgit.protocol.https;

import java.io.File;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import use.tool.jgit.AbstractJgitStarter;
import use.tool.jgit.IConfigJGIT;
import use.tool.jgit.IJgitEnabledZZZ;
import use.tool.jgit.JgitStarterMain;
import use.tool.jgit.JgitUtil;
import use.tool.jgit.JgitUtilHTTPS;
import use.tool.jgit.JgitUtilSSH;
import use.tool.jgit.merge.GitPostMergeAnalyse;
import use.tool.jgit.merge.ResultPostMergeAnalysis;



public class JgitStarterHTTPS<T> extends AbstractJgitStarter<T> implements IJgitStarterHTTPS{
	private static final long serialVersionUID = -3594348507412511385L;
	//Zugang per ACCESS TOKEN ( PAT ) in github: Account, ganz unten im Navigator "Developer Settings"
	public String sPAT = ""; //Merke: GitHub verweigert das PUSHEN eines PAT-Werts durch sein Regelwerk, hier kann also keine statische Variable final definiert sein!!!
	
	//### aus IJgitStarterHTTPS
	@Override
	public void setPersonalAccessToken(String sPat) throws ExceptionZZZ {
		this.sPAT = sPat;
	}

	@Override
	public String getPersonalAccessToken() throws ExceptionZZZ {
		return this.sPAT;
	}
	
	//### aus IJgitStarter
	@Override
	public String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ {
		return JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sHost, sAccount);
	}
	
	@Override
	public String getRepositoryTotalRemote() throws ExceptionZZZ {		
		if( this.sRepositoryTotalRemote==null) {
			String sHost = this.getRepositoryRemoteHost();
			String sAccount = this.getRepositoryRemoteAccount();
			String sRepositoryProjectRemote = this.getRepositoryProject();
			if(StringZZZ.isEmpty(sHost) || StringZZZ.isEmpty(sAccount) || StringZZZ.isEmpty(sRepositoryProjectRemote)) return null;
			this.sRepositoryTotalRemote = JgitUtilHTTPS.computeRepositoryUrlHTTPS(sHost, sAccount, sRepositoryProjectRemote);			
		}
		return this.sRepositoryTotalRemote;
	}
		
	
	@Override
	public boolean configureGit() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try {
				//Konfiguriere JGit für HTTPS
				
				
				
				//+++ Zugriff sicherstellen
				//wie? sPAT holen, ist das vorhanden?
				//Credentials Provider wird erst nach dem Git-Objekt zur Vefügung stehen, s. unten.
				
				
				//B) Konfiguriere das lokale Repository und init Git-Object (vor dem Remote Repository, damit fehlende Daten ggfs. aus dem lokalen Repository gelesen werden können)
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
					ExceptionZZZ ez = new ExceptionZZZ("Weder Basis Url direkt angegeben noch per Alias '" + sRepositoryRemoteAlias + "' ermittelbar.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
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
				if(JgitUtil.isUrlSSH(sDirectoryRepositoryRemote)) {
					String sAccount = JgitUtilSSH.getAccountFromUrl(sDirectoryRepositoryRemote);
					String sHost = JgitUtilSSH.getHostFromUrl(sDirectoryRepositoryRemote);	
					sRepositoryBaseRemote = JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sHost, sAccount);				
				}else {
					sRepositoryBaseRemote = sDirectoryRepositoryRemote;
				}
				this.setRepositoryBaseRemote(sRepositoryBaseRemote);
				
				
				String sRepositoryTotalRemote = JgitUtilHTTPS.computeRepositoryUrlHTTPS(sRepositoryBaseRemote, sRepositoryProjectRemote);
				this.setRepositoryTotalRemote(sRepositoryTotalRemote);
				
				//+++++++++++++++++++++++++++++++
			
				//+++ HTTPS Zugriff sicherstellen
				Git git = this.getGitObject();
				CredentialsProvider credentialsProvider = this.createCredentialsProviderByToken(git);
				System.out.println("Git Credentials Provider created done.");
				this.setCredentialsProviderObject(credentialsProvider);
			
				
				bReturn = true;
			//###############################################################	  	
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}
			
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
				if(StringZZZ.isEmpty(sRepositoryRemoteAccountIn)) {
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
				
				//################## 
				//Besonderheit HTTPS
				String sPatIn = objConfig.readPersonalAccessToken();
				if(StringZZZ.isEmpty(sPatIn) & StringZZZ.equalsIgnoreCase(sConnectionType, "HTTPS")){
					ExceptionZZZ ez = new ExceptionZZZ("Remote Repository, Personal Access Token (PAT)", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setPersonalAccessToken(sPatIn);
													
				
			
				//################################################
				//Konfiguriere JGit für HTTPS
				boolean bSuccess = this.configureGit();
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
					
				//+++++++++++++++++++++++++++++++++++++++++++++++++
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
			
		    //###############################################################	  

			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			MergeResult objMergeResult = JgitUtilHTTPS.pullHTTPS(git, credentialsProvider, sPAT, sRepoRemote);	
			MergeStatus objMergeStatus = objMergeResult.getMergeStatus();
			bReturn = objMergeStatus.isSuccessful();
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			MergeResult objMergeResult =  JgitUtilHTTPS.pullIgnoreCheckoutConflictsHTTPS(git, credentialsProvider, sPAT, sRepoRemote);	
			MergeStatus objMergeStatus = objMergeResult.getMergeStatus();
			bReturn = objMergeStatus.isSuccessful();
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{					
			MergeResult objMergeResult = JgitUtilHTTPS.pullSingleBranchWithAutoResolveHTTPS(git, credentialsProvider, sPAT, sRepoRemote, sBranch);
			MergeStatus objMergeStatus = objMergeResult.getMergeStatus();
			bReturn = objMergeStatus.isSuccessful();
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullitSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			MergeResult objMergeResult = JgitUtilHTTPS.pullSingleBranchHTTPS(git, credentialsProvider, sPAT, sRepoRemote, sBranch);//JgitUtilHTTPS.pullSingleBranchWithAutoResolveHTTPS(git, credentialsProvider, sPAT, sRepoRemote, sBranch);
			if(objMergeResult==null) {
				System.out.println("Kein Merge durchgeführt/Kein MergeResult-Objekt. Vorbedingungen für ein sauberes Repository nicht erfüllt. Bitte (wenn vorhanden) Lösungsvorschläge probieren.");
				break main;
			}
			
			MergeStatus objMergeStatus = objMergeResult.getMergeStatus();			
			bReturn = objMergeStatus.isSuccessful();
			if(bReturn) break main;
			
			//Falls Merge nicht erfolgreich ist, hier am Schluss die Dateien mit den Konflikten auflisten
			boolean bAnyConflict = JgitUtil.logConflicts(objMergeResult);
			bReturn = !bAnyConflict;
			
			
			System.out.println("##### ANALYSE #######");
			ResultPostMergeAnalysis objAnalyseResult = GitPostMergeAnalyse.analyzeMergeResult(objMergeResult);
			objAnalyseResult.printReport();
			
					
		}//end main:
		return bReturn;
	}
	
	
	
			
	//###############################################
	@Override
	public boolean commitit(IConfigJGIT objConfig) throws ExceptionZZZ {	
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
						ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					sConnectionTypeIn = JgitUtil.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);
				}else {
					ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
			
		
			String sRepositoryRemoteHost = objConfig.readRepositoryRemoteHost();
			if(StringZZZ.isEmpty(sRepositoryRemoteHost)){
				ExceptionZZZ ez = new ExceptionZZZ("Hostname des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteAccount = objConfig.readRepositoryRemoteAccount();
			if(StringZZZ.isEmpty(sRepositoryRemoteAccount)){
				ExceptionZZZ ez = new ExceptionZZZ("Account des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}

			
			//+++++++++++++++++++++++								
			this.setConnectionType(sConnectionTypeIn);
			this.setRepositoryRemoteHost(sRepositoryRemoteHost);
			this.setRepositoryRemoteAccount(sRepositoryRemoteAccount);
							
			
			String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
			if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
				ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote SSH Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryBaseRemote(sRepositoryRemoteIn);
			
			//#################### Besonderheit HTTPS
			String sPatIn = objConfig.readPersonalAccessToken();
			if(StringZZZ.isEmpty(sPatIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository, Personal Access Token (PAT)", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setPersonalAccessToken(sPatIn);
			
			
			
			//+++++++++++++++++++++++++++++++
			//Konfiguriere JGit für HTTPS
			boolean bSuccess = this.configureGit();
			if(bSuccess) {
				System.out.println("Git erfolgreich konfiguriert");
			}else {
				System.out.println("Git NICHT erfolgreich konfiguriert");
				break main;
			}
			
			//+++++++++++++++++++++++++++++++
			//Finde geaenderte und neue Dateien fuer den commit
			Git git = this.getGitObject();
			boolean bSuccessCommit = this.commitit(git);
			if(bSuccessCommit) {
				System.out.println("STATUS AFTER COMMIT: SUCCESSFUL");
				this.printStatus(git);
				  bReturn = true;
			}else {
				System.out.println("STATUS AFTER COMMIT: FAILED");
				this.printStatus(git);	
				bReturn = false;
			}
		
		    git.close();
		  
        //###############################################################
		
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
	public boolean commitAndPushit(IConfigJGIT objConfig) throws ExceptionZZZ {	
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
						ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					sConnectionTypeIn = JgitUtil.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);
				}else {
					ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
			
		
			String sRepositoryRemoteHost = objConfig.readRepositoryRemoteHost();
			if(StringZZZ.isEmpty(sRepositoryRemoteHost)){
				ExceptionZZZ ez = new ExceptionZZZ("Hostname des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepositoryRemoteAccount = objConfig.readRepositoryRemoteAccount();
			if(StringZZZ.isEmpty(sRepositoryRemoteAccount)){
				ExceptionZZZ ez = new ExceptionZZZ("Account des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}

			
			//+++++++++++++++++++++++								
			this.setConnectionType(sConnectionTypeIn);
			this.setRepositoryRemoteHost(sRepositoryRemoteHost);
			this.setRepositoryRemoteAccount(sRepositoryRemoteAccount);
							
			
			String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
			if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
				ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote SSH Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryBaseRemote(sRepositoryRemoteIn);
			
			//#################### Besonderheit HTTPS
			String sPatIn = objConfig.readPersonalAccessToken();
			if(StringZZZ.isEmpty(sPatIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository, Personal Access Token (PAT)", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setPersonalAccessToken(sPatIn);
			
			
			
			//+++++++++++++++++++++++++++++++
			//Konfiguriere JGit für HTTPS
			boolean bSuccess = this.configureGit();
			if(bSuccess) {
				System.out.println("Git erfolgreich konfiguriert");
			}else {
				System.out.println("Git NICHT erfolgreich konfiguriert");
				break main;
			}
			
			//+++++++++++++++++++++++++++++++
			//Finde geaenderte und neue Dateien fuer den commit
			Git git = this.getGitObject();
			boolean bSuccessCommit = this.commitit(git);
			if(!bSuccessCommit) {
				System.out.println("commit NICHT erfolgreich");
				bReturn = false;
				
			} else {
				System.out.println("commit erfolgreich");
								
				//++++++++++++++++++++++++++++++++
				//Führe den Push durch
				
				//a) Zugriff sicherstellen
		        //   Das passiert durch Credential und PAT
		        
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
		        JgitStarterHTTPS.fetchIgnoreNothingToFetch(objFileDir, sRepositoryRemote);
			    System.out.println(("FETCH DONE"));
				
			    bReturn = true;
			}
								  	
		    git.close();
		   
        //###############################################################
		
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

	public CredentialsProvider createCredentialsProviderByToken(Git git) {
		//aus Eclipse-Push Konfiguration:
				//entspricht dem Github - Projekt - SSH
				//git@github.com:firak01/HIS_QISSERVER_FGL.git
				
				//aus Github - Projekt - HTTPS
				//https://github.com/firak01/HIS_QISSERVER_FGL.git
				
				//##################
				//Authentifizierung mit https
				/*https://medium.com/autotrader-engineering/working-with-git-in-java-part-1-a-jgit-tutorial-bc03b404a517
				Authenticating with a remote
				Most remote repos will require authentication (at least for the push command). In this tutorial, we’ll be working with remote repositories hosted on GitHub, which has two common authentication methods:
		    	Using a personal access token (PAT) for authentication over HTTPS
		    	Using SSH keys for authentication over SSH
				To keep things simple in this tutorial, we’ll only be covering HTTPS authentication; SSH is more complex and will be covered in part 2 of this two-part blog post.

				So in the following examples, we’ll be using a personal access token (PAT) for authentication via HTTPS. For more information on creating a PAT token, see the GitHub docs.
				Providing Credentials for Authentication

				The JGit command objects for operations such as git push, git pull, and git clone all share a setCredentialsProvider method that allows us to provide credentials to authenticate with the remote repository.

				The setCredentialsProvider method takes a CredentialsProvider instance as its parameter. This interface has many implementations, the one we need to use for a PAT token is the UsernamePasswordCredentialsProvider (more commonly used for basic authentication).
				Constructing a CredentialsProvider for a PAT token

				The UsernamePasswordCredentialsProvider 's constructor requires a username and password. When using a PAT token, we pass the token as the username and an empty string as the password:
				 */
				
				
				CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(sPAT, ""); //irgendwie empfohlen
				//CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("firak01", sPAT); //so funktioniert es auch nicht
				
				/*Fehler:
				 Exception in thread "main" org.eclipse.jgit.errors.UnsupportedCredentialItem: ssh://git@github.com:22: org.eclipse.jgit.transport.CredentialItem$YesNoType:The authenticity of host 'github.com' can't be established.
		RSA key fingerprint is.... .
		Are you sure you want to continue connecting?
			at org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider.get(UsernamePasswordCredentialsProvider.java:119)
				 */

			 
		return credentialsProvider;
	}
	
	@Override
	public boolean pushit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {		
				// aber mal explizit als pushCommand
				PushCommand pushCommand = git.push();
						
				//An einigen Stellen wird die Syntax der URL mit Username:Token genannt.
				//git clone https://scuzzlebuzzle:<MYTOKEN>@github.com/scuzzlebuzzle/ol3-1.git --branch=gh-pages gh-pages
				//pushCommand.setRemote("https://firak01:" + sPAT + "@github.com/firak01/HIS_QISSERVER_FGL.git");
				
				//anderes Verzeichnis:
				//lokal:
				//remote: 
				//pushCommand.setRemote("https://firak01:" + sPAT + "@github.com/firak01/Projekt_Kernel02_JAZDummy.git");
				
				//SSH VERSION:     git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
				//https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
				
				
				//TODOGOON20260321; // Die Variante mit sPAT in der URL hat den Nachteil, das dies irgendwo im Log etc. auftauchen koennte
				//Darum versuchen dies ohne sPAT in URL zu realisieren
				//                  //Variante A) mit sPAT in URL
				//                  https://firak01:" + sPAT + "@github.com/firak01/Projekt_Kernel02_JAZDummy.git
				//
				//                  //Variante B) ohne sPAT in URL
				//                  https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
		
				String sUrlPartFromRepo = JgitUtil.computeRepositoryUrlPartFromUrlRepo(sRepoRemote);
				pushCommand.setRemote("https://firak01:" + sPAT + "@" + sUrlPartFromRepo);
				
				
				//lokal: File objFileDir = new File("C:\\HIS-Workspace\\1fgl\\repo\\EclipseOxygen\\HIS_QISSERVER_FGL");
				//remote: https://github.com/firak01/HIS_QISSERVER_FGL.git
				//pushCommand.setRemote("https://firak01:" + sPAT + "@github.com/firak01/HIS_QISSERVER_FGL.git");
				
				
				//aber, wenn Fehler: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
				//Loesungsansatz:    keytool ist wohl ein Program unter dem Java JDK
				//                   keytool -import -noprompt -trustcacerts -alias http://www.example.com -file "C:\Path\to\www.example.com.crt" -keystore cacerts
				//Damit erstellt man einen zusaetzlichen Eintrag im Certifier-Store, der Datei cacerts ( z.B. hier: C:\java\jdk1.8.0\jre\lib\security\cacerts )
		  
				//push to remote:
				pushCommand.setCredentialsProvider(credentialsProvider);
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
				
				String sRepositoryRemoteHostIn = objConfig.readRepositoryRemoteHost();
				if(StringZZZ.isEmpty(sRepositoryRemoteHostIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote Host und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
								
				String sRepositoryRemoteAccountIn = objConfig.readRepositoryRemoteAccount();
				if(StringZZZ.isEmpty(sRepositoryRemoteAccountIn)) {
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
				
				//################## 
				//Besonderheit HTTPS
				String sPatIn = objConfig.readPersonalAccessToken();
				if(StringZZZ.isEmpty(sPatIn) & StringZZZ.equalsIgnoreCase(sConnectionType, "HTTPS")){
					ExceptionZZZ ez = new ExceptionZZZ("Remote Repository, Personal Access Token (PAT)", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setPersonalAccessToken(sPatIn);
													
				
			
				//################################################
				//Konfiguriere JGit für HTTPS
				boolean bSuccess = this.configureGit();
				if(bSuccess) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
					
				//+++++++++++++++++++++++++++++++++++++++++++++++++
				//Mache den pull	
				Git git = this.getGitObject();
				boolean bSuccessPush = this.pushit(git);
		        if(bSuccessPush) {
					System.out.println("push erfolgreich");
				}else {
					System.out.println("push NICHT erfolgreich");
					break main;
				}
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

	
	
	@Override
	public boolean pushit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				//a) Zugriff sicherstellen			
				CredentialsProvider credentialsProvider = this.getCredentialsProviderObject();
				String sPAT = this.getPersonalAccessToken();
				String sRepositoryRemoteTotal = this.getRepositoryTotalRemote();
						
		        //b) Mache den push		
		        bReturn = this.pushit(git, credentialsProvider, sPAT, sRepositoryRemoteTotal);
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
		        
		        //aber manchmal ist nichts zu fetchen, dann wuerde ein Fehler geworfen. Das ist unschoen, darum Fehler abfangen
		        String sDirectoryRepositoryLocalTotal = this.getRepositoryTotalLocal();
		        File objFileDir = new File(sDirectoryRepositoryLocalTotal);
		        JgitStarterHTTPS.fetchIgnoreNothingToFetch(objFileDir, sRepositoryRemoteTotal);
			    System.out.println(("FETCH DONE"));
			}catch(TransportException tex) {
				ExceptionZZZ ez = new ExceptionZZZ(tex);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;	
			}
		}//end main:
		return bReturn;
	}

	@Override
	public boolean pullit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			CredentialsProvider credentialsProvider = this.getCredentialsProviderObject();
			String sPAT = this.getPersonalAccessToken();
			String sRepositoryRemoteTotal = this.getRepositoryTotalRemote();
			boolean bIgnoreConflicts = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.MERGE_IGNORE_CHECKOUT_CONFLICTS);
			boolean bAutoResolveConflicts = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.MERGE_AUTOSOLVE_CHECKOUT_CONFLICTS);
			
			//Zum Testen gezielt steuern
			bIgnoreConflicts = false;
			bAutoResolveConflicts = false;
			if (!bIgnoreConflicts & !bAutoResolveConflicts) {
				//Normaler Pull, Konflikte ausgeben, nicht auflösen
				//wir wollen aber immer den bestimmten Branch... this.pullit(git, credentialsProvider, sPAT, sRepoRemote);
				
				String sBranch = "master";
				bReturn = this.pullitSingleBranch(git, credentialsProvider, sPAT, sRepositoryRemoteTotal, sBranch);
								
			} else if(bIgnoreConflicts & !bAutoResolveConflicts) {

				//Konflikte Ignorieren. Die Konfliktdateien werden gezielt zurückgesetzt
				bReturn = this.pullitIgnoreCheckoutConflicts(git, credentialsProvider, sPAT, sRepositoryRemoteTotal);
								
			} else if(!bIgnoreConflicts & bAutoResolveConflicts) {
				
				//Versuchen die Konflikte aufzulösen, ggfs. noch per Strategie, gesteuert durch weitere FLAGZLOCAL
				String sBranch = "master";
				bReturn = this.pullitResolveCheckoutConflictsSingleBranch(git, credentialsProvider, sPAT, sRepositoryRemoteTotal, sBranch);
			
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Unerwartet FlagKombination beim PULL.", iERROR_PARAMETER_VALUE, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return bReturn;
	}

	



	
}
