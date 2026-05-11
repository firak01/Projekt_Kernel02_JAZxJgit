package use.jgit.resolve;

import java.util.EnumSet;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetFactoryZZZ;
import basic.zBasic.util.datatype.enums.EnumSetUtilZZZ;
import use.jgit.IJgitEnabledZZZ;
import use.jgit.resolve.IJgitResolverEnabled.STRATEGYMERGECONFLICT;

public class EnumSetMappedStrategyMergeConflictUtilZZZ extends EnumSetUtilZZZ{
	private static final long serialVersionUID = 9011362468839990162L;

	public EnumSetMappedStrategyMergeConflictUtilZZZ(){		
	}
	public EnumSetMappedStrategyMergeConflictUtilZZZ(EnumSet<?>enumSetUsed){
		super(enumSetUsed);
	}
	public EnumSetMappedStrategyMergeConflictUtilZZZ(Class<?>objClass)throws ExceptionZZZ{
		super(objClass);
	}
	public EnumSetMappedStrategyMergeConflictUtilZZZ(IEnumSetFactoryZZZ objEnumSetFactory, Class<?> objClass) throws ExceptionZZZ{
		super(objEnumSetFactory, objClass);
	}
	public EnumSetMappedStrategyMergeConflictUtilZZZ(IEnumSetFactoryZZZ objEnumSetFactory){
		this.setEnumFactoryCurrent(objEnumSetFactory);
	}

	public static STRATEGYMERGECONFLICT getStrategyChoosenByFlag(IJgitEnabledZZZ objGitStarter) throws ExceptionZZZ{
		STRATEGYMERGECONFLICT objReturn = null;
		main:{
			boolean bUseStrategyMergeConflictsOurs = objGitStarter.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_OURS);
			boolean bUseStrategyMergeConflictsTheirs = objGitStarter.getFlagLocal(IJgitEnabledZZZ.FLAGZLOCAL.USE_STRATEGY_MERGE_CONFLICT_THEIRS);
					
			if(bUseStrategyMergeConflictsOurs & bUseStrategyMergeConflictsTheirs) {
				ExceptionZZZ ez = new ExceptionZZZ("Ungueltige FlagKombination .", iERROR_PARAMETER_VALUE, EnumSetMappedStrategyMergeConflictUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else if(!(bUseStrategyMergeConflictsOurs | bUseStrategyMergeConflictsTheirs)) {
				objReturn = IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS; //!!! Die Default - Strategie
			}else if(bUseStrategyMergeConflictsOurs) {
				objReturn = IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS;
			}else if(bUseStrategyMergeConflictsTheirs) {
				objReturn = IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS;
			//NEWEST noch nicht implementiert
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Unerwartet FlagKombination .", iERROR_PARAMETER_VALUE, EnumSetMappedStrategyMergeConflictUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return objReturn;
	}
	
	
}
