import junit.framework.Test;
import junit.framework.TestSuite;
import use.jgit.manage.protocol.git.JgitRepositoryManagerGITTest;
import use.jgit.manage.protocol.https.JgitRepositoryManagerHTTPSTest;
import use.jgit.resolve.JgitResolverLocalGITTest;
import use.jgit.start.protocol.git.JgitStarterGITTest;

public class XJgitAllTest {
			public static Test suite(){
			TestSuite objReturn = new TestSuite();
			//Merke: Die Tests bilden in ihrer Reihenfolge in etwa die Hierarchie im Framework ab. 
			//            Dies beim Einf�gen weiterer Tests bitte beachten.         
			
			objReturn.addTestSuite(JgitRepositoryManagerGITTest.class);
			objReturn.addTestSuite(JgitRepositoryManagerHTTPSTest.class);
			objReturn.addTestSuite(JgitResolverLocalGITTest.class);
			objReturn.addTestSuite(JgitStarterGITTest.class);			
			return objReturn;
		}
		/**
		 * Hiermit eine Swing-Gui starten.
		 * Das ist bei eclipse aber nicht notwendig, außer man will alle hier eingebundenen Tests durchführen.
		 * @param args
		 */
		public static void main(String[] args) {
			//Ab Eclipse 4.4 ist junit.swingui sogar nicht mehr Bestandteil des Bundles
			//also auch nicht mehr unter der Eclipse Variablen JUNIT_HOME/junit.jar zu finden
			//junit.swingui.TestRunner.run(LanguageMarkupAllTest.class);
		}
}
