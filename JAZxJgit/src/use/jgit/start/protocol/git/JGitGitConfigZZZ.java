package use.jgit.start.protocol.git;

import org.eclipse.jgit.transport.SshSessionFactory;

import use.jgit.common.JschConfigSessionFactoryZZZ;

/**Erstellt von ChatGPT 2026-03-16
 * @author fl86kyvo
 *
 */
public class JGitGitConfigZZZ {

    public static void configure() {
    	//Das funktioniert aber nur in bestimmten Umgebungen
    			String sVersion = org.eclipse.jgit.lib.Constants.class
    			        .getPackage()
    			        .getImplementationVersion();
    			System.out.println("JschConfigSessionFactoryZZZ wird konfiguriert und trifft auf implementierte Version: '" + sVersion + "'");
    			
    	
    	JschConfigSessionFactoryZZZ myJschConfigSessionFactory = new JschConfigSessionFactoryZZZ();    	
        SshSessionFactory.setInstance(myJschConfigSessionFactory) ;
    }
}