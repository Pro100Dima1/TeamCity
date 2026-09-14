package api.models.build_step;

import api.models.BaseModel;
import java.util.List;
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
public class BuildStepsResponse extends BaseModel {
    private Integer count;
    private List<BuildStepResponse> step;
}
