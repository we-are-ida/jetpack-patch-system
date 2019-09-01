package be.ida_mediafoundry.jetpack.patchsystem.servlets;

import be.ida_mediafoundry.jetpack.patchsystem.JetpackConstants;
import be.ida_mediafoundry.jetpack.patchsystem.models.SimplePatchFile;
import be.ida_mediafoundry.jetpack.patchsystem.services.PatchSystemJobService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TriggerNewPatchesServletTest {

    @InjectMocks
    private TriggerNewPatchesServlet servlet;

    @Mock
    private PatchSystemJobService patchSystemJobService;

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

        List<SimplePatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(new SimplePatchFile("groovy", "/apps/script/1.groovy"));
        patchFiles.add(new SimplePatchFile("groovy", "/apps/script/2.groovy"));

        given(patchSystemJobService.executeNewPatches()).willReturn(patchFiles);

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"message\":\"Success.\",\"patches\":[{\"type\":\"groovy\",\"patchFile\":\"/apps/script/1.groovy\"},{\"type\":\"groovy\",\"patchFile\":\"/apps/script/2.groovy\"}]}");
    }

    @Test
    public void test_doPost_valid_noScriptsFound() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getContentType()).willReturn(JetpackConstants.APPLICATION_JSON);

        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.executeNewPatches()).willReturn(Collections.emptyList());

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"message\":\"No patches found to trigger.\"}");
    }

    @Test
    public void test_doPost_valid_noScriptsFound_withOnDeployScripts() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getContentType()).willReturn(JetpackConstants.APPLICATION_JSON);

        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.executeNewPatches()).willReturn(Collections.emptyList());

        servlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"message\":\"No patches found to trigger.\"}");
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
