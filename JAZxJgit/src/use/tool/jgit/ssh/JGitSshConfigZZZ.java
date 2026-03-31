package use.tool.jgit.ssh;

import org.eclipse.jgit.transport.SshSessionFactory;

/**Erstellt von ChatGPT 2026-03-16
 * @author fl86kyvo
 *
 */
public class JGitSshConfigZZZ {

    public static void configure() {

    	JschConfigSessionFactoryZZZ myJschConfigSessionFactory = new JschConfigSessionFactoryZZZ();    	
        SshSessionFactory.setInstance(myJschConfigSessionFactory) ;
    }
}