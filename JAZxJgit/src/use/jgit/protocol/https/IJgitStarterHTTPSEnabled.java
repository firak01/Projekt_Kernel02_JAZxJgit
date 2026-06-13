package use.jgit.protocol.https;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitEnabledZZZ;

public interface IJgitStarterHTTPSEnabled extends IJgitEnabledZZZ{
	// #############################################################
	// ### FLAGZ und FLAG - BASIS METHODEN
	// #############################################################

	public enum FLAGZLOCAL {
		DUMMY //Hier kommen Flags hin, die speziell für HTTPS gedacht sind					
	}
	
	//damit muss man nicht mehr tippen hinter dem enum .name()
	public abstract boolean getFlagLocal(FLAGZLOCAL objEnumFlag) throws ExceptionZZZ;
	public abstract boolean setFlagLocal(FLAGZLOCAL objEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	public abstract boolean[] setFlagLocal(FLAGZLOCAL[] objaEnumFlag, boolean bFlagValue) throws ExceptionZZZ;
	public abstract boolean proofFlagLocalExists(FLAGZLOCAL objEnumFlag) throws ExceptionZZZ;
	public abstract boolean proofFlagSetBefore(FLAGZLOCAL objEnumFlag) throws ExceptionZZZ;		
}
