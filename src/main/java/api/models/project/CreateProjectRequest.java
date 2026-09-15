package api.models.project;

import api.generators.GeneratingRule;
import api.models.BaseModel;
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
public class CreateProjectRequest extends BaseModel {
    @GeneratingRule(regex = "Project_[A-Z][a-z]{3}")
    private String name;
    @GeneratingRule(regex = "[a-zA-Z][a-zA-Z0-9]{7}")
    private String id;
    private String description;
    /** Parent stub: set id or locator, e.g. locator="_Root". */
    private ProjectResponse parentProject;
}
