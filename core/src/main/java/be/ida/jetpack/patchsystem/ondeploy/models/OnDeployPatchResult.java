package be.ida.jetpack.patchsystem.ondeploy.models;

import be.ida.jetpack.patchsystem.models.PatchResult;
import be.ida.jetpack.patchsystem.utils.DateUtils;
import be.ida_mediafoundry.jetpack.carve.annotations.CarveId;
import be.ida_mediafoundry.jetpack.carve.annotations.CarveModel;
import be.ida_mediafoundry.jetpack.carve.manager.pathpolicy.providers.SimplePathPolicyProvider;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Calendar;

@CarveModel(pathPolicyProvider = SimplePathPolicyProvider.class, location = "/var/acs-commons/on-deploy-scripts-status")
@Model(adaptables = Resource.class)
public class OnDeployPatchResult implements PatchResult {

    private static final String FAIL = "fail";

    @CarveId
    @Inject
    @Optional
    private String id;

    @Inject
    private String status;

    @Inject
    private Calendar startDate;

    @Inject
    @Optional
    private Calendar endDate;

    //TODO try to get output
    private String output;

    private String runningTime;

    @PostConstruct
    protected void initModel() {
        if (FAIL.equals(this.status)) {
            this.status = "ERROR";
        }
        this.status = status.toUpperCase();

        this.runningTime = DateUtils.formattedRunningTime(this);
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public String getOutput() {
        return output;
    }

    public String getRunningTime() {
        return runningTime;
    }

    @Override
    public boolean isError() {
        return getStatus().equals("ERROR");
    }
}
