package be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl;

import be.ida_mediafoundry.jetpack.patchsystem.models.PatchFile;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
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
    public void testGetPatches_withProviders_NEW() {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);

        patchSystemService.bindOnDeployScriptProvider(new TestProvider());

        List<PatchFileWithResultResource> patches = patchSystemService.getPatches(resourceResolver);

        assertThat(patches)
                .isNotNull()
                .isNotEmpty()
                .extracting("valueMap")
                .extracting("type", "runnable", "status", "projectName", "scriptName")
                .containsExactly(
                        tuple("onDeployScript", true, "NEW", "TestProvider", "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript"),
                        tuple("onDeployScript", true, "NEW", "TestProvider", "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript")
                );
    }

    @Test
    public void testGetPatches_withProviders_SUCCESS() {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        patchSystemService.bindOnDeployScriptProvider(new TestProvider());

        OnDeployPatchResult onDeployPatchResult = mock(OnDeployPatchResult.class);
        given(onDeployPatchResult.getStatus()).willReturn("SUCCESS");
        given(patchResultRepository.getResult("be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript")).willReturn(onDeployPatchResult);

        List<PatchFileWithResultResource> patches = patchSystemService.getPatches(resourceResolver);

        assertThat(patches)
                .isNotNull()
                .isNotEmpty()
                .extracting("valueMap")
                .extracting("type", "runnable", "status")
                .containsExactly(
                        tuple("onDeployScript", true, "SUCCESS"),
                        tuple("onDeployScript", true, "SUCCESS")
                );
    }

    @Test
    public void testGetPatches_withProviders_RUNNING() {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        patchSystemService.bindOnDeployScriptProvider(new TestProvider());

        OnDeployPatchResult onDeployPatchResult = mock(OnDeployPatchResult.class);
        given(onDeployPatchResult.getStatus()).willReturn("RUNNING");
        given(patchResultRepository.getResult("be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript")).willReturn(onDeployPatchResult);

        List<PatchFileWithResultResource> patches = patchSystemService.getPatches(resourceResolver);

        assertThat(patches)
                .isNotNull()
                .isNotEmpty()
                .extracting("valueMap")
                .extracting("type", "runnable", "status")
                .containsExactly(
                        tuple("onDeployScript", true, "RUNNING"),
                        tuple("onDeployScript", true, "RUNNING")
                );
    }

    @Test
    public void testGetPatchesToExecute_withProviders_ERROR() {
        patchSystemService.bindOnDeployScriptProvider(new TestProvider());

        OnDeployPatchResult onDeployPatchResult = mock(OnDeployPatchResult.class);
        given(onDeployPatchResult.isError()).willReturn(true);
        given(patchResultRepository.getResult("be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript")).willReturn(onDeployPatchResult);

        List<PatchFile> patches = patchSystemService.getPatchesToExecute();

        assertThat(patches)
                .isNotNull()
                .isNotEmpty()
                .extracting("type", "runnable", "path")
                .containsExactly(
                        tuple("onDeployScript", true, "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript"),
                        tuple("onDeployScript", true, "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript")
                );
    }

    @Test
    public void testGetPatchesToExecute_withProviders_neverRan() {
        patchSystemService.bindOnDeployScriptProvider(new TestProvider());

        List<PatchFile> patches = patchSystemService.getPatchesToExecute();

        assertThat(patches)
                .isNotNull()
                .isNotEmpty()
                .extracting("type", "runnable", "path")
                .containsExactly(
                        tuple("onDeployScript", true, "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript"),
                        tuple("onDeployScript", true, "be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript")
                );
    }

    @Test
    public void testGetPatchesToExecute_withProviders_noNewScripts() {
        patchSystemService.bindOnDeployScriptProvider(new TestProvider());

        OnDeployPatchResult onDeployPatchResult = mock(OnDeployPatchResult.class);
        given(onDeployPatchResult.isError()).willReturn(false);
        given(patchResultRepository.getResult("be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.impl.OnDeployScriptSystemServiceImplTest$TestScript")).willReturn(onDeployPatchResult);

        List<PatchFile> patches = patchSystemService.getPatchesToExecute();

        assertThat(patches)
                .isNotNull()
                .isEmpty();
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
