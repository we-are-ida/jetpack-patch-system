package be.ida.jetpack.patchsystem.ondeploytest;

import com.adobe.acs.commons.ondeploy.OnDeployScriptProvider;
import com.adobe.acs.commons.ondeploy.scripts.OnDeployScript;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.List;

@Component(immediate = true)
public class OnDeployScriptRunnerImpl implements OnDeployScriptProvider {

    @Override
    public List<OnDeployScript> getScripts() {
        return Arrays.asList(
                new TestOnDeployScript()
                // List of script instances - e.g. new MyScript1(), new MyScript2(), new MyScript3()
        );
    }

}
