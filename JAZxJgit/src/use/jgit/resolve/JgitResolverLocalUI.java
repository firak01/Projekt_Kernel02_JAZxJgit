package use.jgit.resolve;

import java.io.File;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.ArrayListUtilZZZ;
import basic.zBasic.util.datatype.string.StringArrayZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.system.Syso;

public class JgitResolverLocalUI implements IConstantZZZ{

	public static void printResolveResultListFile(String sTitle, List<File> listaFile) throws ExceptionZZZ{
		printResolveResultListFile(sTitle, listaFile, true);
	}
	public static void printResolveResultListFile(String sTitle, List<File> listaFile, boolean bPrintOutput) throws ExceptionZZZ{
		if(!bPrintOutput) return;
		
        Syso.println("\n" + sTitle);
        if(listaFile==null || listaFile.isEmpty()){
            Syso.println("* NO FILE");
        }else{
        	for(File objFile : listaFile) {
				Syso.println("* " + objFile.getAbsolutePath());
			}
        }
    }
	
   
	public static void printResolveResultListString(String sTitle, List<String> listasPathInRepository) throws ExceptionZZZ{
		printResolveResultListString(sTitle, listasPathInRepository, true);
	}
	
    public static void printResolveResultListString(String sTitle, List<String> listasPathInRepository, boolean bPrintOutput) throws ExceptionZZZ{
    	if(!bPrintOutput) return;
		
        Syso.println("\n" + sTitle);
        if(listasPathInRepository==null || listasPathInRepository.isEmpty()){
            Syso.println("* NO FILE");
        }else{
            String[] saFile = ArrayListUtilZZZ.toStringArray(listasPathInRepository);
            saFile = StringArrayZZZ.plusString("* ", saFile);
            Syso.println(StringArrayZZZ.implode(saFile, StringZZZ.crlf()));
        }
    }
    
    public static void printResolveResultListAny(String sTitle, List<File> listaFile, List<String> listasPathInRepository) throws ExceptionZZZ{
    	printResolveResultListAny(sTitle, listaFile,listasPathInRepository, true);
    }
    public static void printResolveResultListAny(String sTitle, List<File> listaFile, List<String> listasPathInRepository, boolean bPrintOutput) throws ExceptionZZZ{
    	if(!bPrintOutput) return;
    	
        Syso.println("\n" + sTitle);
        if((listaFile==null || listaFile.isEmpty()) && (listasPathInRepository==null || listasPathInRepository.isEmpty())) {
            Syso.println("* NO FILE");
        }else{
        	Syso.println("Dateien mit Konfliktmarkierung in Datei:");
        	if(listaFile!=null) {
        		for(File objFile : listaFile) {
        			Syso.println("* " + objFile.getAbsolutePath());
        		}
        	}
        	
        	Syso.println("Dateien mit Konfliktmarkierung im Git-Index:");
        	if(listasPathInRepository!=null && !listasPathInRepository.isEmpty()){                
                String[] saFile = ArrayListUtilZZZ.toStringArray(listasPathInRepository);
                saFile = StringArrayZZZ.plusString("* ", saFile);
                Syso.println(StringArrayZZZ.implode(saFile, StringZZZ.crlf()));
            }
        }
    }
    

    public static void printResolveResultSingle(String sTitle, String sFilePath) throws ExceptionZZZ{
    	printResolveResultSingle(sTitle, sFilePath, true);
    }
    
    public static void printResolveResultSingle(String sTitle, String sFilePath, boolean bPrintOutput) throws ExceptionZZZ{
    	if(!bPrintOutput) return;
    	
        Syso.println("\n" + sTitle);
        if(StringZZZ.isEmptyTrimmed(sFilePath)){
            Syso.println("* NO FILE");
        }else{
            Syso.println("* " + sFilePath);
        }
    }

    public static void printResolveStrategyHint(String sTitle, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ{
    	printResolveStrategyHint(sTitle, objEnumStrategyMergeConflict, true);
    }
    public static void printResolveStrategyHint(String sTitle, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict, boolean bPrintOutput) throws ExceptionZZZ{
    	printResolveStrategyHint(sTitle, "", objEnumStrategyMergeConflict, bPrintOutput);
    }
    
    public static void printResolveStrategyHint(String sTitle, String sFilePath, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ{
    	printResolveStrategyHint(sTitle, sFilePath, objEnumStrategyMergeConflict, true);
    }
    public static void printResolveStrategyHint(String sTitle, String sFilePath, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict, boolean bPrintOutput) throws ExceptionZZZ{
    	if(!bPrintOutput) return;
    	
    	String sLog;
        if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
			sLog="\nErfolgreiche Konfliktauflösung. " + sTitle + "\n"
					  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
					  + "HINWEIS: \t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt. Ein zusätzlicher Commit muss ggfs. noch gemacht werden.";
		}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
			sLog="\nErfolgreiche Konfliktauflösung. " + sTitle + "\n"
					  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
					  + "HINWEIS: \t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Ein Commit und PUSH muss  noch gemacht werden.";						
		}else {
			Syso.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
			ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocalGIT.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
        Syso.println(sLog);
    }
    
    //#################################################
    //Beim Pullit findet im Ignorieren Fall auch etwas mit dem Resolver statt. 
    //Muss aber leicht anderen Strategiehinweis-Text bekommen
    public static void printIgnoreStrategyHint(String sTitle, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ{
    	printIgnoreStrategyHint(sTitle, objEnumStrategyMergeConflict, true);
    }
    public static void printIgnoreStrategyHint(String sTitle, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict, boolean bPrintOutput) throws ExceptionZZZ{
    	printResolveStrategyHint(sTitle, "", objEnumStrategyMergeConflict, bPrintOutput);
    }
    public static void printIgnoreStrategyHint(String sTitle, String sFilePath, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ{
    	printIgnoreStrategyHint(sTitle, sFilePath, objEnumStrategyMergeConflict, true);
    }
    public static void printIgnoreStrategyHint(String sTitle, String sFilePath, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict, boolean bPrintOutput) throws ExceptionZZZ{
    	if(!bPrintOutput) return;
    	
    	String sLog;
        if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
			sLog="\nErfolgreiches Ignorieren von Konflikten. " + sTitle + "\n"
					  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
					  + "HINWEIS: \t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt. Ein zusätzlicher Commit muss ggfs. noch gemacht werden.";
		}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
			sLog="\nErfolgreiches Ignorieren von Konflikten. " + sTitle + "\n"
					  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
					  + "HINWEIS: \t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Ein Commit und PUSH muss  noch gemacht werden.";						
		}else {
			Syso.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
			ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocalGIT.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
        Syso.println(sLog);
    }
    
    
}
