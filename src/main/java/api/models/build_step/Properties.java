package api.models.build_step;

import api.models.BaseModel;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Properties extends BaseModel {
    private Integer count;
    private List<Property> property;
}
