package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigResolverJGIT extends IConfigStarterLocalJGIT{

	//#####################################################################
	//####### Konfiguration der Argumgentuebergabe von aussen an das Program (s. GetOptZZZ).
	//Merke1: Ein Doppelpunkt bedeutet "es folgt ein Wert". 
	//        Moeglich ist auch ein Pipe "|" nachfolgend. D.h. es gibt dazu keinen Wert.
	//        Entsprechend wird ein Wert ohne "|" gesehen.
	//Merke2: Es ist auch moeglich Argumente mit mehr als 2 Zeichen zu definieren.
	final static String sPATTERN4GIT_RESOLVER_DEFAULT="help|?|status|commit|resolveByStageState|resolveConflict:resolveConflictDeleted|resolveConflictMarked|resolveConflictMarkedCommit|searchConflictFiles:searchConflictFilesByScan:searchConflictFilesMarked|searchConflictFilesDeleted|rl:project:filepath:comment."; 
	                                                //Aktionen;
													//conflict = löse in einer angegebenen die angezeigten Konflikte automatisch
	                                                //           Konflikte werden in einer Datei mit <<<< oder >>>> angezeigt, etc.
													//rl       = Pfad zum lokalen repository
													//project  = Name des Projekts... gleich Verzeichnis unterhalb des lokalen Repositories
													//filepath = Gefolgt von dem Pfad zu der Datei, die den Konflikt hat. 
	
													//Merke: sPATTERN4FLAG_DEFAULT besteht aus
	                                              	  //z = Flags, die dann JSON aehnlich uebergeben werden
													  //zlocal = Lokale Flags, die dann JSON aehnlich uebergeben werden
	final static String sPATTERN_DEFAULT= sPATTERN4GIT_RESOLVER_DEFAULT + IKernelConfigZZZ.sPATTERN4FLAG_DEFAULT;
	final static String sFLAGZ_DEFAULT="{}";      //leerer JSON aehnlicher String für zu setztende Flags, z.B. gefuellt {"DEBUGUI_PANELLABEL_ON":true}
	
	
	public String readFilePath() throws ExceptionZZZ;
	public String getFilePathDefault() throws ExceptionZZZ;
	
	public String readActionResolveByStageState() throws ExceptionZZZ;
	public String readActionResolveConflict() throws ExceptionZZZ;
	public String readActionResolveConflictDeleted() throws ExceptionZZZ;
	public String readActionResolveConflictMarked() throws ExceptionZZZ;
	public String readActionResolveConflictMarkedCommit() throws ExceptionZZZ;	
	
	public String readActionSearchConflictFiles() throws ExceptionZZZ;
	public String readActionSearchConflictFilesMarked() throws ExceptionZZZ;
	public String readActionSearchConflictFilesDeleted() throws ExceptionZZZ;
}
