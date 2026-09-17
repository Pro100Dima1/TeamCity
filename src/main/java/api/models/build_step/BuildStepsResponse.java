package api.models.build_step;

import api.models.BaseModel;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildStepsResponse extends BaseModel {
    private Integer count;
    private List<BuildStepResponse> step;
}
