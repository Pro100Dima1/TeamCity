package api.models.build;

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
public class RunBuildRequest extends BaseModel {

    private BuildTypeReference buildType;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuildTypeReference {

        private String id;
    }
}