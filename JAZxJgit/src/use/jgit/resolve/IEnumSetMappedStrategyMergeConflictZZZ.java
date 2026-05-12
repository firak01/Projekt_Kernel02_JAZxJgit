package use.jgit.resolve;

import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;

public interface IEnumSetMappedStrategyMergeConflictZZZ extends IEnumSetMappedZZZ{
	public static String sENUMNAME="STRATEGYMERGECONFLICT";
	
	//weitere Erweiterungen, speziell für STRATEGYMERGECONFLICT
	public String getStrategyMessage();
	String getDescriptionShort();
	
	//public int getStatusGroupId();

	
}
