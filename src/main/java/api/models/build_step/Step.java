package api.models.build_step;

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
public class Step extends BaseModel {
    private String id;
    private String name;
    private String type;
    private Boolean disabled;
    private Properties properties;
    private String shortDescription;
}
