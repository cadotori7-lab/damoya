package com.soldesk.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectCompletionVO {

    private int totalTaskCount;
    private int completedTaskCount;
    private List<IncompleteTaskVO> incompleteTasks = new ArrayList<>();
    private TaskVO finalResult;

    public int getTotalTaskCount() {
        return totalTaskCount;
    }

    public void setTotalTaskCount(int totalTaskCount) {
        this.totalTaskCount = totalTaskCount;
    }

    public int getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(int completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    public List<IncompleteTaskVO> getIncompleteTasks() {
        return Collections.unmodifiableList(incompleteTasks);
    }

    public void setIncompleteTasks(List<IncompleteTaskVO> incompleteTasks) {
        this.incompleteTasks = incompleteTasks == null
                ? new ArrayList<>()
                : new ArrayList<>(incompleteTasks);
    }

    public TaskVO getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(TaskVO finalResult) {
        this.finalResult = finalResult;
    }

    public boolean isAllTasksCompleted() {
        return incompleteTasks.isEmpty();
    }

    public boolean isCompleted() {
        return finalResult != null;
    }

    public String getFinalFileName() {
        if (finalResult == null
                || finalResult.getSubmit_file() == null
                || finalResult.getSubmit_file().trim().isEmpty()) {
            return "";
        }
        String savedName = finalResult.getSubmit_file();
        int separator = savedName.indexOf('_');
        return separator >= 0 && separator < savedName.length() - 1
                ? savedName.substring(separator + 1)
                : savedName;
    }
}
