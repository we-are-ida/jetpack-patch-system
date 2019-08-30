package be.ida_mediafoundry.jetpack.patchsystem.services.impl;

import be.ida_mediafoundry.jetpack.patchsystem.JetpackConstants;
import be.ida_mediafoundry.jetpack.patchsystem.executors.JobResult;
import be.ida_mediafoundry.jetpack.patchsystem.executors.PatchJobExecutor;
import be.ida_mediafoundry.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida_mediafoundry.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import be.ida_mediafoundry.jetpack.patchsystem.models.PatchFile;
import be.ida_mediafoundry.jetpack.patchsystem.models.SimplePatchFile;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.services.OnDeployScriptSystemService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PatchSystemJobServiceImplTest {

    @InjectMocks
    private PatchSystemJobServiceImpl patchSystemJobService;

    @Mock
    private JobManager jobManager;

    @Mock
    private GroovyPatchSystemService groovyPatchSystemService;

    @Mock
    private OnDeployScriptSystemService onDeployScriptSystemService;

    @Test
    public void testExecutePatches_empty() {
        //given
        List<SimplePatchFile> patches = Collections.EMPTY_LIST;

        //test
        boolean status = patchSystemJobService.executePatches(patches);

        //check
        assertThat(status).isFalse();
    }

    @Test
    public void testExecutePatches_2patches() {
        //given
        List<SimplePatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(new SimplePatchFile("groovy", "/apps/script/1.groovy"));
        patchFiles.add(new SimplePatchFile("groovy", "/apps/script/2.groovy"));

        List<String> patches = Arrays.asList("/apps/script/1.groovy", "/apps/script/2.groovy");
        List<String> types = Arrays.asList("groovy", "groovy");

        Map<String, Object> properties = new HashMap<>();
        properties.put(JetpackConstants.PATCH_PATHS, patches);
        properties.put(JetpackConstants.TYPES, types);

        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(mock(Job.class));

        //test
        boolean status = patchSystemJobService.executePatches(patchFiles);

        //check
        assertThat(status).isTrue();
    }

    @Test
    public void testExecutePatches_2patches_fails() {
        //given
        List<SimplePatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(new SimplePatchFile("groovy", "/apps/script/1.groovy"));
        patchFiles.add(new SimplePatchFile("groovy", "/apps/script/2.groovy"));

        List<String> patches = Arrays.asList("/apps/script/1.groovy", "/apps/script/2.groovy");
        List<String> types = Arrays.asList("groovy", "groovy");

        Map<String, Object> properties = new HashMap<>();
        properties.put(JetpackConstants.PATCH_PATHS, patches);
        properties.put(JetpackConstants.TYPES, types);

        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(null);

        //test
        boolean status = patchSystemJobService.executePatches(patchFiles);

        //check
        assertThat(status).isFalse();
    }

    @Test
    public void testExecutePatch() {
        //given
        List<String> patches = Arrays.asList("/apps/patches/1.groovy");
        List<String> types = Arrays.asList("bla");

        Map<String, Object> properties = new HashMap<>();
        properties.put(JetpackConstants.PATCH_PATHS, patches);
        properties.put(JetpackConstants.TYPES, types);

        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(mock(Job.class));

        //test
        boolean status = patchSystemJobService.executePatch("/apps/patches/1.groovy", "bla");

        //check
        assertThat(status).isTrue();
    }

    @Test
    public void testExecuteNewPatches_notFound() {
        //given
        given(groovyPatchSystemService.isPatchSystemReady()).willReturn(true);
        given(groovyPatchSystemService.getPatchesToExecute()).willReturn(new ArrayList<>());

        //test
        List<SimplePatchFile> patches = patchSystemJobService.executeNewPatches();

        //check
        assertThat(patches).isEmpty();
    }

    @Test
    public void testExecuteNewPatches_2Found_success() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getPath()).willReturn("/apps/patch/path1.groovy");
        given(patchFile1.getType()).willReturn("groovy");
        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getPath()).willReturn("/apps/patch/path2.groovy");
        given(patchFile2.getType()).willReturn("groovy");
        List<PatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);

        given(groovyPatchSystemService.getPatchesToExecute()).willReturn(patchFiles);
        given(groovyPatchSystemService.isPatchSystemReady()).willReturn(true);
        given(onDeployScriptSystemService.isPatchSystemReady()).willReturn(true);

        given(jobManager.addJob(eq("be/ida/jetpack/patch"), any())).willReturn(mock(Job.class));

        //test
        List<SimplePatchFile> patchesReturned = patchSystemJobService.executeNewPatches();

        //check
        assertThat(patchesReturned).isNotEmpty();
        assertThat(patchesReturned).hasSize(2);
        assertThat(patchesReturned)
                .extracting("patchFile", "type")
                .containsExactly(
                        tuple("/apps/patch/path1.groovy", "groovy"),
                        tuple("/apps/patch/path2.groovy", "groovy")
                );
    }

    @Test
    public void testExecuteNewPatches_2Found_failed() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getPath()).willReturn("/apps/patch/path1.groovy");
        given(patchFile1.getType()).willReturn("groovy");
        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getPath()).willReturn("/apps/patch/path2.groovy");
        given(patchFile2.getType()).willReturn("groovy");
        List<PatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);

        given(groovyPatchSystemService.getPatchesToExecute()).willReturn(patchFiles);
        given(onDeployScriptSystemService.isPatchSystemReady()).willReturn(true);
        given(groovyPatchSystemService.isPatchSystemReady()).willReturn(true);

        List<String> patches = Arrays.asList(new String[] {"/apps/patch/path1.groovy", "/apps/patch/path2.groovy"});
        List<String> types = Arrays.asList(new String[] {"groovy", "groovy"});

        Map<String, Object> properties = new HashMap<>();
        properties.put(JetpackConstants.PATCH_PATHS, patches);
        properties.put(JetpackConstants.TYPES, types);

        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(null);

        //test
        List<SimplePatchFile> patchesReturned = patchSystemJobService.executeNewPatches();

        //check
        assertThat(patchesReturned).isEmpty();
    }

    @Test
    public void testGetPatchSystemStatus_noActiveJobsNull() {
        given(jobManager.findJobs(JobManager.QueryType.ALL, PatchJobExecutor.TOPIC, 1, null))
                .willReturn(null);

        JobResult result = patchSystemJobService.getPatchSystemStatus();

        assertThat(result).isNotNull();
        assertThat(result.isRunning()).isFalse();
    }

    @Test
    public void testGetPatchSystemStatus_noActiveJobsEmptyList() {
        given(jobManager.findJobs(JobManager.QueryType.ALL, PatchJobExecutor.TOPIC, 1, null))
                .willReturn(CollectionUtils.emptyCollection());

        JobResult result = patchSystemJobService.getPatchSystemStatus();

        assertThat(result).isNotNull();
        assertThat(result.isRunning()).isFalse();
    }

    @Test
    public void testGetPatchSystemStatus_1activeJobs() {
        List<Job> jobs = new ArrayList<>();
        Job job = mock(Job.class);
        given(job.getProgressLog()).willReturn(new String[] {"Message 1", "Message 2"});
        given(job.getFinishedProgressStep()).willReturn(0);
        given(job.getProgressStepCount()).willReturn(4);
        jobs.add(job);


        given(jobManager.findJobs(JobManager.QueryType.ALL, PatchJobExecutor.TOPIC, 1, null))
                .willReturn(jobs);

        JobResult result = patchSystemJobService.getPatchSystemStatus();

        assertThat(result).isNotNull();
        assertThat(result.getProgress()).isEqualTo(0);
        assertThat(result.getNumberOfPatches()).isEqualTo(4);
        assertThat(result.getLogs()).isEqualTo("Message 1, Message 2");
    }

    @Test
    public void testUnBinding() {
        patchSystemJobService.unbindGroovyPatchSystemService();
        patchSystemJobService.unbindOnDeployScriptSystemService();

        List<SimplePatchFile> patchFiles = patchSystemJobService.getAllPatchesToExecute();

        assertThat(patchFiles).isEmpty();

        verify(groovyPatchSystemService, never()).getPatchesToExecute();
        verify(onDeployScriptSystemService, never()).getPatchesToExecute();
    }
}
