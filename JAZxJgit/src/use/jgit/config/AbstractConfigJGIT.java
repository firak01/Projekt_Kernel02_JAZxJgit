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
		
		//######################################
		//### Spezielle Argumente, die nix mit dem Kernel zu tun haben
		
		//++++++++++++++++++++++++++++++++++++++++++
		@Override
		public String getRepositoryLocalBaseDefault() throws ExceptionZZZ {
			return IConfigJGIT.sPROJECT_PATH; //Also das eigene Projekt-Verzeichnis als Default
		}
		@Override
		public String readRepositoryLocal() throws ExceptionZZZ {		
			String sReturn = null;
			main:{
				GetOptZZZ objOpt = this.getOptObject();
				if(objOpt==null) break main;
				if(objOpt.getFlag("isLoaded")==false) break main;
				
				sReturn = objOpt.readValue("rl");
				if(sReturn==null){
					sReturn = this.getRepositoryLocalBaseDefault();
				}
			}//end main:		
			return sReturn;
		}		
		
		//++++++++++++++++++++++++++++++++++++++++++++
		@Override
		public String getRepositoryProjectNameDefault() throws ExceptionZZZ {		
			return null;
		}
			
		//++++++++++++++++++++++++++++++++++++++
		@Override
		public String readRepositoryProjectName() throws ExceptionZZZ {
			String sReturn = null;
			main:{
				GetOptZZZ objOpt = this.getOptObject();
				if(objOpt==null) break main;
				if(objOpt.getFlag("isLoaded")==false) break main;
				
				String sProject = objOpt.readValue("project");
				if(StringZZZ.isEmpty(sProject)) {
					sProject = this.getRepositoryProjectNameDefault();				
				}
				
				sReturn = sProject;
			}//end main:		
			return sReturn;
		}
		
		//++++++++++++++++++++++++++++++++++++++++++++
		@Override
		public String getRepositoryBranchDefault() throws ExceptionZZZ {		
			return IConfigJGIT.sBRANCH_DEFAULT;
		}
		
		@Override
		public String getRepositoryBranchAll() throws ExceptionZZZ {		
			return IConfigJGIT.sBRANCH_ALL;
		}
		
			
		//++++++++++++++++++++++++++++++++++++++
		@Override
		public String readRepositoryBranch() throws ExceptionZZZ {
			String sReturn = null;
			main:{
				GetOptZZZ objOpt = this.getOptObject();
				if(objOpt==null) break main;
				if(objOpt.getFlag("isLoaded")==false) break main;
				
				String sBranch = objOpt.readValue("branch");
				if(StringZZZ.isEmpty(sBranch)) {
					sBranch = this.getRepositoryBranchAll();				
				}
				sReturn = sBranch;
			}//end main:		
			return sReturn;
		}
		
		
	
		
		//++++++++++++++++++++++++++++++++++++++++++				
		//### aus IConfigJGIT
		@Override
		public String getCommentDefault() throws ExceptionZZZ {
			return "";
		}
		@Override
		public String readComment() throws ExceptionZZZ {
			String sReturn = null;
			main:{
				GetOptZZZ objOpt = this.getOptObject();
				if(objOpt==null) break main;
				if(objOpt.getFlag("isLoaded")==false) break main;
				
				sReturn = objOpt.readValue("comment");
				if(sReturn==null){
					sReturn = this.getCommentDefault();
				}
			}//end main:		
			return sReturn;

		}
	}
