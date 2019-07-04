package be.ida.jetpack.patchsystem.repositories.impl;

import be.ida_mediafoundry.jetpack.carve.manager.ModelManager;
import be.ida_mediafoundry.jetpack.carve.manager.exception.ModelManagerException;
import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.models.PatchFolder;
import be.ida.jetpack.patchsystem.models.PatchResult;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.IsInstanceOf.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@RunWith(MockitoJUnitRunner.class)
public class PatchResultRepositoryImplTest {

    @InjectMocks
    private PatchResultRepositoryImpl repository;

    @Mock
    private ModelManager modelManager;

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() {
        context.load().json("/mocks/patches.json", "/apps/patches");
        context.addModelsForClasses(PatchFile.class, PatchFolder.class);
    }

    @Test
    public void testGetPatchResult_withFolder() throws ModelManagerException {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-A/script-1.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-A");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        patchFile.setParentFolder(patchFolder);

        //given
        given(modelManager.retrieve(PatchResult.class, "project-A/script-1.groovy")).willReturn(new PatchResult());

        //test
        PatchResult patchResult = repository.getResult(patchFile);
        assertThat(patchResult).isNotNull();
    }

    @Test
    public void testGetPatchResult_withNestedFolder() throws ModelManagerException {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-B/sub-project-B/nested-script-3.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-B");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        Resource subFolderResource = context.resourceResolver().getResource("/apps/patches/project-B/sub-project-B");
        PatchFolder subPatchFolder = subFolderResource.adaptTo(PatchFolder.class);

        subPatchFolder.setParent(patchFolder);
        patchFile.setParentFolder(subPatchFolder);

        //given
        given(modelManager.retrieve(PatchResult.class, "project-B/sub-project-B/nested-script-3.groovy")).willReturn(new PatchResult());

        //test
        PatchResult patchResult = repository.getResult(patchFile);
        assertThat(patchResult).isNotNull();
    }

    @Test
    public void testGetPatchResult_withoutFolder() throws ModelManagerException {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/script-4.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        given(modelManager.retrieve(PatchResult.class, "script-4.groovy")).willReturn(new PatchResult());

        PatchResult patchResult = repository.getResult(patchFile);
        assertThat(patchResult).isNotNull();
    }

    @Test
    public void testGetPatchResult_withoutFolder_notFound() throws ModelManagerException {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/script-4.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        given(modelManager.retrieve(PatchResult.class, "script-4.groovy")).willReturn(null);

        PatchResult patchResult = repository.getResult(patchFile);
        assertThat(patchResult).isNull();
    }

    @Test
    public void testGetPatchResult_exception() throws ModelManagerException {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/script-4.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        given(modelManager.retrieve(PatchResult.class, "script-4.groovy")).willThrow(ModelManagerException.class);

        PatchResult patchResult = repository.getResult(patchFile);
        assertThat(patchResult).isNull();
    }

    @Test
    public void testCreatePatchResult_withoutFolder_notFound() throws ModelManagerException {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/script-4.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        modelManager.persist(any(PatchResult.class));

        PatchResult patchResult = repository.createResult(patchFile);
        assertThat(patchResult).isNotNull();
        assertThat(patchResult.getId()).isEqualTo("script-4.groovy");
        assertThat(patchResult.getStatus()).isEqualTo("RUNNING");
        assertThat(patchResult.getStartDate()).isNotNull();
        assertThat(patchResult.getEndDate()).isNull();
        assertThat(patchResult.getMd5()).isNotNull();
    }

    @Test
    public void testCreatePatchResult_withFolder_notFound() throws ModelManagerException {
        Resource scriptResource = context.resourceResolver().getResource("/apps/patches/project-B/sub-project-B/nested-script-3.groovy");
        PatchFile patchFile = scriptResource.adaptTo(PatchFile.class);

        Resource folderResource = context.resourceResolver().getResource("/apps/patches/project-B");
        PatchFolder patchFolder = folderResource.adaptTo(PatchFolder.class);

        Resource subFolderResource = context.resourceResolver().getResource("/apps/patches/project-B/sub-project-B");
        PatchFolder subPatchFolder = subFolderResource.adaptTo(PatchFolder.class);

        subPatchFolder.setParent(patchFolder);
        patchFile.setParentFolder(subPatchFolder);

        modelManager.persist(any(PatchResult.class));

        PatchResult patchResult = repository.createResult(patchFile);
        assertThat(patchResult).isNotNull();
        assertThat(patchResult.getId()).isEqualTo("project-B/sub-project-B/nested-script-3.groovy");
    }

    @Test
    public void testUpdatePatchResult() throws ModelManagerException {
        PatchResult patchResult = new PatchResult("100", "RUNNING", Calendar.getInstance());

        modelManager.persist(patchResult);

        repository.updateResult(patchResult);
        assertThat(patchResult.getEndDate()).isNotNull();
    }

    @Test
    public void testUpdatePatchResult_exception() throws ModelManagerException {
        PatchResult patchResult = new PatchResult("100", "RUNNING", Calendar.getInstance());

        willThrow(new ModelManagerException("message")).given(modelManager).persist(patchResult);

        repository.updateResult(patchResult);
        assertThat(patchResult).isNotNull();
    }
}
