package use.jgit.resolve;

import java.util.EnumSet;

import basic.zBasic.ExceptionZZZ;

public interface IJgitResolverEnabled {
	
	public enum STRATEGYMERGECONFLICT implements IEnumSetMappedStrategyMergeConflictZZZ{
		OURS("isours","ZZZ: Strategy - Lokale Datei bleibt erhalten",""),
		THEIRS("istheirs","ZZZ: Strategy - Remote Datei bleibt erhalten",""),		
		NEWEST("isnewest","ZZZ: Strategy - neueste Datei bleibt erhalten","noch nicht implementiert");

		private String sAbbreviation,sStrategyMessage,sDescription;
	
		//#############################################
		//#### Konstruktoren
		//Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
		//In der Util-Klasse habe ich aber einen Workaround gefunden.
		STRATEGYMERGECONFLICT(String sAbbreviation, String sStatusMessage, String sDescription) {		
		    this.sAbbreviation = sAbbreviation;
		    this.sStrategyMessage = sStatusMessage;
		    this.sDescription = sDescription;
		}
				
		@Override
		public String getAbbreviation() {
		 return this.sAbbreviation;
		}
		
		@Override
		public String getStrategyMessage() {
			 return this.sStrategyMessage;
		}
		
		public EnumSet<?>getEnumSetUsed(){
			return STRATEGYMERGECONFLICT.getEnumSet();
		}
	
		/* Die in dieser Methode verwendete Klasse für den ...TypeZZZ muss immer angepasst werden. */
		@SuppressWarnings("rawtypes")
		public static <E> EnumSet getEnumSet() {
			
		 //Merke: Das wird anders behandelt als FLAGZ Enumeration.
			//String sFilterName = "FLAGZ"; /
			//...
			//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
			
			//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
			Class<STRATEGYMERGECONFLICT> enumClass = STRATEGYMERGECONFLICT.class;
			EnumSet<STRATEGYMERGECONFLICT> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
					
			Enum[]objaEnum = (Enum[]) enumClass.getEnumConstants();
			for(Object obj : objaEnum){
				//System.out.println(obj + "; "+obj.getClass().getName());
				set.add((STRATEGYMERGECONFLICT) obj);
			}
			return set;
			
		}
	
		//TODO: Mal ausprobieren was das bringt
		//Convert Enumeration to a Set/List
		private static <E extends Enum<E>>EnumSet<E> toEnumSet(Class<E> enumClass,long vector){
			  EnumSet<E> set=EnumSet.noneOf(enumClass);
			  long mask=1;
			  for (  E e : enumClass.getEnumConstants()) {
			    if ((mask & vector) == mask) {
			      set.add(e);
			    }
			    mask<<=1;
			  }
			  return set;
			}
	
		//+++ Das könnte auch in einer Utility-Klasse sein.
		//the valueOfMethod <--- Translating from DB
		public static STRATEGYMERGECONFLICT fromAbbreviation(String s) {
		for (STRATEGYMERGECONFLICT state : values()) {
		   if (s.equals(state.getAbbreviation()))
		       return state;
		}
		throw new IllegalArgumentException("Not a correct abbreviation: " + s);
		}
	
		//##################################################
		//#### Folgende Methoden bring Enumeration von Hause aus mit. 
				//Merke: Diese Methoden können aber nicht in eine abstrakte Klasse verschoben werden, zum daraus Erben. Grund: Enum erweitert schon eine Klasse.
		@Override
		public String getName() {	
			return super.name();
		}
	
		@Override
		public String toString() {//Mehrere Werte mit # abtennen
		    return this.sAbbreviation+"="+this.sDescription;
		}
	
		@Override
		public int getIndex() {
			return ordinal();
		}
	
		//### Folgende Methoden sind zum komfortablen Arbeiten gedacht.
		@Override
		public int getPosition() {
			return getIndex()+1; 
		}
	
		@Override
		public String getDescription() {
			return this.sDescription;
		}

		//+++++++++++++++++++++++++
	}//End internal Class
	//##### END STRETAGYMERGECONFLICT DIRECT eingebunden #######################################
	
	// #############################################################
	// ### FLAGZ und FLAG - BASIS METHODEN
	// #############################################################
	public enum FLAGZLOCAL {
		DUMMYFLAGZLOCAL (1 << 0), 		
		USE_STRATEGY_MERGE_CONFLICT_OURS    (1 << 2),
		USE_STRATEGY_MERGE_CONFLICT_THEIRS (1 << 3), 
		USE_STRATEGY_MERGE_CONFLICT_NEWEST  (1 << 4),
		; //beim PULL / MERGE wird erst versucht zu Mischen. Konflikte werden danach gemäß Strategie aufgelöst was gewinnt.
		
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
