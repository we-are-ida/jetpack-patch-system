package be.ida.jetpack.patchsystem.models;

import java.util.Calendar;

/**
 * @author michael
 * @since 2019-06-12
 */
public interface PatchResult {

    String getId();

    String getStatus();

    Calendar getStartDate();

    Calendar getEndDate();

    String getOutput();

    String getRunningTime();

    boolean isError();
}
