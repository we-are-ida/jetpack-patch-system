package be.ida_mediafoundry.jetpack.patchsystem.servlets;

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
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class HasPatchesToExecuteServletTest {

    @InjectMocks
    private HasPatchesToExecuteServlet servlet;

    @Mock
    private PatchSystemJobService patchSystemJobService;

    @Test
    public void test_doGet_noPatches() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        List<SimplePatchFile> patchFiles = new ArrayList<>();

        given(patchSystemJobService.getAllPatchesToExecute()).willReturn(patchFiles);

        servlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"count\":0,\"patches\":[]}");
        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void test_doGet_1patchFound() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        List<SimplePatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(new SimplePatchFile("groovy", "/apps/groovy.groovy"));
        given(patchSystemJobService.getAllPatchesToExecute()).willReturn(patchFiles);

        servlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"count\":1,\"patches\":[{\"type\":\"groovy\",\"patchFile\":\"/apps/groovy.groovy\"}]}");
        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void test_doGet_2patchFound() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        List<SimplePatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(new SimplePatchFile("groovy", "/apps/groovy.groovy"));
        patchFiles.add(new SimplePatchFile("other", "/apps/other.other"));
        given(patchSystemJobService.getAllPatchesToExecute()).willReturn(patchFiles);

        servlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"count\":2,\"patches\":[{\"type\":\"groovy\",\"patchFile\":\"/apps/groovy.groovy\"},{\"type\":\"other\",\"patchFile\":\"/apps/other.other\"}]}");
        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
    }
}
