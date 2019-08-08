package be.ida-mediafoundry.jetpack.patchsystem.services;

import be.ida-mediafoundry.jetpack.patchsystem.executors.JobResult;

import java.util.List;

public interface PatchSystemJobService {

    boolean executePatch(String patchPath, String type, boolean runnable);

    boolean executePatches(List<String> patchPaths);

    List<String> getAllPatchesToExecute();

    List<String> executeNewPatches();

    JobResult getPatchSystemStatus();
}