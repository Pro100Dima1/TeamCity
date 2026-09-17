package api.models.user;

import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse extends BaseModel {
    private String id;
    private String username;
    private String name;
}
