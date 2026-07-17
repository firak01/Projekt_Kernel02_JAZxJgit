package use.jgit.config;

import java.util.ArrayList;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.GetOptZZZ;
import basic.zKernel.config.help.IKernelConfigHeaderLineZZZ;
import basic.zKernel.config.help.IKernelConfigHelpLineZZZ;
import basic.zKernel.config.help.KernelConfigHeaderLineZZZ;
import basic.zKernel.config.help.KernelConfigHelpLineZZZ;
import use.jgit.protocol.git.JgitStarterGIT;
import use.jgit.protocol.https.JgitStarterHTTPS;
import use.jgit.protocol.ssh.JgitStarterSSH;

public abstract class AbstractConfigStarterRemoteJGIT extends AbstractConfigStarterLocalJGIT implements IConfigStarterRemoteJGIT{
	
	public AbstractConfigStarterRemoteJGIT() throws ExceptionZZZ {
		super();		
	}

	public AbstractConfigStarterRemoteJGIT(String[] saArg) throws ExceptionZZZ {
		super(saArg);
	}

	//### aus IConfigStarterRemoteJGIT
	//siehe dort...
	@Override
	public String getPatternStringDefault() throws ExceptionZZZ{
		return IConfigStarterRemoteJGIT.sPATTERN_DEFAULT;
	}
	
	@Override
	public String[] getArgumentArrayDefault() throws ExceptionZZZ{
		String[] saArg = new String[8];
		saArg[0] = "-pull";
		saArg[1] = "-git";	//Merke: aus dem lokalen Repository, in der Datei .git\config kommt die remote URL 		 
		saArg[2] = "-rra";   //       dazu ist der Remote Alias wichtig, per Default ist das "origin", kann aber auch anders benannt werden.
		saArg[3] = "origin";
		saArg[4] = "-rl";
		saArg[5] = "."; //statt IConfigStarterRemoteJGIT.sPROJECT_PATH;  //Das eigene Projekt als Default
		saArg[6] = "-z";
		saArg[7] = this.getConfigFlagzJsonDefault();
	
		return saArg;
	}
	
	//######################################
	//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
		
	//### aus IConfigZZZ
	@Override
	public String getProjectDirectory() throws ExceptionZZZ {
		return IConfigJGIT.sPROJECT_DIRECTORY;
	}
	
	@Override
	public String getProjectName() throws ExceptionZZZ {
		return IConfigJGIT.sPROJECT_NAME;
	}
	
	//Merke 20260615: Besser eine Liste von Hilf-Objekt-Zeilen auch kein Enum, der Ansatz mit der einfachen Liste der Objekte läßt sich einfacher 
	//                über mehrere Projekte und Vererbungstrukturen umsetzen
	//Also nicht so etwas nutzen wie:
	//public enum LOGSTRINGFORMAT implements IEnumSetMappedStringFormatZZZ{		
	//            und darin:    STRINGTYPE01_STRING_BY_STRING("stringtype01",IStringFormatZZZ.iFACTOR_STRINGTYPE01_STRING_BY_STRING, IStringFormatZZZ.sSEPARATOR_PREFIX_DEFAULT + "[A01]", "%s",IStringFormatZZZ.iARG_STRING,  "[/A01]" + IStringFormatZZZ.sSEPARATOR_POSTFIX_DEFAULT, "Gib den naechsten Log String - sofern vorhanden - in diesem Format aus."),			
	@Override
	public List<IKernelConfigHelpLineZZZ>getHelpList() throws ExceptionZZZ{
		ArrayList<IKernelConfigHelpLineZZZ>listaReturn=new ArrayList<IKernelConfigHelpLineZZZ>();
		main:{
			//Berücksichtige dabei die Paramter aus den "Pattern" Strings
			//final static String sPATTERN4GIT_REMOTE_DEFAULT="help|h|status|commit|comment:rl:project:" + 
				//JgitStarterSSH.sPROTOCOL +"|" + JgitStarterHTTPS.sPROTOCOL + "|" + JgitStarterGIT.sPROTOCOL +"|" + "pull|fetch|push|commitPush|pat:rrh:rra:rrac:branch:";
			IKernelConfigHeaderLineZZZ objHeaderLine= new KernelConfigHeaderLineZZZ("Argumente aus: " + this.getProjectName());
				
			IKernelConfigHelpLineZZZ objHelp=null;
			objHelp = new KernelConfigHelpLineZZZ();
			objHelp.setHeaderLine(objHeaderLine);
			listaReturn.add(objHelp);
			
			objHeaderLine= new KernelConfigHeaderLineZZZ("Argumente aus: " + IConfigStarterRemoteJGIT.sPROJECT_NAME);		
			
			objHelp = new KernelConfigHelpLineZZZ("ssh","SSH Protokol","Nutze das SSH Protokol in der URL für Aktionen. (noch todo)");
			objHelp.setHeaderLine(objHeaderLine);
			listaReturn.add(objHelp);
			
			objHelp = new KernelConfigHelpLineZZZ("https","HTTPS Protokol","Nutze das HTTPS Protokol in der URL für Aktionen.");
			listaReturn.add(objHelp);	
			objHelp = new KernelConfigHelpLineZZZ("git","GIT Protokol","Nutze das GIT Protokol in der URL für Aktionen.");
			listaReturn.add(objHelp);	
			
			//Diese werden auch im ConflictResolver genutzt
			objHelp = new KernelConfigHelpLineZZZ("status","Status","Status des rl: Repositories");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("conflict","Konfliktauflösung","Löse den Konflikt in der angegebenen Datei auf, brücksichtige dabei per Flag übergebene Strategien.");
			listaReturn.add(objHelp);	
			objHelp = new KernelConfigHelpLineZZZ("commit","Commit","Änderungen an das lokale Repository übertragen");
			listaReturn.add(objHelp);	
			objHelp = new KernelConfigHelpLineZZZ("conflictCommit","Konflikt und Commit","Löse den Konflikt in der angegebenen Datei auf, brücksichtige dabei per Flag übergebene Strategien UND sofort die Änderungen an das lokale Repository übertragen");
			listaReturn.add(objHelp);	
			objHelp = new KernelConfigHelpLineZZZ("comment:","Kommentar","Kommentar für den Commit");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("rl:","Dateipfad","Pfad zum lokalen Repository");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("project:","Projekt","Name des Projekts im Repository");
			listaReturn.add(objHelp);
					
			//Diese sind dann nur fuer RemoteStarter
			//pull|fetch|push|commitPush|pat:rrh:rra:rrac:branch:";
			objHelp = new KernelConfigHelpLineZZZ("fetch","Fetch","Gleiche den Stand des lokalen Repositories mit dem remote Repository des Projekts ab");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("pull","Pull","Hole den Stand des remote Repositories in das lokale Repository des Projekts");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("push","Push","Schiebe den Stand des lokalen Repositories in das remote Repository des Projekts");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("CommitPush","CommitPush","Mache lokal den Commit und Schiebe den Stand des lokalen Repositories in das remote Repository des Projekts");
			listaReturn.add(objHelp);
			
			objHelp = new KernelConfigHelpLineZZZ("pat:","PersonalAccessToken","Fuer HTTPS, von GitHub vergeben. Wird aus einer Umgebungsvariablen geholt.");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("rrh:","RemoteRepositoryHost","Name des Host, z.B. github.com. Wird aus einer Umgebungsvariablen geholt.");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("rra:","RemoteRepositoryAlias","Aliasname des Repository. Siehe lokale git Konfiguration, Datei config. Z.B. Hier im Abschnitt [remote \"origin\"]\". Dann ist origin der Alias." );
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("rrac:","RemoteRepositoryAccount","Account fuer die Anmeldung und auch Teil der URL. Wird aus einer Umgebungsvariablen geholt.");
			listaReturn.add(objHelp);
			objHelp = new KernelConfigHelpLineZZZ("branch:","Branch","Branch im Repository. Möglich ist auch '*' fuer alle, bzw. den ersten gefundenen.");
			listaReturn.add(objHelp);
			
		}//end main:
		return listaReturn;
	}
	
	//### aus IConfigJGIT
	@Override
	public String getCommentDefault() throws ExceptionZZZ {
		return "";
	}
	
	//### aus IConfigStarterRemoteJGIT
	//siehe dort...
	@Override
	public String readActionPull() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("pull");			
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String readActionPush() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("push");			
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String readActionCommitPush() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("commitPush");			
		}//end main:		
		return sReturn;
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String getConnectionTypeDefault() throws ExceptionZZZ{
		return "ssh";
	}
	@Override
	public String readConnectionType() throws ExceptionZZZ{
		String sReturn = null;
		main:{
			boolean bReturn = false;
			
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			bReturn = this.isConnectionTypeGIT();
			if(bReturn) {
				sReturn = JgitStarterGIT.sPROTOCOL;
				break main;
			}
			
			bReturn = this.isConnectionTypeHTTPS();
			if(bReturn) {
				sReturn = JgitStarterHTTPS.sPROTOCOL;
				break main;
			}
			
			bReturn = this.isConnectionTypeSSH();
			if(bReturn) {
				sReturn = JgitStarterSSH.sPROTOCOL;
				break main;
			}
			
			sReturn = this.getConnectionTypeDefault();			
		}//end main:		
		return sReturn;
	}
	
	@Override
	public boolean isConnectionTypeSSH() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sReturn = objOpt.readValue(JgitStarterGIT.sPROTOCOL);	
			if(!StringZZZ.isEmpty(sReturn)) bReturn = true;
			
		}//end main:		
		return bReturn;
	}
	
	@Override
	public boolean isConnectionTypeHTTPS() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sReturn = objOpt.readValue(JgitStarterHTTPS.sPROTOCOL);
			if(!StringZZZ.isEmpty(sReturn)) bReturn = true;
			
		}//end main:		
		return bReturn;
	}
	
	@Override
	public boolean isConnectionTypeGIT() throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sReturn = objOpt.readValue(JgitStarterGIT.sPROTOCOL);	
			if(!StringZZZ.isEmpty(sReturn)) bReturn = true;
			
		}//end main:		
		return bReturn;
	}
		
		
		//++++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String readRepositoryRemoteHost() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sHost = objOpt.readValue("rrh");
			if(StringZZZ.isEmpty(sHost)) {
				sHost = this.getRepositoryRemoteHostDefault();				
			}
			
			sReturn = sHost;
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String getRepositoryRemoteHostDefault() throws ExceptionZZZ{
		return "github.com";
	}
	
	@Override
	public String readRepositoryRemoteAccount() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			String sHost = objOpt.readValue("rrac");
			if(StringZZZ.isEmpty(sHost)) {
				sHost = this.getRepositoryRemoteAccountDefault();				
			}
			
			sReturn = sHost;
		}//end main:		
		return sReturn;
	}
	
	@Override
	public String getRepositoryRemoteAccountDefault() throws ExceptionZZZ{
		return "firak01";
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++
	@Override
	public String getPersonalAccessTokenDefault() {
		return ""; //Merke: GitHub verweigert das PUSHEN eines PATs durch sein Regelwerk!!!
	}
	@Override
	public String readPersonalAccessToken() throws ExceptionZZZ{
		String sReturn = null;
		main:{
			GetOptZZZ objOpt = this.getOptObject();
			if(objOpt==null) break main;
			if(objOpt.getFlag("isLoaded")==false) break main;
			
			sReturn = objOpt.readValue("pat");
			if(sReturn==null){
				sReturn = this.getPersonalAccessTokenDefault();
			}
		}//end main:		
		return sReturn;
	}
}
