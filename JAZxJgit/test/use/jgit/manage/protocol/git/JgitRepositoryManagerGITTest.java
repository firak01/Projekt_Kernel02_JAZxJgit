package use.jgit.manage.protocol.git;

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
import use.jgit.config.ConfigStarterRemote4TestJGIT_XonAny;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.ITestHelperConstant;
import use.jgit.config.TestHelper;
import use.jgit.config.TestHelperGIT;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.resolve.JgitResolverLocalGIT;
import use.jgit.start.protocol.git.JgitStarterGIT;

public class JgitRepositoryManagerGITTest extends TestCase{
	private static String sDirectoryRepoBaseA=ITestHelperConstant.sDirectoryRepoBaseA;
	private static String sDirectoryRepoBaseB=ITestHelperConstant.sDirectoryRepoBaseB;
	
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
	
	public void testManager_createGit() {
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
	
	//####################################################################
	public void testManager_A_cloneRepositoryTo() {
		try {
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode
			File objFileDirectoryANew = new File(sDirectoryRepoBaseA);
			File objFileDirectoryBNew = new File(sDirectoryRepoBaseB);
			boolean bSuccess = false;
			
			//###################################################
			//A) "Längere" Variante: Konfiguration im Konstruktor übergeben
			JgitRepositoryManagerGIT objRepositoryManager = null;
			Syso.printSection("A) CloneRepositoryTo");
			try {					
				objRepositoryManager = new JgitRepositoryManagerGIT(objConfigRepoManager);					
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			try {
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryANew);	
				bSuccess = FileEasyZZZ.exists(objFileDirectoryANew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryANew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryANew.getAbsolutePath() + "'");			
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			try {
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryBNew);
				bSuccess = FileEasyZZZ.exists(objFileDirectoryBNew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryBNew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryBNew.getAbsolutePath() + "'");		
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}
	
	public void testManager_B_cloneRepositoryTo() {
		try {
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode
			File objFileDirectoryANew = new File(sDirectoryRepoBaseA);
			File objFileDirectoryBNew = new File(sDirectoryRepoBaseB);
			boolean bSuccess=false;
			
			//###################################################
			//B) "Noch Längere" Variante: Konfiguration übergeben
			Syso.printSection("B) CloneRepositoryTo");
			JgitRepositoryManagerGIT objRepositoryManager = null;
			try {
				objRepositoryManager = new JgitRepositoryManagerGIT();
				objRepositoryManager.configureGit(objConfigRepoManager); 
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			
			try {	
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryANew);				
				bSuccess = FileEasyZZZ.exists(objFileDirectoryANew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryANew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryANew.getAbsolutePath() + "'");
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			
			try{	
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryBNew);							
				bSuccess = FileEasyZZZ.exists(objFileDirectoryBNew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryBNew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryBNew.getAbsolutePath() + "'");							
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}
	
	public void testManager_C_cloneRepositoryTo() {
		try {
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode
			File objFileDirectoryANew = new File(sDirectoryRepoBaseA);
			File objFileDirectoryBNew = new File(sDirectoryRepoBaseB);
			boolean bSuccess = false;
			
			//###################################################
			//C) "Verkürzte" Variante: Konfiguration erst der Methode übergeben.
			Syso.printSection("C) CloneRepositoryTo");
			JgitRepositoryManagerGIT objRepositoryManager = null;
			try {
				objRepositoryManager = new JgitRepositoryManagerGIT();				
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
				
			try {
				objRepositoryManager.cloneRepositoryTo(objConfigRepoManager, objFileDirectoryANew);
				bSuccess = FileEasyZZZ.exists(objFileDirectoryANew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryANew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryANew.getAbsolutePath() + "'");	
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}			
			
			try {
				objRepositoryManager.cloneRepositoryTo(objConfigRepoManager, objFileDirectoryBNew);	
				bSuccess = FileEasyZZZ.exists(objFileDirectoryBNew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryBNew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryBNew.getAbsolutePath() + "'");		
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}	
}