package api.models.build;

import api.models.BaseModel;
import api.models.build_type.BuildTypeResponse;
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
public class BuildResponse extends BaseModel {
    private Integer id;
    private String buildTypeId;
    private String number;
    private String status;
    private String statusText;
    private String state;
    private Boolean running;
    private BuildTypeResponse buildType;
    private String WaitReason;
}
