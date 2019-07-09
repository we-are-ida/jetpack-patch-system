package be.ida.jetpack.patchsystem.servlets;

import be.ida.jetpack.patchsystem.services.PatchSystemJobService;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.commands.WCMCommandContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HtmlResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TriggerSinglePatchWcmCommandTest {

    @InjectMocks
    private TriggerSinglePatchWcmCommand command;

    @Mock
    private PatchSystemJobService patchSystemJobService;

    @Test
    public void testGetCommandName() {
        assertThat(command.getCommandName()).isEqualTo("triggerSinglePatch");
    }

    @Test
    public void testPerformCommand_success() {
        RequestParameter requestParameter = mock(RequestParameter.class);
        given(requestParameter.getString()).willReturn("/apps/script/script.groovy");

        RequestParameter requestParameterRunnable = mock(RequestParameter.class);
        given(requestParameterRunnable.getString()).willReturn("yes");

        RequestParameter requestParameterType = mock(RequestParameter.class);
        given(requestParameterType.getString()).willReturn("bla");

        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getRequestParameter("path")).willReturn(requestParameter);
        given(slingHttpServletRequest.getRequestParameter("runnable")).willReturn(requestParameterRunnable);
        given(slingHttpServletRequest.getRequestParameter("type")).willReturn(requestParameterType);

        given(patchSystemJobService.executePatch("/apps/script/script.groovy", "bla", true)).willReturn(true);

        HtmlResponse htmlResponse = command.performCommand(mock(WCMCommandContext.class),
                slingHttpServletRequest,
                mock(SlingHttpServletResponse.class),
                mock(PageManager.class));

        assertThat(htmlResponse).isNotNull();
        assertThat(htmlResponse.getStatusCode()).isEqualTo(200);
        assertThat(htmlResponse.getStatusMessage()).isEqualTo("executed");
    }

    @Test
    public void testPerformCommand_fail() {
        RequestParameter requestParameter = mock(RequestParameter.class);
        given(requestParameter.getString()).willReturn("/apps/script/script.groovy");
        RequestParameter requestParameterRunnable = mock(RequestParameter.class);
        given(requestParameterRunnable.getString()).willReturn("yes");
        RequestParameter requestParameterType = mock(RequestParameter.class);
        given(requestParameterType.getString()).willReturn("bla");

        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getRequestParameter("path")).willReturn(requestParameter);
        given(slingHttpServletRequest.getRequestParameter("runnable")).willReturn(requestParameterRunnable);
        given(slingHttpServletRequest.getRequestParameter("type")).willReturn(requestParameterType);

        given(patchSystemJobService.executePatch("/apps/script/script.groovy", "bla", true)).willReturn(false);

        HtmlResponse htmlResponse = command.performCommand(mock(WCMCommandContext.class),
                slingHttpServletRequest,
                mock(SlingHttpServletResponse.class),
                mock(PageManager.class));

        assertThat(htmlResponse).isNotNull();
        assertThat(htmlResponse.getStatusCode()).isEqualTo(500);
        assertThat(htmlResponse.getStatusMessage()).isEqualTo("executed");
    }

    @Test
    public void testPerformCommand_exception() {
        RequestParameter requestParameter = mock(RequestParameter.class);
        given(requestParameter.getString()).willReturn("/apps/script/script.groovy");
        RequestParameter requestParameterRunnable = mock(RequestParameter.class);
        given(requestParameterRunnable.getString()).willReturn("yes");
        RequestParameter requestParameterType = mock(RequestParameter.class);
        given(requestParameterType.getString()).willReturn("bla");

        SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
        given(slingHttpServletRequest.getRequestParameter("path")).willReturn(requestParameter);
        given(slingHttpServletRequest.getRequestParameter("runnable")).willReturn(requestParameterRunnable);
        given(slingHttpServletRequest.getRequestParameter("type")).willReturn(requestParameterType);

        given(patchSystemJobService.executePatch("/apps/script/script.groovy", "bla", true)).willThrow(new NullPointerException("message"));

        HtmlResponse htmlResponse = command.performCommand(mock(WCMCommandContext.class),
                slingHttpServletRequest,
                mock(SlingHttpServletResponse.class),
                mock(PageManager.class));

        assertThat(htmlResponse).isNotNull();
        assertThat(htmlResponse.getStatusCode()).isEqualTo(500);
        assertThat(htmlResponse.getStatusMessage()).isEqualTo("message");
    }
}
