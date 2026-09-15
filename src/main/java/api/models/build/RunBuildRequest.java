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
public class RunBuildRequest extends BaseModel {
    private String buildTypeId;
    private BuildTypeResponse buildType;
}
