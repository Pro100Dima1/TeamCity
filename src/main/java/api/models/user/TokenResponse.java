package api.models.user;

import api.models.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TokenResponse extends BaseModel {

    private String name;
    private String creationTime;
    private String value;
    private String expirationTime;
}
