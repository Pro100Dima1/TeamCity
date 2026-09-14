package api.models.project;

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
public class Project extends BaseModel {
    private String id;
    private String internalId;
    private String name;
    private String parentProjectId;
    private String description;
    private String href;
    private String webUrl;
    private String locator;
}
