package use.jgit.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.RepositoryState;

import basic.zBasic.ExceptionZZZ;

/**Das Problem: Das Merge-Result-Objekt bekommt von einer Konflikt Lösung nichts mit.
					Es wird davon abgeraten einen neuen Merge zu machen um dies zu aktualisieren.
					
					Vorgeschlagener Lösungsweg:
					Eine eigene Rückgabeklasse mit dem ursprünglichen MergeResult Object 
					und dem Status conflicsResolved=true
					
					Ich gebe dann auch noch weitere Objekte mit, die dann abgefragt werden können,
					um eine erfolgreiche Konfliktauflösung zu bestätigen.
					
 * @author Fritz Lindhauer
 *
 */
public class MergeResultResolvedZZZ implements IMergeResultResolvedZZZ{
	 

	private MergeResult mergeResultOriginal=null;
	 private boolean bConflictsResolved=false;
	 
	 private List<String>listsFile=null;
	 
	 MergeStatus mergeStatus = null;
	 Status gitStatus = null;
	 RepositoryState repositoryState = null;
	 
	 //### Konsturktoren
	 public MergeResultResolvedZZZ() {		 
	 }
	 
	 
	//### GETTER / SETTER #######################
	@Override
	public MergeResult getMergeResultOriginal() throws ExceptionZZZ {
		return this.mergeResultOriginal;
	}
	
	@Override
	 public void setOriginalResult(MergeResult mergeResultOriginal) throws ExceptionZZZ {
		this.mergeResultOriginal = mergeResultOriginal;
	}

	 
	@Override
	public boolean isConflictsResolved() throws ExceptionZZZ {
		return this.bConflictsResolved;
	}

	@Override
	public void isConflictsResolved(boolean bConflictsResolved) throws ExceptionZZZ {
		this.bConflictsResolved = bConflictsResolved;
	}

	@Override
	public List<String> getListFile() throws ExceptionZZZ {
		if(this.listsFile==null) {
			List<String> listsFile = new ArrayList<String>();
			this.listsFile = listsFile;
		}
		return this.listsFile;
	}
	
	@Override
	public void setListFile(List<String> listsFile) throws ExceptionZZZ {
		this.listsFile = listsFile;
	}

	@Override
	public MergeStatus getMergeStatusOriginal() throws ExceptionZZZ {
		if(this.getMergeResultOriginal()!=null) {
			return this.getMergeResultOriginal().getMergeStatus();
		}
		return null;
	}

	@Override
	public RepositoryState getRepositoryState() throws ExceptionZZZ {
		return this.repositoryState;
	}

	@Override
	public void setRepositoryState(RepositoryState repositoryState) throws ExceptionZZZ {
		this.repositoryState = repositoryState;
	}


	@Override
	public Status getGitStatus() throws ExceptionZZZ {
		return this.gitStatus;
	}

	@Override
	public void setGitStatus(Status gitStatus) throws ExceptionZZZ {
		this.gitStatus = gitStatus;
	}
	
	//#####################
	@Override
	public boolean isGitStatusClean() throws ExceptionZZZ{
		boolean bReturn = false;
		if(this.getGitStatus()!=null) {
			bReturn = this.getGitStatus().isClean();
		}
		return bReturn;
	}
	
	
	@Override
	public boolean isRepositoryStateSafe() throws ExceptionZZZ{
		boolean bReturn = false;
		if(this.getRepositoryState()!=null) {
			if(this.getRepositoryState().equals(RepositoryState.SAFE)){
				bReturn = true;
			}			
		}
		return bReturn;
	}

}
