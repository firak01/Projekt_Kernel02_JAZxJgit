package use.jgit.tool.resolve;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.JgitStarterMain;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;

public class JgitResolverConflictPostUtilZZZ implements IConstantZZZ{

	//#############################
	public static List<File> findFilesWithConflictMarkers(File objFileDirectory) throws ExceptionZZZ {
	    List<File> result = new ArrayList<File>();
	    main:{
	    		boolean bFileExists = FileEasyZZZ.exists(objFileDirectory);
	    		if(!bFileExists) {
	    			ExceptionZZZ ez = new ExceptionZZZ("Directory does not exist: '" + objFileDirectory.getAbsolutePath() + "'", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
	    		}
	    		
	    		boolean bFileIsDirectory = FileEasyZZZ.isDirectory(objFileDirectory);
	    		if(!bFileIsDirectory) {
	    			ExceptionZZZ ez = new ExceptionZZZ("File is no directory: '" + objFileDirectory.getAbsolutePath() + "'", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
	    		}
	    		
	    		//++++++++++++++++++++++++++++++++++++++++++++++++++++
			    File[] files = objFileDirectory.listFiles();
		
			    if (files == null) {
			        return result;
			    }
		
			    for (File file : files) {
		
			        if (".git".equals(file.getName())) {
			            continue;
			        }
		
			        if (file.isDirectory()) {
			            result.addAll(findFilesWithConflictMarkers(file));
			        } else {
			            //if (containsConflictMarkersByRegEx(file)) {
			        	if (JgitResolverConflictPostUtilZZZ.containsConflictMarkersByLinesParsed(file)) {
			                result.add(file);
			            }
			        }
			    }
	    }//end main:
	    return result;
	}

	/** Methode um in einem konkreten File die Konfliktmarker zu analysieren und 
	 *  danach je nach Strategie den einen oder den anderen Blick bestehen zu lassen.
	 *  Wird also nach der Erstehung des Konflikts gemacht und nicht z.B. während eines Pulls
	 * @param content
	 * @param strategy
	 * @return
	 */
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

	/** Robustere Methode die Zeilen der Datei durch eine Art Statefull-Parser zu verarbeiten.
	 *  Alternativ mit RegEx: containsConflictMarkersByRegEx(file), was aber nicht so robust ist.
	 * @param file
	 * @return
	 * @throws ExceptionZZZ
	 */
	static boolean containsConflictMarkersByLinesParsed(File file) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			//Das kann nur bei reinen Textdateien klappen.
			//Aber nicht nur nach der Endung gehen
			boolean bFileText = FileEasyZZZ.isFileText(file);
			if(!bFileText) break main;
			
			//#######################################################
			boolean start = false;
			boolean middle = false;
			boolean end = false;
			
			try {				
				Path path = file.toPath();
				for(String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
				    if(line.startsWith("<<<<<<< ")) start = true;
				    if(line.equals("=======")) middle = true;
				    if(line.startsWith(">>>>>>> ")) end = true;
				    bReturn = start && middle && end;
				    if(bReturn) break;
				}
			}catch(IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
	    	}
			
			
		}//end main:
		return bReturn;
	}

	/** Alternative Methode, falls man RegEx einsetzen will.
	 *  Aber robuster ist containsConflictMarkersByLineParsed(file) 
	 * @param file
	 * @return
	 * @throws ExceptionZZZ
	 */
	private static boolean containsConflictMarkersByRegEx(File file) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			try {
				//Das kann nur bei reinen Textdateien klappen.
				//Aber nicht nur nach der Endung gehen
				boolean bFileText = FileEasyZZZ.isFileText(file);
				if(!bFileText) break main;
				
				//#######################################################
	//	    Pattern pattern = Pattern.compile(
	//	        "^(<<<<<<<|=======|>>>>>>>)",
	//	        Pattern.MULTILINE);
	
			//bei nur einer Zeile dazwischen ...
	//	    Pattern pattern = Pattern.compile(
	//	    "^<<<<<<< .*\\R.*\\R^=======\\R.*\\R^>>>>>>> .*",
	//	    Pattern.MULTILINE);
		    
		    //da mehrere Zeilen dazwischen stehen können
		    Pattern pattern = Pattern.compile(
		    	    "^<<<<<<< .*\\R([\\s\\S]*?)^=======\\R([\\s\\S]*?)^>>>>>>> .*$",
		    	    Pattern.MULTILINE);
		    
			    String content = new String(
			        Files.readAllBytes(file.toPath()),
			        StandardCharsets.UTF_8);
		
			    bReturn = pattern.matcher(content).find();
			}catch(IOException ioe) {
				ExceptionZZZ ez = new ExceptionZZZ(ioe);
				throw ez;
			}
		}//end main:
		return bReturn;
	}

}
