package use.jgit.common;

import java.util.List;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.RepositoryState;

import basic.zBasic.ExceptionZZZ;

public interface IMergeResultResolvedZZZ {
	public MergeResult getMergeResultOriginal() throws ExceptionZZZ;	
	 public void setOriginalResult(MergeResult originalResult)  throws ExceptionZZZ;

	public boolean isConflictsResolved() throws ExceptionZZZ;
	public void isConflictsResolved(boolean conflictsResolved) throws ExceptionZZZ;

	public List<String> getListFile() throws ExceptionZZZ;
	public void setListFile(List<String> listFile) throws ExceptionZZZ;

	public MergeStatus getMergeStatusOriginal() throws ExceptionZZZ;
//* KEINE KONFLIKTEn Setter weil der Status aus dem MergeResult-Objekt geholt wird.
	//public void setMergeStatusOriginal(MergeStatus mergeStatus) throws ExceptionZZZ;

	public Status getGitStatus() throws ExceptionZZZ;
	public void setGitStatus(Status gitStatus) throws ExceptionZZZ;
	
	public RepositoryState getRepositoryState() throws ExceptionZZZ;
	public void setRepositoryState(RepositoryState stateRepo) throws ExceptionZZZ;
	
	//###################
	public boolean isGitStatusClean() throws ExceptionZZZ;
	public boolean isRepositoryStateSafe() throws ExceptionZZZ;
}
