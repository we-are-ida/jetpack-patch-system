package be.ida.jetpack.patchsystem.groovy.utils;

import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;
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
    public static boolean isDiff(GroovyPatchFile patchFile, GroovyPatchResult patchResult) {
        return patchResult != null && StringUtils.isNotBlank(patchResult.getMd5()) && !patchFile.getMd5().equals(patchResult.getMd5());
    }
}
