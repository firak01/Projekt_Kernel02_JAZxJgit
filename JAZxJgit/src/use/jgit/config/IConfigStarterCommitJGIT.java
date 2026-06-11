package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.IKernelConfigZZZ;

public interface IConfigStarterCommitJGIT extends IConfigJGIT{

	//#####################################################################
	//Methoden, die vom Resolver und von den Startern verwendet werden
	public String readActionStatus() throws ExceptionZZZ;	
	public String readActionCommit() throws ExceptionZZZ;
	public String readActionFetch() throws ExceptionZZZ;
		
}
