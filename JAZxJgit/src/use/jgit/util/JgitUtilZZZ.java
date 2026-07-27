package use.jgit.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ResolveMerger.MergeFailureReason;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.URIish;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.machine.EnvironmentZZZ;
import basic.zBasic.util.web.cgi.UrlLogicZZZ;
import use.jgit.config.IConfigJGIT;
import use.jgit.start.protocol.git.JgitStarterGIT;
import use.jgit.start.protocol.https.JgitStarterHTTPS;
import use.jgit.start.protocol.ssh.JgitStarterSSH;

public class JgitUtilZZZ implements IConstantZZZ {
	
	public static String addProtocolToUrl(String sProtocol, String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocol)) {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("UrlRepo", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = JgitUtilGIT.addProtocolToUrl(sUrlRepo);
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = JgitUtilHTTPS.addProtocolToUrl(sUrlRepo);
			}else if(sProtocol.equalsIgnoreCase("ssh")) {
				sReturn = JgitUtilSSH.addProtocolToUrl(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol für Git Repository. Unbekannter Typ: '" + sProtocol + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String createCommentCommitDefault() throws ExceptionZZZ {
		String sReturn = null;
		main:{			
			sReturn = JgitUtilZZZ.createCommentCommit(null);
		}//end main:
		return sReturn;
	}
	
	public static String createCommentCommit(String sCommentIn) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			String sComment = "";
			if(!StringZZZ.isEmpty(sCommentIn)) {
				sComment = sCommentIn;
			}
			
			String sCommentMetadata = JgitUtilZZZ.createCommentCommitTemplate();
			if(!StringZZZ.isEmpty(sCommentMetadata)) {
				sReturn = String.format(sCommentMetadata, sComment);
			}
		}//end main:
		return sReturn;
	}
	
	public static String createCommentCommit(String sCommentIn, String sProjectExecutingIn) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			String sComment = "";
			if(!StringZZZ.isEmpty(sCommentIn)) {
				sComment = sCommentIn;
			}
			
			String sCommentMetadata = "";
			String sProjectExecuting = "";
			if(!StringZZZ.isEmpty(sProjectExecutingIn)) {
				sProjectExecuting = sProjectExecutingIn;
				sCommentMetadata = JgitUtilZZZ.createCommentCommitTemplate(sProjectExecuting);					
			}else {
				sCommentMetadata = JgitUtilZZZ.createCommentCommitTemplate();								
			}
			
			if(!StringZZZ.isEmpty(sCommentMetadata)) {
				sReturn = String.format(sCommentMetadata, sComment);
			}
			
			
		}//end main:
		return sReturn;
	}
	
	public static String createCommentCommitTemplate() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			String sMetadata01 = JgitUtilZZZ.createCommentCommitMetadata01();			
			sReturn = sMetadata01 + " %s ";
		}//end main:
		return sReturn;	
	}
	
	public static String createCommentCommitTemplate(String sProjectExecuting) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			String sMetadata01 = JgitUtilZZZ.createCommentCommitMetadata01();
			String sMetadata02 = JgitUtilZZZ.createCommentCommitMetadata02(sProjectExecuting);
			sReturn = sMetadata01 + " %s " + sMetadata02;
		}//end main:
		return sReturn;	
	}
	
	public static String createCommentCommitMetadata01() throws ExceptionZZZ {
		String sReturn = null;
		main:{
			long lTimestamp = DateTimeZZZ.computeTimestamp();
			SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy_H:m");		
			String sDateFormated = dateFormater.format(lTimestamp);

			//Hier den Namen des Rechners einfügen
			String sHostname = EnvironmentZZZ.getHostName();
			
			sReturn = sDateFormated + " (Host: '" + sHostname + "')";
		}//end main:
		return sReturn;	
	}
	
	public static String createCommentCommitMetadata02(String sProjectExecuting) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			sReturn =  "(by project '" + sProjectExecuting + "')";
		}//end main:
		return sReturn;
	}
	
	//##########################################################
	
	/** Normiere einen ggfs. von Windows übergebenen Dateipfad in die Form, wie sie in Git verwendet wird.
	 * @param sFilePathInRepository
	 * @return
	 */
	public static String computeGitPath(String sFilePathInRepository) {
	    return sFilePathInRepository.replace('\\', '/');
	}
	
	//##########################################################
	
	public static String computeRepositoryConnectionTypeFromProtocol(String sProtocol) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocol)) {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = "SSH";
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = "HTTPS";
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Protokol für Git Repository. Unbekannter Typ: '" + sProtocol + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryConnectionTypeFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtilZZZ.computeRepositoryProtocolFromUrlRepo(sUrlRepo);			
			sReturn = JgitUtilZZZ.computeRepositoryConnectionTypeFromProtocol(sProtocol);
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryProtocolFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryProtocolFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryProtocolFromUrlSSH(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryHostFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryHostFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryHostFromUrlSSH(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlGIT(sUrlRepo)) {
				sReturn = JgitUtilGIT.computeRepositoryHostFromUrlGIT(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryAccountFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryAccountFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryAccountFromUrlSSH(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlGIT(sUrlRepo)) {
				sReturn = JgitUtilGIT.computeRepositoryAccountFromUrlGIT(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryProjectFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryProjectFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryProjectFromUrlSSH(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlGIT(sUrlRepo)) {
				sReturn = JgitUtilGIT.computeRepositoryProjectFromUrlGIT(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	/** Wichtig: Hier soll eine URL Teil zurückgegeben werden, der mit new URL(sUrlPart) keine Malformed Exception wirft.
	 *           Das ist wg. :<Accountname> ein Problem
	 * @param sUrlRepo
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryUrlPartFromUrlRepo(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.computeRepositoryUrlPartFromUrlHTTPS(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.computeRepositoryUrlPartFromUrlSSH(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlGIT(sUrlRepo)) {
				sReturn = JgitUtilGIT.computeRepositoryUrlPartFromUrlGIT(sUrlRepo);				
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlTotalFor(String sProtocolType, String sUrlBaseIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocolType)){
				ExceptionZZZ ez = new ExceptionZZZ("ProtocolType", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sUrlBaseIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Base Url Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			String sUrlBase = sUrlBaseIn;
			String sRepositoryProject = sRepositoryProjectIn;
			
			if(sProtocolType.equalsIgnoreCase("https")) {
				sReturn = JgitUtilHTTPS.computeRepositoryUrlTotalHTTPS(sUrlBase, sRepositoryProject);
			}else if(sProtocolType.equalsIgnoreCase("ssh")) {
				sReturn = JgitUtilSSH.computeRepositoryUrlTotalSSH(sUrlBase, sRepositoryProject);
			}else if(sProtocolType.equalsIgnoreCase("git")) {
				sReturn = JgitUtilGIT.computeRepositoryUrlTotalGIT(sUrlBase, sRepositoryProject);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekannter Typ: '" + sUrlBase + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	public static String computeRepositoryUrlTotalFor(String sProtocolType, String sRepositoryRemoteHostIn, String sRepositoryRemoteAccountIn, String sRepositoryProjectIn) throws ExceptionZZZ{
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sProtocolType)){
				ExceptionZZZ ez = new ExceptionZZZ("ProtocolType", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryRemoteHostIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Remote Host", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryRemoteAccountIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Remote Account", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(StringZZZ.isEmpty(sRepositoryProjectIn)){
				ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			
			String sRepositoryProject = sRepositoryProjectIn;
			
			String sUrlBase;			
			if(sProtocolType.equalsIgnoreCase("https")) {
				sUrlBase = JgitUtilHTTPS.computeRepositoryUrlBaseHTTPS(sRepositoryRemoteHostIn, sRepositoryRemoteAccountIn);
				sReturn = JgitUtilHTTPS.computeRepositoryUrlTotalHTTPS(sUrlBase, sRepositoryProject);
			}else if(sProtocolType.equalsIgnoreCase("ssh")) {
				sUrlBase = JgitUtilSSH.computeRepositoryUrlBaseSSH(sRepositoryRemoteHostIn, sRepositoryRemoteAccountIn);
				sReturn = JgitUtilSSH.computeRepositoryUrlTotalSSH(sUrlBase, sRepositoryProject);
			}else if(sProtocolType.equalsIgnoreCase("git")) {
				sUrlBase = JgitUtilGIT.computeRepositoryUrlBaseGIT(sRepositoryRemoteHostIn, sRepositoryRemoteAccountIn);
				sReturn = JgitUtilGIT.computeRepositoryUrlTotalGIT(sUrlBase, sRepositoryProject);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Unbekanntes Protokol: '" + sProtocolType + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
	}
	
	
	
	
	//#########################################################
	
	/** Wenn der Filepath nicht absolut ist... baseRepository und Projekt holen und voranstellen
	 * @param sRepositoryLocalBase
	 * @param sProject
	 * @param sFilePath
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String computeRepositoryLocalFilePath(String sRepositoryLocalBase, String sProject, String sFilePath) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sFilePath)) {
				ExceptionZZZ ez = new ExceptionZZZ("sFilePath", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			//Falls der Filepath relativ ist, müssen die Verzeichnisangaben da sein.
			if(FileEasyZZZ.isPathRelative(sFilePath)){
				if(StringZZZ.isEmpty(sRepositoryLocalBase)) {
					ExceptionZZZ ez = new ExceptionZZZ("sRepositoryLocalBase", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
				
				if(StringZZZ.isEmpty(sProject)) {
					ExceptionZZZ ez = new ExceptionZZZ("sProject", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;				
				}
				
				String sDirectory = FileEasyZZZ.joinFilePathName(sRepositoryLocalBase, sProject);
				sReturn = FileEasyZZZ.joinFilePathName(sDirectory, sFilePath);
			}else {
				//Falls Base und Projekt vorhanden ist, hole den relativen Pfad aus dem Dateipfad
				if(!StringZZZ.isEmpty(sRepositoryLocalBase) && !StringZZZ.isEmpty(sProject)) {
					String sDirectory = FileEasyZZZ.joinFilePathName(sRepositoryLocalBase, sProject);
					String sFileName = FileEasyZZZ.getNameFromFilepath(sFilePath);
					sReturn = FileEasyZZZ.joinFilePathName(sDirectory, sFileName);
				}else {				
					sReturn = sFilePath;
				}
			}		
		}//end main:
		return sReturn;
	}
	
	
	//#########################################################
	
	public static void debugForFetch(Git git) throws URISyntaxException, IOException {
		
		System.out.println("### DEBUG FOR FETCH: START");

		Repository repository = git.getRepository();

		String branch = repository.getBranch();

		System.out.println("Aktueller Branch: " + branch);

		// -------------------------------------------------
		// BranchConfig
		// -------------------------------------------------
		BranchConfig branchConfig =
		        new BranchConfig(repository.getConfig(), branch);

		System.out.println("Branch Remote : " + branchConfig.getRemote());
		System.out.println("Branch Merge  : " + branchConfig.getMerge());

		// -------------------------------------------------
		// RemoteConfig
		// -------------------------------------------------
		StoredConfig storedConfig = repository.getConfig();

		RemoteConfig remoteConfig =
		        new RemoteConfig(storedConfig, branchConfig.getRemote());

		System.out.println("Remote Name   : " + remoteConfig.getName());

		for (URIish uri : remoteConfig.getURIs()) {
		    System.out.println("Remote URI    : " + uri);
		}

		for (RefSpec refSpec : remoteConfig.getFetchRefSpecs()) {
		    System.out.println("Fetch RefSpec : " + refSpec);
		}

		for (RefSpec refSpec : remoteConfig.getPushRefSpecs()) {
		    System.out.println("Push RefSpec  : " + refSpec);
		}

		System.out.println("### DEBUG FOR FETCH: ENDE");
		
		
	}
	
	
	public static void debugForMerge(Git git) throws Exception {

		System.out.println("### DEBUG FOR MERGE: START");

		Repository repository = git.getRepository();

		// -------------------------------------------------
		// Aktueller Branch
		// -------------------------------------------------
		String branch = repository.getBranch();

		System.out.println("Current Branch        : " + branch);

		// -------------------------------------------------
		// BranchConfig
		// -------------------------------------------------
		BranchConfig branchConfig =
		        new BranchConfig(repository.getConfig(), branch);

		String sRemoteName = branchConfig.getRemote();
		String sMergeBranch = branchConfig.getMerge();

		System.out.println("Configured Remote     : " + sRemoteName);
		System.out.println("Configured Merge Ref  : " + sMergeBranch);

		// -------------------------------------------------
		// HEAD
		// -------------------------------------------------
		ObjectId headId = repository.resolve("HEAD");

		System.out.println("HEAD Commit           : " + headId.getName());

		// -------------------------------------------------
		// Remote Tracking Ref
		// Beispiel:
		// refs/remotes/origin/main
		// -------------------------------------------------
		String sTrackingRef =
		        "refs/remotes/"
		        + sRemoteName
		        + "/"
		        + Repository.shortenRefName(sMergeBranch);

		System.out.println("Tracking Ref          : " + sTrackingRef);

		Ref refTracking = repository.findRef(sTrackingRef);

		if (refTracking != null) {

		    System.out.println("Tracking Ref Found    : YES");
		    System.out.println("Tracking ObjectId     : "
		            + refTracking.getObjectId().getName());

		} else {

		    System.out.println("Tracking Ref Found    : NO");
		}

		// -------------------------------------------------
		// Lokaler Branch Ref
		// -------------------------------------------------
		Ref refLocal = repository.findRef(branch);

		if (refLocal != null) {

		    System.out.println("Local Branch Ref      : "
		            + refLocal.getName());

		    System.out.println("Local Branch ObjectId : "
		            + refLocal.getObjectId().getName());
		}

		System.out.println("### DEBUG FOR MERGE: END");
	}
	
	/** Ueberpruefe, ob unter dem Alias des remote Repositories auch eine URL gefunden wird.
	 *  Falls, nein, setzte es ggfs. bei bOverwrite = true;
	 * 
	 * z.B.:
	 * 
	 * [remote "origin"]
			url = https://github.com/firak01/Projekt_Kernel02_JAZDummy.git
			fetch = +refs/heads/*:refs/remotes/origin/*
	 * 
	 * @param repo
	 * @param sRepositoryRemoteAlias
	 * @param sRepositoryRemoteUrl
	 * @param bOverwrite
	 * @throws IOException
	 */
	public static void ensureRemoteExists(Repository repo, String sRepositoryRemoteAliasIn, String sRepositoryRemoteUrl, String sRepositoryRemoteBranchIn, boolean bOverwrite) throws ExceptionZZZ {
		try {
			StoredConfig config = repo.getConfig();
	
			String sRepositoryRemoteAlias = "origin";
		    if(!StringZZZ.isEmpty(sRepositoryRemoteAliasIn)) sRepositoryRemoteAlias = sRepositoryRemoteAliasIn;
		       
		    String existingUrl = config.getString("remote", sRepositoryRemoteAlias, "url");
		    if (existingUrl == null || existingUrl.trim().isEmpty() || bOverwrite) {
	
		        if (bOverwrite && existingUrl != null && !existingUrl.equals(sRepositoryRemoteUrl)) {
		            System.out.println("Remote '" + sRepositoryRemoteAlias + "' wird überschrieben:");
		            System.out.println("  alt: " + existingUrl);
		            System.out.println("  neu: " + sRepositoryRemoteUrl);
		        }
	
		        
		        config.setString("remote", sRepositoryRemoteAlias, "url", sRepositoryRemoteUrl);
	
		        //Variante ohne einen konkreten Branch berücksichtigen, darum darf der Übergebwert leer sein
		        //und der leere Wert wird nicht automatisch mit "master"erstetz
		        String sRepositoryRemoteBranch = sRepositoryRemoteBranchIn;
		        
		        boolean bBranchAll = false;
		        if(StringZZZ.isEmptyTrimmed(sRepositoryRemoteBranch)) {
		        	bBranchAll=true;
		        }else if(sRepositoryRemoteBranch.equals(IConfigJGIT.sBRANCH_ALL)) {
		        	bBranchAll=true;
		        }
		        
		        if(bBranchAll){ 
			        
					//Variante für alle Branches
					 config.setStringList(
					    "remote",
					    sRepositoryRemoteAlias,
					    "fetch",
					    Collections.singletonList(
					        "+refs/heads/*:refs/remotes/"
					        + sRepositoryRemoteAlias
					        + "/*"
					    )
					);  
		        }else {
		        	
		        	//Variante für einen konkreten Branch
			        config.setStringList(
			        	    "remote",
			        	    sRepositoryRemoteAlias,
			        	    "fetch",
			        	    Collections.singletonList(
			        	        "+refs/heads/"
			        	        + sRepositoryRemoteBranch
			        	        + ":refs/remotes/"
			        	        + sRepositoryRemoteAlias
			        	        + "/"
			        	        + sRepositoryRemoteBranch
			        	    )
			        );
		        }
	
		        config.save();
		    }//end if(...bOverwrite)
		} catch(IOException ioe) {
			ExceptionZZZ ez = new ExceptionZZZ("IOException: " +ioe.getMessage(), iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	
	  /**
     * Findet den Remote-Namen (z.B. "origin") anhand einer URL.
     * Hintergrund: 
     * 
     *  Ich bekomme folgende JGit Fehlermeldung: 
     *  org.eclipse.jgit.api.errors.InvalidConfigurationException: 
     *  No value for key remote.git@github.com:firak01/Projekt_Kernel02_JAZDummy.git.url found in configuration
     *  
     *  Es wird unter "origin" nachgesehen. 
     *  In dem lokalen Repository gibt es in der Datei .git\config den Eintrag: [remote "origin"] url = git@github.com:firak01/Projekt_Kernel02_JAZDummy.git 
     *  Der Grund für den Fehler liegt daran, dass ich in pullCommand.setRemote("einString")
     *  für "einString" die Url angeben. Das ist aber bei SSH falsch und funktioniert nur bei HTTPS.
     *  Ich benötige nun eine statische Methode, mit der ich den Einrag für remote bekomme, wenn ich die URL als Suchwert verwende:  
     */
    public static String findRemoteNameByUrl(Git git, String url) {
        if (git == null || url == null) {
            return null;
        }

        Repository repo = git.getRepository();
        Config config = repo.getConfig();

        // Alle Remote-Namen holen (origin, upstream, etc.)
        Set<String> remotes = config.getSubsections("remote");

        for (String remoteName : remotes) {
            String remoteUrl = config.getString("remote", remoteName, "url");

            if (remoteUrl != null && remoteUrl.equals(url)) {
                return remoteName;
            }
        }

        return null; // nichts gefunden
    }
    
    
    //##################################################
    public static List<Ref> getRepositoryBranches(Git git) throws ExceptionZZZ {
    	List<Ref> listReturn=null;
    	main:{
			try {
				listReturn = git.branchList().call();
			} catch (GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}    
    	}//end main;
    	return listReturn;
    }
    
    /** Liefert z.B.
     	refs/heads/main
		refs/heads/develop
		refs/heads/feature1
		
     * @param git
     * @return
     */
    public static List<String> getRepositoryBranchesName(Git git) throws ExceptionZZZ {
    	List<String> listReturn=new ArrayList<String>();
    	main:{
			List<Ref> branches = JgitUtilZZZ.getRepositoryBranches(git);
			
			for (Ref localRef : branches) {
			    String branchName = localRef.getName();				   
			    listReturn.add(branchName);
			}   
    	}//end main;
    	return listReturn;
    }
    
    /** Liefert z.B.
 	main
	develop
	feature1
	
     * @param git
     * @return
     */
    public static List<String> getRepositoryBranchesShortName(Git git) throws ExceptionZZZ {
    	List<String> listReturn=new ArrayList<String>();
    	main:{			
			List<Ref> branches = JgitUtilZZZ.getRepositoryBranches(git);
			
			for (Ref localRef : branches) {
			    String branchNameShort = Repository.shortenRefName(localRef.getName());				   
			    listReturn.add(branchNameShort);
			}			
    	}//end main;
    	return listReturn;
    }
	//#############################################################
	
	/* https://git-scm.com/book/de/v2/Anhang-B:-Git-in-deine-Anwendungen-einbetten-JGit
	// Create a new repository
Repository newlyCreatedRepo = FileRepositoryBuilder.create(
new File("/tmp/new_repo/.git"));
newlyCreatedRepo.create();

//Open an existing repository
Repository existingRepo = new FileRepositoryBuilder()
.setGitDir(new File("my_repo/.git"))
.build();
	 */
	public static Repository getRepositoryObject(String sRepositoryDirectoryTotal, boolean bCreateMissing) throws ExceptionZZZ{
		Repository objReturn = null;
		main:{
			try {
				if(StringZZZ.isEmpty(sRepositoryDirectoryTotal)){
					ExceptionZZZ ez = new ExceptionZZZ("Projekname des Remote Repository", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				File objDirectoryRepo = new File(sRepositoryDirectoryTotal);
				if(!FileEasyZZZ.exists(objDirectoryRepo)) {
					if(!bCreateMissing) {
						ExceptionZZZ ez = new ExceptionZZZ("Projektverzeichnis des Remote Repository existiert nicht.", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						System.out.println("Erstelle fehlendes Repository Verzeichnis: '" + sRepositoryDirectoryTotal + "'");
						FileEasyZZZ.createDirectory(objDirectoryRepo);
					}
				}
				
				//s. https://git-scm.com/book/de/v2/Anhang-B:-Git-in-deine-Anwendungen-einbetten-JGit
				String sRepositoryFileTotal = FileEasyZZZ.joinFilePathName(objDirectoryRepo, ".git");
				File objFileRepo = new File(sRepositoryFileTotal);
				if(!FileEasyZZZ.exists(objFileRepo)) {
					if(!bCreateMissing) {
						ExceptionZZZ ez = new ExceptionZZZ("Projektverzeichnis '" + sRepositoryDirectoryTotal + "' ist kein Git Repository. Es fehlt Datei .git", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
						throw ez;
					}else {
						// Create a new repository
						objReturn = FileRepositoryBuilder.create(objFileRepo);					
						objReturn.create();											
					}				
				}else {
					//Open an existing repository
					FileRepositoryBuilder objRepoBuilder = new FileRepositoryBuilder();
					objRepoBuilder.setGitDir(objFileRepo);
					objReturn = objRepoBuilder.build();
				}		
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			}
		}//end main:
		return objReturn;
	}
	
	/** liefert nur das Protokoll zurück.
	 * z.B.: https://github.com/firak01  --> https
	 * oder  git@github.com:firak01      --> git
	 * 
	 *  
	 * @param sUrlRepo
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocol(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			if(JgitUtilZZZ.isUrlHTTPS(sUrlRepo)) {
				sReturn = JgitUtilHTTPS.getProtocolFromUrl(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlSSH(sUrlRepo)) {
				sReturn = JgitUtilSSH.getProtocolFromUrl(sUrlRepo);
			}else if(JgitUtilZZZ.isUrlGIT(sUrlRepo)) {
				sReturn = JgitUtilGIT.getProtocolFromUrl(sUrlRepo);
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL. Kein Protokol vorhanden oder unbekannter Typ: '" + sUrlRepo + "'", iERROR_PARAMETER_VALUE, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
		}//end main:
		return sReturn;
		
	}
	
	/** z.B.: https://github.com/firak01   oder  git@github.com:firak01
	 *  liefert das Protokoll PLUS die Protokol-Separatorzeichen zurück.
	 * @param sUrlRepo
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getProtocolPart(String sUrlRepo) throws ExceptionZZZ {
		String sReturn = null;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) break main;
			
			String sProtocol=null;
			try {
				//Fange den Fehler ab, falls kein Protokoll vorhanden ist
				sProtocol = JgitUtilZZZ.getProtocol(sUrlRepo);
			}catch (ExceptionZZZ ez){				
			}

			if(StringZZZ.isEmptyNull(sProtocol)) break main;
			
			if(sProtocol.equalsIgnoreCase("git")) {
				sReturn = sProtocol + "@";
			}else if(sProtocol.equalsIgnoreCase("https")) {
				sReturn = sProtocol + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL;
			}else if(sProtocol.equalsIgnoreCase("ssh")) {
				sReturn = sProtocol + UrlLogicZZZ.sURL_SEPARATOR_PROTOCOL;
			}									
		}//end main:
		return sReturn;
	}
	
	/** Z.B.  von git@github.com:firak01
	 *       oder git@github.com:firak01/Projekt_Kernel02_JAZDummy.git
	 * @param sRepositoryRemoteUrlSSH
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static String getAccountFromUrl(String sRepositoryRemoteUrl) throws ExceptionZZZ{
		return computeRepositoryAccountFromUrlRepo(sRepositoryRemoteUrl);
	}
	
	public static boolean isUrlSSH(String sUrlRepo) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtilSSH.getProtocolFromUrl(sUrlRepo);
			if(StringZZZ.isEmpty(sProtocol)) break main;
			
			if(sProtocol.equalsIgnoreCase(JgitStarterSSH.sPROTOCOL)) {
				bReturn = true;
				break main;
			}
		}//end main:
		return bReturn;
	}
	
	public static boolean isUrlGIT(String sUrlRepo) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtilGIT.getProtocolFromUrl(sUrlRepo);
			if(StringZZZ.isEmpty(sProtocol)) break main;
			
			if(sProtocol.equalsIgnoreCase(JgitStarterGIT.sPROTOCOL)) {
				bReturn = true;
				break main;
			}
		}//end main:
		return bReturn;
	}
	
	
	public static boolean isUrlHTTPS(String sUrlRepo) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sUrlRepo)) {
				ExceptionZZZ ez = new ExceptionZZZ("Remote Repository URL", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;				
			}
			
			String sProtocol = JgitUtilHTTPS.getProtocolFromUrl(sUrlRepo);
			if(StringZZZ.isEmpty(sProtocol)) break main;
			
			if(sProtocol.equalsIgnoreCase(JgitStarterHTTPS.sPROTOCOL)) {
				bReturn = true;
				break main;
			}
		}//end main:
		return bReturn;
	}
		
	//#####################################
	//######## MERGE BETREFFEND
	public static boolean logConflicts(MergeResult mergeResult) throws ExceptionZZZ {
	    boolean bReturn = false;
	    main:{
	        if(mergeResult==null) {
	            ExceptionZZZ ez = new ExceptionZZZ("MergeResult", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
	            throw ez;                
	        }

	        MergeResult.MergeStatus status = mergeResult.getMergeStatus();
	        System.out.println("MergeStatus: " + status);

	        // =========================================
	        // 1. Erfolgsfälle (kein Problem)
	        // =========================================
	        if(status.isSuccessful()) {

	            switch(status) {
	                case FAST_FORWARD:
	                case FAST_FORWARD_SQUASHED:
	                    System.out.println("Fast-forward merge performed.");
	                    break;

	                case ALREADY_UP_TO_DATE:
	                    System.out.println("Already up-to-date.");
	                    break;

	                case MERGED:
	                case MERGED_SQUASHED:
	                case MERGED_NOT_COMMITTED:
	                    System.out.println("Merge successful.");
	                    break;

	                default:
	                    System.out.println("Successful merge with status: " + status);
	            }

	            break main; // kein Fehler
	        }

	        // =========================================
	        // 2. Echte Konflikte (Merge wurde durchgeführt)
	        // =========================================
	        if(status == MergeResult.MergeStatus.CONFLICTING) {

	            System.out.println("Merge conflicts detected!");

	            Map<String, int[][]> conflicts = mergeResult.getConflicts();

	            if (conflicts != null && !conflicts.isEmpty()) {
	                System.out.println("Conflicting files:");

	                for (Map.Entry<String, int[][]> entry : conflicts.entrySet()) {
	                    String filePath = entry.getKey();
	                    System.out.println(" - " + filePath);

	                    // Detailinfos
	                    //z.B.: baseStart=0, baseEnd=19, oursStart=10
	                    //Bedeutet:
	                    /*
	                    Diese Zahlen beschreiben die Positionen im Vergleich der drei Versionen:

						base = gemeinsamer Vorfahr (Common Ancestor)
						ours = dein lokaler Stand
						theirs = Remote-Stand (fehlt hier in deinem Ausschnitt)
						
						Konkret:
						
						baseStart=0, baseEnd=19
						→ Im gemeinsamen Vorfahr liegt der betroffene Bereich in den Zeilen 0 bis 19
						oursStart=10
						→ In deiner lokalen Version beginnt der entsprechende Bereich bei Zeile 10
						
						👉 Das bedeutet:
						Der gleiche logische Codeblock wurde zwischen base → yours (und vermutlich auch base → theirs) unterschiedlich verändert → klassischer Mergekonflikt
	                     */
	                    int[][] chunks = entry.getValue();
	                    if (chunks != null) {
	                        for (int i = 0; i < chunks.length; i++) {
	                            int[] c = chunks[i];
	                            
	                            System.out.println("   conflict chunk " + i + "[");
	                            if(c.length>=1) {
	                            	System.out.print("baseStart=" + c[0]);
	                            };
	                            if(c.length>=2) {
	                            	System.out.print(", baseEnd=" + c[1]);
	                            };
	                            if(c.length>=3) {
	                            	System.out.print(", oursStart=" + c[2]);
	                            };
	                            if(c.length>=4) {
	                            	System.out.print(", oursEnd=" + c[3]);
	                            };
	                            if(c.length>=5) {
	                            	System.out.print(", theirsStart=" + c[4]);
	                            };
	                            if(c.length>=6) {
	                            	System.out.print(", theirsEnd=" + c[5]);
	                            };
	                            System.out.print("]\n");
	                        }//end for
	                    }//end   if (chunks != null) {
	                }//end for

	                bReturn = true;
	                break main;
	            } else {
	                System.out.println("Conflict status but no detailed conflict info available.");
	                bReturn = true;
	                break main;
	            }
	        }

	        // =========================================
	        // 3. FAILED → häufig Dirty Worktree oder andere Ursachen
	        // =========================================
	        if(status == MergeResult.MergeStatus.FAILED) {

	            System.out.println("Merge FAILED.");

	            Map<String, MergeFailureReason> failingPaths = mergeResult.getFailingPaths();

	            if (failingPaths != null && !failingPaths.isEmpty()) {

	                System.out.println("Failing paths:");

	                for (Map.Entry<String, MergeFailureReason> entry : failingPaths.entrySet()) {
	                    String filePath = entry.getKey();
	                    MergeFailureReason reason = entry.getValue();

	                    System.out.println(" - " + filePath + " : " + reason);

	                    // Spezifische Diagnose
	                    if(reason == MergeFailureReason.DIRTY_WORKTREE) {
	                        System.out.println("   -> Local changes would be overwritten (DIRTY_WORKTREE).");
	                    } else if(reason == MergeFailureReason.COULD_NOT_DELETE) {
	                        System.out.println("   -> File could not be deleted.");
	                   // } else if(reason == MergeFailureReason.COULD_NOT_RENAME) {
	                   //     System.out.println("   -> File could not be renamed.");
	                    }
	                }

	                bReturn = true;
	                break main;

	            } else {
	                System.out.println("FAILED but no failing paths information available.");
	                bReturn = true;
	                break main;
	            }
	        }

	        // =========================================
	        // 4. Sonstige Fälle
	        // =========================================
	        System.out.println("Unhandled merge status: " + status);
	        bReturn = true;

	    }//end main:	    
	    return bReturn;    
	}
	
	
	//######################################################
	//####### PUSH BETREFFEND
	public static boolean logPushResults(Iterable<PushResult> pushResults) throws ExceptionZZZ {
	    boolean bReturn = false;

	    main:{
	        if(pushResults == null) {
	            ExceptionZZZ ez = new ExceptionZZZ(
	                    "PushResults",
	                    iERROR_PARAMETER_MISSING,
	                    JgitUtilZZZ.class,
	                    ReflectCodeZZZ.getMethodCurrentName());

	            throw ez;
	        }

	        boolean bAnyProblem = false;

	        // #############################################################
	        for(PushResult pushResult : pushResults) {

	            if(pushResult == null) {
	                continue;
	            }

	            System.out.println("=================================================");
	            System.out.println("PushResult for remote: "
	                    + pushResult.getURI());
	            System.out.println("=================================================");

	            Collection<RemoteRefUpdate> updates =
	                    pushResult.getRemoteUpdates();

	            if(updates == null || updates.isEmpty()) {

	                System.out.println("No remote updates available.");
	                bAnyProblem = true;
	                continue;
	            }

	            // #########################################################
	            for(RemoteRefUpdate update : updates) {

	                String sRemoteName = update.getRemoteName();

	                RemoteRefUpdate.Status status =
	                        update.getStatus();

	                String sMessage = update.getMessage();

	                System.out.println("-----------------------------------------");
	                System.out.println("Remote Ref : " + sRemoteName);
	                System.out.println("Status     : " + status);

	                if(sMessage != null) {
	                    System.out.println("Message    : " + sMessage);
	                }

	                // =========================================
	                // Erfolgsfälle
	                // =========================================
	                if(status == RemoteRefUpdate.Status.OK) {

	                    System.out.println("Push successful.");

	                }else if(status == RemoteRefUpdate.Status.UP_TO_DATE) {

	                    System.out.println("Already up-to-date.");

	                // =========================================
	                // Problemfälle
	                // =========================================
	                }else if(status == RemoteRefUpdate.Status.REJECTED_NONFASTFORWARD) {

	                    System.out.println("Push rejected: NONFASTFORWARD");
	                    System.out.println("-> Remote branch contains newer commits.");
	                    System.out.println("-> Execute pull/merge/rebase first.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.REJECTED_NODELETE) {

	                    System.out.println("Push rejected: NODELETE");
	                    System.out.println("-> Remote branch deletion denied.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.REJECTED_REMOTE_CHANGED) {

	                    System.out.println("Push rejected: REMOTE_CHANGED");
	                    System.out.println("-> Remote changed during push.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.NON_EXISTING) {

	                    System.out.println("Remote ref does not exist.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.AWAITING_REPORT) {

	                    System.out.println("Awaiting remote report.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.NOT_ATTEMPTED) {

	                    System.out.println("Push not attempted.");

	                    bAnyProblem = true;

	                }else if(status == RemoteRefUpdate.Status.REJECTED_OTHER_REASON) {

	                    System.out.println("Push rejected: OTHER_REASON");

	                    if(sMessage != null) {
	                        System.out.println("Server message:");
	                        System.out.println("-> " + sMessage);
	                    }

	                    bAnyProblem = true;

	                }else {

	                    System.out.println("Unhandled push status: " + status);

	                    bAnyProblem = true;
	                }

	                // Zusatzinfos
	                Object objExpectedOldObjectId =
	                        update.getExpectedOldObjectId();

	                Object objNewObjectId =
	                        update.getNewObjectId();

	                if(objExpectedOldObjectId != null) {
	                    System.out.println("ExpectedOldObjectId: "
	                            + objExpectedOldObjectId.toString());
	                }

	                if(objNewObjectId != null) {
	                    System.out.println("NewObjectId         : "
	                            + objNewObjectId.toString());
	                }

	            }//end for updates
	        }//end for pushResults

	        bReturn = bAnyProblem;

	    }//end main:
	    System.out.println(); //Leerzeile zum optischen Trennen der weiteren Ausgaben.
	    return bReturn;
	}
	
	//######################################################
	//######### FETCH	
	//Wenn nicht zu fetchen ist, wird eine Exception geworfen. Das ist unschoen.
	//von ChatGPT 20260320, aber für meine einfachen zwecke brauch ich kein FetchResult, also nur die ExceptionHandling uebernommen
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,
	        String sUrlRemote,
	        CredentialsProvider credentialsProvider
	) throws ExceptionZZZ {
		return JgitUtilZZZ.fetchIgnoreNothingToFetch(git, sUrlRemote, credentialsProvider, null);
	}
	
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,
	        String sUrlRemote,
	        CredentialsProvider credentialsProvider,
	        String sBranchIn
	) throws ExceptionZZZ {
		FetchResult objReturn = null;
		main:{
		    try {
		    	try {
					JgitUtilZZZ.debugForFetch(git);
				} catch (URISyntaxException e) {
					ExceptionZZZ ez = new ExceptionZZZ(e);
					throw ez;
				}
		    	
		    	
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();
	
		        if (sUrlRemote != null && sUrlRemote.trim().length() > 0) {
		            fetchCommand.setRemote(sUrlRemote); // kann Alias ODER URL sein
		        }
	
		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		        

		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*		        		       
		        String branch = "master";
		        if(!StringZZZ.isEmpty(sBranchIn)) branch = sBranchIn;
		        
		        String remoteRef = "refs/heads/" + branch;
		        String localTrackingRef = "refs/remotes/origin/" + branch;
		        
		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		        //+ für "fast forward"
		        fetchCommand.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));

		        objReturn = fetchCommand.call();
	
		    } catch (TransportException te) {
	
		        String msg = te.getMessage();
	
		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
		            System.out.println("Nothing to fetch - Repository ist aktuell.");
		            return null; // bewusst null zurückgeben als Signal
		        }
	
		        // alle anderen Fehler weiterwerfen!
		        ExceptionZZZ ez = new ExceptionZZZ(te);
		        throw ez;
		    }catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
		    } catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			} 
		}//end main:
		 return objReturn;
	}
	
	//######################################################
	//######### FETCH	
	/**Manchmal ist nichts zu fetchen, dann wird ein Fehler geworfen.
	   Das ist unschoen, darum Fehler abfangen
	   
	   Minierklaerung:
					siehe .git\config Datei, entsprechende Zeile.
					 
					Das ist ein sogenannter RefSpec (Reference Specification).
					Er sagt Git/JGit was von wo nach wo kopiert werden soll.
					
					Aufbau allgemein:
					[+]<Quelle>:<Ziel>
					
					Also:
					Quelle (Remote-Seite)
					refs/heads/ = alle Branches im Remote-Repository
					 * = Wildcard → alle Branch-Namen
		
					➡️ Bedeutet:
					Hole alle Branches vom Remote
					
					
					Ziel (lokal)
					refs/remotes/origin/ = Remote-Tracking-Branches
					* = gleicher Name wie Quelle
		
					➡️ Bedeutet:
					Speichere sie lokal als origin/branchname
					
					------------
					Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
					Mit + sagst du:
					„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
					
	 * @param objFileDir
	 * @param sRepositoryRemote
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static boolean fetchIgnoreNothingToFetch(File objFileDir, String sRepositoryRemote) throws ExceptionZZZ {
		return JgitUtilZZZ.fetchIgnoreNothingToFetch_(objFileDir, sRepositoryRemote, null);
	}	
	
	public static boolean fetchIgnoreNothingToFetch(File objFileDir, String sRepositoryRemote, String sBranch) throws ExceptionZZZ {
		return JgitUtilZZZ.fetchIgnoreNothingToFetch_(objFileDir, sRepositoryRemote, sBranch);
	}	
	
	
	private static boolean fetchIgnoreNothingToFetch_(File objFileDir, String sRepositoryRemote, String sBranchIn) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			 try {
				 
				 
				Git git4Fetch = Git.open(objFileDir); 
				System.out.println("Git-Repository 4 Fetch repository opened.");
					
			    FetchCommand gitCommandFetch = git4Fetch.fetch();
			    
			    //Laut chat gpt nicht immer die URL notwendig, da die Remote Daten schon im .git/config stehen, wuerde auch ein Alias funktionieren
			    //aber, die RemoteUrl - einmal ermittelt - geht auch.
			    gitCommandFetch.setRemote(sRepositoryRemote); 
			    
			    String sBranch="master";
			    if(!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch=sBranchIn; 
			    
			    
			    //aber: vermutlich wird auf dem falschen Branch gearbeitet.
			    gitCommandFetch.setRefSpecs(
			    		//Wird ein Branch angegeben, den es nicht gibt:
			    	    //TransportException: Remote does not have refs/heads/main available for fetch.
			    		//
			    		//Nach obiger minierklärung ist der erste Teil aber der lokale
			    		//der zweite Teil ist remote... 
			    		//das Wort orign taucht nur als Section auf			    		
			    		//
			    		//Gültige Werte sind, z.B.:
			    		//new RefSpec("+refs/heads/main:refs/remotes/origin/main")
			    		//new RefSpec("+refs/heads/master:refs/remotes/master")		
			    		//new RefSpec("+refs/heads/*:refs/remotes/*")	
			    		
			    		new RefSpec("+refs/heads/" + sBranch + ":refs/remotes/origin/" + sBranch)
			    		
			    	);
			    gitCommandFetch.call();
	        }catch(TransportException tex) {
		        String msg = tex.getMessage();
		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
		            System.out.println("Nothing to fetch - Repository ist aktuell.");	           
		        }else {
		        	// alle anderen Fehler weiterwerfen!
		        	ExceptionZZZ ez = new ExceptionZZZ(tex);
					throw ez;
		        }
	        }catch (IOException ioe) {
					ExceptionZZZ ez = new ExceptionZZZ(ioe);
					throw ez;
			} catch (InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			} catch (GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}			
			bReturn = true;
		}//end main:
		return bReturn;
	}	
	
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,	        
	        CredentialsProvider credentialsProvider,
	        String sBranchIn
	) throws ExceptionZZZ {
		FetchResult objReturn = null;
		main:{
		    try {
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand fetchCommand = git.fetch();

		        if (credentialsProvider != null) {
		            fetchCommand.setCredentialsProvider(credentialsProvider);
		        }
		        
		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*		        		       
		        String branch = "master";
		        if(!StringZZZ.isEmpty(sBranchIn)) branch = sBranchIn;
		        
		        String remoteRef = "refs/heads/" + branch;
		        String localTrackingRef = "refs/remotes/origin/" + branch;
		        
		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		        //+ für "fast forward"
		        fetchCommand.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));

		        objReturn = fetchCommand.call();
	
		        
		        
		        // Optional: Logging / Prüfung
		        if (objReturn.getTrackingRefUpdates().isEmpty()) {
		            System.out.println("Fetch erfolgreich, aber keine Änderungen vorhanden.");
		        } else {
		            System.out.println("Fetch erfolgreich, Änderungen empfangen.");
		        }
	
		    } catch (TransportException te) {
	
		        String msg = te.getMessage();
	
		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
		            System.out.println("Nothing to fetch - Repository ist aktuell.");
		            return null; // bewusst null zurückgeben als Signal
		        }
	
		        // alle anderen Fehler weiterwerfen!
		        ExceptionZZZ ez = new ExceptionZZZ(te);
		        throw ez;
		    }catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			} 
		}//end main:
		 return objReturn;
	}
	
	/**
	 * @param git
	 * @param credentialsProvider
	 * @param sRemoteRepositoryAlias
	 * @param sBranchIn
	 * @return
	 * @throws ExceptionZZZ
	 * 
	 * 
	 		    Minierklaerung:
				siehe .git\config Datei, entsprechende Zeile.
				 
				Das ist ein sogenannter RefSpec (Reference Specification).
				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
				
				Aufbau allgemein:
				[+]<Quelle>:<Ziel>
				
				Also:
				Quelle (Remote-Seite)
				refs/heads/ = alle Branches im Remote-Repository
				 * = Wildcard → alle Branch-Namen
	
				➡️ Bedeutet:
				Hole alle Branches vom Remote
				
				
				Ziel (lokal)
				refs/remotes/origin/ = Remote-Tracking-Branches
				* = gleicher Name wie Quelle
	
				➡️ Bedeutet:
				Speichere sie lokal als origin/branchname
				
				------------
				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
				Mit + sagst du:
				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
				 
	 */
	public static FetchResult fetchIgnoreNothingToFetch(
	        Git git,	        
	        CredentialsProvider credentialsProvider,
	        String sRemoteRepositoryAlias,
	        String sBranchIn
	) throws ExceptionZZZ{
		FetchResult objReturn = null;
		main:{
		    try {
		    	try {
					JgitUtilZZZ.debugForFetch(git);
				} catch (URISyntaxException e) {
					ExceptionZZZ ez = new ExceptionZZZ(e);
					throw ez;
				}
		    	
		    	
		        // =========================
		        // 1. FETCH (nur ein Branch!)
		        // =========================
		        FetchCommand gitCommandFetch = git.fetch();
		        
		        //SSH-Weg: Ohne URL!
		        //dafuer mit Alias
		        gitCommandFetch.setRemote(sRemoteRepositoryAlias);
		        
		        if (credentialsProvider != null) {
		            gitCommandFetch.setCredentialsProvider(credentialsProvider);
		        }
		        
		        //aus .git\config Datei:
		        //      fetch = +refs/heads/*:refs/remotes/origin/*		        		       
		        String sBranch = "master";
		        if(!StringZZZ.isEmpty(sBranchIn)) sBranch = sBranchIn;
		        
		        String remoteRef = "refs/heads/" + sBranch;
		        String localTrackingRef = "refs/remotes/origin/" + sBranch;
		        
		        //!!! KEIN *, das wären mehrere remote Branches... dann bekommt man Probleme beim Mergen... fetchCommand.setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		        //+ für "fast forward"
		        gitCommandFetch.setRefSpecs(new RefSpec("+" + remoteRef + ":" + localTrackingRef));

		        
		        
//		        //aber: vermutlich wird auf dem falschen Branch gearbeitet.
//			    gitCommandFetch.setRefSpecs(
//			    	   //TransportException: Remote does not have refs/heads/main available for fetch.
//			    		//new RefSpec("+refs/heads/main:refs/remotes/origin/main")
//
//			    		//Nach obiger minierklärung ist der erste Teil aber der lokale
//			    		//der zweite Teil ist remote... 
//			    		//das Wort orign taucht nur als Section auf
//			    		//TODOGOON 20260615: Setze diesen String auch korrekt, wie auch die URL
//			    		//                   Bei der Konfiguration
//			    		new RefSpec("+refs/heads/" + sBranch + ":refs/remotes/origin/"+sBranch);	
//			    		
//			    	);
		        
		        
		        objReturn = gitCommandFetch.call();
	
		     
	
		    } catch (TransportException te) {
	
		        String msg = te.getMessage();
	
		        if (msg != null && msg.toLowerCase().contains("nothing to fetch")) {
		            System.out.println("Nothing to fetch - Lokales Repository ist aktuell bzg. Remore Repository.");
		            return null; // bewusst null zurückgeben als Signal
		        }
	
		        // alle anderen Fehler weiterwerfen!
		        ExceptionZZZ ez = new ExceptionZZZ(te);
		        throw ez;
		    }catch(GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			} catch (IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			} 
		}//end main:
		 return objReturn;
	}
	
	//########################################
	//### MERGE
	public static boolean merge(Git git, String sBranchIn) throws ExceptionZZZ, TransportException, CheckoutConflictException{
		boolean bReturn = false;
		main:{
			String sBranch = "master"; // oder dynamisch
			if(!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch = sBranchIn;
			
			MergeResult objMergeResult = null;
			if(sBranch.equals("*")) {
				objMergeResult = JgitUtilZZZ.mergeWithResultFirstBranch(git, true); //wir hatten noch kein Debug
			}else {	
				objMergeResult = JgitUtilZZZ.mergeWithResult(git, sBranch);
			}
			
			if(objMergeResult!=null) {
				bReturn = objMergeResult.getMergeStatus().isSuccessful();
			}
		}//end main:
		return bReturn;
	}
	
	/**	 			
	 * @param git
	 * @param sBranchIn
	 * @return
	 * @throws ExceptionZZZ
	 * 
	 			Minierklaerung:
				siehe .git\config Datei, entsprechende Zeile.
				 
				Das ist ein sogenannter RefSpec (Reference Specification).
				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
				
				Aufbau allgemein:
				[+]<Quelle>:<Ziel>
				
				Also:
				Quelle (Remote-Seite)
				refs/heads/ = alle Branches im Remote-Repository
				 * = Wildcard → alle Branch-Namen
	
				➡️ Bedeutet:
				Hole alle Branches vom Remote
				
				
				Ziel (lokal)
				refs/remotes/origin/ = Remote-Tracking-Branches
				* = gleicher Name wie Quelle
	
				➡️ Bedeutet:
				Speichere sie lokal als origin/branchname
				
				------------
				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
				Mit + sagst du:
				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
	 * @throws CheckoutConflictException 
	 * @throws TransportException 
				
	 */
	public static MergeResult mergeWithResult(Git git, String sBranchIn) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		return mergeWithResult(git, sBranchIn, true);
	}
	public static MergeResult mergeWithResult(Git git, String sBranchIn, boolean bPrintDebug) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		MergeResult objReturn = null;
		main:{		
			if(bPrintDebug) {
				try {
					JgitUtilZZZ.debugForMerge(git);
				} catch (Exception e) {
					ExceptionZZZ ez = new ExceptionZZZ(e);
					throw ez;
				}
			}
			 
			//####################################################################
			//Hole das Ref-Objekt (jetzt direkt statt über das FetchResult-Objekt)
			//Merke: Per fetchResult
			//Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs); //ohne das im Folgenden einzubinden, kommt die Fehlermeldung:    org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
            //Vorschlag von chatGPT, direkt holen: 
			//Ref objRef = git.getRepository().exactRef("refs/remotes/origin/master");
                	 
			//++++++++++++++++++++++++++++++++
			//den richtigen Branch ansteuern
			String sBranch = "master"; // oder dynamisch
			if(!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch = sBranchIn;
			
			//Problem: java.nio.file.InvalidPathException: Illegal char <*> 
			/*refs/remotes/origin/*
			  Das ist aber kein gültiger Git-Refname. Der Stern * ist in Ref-Namen nicht erlaubt. Intern versucht JGit daraus teilweise einen Path zu bilden, wodurch dann die Exception entsteht.
			  
			  Das * wird nur in RefSpecs verwendet, z.B.:
				+refs/heads/*:refs/remotes/origin/*

			  Das bedeutet:
			   Alle Branches unter refs/heads/ nach refs/remotes/origin/ spiegeln.

              Das ist aber eine RefSpec-Syntax, kein echter Refname.
			
			*/
			
			// In einer Schleife alle echten, vorhandenen, lokalen Branches ermitteln.
			// Aber wir geben nur den ersten zurück
			if(sBranch.equals("*")) {
				objReturn = JgitUtilZZZ.mergeWithResultFirstBranch(git, false); //wir hatten schon 1x Debug
			}else {
				objReturn = JgitUtilZZZ.mergeWithResultByBranchShortName(git, sBranch, false);	//wir hatten schon 1x Debug				
			}					       			
		}//end main:
		return objReturn;
	}
	
	/**	 			
	 * @param git
	 * @param sBranchIn
	 * @return
	 * @throws ExceptionZZZ
	 * 
	 			Minierklaerung:
				siehe .git\config Datei, entsprechende Zeile.
				 
				Das ist ein sogenannter RefSpec (Reference Specification).
				Er sagt Git/JGit was von wo nach wo kopiert werden soll.
				
				Aufbau allgemein:
				[+]<Quelle>:<Ziel>
				
				Also:
				Quelle (Remote-Seite)
				refs/heads/ = alle Branches im Remote-Repository
				 * = Wildcard → alle Branch-Namen
	
				➡️ Bedeutet:
				Hole alle Branches vom Remote
				
				
				Ziel (lokal)
				refs/remotes/origin/ = Remote-Tracking-Branches
				* = gleicher Name wie Quelle
	
				➡️ Bedeutet:
				Speichere sie lokal als origin/branchname
				
				------------
				Normalerweise verweigert Git Updates, wenn sie nicht „fast-forward“ sind.
				Mit + sagst du:
				„Überschreibe den lokalen Stand auch dann, wenn History nicht passt“
	 * @throws CheckoutConflictException 
	 * @throws TransportException 
				
	 */
	public static MergeResult mergeWithResultFirstBranch(Git git, boolean bPrintDebug) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		MergeResult objReturn = null;
		main:{	if(bPrintDebug) {		
			 try {
					JgitUtilZZZ.debugForMerge(git);
				} catch (Exception e) {
					ExceptionZZZ ez = new ExceptionZZZ(e);
					throw ez;
				}               }
			 
			//####################################################################
			//Hole das Ref-Objekt (jetzt direkt statt über das FetchResult-Objekt)
			//Merke: Per fetchResult
			//Ref objRef = fetchResult.getAdvertisedRef(sFetchRefs); //ohne das im Folgenden einzubinden, kommt die Fehlermeldung:    org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
            //Vorschlag von chatGPT, direkt holen: 
			//Ref objRef = git.getRepository().exactRef("refs/remotes/origin/master");
                	 
			//++++++++++++++++++++++++++++++++
			//den richtigen Branch ansteuern, hier also den ersten gefundenen.			 								
			
			// In einer Schleife alle echten, vorhandenen, lokalen Branches ermitteln.
			List<String> listaBranch = JgitUtilZZZ.getRepositoryBranchesShortName(git);
			for(String sBranchTemp : listaBranch) {
				objReturn = JgitUtilZZZ.mergeWithResultByBranchShortName(git, sBranchTemp, false); //wir hatten schon 1x Debug
				break; 	// Aber wir geben nur den ersten zurück
			}							       		
		}//end main:
		return objReturn;
	}
	
	public static MergeResult mergeWithResult(File objFileDir, String sBranch) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		MergeResult objReturn = null;
		main:{
			 try {
				Git git = Git.open(objFileDir); 
				System.out.println("Git-Repository opened.");
					
			    objReturn = JgitUtilZZZ.mergeWithResult(git, sBranch);
	        
	        }catch (IOException ioe) {
					ExceptionZZZ ez = new ExceptionZZZ(ioe);
					throw ez;
	        }		
		}//end main:
		return objReturn;
	}
	
	/**
	 * @param git
	 * @param sBranch
	 * @return
	 * @throws ExceptionZZZ
	 * 
	 * 
	 Problem: java.nio.file.InvalidPathException: Illegal char <*> 
				  bei refs/remotes/origin/*
				  Das ist aber kein gültiger Git-Refname. Der Stern * ist in Ref-Namen nicht erlaubt. Intern versucht JGit daraus teilweise einen Path zu bilden, wodurch dann die Exception entsteht.
				  
				  Das * wird nur in RefSpecs verwendet, z.B.:
					+refs/heads/*:refs/remotes/origin/*

				  Das bedeutet:
				  Alle Branches unter refs/heads/ nach refs/remotes/origin/ spiegeln.

                  Das ist aber eine RefSpec-Syntax, kein echter Refname.
	 * @throws TransportException 
	 * @throws CheckoutConflictException 
	 */
	public static MergeResult mergeWithResultByBranchShortName(Git git, String sBranchIn, boolean bPrintDebug) throws ExceptionZZZ, TransportException, CheckoutConflictException {
		MergeResult objReturn = null;
		main:{
			try {				
				String sBranch = "master"; // oder dynamisch
				if(!StringZZZ.isEmptyTrimmed(sBranchIn)) sBranch = sBranchIn;
								
				//!!! Hier sollen nur echte Branchnamen verwendet werden, also kein "*"
				if(sBranch.equals("*")) {
					ExceptionZZZ ez = new ExceptionZZZ("Not a valid branchname '*'", iERROR_PARAMETER_MISSING, JgitUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				if(bPrintDebug) {		
					 try {
							JgitUtilZZZ.debugForMerge(git);
						} catch (Exception e) {
							ExceptionZZZ ez = new ExceptionZZZ(e);
							throw ez;
						}               }
				
				//Merke.. beim Fetch  new RefSpec("+refs/heads/master:refs/remotes/master")
				//das wäre ein Merge auf den gleichen lokalen Branch String sFetchRefs = "refs/heads/" + sBranch;
				//aber ich will ja den lokalen Branch auf den gleichen remote Branch mergen. 
				String sFetchRefs = "refs/remotes/origin/" + sBranch;
				
				/*Merke:
				MergeCommand mergeCommand = git.merge();
				//geht hier nicht, da nur lokal, mergeCommand.setRemote(sUrl);
				//Also so versuchen.
				//mergeCommand.include(git.getRepository().resolve("FETCH_HEAD")); //ABER: Da hier 2 HEADs sind Fehler : org.eclipse.jgit.api.errors.InvalidMergeHeadsException: merge strategy recursive does not support 2 heads to be merged into HEAD
				//Lösungsansatz: direkt den richtigen Branch verwenden
				//also statt... mergeCommand.include(git.getRepository().resolve("refs/remotes/origin/master"));					
				//mergeCommand.include(remoteMaster);
				//mergeCommand.include(objRef); //ohne das kommt die Fehlermeldung:                 org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
				
				//ABER mit 2 verschiedenen .includes(...) gibt es eine Fehlermeldung wie:
				//Verwende remoteMaster= '56cabdc4169eeb600177b05b8540f5bde4ca3533'
				//Verwende remoteMaster= 'AnyObjectId[56cabdc4169eeb600177b05b8540f5bde4ca3533]'
				//basic.zBasic.ExceptionZZZ: org.eclipse.jgit.api.errors.InvalidMergeHeadsException: merge strategy recursive does not support 2 heads to be merged into HEAD
				
				//Die Lösung ist dann nur 1x das .include(...) aufzurufen.
				//Wenn du nur eine nackte ObjectId übergibst:
				//mergeCommand.include(objectId);
				//kennt JGit keinen Branchnamen mehr. Dann fehlen Informationen wie:
				//welcher Remote? welcher Tracking-Branch? welche Reflog-Namen?
				//
				//Darum ist die Ref-Variante sauberer.
				mergeCommand.include(objRef); //ohne das kommt die Fehlermeldung:                 org.eclipse.jgit.api.errors.InvalidConfigurationException: No value for key remote.origin.url found in configuration
				*/
				
				
				
				
				Ref objRef = git.getRepository().exactRef(sFetchRefs);
				//System.out.println("Merge Ref = " + objRef.getName());
				//System.out.println("ObjectId  = " + objRef.getObjectId().getName());
		
		        MergeCommand mergeCommand = git.merge();
		        mergeCommand.include(objRef);
		        mergeCommand.setStrategy(MergeStrategy.RECURSIVE);									 
				objReturn = mergeCommand.call();
			} catch (TransportException te) {
				throw te;
			} catch (CheckoutConflictException coce) {
				throw coce;
				//!!! Diese Exception ist wie eine TransportException von vor dem Merge. 
				//    Die Dateien müssen also anders behandelt werden.
				//System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": CheckoutConflictException... soll dann später verarbeitet werden.");
				//System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": " + coce.getMessage());
				//Mache nix... in der Erwartung, dass das im Status des MergeResult steht.
			 }catch (IOException ioe) {
					ExceptionZZZ ez = new ExceptionZZZ(ioe);
					throw ez;
			} catch (InvalidRemoteException ire) {
				ExceptionZZZ ez = new ExceptionZZZ(ire);
				throw ez;
			} catch (GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
			}		
		}//end main:
		return objReturn;
	}
	
	
	
}
