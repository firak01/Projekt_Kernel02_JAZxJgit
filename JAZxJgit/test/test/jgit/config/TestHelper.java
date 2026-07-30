package test.jgit.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.system.Syso;
import basic.zWin32.com.wmi.WMIZZZ;
import use.jgit.JgitStarterMain;

public class TestHelper implements IConstantZZZ, ITestHelperConstant {
	public static boolean removeRepositoriesLocal_onSetup() throws ExceptionZZZ{
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
			
			bReturn = true;
		}//end main:
		return bReturn;
	}
}
