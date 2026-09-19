package api.models.user;

import api.generators.GeneratingRule;
import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel {
    @GeneratingRule(regex = "[a-z]{10}")
    private String username;
    @GeneratingRule(regex = "User_[A-Z][a-z]{4}")
    private String name;
    @GeneratingRule(regex = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
    private String password;
}
