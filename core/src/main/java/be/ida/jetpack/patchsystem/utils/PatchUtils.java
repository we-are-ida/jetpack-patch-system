package be.ida.jetpack.patchsystem.utils;

import be.ida.jetpack.patchsystem.models.PatchFile;
import be.ida.jetpack.patchsystem.models.PatchResult;
import org.apache.commons.lang3.StringUtils;

public abstract class PatchUtils {

    /**
     * Check whether there is a difference between the content of the patch file and the earlier created result.
     * Only in case the patch was executed earlier.
     *
     * @param patchFile Patch file to compare
     * @param patchResult Patch result to compare
     * @return true in case a difference is found and the groovy file was updated.
     */
    public static boolean isDiff(PatchFile patchFile, PatchResult patchResult) {
        return patchResult != null && StringUtils.isNotBlank(patchResult.getMd5()) && !patchFile.getMd5().equals(patchResult.getMd5());
    }
}
