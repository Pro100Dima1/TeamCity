package api.models.build_type;

import api.models.BaseModel;
import api.models.build_step.BuildStepsResponse;
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
public class BuildTypeResponse extends BaseModel {
    private String id;
    private String name;
    private String projectId;
    private String projectName;
    private BuildStepsResponse steps;
}
