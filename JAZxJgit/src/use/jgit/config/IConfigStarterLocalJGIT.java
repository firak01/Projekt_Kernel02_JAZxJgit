package use.jgit.config;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigStarterLocalJGIT extends IConfigRepositoryJGIT{
	//##################################
	//Auslesen von Argumenten aus der Kommandozeile

	//... ein moeglicher Kommentar, z.B. für einen (notwendigen) Commit, auch nach dem Aufloesen des Merge-Konflikts
	public String getCommentDefault() throws ExceptionZZZ;
	public String readComment() throws ExceptionZZZ;
	
	//#####################################################################
	//Methoden, die vom Resolver und von den Startern verwendet werden
	public String readActionStatus() throws ExceptionZZZ;	
	public String readActionCommit() throws ExceptionZZZ;
	public String readActionFetch() throws ExceptionZZZ;
}
