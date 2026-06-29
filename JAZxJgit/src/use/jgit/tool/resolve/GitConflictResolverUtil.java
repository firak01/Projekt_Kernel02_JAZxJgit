package use.jgit.tool.resolve;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.resolve.JgitResolverLocal;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;

public class GitConflictResolverUtil implements IConstantZZZ {
    public static String resolveConflicts(String content, STRATEGYMERGECONFLICT strategy) {
        StringBuilder result = new StringBuilder();

        String[] lines = content.split("\\r?\\n");

        boolean inConflict = false;
        boolean isOursPart = false;
        boolean isTheirsPart = false;

        StringBuilder oursBuffer = new StringBuilder();
        StringBuilder theirsBuffer = new StringBuilder();

        for (String line : lines) {

            if (line.startsWith("<<<<<<<")) {
                inConflict = true;
                isOursPart = true;
                isTheirsPart = false;
                oursBuffer.setLength(0);
                theirsBuffer.setLength(0);
                continue;
            }

            if (inConflict && line.startsWith("=======")) {
                isOursPart = false;
                isTheirsPart = true;
                continue;
            }

            if (inConflict && line.startsWith(">>>>>>>")) {
                // Konfliktblock endet → entscheiden
                if (strategy == STRATEGYMERGECONFLICT.OURS) {
                    result.append(oursBuffer);
                } else {
                    result.append(theirsBuffer);
                }

                inConflict = false;
                isOursPart = false;
                isTheirsPart = false;
                continue;
            }

            if (inConflict) {
                if (isOursPart) {
                    oursBuffer.append(line).append("\n");
                } else if (isTheirsPart) {
                    theirsBuffer.append(line).append("\n");
                }
            } else {
                result.append(line).append("\n");
            }
        }

        return result.toString();
    }
    
    public static boolean resolveDeleted(Git git, File objFile, STRATEGYMERGECONFLICT strategy) throws ExceptionZZZ{
       boolean bReturn = false;
       main:{
    	   try {
	    	   if(objFile==null) {
					ExceptionZZZ ez = new ExceptionZZZ("FileObject", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
	    	   
	    	   if(strategy==null) {
					ExceptionZZZ ez = new ExceptionZZZ("StrategyObject", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
	    	   
	    	   boolean bFileExists = FileEasyZZZ.exists(objFile);
				if(!bFileExists) {
					ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePathTotal='" + objFile.getAbsolutePath() + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
				if(!bIsFile) {
					ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePathTotal='" + objFile.getAbsolutePath() + "'", iERROR_PARAMETER_MISSING, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//###############################################
				
				
				String sFilepathTotal = objFile.getAbsolutePath();
				
				//Code Snippet:
				//Merke: das ist Strategieabhängig und StageState abhängig, was passieren soll.					
				//Fazit: Wg. der obigen Prüfungen, ob die Datei existiert... Lokal ist immer da!!!
				 if (strategy == STRATEGYMERGECONFLICT.OURS) {
					//Die Lokale Datei soll erhalten bleiben.
						git.add()
						   .addFilepattern(sFilepathTotal)
						   .call();				
				 }else if( strategy == STRATEGYMERGECONFLICT.THEIRS) {
					//Die Löschung soll gewinnen (rm)
						git.rm()
						   .addFilepattern(sFilepathTotal)
						   .call();
											
				 }else {
					 System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Unerwartetet Strategy: '" + strategy.getName() + "'");
					 ExceptionZZZ ez = new ExceptionZZZ("Unerwartete Strategy: '" +strategy.getName() + "'", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
					 throw ez;
				 }	
				 
				 
			} catch (NoFilepatternException nfe) {
				ExceptionZZZ ez = new ExceptionZZZ(nfe);
				throw ez;
			} catch (GitAPIException gae) {
				ExceptionZZZ ez = new ExceptionZZZ(gae);
				throw ez;
    	   }catch(Exception e) {
		    	ExceptionZZZ ez = new ExceptionZZZ(e);
		    	throw ez;
    	   }
    	   
    	   bReturn = true;
       }//end main:
       return bReturn;
    }
}
