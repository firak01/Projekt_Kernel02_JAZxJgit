package use.jgit.start.protocol.git;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.TransportException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.system.Syso;
import test.jgit.config.AbstractJgitGITTest;
import test.jgit.config.ITestHelperConstant;
import test.jgit.config.RepositoryContext;
import test.jgit.config.TestHelperGIT;
import test.jgit.config.TestRepositoryFactoryGIT;
import use.jgit.config.IConfigJGIT;

/**
 	Konfigurationen, je nach Entwicklungsumgebung eine andere

	 * Aufbau einer Repository Struktur:
	  
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
			//0.2. RepositoryStarter-Konfiguration ausgehend vom neuen, geclonten Repository holen/suchen und zuweisen
			//			configStarter = TestHelperGIT.findStarterRemoteConfiguration_DefinedForRepositoryBaseLocal(objFileRepoBaseLocalNew);
			//
			//I			JgitStarterGIT objStarter = new JgitStarterGIT(configStarter);
			
 * @author Fritz Lindhauer
 *
 */
public class JgitStarterGITTest extends AbstractJgitGITTest{
	private static String sDirectoryRepoBaseA=ITestHelperConstant.sDirectoryRepoBaseA;
	private static String sDirectoryRepoBaseB=ITestHelperConstant.sDirectoryRepoBaseB;
	private static String sDirectoryRepoBaseC=ITestHelperConstant.sDirectoryRepoBaseC;


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
			Syso.printSection("createGit");
			
			//###################################################
			//1. Erstelle mit dem RepositoryManager ein neues Repo
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode								
			RepositoryContext ctx = TestRepositoryFactoryGIT.createCloneA(configRepositoryManager);
									
			boolean bSuccess = false;
			File objFileRepoBaseLocalNew = new File(sDirectoryRepoBaseA); //in der Clone-Methode wird das Projekt geholt... , sRepositoryProject);			
			try {
				bSuccess = FileEasyZZZ.exists(objFileRepoBaseLocalNew);
				assertTrue("Repository existiert nicht: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'");			
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//Zwar liefert das Context-Object auch den Starter direk. 
			//Hier wollen wir aber die unterschiedlichen Varianten des Konstruktors testen.
			IJgitStarterGIT objStarter = null;
			
			//A) "Längere" Variante: Konfiguration im Konstruktor übergeben
			Syso.printSection("A) Create Git-Object");
			try {					
				objStarter = new JgitStarterGIT(configStarter);
				Git gitByStarter = objStarter.createGitObject();
				assertNotNull(gitByStarter);
				
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//B) "Noch Längere" Variante: Konfiguration übergeben
			Syso.printSection("B) Create Git-Object");			
			try {
				objStarter= new JgitStarterGIT();
				objStarter.configureGit(configStarter); 
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			try {
				Git gitByStarter = objStarter.createGitObject();
				assertNotNull(gitByStarter);
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			//###################################################
			//C) "Verkürzte" Variante: Konfiguration erst der Methode übergeben.
			Syso.printSection("C) Create Git-Object");
			try {
				objStarter = new JgitStarterGIT();
				Git gitByStarter = objStarter.createGitObject(configStarter);
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
			//1. Erstelle mit dem RepositoryManager ein neues Repo
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode								
			RepositoryContext ctx = TestRepositoryFactoryGIT.createCloneB(configRepositoryManager);
								
			boolean bSuccess = false;
			File objFileRepoBaseLocalNew = new File(sDirectoryRepoBaseB); //in der Clone-Methode wird das Projekt geholt... , sRepositoryProject);			
			try {
				bSuccess = FileEasyZZZ.exists(objFileRepoBaseLocalNew);
				assertTrue("Repository existiert nicht: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'");			
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}

			//0.3. Ändere im neuen lokalen Repository eine Datei, berücksichtige den Repository Pfad.
			try {
				bSuccess = TestHelperGIT.modifyTestFile(ctx, "test01.txt");
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
		
			//+++++++++++++++++++++++++++++++++++++++++++++++
			//3. Führe den STATUS aus... sichere ihn, zum Vergleich.
			IJgitStarterGIT objStarter = (IJgitStarterGIT) ctx.getStarter();
			objStarter.setProjectStartingName(IConfigJGIT.sPROJECT_NAME);
			objStarter.statusit();
			String sStatusXmlPreCommit = objStarter.getStatusStringXml();
			Syso.println(sStatusXmlPreCommit);
			Syso.printSeparator('x');
			
			//++++++++++++++++++++++++++++++++++++++++++++++++
			//4. Führe den COMMIT aus
			Syso.printSection("commit");
						
			bSuccess = objStarter.commitit("commit (GIT) by JUInitTest");
			assertTrue("Commit nicht erfolgreich", bSuccess);
			System.out.println("commit erfolgreich");			
			Syso.printSeparator('x');
			
			//5. Führe den STATUS erneut aus... vergleiche
			objStarter.statusit();			
			
			String sStatusXmlPostCommit = objStarter.getStatusStringXml();
			Syso.println(sStatusXmlPostCommit);
			boolean bValue = sStatusXmlPostCommit.equals(sStatusXmlPreCommit);
			assertFalse("Status wert hat sich unerwartet trotz commit nicht verändert", bValue);
			
			
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
	public void testStarter_status_commit_push() {
		//Merke: Verwendet wird die gültige, im setup gefundene Konfiguration.
		try {
			Syso.printSection("status_commit_push");			
			
			//###################################################
			//1. Erstelle mit dem RepositoryManager ein neues Repo
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode
			RepositoryContext ctx = TestRepositoryFactoryGIT.createCloneC(configRepositoryManager);
					
			boolean bSuccess = false;			
			File objFileRepoBaseLocalNew = new File(sDirectoryRepoBaseC); //in der clone Methode wird das Projekt geholt... , sRepositoryProject); //B !!!			
			try {								
				bSuccess = FileEasyZZZ.exists(objFileRepoBaseLocalNew);
				assertTrue("Repository existiert nicht: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'", bSuccess);
				Syso.println("Repository existiert nun: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'");			
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
							
			//0.3. Ändere im neuen lokalen Repository eine Datei, berücksichtige den Repository Pfad.
			try {
				bSuccess = TestHelperGIT.modifyTestFile(ctx, "test01.txt");
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}
			
			
			//+++++++++++++++++++++++++++++++++++++++++++++++
			//3. Führe den STATUS aus... sichere ihn, zum Vergleich.
			//Hole erst einmal die für das neu erstellte lokale Repo die passende RemoteStarter Konfiguration				
			IJgitStarterGIT objStarter = (IJgitStarterGIT) ctx.getStarter();
			objStarter.setProjectStartingName(IConfigJGIT.sPROJECT_NAME);
			objStarter.statusit();
			String sStatusXmlPreCommit = objStarter.getStatusStringXml();
			Syso.println(sStatusXmlPreCommit);
			Syso.printSeparator('x');
			
			//+++++++++++++++++++++++++++++++++++++++++++++++
			//4. Führe den COMMIT aus
			Syso.printSection("commit");
			
			bSuccess = objStarter.commitit("commit (GIT) by JUInitTest");
			assertTrue("Commit nicht erfolgreich", bSuccess);
			System.out.println("commit erfolgreich");			
			Syso.printSeparator('x');
			
			//++++++++++++++++++++++++++++++++++++++++++++++
			//5. Führe den STATUS erneut aus... vergleiche
			objStarter.statusit();			
			
			String sStatusXmlPostCommit = objStarter.getStatusStringXml();
			Syso.println(sStatusXmlPostCommit);
			boolean bValue = sStatusXmlPostCommit.equals(sStatusXmlPreCommit);
			assertFalse("Status wert hat sich unerwartet trotz commit nicht verändert", bValue);
			
			//++++++++++++++++++++++++++++++++++++++++++++++
			//6. Pushe den commit zum Remote Repository
			Syso.printSeparator('x');
			try {
				Syso.printSection("push");
				bSuccess = objStarter.pushit();
				assertTrue("Push nicht erfolgreich", bSuccess);
				
				String sStatusXmlPostPush = objStarter.getStatusStringXml();
				Syso.println(sStatusXmlPostPush);
				bValue = sStatusXmlPostPush.equals(sStatusXmlPostCommit);
				assertTrue("Status wert hat sich unerwartet wg. push verändert", bValue);
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CheckoutConflictException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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