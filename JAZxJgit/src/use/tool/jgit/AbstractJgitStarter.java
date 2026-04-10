package use.tool.jgit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.tool.jgit.IConfigJGIT;

public abstract class AbstractJgitStarter extends AbstractObjectWithFlagZZZ implements IJgitStarter{
	protected volatile Git gitObject = null;
	protected volatile CredentialsProvider credentialsProviderObject = null;
	
	protected volatile String sRepositoryProject=null;//Der Name des Projekt, wie er hinter die Basis Verzeichnis/Url kommt.
	protected volatile String sRepositoryBaseLocal=null;  //Basis Verzeichnis
	protected volatile String sRepositoryTotalLocal=null;  //Geamt Verzeichnis
	
	protected volatile String sRepositoryBaseRemote=null; //Basis URL	
	protected volatile String sRepositoryTotalRemote=null; //Gesamt URL
		
	protected volatile String sRepositoryRemoteAlias=null;
	
	

	//aus IJgitStarter
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
		return this.sRepositoryRemoteAlias;
	}

	@Override
	public void setRepositoryRemoteAlias(String sRepositoryRemoteAlias) throws ExceptionZZZ {
		this.sRepositoryRemoteAlias = sRepositoryRemoteAlias;
	}
	
	@Override
	public String getRepositoryBaseRemote() throws ExceptionZZZ {
		if(StringZZZ.isEmpty(this.sRepositoryBaseRemote)) {
			String sRepositoryRemoteBySearch = this.searchRepositoryRemote();
			this.setRepositoryBaseRemote(sRepositoryRemoteBySearch);
		}
		return this.sRepositoryBaseRemote;
	}

	@Override
	public void setRepositoryBaseRemote(String sRepositoryBaseRemote) throws ExceptionZZZ {
		this.sRepositoryBaseRemote = sRepositoryBaseRemote;
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
	public String computeRepositoryRemoteUrl(String sRepositoryBaseRemoteIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			String sRepositoryBaseRemote=null; String sRepositoryProject=null;
			if(StringZZZ.isEmpty(sRepositoryBaseRemoteIn)) {
				sRepositoryBaseRemote = this.getRepositoryBaseRemote();
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
					
				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH)
				//
				//       !!! UND DAS SOLLTE VORHER PASSIEREN
				String sDirectoryRepositoryRemote = this.getRepositoryBaseRemote();
				String sRepositoryProjectRemote = this.getRepositoryProject();
				String sRepositoryRemoteUrl = this.computeRepositoryRemoteUrl(sDirectoryRepositoryRemote, sRepositoryProjectRemote);
				
				
				Repository repo = JgitUtil.getRepositoryObject(sDirectoryRepositoryLocalTotal, true);
				JgitUtil.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);
	
			
				//##############################################
				InitCommand gitCommandInit = Git.init();
				gitCommandInit.setDirectory(objFileDirTotal);
				
				Git git = gitCommandInit.call(); //Merke: damit das funktioniert muss der Pfad zu git.exe in der PATH Umgebungsvariablen sein. Z.B. c:\Progamme\Git\bin
				this.setGitObject(git);
				System.out.println("Local Git-Repository init done: " + objFileDirTotal.getAbsolutePath());

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
	public abstract boolean pushit(IConfigJGIT objConfig) throws ExceptionZZZ;

	@Override
	public abstract boolean pullit(IConfigJGIT objConfig) throws ExceptionZZZ;

	
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
}
