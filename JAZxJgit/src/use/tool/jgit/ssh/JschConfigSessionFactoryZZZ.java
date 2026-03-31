package use.tool.jgit.ssh;

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

          String home = System.getProperty("user.home");
          File sshDir = new File(home, ".ssh");

          if(!sshDir.exists()) {
          	System.out.println("SSH Directory not found: '"+ sshDir.getAbsolutePath() + "'");
              return jsch;
          }

          // known_hosts setzen
          File knownHosts = new File(sshDir, "known_hosts");
          if(knownHosts.exists()) {
          	System.out.println("KnownHost file found");
              jsch.setKnownHosts(knownHosts.getAbsolutePath());
          }else {
          	System.out.println("KnownHost file not found");
          }

          // alle privaten Schlüssel laden
          File[] keys = sshDir.listFiles(new FilenameFilter() {

              public boolean accept(File dir, String name) {

                  if(name.endsWith(".pub")) return false;
                  if(name.endsWith(".ppk")) return false; //das ist etwas mit putty
                  
                  boolean bValidPrivateKeyFile = name.startsWith("id_");
                  bValidPrivateKeyFile = bValidPrivateKeyFile | name.startsWith("tubaf_id_");
                  bValidPrivateKeyFile = bValidPrivateKeyFile | name.startsWith("github_id_");
                  
                  return bValidPrivateKeyFile;
              }
          });

          if(keys != null) {
              for(File key : keys) {
                  try {
                      jsch.addIdentity(key.getAbsolutePath());
                      System.out.println("SSH key loaded: " + key.getName());
                  } catch(Exception e) {
                      System.out.println("SSH key skipped: " + key.getName());
                  }
              }
          }

          return jsch;
      }

}
