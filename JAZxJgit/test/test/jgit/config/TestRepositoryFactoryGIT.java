package test.jgit.config;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import test.jgit.config.ITestHelperConstant;
import test.jgit.config.TestHelperGIT;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.start.protocol.git.IJgitStarterGIT;
import use.jgit.start.protocol.git.JgitStarterGIT;

/**
 * Factory zum Erzeugen lokaler Test-Repositories.
 * Zudem wird ein RepositoryContext - Objekt zur Verfügung gestellt, das dann schon das entsprechend konfigurierte Starter - Objekt enthält.
 *  *
 * Erstellt:
 * - Clone A
 * - Clone B
 *
 * Rückgabe jeweils als RepositoryContext.
 *
 * @author Fritz
 */
public class TestRepositoryFactoryGIT {

    /**
     * Erstellt Clone A.
     */
    public static RepositoryContext createCloneA(IConfigRepositoryManagerJGIT configRepositoryManager) throws ExceptionZZZ {
        File objDirectory = new File(ITestHelperConstant.sDirectoryRepoBaseA);
        return createClone(configRepositoryManager, objDirectory, "CloneA");
    }

    /**
     * Erstellt Clone B.
     */
    public static RepositoryContext createCloneB(IConfigRepositoryManagerJGIT configRepositoryManager) throws ExceptionZZZ {
        File objDirectory = new File(ITestHelperConstant.sDirectoryRepoBaseB);
        return createClone(configRepositoryManager, objDirectory, "CloneB");
    }
    
    /**
     * Erstellt Clone C.
     */
    public static RepositoryContext createCloneC(IConfigRepositoryManagerJGIT configRepositoryManager) throws ExceptionZZZ {
        File objDirectory = new File(ITestHelperConstant.sDirectoryRepoBaseC);
        return createClone(configRepositoryManager, objDirectory, "CloneC");
    }

    /**
     * Gemeinsame Clone-Erstellung.
     */
    private static RepositoryContext createClone(
            IConfigRepositoryManagerJGIT configRepositoryManager,
            File objRepositoryBaseDirectory,
            String sAlias) throws ExceptionZZZ {

        RepositoryContext objContext = new RepositoryContext();

        //---------------------------------------------------------
        // RepositoryManager erzeugen
        JgitRepositoryManagerGIT objRepositoryManager =
                new JgitRepositoryManagerGIT(configRepositoryManager);

        //---------------------------------------------------------
        // Clone erzeugen
        objRepositoryManager.cloneRepositoryTo(objRepositoryBaseDirectory);

        if(!FileEasyZZZ.exists(objRepositoryBaseDirectory)){
            throw new ExceptionZZZ(
                    "Repository konnte nicht erzeugt werden: "
                    + objRepositoryBaseDirectory.getAbsolutePath(),
                    ExceptionZZZ.iERROR_RUNTIME,
                    TestRepositoryFactoryGIT.class,
                    "createClone");
        }

        //---------------------------------------------------------
        // Starter-Konfiguration bestimmen
        IConfigStarterRemoteJGIT objStarterConfig =
                TestHelperGIT.findStarterRemoteConfiguration_DefinedForRepositoryBaseLocalAtoC(
                        objRepositoryBaseDirectory);

        //---------------------------------------------------------
        // Starter erzeugen
        IJgitStarterGIT objStarter =
                new JgitStarterGIT(objStarterConfig);

        //---------------------------------------------------------
        // Projektverzeichnis bestimmen
        File objProjectDirectory =
                new File(
                        objRepositoryBaseDirectory,
                        objRepositoryManager.getRepositoryProjectName());

        //---------------------------------------------------------
        // Context füllen
        objContext.setAlias(sAlias);
        objContext.setRepositoryBaseDirectory(objRepositoryBaseDirectory);
        objContext.setRepositoryProjectDirectory(objProjectDirectory);

        objContext.setRepositoryManager(objRepositoryManager);
        objContext.setStarterConfiguration(objStarterConfig);
        objContext.setStarter(objStarter);

        return objContext;
    }

}
