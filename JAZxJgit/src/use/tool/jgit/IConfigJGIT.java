package use.tool.jgit;

import basic.zBasic.ExceptionZZZ;

public interface IConfigJGIT {

	//#####################################################################
	//####### Konfiguration der Argumgentuebergabe von aussen an das Program (s. GetOptZZZ).
	//Merke1: Ein Doppelpunkt bedeutet "es folgt ein Wert". 
	//        Moeglich ist auch ein Pipe "|" nachfolgend. D.h. es gibt dazu keinen Wert.
	//        Entsprechend wird ein Wert ohne "|" gesehen.
	//Merke2: Es ist auch moeglich Argumente mit mehr als 2 Zeichen zu definieren.
	final static String sPATTERN_DEFAULT="pull|push|ssh|https|rl:pat:rr:rra:z:"; //ConnectionType: HTTPS oder SSH
													  //gefolgt jeweils von einer URL
													  //pat = Personal Access Token fuer HTTPS
													  //rl  = Repository local
													  //rr  = Repository remote
													  //rra  = Repository remote alias. Wie in .git\config Datei angegeben
	                                              //z = Flags, die dann JSON aehnlich uebergeben werden
	final static String sFLAGZ_DEFAULT="{}";      //leerer JSON aehnlicher String für zu setztende Flags, z.B. gefuellt {"DEBUGUI_PANELLABEL_ON":true}
	
	public String readActionPull() throws ExceptionZZZ;
	public String readActionPush() throws ExceptionZZZ;
	
	public String getConnectionTypeDefault() throws ExceptionZZZ;
	public String readConnectionType() throws ExceptionZZZ;	
	public boolean isConnectionTypeSSH() throws ExceptionZZZ;
	public boolean isConnectionTypeHTTPS() throws ExceptionZZZ;
	
	public String readPersonalAccessToken() throws ExceptionZZZ;
	public String getPersonalAccessTokenDefault() throws ExceptionZZZ;
	
	public String readRepositoryLocal() throws ExceptionZZZ;
	public String getRepositoryLocalDefault() throws ExceptionZZZ;
	
	//Verwende das ueber diesen Alias definerte remote Repository
	public String readRepositoryRemoteAlias() throws ExceptionZZZ;
	public String getRepositoryRemoteAliasDefault() throws ExceptionZZZ;
	
	//Die URL zum Repostiory direkte angeben als Alternative zum in .git/config ueber einen Alias definierte remote Repository.
	public String readRepositoryRemote() throws ExceptionZZZ;
	public String getRepositoryRemoteDefaultSSH() throws ExceptionZZZ;
	public String readRepositoryRemoteSSH() throws ExceptionZZZ;
	public String getRepositoryRemoteDefaultHTTPS() throws ExceptionZZZ;
	public String readRepositoryRemoteHTTPS() throws ExceptionZZZ;
		
}
