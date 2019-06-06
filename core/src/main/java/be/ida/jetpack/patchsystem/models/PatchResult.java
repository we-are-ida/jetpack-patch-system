package be.ida.jetpack.patchsystem.models;

import be.ida.jetpack.carve.annotations.CarveId;
import be.ida.jetpack.carve.annotations.CarveModel;
import be.ida.jetpack.carve.manager.pathpolicy.providers.SimplePathPolicyProvider;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;
import java.util.Calendar;

@CarveModel(pathPolicyProvider = SimplePathPolicyProvider.class, location = "/var/patches/completed")
@Model(adaptables = Resource.class)
public class PatchResult {

    @CarveId
    @Inject
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
    private String md5;

    @Inject
    @Optional
    private String output;

    @Inject
    @Optional
    private String runningTime;

    public PatchResult() {
    }

    public PatchResult(String id, String status, Calendar startDate) {
        this.id = id;
        this.status = status;
        this.startDate = startDate;
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

    public String getMd5() {
        return md5;
    }

    public String getOutput() {
        return output;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }
}
