package be.ida.jetpack.patchsystem.ondeploy.models;

import com.adobe.acs.commons.ondeploy.scripts.OnDeployScriptBase;

/**
 * @author michael
 * @since 2019-06-14
 */
public abstract class OnDeployScriptPatch extends OnDeployScriptBase {

    public abstract String name();

}
