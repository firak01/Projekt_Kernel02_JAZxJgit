package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;
import use.jgit.protcol.git.JgitStarterGIT;
import use.jgit.protocol.https.JgitStarterHTTPS;
import use.jgit.protocol.ssh.JgitStarterSSH;

public interface IConfigStarterRemoteJGIT extends IConfigStarterLocalJGIT{

	//#####################################################################
	//####### Konfiguration der Argumgentuebergabe von aussen an das Program (s. GetOptZZZ).
	//Merke1: Ein Doppelpunkt bedeutet "es folgt ein Wert". 
	//        Moeglich ist auch ein Pipe "|" nachfolgend. D.h. es gibt dazu keinen Wert.
	//        Entsprechend wird ein Wert ohne "|" gesehen.
	//Merke2: Es ist auch moeglich Argumente mit mehr als 2 Zeichen zu definieren.
	final static String sPATTERN4GIT_REMOTE_DEFAULT= JgitStarterSSH.sPROTOCOL +"|" + JgitStarterHTTPS.sPROTOCOL + "|" + JgitStarterGIT.sPROTOCOL + "|help|?|status|pull|commit|fetch|push|commitAndPush|rl:pat:rrh:rra:rrac:project:branch:comment:"; 
													  //Aktionen, ggfs. kombinierbar, aber meist nur 1 pro start:
	                                                  //                pull, commit, fetch, push, commitAndPush 
													  //ConnectionType: HTTPS oder SSH oder GIT, damit kann man die angegebene URL übersteuern
	
													  //gefolgt jeweils von einer URL
													  //pat = Personal Access Token fuer HTTPS
													  //rl  = Repository local, Basis Pfad
													  //rrh  = Repository remote, Host
													  //rra  = Repository remote, Alias. Wie in .git\config Datei angegeben
													  //rrac = Repository remote, Account
													  //project = Name des Repository, ohne Basis
													  //comment = Kommentar, z.B. für einen Commit
	
													  //Merke: sPATTERN4FLAG_DEFAULT besteht aus
	                                              	  //z = Flags, die dann JSON aehnlich uebergeben werden
													  //zlocal = Lokale Flags, die dann JSON aehnlich uebergeben werden
	final static String sPATTERN_DEFAULT= sPATTERN4GIT_REMOTE_DEFAULT + IKernelConfigZZZ.sPATTERN4FLAG_DEFAULT;
	final static String sFLAGZ_DEFAULT="{}";      //leerer JSON aehnlicher String für zu setztende Flags, z.B. gefuellt {"DEBUGUI_PANELLABEL_ON":true}
	
	public String readActionStatus() throws ExceptionZZZ;
	public String readActionPull() throws ExceptionZZZ;
	public String readActionCommit() throws ExceptionZZZ;
	public String readActionFetch() throws ExceptionZZZ;
	public String readActionPush() throws ExceptionZZZ;
	public String readActionCommitAndPush() throws ExceptionZZZ;
	
	public String getConnectionTypeDefault() throws ExceptionZZZ;
	public String readConnectionType() throws ExceptionZZZ;	
	public boolean isConnectionTypeSSH() throws ExceptionZZZ;
	public boolean isConnectionTypeHTTPS() throws ExceptionZZZ;
	public boolean isConnectionTypeGIT() throws ExceptionZZZ;
	
	public String readPersonalAccessToken() throws ExceptionZZZ;
	public String getPersonalAccessTokenDefault() throws ExceptionZZZ;
	
	//Die URL zum Repository direkte angeben als Alternative zum in .git/config ueber einen Alias definierte remote Repository.
	//Hier erst einmal eine Basis URL/ein Basis Verzeichnis....
	public String readRepositoryRemoteHost() throws ExceptionZZZ;
	public String getRepositoryRemoteHostDefault() throws ExceptionZZZ;
	
//	//Verwende das ueber diesen Alias definerte remote Repository
//	public String readRepositoryRemoteAlias() throws ExceptionZZZ;
//	public String getRepositoryRemoteAliasDefault() throws ExceptionZZZ;
		
	//Verwende den Accountnamen
	public String readRepositoryRemoteAccount() throws ExceptionZZZ;
	public String getRepositoryRemoteAccountDefault() throws ExceptionZZZ;
		
}
