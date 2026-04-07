package use.tool.jgit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import use.tool.jgit.IConfigJGIT;

public abstract class AbstractJgitStarter extends AbstractObjectWithFlagZZZ implements IJgitStarter{
	protected volatile Git gitObject = null;
	
	protected volatile String sRepositoryBaseLocal=null;  //Basis Verzeichnis
	protected volatile String sRepositoryBaseRemote=null; //Basis URL
	protected volatile String sRepositoryProject=null;//Der Name des Projekt, wie er hinter die Basis Verzeichnis/Url kommt.
	protected volatile String sRepositoryRemoteAlias=null;
	

	//aus IJgitStarter
	@Override 
	public Git getGitObject() throws ExceptionZZZ{
		return this.gitObject;
	}
	
	@Override
	public void setGitObject(Git git) throws ExceptionZZZ{
		this.gitObject = git;
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
