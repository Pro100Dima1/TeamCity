package api.models.build_type;

import api.generators.GeneratingRule;
import api.models.BaseModel;
import api.models.build_step.CreateBuildStepRequest;
import api.models.project.ProjectResponse;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBuildTypeRequest extends BaseModel {
    @GeneratingRule(regex = "[a-zA-Z][a-zA-Z0-9_]{7}")
    private String id;
    private String name;
    private ProjectResponse project;
    private CreateBuildStepRequest steps;
}
