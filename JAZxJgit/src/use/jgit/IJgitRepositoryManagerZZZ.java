package use.jgit;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigStarterLocalJGIT;

public interface IJgitRepositoryManagerZZZ {
	//GETTER / SETTER
	public boolean isRepositoryBare() throws ExceptionZZZ;
	public void isRepositoryBare(boolean bRepositoryBare) throws ExceptionZZZ;
	
	//KONFIGURATION
	public boolean configureGit(IConfigRepositoryManagerJGIT objConfig) throws ExceptionZZZ;
	public boolean configureGitCustom(IConfigRepositoryManagerJGIT objConfig) throws ExceptionZZZ;
	
	//METHODEN
	public boolean cloneRepositoryTo(File obFileDirectory) throws ExceptionZZZ;
	public boolean cloneRepositoryTo(IConfigRepositoryManagerJGIT objConfig, File obFileDirectory) throws ExceptionZZZ;
}
