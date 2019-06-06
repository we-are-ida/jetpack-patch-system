package be.ida.jetpack.patchsystem.models;

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PatchFileWithResultTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() throws Exception {
        context.load().json("/mocks/patches.json", "/apps/patches");
        context.addModelsForClasses(PatchFileWithResult.class, PatchFolder.class, PatchFile.class);
        context.request().setContextPath("context");
    }

    @Test
    public void test_newResult_notModified() {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        Calendar calendar = Calendar.getInstance();
        PatchResult patchResult = new PatchResult("1000", "RUNNING", calendar);

        PatchFileWithResultResource patchFileWithResultResource = new PatchFileWithResultResource(context.resourceResolver(),
                patchFile, patchResult, false);

        //test 1
        ValueMap valueMap = patchFileWithResultResource.getValueMap();
        assertThat(valueMap.get("status")).isEqualTo("RUNNING");
        assertThat(valueMap.get("startDate")).isEqualTo(calendar);
        assertThat(valueMap.get("scriptName")).isEqualTo("script-1.groovy");
        assertThat(valueMap.get("output")).isNull();
        assertThat(valueMap.get("endDate")).isNull();
        assertThat(valueMap.get("projectName")).isEqualTo("project-A");

        //test
        context.request().setResource(patchFileWithResultResource);
        PatchFileWithResult patchFileWithResult = context.request().adaptTo(PatchFileWithResult.class);
        assertThat(patchFileWithResult).isNotNull();
        assertThat(patchFileWithResult.getStatusClass()).isEqualTo("warning");
        assertThat(patchFileWithResult.getStatus()).isEqualTo("RUNNING");
        assertThat(patchFileWithResult.getPath()).isEqualTo("/apps/patches/project-A/script-1.groovy");
        assertThat(patchFileWithResult.getScript()).isEqualTo("script-1.groovy");
        assertThat(patchFileWithResult.getDateExecuted()).isNull();
    }

    @Test
    public void test_finalResult_Success_notModified() {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        Calendar calendar = Calendar.getInstance();
        PatchResult patchResult = new PatchResult("1000", "RUNNING", calendar);
        patchResult.setStatus("SUCCESS");
        patchResult.setOutput("Output");
        patchResult.setEndDate(calendar);
        patchResult.setRunningTime("200");
        patchResult.setMd5("AAAA1");

        PatchFileWithResultResource patchFileWithResultResource = new PatchFileWithResultResource(context.resourceResolver(),
                patchFile, patchResult, false);

        //test
        context.request().setResource(patchFileWithResultResource);
        PatchFileWithResult patchFileWithResult = context.request().adaptTo(PatchFileWithResult.class);
        assertThat(patchFileWithResult).isNotNull();
        assertThat(patchFileWithResult.getStatusClass()).isEqualTo("success");
        assertThat(patchFileWithResult.getStatus()).isEqualTo("SUCCESS");
        assertThat(patchFileWithResult.getOutput()).isEqualTo("Output");
        assertThat(patchFileWithResult.getRunningTime()).isEqualTo("200");
    }

    @Test
    public void test_finalResult_Error_notModified() {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        Calendar calendar = Calendar.getInstance();
        PatchResult patchResult = new PatchResult("1000", "RUNNING", calendar);
        patchResult.setStatus("ERROR");
        patchResult.setOutput("Output");
        patchResult.setEndDate(calendar);
        patchResult.setRunningTime("200");
        patchResult.setMd5("AAAA1");

        PatchFileWithResultResource patchFileWithResultResource = new PatchFileWithResultResource(context.resourceResolver(),
                patchFile, patchResult, false);

        //test
        context.request().setResource(patchFileWithResultResource);
        PatchFileWithResult patchFileWithResult = context.request().adaptTo(PatchFileWithResult.class);
        assertThat(patchFileWithResult).isNotNull();
        assertThat(patchFileWithResult.getStatusClass()).isEqualTo("error");
        assertThat(patchFileWithResult.getStatus()).isEqualTo("ERROR");
        assertThat(patchFileWithResult.getOutput()).isEqualTo("Output");
        assertThat(patchFileWithResult.getRunningTime()).isEqualTo("200");
    }

    @Test
    public void test_finalResult_Success_modified() {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        Calendar calendar = Calendar.getInstance();
        PatchResult patchResult = new PatchResult("1000", "RUNNING", calendar);
        patchResult.setStatus("SUCCESS");
        patchResult.setOutput("Output");
        patchResult.setEndDate(calendar);
        patchResult.setRunningTime("200");
        patchResult.setMd5("AAAA1");

        PatchFileWithResultResource patchFileWithResultResource = new PatchFileWithResultResource(context.resourceResolver(),
                patchFile, patchResult, true);

        //test
        context.request().setResource(patchFileWithResultResource);
        PatchFileWithResult patchFileWithResult = context.request().adaptTo(PatchFileWithResult.class);
        assertThat(patchFileWithResult).isNotNull();
        assertThat(patchFileWithResult.getStatusClass()).isEqualTo("info");
        assertThat(patchFileWithResult.getStatus()).isEqualTo("RE-RUN");
        assertThat(patchFileWithResult.getOutput()).isEqualTo("Output");
        assertThat(patchFileWithResult.getRunningTime()).isEqualTo("200");
        assertThat(patchFileWithResult.getDateExecuted()).isNotNull();
        assertThat(patchFileWithResult.getProject()).isEqualTo("project-A");
    }

    @Test
    public void test_finalResult_other_notModified() {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        Calendar calendar = Calendar.getInstance();
        PatchResult patchResult = new PatchResult("1000", "RUNNING", calendar);
        patchResult.setStatus("OTHER");
        patchResult.setOutput("Output");
        patchResult.setEndDate(calendar);
        patchResult.setRunningTime("200");
        patchResult.setMd5("AAAA1");

        PatchFileWithResultResource patchFileWithResultResource = new PatchFileWithResultResource(context.resourceResolver(),
                patchFile, patchResult, false);

        //test
        context.request().setResource(patchFileWithResultResource);
        PatchFileWithResult patchFileWithResult = context.request().adaptTo(PatchFileWithResult.class);
        assertThat(patchFileWithResult).isNotNull();
        assertThat(patchFileWithResult.getStatusClass()).isEqualTo("info");
        assertThat(patchFileWithResult.getStatus()).isEqualTo("OTHER");
        assertThat(patchFileWithResult.getOutput()).isEqualTo("Output");
        assertThat(patchFileWithResult.getRunningTime()).isEqualTo("200");
        assertThat(patchFileWithResult.getDateExecuted()).isNotNull();
        assertThat(patchFileWithResult.getProject()).isEqualTo("project-A");
    }

    @Test
    public void test_noResult() {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        PatchFileWithResultResource patchFileWithResultResource = new PatchFileWithResultResource(context.resourceResolver(),
                patchFile, null, false);

        //test
        context.request().setResource(patchFileWithResultResource);
        PatchFileWithResult patchFileWithResult = context.request().adaptTo(PatchFileWithResult.class);
        assertThat(patchFileWithResult).isNotNull();
        assertThat(patchFileWithResult.getStatusClass()).isEqualTo("new");
        assertThat(patchFileWithResult.getStatus()).isEqualTo("NEW");
        assertThat(patchFileWithResult.getOutput()).isNull();
        assertThat(patchFileWithResult.getRunningTime()).isNull();
        assertThat(patchFileWithResult.getThumbnail()).isEqualTo("context/apps/jetpack/patchsystem/components/thumb.png");
        assertThat(patchFileWithResult.getDateExecuted()).isNull();
    }

    @Test
    public void test_AdaptToValueMap() {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        Calendar calendar = Calendar.getInstance();
        PatchResult patchResult = new PatchResult("1000", "RUNNING", calendar);
        patchResult.setStatus("ERROR");
        patchResult.setOutput("Output");
        patchResult.setEndDate(calendar);
        patchResult.setRunningTime("200");
        patchResult.setMd5("AAAA1");

        PatchFileWithResultResource patchFileWithResultResource = new PatchFileWithResultResource(context.resourceResolver(),
                patchFile, patchResult, false);

        ValueMap valueMap = patchFileWithResultResource.adaptTo(ValueMap.class);
        assertThat(valueMap).isNotNull();
    }
}
