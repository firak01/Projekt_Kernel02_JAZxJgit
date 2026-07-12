package use.jgit.resolve;

import java.io.File;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.ArrayListUtilZZZ;
import basic.zBasic.util.datatype.string.StringArrayZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

public class JgitResolverLocalUI implements IConstantZZZ{

	public static void printResolveResultListFile(String sTitle, List<File> listaFile) throws ExceptionZZZ{
        System.out.println("\n" + sTitle);
        if(listaFile==null || listaFile.isEmpty()){
            System.out.println("* NO FILE");
        }else{
        	for(File objFile : listaFile) {
				System.out.println("* " + objFile.getAbsolutePath());
			}
        }
    }
	
   
    public static void printResolveResultListString(String sTitle, List<String> listasPathInRepository) throws ExceptionZZZ{
        System.out.println("\n" + sTitle);
        if(listasPathInRepository==null || listasPathInRepository.isEmpty()){
            System.out.println("* NO FILE");
        }else{
            String[] saFile = ArrayListUtilZZZ.toStringArray(listasPathInRepository);
            saFile = StringArrayZZZ.plusString("* ", saFile);
            System.out.println(StringArrayZZZ.implode(saFile, StringZZZ.crlf()));
        }
    }
    
    public static void printResolveResultListAny(String sTitle, List<File> listaFile, List<String> listasPathInRepository) throws ExceptionZZZ{
        System.out.println("\n" + sTitle);
        if((listaFile==null || listaFile.isEmpty()) && (listasPathInRepository==null || listasPathInRepository.isEmpty())) {
            System.out.println("* NO FILE");
        }else{
        	System.out.println("Dateien mit Konfliktmarkierung in Datei:");
        	if(listaFile!=null) {
        		for(File objFile : listaFile) {
        			System.out.println("* " + objFile.getAbsolutePath());
        		}
        	}
        	
        	System.out.println("Dateien mit Konfliktmarkierung im Git-Index:");
        	if(listasPathInRepository!=null && !listasPathInRepository.isEmpty()){                
                String[] saFile = ArrayListUtilZZZ.toStringArray(listasPathInRepository);
                saFile = StringArrayZZZ.plusString("* ", saFile);
                System.out.println(StringArrayZZZ.implode(saFile, StringZZZ.crlf()));
            }
        }
    }
    

    public static void printResolveResultSingle(String sTitle, String sFilePath) throws ExceptionZZZ{
        System.out.println("\n" + sTitle);
        if(StringZZZ.isEmptyTrimmed(sFilePath)){
            System.out.println("* NO FILE");
        }else{
            System.out.println("* " + sFilePath);
        }
    }

    public static void printStrategyHint(IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ{
    	printStrategyHint("", objEnumStrategyMergeConflict);
    }
    
    public static void printStrategyHint(String sFilePath, IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategyMergeConflict) throws ExceptionZZZ{
        String sLog;
        if(objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS)) {
			sLog="\nErfolgreiche Konfliktauflösung.\n"
					  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS.name() + "\n"
					  + "HINWEIS: \t\tRemote Änderung wurde übernommen. Die lokale Änderung wurde entfernt. Ein zusätzlicher Commit muss ggfs. noch gemacht werden.";
		}else if (objEnumStrategyMergeConflict.equals(IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS)) {
			sLog="\nErfolgreiche Konfliktauflösung.\n"
					  + "Verwendete Stategie: \t" + objEnumStrategyMergeConflict.getName() + "\n"//IJgitResolverEnabled.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS.name() + "\n"
					  + "HINWEIS: \t\tLokale Änderung wurde übernommen. Diese ist noch nicht auf dem Server. Ein Commit und PUSH muss  noch gemacht werden.";						
		}else {
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "Keine gueltige Strategy per Flag gesetzt.");
			ExceptionZZZ ez = new ExceptionZZZ("Keine gueltige Strategy per Flag gesetzt.", iERROR_PARAMETER_VALUE, JgitResolverLocal.class, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
        System.out.println(sLog);
    }
}
