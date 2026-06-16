package use.jgit.config;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.config.AbstractConfigZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.AbstractKernelConfigZZZ;
import basic.zKernel.GetOptZZZ;

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
		
		
		
		//### HIER NUR ARGUMENTE, DIE ETWAS MIT DEM KERNEL ZU TUN HABEN
		//### Dafür muss AbstractKernelConfigZZZ die Elternklasse sein, z.B.:
		//@Override
		//public String getApplicationKeyDefault() { ....
		
	}
