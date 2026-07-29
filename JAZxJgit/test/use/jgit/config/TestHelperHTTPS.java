package use.jgit.config;

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

public class TestHelperHTTPS extends TestHelper {
		
	public static IConfigRepositoryManagerJGIT findRepositoryManagerConfiguration_DefinedForEnvironmentCurrent() throws ExceptionZZZ{
		IConfigRepositoryManagerJGIT objConfigRepoManager=null;
		main:{
		//Für die unterschiedlichen Entwicklungsumgebungen die passende Konfiguration bereitstellen.
		//IDEE: ArrayList der möglichen Konfigurationsobjekte erstellen und durchgehen.
		List<IConfigRepositoryManagerJGIT> listConfig = new ArrayList<IConfigRepositoryManagerJGIT>();
		listConfig.add(new ConfigRepositoryManager4TestHTTPS_onAny());
		//listConfig.add(new ConfigRepositoryManager4TestHTTPS_onDEV04());
		//listConfig.add(new ConfigRepositoryManager4TestHTTPS_onTUBAF());
		
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
			ExceptionZZZ ez = new ExceptionZZZ("Konnte kein existierendes Basis Repository für eine Entwicklungsumgebung finden.", iERROR_RUNTIME, TestHelperHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		}//end main:
		return objConfigRepoManager;
	}
	
	public static IConfigStarterRemoteJGIT findStarterRemoteConfiguration_DefinedForEnvironmentCurrent() throws ExceptionZZZ{
		IConfigStarterRemoteJGIT objConfigStarterRemote=null;
		main:{
		//Für die unterschiedlichen Entwicklungsumgebungen die passende Konfiguration bereitstellen.
		//IDEE: ArrayList der möglichen Konfigurationsobjekte erstellen und durchgehen.		
		objConfigStarterRemote = new ConfigStarterRemote4TestHTTPS_onAnyA();
		
		/* 
		 * PROBLEM: Im Setup wurde das lokale Test-Repository gerade gelöscht. Darum ist es nicht da
		 *          Also hier darauf verlassen, das im Test das Repository korrekt erzeugt wird.
		List<IConfigStarterRemoteJGIT> listConfig = new ArrayList<IConfigStarterRemoteJGIT>();          
		listConfig.add(objConfigStarterRemote);
		//listConfig.add(new ConfigRepositoryManager4TestGIT_onDEV04());
		//listConfig.add(new ConfigRepositoryManager4TestGIT_onTUBAF());
		
		for(IConfigStarterRemoteJGIT objConfigRepoManagerTemp : listConfig) {
			String sRepoLocalBase = objConfigRepoManagerTemp.readRepositoryLocalBaseDirectory();
			Syso.println("Suche in dieser Entwicklungsumgebung das Basis Repository: '" + sRepoLocalBase + "'");
			if(FileEasyZZZ.exists(sRepoLocalBase)){
				Syso.println("Verwende in dieser Entwicklungsumgebung das Basis Repository: '" + sRepoLocalBase + "'");
				objConfigStarterRemote=objConfigRepoManagerTemp;
				break;
			}
		}
		
		
		if(objConfigStarterRemote==null) {
			ExceptionZZZ ez = new ExceptionZZZ("Konnte kein existierendes Basis Repository für eine Entwicklungsumgebung finden.", iERROR_RUNTIME, TestHelperGIT.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		*/
		}//end main:
		return objConfigStarterRemote;
	}
	
	public static IConfigStarterRemoteJGIT findStarterRemoteConfiguration_DefinedForRepositoryBaseLocal(File objFileBaseLocalProject) throws ExceptionZZZ{
		IConfigStarterRemoteJGIT objConfigStarterRemote=null;
		main:{		
			/* 
			 * PROBLEM: Im Setup wurde das lokale Test-Repository gerade gelöscht. Darum ist es nicht da
			 *          Also hier darauf verlassen, das im Test das Repository korrekt erzeugt wird.
			 */         
			if(objFileBaseLocalProject==null) {
				ExceptionZZZ ez = new ExceptionZZZ("File Objekt für lokale Repo-Projekt Verzeichnis nicht übergeben.", iERROR_PARAMETER_MISSING, TestHelperGIT.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sRepoBaseLocalUsed = objFileBaseLocalProject.getAbsolutePath();			
			if(!FileEasyZZZ.exists(objFileBaseLocalProject)) {
				ExceptionZZZ ez = new ExceptionZZZ("File Objekt für lokales Repo. Basis Verzeichnis existiert nicht: '" + sRepoBaseLocalUsed + "'", iERROR_PARAMETER_MISSING, TestHelperGIT.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
						
			//Für die unterschiedlichen Entwicklungsumgebungen die passende Konfiguration bereitstellen.
			//IDEE: ArrayList der möglichen Konfigurationsobjekte erstellen und durchgehen.
			List<IConfigStarterRemoteJGIT> listConfig = new ArrayList<IConfigStarterRemoteJGIT>();
			objConfigStarterRemote = new ConfigStarterRemote4TestHTTPS_onAnyA();			
			listConfig.add(objConfigStarterRemote);
			
			objConfigStarterRemote = new ConfigStarterRemote4TestHTTPS_onAnyB();			
			listConfig.add(objConfigStarterRemote);
			//listConfig.add(new ConfigRepositoryManager4TestHTTPS_onDEV04());
			//listConfig.add(new ConfigRepositoryManager4TestHTTPS_onTUBAF());
		
			Syso.println("Suche in dieser Entwicklungsumgebung das passende Konfigurationsobjekt für das das Basis Repository: '" + sRepoBaseLocalUsed + "'");
			for(IConfigStarterRemoteJGIT objConfigRepoManagerTemp : listConfig) {
				String sRepoBaseLocalTemp = objConfigRepoManagerTemp.readRepositoryLocalBaseDirectory();
				String sRepoBaseLocalProjectTemp = FileEasyZZZ.joinFilePathName(sRepoBaseLocalTemp, objConfigRepoManagerTemp.readRepositoryProjectName());
				
				String sRepoBaseLocalProjectUsed = FileEasyZZZ.joinFilePathName(sRepoBaseLocalUsed, objConfigRepoManagerTemp.readRepositoryProjectName());
				if(sRepoBaseLocalProjectUsed.equals(sRepoBaseLocalProjectTemp)) {
					Syso.println("Passendes Konfigurationsobject gefunden für das Basis Repository Projekt: '" + sRepoBaseLocalProjectUsed + "'");
					objConfigStarterRemote=objConfigRepoManagerTemp;
					break;
				}
			}
		
			if(objConfigStarterRemote==null) {
				ExceptionZZZ ez = new ExceptionZZZ("Konnte kein passendes Konfigurationsobjekt finden für das in dieser Entwicklungsumgebung existierende Basis Repository '" + sRepoBaseLocalUsed + "'", iERROR_RUNTIME, TestHelperGIT.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}	
		}//end main:
		return objConfigStarterRemote;
	}
}
