package be.ida.jetpack.patchsystem.servlets.responsemodels;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author michael
 * @since 2019-06-06
 */
public class PatchesListResponse {

    private int count;
    private List<String> patches;

    public PatchesListResponse(List<String> patches) {
        this.patches = patches;
        if (CollectionUtils.isNotEmpty(this.patches)) {
            this.count = this.patches.size();
        }
    }

    public int getCount() {
        return count;
    }

    public List<String> getPatches() {
        return patches;
    }
}
