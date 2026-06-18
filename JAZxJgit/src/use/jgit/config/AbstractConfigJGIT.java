package use.jgit.config;

import java.util.List;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.config.AbstractConfigZZZ;
import basic.zKernel.config.help.IKernelConfigHelpLineZZZ;

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
		public abstract String createHelp() throws ExceptionZZZ;
		
		@Override
		public List<IKernelConfigHelpLineZZZ> getHelpList() throws ExceptionZZZ{
			return super.getHelpList();
		}
				
	}
