package be.ida.jetpack.patchsystem.executors;

public class JobResult {

    private boolean running;

    private int progress;
    private int numberOfPatches;

    private String logs;

    public JobResult(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public int getNumberOfPatches() {
        return numberOfPatches;
    }

    public void setNumberOfPatches(int numberOfPatches) {
        this.numberOfPatches = numberOfPatches;
    }
}
