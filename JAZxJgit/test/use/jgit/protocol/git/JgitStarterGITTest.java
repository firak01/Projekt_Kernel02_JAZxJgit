package use.jgit.protocol.git;

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
import use.jgit.config.ConfigRepositoryManager4TestJGIT_onDEV04;
import use.jgit.config.ConfigRepositoryManager4TestJGIT_onTUBAF;
import use.jgit.config.ConfigStarterLocal4TestJGIT;
import use.jgit.config.ConfigStarterRemote4TestJGIT;
import use.jgit.config.IConfigRepositoryManagerJGIT;
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
				boolean bSuccess = false; boolean bRunning = false;
				
				//Merke: In meiner WinXP Umgebung muss zuvor der TGitCache - Prozess beendet werden.
				WMIZZZ objWmi = new WMIZZZ();
				bRunning = objWmi.isProcessRunning("TGitCache.exe");
				if(bRunning) {
					objWmi.killProcessAll("TGitCache.exe");
				}
				
				//Lösche ggfs. in einem vorherigen Test erstellte Lokale Repositories.
				//- Darin sind vielleicht Änderungen drin, die nicht mehr gewünscht sind.
				//- Erst wenn die Verzeichnisse weg sind, können sie neu gecloned werden.
				//
				//Lösche also alle Inhalte und Unterverzeichnissse: true, true
				File objFileDirectoryANew = new File(sDirectoryRepoA);
				if(FileEasyZZZ.exists(objFileDirectoryANew)) {
					bSuccess = FileEasyZZZ.removeDirectory(objFileDirectoryANew, true, true);
					if(!bSuccess) {
						fail("Konnte Verzeichnis nicht löschen: '" + sDirectoryRepoA + "'");
					}
				}
				
				//#########################################################################

				//Merke: In meiner WinXP Umgebung muss zuvor der TGitCache - Prozess beendet werden.
				bRunning = objWmi.isProcessRunning("TGitCache.exe");
				if(bRunning) {
					objWmi.killProcessAll("TGitCache.exe");
				}
				
				//Lösche ggfs. in einem vorherigen Test erstellte Lokale Repositories.
				//- Darin sind vielleicht Änderungen drin, die nicht mehr gewünscht sind.
				//- Erst wenn die Verzeichnisse weg sind, können sie neu gecloned werden.
				//
				//Lösche also alle Inhalte und Unterverzeichnissse: true, true
				File objFileDirectoryBNew = new File(sDirectoryRepoB);
				if(FileEasyZZZ.exists(objFileDirectoryBNew)) {
					bSuccess = FileEasyZZZ.removeDirectory(objFileDirectoryBNew, true, true);
					if(!bSuccess) {
						fail("Konnte Verzeichnis nicht löschen: '" + sDirectoryRepoB + "'");
					}
				}	
				
				//Für die unterschiedlichen Entwicklungsumgebungen die passende Konfiguration bereitstellen.
				//IDEE: ArrayList der möglichen Konfigurationsobjekte erstellen und durchgehen.
				List<IConfigRepositoryManagerJGIT> listConfig = new ArrayList<IConfigRepositoryManagerJGIT>();
				listConfig.add(new ConfigRepositoryManager4TestJGIT_onDEV04());
				listConfig.add(new ConfigRepositoryManager4TestJGIT_onTUBAF());
				
				for(IConfigRepositoryManagerJGIT objConfigRepoManagerTemp : listConfig) {
					String sRepoLocalBase = objConfigRepoManagerTemp.readRepositoryLocalBaseDirectory();
					Syso.println("Suche in dieser Entwicklungsumgebung das Basis Repository: '" + sRepoLocalBase + "'");
					if(FileEasyZZZ.exists(sRepoLocalBase)){
						Syso.println("Verwende in dieser Entwicklungsumgebung das Basis Repository: '" + sRepoLocalBase + "'");
						objConfigRepoManager=objConfigRepoManagerTemp;
						break;
					}
				}
				
				if(objConfigRepoManager==null) {
					fail("Konnte kein existierendes Basis Repository für eine Entwicklungsumgebung finden.");
				}
											
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
	
	public void testManager_A_cloneRepositoryTo() {
		try {
			//Merke: Im Setup werden die Repositories wieder aufgeräumt.
			//       Darum jede "Variante" in einer eigenen test-Methode
			File objFileDirectoryANew = new File(sDirectoryRepoA);
			File objFileDirectoryBNew = new File(sDirectoryRepoB);
			
			//###################################################
			//A) "Längere" Variante: Konfiguration im Konstruktor übergeben
			Syso.printSection("A) CloneRepositoryTo");
			try {					
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT(objConfigRepoManager);										
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryANew);									
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryBNew);
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
			File objFileDirectoryANew = new File(sDirectoryRepoA);
			File objFileDirectoryBNew = new File(sDirectoryRepoB);
			
			//###################################################
			//B) "Noch Längere" Variante: Konfiguration übergeben
			Syso.printSection("B) CloneRepositoryTo");
			try {
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT();
				objRepositoryManager.configureGit(objConfigRepoManager); 
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryANew);									
				objRepositoryManager.cloneRepositoryTo(objFileDirectoryBNew);					
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
			File objFileDirectoryANew = new File(sDirectoryRepoA);
			File objFileDirectoryBNew = new File(sDirectoryRepoB);
			
			//###################################################
			//C) "Verkürzte" Variante: Konfiguration erst der Methode übergeben.
			Syso.printSection("C) CloneRepositoryTo");
			try {
				JgitRepositoryManagerGIT objRepositoryManager = new JgitRepositoryManagerGIT();
				objRepositoryManager.cloneRepositoryTo(objConfigRepoManager, objFileDirectoryANew);									
				objRepositoryManager.cloneRepositoryTo(objConfigRepoManager, objFileDirectoryBNew);				
			}catch(ExceptionZZZ ez){
				fail("Method throws an exception." + ez.getMessageLast());
			}			
					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}
	
	
	
	public void testResolverLocal_ConfigureGit() {
		
		try{
			
			//+++++++++++++++++
			ConfigStarterLocal4TestJGIT objConfigLocal = new ConfigStarterLocal4TestJGIT();
			
			JgitResolverLocalGIT objResolver = new JgitResolverLocalGIT();
			objResolver.configureGit(objConfigLocal);
			Git gitByResolver = objResolver.getGitObject();
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
		
		//++++++++++++++++++++++++++++++++++
		
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