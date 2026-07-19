package use.jgit.config;

import basic.zBasic.ExceptionZZZ;

public interface IConfigRepositoryManagerJGIT extends IConfigWithAuthentificationJGIT{
	//##################################
	//ist das Repository "bare", das wird für die JUnit-Tests gebraucht
	//und nicht aus einem Kommandozeilen-Argument ausgelesen
	public void isRepositoryBare(boolean bRepositoryBare) throws ExceptionZZZ;
	public boolean isRepositoryBare() throws ExceptionZZZ;
	
}
