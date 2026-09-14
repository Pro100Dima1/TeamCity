package api.models.build;

import api.models.BaseModel;
import api.models.build_type.BuildType;
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
public class Build extends BaseModel {
    private Long id;
    private String buildTypeId;
    private String number;
    private String status;
    private String statusText;
    private String state;
    private Boolean running;
    private String href;
    private String webUrl;
    private BuildType buildType;
}
