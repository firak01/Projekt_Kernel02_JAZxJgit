package test.jgit.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.FileTextReplacerZZZ;
import basic.zBasic.util.machine.EnvironmentZZZ;
import basic.zBasic.util.system.Syso;
import basic.zWin32.com.wmi.WMIZZZ;
import use.jgit.JgitStarterMain;

public class TestHelper implements IConstantZZZ, ITestHelperConstant {
	
	public static boolean removeRepositoriesLocalAtoC_onSetup() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			boolean bSuccess = false; boolean bRunning = false;
			
			//Merke: In meiner WinXP Umgebung muss zuvor der TGitCache - Prozess beendet werden.
			//Dauerhafter wäre: https://stackoverflow.com/questions/16773257/how-can-i-stop-and-start-tgitcache-exe-gracefully
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
			File objFileDirectoryANew = new File(sDirectoryRepoBaseA);
			if(FileEasyZZZ.exists(objFileDirectoryANew)) {
				bSuccess = FileEasyZZZ.removeDirectory(objFileDirectoryANew, true, true);
				if(!bSuccess) {
					ExceptionZZZ ez = new ExceptionZZZ("Konnte Verzeichnis nicht löschen: '" + sDirectoryRepoBaseA + "'", iERROR_RUNTIME, TestHelper.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
					
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
			File objFileDirectoryBNew = new File(sDirectoryRepoBaseB);
			if(FileEasyZZZ.exists(objFileDirectoryBNew)) {
				bSuccess = FileEasyZZZ.removeDirectory(objFileDirectoryBNew, true, true);
				if(!bSuccess) {
					ExceptionZZZ ez = new ExceptionZZZ("Konnte Verzeichnis nicht löschen: '" + sDirectoryRepoBaseB + "'", iERROR_RUNTIME, TestHelper.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
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
			File objFileDirectoryCNew = new File(sDirectoryRepoBaseC);
			if(FileEasyZZZ.exists(objFileDirectoryCNew)) {
				bSuccess = FileEasyZZZ.removeDirectory(objFileDirectoryCNew, true, true);
				if(!bSuccess) {
					ExceptionZZZ ez = new ExceptionZZZ("Konnte Verzeichnis nicht löschen: '" + sDirectoryRepoBaseB + "'", iERROR_RUNTIME, TestHelper.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}	
			
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	
	public static boolean modifyTestFileFor(RepositoryContext ctx, String sFileNameIn, String sConnectionType) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			String sFileName = null;
			if(StringZZZ.isEmpty(sFileNameIn)) {
				ExceptionZZZ ez = new ExceptionZZZ("Dateiname", iERROR_PARAMETER_MISSING, TestHelper.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else {
				sFileName = sFileNameIn;
			}
			
			
			String sDate = DateTimeZZZ.computeTimestampStringFormatedDefault(); 
			String sMachine = EnvironmentZZZ.getHostName();			
			String sLine = sDate + " " + sMachine + " " + "per JunitTest (" + sConnectionType + ") generiert.";
			
			String sProjectName = ctx.getRepositoryProjectName();
			if(StringZZZ.isEmpty(sProjectName)) {
				ExceptionZZZ ez = new ExceptionZZZ("Projektname nicht im Context.", iERROR_RUNTIME, TestHelper.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}

			//Hole den geclonten Repository-Pfad
			File objFileRepoBaseLocalNew = ctx.getRepositoryBaseDirectory(); //in der Clone-Methode wird das Projekt geholt... , sRepositoryProject);
			if(objFileRepoBaseLocalNew==null) {
				ExceptionZZZ ez = new ExceptionZZZ("RepositoryBaseDirectory nicht im Context.", iERROR_PROPERTY_MISSING, TestHelper.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			boolean bSuccess = FileEasyZZZ.exists(objFileRepoBaseLocalNew);
			if(bSuccess) {
				Syso.println("Repository existiert nun: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'");
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Repository existiert nicht: '" + objFileRepoBaseLocalNew.getAbsolutePath() + "'", iERROR_RUNTIME, TestHelper.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
				
			
			//Hole unterhalb des geklonten Repository-Pfads das Arbeitsverzeichnis oder erstelle es
			String sFilePathRepositoryProject = FileEasyZZZ.joinFilePathName(objFileRepoBaseLocalNew, sProjectName);
			String sFilePathDirectory = FileEasyZZZ.joinFilePathName(sFilePathRepositoryProject, "Test_repo_JAZxJgit\\Arbeit_mit_Git");
			FileEasyZZZ.createDirectory(sFilePathDirectory); //sicher ist sicher. Ohne das Verzeichnis kann der Stream nicht erstellt werden.
			
			//Hole den Pfad der zu verändernden Datei
			String sFilePathTotal = FileEasyZZZ.joinFilePathName(sFilePathDirectory, sFileName);
			
			//VERARBEITE DIE DATEI:
			//DAFÜER GIBT ES GRUNDSÄTZLICH MEHRERE MÖGLICHKEITEN, DIE HIER AUSKOMMENTIERT SIND:
			
			//Nette Gelegenheit diese FileText Bearbeiter zu testen und weiterzuentwickeln...
			//FileTextAppenderZZZ objApender = new FileTextAppenderZZZ(sFilePathTotal);
			//objApender.appendAndSave(sLine);
			
			//FileTextInserterZZZ objInserter = new FileTextInserterZZZ(sFilePathTotal);
			//a) objInserter.insertBeforeAndSave(0, sLine);
			//b) objInserter.insertBehindAndSave(0, sLine);
			
			//FileTextPrependerZZZ objPrepender = new FileTextPrependerZZZ(sFilePathTotal);
			//objPrepender.prependAndSave(sLine); 

			//... aber besser ist es immer nur 1 Zeile zu generieren... auch später für Konflikte... in der gleichen Zeile halt.
			//FileTextWriterZZZ objWriter = new FileTextWriterZZZ(sFilePathTotal);
			//objWriter.writeLine(sLine);
				
			FileTextReplacerZZZ objReplacer = new FileTextReplacerZZZ(sFilePathTotal);
			objReplacer.replaceAndSave(0, sLine);
						
			bReturn = true;
		}//end main:
		return bReturn;
	}
}
