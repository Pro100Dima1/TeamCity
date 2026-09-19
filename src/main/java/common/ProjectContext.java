package common;


public class ProjectContext {

    private final String projectId;
    private final String projectName;

    public ProjectContext(String projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }
}