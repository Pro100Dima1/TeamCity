package api.models.user;

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
public class CreateUserRequest extends BaseModel {
    private String username;
    @GeneratingRule(regex = "User_[A-Z][a-z]{4}")
    private String name;
    @GeneratingRule(regex = "[a-z]{5}@testmail\\.com")
    private String email;
    private String password;
}
