package use.jgit.protcol.git;

import org.eclipse.jgit.transport.SshSessionFactory;

/**Erstellt von ChatGPT 2026-03-16
 * @author fl86kyvo
 *
 */
public class JGitGitConfigZZZ {

    public static void configure() {

    	JschConfigSessionFactoryZZZ myJschConfigSessionFactory = new JschConfigSessionFactoryZZZ();    	
        SshSessionFactory.setInstance(myJschConfigSessionFactory) ;
    }
}