package api.models.project;

import api.models.BaseModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponse extends BaseModel {
    private String id;
    private String internalId;
    private String name;
    private String parentProjectId;
    private String description;
    private String locator;
}
