package use.jgit;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.protocol.ssh.JGitSshConfigZZZ;
import use.jgit.util.JgitUtilHTTPS;
import use.jgit.util.JgitUtilZZZ;

public class AbstractJgitRepositoryManagerZZZ<T> extends AbstractJgitStarterAuthentificated<T> implements IJgitRepositoryManagerZZZ, IJgitStarterAuthentificatedEnabledZZZ{
	
	protected boolean bRepositoryBare = false;
	
	
	@Override
	public boolean isRepositoryBare() throws ExceptionZZZ{
		return this.bRepositoryBare;
	}
	
	@Override
	public void isRepositoryBare(boolean bRepositoryBare) throws ExceptionZZZ {
		this.bRepositoryBare = bRepositoryBare;
	}
	
	@Override
	public boolean configureGitCustom(InitCommand objInitCommand) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(this.isRepositoryBare()) {
				objInitCommand.setBare(true);
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean configureGitCustom(IConfigRepositoryManagerJGIT objConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			
			boolean bRepositoryBare = objConfig.isRepositoryBare();
			this.isRepositoryBare(bRepositoryBare);
			
		}//end main:
		return bReturn;
	}

	//#######################################
	@Override
	public boolean configureGit(IConfigRepositoryManagerJGIT objConfig) throws ExceptionZZZ{
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
			
			bReturn = this.createGitObject();
			
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
			String sConnectionType = sConnectionTypeIn.toLowerCase();
			
			
			//+++ Zugriff sicherstellen
			//0) SshSessionFactory ... mit den verwendeten Ids, Pfaden, etc.
			//Das müsste eigentlich für HTTPS nicht gemacht werden.
			if(sConnectionType.equalsIgnoreCase("https")) {
				System.out.println("Bei HTTPS wird keine SSH Session Factory benötigt");
			}else {
				JGitSshConfigZZZ.configure();
				System.out.println("Verwendete Ssh Session Factory: " + SshSessionFactory.getInstance().getClass());
			}
			
			//+++ HTTPS Zugriff sicherstellen
			CredentialsProvider credentialsProvider = JgitUtilHTTPS.createCredentialsProviderByToken(this.getPersonalAccessToken());
			System.out.println("Git Credentials Provider created done.");
			this.setCredentialsProviderObject(credentialsProvider);
			
			
			///##############################################
			//Weil das was mit dem Wunsch-Protocol zu tun hat, hier nicht machen
			//... JgitUtilZZZ.ensureRemoteExists(repo, sRepositoryRemoteAlias, sRepositoryRemoteUrl, true);
			//######################################			
		}//end main:
		return bReturn;
	}
	
	//Methoden
	@Override
	public boolean cloneRepositoryTo(File objFileDirectory) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				Git git = this.getGitObject();
				//remoteRepoDir.toURI().toString()
				String sUriRemote = this.getRepositoryTotalRemote();
				CredentialsProvider credentialsProvider = this.getCredentialsProviderObject();
		
				CloneCommand cloneCommand = git.cloneRepository();
				
				cloneCommand.setURI(sUriRemote)
				.setDirectory(objFileDirectory)
				   .call();
			} catch (InvalidRemoteException e) {
				ExceptionZZZ ez = new ExceptionZZZ(e);
				throw ez;
			} catch (TransportException e) {
				ExceptionZZZ ez = new ExceptionZZZ(e);
				throw ez;
			} catch (GitAPIException e) {
				ExceptionZZZ ez = new ExceptionZZZ(e);
				throw ez;
			}
		}//end main:
		return bReturn;		
	}
}
