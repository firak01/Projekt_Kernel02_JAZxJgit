package use.jgit.protocol.https;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JschConfigSessionFactoryZZZ extends JschConfigSessionFactory {

	  @Override
      protected void configure(OpenSshConfig.Host host, Session session) {
        // identisch zum normalen Git Verhalten
        //session.setConfig("StrictHostKeyChecking", "yes");
		  
		  
		//+++++++++++++++++++++
		//Verhindere die Prüfung auf Datei C:\Users\<User>\.ssh\known_hosts
		//Für diese stehen für github die aktuellen Keys hier:
		//https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/githubs-ssh-key-fingerprints
		session.setConfig("StrictHostKeyChecking", "no");
		   
      }

      @Override
      protected JSch createDefaultJSch(FS fs) throws JSchException {

          JSch jsch = super.createDefaultJSch(fs);
          
          //anders als bei SSH, hier nix machen

          return jsch;
      }

}
