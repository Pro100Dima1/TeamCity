package api.models.build_step;

import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBuildStepRequest extends BaseModel {
    private String id;
    private String name;
    private String type;
    private Boolean disabled;
    private Properties properties;
}
