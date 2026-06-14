package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.AbstractKernelConfigZZZ;
import basic.zKernel.GetOptZZZ;

public abstract class AbstractConfigJGIT extends AbstractKernelConfigZZZ implements IConfigJGIT{

		public AbstractConfigJGIT() throws ExceptionZZZ{
			super();
		}
		public AbstractConfigJGIT(String[] saArg) throws ExceptionZZZ {
			super(saArg); 
		} 
		
		//### HIER NUR ARGUMENTE, DIE ETWAS MIT DEM KERNEL ZU TUN HABEN
		//Die verschiedenen Konfigurationen koennen unterschiedlichen Default PATTERN Strings haben.
		@Override 
		public abstract String getPatternStringDefault() throws ExceptionZZZ;
		
		@Override
		public abstract String[] getArgumentArrayDefault() throws ExceptionZZZ;
		
		@Override
		public String getApplicationKeyDefault() {
			return IConfigJGIT.sKEY_APPLICATION_DEFAULT;
		}
		@Override
		public String getConfigDirectoryNameDefault() {
			return IConfigJGIT.sDIRECTORY_CONFIG_DEFAULT;
		}
		@Override
		public String getConfigFileNameDefault() {		
			return IConfigJGIT.sFILE_CONFIG_DEFAULT;
		}	
		@Override
		public String getSystemNumberDefault() {
			return IConfigJGIT.sNUMBER_SYSTEM_DEFAULT;
	}
		@Override
		public String getProjectName() {
			return IConfigJGIT.sPROJECT_NAME;
		}
		@Override
		public String getProjectDirectory() {
			return IConfigJGIT.sPROJECT_PATH;
		}
		
	}
