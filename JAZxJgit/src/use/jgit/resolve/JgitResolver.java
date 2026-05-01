package use.jgit.resolve;

import java.io.File;

import basic.zBasic.AbstractObjectWithFlagZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.file.FileTextParserZZZ;
import basic.zBasic.util.file.FileTextReaderZZZ;
import basic.zBasic.util.file.FileTextWriterZZZ;
import use.jgit.JgitStarterMain;
import use.jgit.config.IConfigResolverJGIT;
import use.jgit.tool.resolve.GitConflictResolverUtil;



public class JgitResolver<T> extends AbstractObjectWithFlagZZZ<T> implements IJgitResolver, IJgitResolverEnabled{
	private static final long serialVersionUID = 521157607363069534L;

	
	//### aus IJgitResolver
	
	//##################################################
	//###### CONFLICT ######################################
	@Override
	public boolean conflictit(IConfigResolverJGIT objConfig) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			//try {
				if(objConfig==null) {
					ExceptionZZZ ez = new ExceptionZZZ("Konfigurationsobjekt mit den entgegengenommenen Argumente der Kommandozeile.", iERROR_PARAMETER_MISSING, JgitStarterMain.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				String sFilePath = objConfig.readFilePath();
				if(StringZZZ.isEmpty(sFilePath)) {
					ExceptionZZZ ez = new ExceptionZZZ("FilePath, ggfs. per Kommandozeile.", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				boolean bSuccessConflict = this.conflictit(sFilePath);
				if(bSuccessConflict) {
					System.out.println("STATUS AFTER RESOLVING CONFLICT: SUCCESSFUL");					
					bReturn = true;
				}else {
					System.out.println("STATUS AFTER RESOLVING CONFLICT: FAILED");					
					bReturn = false;
				}
			
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean conflictit(String sFilePath) throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			if(StringZZZ.isEmpty(sFilePath)) {
				ExceptionZZZ ez = new ExceptionZZZ("FilePath", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			File objFile = new File(sFilePath);
			boolean bFileExists = FileEasyZZZ.exists(objFile);
			if(!bFileExists) {
				ExceptionZZZ ez = new ExceptionZZZ("File not found. FilePath='" + sFilePath + "'", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			boolean bIsFile = FileEasyZZZ.isFileExisting(objFile);
			if(!bIsFile) {
				ExceptionZZZ ez = new ExceptionZZZ("This is not a file, may a directory. FilePath='" + sFilePath + "'", iERROR_PARAMETER_MISSING, JgitResolver.class, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			FileTextReaderZZZ objReader = new FileTextReaderZZZ(objFile);
			String sContent = objReader.read();
			
			TODOGOON20260501: Die Stategie aus einem FLAGCUSTOMZZZ - Wert lesen
			String sResolved = GitConflictResolverUtil.resolveConflicts(sContent, IJgitResolverEnabled.ConflictStrategy.OURS);
			
			FileTextWriterZZZ objWriter = new FileTextWriterZZZ(objFile);
			bReturn = objWriter.write(sResolved);	
			
		}//end main:
		return bReturn;
	}

	
}
