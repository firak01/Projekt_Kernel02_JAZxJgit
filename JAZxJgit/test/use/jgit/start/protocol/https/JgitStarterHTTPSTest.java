package use.jgit.start.protocol.https;

import java.io.File;

import org.eclipse.jgit.api.Git;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.system.Syso;
import junit.framework.TestCase;
import use.jgit.config.ConfigRepositoryManager4TestGIT_onAny;
import use.jgit.config.ConfigRepositoryManager4TestHTTPS_onAny;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.config.IConfigWithAuthentificationJGIT;
import use.jgit.config.ITestHelperConstant;
import use.jgit.config.TestHelper;
import use.jgit.config.TestHelperHTTPS;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.manage.protocol.https.JgitRepositoryManagerHTTPS;

public class JgitStarterHTTPSTest extends TestCase{
	private static String sDirectoryRepoA=ITestHelperConstant.sDirectoryRepoA;
	private static String sDirectoryRepoB=ITestHelperConstant.sDirectoryRepoB;
	
	//Konfigurationen, je nach Entwicklungsumgebung eine andere
	private IConfigStarterRemoteJGIT objConfigStarterRemote=null;	
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
			objConfigRepoManager = TestHelperHTTPS.findRepositoryManagerConfiguration_DefinedForEnvironmentCurrent();
			
			//3. StarterManager für die Aktivitäten mit dem Lokalen/Remote Repository holen
			objConfigStarterRemote = TestHelperHTTPS.findStarterRemoteConfiguration_DefinedForEnvironmentCurrent();
												
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
				JgitStarterHTTPS objStarter = new JgitStarterHTTPS(objConfigStarterRemote);
				Git gitByStarter = objStarter.createGitObject();
				assertNotNull(gitByStarter);
				
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//B) "Noch Längere" Variante: Konfiguration übergeben
			Syso.printSection("B) Create Git-Object");
			try {
				JgitStarterHTTPS objStarter= new JgitStarterHTTPS();
				objStarter.configureGit(objConfigStarterRemote); 
				Git gitByStarter = objStarter.createGitObject();
				assertNotNull(gitByStarter);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//C) "Verkürzte" Variante: Konfiguration erst der Methode übergeben.
			Syso.printSection("C) Create Git-Object");
			try {
				JgitStarterHTTPS objStarter = new JgitStarterHTTPS();
				Git gitByStarter = objStarter.createGitObject(objConfigStarterRemote);
				assertNotNull(gitByStarter);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
		
	}
	
	public void testStarter_status_commit() {
		//Merke: Verwendet wird die gültige, im setup gefundene Konfiguration.
		try {
			//###################################################
			//Idee: Erstelle erst ein lokales Test-Repository. Auch mit HTTPS!!!
			//      Ändere darin eine Datei
			//      Führe den STATUS aus... sichere ihn, zum Vergleich.
			
			
			//      Führe den COMMIT aus
			
			//		Führe den STATUS erneut aus... vergleiche
			
			
			//1. Erstelle mit dem RepositoryManager ein neues Repo
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode
			File objFileDirectoryANew = new File(sDirectoryRepoA);			
			boolean bSuccess = false;
			
			//###################################################
			//1. A) "Längere" Variante: Konfiguration im Konstruktor übergeben
			JgitRepositoryManagerHTTPS objRepositoryManager = null;
			Syso.printSection("A) CloneRepositoryTo");
			try {					
				objRepositoryManager = new JgitRepositoryManagerHTTPS(objConfigRepoManager);					
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
			
			//2. Ändere im neuen lokalen Repository eine Datei
			TODOGOON20260723
			
			
			//3. Führe den STATUS aus... sichere ihn, zum Vergleich.
			
			//4. Führe den COMMIT aus
			
			//5. Führe den STATUS erneut aus... vergleiche
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}	
	}
	
//	public void testStarterRemote_ConfigureGit() {
//		
//		try {
//			//+++++++++++++++++
//			ConfigStarterRemote4TestJGIT objConfigRemote = new ConfigStarterRemote4TestJGIT();
//			
//			JgitStarterGIT objStarter = new JgitStarterGIT();
//			objStarter.configureGit(objConfigRemote);
//			Git gitByStarter = objStarter.getGitObject();
//		}catch(ExceptionZZZ ez){
//			fail("Method throws an exception." + ez.getMessageLast());
//		}	
//				
//		//++++++++++++++++++++++++++++++++++
//		
//	}
	
}