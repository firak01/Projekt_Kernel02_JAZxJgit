package use.jgit.protcol.git;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.SshSessionFactory;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractArray.ArrayUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import use.jgit.AbstractJgitStarterRemote;
import use.jgit.IJgitEnabledZZZ;
import use.jgit.JgitStarterMain;
import use.jgit.common.IMergeResultResolvedZZZ;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.protocol.https.JgitStarterHTTPS;
import use.jgit.resolve.EnumSetMappedStrategyMergeConflictUtilZZZ;
import use.jgit.resolve.IJgitResolverEnabled;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;
import use.jgit.tool.merge.GitPostMergeAnalyse;
import use.jgit.tool.merge.ResultPostMergeAnalysis;
import use.jgit.tool.push.GitPostPushAnalyse;
import use.jgit.tool.push.ResultPostPushAnalysis;
import use.jgit.util.JgitUtilGIT;
import use.jgit.util.JgitUtilHTTPS;
import use.jgit.util.JgitUtilZZZ;



/**Klasse heisst GIT, weil sie den GIT Port verwendet.
 * Die URLs lauten dann aber mit dem Protocol git
 * @author Fritz Lindhauer
 *
 * @param <T>
 */
public class JgitStarterGIT<T> extends AbstractJgitStarterRemote<T> implements IJgitStarterGIT{
	private static final long serialVersionUID = 521157607363069534L;
	public static final String sPROTOCOL="git";
	
	//### Konstruktor
	public JgitStarterGIT() {	
		super();			
	}
	
	//### aus IJgitStarterRemote
	@Override 
	public String getRepositoryRemoteProtocol() throws ExceptionZZZ {
		return JgitStarterGIT.sPROTOCOL;
	}
	//!!! Kein Setter
	
	@Override
	public String computeRepositoryBaseRemote(String sHost, String sAccount) throws ExceptionZZZ{
		return JgitUtilGIT.computeRepositoryUrlBaseGIT(sHost, sAccount);
	}
	
	@Override
	public String getRepositoryTotalRemote() throws ExceptionZZZ {		
		if( this.sRepositoryTotalRemote==null) {
			String sHost = this.getRepositoryRemoteHost();
			String sAccount = this.getRepositoryRemoteAccount();						
			String sRepositoryProjectRemote = this.getRepositoryProject();	
			if(StringZZZ.isEmpty(sHost) || StringZZZ.isEmpty(sAccount) || StringZZZ.isEmpty(sRepositoryProjectRemote)) return null;
			this.sRepositoryTotalRemote = JgitUtilGIT.computeRepositoryUrlTotalGIT(sHost, sAccount, sRepositoryProjectRemote);			
		}
		return this.sRepositoryTotalRemote;
	}
	
	/** Ohne ein IConfig - Objekt als Argument, muss alles aus den Properties des Objekts gelesen werden.
	 * @return
	 * @throws ExceptionZZZ
	 */
	@Override
	public boolean configureGit() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
		
			//Konfiguriere JGit für GIT
			
			//+++ Zugriff sicherstellen
			//0) SshSessionFactory ... mit den verwendeten Ids, Pfaden, etc.
			JGitGitConfigZZZ.configure();
			System.out.println("Verwendete Ssh Session Factory: " + SshSessionFactory.getInstance().getClass());
				
			
			//B) Konfiguriere das lokale Repository und init Git-Object (nach demm Remote Repository, da die Daten des Remote Repository ggfs. in das Lokale Repository uebernommen werden)
			//a) + b)
			bReturn = super.configureGit();

			
			//Die Remote Repository Einstellungen in der Jeweiligen Klasse des Protokolls machen
			//A) Remote (zuerst, weil die Einstellungen in die Konfiguration des Lokalen Repositories uebenommen werden.
			//a) Remote Basis Url
			String sDirectoryRepositoryRemote = this.getRepositoryBaseRemote();
			if(StringZZZ.isEmpty(sDirectoryRepositoryRemote)) {
				//ExceptionZZZ ez = new ExceptionZZZ("Remote Repository Basis URL, Angabe fehlt: '" + sDirectoryRepositoryRemote + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				//throw ez;
				
				//Versuch dies über den Alias zu ermitteln
				String sRepositoryRemoteAlias = this.getRepositoryRemoteAlias();
				if(StringZZZ.isEmpty(sRepositoryRemoteAlias)){
					ExceptionZZZ ez = new ExceptionZZZ("Alias vom Remote Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				String sUrlGITorHTTPS = this.searchRepositoryRemote(sRepositoryRemoteAlias);
				sDirectoryRepositoryRemote = JgitUtilGIT.computeRepositoryUrlPartFromUrlGIT(sUrlGITorHTTPS);
			}
			if(StringZZZ.isEmpty(sDirectoryRepositoryRemote)) {
				ExceptionZZZ ez = new ExceptionZZZ("Weder Url direkt angegeben noch per Alias '" + sRepositoryRemoteAlias + "' ermittelbar.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			this.setRepositoryBaseRemote(sDirectoryRepositoryRemote);	
			
			//b) Remote Repository-Verzeichnis des Projekts
			String sRepositoryProjectRemote = this.getRepositoryProject(); //momentan identisch mit lokal)
			if(StringZZZ.isEmpty(sRepositoryProjectRemote)) {
				ExceptionZZZ ez = new ExceptionZZZ("Projektname der remote Repositories, Angabe fehlt: '" + sRepositoryProjectRemote + "'", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		
			//Das ist umso wichtiger, weil mit HTTPS Url wird ein Credentials Provider erwartet.
			//Den gibt es für GIT aber nicht... 
			//Darum muss die URL zum verwendeten Protokol stimmen.
			String sRepositoryBaseRemote = null;
			if(JgitUtilZZZ.isUrlHTTPS(sDirectoryRepositoryRemote)) {
				String sAccount = JgitUtilHTTPS.getAccountFromUrl(sDirectoryRepositoryRemote);
				String sHost = JgitUtilHTTPS.getHostFromUrl(sDirectoryRepositoryRemote);	
				sRepositoryBaseRemote = JgitUtilGIT.computeRepositoryUrlBaseGIT(sHost, sAccount);				
			}else {
				sRepositoryBaseRemote = sDirectoryRepositoryRemote;
			}
			this.setRepositoryBaseRemote(sRepositoryBaseRemote);
			
			String sRepositoryTotalRemote = JgitUtilGIT.computeRepositoryUrlTotalGIT(sRepositoryBaseRemote, sRepositoryProjectRemote);
			this.setRepositoryTotalRemote(sRepositoryTotalRemote);
				
			
			//+++ GIT Zugriff sicherstellen
			//Merke: Es gibt keinen Credentials Provider für GIT.
			//Bei GIT muss man sich auf die korrekte ssh URL verlassen
			//Übergibt man eine HTTPS URL kommt die Fehlermeldung:
			//basic.zBasic.ExceptionZZZ: org.eclipse.jgit.api.errors.TransportException: https://github.com/firak01/Projekt_Kernel02_JAZDummy.git: Authentication is required but no CredentialsProvider has been registered

		}//end main:
		return bReturn;
	}
	
	
	//##################################################
	//###### PULL ######################################
	@Override 
	public boolean pullit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
						
				//######################################################
				//Konfiguriere JGit für GIT				
				boolean bSuccessConfigureGit = this.configureGit(objConfig);
				if(bSuccessConfigureGit) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				//#####################################################################################
				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / GIT / SSH)				
				//######################################################################################				
				
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen		
				String sRepositoryRemoteHostIn = objConfig.readRepositoryRemoteHost();
				if(StringZZZ.isEmpty(sRepositoryRemoteHostIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote Host und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
									
				//Wird der Accountname überhaupt gebraucht bei GIT? JA, zum neuen Ausrechnen der URL
				String sRepositoryRemoteAccountIn = objConfig.readRepositoryRemoteAccount();
				if(StringZZZ.isEmpty(sRepositoryRemoteHostIn)){
					ExceptionZZZ ez = new ExceptionZZZ("Accountname", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//######################################################################################
				//+++ Folgende Konfiguration könnten aus dem Alias und dem Repository geholt werden
				String sConnectionTypeIn = objConfig.readConnectionType();
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
						//Diese Detail aus der .git\config Datei unter dem Alias auslesen.
						String sDirectoryRepositoryLocalRemote = this.getRepositoryTotalRemote();
						if(StringZZZ.isEmpty(sDirectoryRepositoryLocalRemote)) {
							ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
						
						sConnectionTypeIn = JgitUtilZZZ.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);
				}
				//Falls immer noch leer, Fehler!
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
					ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
																		
				//+++++++++++++++++++++++								
				this.setConnectionType(sConnectionTypeIn);
				this.setRepositoryRemoteHost(sRepositoryRemoteHostIn);
				this.setRepositoryRemoteAccount(sRepositoryRemoteAccountIn);
								
				String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote GIT Repository und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setRepositoryBaseRemote(sRepositoryRemoteIn);
				
				//###########################################
				//Keine Besonderheit für GIT (also kein sPAT wie bei HTTPS) an dieser Stelle.
				
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++
				//Mache den pull	
				Git git = this.getGitObject();
		        boolean bSuccessPull = this.pullit(git);
		        if(bSuccessPull) {
					System.out.println("pullit erfolgreich");
				}else {
					System.out.println("pullit NICHT erfolgreich");
					break main;
				}
		        git.close();
		        bReturn = true;
		        //#######################################################	  
			
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;			
			}
		}//end main:
		return bReturn;
	}
		
	@Override
	public boolean pullit(Git git) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		boolean bReturn = false;
		main:{
			CredentialsProvider credentialsProvider = this.getCredentialsProviderObject();			
			String sRepositoryRemoteTotal = this.getRepositoryTotalRemote();
			
			String sBranch = "master";
			String sBranchIn = this.getRepositoryBranch();
			if(!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch = sBranchIn;
				
			boolean bIgnoreConflicts = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.MERGE_IGNORE_CHECKOUT_CONFLICTS);	
			boolean bAutosolveConflicts = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.MERGE_AUTOSOLVE_CHECKOUT_CONFLICTS);

			//Zum Testen gezielt steuern
			//bIgnoreConflicts = false;
			//bAutosolveConflicts = false;
			if (!bIgnoreConflicts & !bAutosolveConflicts) {
				//Normaler Pull, Konflikte ausgeben, nicht auflösen
				//wir wollen aber immer den bestimmten Branch... this.pullit(git, credentialsProvider, sPAT, sRepoRemote);				
				bReturn = this.pullit(git, credentialsProvider, sRepositoryRemoteTotal, sBranch);
				
			} else if(bIgnoreConflicts) { 
				
				//Konflikte Ignorieren. Die Konfliktdateien werden gezielt zurückgesetzt
				//Hier wird keine Strategie mehr berücksichtig.				
				bReturn = this.pullitIgnoreCheckoutConflicts(git, credentialsProvider, sRepositoryRemoteTotal, sBranch);
		
			} else if(!bIgnoreConflicts & bAutosolveConflicts) {
				
				//Statt so etwas zu machen, das Enum für das entsprechende Flag übergeben:
				//boolean bUseStrategyMergeConflictsOurs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
				//boolean bUseStrategyMergeConflictsTheirs = this.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
				STRATEGYMERGECONFLICT objEnumStrategyMergeConflict = EnumSetMappedStrategyMergeConflictUtilZZZ.getStrategyChoosenByFlag(this);
				
				//Versuchen die Konflikte aufzulösen, ggfs. noch per Strategie, gesteuert durch weitere FLAGZLOCAL
				bReturn = this.pullitResolveCheckoutConflicts(git, credentialsProvider, sRepositoryRemoteTotal, sBranch, objEnumStrategyMergeConflict);
			
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Unerwartet FlagKombination beim PULL.", iERROR_PARAMETER_VALUE, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		boolean bReturn = false;
		main:{			
			MergeResult objMergeResult = JgitUtilGIT.pullGIT(git, credentialsProvider, sRepoRemote);
			if(objMergeResult==null) {
				System.out.println("Kein Merge durchgeführt/Kein MergeResult-Objekt. Vorbedingungen für ein sauberes Repository nicht erfüllt. Bitte (wenn vorhanden) Lösungsvorschläge probieren.");
				break main;
			}
			
			MergeStatus objMergeStatus = objMergeResult.getMergeStatus();
			bReturn = objMergeStatus.isSuccessful();
			if(bReturn) break main;
			
			//Falls Merge nicht erfolgreich ist, hier am Schluss die Dateien mit den Konflikten auflisten
			System.out.println("##### MERGE: GGFS. NICHT ZU BEHEBENDE KONFLIKTE #######");
			boolean bAnyConflict = JgitUtilZZZ.logConflicts(objMergeResult);
			if(!bAnyConflict) {
				System.out.println("* KEINE KONFLIKTE");
			}
			System.out.println();//Trennzeile zwischen den Ausgaben
			bReturn = !bAnyConflict;
			if(bReturn) break main;
			
			System.out.println("##### MERGE: ANALYSE UND GGFS. LOESUNGSVORSCHLAEGE #######");
			ResultPostMergeAnalysis objAnalyseResult = GitPostMergeAnalyse.analyzeMergeResult(objMergeResult);
			objAnalyseResult.printReport();
			
			
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullit(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		boolean bReturn = false;
		main:{			
			MergeResult objMergeResult = null;
			
			//anders als bei HTTPS gibt es hier auch die Möglichkeite direkt zu pull
			//Was aber eigentlich technisch umständlicher ist.
			boolean bUsePullDirect = this.getFlagLocal(IJgitStarterGITEnabled.FLAGZLOCAL.USE_PULL_DIRECT);
			objMergeResult = JgitUtilGIT.pullGIT(git, credentialsProvider, sRepoRemote, sBranch, !bUsePullDirect);
			
			
			if(objMergeResult==null) {
				System.out.println("Kein Merge durchgeführt/Kein MergeResult-Objekt. Vorbedingungen für ein sauberes Repository nicht erfüllt. Bitte (wenn vorhanden) Lösungsvorschläge probieren.");
				break main;
			}
			
			MergeStatus objMergeStatus = objMergeResult.getMergeStatus();
			bReturn = objMergeStatus.isSuccessful();
			if(bReturn) break main;
			
			//Falls Merge nicht erfolgreich ist, hier am Schluss die Dateien mit den Konflikten auflisten
			System.out.println("##### MERGE: GGFS. NICHT ZU BEHEBENDE KONFLIKTE #######");
			boolean bAnyConflict = JgitUtilZZZ.logConflicts(objMergeResult);
			if(!bAnyConflict) {
				System.out.println("* KEINE KONFLIKTE");
			}
			System.out.println();//Trennzeile zwischen den Ausgaben
			bReturn = !bAnyConflict;
			if(bReturn) break main;			
			
			System.out.println("##### MERGE: ANALYSE UND GGFS. LOESUNGSVORSCHLAEGE #######");
			ResultPostMergeAnalysis objAnalyseResult = GitPostMergeAnalyse.analyzeMergeResult(objMergeResult);
			objAnalyseResult.printReport();
			
			
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean pullitIgnoreCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{		
			//Merke: Bei GIT gibt es einen direkten PULL-Befehl oder die Kombination aus FETCH + MERGE
			//       FETCH + MERGE ist eigentlich optimaler als direkt.
			//Der Weg ist über FLAGZ konfigurierbar
			boolean bUseDirect = this.getFlagLocal(IJgitStarterGITEnabled.FLAGZLOCAL.USE_PULL_DIRECT);
			boolean bUseFetchMerge = !bUseDirect;
			
			//Das Problem: Der originale MergeStatus bekommt nix von der Auflösung der Konflikte mit.
			bReturn =  JgitUtilGIT.pullIgnoreCheckoutConflictsGIT(git, credentialsProvider, sRepoRemote, sBranch, bUseFetchMerge);			
			if(!bReturn) {
				System.out.println("PULL: Nicht durchgeführt. Falls vorhanden Lösungshinweis beachten. Wahrscheinlich Vorbedingungen für ein sauberes Repository nicht erfüllt, z.B. COMMIT.");
				break main;
			}						
		}//end main:
		return bReturn;
	}
	
//++++++++++++++++++++++++++++++++++++++++
	@Override
	public boolean pullitResolveCheckoutConflicts(Git git, CredentialsProvider credentialsProvider, String sRepoRemote, String sBranch, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{		
			//Merke: Bei GIT gibt es einen direkten PULL-Befehl oder die Kombination aus FETCH + MERGE
			//       FETCH + MERGE ist eigentlich optimaler als direkt.
			//Der Weg ist über FLAGZ konfigurierbar
			boolean bUsePullDirect = this.getFlagLocal(IJgitStarterGITEnabled.FLAGZLOCAL.USE_PULL_DIRECT);
			
			//Das Problem: Der originale MergeStatus bekommt nix von der Auflösung der Konflikte mit.
			IMergeResultResolvedZZZ objMergeResultResolved =  JgitUtilGIT.pullResolveCheckoutConflictsGIT(git, credentialsProvider, sRepoRemote, sBranch, objEnumStrategyMergeConflict);			
			if(objMergeResultResolved==null) {
				System.out.println("PULL: Kein Merge durchgeführt/Kein MergeResultResolve-Objekt. Vorbedingungen für ein sauberes Repository nicht erfüllt. Bitte (wenn vorhanden) Lösungsvorschläge probieren.");
				break main;
			}
			
			bReturn = objMergeResultResolved.isConflictsResolved();
			bReturn = bReturn & objMergeResultResolved.isGitStatusClean();
			bReturn = bReturn & objMergeResultResolved.isRepositoryStateSafe();
			if(bReturn) break main;
										
			MergeResult objMergeResultOriginal = objMergeResultResolved.getMergeResultOriginal();
			if(objMergeResultOriginal==null) {
				System.out.println("PULL: Kein Merge durchgeführt/Kein MergeResultOriginal-Objekt. Vorbedingungen für ein sauberes Repository nicht erfüllt. Bitte (wenn vorhanden) Lösungsvorschläge probieren.");
				break main;	
			}
			if(objMergeResultOriginal.getMergeStatus().equals(MergeResult.MergeStatus.FAST_FORWARD)){
				System.out.println("PULL: Fast-Forward.");
				bReturn = true;
				break main;	
			}
			
			//+++ Eigentlich gehe ich davon aus, das beim Ignorieren von Konflikten hier 
			//Falls Merge nicht erfolgreich ist, hier am Schluss die Dateien mit den Konflikten auflisten
			System.out.println("##### MERGE: GGFS. NICHT ZU BEHEBENDE KONFLIKTE #######");
			boolean bAnyConflict = JgitUtilZZZ.logConflicts(objMergeResultOriginal);
			if(!bAnyConflict) {
				System.out.println("* KEINE KONFLIKTE");
			}
			System.out.println();//Trennzeile zwischen den Ausgaben
			bReturn = !bAnyConflict;
			if(bReturn) break main;
			
			System.out.println("##### MERGE: ANALYSE UND GGFS. LOESUNGSVORSCHLAEGE #######");
			ResultPostMergeAnalysis objAnalyseResult = GitPostMergeAnalyse.analyzeMergeResult(objMergeResultOriginal);
			objAnalyseResult.printReport();
			System.out.println();//Trennzeile zwischen den Ausgaben
			
		}//end main:
		return bReturn;
	}

			
	//################################################################
	//###### CommitPush ###########################################

	@Override
	public boolean commitPushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ {
		return this.commitPushit(objConfig, null);	
	}
	
	@Override
	public boolean commitPushit(IConfigStarterRemoteJGIT objConfig, String sCommentIn) throws ExceptionZZZ {	
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
							
				//######################################################
				//Konfiguriere JGit für GIT
				boolean bSuccessConfigureGit = this.configureGit(objConfig);
				if(bSuccessConfigureGit) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
					
				//#####################################################################################
				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH / GIT)				
				//######################################################################################				
				
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen
				String sRepositoryRemoteHostIn = objConfig.readRepositoryRemoteHost();
				if(StringZZZ.isEmpty(sRepositoryRemoteHostIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote Host und ein zu verwendender Alias aus .git\\config", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
															
				String sRepositoryRemoteAccountIn = objConfig.readRepositoryRemoteAccount();
				if(StringZZZ.isEmpty(sRepositoryRemoteAccountIn)) {
					ExceptionZZZ ez = new ExceptionZZZ("Kein Account für ConnectionType '"+sConnectionType+"'", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}	
				
				//######################################################################################
				//+++ Folgende Konfiguration könnten aus dem Alias und dem Repository geholt werden
				String sConnectionTypeIn = objConfig.readConnectionType();
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
					//Diese Detail aus der .git\config Datei unter dem Alias auslesen.
					String sDirectoryRepositoryLocalRemote = this.getRepositoryTotalRemote();
					if(StringZZZ.isEmpty(sDirectoryRepositoryLocalRemote)) {
						ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					sConnectionTypeIn = JgitUtilZZZ.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);					
				}
				//Falls immer noch leer, Fehler!
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
					ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++++++++++++++++++++++								
				this.setConnectionType(sConnectionTypeIn);
				this.setRepositoryRemoteHost(sRepositoryRemoteHost);
				this.setRepositoryRemoteAccount(sRepositoryRemoteAccount);
								
				
				String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote GIT Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setRepositoryBaseRemote(sRepositoryRemoteIn);

			
				String sComment = objConfig.readComment(); //vielleicht noch einen weiteren Kommentar per Batch-Argument übergeben 
				this.setCommentCommit(sComment);
				
				//+++++++++++++++++++++++++++++++
				//Finde geaenderte und neue Dateien fuer den commit
				Git git = this.getGitObject();
				boolean bSuccessCommit = this.commitit(git, sCommentIn);
				if(bSuccessCommit) {
					System.out.println("commitit erfolgreich");
				}else {
					System.out.println("commitit NICHT erfolgreich");
					break main;
				}
 
				//+++++++++++++++++++++++++++++++++++
		        //Führe den Push durch
		        
		        //a) Zugriff sicherstellen
		        //   Das passiert durch die lokalen ssh-id Dateien
		        
		        //b) Mache den push	
		        bReturn = this.pushit(git);
		        if(bReturn) {
		        	System.out.println("STATUS AFTER PUSH: SUCCESSFULL");
		        	this.printStatus(git);
		        }else {
		        	System.out.println("STATUS AFTER PUSH: FAILED");
		        	this.printStatus(git);
		        }
		       
		        
		        //s. ChatGPT vom 20260313
		        //Problem: Eclipse "registriert/bemerkt" den Push nicht (also Pfeil nach oben mit 1 dahinter wird angezeigt).
		        //Damit in Eclipse auch der Push "registriert/bemerkt wird" muss noch ein Fetch gemacht werden.
		        //Der letzte fetch() sorgt dafür, dass lokale Remote-Tracking-Branches synchron bleiben, 
		        //was besonders hilfreich ist, wenn gleichzeitig ein Tool wie Eclipse auf das gleiche Repository schaut.
		        	        
		        //aber manchmal ist nichts zu fetchen, darum Fehler abfangen 
		        String sDirectoryRepositoryLocalTotal = this.getRepositoryLocalTotal();
		        File objFileDir = new File(sDirectoryRepositoryLocalTotal);
		        
		        String sRepositoryRemote = this.getRepositoryTotalRemote();
		        
		        String sBranch = this.getRepositoryBranch();
		        JgitUtilZZZ.fetchIgnoreNothingToFetch(objFileDir, sRepositoryRemote, sBranch);
			    System.out.println(("FETCH DONE"));
			  	
			    git.close();
			    bReturn = true;
	        //###############################################################	  
			}catch(TransportException tex) {
				ExceptionZZZ ez = new ExceptionZZZ(tex);
				throw ez;	
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
	
	
	@Override
	public boolean pushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
							
				//######################################################
				//Konfiguriere JGit für GIT
				boolean bSuccessConfigureGit = this.configureGit(objConfig);
				if(bSuccessConfigureGit) {
					System.out.println("Git erfolgreich konfiguriert");
				}else {
					System.out.println("Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				//################################################
				//### Die benoetigten Parameter aus dem Argumenten des Aufrufs holen
									
				//#####################################################################################
				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH / GIT)				
				//######################################################################################
				
				//+++++++++++++++++++++++++++++++++++
		        //Führe den Push durch
		        //a) Zugriff sicherstellen
		        //   Das passiert durch die lokalen ssh-id Dateien
		        
		        //b) Mache den push	
				Git git = this.getGitObject();				
		        boolean bSuccessPush = this.pushit(git);
		        if(bSuccessPush) {
					System.out.println("pushit erfolgreich");
				}else {
					System.out.println("pushit NICHT erfolgreich");
					break main;
				}
		        bReturn = true;
		        
		        if(bReturn) {
		        	System.out.println("STATUS AFTER PUSH: SUCCESSFULL");
		        	this.printStatus(git);
		        }else {
		        	System.out.println("STATUS AFTER PUSH: FAILED");
		        	this.printStatus(git);
		        }
		       
		        //s. ChatGPT vom 20260313
		        //Problem: Eclipse "registriert/bemerkt" den Push nicht (also Pfeil nach oben mit 1 dahinter wird angezeigt).
		        //Damit in Eclipse auch der Push "registriert/bemerkt wird" muss noch ein Fetch gemacht werden.
		        //Der letzte fetch() sorgt dafür, dass lokale Remote-Tracking-Branches synchron bleiben, 
		        //was besonders hilfreich ist, wenn gleichzeitig ein Tool wie Eclipse auf das gleiche Repository schaut.
		        	        
		        //aber manchmal ist nichts zu fetchen, darum Fehler abfangen 
		        String sDirectoryRepositoryLocalTotal = this.getRepositoryLocalTotal();
		        File objFileDir = new File(sDirectoryRepositoryLocalTotal);
		        
		        String sRepositoryRemote = this.getRepositoryTotalRemote();
		        
		        String sBranch = this.getRepositoryBranch();
		        JgitUtilZZZ.fetchIgnoreNothingToFetch(objFileDir, sRepositoryRemote, sBranch);
			    System.out.println(("FETCH DONE"));
			  	
			    git.close();
	        //###############################################################	  
			}catch(TransportException tex) {
				ExceptionZZZ ez = new ExceptionZZZ(tex);
				throw ez;	
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}
		
	
	@Override
	public boolean pushit(Git git) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				//wg. Authentifizierung: Ausgabe der verwendeten SessionFactory - Klasse... ist das auch meine?
				System.out.println("Verwendete SshSessionFactory: " + SshSessionFactory.getInstance().getClass());
				
				// aber mal explizit als pushCommand
				PushCommand pushCommand = git.push();
				
				String sRemoteRepositoryAlias = this.getRepositoryRemoteAlias();
				pushCommand.setRemote(sRemoteRepositoryAlias);
		
				// ############################################################
				// Push ausführen und Result entgegennehmen
				Iterable<PushResult> pushResults = pushCommand.call();

				//Falls Push nicht erfolgreich ist, hier die Ursachen auflisten		
				System.out.println("##### PUSH ERGEBNIS #######");
				boolean bAnyConflict = JgitUtilZZZ.logPushResults(pushResults);
				bReturn = !bAnyConflict;
				
				
				System.out.println("##### PUSH ANALYSE UND LOESUNGSVORSCHLAEGE #######");
				for(PushResult pushResult : pushResults) {
					ResultPostPushAnalysis objAnalyseResult = 
							GitPostPushAnalyse.analyzePushResult(pushResult);

					objAnalyseResult.printReport();
				}
				
			}catch(InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			}catch(TransportException te) {
				ExceptionZZZ ez = new ExceptionZZZ(te);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}

	//##############################
	//###### FETCH #################
	@Override
	public boolean fetchit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ {	
		boolean bReturn = false;
		main:{
			try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
						
				//######################################################
				//Konfiguriere JGit für GIT
				boolean bSuccessConfigureGit = this.configureGit(objConfig);
				if(bSuccessConfigureGit) {
					System.out.println("Basis Git erfolgreich konfiguriert");
				}else {
					System.out.println("Basis Git NICHT erfolgreich konfiguriert");
					break main;
				}
				
				//#####################################################################################
				//Merke: Die Remote-Repository-Daten können nicht hier in der abstrakten Klasse gemacht werden,
				//       sondern müssen in der zum Protokoll passenden Klasse gemacht werden (HTTPS / SSH / GIT)				
				//######################################################################################
				
				String sRepositoryRemoteHost = objConfig.readRepositoryRemoteHost();
				if(StringZZZ.isEmpty(sRepositoryRemoteHost)){
					ExceptionZZZ ez = new ExceptionZZZ("Hostname des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sRepositoryRemoteAccount = objConfig.readRepositoryRemoteAccount();
				if(StringZZZ.isEmpty(sRepositoryRemoteAccount)){
					ExceptionZZZ ez = new ExceptionZZZ("Account des remote Repository", iERROR_PARAMETER_MISSING, JgitStarterHTTPS.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++ Folgende Konfiguration könnten aus dem Alias und dem Repository geholt werden
				String sConnectionTypeIn = objConfig.readConnectionType();
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
					//Diese Detail aus der .git\config Datei unter dem Alias auslesen.
					String sDirectoryRepositoryLocalRemote = this.getRepositoryTotalRemote();
					if(StringZZZ.isEmpty(sDirectoryRepositoryLocalRemote)) {
						ExceptionZZZ ez = new ExceptionZZZ("ConnectionType fehlt und lokales Repository ist unerwartet nicht gesetzt.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}
					
					sConnectionTypeIn = JgitUtilZZZ.computeRepositoryConnectionTypeFromUrlRepo(sDirectoryRepositoryLocalRemote);
				}
				//Falls immer noch leer, Fehler!
				if(StringZZZ.isEmpty(sConnectionTypeIn) ) {
					ExceptionZZZ ez = new ExceptionZZZ("ConnectionType", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//+++++++++++++++++++++++								
				this.setConnectionType(sConnectionTypeIn);
				this.setRepositoryRemoteHost(sRepositoryRemoteHost);
				this.setRepositoryRemoteAccount(sRepositoryRemoteAccount);
					
				
				String sRepositoryRemoteIn = this.computeRepositoryBaseRemote();
				if(StringZZZ.isEmpty(sRepositoryRemoteIn)){
					ExceptionZZZ ez = new ExceptionZZZ("URL zum entfernten/remote GIT Repository", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				this.setRepositoryBaseRemote(sRepositoryRemoteIn);
							
				//+++++++++++++++++++++++++++++++
				//Finde geaenderte und neue Dateien fuer den commit
				Git git = this.getGitObject();
				boolean bSuccessFetch = this.fetchit(git);
		        if(bSuccessFetch) {
		        	System.out.println("STATUS AFTER FETCH: SUCCESSFULL");
		        	this.printStatus(git);
		        	bReturn = true;
		        }else {
		        	System.out.println("STATUS AFTER FETCH: FAILED");
		        	this.printStatus(git);
		        	bReturn = false;
		        }

			    git.close();
			    
	        //###############################################################	  
			}catch(TransportException tex) {
				ExceptionZZZ ez = new ExceptionZZZ(tex);
				throw ez;	
			}catch(IllegalStateException ie) {
				ExceptionZZZ ez = new ExceptionZZZ(ie);
				throw ez;
			}catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}
		}//end main:
		return bReturn;
	}

	
	//###############################################
	//### FLAG HANDLING
	//###############################################
	
	//###############################################
	//### FLOGLOCAL 
	
	//### aus IJgitStarterGITEnabled	
	@Override
	public boolean getFlagLocal(IJgitStarterGITEnabled.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.getFlagLocal(objEnumFlag.name());
	}

	@Override
	public boolean setFlagLocal(IJgitStarterGITEnabled.FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		return this.setFlagLocal(objEnumFlag.name(), bFlagValue);
	}

	@Override
	public boolean[] setFlagLocal(IJgitStarterGITEnabled.FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ {
		boolean[] baReturn=null;
		main:{
			if(!ArrayUtilZZZ.isNull(objaEnumFlag)) {
				baReturn = new boolean[objaEnumFlag.length];
				int iCounter=-1;
				for(IJgitStarterGITEnabled.FLAGZLOCAL objEnumFlag:objaEnumFlag) {
					iCounter++;
					boolean bReturn = this.setFlagLocal(objEnumFlag, bFlagValue);
					baReturn[iCounter]=bReturn;
				}
			}
		}//end main:
		return baReturn;
	}

	@Override
	public boolean proofFlagLocalExists(IJgitStarterGITEnabled.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagLocalExists(objEnumFlag.name());
	}

	@Override
	public boolean proofFlagSetBefore(IJgitStarterGITEnabled.FLAGZLOCAL objEnumFlag) throws ExceptionZZZ {
		return this.proofFlagSetBefore(objEnumFlag.name());
	}
}
