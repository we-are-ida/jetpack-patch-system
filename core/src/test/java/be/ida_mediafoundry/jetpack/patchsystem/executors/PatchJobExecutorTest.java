package be.ida_mediafoundry.jetpack.patchsystem.executors;

import be.ida_mediafoundry.jetpack.patchsystem.JetpackConstants;
import be.ida_mediafoundry.jetpack.patchsystem.groovy.models.GroovyPatchResult;
import be.ida_mediafoundry.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models.OnDeployPatchResult;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
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
    private GroovyPatchSystemService groovyPatchSystemService;

    @Mock
    private OnDeployScriptSystemService onDeployScriptSystemService;

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
    public void testProcess_1GroovyScript_fails() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        JobExecutionResult failedJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(resultBuilder.message(anyString())).willReturn(resultBuilder);
        given(resultBuilder.failed()).willReturn(failedJobExecutionResult);

        given(groovyPatchSystemService.runPatch("/path/to/script-1.groovy")).willThrow(new IllegalArgumentException("Error"));

        List<String> patchFiles = new ArrayList<>();
        patchFiles.add("/path/to/script-1.groovy");

        List<String> typeList = new ArrayList<>();
        typeList.add("groovy");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(patchFiles);
        given(job.getProperty(JetpackConstants.TYPES, List.class)).willReturn(typeList);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(failedJobExecutionResult);
    }

    @Test
    public void testProcess_1GroovyScript_success() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        JobExecutionResult failedJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(groovyPatchSystemService.runPatch("/path/to/script-1.groovy")).willReturn(new GroovyPatchResult());

        List<String> patchFiles = new ArrayList<>();
        patchFiles.add("/path/to/script-1.groovy");

        List<String> typeList = new ArrayList<>();
        typeList.add("groovy");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(patchFiles);
        given(job.getProperty(JetpackConstants.TYPES, List.class)).willReturn(typeList);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }

    @Test
    public void testProcess_2GroovyScripts_success() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        JobExecutionResult failedJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(groovyPatchSystemService.runPatch("/path/to/script-1.groovy")).willReturn(new GroovyPatchResult());
        given(groovyPatchSystemService.runPatch("/path/to/script-2.groovy")).willReturn(new GroovyPatchResult());

        List<String> patchFiles = new ArrayList<>();
        patchFiles.add("/path/to/script-1.groovy");
        patchFiles.add("/path/to/script-2.groovy");

        List<String> typeList = new ArrayList<>();
        typeList.add("groovy");
        typeList.add("groovy");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(patchFiles);
        given(job.getProperty(JetpackConstants.TYPES, List.class)).willReturn(typeList);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }

    @Test
    public void testProcess_OnDeployScripts_success() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(onDeployScriptSystemService.runPatch("be.ida.OnDeployScript1")).willReturn(new OnDeployPatchResult());
        given(onDeployScriptSystemService.runPatch("be.ida.OnDeployScript2")).willReturn(new OnDeployPatchResult());

        List<String> patchFiles = new ArrayList<>();
        patchFiles.add("be.ida.OnDeployScript1");
        patchFiles.add("be.ida.OnDeployScript2");

        List<String> typeList = new ArrayList<>();
        typeList.add("onDeployScript");
        typeList.add("onDeployScript");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(patchFiles);
        given(job.getProperty(JetpackConstants.TYPES, List.class)).willReturn(typeList);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }

    @Test
    public void testProcess_MixedScripts_success() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);

        given(groovyPatchSystemService.runPatch("/path/to/script-1.groovy")).willReturn(new GroovyPatchResult());
        given(onDeployScriptSystemService.runPatch("be.ida.OnDeployScript2")).willReturn(new OnDeployPatchResult());

        List<String> patchFiles = new ArrayList<>();
        patchFiles.add("/path/to/script-1.groovy");
        patchFiles.add("be.ida.OnDeployScript2");

        List<String> typeList = new ArrayList<>();
        typeList.add("groovy");
        typeList.add("onDeployScript");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(patchFiles);
        given(job.getProperty(JetpackConstants.TYPES, List.class)).willReturn(typeList);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }

    @Test
    public void testProcess_unknownType() {
        JobExecutionContext.ResultBuilder resultBuilder = mock(JobExecutionContext.ResultBuilder.class);
        given(context.result()).willReturn(resultBuilder);
        JobExecutionResult successJobExecutionResult = mock(JobExecutionResult.class);
        given(resultBuilder.succeeded()).willReturn(successJobExecutionResult);


        List<String> patchFiles = new ArrayList<>();
        patchFiles.add("a-script.txt");

        List<String> typeList = new ArrayList<>();
        typeList.add("something-else");

        given(job.getProperty(JetpackConstants.PATCH_PATHS, List.class)).willReturn(patchFiles);
        given(job.getProperty(JetpackConstants.TYPES, List.class)).willReturn(typeList);

        JobExecutionResult result = patchJobExecutor.process(job, context);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(successJobExecutionResult);
    }
}
