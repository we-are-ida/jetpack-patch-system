package be.ida.jetpack.patchsystem.groovy.services.impl;

import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;
import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.models.PatchFileWithResultResource;
import be.ida.jetpack.patchsystem.groovy.repositories.GroovyPatchResultRepository;
import be.ida.jetpack.patchsystem.groovy.repositories.GroovyPatchFileRepository;
import be.ida.jetpack.patchsystem.models.PatchStatus;
import com.icfolson.aem.groovy.console.GroovyConsoleService;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class GroovyPatchSystemJobServiceImplTest {

    @InjectMocks
    private GroovyPatchSystemServiceImpl patchSystemService;

    @Mock
    private GroovyPatchResultRepository patchResultRepository;
    @Mock
    private GroovyPatchFileRepository patchFileRepository;
    @Mock
    private GroovyConsoleService groovyConsoleService;
    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Test
    public void test_getPatchesToExecute_2Scripts_alreadyExecuted_notModified() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getMd5()).willReturn("100");
        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getMd5()).willReturn("200");
        List<GroovyPatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);

        GroovyPatchResult patchResult1 = new GroovyPatchResult();
        patchResult1.setMd5("100");

        GroovyPatchResult patchResult2 = new GroovyPatchResult();
        patchResult2.setMd5("200");

        given(patchResultRepository.getResult(patchFile1)).willReturn(patchResult1);
        given(patchResultRepository.getResult(patchFile2)).willReturn(patchResult2);

        given(patchFileRepository.getPatches()).willReturn(patchFiles);

        //test
        List<PatchFile> patchFilesToExecute = patchSystemService.getPatchesToExecute();

        //check
        assertThat(patchFilesToExecute).isEmpty();
    }

    @Test
    public void test_getPatchesToExecute_2Scripts_alreadyExecuted_1Modified() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getMd5()).willReturn("100");
        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getMd5()).willReturn("999");
        List<GroovyPatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);

        GroovyPatchResult patchResult1 = new GroovyPatchResult();
        patchResult1.setMd5("100");

        GroovyPatchResult patchResult2 = new GroovyPatchResult();
        patchResult2.setMd5("200");

        given(patchResultRepository.getResult(patchFile1)).willReturn(patchResult1);
        given(patchResultRepository.getResult(patchFile2)).willReturn(patchResult2);

        given(patchFileRepository.getPatches()).willReturn(patchFiles);

        //test
        List<PatchFile> patchFilesToExecute = patchSystemService.getPatchesToExecute();

        //check
        assertThat(patchFilesToExecute).isNotEmpty();
        assertThat(patchFilesToExecute).hasSize(1);
        assertThat(patchFilesToExecute.get(0)).isInstanceOf(GroovyPatchFile.class);
        assertThat(((GroovyPatchFile)patchFilesToExecute.get(0)).getMd5()).isEqualTo("999");
    }

    @Test
    public void test_getPatchesToExecute_2Scripts_1Executed_1New() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getMd5()).willReturn("100");
        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getMd5()).willReturn("200");
        List<GroovyPatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);

        GroovyPatchResult patchResult1 = new GroovyPatchResult();
        patchResult1.setMd5("100");

        given(patchResultRepository.getResult(patchFile1)).willReturn(patchResult1);
        given(patchResultRepository.getResult(patchFile2)).willReturn(null);

        given(patchFileRepository.getPatches()).willReturn(patchFiles);

        //test
        List<PatchFile> patchFilesToExecute = patchSystemService.getPatchesToExecute();

        //check
        assertThat(patchFilesToExecute).isNotEmpty();
        assertThat(patchFilesToExecute.size()).isEqualTo(1);
        assertThat(patchFilesToExecute.get(0)).isInstanceOf(GroovyPatchFile.class);
        assertThat(((GroovyPatchFile)patchFilesToExecute.get(0)).getMd5()).isEqualTo("200");
    }

    @Test
    public void testGetPatches() {
        //given
        GroovyPatchFile patchFile1 = mock(GroovyPatchFile.class);
        given(patchFile1.getMd5()).willReturn("100");
        given(patchFile1.getScriptName()).willReturn("Script 1.groovy");
        given(patchFile1.getProjectName()).willReturn("Project A");
        given(patchFile1.isRunnable()).willReturn(true);
        given(patchFile1.getType()).willReturn("groovy");

        GroovyPatchFile patchFile2 = mock(GroovyPatchFile.class);
        given(patchFile2.getMd5()).willReturn("200");
        given(patchFile2.getScriptName()).willReturn("Script 2.groovy");
        given(patchFile2.getProjectName()).willReturn("Project B");
        GroovyPatchFile patchFile3 = mock(GroovyPatchFile.class);
        given(patchFile3.getScriptName()).willReturn("Script 3.groovy");
        given(patchFile3.getProjectName()).willReturn("Project C");
        List<GroovyPatchFile> patchFiles = new ArrayList<>();
        patchFiles.add(patchFile1);
        patchFiles.add(patchFile2);
        patchFiles.add(patchFile3);

        given(patchResultRepository.getResult(patchFile1)).willReturn(createPatchResult("001", "100"));
        given(patchResultRepository.getResult(patchFile2)).willReturn(createPatchResult("002", "999"));
        given(patchResultRepository.getResult(patchFile3)).willReturn(null);

        given(patchFileRepository.getPatches()).willReturn(patchFiles);

        //test
        List<PatchFileWithResultResource> patches = patchSystemService.getPatches(mock(ResourceResolver.class));

        //check
        assertThat(patches).isNotEmpty();
        assertThat(patches.size()).isEqualTo(3);

        //patch 0 = already executed
        assertThat(patches.get(0).getValueMap()).hasSize(9);
        assertThat(patches.get(0).getValueMap().get("status")).isEqualTo("SUCCESS");
        assertThat(patches.get(0).getValueMap().get("projectName")).isEqualTo("Project A");
        assertThat(patches.get(0).getValueMap().get("scriptName")).isEqualTo("Script 1.groovy");
        assertThat(patches.get(0).getValueMap().get("runnable")).isEqualTo(Boolean.TRUE);
        assertThat(patches.get(0).getValueMap().get("type")).isEqualTo("groovy");

        //patch 1 = already executed, but modified
        assertThat(patches.get(1).getValueMap()).hasSize(9);
        assertThat(patches.get(1).getValueMap().get("status")).isEqualTo("RE-RUN");
        assertThat(patches.get(1).getValueMap().get("projectName")).isEqualTo("Project B");
        assertThat(patches.get(1).getValueMap().get("scriptName")).isEqualTo("Script 2.groovy");

        //patch 2 = new script
        assertThat(patches.get(2).getValueMap()).hasSize(5);
        assertThat(patches.get(2).getValueMap().get("status")).isEqualTo("NEW");
        assertThat(patches.get(2).getValueMap().get("projectName")).isEqualTo("Project C");
        assertThat(patches.get(2).getValueMap().get("scriptName")).isEqualTo("Script 3.groovy");
    }

    @Test
    public void testRunPatch_firstRun_success() throws Exception {
        GroovyPatchFile patchFile = mock(GroovyPatchFile.class);
        given(patchFile.getMd5()).willReturn("100");
        given(patchFile.getPath()).willReturn("/etc/patch/patchfile.groovy");

        given(patchFileRepository.getPatch("/etc/patch/patchfile.groovy")).willReturn(patchFile);
        GroovyPatchResult patchResult = new GroovyPatchResult(patchFile.getResultPath(), PatchStatus.RUNNING, Calendar.getInstance());
        patchResult.setMd5(patchFile.getMd5());

        given(patchResultRepository.createResult(patchFile)).willReturn(patchResult);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        given(resourceResolverFactory.getServiceResourceResolver(any())).willReturn(resourceResolver);

        RunScriptResponse response = new RunScriptResponse("script", "data", "result", "output", null, "3000", "userId");
        given(groovyConsoleService.runScript(any(MockSlingHttpServletRequest.class), any(MockSlingHttpServletResponse.class), eq("/etc/patch/patchfile.groovy"))).willReturn(response);

        //test
        GroovyPatchResult patchResultReturned = patchSystemService.runPatch("/etc/patch/patchfile.groovy");

        //check
        assertThat(patchResultReturned).isNotNull();
        assertThat(patchResultReturned.getStatus()).isEqualTo("SUCCESS");
        assertThat(patchResultReturned.getOutput()).isEqualTo("output");
        assertThat(patchResultReturned.getRunningTime()).isEqualTo("3000");
    }

    @Test
    public void testRunPatch_patchSystemNotRunning() throws Exception {
        GroovyPatchFile patchFile = mock(GroovyPatchFile.class);
        given(patchFile.getMd5()).willReturn("100");

        given(patchFileRepository.getPatch("/etc/patch/patchfile.groovy")).willReturn(patchFile);
        GroovyPatchResult patchResult = new GroovyPatchResult(patchFile.getResultPath(), PatchStatus.RUNNING, Calendar.getInstance());
        patchResult.setMd5(patchFile.getMd5());

        given(patchResultRepository.createResult(patchFile)).willReturn(patchResult);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        given(resourceResolverFactory.getServiceResourceResolver(any())).willReturn(resourceResolver);

        patchSystemService.unbindGroovyConsole();

        //test
        GroovyPatchResult patchResultReturned = patchSystemService.runPatch("/etc/patch/patchfile.groovy");

        //check
        assertThat(patchResultReturned).isNotNull();
        assertThat(patchResultReturned.getStatus()).isEqualTo("ERROR");
        assertThat(patchResultReturned.getOutput()).isEqualTo("Groovy Console is not installed.");
    }

    @Test
    public void testRunPatch_firstRun_failed() throws Exception {
        GroovyPatchFile patchFile = mock(GroovyPatchFile.class);
        given(patchFile.getMd5()).willReturn("100");
        given(patchFile.getPath()).willReturn("/etc/patch/patchfile.groovy");

        given(patchFileRepository.getPatch("/etc/patch/patchfile.groovy")).willReturn(patchFile);
        GroovyPatchResult patchResult = new GroovyPatchResult(patchFile.getResultPath(), PatchStatus.RUNNING, Calendar.getInstance());
        patchResult.setMd5(patchFile.getMd5());

        given(patchResultRepository.createResult(patchFile)).willReturn(patchResult);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        given(resourceResolverFactory.getServiceResourceResolver(any())).willReturn(resourceResolver);

        RunScriptResponse response = new RunScriptResponse("script", "data", "result", null, "stack trace", "3000", "userId");
        given(groovyConsoleService.runScript(any(MockSlingHttpServletRequest.class), any(MockSlingHttpServletResponse.class), eq("/etc/patch/patchfile.groovy"))).willReturn(response);

        //test
        GroovyPatchResult patchResultReturned = patchSystemService.runPatch("/etc/patch/patchfile.groovy");

        //check
        assertThat(patchResultReturned).isNotNull();
        assertThat(patchResultReturned.getStatus()).isEqualTo("ERROR");
        assertThat(patchResultReturned.getOutput()).isEqualTo("stack trace");
        assertThat(patchResultReturned.getRunningTime()).isEqualTo("3000");
    }

    @Test
    public void testRunPatch_firstRun_exception() throws Exception {
        GroovyPatchFile patchFile = mock(GroovyPatchFile.class);
        given(patchFile.getMd5()).willReturn("100");
        given(patchFile.getPath()).willReturn("/etc/patch/patchfile.groovy");

        given(patchFileRepository.getPatch("/etc/patch/patchfile.groovy")).willReturn(patchFile);
        GroovyPatchResult patchResult = new GroovyPatchResult(patchFile.getResultPath(), PatchStatus.RUNNING, Calendar.getInstance());
        patchResult.setMd5(patchFile.getMd5());

        given(patchResultRepository.createResult(patchFile)).willReturn(patchResult);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        given(resourceResolverFactory.getServiceResourceResolver(any())).willReturn(resourceResolver);

        given(groovyConsoleService.runScript(any(MockSlingHttpServletRequest.class), any(MockSlingHttpServletResponse.class), eq("/etc/patch/patchfile.groovy"))).willThrow(NullPointerException.class);

        //test
        GroovyPatchResult patchResultReturned = patchSystemService.runPatch("/etc/patch/patchfile.groovy");

        //check
        assertThat(patchResultReturned).isNotNull();
        assertThat(patchResultReturned.getStatus()).isEqualTo("ERROR");
        assertThat(patchResultReturned.getOutput()).isEqualTo("Script Execution error, check log files");
        assertThat(patchResultReturned.getRunningTime()).isNotBlank();
    }

    private static GroovyPatchResult createPatchResult(String id, String md5) {
        GroovyPatchResult patchResult = new GroovyPatchResult(id, PatchStatus.RUNNING, Calendar.getInstance());
        patchResult.setMd5(md5);
        patchResult.setStatus(PatchStatus.SUCCESS);
        patchResult.setEndDate(Calendar.getInstance());
        patchResult.setRunningTime("2000");
        patchResult.setOutput("output");
        return patchResult;
    }

}
