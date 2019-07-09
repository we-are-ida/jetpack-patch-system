package be.ida.jetpack.patchsystem.servlets;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TriggerNewPatchesServletTest {

    @InjectMocks
    private TriggerNewPatchesServlet servlet;

    @Mock
    private PatchSystemJobService patchSystemJobService;

    @Mock
    private OnDeployScriptSystemService onDeployScriptSystemService;

    @Test
    public void test_doPost_invalid() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getOutputAsString()).isEmpty();
        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(400);
    }

    @Test
    public void test_doPost_valid_2scriptsFound() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getContentType()).willReturn(JetpackConstants.APPLICATION_JSON);

        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.executeNewPatches())
                .willReturn(Arrays.asList(new String[] {"/apps/script/1.groovy", "/apps/script/1.groovy"}));

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"message\":\"Success.\",\"patches\":[\"/apps/script/1.groovy\",\"/apps/script/1.groovy\"]}");
    }

    @Test
    public void test_doPost_valid_noScriptsFound() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getContentType()).willReturn(JetpackConstants.APPLICATION_JSON);

        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.executeNewPatches()).willReturn(Arrays.asList(new String[] {}));

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"message\":\"No patches found to trigger.\"}");
    }

    @Test
    public void test_doPost_valid_noScriptsFound_withOnDeployScripts() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getContentType()).willReturn(JetpackConstants.APPLICATION_JSON);

        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.executeNewPatches()).willReturn(Arrays.asList(new String[] {}));
        given(onDeployScriptSystemService.isPatchSystemReady()).willReturn(true);

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"message\":\"No patches found to trigger. On Deploy Scripts cannot be triggered using the Patch System.\"}");
    }

    @Test
    public void test_doPost_valid_returnedNull() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getContentType()).willReturn(JetpackConstants.APPLICATION_JSON);

        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.executeNewPatches()).willReturn(null);

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(500);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"message\":\"Could not trigger patches.\"}");
    }

    @Test
    public void test_doPost_valid_Exception() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getContentType()).willReturn(JetpackConstants.APPLICATION_JSON);

        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.executeNewPatches()).willThrow(new NullPointerException("message"));

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(500);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEmpty();
    }

}
