package api.models.build_step;

import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Property extends BaseModel {
    private String name;
    private String value;
}
