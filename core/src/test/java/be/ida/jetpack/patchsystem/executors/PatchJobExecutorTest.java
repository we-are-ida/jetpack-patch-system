package be.ida.jetpack.patchsystem.executors;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;
import be.ida.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class PatchJobExecutorTest {

    @InjectMocks
    private PatchJobExecutor patchJobExecutor;

    @Mock
    private GroovyPatchSystemService patchSystemService;

    @Mock
    private Job job;

    @Mock
    private JobExecutionContext context;

    @Test
    public void testProcess_noScripts() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }

    @Test
    public void testProcess_1Script_fails() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        JobExecutionResult failedJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(resultBuilder.message(anyString())).willReturn(resultBuilder);
        given(resultBuilder.failed()).willReturn(failedJobExecutionResult);

        given(patchSystemService.runPatch("/path/to/script-1.groovy")).willThrow(new IllegalArgumentException("Error"));

        List<String> list = new ArrayList<>();
        list.add("/path/to/script-1.groovy");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(list);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(failedJobExecutionResult);
    }

    @Test
    public void testProcess_1Script_success() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        JobExecutionResult failedJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(patchSystemService.runPatch("/path/to/script-1.groovy")).willReturn(new GroovyPatchResult());

        List<String> list = new ArrayList<>();
        list.add("/path/to/script-1.groovy");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(list);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }

    @Test
    public void testProcess_2Scripts_success() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        JobExecutionResult failedJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(patchSystemService.runPatch("/path/to/script-1.groovy")).willReturn(new GroovyPatchResult());
        given(patchSystemService.runPatch("/path/to/script-2.groovy")).willReturn(new GroovyPatchResult());

        List<String> list = new ArrayList<>();
        list.add("/path/to/script-1.groovy");
        list.add("/path/to/script-2.groovy");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(list);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }
}
