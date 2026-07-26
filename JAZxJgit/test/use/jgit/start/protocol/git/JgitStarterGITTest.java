package use.jgit.start.protocol.git;

import java.io.File;

import org.eclipse.jgit.api.Git;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.FileTextWriterZZZ;
import basic.zBasic.util.machine.EnvironmentZZZ;
import basic.zBasic.util.system.Syso;
import junit.framework.TestCase;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.config.ITestHelperConstant;
import use.jgit.config.TestHelper;
import use.jgit.config.TestHelperGIT;
import use.jgit.config.TestHelperHTTPS;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.manage.protocol.https.JgitRepositoryManagerHTTPS;
import use.jgit.start.protocol.https.JgitStarterHTTPS;

public class JgitStarterGITTest extends TestCase{
	private static String sDirectoryRepoBaseA=ITestHelperConstant.sDirectoryRepoBaseA;
	private static String sDirectoryRepoBaseB=ITestHelperConstant.sDirectoryRepoBaseB;
	
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
			objConfigRepoManager = TestHelperGIT.findRepositoryManagerConfiguration_DefinedForEnvironmentCurrent();			
			
			//3. RepositoryManger für die Erstellung von TestRepositories holen
			objConfigStarterRemote = TestHelperGIT.findStarterRemoteConfiguration_DefinedForEnvironmentCurrent();
												
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
				JgitStarterGIT objStarter = new JgitStarterGIT(objConfigStarterRemote);
				Git gitByStarter = objStarter.createGitObject();
				assertNotNull(gitByStarter);
				
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//B) "Noch Längere" Variante: Konfiguration übergeben
			Syso.printSection("B) Create Git-Object");
			try {
				JgitStarterGIT objStarter= new JgitStarterGIT();
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
				JgitStarterGIT objStarter = new JgitStarterGIT();
				Git gitByStarter = objStarter.createGitObject(objConfigStarterRemote);
				assertNotNull(gitByStarter);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
		
	}
	
	/**Idee: Erstelle erst ein lokales Test-Repository. Auch mit GIT!!!
    Ändere darin eine Datei
    Führe den STATUS aus... sichere ihn, zum Vergleich.

    Führe den COMMIT aus

	Führe den STATUS erneut aus... vergleiche 
*/
	public void testStarter_status_commit() {
		//Merke: Verwendet wird die gültige, im setup gefundene Konfiguration.
		try {
			Syso.printSection("status_commit");			
			
			//###################################################
			//1. A) "Längere" Variante: Konfiguration im Konstruktor übergeben
			JgitRepositoryManagerGIT objRepositoryManager = null;
			Syso.printSection("A) CloneRepositoryTo");
			try {					
				objRepositoryManager = new JgitRepositoryManagerGIT(objConfigRepoManager);					
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
		
			String sRepositoryProject = objRepositoryManager.getRepositoryProject();
			File objFileDirectoryANew = new File(sDirectoryRepoBaseA, sRepositoryProject);			
			boolean bSuccess = false;
			
			try {
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryANew);	
				bSuccess = FileEasyZZZ.exists(objFileDirectoryANew);
				assertTrue("Repository existiert nicht: '" + objFileDirectoryANew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileDirectoryANew.getAbsolutePath() + "'");			
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//2. Ändere im neuen lokalen Repository eine Datei
			String sDate = DateTimeZZZ.computeTimestampStringFormatedDefault(); 
			String sMachine = EnvironmentZZZ.getHostName();
			String sLine = sDate + " " + sMachine + " " + "per JunitTest (GIT) generiert.";
			
			String sFilePathDirectory = FileEasyZZZ.joinFilePathName(objFileDirectoryANew, "Test_repo_JAZxJgit\\Arbeit_mit_Git");
			FileEasyZZZ.createDirectory(sFilePathDirectory); //ohne das Verzeichnis kann der Stream nicht erstellt werden.
			
			String sFilePathTotal = FileEasyZZZ.joinFilePathName(sFilePathDirectory, "test01.txt");
			FileTextWriterZZZ objWriter = new FileTextWriterZZZ(sFilePathTotal);
			objWriter.writeLine(sLine);
						
			//3. Führe den STATUS aus... sichere ihn, zum Vergleich.
			JgitStarterGIT objStarter = new JgitStarterGIT(objConfigStarterRemote);
			objStarter.statusit();
			String sStatusXmlPreCommit = objStarter.getStatusStringXml();
			Syso.println(sStatusXmlPreCommit);
			Syso.printSeparator('x');
			
			//4. Führe den COMMIT aus
			bSuccess = objStarter.commitit("commit by JUInitTest");
			assertTrue("Commit nicht erfolgreich", bSuccess);
			System.out.println("commit erfolgreich");			
			Syso.printSeparator('x');
			
			//5. Führe den STATUS erneut aus... vergleiche
			objStarter.statusit();			
			
			String sStatusXmlPostCommit = objStarter.getStatusStringXml();
			Syso.println(sStatusXmlPostCommit);
			boolean bValue = sStatusXmlPostCommit.equals(sStatusXmlPreCommit);
			assertFalse("Status wert hat sich trotz commit nicht verändert", bValue);
			
			
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