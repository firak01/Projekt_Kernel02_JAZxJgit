package use.jgit.tool.status;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import basic.zBasic.ExceptionZZZ;

public class GitAutoStageService {

    private final GitStatusMapper mapper = new GitStatusMapper();
    private final GitStagingService staging = new GitStagingService();

    public void stage(Git git) throws ExceptionZZZ {      
        try {
        	GitStatusMap map = mapper.map(git);
			staging.stageAll(git, map);
        }catch (NoWorkTreeException nwte) {
			System.out.println(nwte.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(nwte);
    		throw ez;
		}catch( GitAPIException gae) {
			System.out.println(gae.getMessage());
    		
    		ExceptionZZZ ez = new ExceptionZZZ(gae);
    		throw ez;
		} catch (Exception e) {
			ExceptionZZZ ez = new ExceptionZZZ(e);
			throw ez;
		}
    }
}
