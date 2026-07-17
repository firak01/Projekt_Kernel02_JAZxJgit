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
import use.jgit.config.ConfigStarterRemote4TestJGIT;
import use.jgit.manager.JgitRepositoryManagerZZZ;

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
			try {
				ConfigStarterRemote4TestJGIT objConfig = new ConfigStarterRemote4TestJGIT();
				
				JgitRepositoryManagerZZZ objRepostitoryManager = new JgitRepositoryManagerZZZ();
				objRepostitoryManager.configureGit(objConfig);
				
				
				JgitStarterGIT objStarterGIT = new JgitStarterGIT();
				objStarterGIT.configureGit(objConfig);
				
				Git git = objStarterGIT.getGitObject();
			
//				File remoteRepoDir = new File(sTempDir, "remote.git");
//				Git.init()
//				   .setBare(true)
//				   .setDirectory(remoteRepoDir)
//				   .call();
				
				String sLocalA = "C:\\temp\\RepoA";
				File localARepoDir = new File(sLocalA);
				Git.cloneRepository()
				   .setURI(remoteRepoDir.toURI().toString())
				   .setDirectory(localARepoDir)
				   .call();
	
				String sLocalB = "C:\\temp\\RepoB";
				File localBRepoDir = new File(sLocalB);
				Git.cloneRepository()
				   .setURI(remoteRepoDir.toURI().toString())
				   .setDirectory(localBRepoDir)
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
	}catch(ExceptionZZZ ez){
		fail("Method throws an exception." + ez.getMessageLast());
	}
	
	}//END setup
	
	public void tearDown() throws Exception {
		
	}
	
	
	
	//###################################################
	//Die Tests		
	public void testContructor(){
		
		try{
				//+++ Hier wird ein Fehler erwarte
				KernelReaderHtmlZZZ objReaderInit = new KernelReaderHtmlZZZ();
				boolean btemp = objReaderInit.getFlag("init");
				assertTrue("Unexpected: The init-Flag was expected to be set", btemp);
			
				//+++ This is not correct when using the test object
				btemp = objReaderTest.getFlag("init");
				assertFalse("Unexpected: The init flag was expected NOT to be set", btemp);
				
				//+++ Nun eine Log-Ausgabe (Notes-Log)
				AbstractKernelLogZZZ objKernelLog = objReaderTest.getLogObject();
				assertNotNull(objKernelLog);				
				objKernelLog.write("succesfully created");
					
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());			
		}
	}//END testConstructor
	
	public void testSearchElementAll(){
		try{
			
			ArrayList alsReturn = new ArrayList();
			org.jdom.Element elem = null;
			String stemp; String stemp2;
			
			//#### INPUT - TAG
			TagTypeInputZZZ tagInputType = new TagTypeInputZZZ(objKernel);
			objReaderTest.searchElementAll(null, tagInputType, alsReturn);
			
			assertEquals(1, alsReturn.size());
			
			
			//Nun noch testen, ob das Element auch den korrekten Namen hat
			elem = (org.jdom.Element) alsReturn.get(0);
			stemp = tagInputType.getTagName();
			stemp2 = elem.getName();
			assertTrue(stemp2.equalsIgnoreCase(stemp));
			
			//#### ZHTML - Tag
			alsReturn.clear();
			TagTypeZHtmlZZZ tagZHtmlType = new TagTypeZHtmlZZZ(objKernel);
			objReaderTest.searchElementAll(null, tagZHtmlType, alsReturn);
			
			assertEquals(2, alsReturn.size());
			
			
			//Nun noch testen, ob die Element auch den korrekten Namen haben
			stemp = tagZHtmlType.getTagName();
			for(int icount=0; icount <= alsReturn.size()-1; icount++){
				elem = (org.jdom.Element) alsReturn.get(icount);					
				stemp2 = elem.getName();
				assertTrue("unkorrekter Name: Bei der Such nach '" + stemp + "' wurde ein Element des Tags '" + stemp2 + "' gefunden.", stemp2.equalsIgnoreCase(stemp));
			}
			
			
			//#### WERT des ZHTML - Tags
			elem = (org.jdom.Element) alsReturn.get(0);		
			
			TagZHtmlZZZ objTagZ = new TagZHtmlZZZ(objKernel, tagZHtmlType, elem);
			stemp = objTagZ.readValue();
			assertEquals("ipexternal", stemp);
			
			
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());			
		}			
	}
	
	public void testReadValueZHtml(){
		try{
			ArrayList alsReturn = new ArrayList();
			org.jdom.Element elem = null;
			String stemp; 

			TagTypeZHtmlZZZ tagZHtmlType = new TagTypeZHtmlZZZ(objKernel);
			objReaderTest.searchElementAll(null, tagZHtmlType, alsReturn);
			assertTrue(alsReturn.size() >= 0);
			
			//#### WERT des ZHTML - Tags
			elem = (org.jdom.Element) alsReturn.get(0);		
			
			TagZHtmlZZZ objTagZ = new TagZHtmlZZZ(objKernel, tagZHtmlType, elem);
			stemp = objTagZ.readValue();
			assertEquals("ipexternal", stemp);
			
			
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());			
		}			
		
	}
	
	
	
	public void testReadEncoding(){
		try{
			String sEncoding = objReaderTest.readEncodingUsed(null);
			assertEquals("ISO-8859-1", sEncoding);
			
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());			
		}			
		
	}
	 
}