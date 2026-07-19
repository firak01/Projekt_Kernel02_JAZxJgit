package use.jgit;

import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;

public interface IJgitStarterAuthentificated  extends IJgitStarterLocal{
	
	//+++ Arbeit mit dem GitObject, etc.	
	public CredentialsProvider getCredentialsProviderObject() throws ExceptionZZZ;
	public void setCredentialsProviderObject(CredentialsProvider objCredentialsProvider) throws ExceptionZZZ;	
}
