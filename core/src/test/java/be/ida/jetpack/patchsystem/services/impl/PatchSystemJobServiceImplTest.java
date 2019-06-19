package be.ida.jetpack.patchsystem.services.impl;

import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.executors.PatchJobExecutor;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.executors.JobResult;
import be.ida.jetpack.patchsystem.groovy.services.GroovyPatchSystemService;
import be.ida.jetpack.patchsystem.models.PatchFile;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class PatchSystemJobServiceImplTest {

    @InjectMocks
    private PatchSystemJobServiceImpl patchSystemJobService;

    @Mock
    private JobManager jobManager;

    @Mock
    private GroovyPatchSystemService patchSystemService;

    @Test
    public void testExecutePatches_empty() {
        //given
        List<String> patches = Arrays.asList(new String[] {});

        //test
        boolean status = patchSystemJobService.executePatches(patches);

        //check
        assertThat(status).isFalse();
    }

    @Test
    public void testExecutePatches_2patches() {
        //given
        List<String> patches = Arrays.asList(new String[] {"/apps/patches/1.groovy", "/apps/patches/2.groovy"});
        Map<String, Object> properties = Collections.singletonMap(JetpackConstants.PATCH_PATHS, patches);
        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(mock(Job.class));

        //test
        boolean status = patchSystemJobService.executePatches(patches);

        //check
        assertThat(status).isTrue();
    }

    @Test
    public void testExecutePatches_2patches_fails() {
        //given
        List<String> patches = Arrays.asList(new String[] {"/apps/patches/1.groovy", "/etc/patches/2.groovy"});
        Map<String, Object> properties = Collections.singletonMap(JetpackConstants.PATCH_PATHS, patches);
        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(null);

        //test
        boolean status = patchSystemJobService.executePatches(patches);

        //check
        assertThat(status).isFalse();
    }

    @Test
    public void testExecutePatch() {
        //given
        List<String> patches = Arrays.asList(new String[] {"/apps/patches/1.groovy"});
        Map<String, Object> properties = Collections.singletonMap(JetpackConstants.PATCH_PATHS, patches);
        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(mock(Job.class));

        //test
        boolean status = patchSystemJobService.executePatch("/apps/patches/1.groovy", "bla", true);

        //check
        assertThat(status).isTrue();
    }

    @Test
    public void testExecuteNewPatches_notFound() {
        //given
        given(patchSystemService.getPatchesToExecute()).willReturn(new ArrayList<>());

        //test
        List<String> patches = patchSystemJobService.executeNewPatches();

        //check
        assertThat(patches).isEmpty();
    }

    @Test
    public void testExecuteNewPatches_2Found_success() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getPath()).willReturn("/apps/patch/path1.groovy");
        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getPath()).willReturn("/apps/patch/path2.groovy");
        List<PatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);

        given(patchSystemService.getPatchesToExecute()).willReturn(patchFiles);

        List<String> patches = Arrays.asList(new String[] {"/apps/patch/path1.groovy", "/apps/patch/path2.groovy"});

        given(jobManager.addJob(eq("be/ida/jetpack/patch"), any())).willReturn(mock(Job.class));

        //test
        List<String> patchesReturned = patchSystemJobService.executeNewPatches();

        //check
        assertThat(patchesReturned).isNotEmpty();
        assertThat(patchesReturned.size()).isEqualTo(2);
        assertThat(patchesReturned).isEqualTo(patches);
    }

    @Test
    public void testExecuteNewPatches_2Found_failed() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getPath()).willReturn("/apps/patch/path1.groovy");
        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getPath()).willReturn("/apps/patch/path2.groovy");
        List<PatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);

        given(patchSystemService.getPatchesToExecute()).willReturn(patchFiles);

        List<String> patches = Arrays.asList(new String[] {"/apps/patch/path1.groovy", "/apps/patch/path2.groovy"});

        Map<String, Object> properties = Collections.singletonMap(JetpackConstants.PATCH_PATHS, patches);
        given(jobManager.addJob("be/ida/jetpack/patch", properties)).willReturn(null);

        //test
        List<String> patchesReturned = patchSystemJobService.executeNewPatches();

        //check
        assertThat(patchesReturned).isNull();
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
}
