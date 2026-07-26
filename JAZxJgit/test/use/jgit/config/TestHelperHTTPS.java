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
		List<IConfigStarterRemoteJGIT> listConfig = new ArrayList<IConfigStarterRemoteJGIT>();
		objConfigStarterRemote = new ConfigStarterRemote4TestHTTPS_AonAny();
		
		/* 
		 * PROBLEM: Im Setup wurde das lokale Test-Repository gerade gelöscht. Darum ist es nicht da
		 *          Also hier darauf verlassen, das im Test das Repository korrekt erzeugt wird.
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
}
