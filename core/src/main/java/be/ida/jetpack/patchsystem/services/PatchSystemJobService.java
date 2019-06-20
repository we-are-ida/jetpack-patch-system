package be.ida.jetpack.patchsystem.services;

import be.ida.jetpack.patchsystem.executors.JobResult;
import be.ida.jetpack.patchsystem.models.SimplePatchFile;

import java.util.List;

public interface PatchSystemJobService {

    boolean executePatch(String patchPath, String type);

    boolean executePatches(List<SimplePatchFile> patchFiles);

    List<SimplePatchFile> getAllPatchesToExecute();

    List<SimplePatchFile> executeNewPatches();

    JobResult getPatchSystemStatus();
}