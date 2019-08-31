package be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models;

import be.ida_mediafoundry.jetpack.patchsystem.models.PatchStatus;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OnDeployPatchResultTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() throws Exception {
        context.load().json("/mocks/onDeployScriptResults.json", "/var/acs-on-deploy-results");
        context.addModelsForClasses(OnDeployPatchResult.class);
    }

    @Test
    public void testGetPatchResult_running() {
        Resource resource = context.resourceResolver().getResource("/var/acs-on-deploy-results/be.ida.script.Modify-running");

        OnDeployPatchResult result = resource.adaptTo(OnDeployPatchResult.class);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getStatus()).isEqualTo("RUNNING");
        assertThat(result.getRunningTime()).isNotNull();
        assertThat(result.getOutput()).isNull();
        assertThat(result.getEndDate()).isNull();
        assertThat(result.getStartDate()).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(PatchStatus.RUNNING.isOfStatus(result)).isTrue();
    }

    @Test
    public void testGetPatchResult_fail() {
        Resource resource = context.resourceResolver().getResource("/var/acs-on-deploy-results/be.ida.script.Modify-fail");

        OnDeployPatchResult result = resource.adaptTo(OnDeployPatchResult.class);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("ERROR");
        assertThat(result.getOutput()).isEqualTo("This is the error message");
        assertThat(result.getRunningTime()).isNull();
        assertThat(result.getEndDate()).isNull();
        assertThat(result.getStartDate()).isNotNull();
        assertThat(result.isError()).isTrue();
        assertThat(PatchStatus.ERROR.isOfStatus(result)).isTrue();
    }

    @Test
    public void testGetPatchResult_success() {
        Resource resource = context.resourceResolver().getResource("/var/acs-on-deploy-results/be.ida.script.Modify-success");

        OnDeployPatchResult result = resource.adaptTo(OnDeployPatchResult.class);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getOutput()).isNull();
        assertThat(result.getRunningTime()).isEqualTo("00:04:00.000");
        assertThat(result.getEndDate()).isNotNull();
        assertThat(result.getStartDate()).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(PatchStatus.SUCCESS.isOfStatus(result)).isTrue();
    }
}
