package use.tool.jgit;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigJGIT {

	//#####################################################################
	//####### Konfiguration der Argumgentuebergabe von aussen an das Program (s. GetOptZZZ).
	//Merke1: Ein Doppelpunkt bedeutet "es folgt ein Wert". 
	//        Moeglich ist auch ein Pipe "|" nachfolgend. D.h. es gibt dazu keinen Wert.
	//        Entsprechend wird ein Wert ohne "|" gesehen.
	//Merke2: Es ist auch moeglich Argumente mit mehr als 2 Zeichen zu definieren.
	final static String sPATTERN4GIT_DEFAULT="pull|commit|push|commitAndPush|ssh|https|rl:pat:rrh:rra:rrac:project:"; 
													//ConnectionType: HTTPS oder SSH
	
													  //gefolgt jeweils von einer URL
													  //pat = Personal Access Token fuer HTTPS
													  //rl  = Repository local, Basis Pfad
													  //rrh  = Repository remote, Host
													  //rra  = Repository remote, Alias. Wie in .git\config Datei angegeben
													  //rrac = Repository remote, Account
													  //project = Name des Repository, ohne Basis
	
													  //Merke: sPATTERN4FLAG_DEFAULT besteht aus
	                                              	  //z = Flags, die dann JSON aehnlich uebergeben werden
													  //zlocal = Lokale Flags, die dann JSON aehnlich uebergeben werden
	final static String sPATTERN_DEFAULT= sPATTERN4GIT_DEFAULT + IKernelConfigZZZ.sPATTERN4FLAG_DEFAULT;
	final static String sFLAGZ_DEFAULT="{}";      //leerer JSON aehnlicher String für zu setztende Flags, z.B. gefuellt {"DEBUGUI_PANELLABEL_ON":true}
	
	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = "origin";
	
	public String readActionPull() throws ExceptionZZZ;
	public String readActionCommit() throws ExceptionZZZ;
	public String readActionPush() throws ExceptionZZZ;
	public String readActionCommitAndPush() throws ExceptionZZZ;
	
	public String getConnectionTypeDefault() throws ExceptionZZZ;
	public String readConnectionType() throws ExceptionZZZ;	
	public boolean isConnectionTypeSSH() throws ExceptionZZZ;
	public boolean isConnectionTypeHTTPS() throws ExceptionZZZ;
	
	public String readPersonalAccessToken() throws ExceptionZZZ;
	public String getPersonalAccessTokenDefault() throws ExceptionZZZ;
	
	public String readRepositoryLocal() throws ExceptionZZZ;
	public String getRepositoryLocalBaseDefault() throws ExceptionZZZ;
	
	
	//Die URL zum Repository direkte angeben als Alternative zum in .git/config ueber einen Alias definierte remote Repository.
	//Hier erst einmal eine Basis URL/ein Basis Verzeichnis....
	public String readRepositoryRemoteHost() throws ExceptionZZZ;
	public String getRepositoryRemoteHostDefault() throws ExceptionZZZ;
	
	//Verwende das ueber diesen Alias definerte remote Repository
	public String readRepositoryRemoteAlias() throws ExceptionZZZ;
	public String getRepositoryRemoteAliasDefault() throws ExceptionZZZ;
		
	//Verwende den Accountnamen
	public String readRepositoryRemoteAccount() throws ExceptionZZZ;
	public String getRepositoryRemoteAccountDefault() throws ExceptionZZZ;
	
	//... daran kommt dann noch das Projektverzeichnis
	public String getRepositoryProjectNameDefault() throws ExceptionZZZ;
	public String readRepositoryProjectName() throws ExceptionZZZ;
	
		
}
