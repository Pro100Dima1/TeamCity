package api.models.agent;

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
public class Agent extends BaseModel {
    private Integer id;
    private String name;
    private Integer typeId;
    private Boolean connected;
    private Boolean enabled;
    private Boolean authorized;
    private String href;
    private String webUrl;
}
