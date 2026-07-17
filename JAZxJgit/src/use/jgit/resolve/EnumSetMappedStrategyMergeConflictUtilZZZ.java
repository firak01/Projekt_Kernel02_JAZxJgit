package use.jgit.resolve;

import java.util.EnumSet;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.merge.MergeStrategy;

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
	
	public static STRATEGYMERGECONFLICT getStrategyByMergeStrategy(MergeStrategy objEnumMergeStrategy) throws ExceptionZZZ{
		STRATEGYMERGECONFLICT objReturn = null;
		main:{
			if(objEnumMergeStrategy==null) {
				ExceptionZZZ ez = new ExceptionZZZ("MergeStrategy", iERROR_PARAMETER_MISSING, EnumSetMappedStrategyMergeConflictUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			if(objEnumMergeStrategy == MergeStrategy.OURS) {
				objReturn = IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS;
			}else if(objEnumMergeStrategy == MergeStrategy.THEIRS) {
				objReturn = IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS;
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Nicht behandelte MergeStrategy: '" + objEnumMergeStrategy.getName() + "'", iERROR_PARAMETER_VALUE, EnumSetMappedStrategyMergeConflictUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return objReturn;
	}

	public static STRATEGYMERGECONFLICT getStrategyChoosenByFlag(IJgitEnabledZZZ objGitStarter) throws ExceptionZZZ{
		STRATEGYMERGECONFLICT objReturn = null;
		main:{
			if(objGitStarter==null) {
				ExceptionZZZ ez = new ExceptionZZZ("IJgitEnabledZZZ Object", iERROR_PARAMETER_MISSING, EnumSetMappedStrategyMergeConflictUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
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
	
	public static CheckoutCommand.Stage getJgitStageAccordingStrategy(IJgitResolverEnabled.STRATEGYMERGECONFLICT objEnumStrategy) throws ExceptionZZZ{
		CheckoutCommand.Stage objReturn = null;
		main:{
			if(objEnumStrategy==null) break main;
			
			if(objEnumStrategy == IJgitResolverEnabled.STRATEGYMERGECONFLICT.THEIRS) {
				objReturn = CheckoutCommand.Stage.THEIRS;
			}else if(objEnumStrategy == IJgitResolverEnabled.STRATEGYMERGECONFLICT.OURS) {
				objReturn = CheckoutCommand.Stage.OURS;
			}else {
				ExceptionZZZ ez = new ExceptionZZZ("Nicht behandelte Strategie: '" + objEnumStrategy.getName() + "'", iERROR_PARAMETER_VALUE, EnumSetMappedStrategyMergeConflictUtilZZZ.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return objReturn;
	}
	
	
}
