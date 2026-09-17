package api.models.project;

import api.generators.GeneratingRule;
import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProjectRequest extends BaseModel {
    @GeneratingRule(regex = "Project_[A-Z][a-z]{3}")
    private String name;
    @GeneratingRule(regex = "[a-zA-Z][a-zA-Z0-9_]{7}")
    private String id;
    private String description;
}
