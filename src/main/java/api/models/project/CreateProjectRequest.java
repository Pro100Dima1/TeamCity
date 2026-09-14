package api.models.project;

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
    private String name;
    private String id;
    private String description;
    /** Parent stub: set id or locator, e.g. locator="_Root". */
    private ProjectResponse parentProject;
}
