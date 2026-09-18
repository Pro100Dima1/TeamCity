package common.extensions;

import api.steps.ProjectSteps;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;

public class ProjectCleanupExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) {
        Object testInstance = context.getRequiredTestInstance();

        try {
            Field projectIdField = testInstance.getClass().getDeclaredField("projectId");
            projectIdField.setAccessible(true);

            String projectId = (String) projectIdField.get(testInstance);

            if (projectId == null || projectId.isBlank()) {
                return;
            }

            ProjectSteps.deleteProject(projectId);
            projectIdField.set(testInstance, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to cleanup created project", e);
        }
    }
}