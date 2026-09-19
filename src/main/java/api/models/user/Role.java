package api.models.user;

import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseModel {
    private String roleId;
    private String scope;
}
