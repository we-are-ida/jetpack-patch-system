package be.ida_mediafoundry.jetpack.patchsystem.servlets.responsemodels;

import be.ida.jetpack.patchsystem.models.SimplePatchFile;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author michael
 * @since 2019-06-06
 */
public class PatchesListResponse {

    private int count;
    private List<SimplePatchFile> patches;

    public PatchesListResponse(List<SimplePatchFile> patches) {
        this.patches = patches;
        if (CollectionUtils.isNotEmpty(this.patches)) {
            this.count = this.patches.size();
        }
    }

    public int getCount() {
        return count;
    }

    public List<SimplePatchFile> getPatches() {
        return patches;
    }
}
