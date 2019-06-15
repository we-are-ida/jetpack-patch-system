package be.ida.jetpack.patchsystem.ondeploytest;

import be.ida.jetpack.patchsystem.ondeploy.models.OnDeployScriptPatch;

/**
 * @author michael
 * @since 2019-06-12
 */
public class TestOnDeployScript extends OnDeployScriptPatch {

    @Override
    public String name() {
        return "This is my test script";
    }

    @Override
    protected void execute() throws Exception {

        logger.info("ddd");

        System.out.println("ddd");
        throw new Exception("dd");

        //TODO output logger

    }

}
