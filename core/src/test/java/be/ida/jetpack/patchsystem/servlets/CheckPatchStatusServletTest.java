package be.ida.jetpack.patchsystem.servlets;

import be.ida.jetpack.patchsystem.executors.JobResult;
import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CheckPatchStatusServletTest {

    @InjectMocks
    private CheckPatchStatusServlet servlet;

    @Mock
    private PatchSystemJobService patchSystemJobService;

    @Test
    public void test_doGet_patchSystemRunning_noActiveJobs() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.getPatchSystemStatus()).willReturn(new JobResult(false));

        servlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"running\":false,\"progress\":0,\"numberOfPatches\":0}");
        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void test_doGet_patchSystemNotRunning_noActiveJobs() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        given(patchSystemJobService.getPatchSystemStatus()).willReturn(new JobResult(false));

        servlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"running\":false,\"progress\":0,\"numberOfPatches\":0}");
        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void test_doGet_patchSystemRunning_activeJob() {
        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        MockSlingHttpServletResponse slingHttpServletResponse = new MockSlingHttpServletResponse();

        JobResult jobResult = new JobResult(true);
        jobResult.setNumberOfPatches(4);
        jobResult.setProgress(25);
        jobResult.setLogs("Log output");
        given(patchSystemJobService.getPatchSystemStatus()).willReturn(jobResult);

        servlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

        assertThat(slingHttpServletResponse.getOutputAsString()).isEqualTo("{\"running\":true,\"progress\":25,\"numberOfPatches\":4,\"logs\":\"Log output\"}");
        assertThat(slingHttpServletResponse.getStatus()).isEqualTo(200);
    }
}
