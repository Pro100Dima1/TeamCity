package api.models.build_step;

import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildStepResponse extends BaseModel {
    private String id;
    private String name;
    private String type;
    private Boolean disabled;
    private Properties properties;
    private String shortDescription;
}
