package test.jgit.config;

import java.io.File;

import basic.zBasic.ExceptionZZZ;
import use.jgit.IJgitRepositoryManagerJGIT;
import use.jgit.IJgitRepositoryManagerZZZ;
import use.jgit.IJgitStarterRemoteJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGIT;
import use.jgit.start.protocol.git.IJgitStarterGIT;
import use.jgit.start.protocol.https.IJgitStarterHTTPS;

/**
 * Enthält alle Informationen zu einem lokalen Test-Repository.
 *
 * Ziel:
 * - Repository nur einmal erzeugen
 * - Starter und Manager wiederverwenden
 * - Später leicht um Pull-, Fetch-, Merge-Informationen erweiterbar.
 *
 * @author Fritz
 *
 */
public class RepositoryContext {

    /** Basisverzeichnis des lokalen Repositories */
    private File repositoryBaseDirectory;

    /** Projektverzeichnis innerhalb des Repositories */
    private File repositoryProjectDirectory;

    /** RepositoryManager */
    private IJgitRepositoryManagerJGIT repositoryManager;

    /** Git-Starter */
    private IJgitStarterRemoteJGIT starter;

    /** Konfiguration des Starters */
    private IConfigStarterRemoteJGIT starterConfiguration;

    /** frei verwendbarer Name (CloneA, CloneB, Remote usw.) */
    private String alias;

    // ###############################################################
    // Konstruktoren
    // ###############################################################

    public RepositoryContext() {
    }

    public RepositoryContext(File repositoryBaseDirectory) {
        this.repositoryBaseDirectory = repositoryBaseDirectory;
    }

    // ###############################################################
    // Getter / Setter
    // ###############################################################

    public File getRepositoryBaseDirectory() {
        return repositoryBaseDirectory;
    }

    public void setRepositoryBaseDirectory(File repositoryBaseDirectory) {
        this.repositoryBaseDirectory = repositoryBaseDirectory;
    }

    public File getRepositoryProjectDirectory() {
        return repositoryProjectDirectory;
    }

    public void setRepositoryProjectDirectory(File repositoryProjectDirectory) {
        this.repositoryProjectDirectory = repositoryProjectDirectory;
    }

    public IJgitRepositoryManagerZZZ getRepositoryManager() {
        return repositoryManager;
    }

    public void setRepositoryManager(IJgitRepositoryManagerJGIT repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public IJgitStarterRemoteJGIT getStarter() {
        return starter;
    }

    public void setStarter(IJgitStarterRemoteJGIT starter) {
        this.starter = starter;
    }

    public IConfigStarterRemoteJGIT getStarterConfiguration() {
        return starterConfiguration;
    }

    public void setStarterConfiguration(IConfigStarterRemoteJGIT starterConfiguration) {
        this.starterConfiguration = starterConfiguration;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    // ###############################################################
    // Komfortmethoden
    // ###############################################################

    /**
     * Liefert den vollständigen Pfad zu einer Datei innerhalb
     * des Projektverzeichnisses.
     */
    public File computeProjectFile(String sRelativePath) {

        if(repositoryProjectDirectory==null){
            return null;
        }

        return new File(repositoryProjectDirectory, sRelativePath);
    }

    /**
     * Liefert den Repository-Projektnamen.
     * @throws ExceptionZZZ 
     */
    public String getRepositoryProjectName() throws ExceptionZZZ {

        if(repositoryManager==null){
            return null;
        }

        return repositoryManager.getRepositoryProjectName();
    }

    /**
     * Prüft, ob Starter vorhanden ist.
     */
    public boolean hasStarter() {
        return starter!=null;
    }

    /**
     * Prüft, ob RepositoryManager vorhanden ist.
     */
    public boolean hasRepositoryManager() {
        return repositoryManager!=null;
    }

    /**
     * Prüft, ob Starter-Konfiguration vorhanden ist.
     */
    public boolean hasStarterConfiguration() {
        return starterConfiguration!=null;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("RepositoryContext[");

        if(alias!=null){
            sb.append(alias);
            sb.append(", ");
        }

        if(repositoryBaseDirectory!=null){
            sb.append(repositoryBaseDirectory.getAbsolutePath());
        }

        sb.append("]");

        return sb.toString();
    }

}
