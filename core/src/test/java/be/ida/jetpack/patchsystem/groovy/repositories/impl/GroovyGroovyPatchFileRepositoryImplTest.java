package be.ida.jetpack.patchsystem.groovy.repositories.impl;

import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFolder;
import be.ida.jetpack.patchsystem.groovy.repositories.GroovyPatchFileRepository;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GroovyGroovyPatchFileRepositoryImplTest {

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() {
        context.load().json("/mocks/patches.json", "/apps/patches");
        context.addModelsForClasses(GroovyPatchFile.class, GroovyPatchFolder.class);
        context.registerInjectActivateService(new GroovyPatchFileRepositoryImpl());
    }

    @Test
    public void testGetPatches() {
        GroovyPatchFileRepository repository = context.getService(GroovyPatchFileRepository.class);
        //given
        Resource resource = context.resourceResolver().getResource("/apps/patches");
        context.request().setResource(resource);

        List<GroovyPatchFile> patchFileList = repository.getPatches();
        assertThat(patchFileList).isNotEmpty();
        assertThat(patchFileList.size()).isEqualTo(4);

        GroovyPatchFile patchFile1 = patchFileList.get(0);
        assertThat(patchFile1.getProjectName()).isEqualTo("project-A");
        assertThat(patchFile1.getResultPath()).isEqualTo("project-A/script-1.groovy");

        GroovyPatchFile patchFile2 = patchFileList.get(1);
        assertThat(patchFile2.getProjectName()).isEqualTo("Project B");
        assertThat(patchFile2.getResultPath()).isEqualTo("project-B/script-2.groovy");

        GroovyPatchFile patchFile3 = patchFileList.get(2);
        assertThat(patchFile3.getProjectName()).isEqualTo("Project B > Sub-Project B");
        assertThat(patchFile3.getResultPath()).isEqualTo("project-B/sub-project-B/nested-script-3.groovy");

        GroovyPatchFile patchFile4 = patchFileList.get(3);
        assertThat(patchFile4.getProjectName()).isNull();
        assertThat(patchFile4.getResultPath()).isEqualTo("script-4.groovy");
    }

    @Test
    public void testGetPatch_projectA() {
        GroovyPatchFileRepository repository = context.getService(GroovyPatchFileRepository.class);

        GroovyPatchFile file = repository.getPatch("/apps/patches/project-A/script-1.groovy");
        assertThat(file).isNotNull();
        assertThat(file.getScriptName()).isEqualTo("script-1.groovy");
        assertThat(file.getFileContent()).isEqualTo("//Hello Script 1");
        assertThat(file.getMd5()).isEqualTo("9180daf17004dd65ba43b8db396e692f");
        assertThat(file.getPath()).isEqualTo("/apps/patches/project-A/script-1.groovy");
        assertThat(file.getProjectName()).isEqualTo("project-A");
        assertThat(file.getResultPath()).isEqualTo("project-A/script-1.groovy");
    }

    @Test
    public void testGetPatch_projectB() {
        GroovyPatchFileRepository repository = context.getService(GroovyPatchFileRepository.class);

        GroovyPatchFile file = repository.getPatch("/apps/patches/project-B/script-2.groovy");
        assertThat(file).isNotNull();
        assertThat(file.getScriptName()).isEqualTo("script-2.groovy");
        assertThat(file.getFileContent()).isEqualTo("//Hello Script 2");
        assertThat(file.getMd5()).isEqualTo("ca5df826cde2ad761f8c13c36951c00d");
        assertThat(file.getPath()).isEqualTo("/apps/patches/project-B/script-2.groovy");
        assertThat(file.getProjectName()).isEqualTo("Project B");
        assertThat(file.getResultPath()).isEqualTo("project-B/script-2.groovy");
    }

    @Test
    public void testGetPatch_subProjectB() {
        GroovyPatchFileRepository repository = context.getService(GroovyPatchFileRepository.class);

        GroovyPatchFile file = repository.getPatch("/apps/patches/project-B/sub-project-B/nested-script-3.groovy");
        assertThat(file).isNotNull();
        assertThat(file.getScriptName()).isEqualTo("nested-script-3.groovy");
        assertThat(file.getFileContent()).isEqualTo("//Hello Script 3");
        assertThat(file.getMd5()).isEqualTo("bb36a61994ea126627b834a828c56568");
        assertThat(file.getPath()).isEqualTo("/apps/patches/project-B/sub-project-B/nested-script-3.groovy");
        assertThat(file.getProjectName()).isEqualTo("Project B > Sub-Project B");
        assertThat(file.getResultPath()).isEqualTo("project-B/sub-project-B/nested-script-3.groovy");
    }

    @Test
    public void testGetPatch_noParent() {
        GroovyPatchFileRepository repository = context.getService(GroovyPatchFileRepository.class);

        GroovyPatchFile file = repository.getPatch("/apps/patches/script-4.groovy");
        assertThat(file).isNotNull();
        assertThat(file.getScriptName()).isEqualTo("script-4.groovy");
        assertThat(file.getFileContent()).isEqualTo("//Hello Script 4");
        assertThat(file.getMd5()).isEqualTo("a739725d8bb510acb60dc304f806458a");
        assertThat(file.getPath()).isEqualTo("/apps/patches/script-4.groovy");
        assertThat(file.getProjectName()).isNull();
        assertThat(file.getResultPath()).isEqualTo("script-4.groovy");
    }
}
