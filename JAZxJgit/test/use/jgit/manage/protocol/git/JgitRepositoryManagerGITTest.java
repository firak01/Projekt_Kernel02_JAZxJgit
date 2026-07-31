package use.jgit.manage.protocol.git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.FileTextReplacerZZZ;
import basic.zBasic.util.machine.EnvironmentZZZ;
import basic.zBasic.util.system.Syso;
import basic.zKernel.AbstractKernelLogZZZ;
import basic.zKernel.KernelZZZ;
import basic.zWin32.com.wmi.WMIZZZ;
import junit.framework.TestCase;
import test.jgit.config.AbstractJgitGITTest;
import test.jgit.config.ConfigRepositoryManager4TestGIT_onDEV04;
import test.jgit.config.ConfigRepositoryManager4TestGIT_onTUBAF;
import test.jgit.config.ITestHelperConstant;
import test.jgit.config.TestHelper;
import test.jgit.config.TestHelperGIT;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.resolve.JgitResolverLocalGIT;
import use.jgit.start.protocol.git.IJgitStarterGIT;
import use.jgit.start.protocol.git.JgitStarterGIT;

/* (non-Javadoc)
 * @see junit.framework.TestCase#setUp()
 * 
 * Aufbau einer Repository Struktur:
 * 
  	Remote
	   ^
	   |
	+--+----------------+
	|                   |
	Clone A         Clone B



//Merke: Es gibt die TestRepositoryFactory.createCloneA(configRepositoryManager);
//IDEE: Mit dieser TestFactory verkürzt man idealerweise im JGitStarterTests den Code.
//      In dem JgitRepositoryManager...Tests bleibt dann der ausführliche Code bestehen, also: ...
//      objRepositoryManagerRemote = new JgitRepositoryManagerGIT(configRepositoryManager);
//      ... objRepositoryManagerRemote.cloneRepositoryTo(objFileRepoBaseLocalNew);
//			
//IDEE: Entsprechend in dem createStarter() Test hier die lange Version erhalten.      
//			0.2. RepositoryStarter-Konfiguration ausgehend vom neuen, geclonten Repository holen/suchen und zuweisen
//			configStarter = TestHelperGIT.findStarterRemoteConfiguration_DefinedForRepositoryBaseLocal(objFileRepoBaseLocalNew);
//
//I			JgitStarterGIT objStarter = new JgitStarterGIT(configStarter);


 */
public class JgitRepositoryManagerGITTest extends AbstractJgitGITTest{
	private static String sDirectoryRepoBaseA=ITestHelperConstant.sDirectoryRepoBaseA;
	private static String sDirectoryRepoBaseB=ITestHelperConstant.sDirectoryRepoBaseB;
	
	//Konfigurationen, je nach Entwicklungsumgebung eine andere
	private IConfigRepositoryManagerJGIT objConfigRepoManager=null;
		
	
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
	
	public void testManager_createGit_onOriginal() {
		//Merke: Verwendet wird die gültige, im setup gefundene Konfiguration.
		try {
			//###################################################
			//A) "Längere" Variante: Konfiguration im Konstruktor übergeben
			Syso.printSection("A) Create Git-Object");
			try {					
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT(configRepositoryManager);
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
				objRepositoryManager.configureGit(configRepositoryManager); 
				Git gitByManager = objRepositoryManager.createGitObject();
				assertNotNull(gitByManager);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//C) "Verkürzte" Variante: Konfiguration erst der Methode übergeben.
			Syso.printSection("C) Create Git-Object");
			try {
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT();
				Git gitByManager = objRepositoryManager.createGitObject(configRepositoryManager);
				assertNotNull(gitByManager);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
		
	}
	
	public void testManager_createStarter() {
		try {
			//###################################################
			//1. Erstelle mit dem RepositoryManager ein neues Repo
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode
	
			//###################################################
			//0) erstelle das geclonte Repo
			Syso.printSection("0) Create local repo");
			
			//++++++++++++++++++++++++++++++++++++++++++++++
			//0.1. Lokales Repository als Clone bereitstellen
			//   Verwende für den RepositoryManager des Remote Repositories die Konfiguration aus dem Setup
			//   "Längere" Variante: Konfiguration im Konstruktor übergeben
			JgitRepositoryManagerGIT objRepositoryManager = null;			
			try {					
				objRepositoryManager =  new JgitRepositoryManagerGIT(configRepositoryManager);								
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
							
			boolean bSuccess = false;			
			File objFileRepoBaseLocalNew = new File(sDirectoryRepoBaseB); //in der clone Methode wird das Projekt geholt... , sRepositoryProject); //B !!!			
			try {							
				objRepositoryManager.cloneRepositoryTo(objFileRepoBaseLocalNew);	
				bSuccess = FileEasyZZZ.exists(objFileRepoBaseLocalNew);
				assertTrue("Repository existiert nicht: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'");			
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//0.2. RepositoryStarter-Konfiguration ausgehend vom neuen, geclonten Repository holen/suchen und zuweisen
			configStarter = TestHelperGIT.findStarterRemoteConfiguration_DefinedForRepositoryBaseLocalAtoC(objFileRepoBaseLocalNew);
										
			//+++++++++++++++++++++++++++++++++++++++++++++++
			//3. Führe den STATUS aus... sichere ihn, zum Vergleich.
			//Hole erst einmal die für das neu erstellte lokale Repo die passende RemoteStarter Konfiguration	
			try {
				IJgitStarterGIT objStarter = new JgitStarterGIT(configStarter);
				objStarter.setProjectStartingName(IConfigJGIT.sPROJECT_NAME);
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
				objRepositoryManager = new JgitRepositoryManagerGIT(configRepositoryManager);					
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
				objRepositoryManager.configureGit(configRepositoryManager); 
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
				objRepositoryManager.cloneRepositoryTo(configRepositoryManager, objFileDirectoryANew);
				bSuccess = FileEasyZZZ.exists(objFileDirectoryANew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryANew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryANew.getAbsolutePath() + "'");	
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}			
			
			try {
				objRepositoryManager.cloneRepositoryTo(configRepositoryManager, objFileDirectoryBNew);	
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