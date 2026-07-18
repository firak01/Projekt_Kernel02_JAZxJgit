package use.jgit;

import org.eclipse.jgit.api.InitCommand;

import basic.zBasic.ExceptionZZZ;

public class AbstractJgitRepositoryManagerZZZ<T> extends AbstractJgitRepository<T> implements IJgitRepositoryManagerZZZ, IJgitRepositoryEnabledZZZ{
	
	protected boolean bRepositoryBare = false;
	
	
	@Override
	public boolean isRepositoryBare() throws ExceptionZZZ{
		return this.bRepositoryBare;
	}
	
	@Override
	public void isRepositoryBare(boolean bRepositoryBare) throws ExceptionZZZ {
		this.bRepositoryBare = bRepositoryBare;
	}
	
	@Override
	public boolean configureGitCustom(InitCommand objInitCommand) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(this.isRepositoryBare()) {
				objInitCommand.setBare(true);
			}
		}//end main:
		return bReturn;
	}
}
