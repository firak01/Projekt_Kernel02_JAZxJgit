package use.jgit.config;

import basic.zBasic.config.IConfigZZZ;

public interface IConfigJGIT extends IConfigZZZ{
	//#################################################
	//Merke: Die Konstanten sind meist nicht final, damit sie von der konkreten Konfiguration
	//       ueberschrieben werden koennen.
	//       Final sind die fuer den Kernel selbst wichtige Konstanten
    static String sPROJECT_DIRECTORY = "Projekt_Kernel02_JAZxJgit";
    static String sPROJECT_NAME = "JAZxJgit";
	
	final static String sBRANCH_DEFAULT = "master";
	final static String sBRANCH_ALL = "*";
	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = "origin";
}
