package be.ida.jetpack.patchsystem.groovy.models;

import be.ida.jetpack.patchsystem.models.PatchResult;
import be.ida.jetpack.patchsystem.models.PatchStatus;
import be.ida.jetpack.patchsystem.utils.DateUtils;
import be.ida_mediafoundry.jetpack.carve.annotations.CarveId;
import be.ida_mediafoundry.jetpack.carve.annotations.CarveModel;
import be.ida_mediafoundry.jetpack.carve.manager.pathpolicy.providers.SimplePathPolicyProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Calendar;

@CarveModel(pathPolicyProvider = SimplePathPolicyProvider.class, location = "/var/patches/completed")
@Model(adaptables = Resource.class)
public class GroovyPatchResult implements PatchResult {

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

    public GroovyPatchResult() {
    }

    public GroovyPatchResult(String id, PatchStatus status, Calendar startDate) {
        this.id = id;
        this.status = status.displayName();
        this.startDate = startDate;

        initModel();
    }

    @PostConstruct
    protected void initModel() {
        if (StringUtils.isBlank(this.runningTime)) {
            this.runningTime = DateUtils.formattedRunningTime(this);
        }
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

    public void setStatus(PatchStatus status) {
        this.status = status.displayName();
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

    @Override
    public boolean isError() {
        return PatchStatus.ERROR.isOfStatus(this);
    }
}
