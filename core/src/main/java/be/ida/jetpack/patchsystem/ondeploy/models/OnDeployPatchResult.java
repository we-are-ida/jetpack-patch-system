package be.ida.jetpack.patchsystem.ondeploy.models;

import be.ida.jetpack.carve.annotations.CarveId;
import be.ida.jetpack.carve.annotations.CarveModel;
import be.ida.jetpack.carve.manager.pathpolicy.providers.SimplePathPolicyProvider;
import be.ida.jetpack.patchsystem.models.PatchResult;
import be.ida.jetpack.patchsystem.utils.DateUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

    @Inject
    @Optional
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
