package use.jgit;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.config.IConfigZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.start.protocol.ssh.JGitSshConfigZZZ;
import use.jgit.start.protocol.ssh.JgitStarterSSH;
import use.jgit.util.JgitUtilHTTPS;
import use.jgit.util.JgitUtilZZZ;

public abstract class AbstractJgitRepositoryManagerZZZ<T> extends AbstractJgitStarterAuthentificated<T> implements IJgitRepositoryManagerZZZ, IJgitStarterAuthentificatedEnabledZZZ{
	
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
	public boolean createGitCustom(InitCommand objInitCommand) throws ExceptionZZZ{
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
			bReturn = super.configureGit(objConfig);
		}//end main:
		return bReturn;
	}
	
	
	//##########################################################
	
	//Methoden
	@Override
	public boolean cloneRepositoryTo(File objFileBaseDirectory) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				IConfigRepositoryManagerJGIT objConfig = (IConfigRepositoryManagerJGIT) this.getConfiguration();
				
				boolean bSuccess = this.configureGit(objConfig);
				if(!bSuccess) break main;
				
				//Man braucht das Git - Objekt hier nicht. Git git = this.getGitObject();

				String sUriRemote = this.getRepositoryTotalRemote();
				if(StringZZZ.isEmpty(sUriRemote)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryTotalRemote", iERROR_PROPERTY_MISSING, AbstractJgitRepositoryManagerZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
				
				String sRepositoryProject = objConfig.readRepositoryProjectName();
				String sFileDirctoryRepositoryProject = FileEasyZZZ.joinFilePathName(objFileBaseDirectory, sRepositoryProject);
				File objFileDirectoryRepositoryProject = new File(sFileDirctoryRepositoryProject);
				
				
				CredentialsProvider credentialsProvider = this.getCredentialsProviderObject();
				
				CloneCommand cloneCommand = Git.cloneRepository();
				
				if(credentialsProvider!=null) { cloneCommand.setCredentialsProvider(credentialsProvider); }
				cloneCommand.setURI(sUriRemote)
				.setDirectory(objFileDirectoryRepositoryProject)
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
			bReturn = true;
		}//end main:
		return bReturn;		
	}
	
	@Override
	public boolean cloneRepositoryTo(IConfigRepositoryManagerJGIT objConfig, File objFileDirectory) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				boolean bSuccess = this.configureGit(objConfig);
				if(!bSuccess) break main;
				
				//Man braucht das Git - Objekt hier nicht. Git git = this.getGitObject();

				String sUriRemote = this.getRepositoryTotalRemote();
				if(StringZZZ.isEmpty(sUriRemote)) {
					ExceptionZZZ ez = new ExceptionZZZ("RepositoryTotalRemote", iERROR_PROPERTY_MISSING, AbstractJgitRepositoryManagerZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
				
				CredentialsProvider credentialsProvider = this.getCredentialsProviderObject();
				
				CloneCommand cloneCommand = Git.cloneRepository();
				
				if(credentialsProvider!=null) { cloneCommand.setCredentialsProvider(credentialsProvider); }
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

	
	//###################################################
	
}
