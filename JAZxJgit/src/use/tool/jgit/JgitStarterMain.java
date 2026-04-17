package use.tool.jgit;

import java.util.HashMap;

import javax.ws.rs.NotSupportedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.JGitInternalException;

import com.google.gson.Gson;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.ArrayListZZZ;
import basic.zBasic.util.abstractList.HashMapUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.flag.json.FlagContainerZZZ;
import use.tool.jgit.ConfigJGIT;
import use.tool.jgit.https.JgitStarterHTTPS;
import use.tool.jgit.ssh.JgitStarterSSH;

public class JgitStarterMain implements IConstantZZZ{

	/**
	//#######################################
	//### Pfad zum certifier - store, je nach Arbeitsumgebung ist das ein anderer.
	//### Diesen in der Startkonfiguration der JVM setzen
	//### von DEV04
	//### -Djavax.net.ssl.trustStore=C:\java\jdk1.8.0\jre\lib\security\cacerts  -Djavax.net.ssl.trustStorePassword=changeit
	//###
	//### von ERMANARICH / TUBAF
	//### -Djavax.net.ssl.trustStore=C:\java\jdk1.8.0_202\jre\lib\security\cacerts  -Djavax.net.ssl.trustStorePassword=changeit
    //###
	//#########################################
	
	//#########################################
	//### Syntax der Batch-Zeile, für den Aufruf
	//###  -Djavax.net.ssl.trustStore=C:\java\jdk1.8.0\jre\lib\security\cacerts ^
	//###   -Djavax.net.ssl.trustStorePassword=changeit ^
	//###   -cp JgitStarter.jar JgitStarterMain %1
	//###
	//##########################################
	
	//#######################################################
	//### Es gibt:
	//### a) Verschiedene lokale Repos, je nachdem welches Eclipse/welche Entwicklungsumgebung
	//### b) Der RemoteAlias kann ggfs. anders definiert worden sein. Normalfall scheint "origin" zu sein.
	//### Der zu verwendenden Name steht in der Datei .git\config
	//###	 z.B.:
	//###	[remote "JAZDummy"]
	//###	url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	//###	fetch = +refs/heads/*:refs/remotes/JAZDummy/*
	//###
	//######################################################
	
	//#######################################################
	//Repository Kombinationen fuer TESTS								
	//Merke; Der RemoteAlias wird auch lokal definiert in .git\config
	
	//A) auf TUBAF - HISinOne Eclipse:
	//RepositoryLocal	C:\\HIS-Workspace\\1fgl\\repo\\Eclipse202312\\HIS_QISSERVER_FGL
	//RepositoryRemote	SSH
	//RepositoryRemote	HTTPS			
	//RemoteAlias     	"origin";					
	
	//B) auf TUBAF (Oxygen Version) für Z-Kernel Entwicklung
	//RepositoryLocal 	C:\\HIS-Workspace\\1fgl\\repo\\EclipseOxygen\\HIS_QISSERVER_FGL	
	//RepositoryRemote	SSH
	//RepositoryRemote	HTTPS			
	//RemoteAlias     	"origin";
	
	//C) auf Ermanarich, der HISinOne Tomcat
	//RepositoryLocal	C:\\repo\\Eclipse202312\\HIS_QISSERVER_FGL
	//RepositoryRemote	SSH
	//RepositoryRemote	HTTPS			
	//RemoteAlias		"origin";
	
	//D) Zur Entwicklung (auf DEV04), ein Dummy Verzeichnis
	//RepositoryLocal				C:\\1fgl\\repo\\EclipseOxygen_V01\\Projekt_Kernel02_JAZDummy 
	//RepositoryRemote	SSH			git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	//RepositoryRemote	HTTPS		https://github.com/firak01/Projekt_Kernel02_JAZDummy.git	
	//RemoteAlias					JAZDummy
	//man braucht die remote repository angabe nicht..... liegt in .git/config Datei, unter dem Alias ....... 
	//aber dort ist vielleicht ein anderes Protokoll definiert
	//also Konsolenstring:			-https https://github.com/firak01/Projekt_Kernel02_JAZDummy.git -rl C:\1fgl\repo\EclipseOxygen_V01\Projekt_Kernel02_JAZDummy
	//also Konsolenstring:			-ssh -ra JAZDummy -rl C:\1fgl\repo\EclipseOxygen_V01\Projekt_Kernel02_JAZDummy
	
	//E) Zur Entwicklung (auf ERMANARICH), ein Dummy Verzeichnis
	//RepositoryLocal	C:\\1fgl\\repo\\EclipseOxygen\\Projekt_Kernel02_JAZDummy");
	//RepositoryRemote	SSH
	//RepositoryRemote	HTTPS			
	//RemoteAlias		"origin";
	  		
	//########################################################
	//### Daraus ergeben sich z.B. folgende Kommandozeilenaufrufe, teilweise mit Angabe des "Personal Acces Token" (PAT)
    //AUF DEV04
	//PULL HTTPS: -pull -https -rr https://github.com/firak01/Projekt_Kernel02_JAZDummy.git -pat <PAT> -rl C:\1fgl\repo\EclipseOxygen_V01\Projekt_Kernel02_JAZDummy -z {'IGNORE_CHECKOUT_CONFLICTS':true}
	//PULL SSH	: -pull -ssh -rra JAZDummy -rl C:\1fgl\repo\EclipseOxygen_V01\Projekt_Kernel02_JAZDummy
	//
	//PUSH HTTPS: -push -https -rr https://github.com/firak01/Projekt_Kernel02_JAZDummy.git -pat <PAT> -rl C:\1fgl\repo\EclipseOxygen_V01\Projekt_Kernel02_JAZDummy
	//PUSH SSH	: -push -ssh -rra JAZDummy -rl C:\1fgl\repo\EclipseOxygen_V01\Projekt_Kernel02_JAZDummy
	//
	//
	//AUF ERMANARRICH (Andere lokalen Verzeichnisse, Keine Aliasnamen fuer die Projekte in .git\config Datei)
	//PULL HTTPS: -pull -https -rr https://github.com/firak01/Projekt_Kernel02_JAZDummy.git -pat <PAT> -rl C:\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy -z {'IGNORE_CHECKOUT_CONFLICTS':true}
	//PULL SSH	: -pull -ssh -rra origin -rl C:\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy -z {'IGNORE_CHECKOUT_CONFLICTS':true}
	//PUSH HTTPS: -push -https -rr https://github.com/firak01/Projekt_Kernel02_JAZDummy.git -pat <PAT> -rl C:\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy 
	//PUSH SSH	: -push -ssh -rra origin -rl C:\1fgl\repo\EclipseOxygen\Projekt_Kernel02_JAZDummy 
	
	 */
	
	/*
	Kommandozeilenparamater fuer den Einsatz:
	a) PUSH
	-push -https -pat <PAT> 
	-rr https://github.com/firak01/HIS_QISSERVER_FGL.git
	-rl C:\HIS-Workspace\1fgl\repo\EclipseOxygen\HIS_QISSERVER_FGL
	
	b) PULL
	
	 */
	 
	 
	 /*
	 * @param args
	 * @author Fritz Lindhauer, 22.03.2026, 07:01:47
	 */
	public static void main(String[] args) {
		//siehe: https://www.baeldung.com/jgit
		//siehe: https://www.vogella.com/tutorials/JGit/article.html
		//siehe: https://medium.com/autotrader-engineering/working-with-git-in-java-part-1-a-jgit-tutorial-bc03b404a517
		
		try {	
			//Umgebungsvariablen an die Methode des konkreten Projekts durchreichen
			//Sie sind pro Maschine/Eclipse Instanz ggfs. unterschiedlich
			//Nicht vergessen: Diese Umgebungsvariablen werden NUR beim Eclipsestart(!) im entsprechenden Starter gesetzt.
			System.out.println("Vorhandene Umgebungsvariablen, seit Eclipsestart:");
			System.out.println(System.getenv("MY_TRUSTSTORE"));
			System.out.println(System.getenv("sPATZZZ"));
			System.out.println(System.getenv("sRLZZZ"));
			System.out.println(System.getenv("sRRHZZZ"));		
			System.out.println(System.getenv("sRRACZZZ"));
			
			
			String sAction=null;
			ArrayListZZZ<String>listasAction = new ArrayListZZZ<String>();
						
			//Trotz Einbinden von  in pom.xml Fehlermeldung;
			//ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console
			//Lösung dazu:
			//https://stackoverflow.com/questions/47881821/error-statuslogger-log4j2-could-not-find-a-logging-implementation
			//TODOGOON20260310;//jetzt wird eine logdatei all.log im Root des Projektordners angelegt. Das ist schlecht/unnoetig für GIT. Dort weg.
			System.setProperty("log4j.configurationFile","./use/tool/jgit/log/log4j2.xml");
			
			//Logger log = LogManager.getLogger(this.getClass().getName());		
			Logger log = LogManager.getLogger();
			
			//wg Fehler: Caused by: javax.net.ssl.SSLException: Received fatal alert: protocol_version
			//Github benoetigt TLS Version 1.2 mindestens (kann sogar von WinXP bereitgestellt werden).
			//System.setProperty("https.protocols", "TLSv1");		
			System.setProperty("https.protocols", "TLSv1.2"); 
						
			//### Argumente entgegenzunehmen
			ConfigJGIT objConfig = new ConfigJGIT(args);
			
			//+++++++++++++++++++++++++++++++++
			//actions
			sAction = objConfig.readActionPull();
			if(!StringZZZ.isEmpty(sAction))listasAction.add(sAction);
			
			sAction = objConfig.readActionPush();
			if(!StringZZZ.isEmpty(sAction))listasAction.add(sAction);
			
			if(listasAction.isEmpty()) {
				ExceptionZZZ ez = new ExceptionZZZ("Action", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//++++++++++++++++++++++++++++++++
			//-z  Flags:
			//Per Konsole uebergeben:  -z {"IGNORE_CHECKOUT_CONFLICTS":true}
			HashMap<String,Boolean> hmFlag = null;
			
			//MERKE: Das wird schon beim initialiseren von ConfigDEV gemacht. 
			//       Von dort dann über .getHashMapFlagZPassed holen 
						
			
			//Experiment mit FlagContainerZZZ als Objekt, also aus dem JSON ein Objekt machen
			//Vielleicht einmal eine Option mit unterschiedlichen Objekten zu arbeiten.
			//a) Das Füllen des FlagContainers
			FlagContainerZZZ objFlagContainer = null;					
			String sFlagZJson = "{\"HmFlag\":{\"XYZ\":true,\"abc\":true}}"; //Merke FlagContainerZZZ hat ein public Objekt: HmFlag
			if(!StringZZZ.isEmpty(sFlagZJson)) {
				Gson gson = new Gson();			
				objFlagContainer = gson.fromJson(sFlagZJson, FlagContainerZZZ.class);			
			}						
			
			//Experiment mit FlagContainerZZZ als Objekt, also aus dem JSON ein Objekt machen
			//b) Das Auslesen des FlagContainers und ggfs. uebergebene Flags setzen:
			if(objFlagContainer!=null) {
				HashMap<String,Boolean> hmFlagByContainer = objFlagContainer.getHmFlag();				
				for(int i=0; i< hmFlagByContainer.size(); i++) {
					String sFlagName = (String) HashMapUtilZZZ.getKeyByIndex(hmFlagByContainer, i);
					Boolean boolFlagValue = hmFlagByContainer.get(sFlagName);
					boolean bFlagValue = boolFlagValue.booleanValue();
					
					System.out.println(i + ". HmFlagByContainer: " + sFlagName + ": " + bFlagValue);					
				}
			}
		
			//++++++++++++++++++++++++++++++++
			//Steuerung der Verbindung
			String sConnectionType = objConfig.readConnectionType();
			if(StringZZZ.isEmpty(sConnectionType)){
				ExceptionZZZ ez = new ExceptionZZZ("Verbindungstyp", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			//Unterschiedliche Wege: 
			//-https oder -ssh
			//mit jeweils unterschiedlichem Remote Repository
			//PAT nur bei HTTPS notwendig
			
			boolean bReturn = false;				
			switch(sConnectionType) {
			case"ssh":								
										
				//##############################################################
				//Starte die passende Klasse mit der passenden Methode
				JgitStarterSSH objStarterSSH = new JgitStarterSSH();
				
				//Ggfs. uebergebene Flags setzen
				hmFlag = objConfig.getHashMapFlagPassed();
				if(hmFlag!=null) {
				for(int i=0; i< hmFlag.size(); i++) {
					String sFlagName = (String) HashMapUtilZZZ.getKeyByIndex(hmFlag, i);
					Boolean boolFlagValue = hmFlag.get(sFlagName);
					boolean bFlagValue = boolFlagValue.booleanValue();
					objStarterSSH.setFlag(sFlagName, bFlagValue);
				}
				}
				
				for(String sActionTemp : listasAction) {				
					switch(sActionTemp) {
					case "pull":
						bReturn = objStarterSSH.pullit(objConfig);
						break;
					case "push":
						bReturn = objStarterSSH.pushit(objConfig);						
						break;
					default:
						ExceptionZZZ ez = new ExceptionZZZ("Action not available", iERROR_PARAMETER_VALUE, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					if(!bReturn) {
						ExceptionZZZ ez = new ExceptionZZZ("Action '" + sActionTemp + "' was not successful.", iERROR_RUNTIME, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;						
					}
				}
				break;
				
			case "https":
				
				//##############################################################
				//Starte die passende Klasse mit der passenden Methode
				JgitStarterHTTPS objStarterHTTPS = new JgitStarterHTTPS();
	
				//Ggfs. uebergebene Flags setzen
				hmFlag = objConfig.getHashMapFlagPassed();
				if(hmFlag!=null) {
				for(int i=0; i< hmFlag.size(); i++) {
					String sFlagName = (String) HashMapUtilZZZ.getKeyByIndex(hmFlag, i);
					Boolean boolFlagValue = hmFlag.get(sFlagName);
					boolean bFlagValue = boolFlagValue.booleanValue();
					objStarterHTTPS.setFlag(sFlagName, bFlagValue);
				}
				}
				
				for(String sActionTemp : listasAction) {				
					switch(sActionTemp) {
					case "pull":
						bReturn = objStarterHTTPS.pullit(objConfig);
						break;
					case "push":
						bReturn = objStarterHTTPS.pushit(objConfig);						
						break;
					default:
						ExceptionZZZ ez = new ExceptionZZZ("Action not available", iERROR_PARAMETER_VALUE, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					if(!bReturn) {
						ExceptionZZZ ez = new ExceptionZZZ("Action '" + sActionTemp + "' was not successful.", iERROR_RUNTIME, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;						
					}
				}
				break;
				
			default:
				ExceptionZZZ ez = new ExceptionZZZ("Nicht behandelter Verbindungstyp '" + sConnectionType + "'", iERROR_PARAMETER_VALUE, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JGitInternalException e) {
			e.printStackTrace();
		} catch (NotSupportedException e) {
			e.printStackTrace();
		}
		
	}
	
	

}
