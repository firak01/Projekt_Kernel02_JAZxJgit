package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigResolverJGIT {

	//#####################################################################
	//####### Konfiguration der Argumgentuebergabe von aussen an das Program (s. GetOptZZZ).
	//Merke1: Ein Doppelpunkt bedeutet "es folgt ein Wert". 
	//        Moeglich ist auch ein Pipe "|" nachfolgend. D.h. es gibt dazu keinen Wert.
	//        Entsprechend wird ein Wert ohne "|" gesehen.
	//Merke2: Es ist auch moeglich Argumente mit mehr als 2 Zeichen zu definieren.
	final static String sPATTERN4RESOLVER_DEFAULT="conflict|filepath:"; 
	                                                //Aktionen;
													//conflict = löse in einer angegebenen die angezeigten Konflikte automatisch
	                                                //           Konflikte werden in einer Datei mit <<<< oder >>>> angezeigt, etc.
	
													//filepath = Gefolgt von dem Pfad zu der Datei, die den Konflikt hat. 
	
													//Merke: sPATTERN4FLAG_DEFAULT besteht aus
	                                              	  //z = Flags, die dann JSON aehnlich uebergeben werden
													  //zlocal = Lokale Flags, die dann JSON aehnlich uebergeben werden
	final static String sPATTERN_DEFAULT= sPATTERN4RESOLVER_DEFAULT + IKernelConfigZZZ.sPATTERN4FLAG_DEFAULT;
	final static String sFLAGZ_DEFAULT="{}";      //leerer JSON aehnlicher String für zu setztende Flags, z.B. gefuellt {"DEBUGUI_PANELLABEL_ON":true}
	
	
	public String readActionConflict() throws ExceptionZZZ;	
	
	public String readFilePath() throws ExceptionZZZ;
	public String getFilePathDefault() throws ExceptionZZZ;
}
