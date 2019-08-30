package be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl;

import be.ida_mediafoundry.jetpack.patchsystem.models.PatchFileWithResult;
import be.ida_mediafoundry.jetpack.patchsystem.models.PatchFileWithResultResource;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models.OnDeployPatchResult;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.repositories.OnDeployScriptsResultRepository;
import com.adobe.acs.commons.ondeploy.OnDeployExecutor;
import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScript;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScriptBase;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class OnDeployScriptSystemServiceImplTest {

    @InjectMocks
    private OnDeployScriptSystemServiceImpl patchSystemService;

    @Mock
    private OnDeployExecutor onDeployExecutor;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private OnDeployScriptsResultRepository patchResultRepository;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testIsPatchSystemReady_enabled() {
        patchSystemService.bindOnDeployScriptProvider(new OnDeployScriptProvider() {
            @Override
            public List<OnDeployScript> getScripts() {
                return new ArrayList<>();
            }
        });

        boolean result = patchSystemService.isPatchSystemReady();

        assertThat(result).isTrue();
    }

    @Test
    public void testIsPatchSystemReady_unbind() {
        OnDeployScriptProvider scriptProvider = new OnDeployScriptProvider() {
            @Override
            public List<OnDeployScript> getScripts() {
                return new ArrayList<>();
            }
        };
        patchSystemService.bindOnDeployScriptProvider(scriptProvider);
        patchSystemService.unbindOnDeployScriptProvider(scriptProvider);

        boolean result = patchSystemService.isPatchSystemReady();

        assertThat(result).isFalse();
    }

    @Test
    public void testIsPatchSystemReady_noProvider() {
        boolean result = patchSystemService.isPatchSystemReady();

        assertThat(result).isFalse();
    }



    @Test
    public void testIsPatchSystemReady_noService() {
        patchSystemService.unbindOnDeployExecutor();

        boolean result = patchSystemService.isPatchSystemReady();

        assertThat(result).isFalse();
    }

    @Test
    public void testRunPatch_withExecutor_success() {
        OnDeployPatchResult onDeployPatchResult = mock(OnDeployPatchResult.class);
        given(onDeployPatchResult.getStatus()).willReturn("success");
        given(onDeployExecutor.executeScript("be.ida.script.Modify", true)).willReturn(true);
        given(patchResultRepository.getResult("be.ida.script.Modify")).willReturn(onDeployPatchResult);

        OnDeployPatchResult result = patchSystemService.runPatch("be.ida.script.Modify");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("success");
    }

    @Test
    public void testRunPatch_withExecutor_fail() {
        given(onDeployExecutor.executeScript("be.ida.script.Modify", true)).willReturn(true);
        given(patchResultRepository.getResult("be.ida.script.Modify")).willReturn(null);

        OnDeployPatchResult result = patchSystemService.runPatch("be.ida.script.Modify");

        assertThat(result).isNull();
    }

    @Test
    public void testRunPatch_withoutExecutor() {
        patchSystemService.unbindOnDeployExecutor();

        OnDeployPatchResult result = patchSystemService.runPatch("be.ida.script.Modify");

        assertThat(result).isNull();
    }

    @Test
    public void testGetPatches_noProviders() {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);

        List<PatchFileWithResultResource> patches = patchSystemService.getPatches(resourceResolver);

        assertThat(patches).isNotNull().isEmpty();
    }

    @Test
    public void testGetPatches_withProviders() {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);

        patchSystemService.bindOnDeployScriptProvider(new OnDeployScriptProvider() {
            @Override
            public List<OnDeployScript> getScripts() {
                return Arrays.asList(
                        new TestScript(),
                        new TestScript()
                );
            }
        });

        List<PatchFileWithResultResource> patches = patchSystemService.getPatches(resourceResolver);

        assertThat(patches).isNotNull()
                           .isNotEmpty();

        assertThat(patches.get(0).getValueMap())
                .extracting("type", "runnable", "status")
                .containsExactly("onDeployScript", true, "NEW");

        assertThat(patches.get(1).getValueMap())
                .extracting("type", "runnable", "status")
                .containsExactly("onDeployScript", true, "NEW");
    }

    private class TestScript extends OnDeployScriptBase {
        @Override
        protected void execute() throws Exception {

        }
    }
}
