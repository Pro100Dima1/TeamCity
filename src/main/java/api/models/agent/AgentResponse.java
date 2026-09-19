package api.models.agent;

import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentResponse extends BaseModel {
    private Integer id;
    private String name;
    private Integer typeId;
    private Boolean connected;
    private Boolean enabled;
    private Boolean authorized;
}
