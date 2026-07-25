package use.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;

public interface IJgitStarterRemote extends IJgitStarterAuthentificated {
	final static String sREPOSITORY_REMOTE_ALIAS_DEFAULT = IConfigStarterRemoteJGIT.sREPOSITORY_REMOTE_ALIAS_DEFAULT;
		
	//+++ Arbeiten mit dem REMOTE Repository-Object, etc.
	public boolean configureGit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;	
	
	//+++ Arbeit mit dem GitObject, etc.	
	//nur HTTPS+SSH Objekte
	public boolean commitPushit() throws ExceptionZZZ;
	public boolean commitPushit(String sComment) throws ExceptionZZZ;
	public boolean commitPushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;
	public boolean commitPushit(IConfigStarterRemoteJGIT objConfig, String sComment) throws ExceptionZZZ;
	
	public boolean fetchit() throws ExceptionZZZ;
	public boolean fetchit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ;	
	public boolean fetchit(Git git) throws ExceptionZZZ;
	
	public boolean pushit() throws ExceptionZZZ, TransportException, CheckoutConflictException;
	public boolean pushit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ, TransportException, CheckoutConflictException;
	public boolean pushit(Git git) throws ExceptionZZZ;
	
	public boolean pullit() throws Exception, TransportException, CheckoutConflictException;
	public boolean pullit(IConfigStarterRemoteJGIT objConfig) throws ExceptionZZZ, TransportException, CheckoutConflictException;
	public boolean pullit(Git git) throws ExceptionZZZ, TransportException, CheckoutConflictException;	
}
