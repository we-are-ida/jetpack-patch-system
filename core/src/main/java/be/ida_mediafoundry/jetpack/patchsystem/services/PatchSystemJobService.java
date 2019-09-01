package be.ida_mediafoundry.jetpack.patchsystem.services;

import be.ida_mediafoundry.jetpack.patchsystem.executors.JobResult;
import be.ida_mediafoundry.jetpack.patchsystem.models.SimplePatchFile;

import java.util.List;
import java.util.Map;

public interface PatchSystemJobService {

    boolean executePatch(String patchPath, String type);

    boolean executePatches(List<SimplePatchFile> patchFiles);

    List<SimplePatchFile> getAllPatchesToExecute();

    Map<String, Boolean> getReadyStates();

    List<SimplePatchFile> executeNewPatches();

    JobResult getPatchSystemStatus();
}