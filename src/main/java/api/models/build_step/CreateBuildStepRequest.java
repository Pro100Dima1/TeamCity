package api.models.build_step;

import api.generators.CommandLineCommand;
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
public class CreateBuildStepRequest extends BaseModel {
    @GeneratingRule(regex = "[a-zA-Z][a-zA-Z0-9_]{7}")
    private String id;
    @GeneratingRule(regex = "[a-zA-Z][a-zA-Z0-9_]{7}")
    private String name;
    private String type;
    private Boolean disabled;
    private Properties properties;
}
