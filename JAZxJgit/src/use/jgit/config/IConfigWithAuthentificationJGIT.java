package use.jgit.config;

import java.io.File;

import basic.zBasic.ExceptionZZZ;

public interface IConfigWithAuthentificationJGIT  extends IConfigStarterLocalJGIT{
	//##################################
	//Auslesen von Argumenten aus der Kommandozeile
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
					
	//Verwende den Accountnamen
	public String readRepositoryRemoteAccount() throws ExceptionZZZ;
	public String getRepositoryRemoteAccountDefault() throws ExceptionZZZ;
	
}
