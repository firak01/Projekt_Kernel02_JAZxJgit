package use.jgit.config;

import java.util.ArrayList;
import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.config.AbstractConfigZZZ;
import basic.zBasic.util.abstractList.ListUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.AbstractKernelConfigZZZ;
import basic.zKernel.GetOptZZZ;
import basic.zKernel.config.help.IKernelConfigHelpLineZZZ;
import basic.zKernel.config.help.KernelConfigHelpLineZZZ;

public abstract class AbstractConfigJGIT extends AbstractConfigZZZ implements IConfigJGIT{

		public AbstractConfigJGIT() throws ExceptionZZZ{
			super();
		}
		public AbstractConfigJGIT(String[] saArg) throws ExceptionZZZ {
			super(saArg); 
		} 
		
		//###############################################
		
		//### aus IConfigZZZ
		@Override
		public String getProjectName() throws ExceptionZZZ {
			return IConfigJGIT.sPROJECT_NAME;
		}
		@Override
		public String getProjectDirectory() throws ExceptionZZZ {
			return IConfigJGIT.sPROJECT_DIRECTORY;
		}
		
	
		//Die verschiedenen Konfigurationen koennen unterschiedlichen Default PATTERN Strings haben.
		@Override 
		public abstract String getPatternStringDefault() throws ExceptionZZZ;
		
		@Override
		public abstract String[] getArgumentArrayDefault() throws ExceptionZZZ;
		
		//Gib die Hilfsinfos als String zurück
		@Override
		public abstract String getHelp() throws ExceptionZZZ;
		//kein setter
		
		//Merke 20260615: Besser eine Liste von Hilf-Objekt-Zeilen auch kein Enum, der Ansatz mit der einfachen Liste der Objekte läßt sich einfacher 
		//                über mehrere Projekte und Vererbungstrukturen umsetzen
		//Also nicht so etwas nutzen wie:
		//public enum LOGSTRINGFORMAT implements IEnumSetMappedStringFormatZZZ{		
		//            und darin:    STRINGTYPE01_STRING_BY_STRING("stringtype01",IStringFormatZZZ.iFACTOR_STRINGTYPE01_STRING_BY_STRING, IStringFormatZZZ.sSEPARATOR_PREFIX_DEFAULT + "[A01]", "%s",IStringFormatZZZ.iARG_STRING,  "[/A01]" + IStringFormatZZZ.sSEPARATOR_POSTFIX_DEFAULT, "Gib den naechsten Log String - sofern vorhanden - in diesem Format aus."),			
		//@Override
		//public abstract List<IKernelConfigHelpLineZZZ>getHelpList() throws ExceptionZZZ;
		
		//### HIER NUR ARGUMENTE, DIE ETWAS MIT DEM KERNEL ZU TUN HABEN
		//### Dafür muss AbstractKernelConfigZZZ die Elternklasse sein, z.B.:
		//@Override
		//public String getApplicationKeyDefault() { ....
		
	}
