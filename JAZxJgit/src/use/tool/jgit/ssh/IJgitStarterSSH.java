package use.tool.jgit.ssh;

import org.eclipse.jgit.api.Git;

import basic.zBasic.ExceptionZZZ;
import use.tool.jgit.IJgitStarter;

public interface IJgitStarterSSH extends IJgitStarter{
	public boolean pushit(Git git) throws ExceptionZZZ;
	public boolean pullit(Git git) throws ExceptionZZZ;
}
