package use.tool.jgit.https;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.OperationResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import use.tool.jgit.IConfigJGIT;
import use.tool.jgit.AbstractJgitStarter;
import use.tool.jgit.JgitStarterMain;
import use.tool.jgit.JgitUtil;
import use.tool.jgit.JgitUtilHTTPS;



public class JgitStarterHTTPS extends AbstractJgitStarter implements IJgitStarterHTTPS{
	//Zugang per ACCESS TOKEN ( PAT ) in github: Account, ganz unten im Navigator "Developer Settings"
	//String sPAT = "nicht hier, schau woanders nach";
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
	
	//### aus IGitStarter
	@Override 
	public boolean pullit(IConfigJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {			
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen
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
				
				String sRepositoryRemoteIn = objConfig.readRepositoryRemoteHTTPS();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn) && StringZZZ.isEmpty(sRepositoryRemoteAliasIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote HTTPS Repository und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				
				String sRepositoryLocalIn = objConfig.readRepositoryLocal();
				if(StringZZZ.isEmpty(sRepositoryLocalIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sPatIn = objConfig.readPersonalAccessToken();
				if(StringZZZ.isEmpty(sPatIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Remote Repository, Personal Access Token (PAT)", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				
				
				//+++++++++++++++++++++++
				this.setRepositoryLocal(sRepositoryLocalIn);
				this.setRepositoryRemote(sRepositoryRemoteIn);
				this.setRepositoryRemoteAlias(sRepositoryRemoteAliasIn);					
				this.setPersonalAccessToken(sPatIn);
			
			
				//Konfiguriere JGit für HTTPS
				//+++ Zugriff sicherstellen
				String sDirectoryRepositoryLocal = this.getRepositoryLocal();
				if(StringZZZ.isEmpty(sDirectoryRepositoryLocal)) {
					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Verzeichnis, Angabe fehlt: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				File objFileDir = new File(sDirectoryRepositoryLocal);
				if(!objFileDir.exists()) {
					ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
				
				InitCommand gitCommandInit = Git.init();
				gitCommandInit.setDirectory(objFileDir);
				
				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
				this.setGitObject(git);
				System.out.println("Local Git-Repository init done: " + objFileDir.getAbsolutePath());
				
				//+++ Hole die URL vom Remote Repository
				//TODOGOON20260320;//Plausibilitaet: Prüfe, ob https oder ssh in der .git\config Datei steht
				String sRepositoryRemote = this.getRepositoryRemote();
				if(StringZZZ.isEmpty(sRepositoryRemote)) {
					String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
					if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
						ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
				}
				if(StringZZZ.isEmpty(sRepositoryRemote)) {
					ExceptionZZZ ez = new ExceptionZZZ("Weder Url direkt angegeben noch per Alias '" + sRepositoryRemoteAlias + "' ermittelbar.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setRepositoryRemote(sRepositoryRemote);
				
				//+++ HTTPS Zugriff sicherstellen
				CredentialsProvider credentialsProvider = this.createCredentialsProviderByToken(git);
				System.out.println("Git Credentials Provider created done.");
				//+++++++++++++++++++++++++++++++
				        
				//Mache den pull	
				boolean bIgnoreConflicts = this.getFlag(IJgitStarterHTTPSEnabled.FLAGZ.IGNORE_CHECKOUT_CONFLICTS);	
				if(bIgnoreConflicts) {
					//bReturn = this.pullitIgnoreCheckoutConflicts(git, credentialsProvider, sPAT, sRepositoryRemote);
					
					String sBranch = "master";
					bReturn = this.pullitResolveCheckoutConflictsSingleBranch(git, credentialsProvider, sPAT, sRepositoryRemote, sBranch);					
				}else {
					bReturn = this.pullit(git, credentialsProvider, sPAT, sRepositoryRemote);
				}
		        git.close();
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
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			bReturn = JgitUtilHTTPS.pullHTTPS(git, credentialsProvider, sPAT, sRepoRemote);				
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			bReturn = JgitUtilHTTPS.pullIgnoreCheckoutConflictsHTTPS(git, credentialsProvider, sPAT, sRepoRemote);			
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullitResolveCheckoutConflictsSingleBranch(Git git, CredentialsProvider credentialsProvider, String sPAT, String sRepoRemote, String sBranch) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{			
			//bReturn = JgitUtilHTTPS.pullSingleBranchHTTPS(git, credentialsProvider, sPAT, sRepoRemote, sBranch);
			MergeResult objMergeResult = JgitUtilHTTPS.pullSingleBranchWithAutoResolveHTTPS(git, credentialsProvider, sPAT, sRepoRemote, sBranch);
			MergeStatus objMergeStatus = objMergeResult.getMergeStatus();
			bReturn = objMergeStatus.isSuccessful();
		}//end main:
		return bReturn;
	}
	
	
	
			
	//###############################################
	
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
			
			String sRepositoryRemoteIn = objConfig.readRepositoryRemoteHTTPS();
			if(StringZZZ.isEmpty(sRepositoryRemoteIn) && StringZZZ.isEmpty(sRepositoryRemoteAliasIn)){
				ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote HTTPS Repository und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			
			
			String sRepositoryLocalIn = objConfig.readRepositoryLocal();
			if(StringZZZ.isEmpty(sRepositoryLocalIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Pfad zum lokalen Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sPatIn = objConfig.readPersonalAccessToken();
			if(StringZZZ.isEmpty(sPatIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository, Personal Access Token (PAT)", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			
			
			//+++++++++++++++++++++++
			this.setRepositoryLocal(sRepositoryLocalIn);
			this.setRepositoryRemote(sRepositoryRemoteIn);
			this.setRepositoryRemoteAlias(sRepositoryRemoteAliasIn);					
			this.setPersonalAccessToken(sPatIn);
			
			
		//Konfiguriere JGit für HTTPS
		//+++ Zugriff sicherstellen
		String sDirectoryRepositoryLocal = this.getRepositoryLocal();
		if(StringZZZ.isEmpty(sDirectoryRepositoryLocal)) {
			ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Verzeichnis, Angabe fehlt: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
		File objFileDir = new File(sDirectoryRepositoryLocal);
		if(!objFileDir.exists()) {
			ExceptionZZZ ez = new ExceptionZZZ("Lokales Repository Verzeichnis existiert nicht: '" + sDirectoryRepositoryLocal + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;				
		}
		
		InitCommand gitCommandInit = Git.init();
		gitCommandInit.setDirectory(objFileDir);
		
		Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
		this.setGitObject(git);
		System.out.println("Local Git-Repository init done: " + objFileDir.getAbsolutePath());
		
		//+++ Hole die URL vom Remote Repository
		//TODOGOON20260320;//Plausibilitaet: Prüfe, ob https oder ssh in der .git\config Datei steht
		String sRepositoryRemote = this.getRepositoryRemote();
		if(StringZZZ.isEmpty(sRepositoryRemote)) {
			String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
			if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
				ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		if(StringZZZ.isEmpty(sRepositoryRemote)) {
			ExceptionZZZ ez = new ExceptionZZZ("Weder Url direkt angegeben noch per Alias '" + sRepositoryRemoteAlias + "' ermittelbar.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		this.setRepositoryRemote(sRepositoryRemote);
		
		//+++ HTTPS Zugriff sicherstellen
		CredentialsProvider credentialsProvider = this.createCredentialsProviderByToken(git);
		System.out.println("Git Credentials Provider created done.");
		//+++++++++++++++++++++++++++++++
		
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

		
		CommitCommand gitCommandCommit = git.commit();
		gitCommandCommit.setMessage(sDateFormated + " - Commit by Java-Class from a module of Projekt_Tool_DevEditor");
		gitCommandCommit.call();
        //System.out.println("Committed file " + myFile + " to repository at " + git.getRepository().getDirectory());
        
        System.out.println("STATUS AFTER COMMIT");
        this.printStatus(git);
		
        
        
        //Mache den push		
        String sPAT = this.getPersonalAccessToken();
        this.pushit(git, credentialsProvider, sPAT, sRepositoryRemote);
       
        System.out.println("STATUS AFTER PUSH");
        this.printStatus(git);
       
        
        //s. ChatGPT vom 20260313
        //Problem: Eclipse "registriert/bemerkt" den Push nicht (also Pfeil nach oben mit 1 dahinter wird angezeigt).
        //Damit in Eclipse auch der Push "registriert/bemerkt wird" muss noch ein Fetch gemacht werden.
        //Der letzte fetch() sorgt dafür, dass lokale Remote-Tracking-Branches synchron bleiben, 
        //was besonders hilfreich ist, wenn gleichzeitig ein Tool wie Eclipse auf das gleiche Repository schaut.
        
        //aber manchmal ist nichts zu fetchen, dann wuerde ein Fehler geworfen. Das ist unschoen, darum Fehler abfangen 
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
	
	
	//#################################
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
		StatusCommand gitCommandStatus = git.status();
		Status status = gitCommandStatus.call();
		
		Set<String> uncommittedChanges = status.getUncommittedChanges();
		Set<String> untracked          	  = status.getUntracked();
		ArrayList<String> listasUntracked = new ArrayList<String>();
		
		AddCommand gitCommandAdd = git.add();		
        for (String untrackedTemp : untracked) {
        	if(!uncommittedChanges.contains(untrackedTemp)) {
        		listasUntracked.add(untrackedTemp);
        	}
        }
        
        // run the add-call 
        for(String untrackedTemp : listasUntracked) {
        	System.out.println("untracked to add: '" + untrackedTemp + "'");
        	try {
        		gitCommandAdd.addFilepattern(untrackedTemp);
        		gitCommandAdd.call();
        	}catch(java.lang.IllegalStateException isex) {
        		System.out.println(isex.getMessage());
        	}
        }
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
