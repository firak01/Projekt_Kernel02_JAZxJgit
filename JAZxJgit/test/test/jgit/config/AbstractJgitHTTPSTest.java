package test.jgit.config;

import junit.framework.TestCase;
import use.jgit.config.IConfigRepositoryManagerJGIT;
import use.jgit.config.IConfigStarterRemoteJGIT;

public abstract class AbstractJgitHTTPSTest extends TestCase{
    protected IConfigRepositoryManagerJGIT configRepositoryManager;
    protected IConfigStarterRemoteJGIT configStarter;

    /* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 * 
	 * Aufbau einer Repository Struktur:
	 * 
	 * Remote
		   ^
		   |
		+--+----------------+
		|                   |
		Clone A         Clone B
	 */
    @Override
    protected void setUp() throws Exception {
        TestHelper.removeRepositoriesLocal_onSetup();

        configRepositoryManager =
            TestHelperHTTPS.findRepositoryManagerConfiguration_DefinedForEnvironmentCurrent();

        configStarter =
            TestHelperHTTPS.findStarterRemoteConfiguration_DefinedForEnvironmentCurrent();
    }
    
    @Override
    protected void tearDown() throws Exception {
		
	}
}
