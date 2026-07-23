package use.jgit.stater.protocol.git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.system.Syso;
import basic.zKernel.AbstractKernelLogZZZ;
import basic.zKernel.KernelZZZ;
import basic.zWin32.com.wmi.WMIZZZ;
import junit.framework.TestCase;
import use.jgit.config.ConfigRepositoryManager4TestGIT_onDEV04;
import use.jgit.config.ConfigRepositoryManager4TestGIT_onTUBAF;
import use.jgit.config.ConfigStarterLocal4TestJGIT;
import use.jgit.config.ConfigStarterRemote4TestJGIT;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.TestHelper;
import use.jgit.config.TestHelperGIT;
import use.jgit.manager.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.resolve.JgitResolverLocalGIT;
import use.jgit.starter.protocol.git.JgitStarterGIT;

public class JgitStarterGITTest extends TestCase{
	private static String sDirectoryRepoA="c:\\temp\\RepoA";
	private static String sDirectoryRepoB="c:\\temp\\RepoB";
	
	//Konfigurationen, je nach Entwicklungsumgebung eine andere
	private IConfigRepositoryManagerJGIT objConfigRepoManager=null;
	
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
			//1. Repositories aus vorherigen Tests entfernen
			boolean bSuccess = TestHelper.removeRepositoriesLocal_onSetup();
			
			//2. RepositoryManger für die Erstellung von TestRepositories holen
			objConfigRepoManager = TestHelperGIT.findRepositoryManagerConfiguration_DefinedForEnvironmentCurrent();
												
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
	
	public void testStarter_createGit() {
		//Merke: Verwendet wird die gültige, im setup gefundene Konfiguration.
		try {
			//###################################################
			//A) "Längere" Variante: Konfiguration im Konstruktor übergeben
			Syso.printSection("A) Create Git-Object");
			try {					
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT(objConfigRepoManager);
				Git gitByManager = objRepositoryManager.createGitObject();
				assertNotNull(gitByManager);
				
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//B) "Noch Längere" Variante: Konfiguration übergeben
			Syso.printSection("B) Create Git-Object");
			try {
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT();
				objRepositoryManager.configureGit(objConfigRepoManager); 
				Git gitByManager = objRepositoryManager.createGitObject();
				assertNotNull(gitByManager);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//C) "Verkürzte" Variante: Konfiguration erst der Methode übergeben.
			Syso.printSection("C) Create Git-Object");
			try {
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT(objConfigRepoManager);
				Git gitByManager = objRepositoryManager.createGitObject(objConfigRepoManager);
				assertNotNull(gitByManager);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
		
	}
	
	
	
	public void testStarterRemote_ConfigureGit() {
		
		try {
			//+++++++++++++++++
			ConfigStarterRemote4TestJGIT objConfigRemote = new ConfigStarterRemote4TestJGIT();
			
			JgitStarterGIT objStarter = new JgitStarterGIT();
			objStarter.configureGit(objConfigRemote);
			Git gitByStarter = objStarter.getGitObject();
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}	
				
		//++++++++++++++++++++++++++++++++++
		
	}
	
}