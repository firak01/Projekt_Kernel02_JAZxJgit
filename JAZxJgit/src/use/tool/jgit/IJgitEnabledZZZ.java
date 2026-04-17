package use.tool.jgit;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.flag.IFlagZEnabledZZZ;

public interface IJgitEnabledZZZ  extends IFlagZEnabledZZZ{
	enum Strategy4MergeConflict {
	    OURS,
	    THEIRS,
	    NEWEST
	}
	
	//#############################################################
	//### FLAGZ
	//#############################################################
	public enum FLAGZ{
		DUMMY, 
		IGNORE_CHECKOUT_CONFLICTS, // beim PULL / MERGE werden Konflikte unterdrückt, ggs. Verlust lokaler
		USE_STRATEGY_MERGE_CONFLICT_OURS, USE_STRATEGY__MERGE_CONFLICT_THEIRS, USE_STRATEGY_MERGE_CONFLICT_NEWEST
	}
		
	boolean getFlag(FLAGZ objEnumFlag) throws ExceptionZZZ;
	boolean setFlag(FLAGZ objEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	boolean[] setFlag(FLAGZ[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	boolean proofFlagExists(FLAGZ objEnumFlag) throws ExceptionZZZ;
	boolean proofFlagSetBefore(FLAGZ objEnumFlag) throws ExceptionZZZ;
	
	
	
	//#######################################################################################
	// STATUS	
	//............ hier erst einmal nicht .....................
}
