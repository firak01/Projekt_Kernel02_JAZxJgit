package use.jgit.protocol.git;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.AbstractKernelLogZZZ;
import basic.zKernel.KernelZZZ;
import junit.framework.TestCase;
import use.jgit.config.ConfigRepository4TestJGIT;
import use.jgit.config.ConfigStarterLocal4TestJGIT;
import use.jgit.config.ConfigStarterRemote4TestJGIT;
import use.jgit.manager.JgitRepositoryManagerGIT;
import use.jgit.resolve.JgitResolverLocalGIT;

public class JgitStarterGITTest extends TestCase{
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 * 
	 * Aufbau einer Repository Struktur:
	 * 
	 * Remote
		   ^
		   |
		+--+----------------+
		|                   |
		Clone A         Clone B
	 */
	protected void setUp(){
		try {
//			try {
				ConfigRepository4TestJGIT objConfigRepo = new ConfigRepository4TestJGIT();
							
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT();
				objRepositoryManager.configureGit(objConfigRepo);
				Git gitByManager = objRepositoryManager.getGitObject();
				
				File objFileDirectoryANew = new File("c:\\temp\\RepoA");
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryANew);
				
				File objFileDirectoryBNew = new File("c:\\temp\\RepoB");
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryBNew);
				
				
//				File remoteRepoDir = new File(sTempDir, "remote.git");
//				Git.init()
//				   .setBare(true)
//				   .setDirectory(remoteRepoDir)
//				   .call();
				
//				String sLocalA = "C:\\temp\\RepoA";
//				File localARepoDir = new File(sLocalA);
//				Git.cloneRepository()
//				   .setURI(remoteRepoDir.toURI().toString())
//				   .setDirectory(localARepoDir)
//				   .call();
//	
//				String sLocalB = "C:\\temp\\RepoB";
//				File localBRepoDir = new File(sLocalB);
//				Git.cloneRepository()
//				   .setURI(remoteRepoDir.toURI().toString())
//				   .setDirectory(localBRepoDir)
//				   .call();
				
				
				//+++++++++++++++++
				ConfigStarterLocal4TestJGIT objConfigLocal = new ConfigStarterLocal4TestJGIT();
				
				JgitResolverLocalGIT objResolver = new JgitResolverLocalGIT();
				objResolver.configureGit(objConfigLocal);
				Git gitByResolver = objResolver.getGitObject();
				
				//+++++++++++++++++
				ConfigStarterRemote4TestJGIT objConfigRemote = new ConfigStarterRemote4TestJGIT();
				
				JgitStarterGIT objStarter = new JgitStarterGIT();
				objStarter.configureGit(objConfigRemote);
				Git gitByStarter = objStarter.getGitObject();
				

//			} catch (InvalidRemoteException e) {
//				ExceptionZZZ ez = new ExceptionZZZ(e);
//				throw ez;
//			} catch (TransportException e) {
//				ExceptionZZZ ez = new ExceptionZZZ(e);
//				throw ez;
//			} catch (GitAPIException e) {
//				ExceptionZZZ ez = new ExceptionZZZ(e);
//				throw ez;
//			}
	}catch(ExceptionZZZ ez){
		fail("Method throws an exception." + ez.getMessageLast());
	}
	
	}//END setup
	
	public void tearDown() throws Exception {
		
	}
	
	
	
	//###################################################
	//Die Tests		
	public void testContructor(){
		
//		try{
//				//+++ Hier wird ein Fehler erwarte
//				KernelReaderHtmlZZZ objReaderInit = new KernelReaderHtmlZZZ();
//				boolean btemp = objReaderInit.getFlag("init");
//				assertTrue("Unexpected: The init-Flag was expected to be set", btemp);
//			
//				//+++ This is not correct when using the test object
//				btemp = objReaderTest.getFlag("init");
//				assertFalse("Unexpected: The init flag was expected NOT to be set", btemp);
//				
//				//+++ Nun eine Log-Ausgabe (Notes-Log)
//				AbstractKernelLogZZZ objKernelLog = objReaderTest.getLogObject();
//				assertNotNull(objKernelLog);				
//				objKernelLog.write("succesfully created");
//					
//		}catch(ExceptionZZZ ez){
//			fail("Method throws an exception." + ez.getMessageLast());			
//		}
	}//END testConstructor
	
}