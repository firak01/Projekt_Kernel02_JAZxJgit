package use.jgit;

import basic.zBasic.ExceptionZZZ;
import basic.zKernel.flag.IFlagZEnabledZZZ;

public interface IJgitStarterAuthentificatedEnabledZZZ  extends IFlagZEnabledZZZ{
	//Merke: Die Strategie, wie mit Konflikten umgegangen wird, ist in IJgitResolverEnabled	
	
	public enum FLAGZLOCAL {
		DUMMYFLAGZLOCAL (1 << 0);
		
		private final int mask;
		
		private FLAGZLOCAL(int mask) {
			this.mask = mask;
		}
		
		public int getMask() {
			return mask;
		}
	}
	
	//damit muss man nicht mehr tippen hinter dem enum .name()
	public abstract boolean getFlagLocal(FLAGZLOCAL objEnumFlag) throws ExceptionZZZ;
	public abstract boolean setFlagLocal(FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	public abstract boolean[] setFlagLocal(FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	public abstract boolean proofFlagLocalExists(FLAGZLOCAL objEnumFlag) throws ExceptionZZZ;
	public abstract boolean proofFlagLocalSetBefore(FLAGZLOCAL objEnumFlag) throws ExceptionZZZ;
	
	//#############################################################
	//### FLAGZCustom
	//#############################################################
	public enum FLAGZCUSTOM{
		DUMMYFLAGZCUSTOM
	}
		
	boolean getFlagCustom(FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ;
	boolean setFlagCustom(FLAGZCUSTOM objEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	boolean[] setFlagCustom(FLAGZCUSTOM[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	boolean proofFlagCustomExists(FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ;
	boolean proofFlagCustomSetBefore(FLAGZCUSTOM objEnumFlag) throws ExceptionZZZ;
		
	
	//#############################################################
	//### FLAGZ
	//#############################################################
	public enum FLAGZ{
		DUMMYFLAGZ
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
