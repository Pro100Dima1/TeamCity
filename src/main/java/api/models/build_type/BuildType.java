package api.models.build_type;

import api.models.BaseModel;
import api.models.project.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildType extends BaseModel {
    private String id;
    private String name;
    private String projectId;
    private String projectName;
    private Project project;
    private String href;
    private String webUrl;
}
