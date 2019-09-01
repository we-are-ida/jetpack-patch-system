package be.ida_mediafoundry.jetpack.patchsystem.ondeploy.repositories.impl;

import be.ida_mediafoundry.jetpack.carve.manager.ModelManager;
import be.ida_mediafoundry.jetpack.carve.manager.exception.ModelManagerException;
import be.ida_mediafoundry.jetpack.patchsystem.ondeploy.models.OnDeployPatchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class OnDeployScriptResultRepositoryImplTest {

    @InjectMocks
    private OnDeployScriptResultRepositoryImpl repository;

    @Mock
    private ModelManager modelManager;

    @Test
    public void testGetPatchResult_found() throws ModelManagerException {
        given(modelManager.retrieve(OnDeployPatchResult.class, "be.ida.script.Modify")).willReturn(new OnDeployPatchResult());

        OnDeployPatchResult patchResult = repository.getResult("be.ida.script.Modify");
        assertThat(patchResult).isNotNull();
    }

    @Test
    public void testGetPatchResult_notFound() throws ModelManagerException {
        given(modelManager.retrieve(OnDeployPatchResult.class, "be.ida.script.Create")).willReturn(null);

        OnDeployPatchResult patchResult = repository.getResult("be.ida.script.Create");
        assertThat(patchResult).isNull();
    }

    @Test
    public void testGetPatchResult_exception() throws ModelManagerException {
        given(modelManager.retrieve(OnDeployPatchResult.class, "be.ida.script.Create")).willThrow(ModelManagerException.class);

        OnDeployPatchResult patchResult = repository.getResult("be.ida.script.Create");
        assertThat(patchResult).isNull();
    }

}
