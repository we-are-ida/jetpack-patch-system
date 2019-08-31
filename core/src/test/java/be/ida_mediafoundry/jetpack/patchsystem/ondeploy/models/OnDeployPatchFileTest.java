package be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models;

import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest;
import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScript;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScriptBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OnDeployPatchFileTest {

    @Test
    public void testCreateOnDeployPatchFile() {
        OnDeployScriptProvider provider = new TestProvider();

        OnDeployPatchFile file = new OnDeployPatchFile(provider.getScripts().get(0), provider);

        assertThat(file)
                .isNotNull()
                .extracting("projectName", "scriptName", "runnable", "type", "path", "resultPath")
                .containsExactly("TestProvider", "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models.OnDeployPatchFileTest$TestScript", true,
                        "onDeployScript",
                        "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models.OnDeployPatchFileTest$TestScript",
                        "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models.OnDeployPatchFileTest$TestScript");
    }

    private class TestProvider implements OnDeployScriptProvider {
        @Override
        public List<OnDeployScript> getScripts() {
            return Arrays.asList(
                    new TestScript(),
                    new TestScript()
            );
        }
    }

    private class TestScript extends OnDeployScriptBase {
        @Override
        protected void execute() throws Exception {

        }
    }
}
