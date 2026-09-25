package api.models.build;

import api.models.BaseModel;
import lombok.*;

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
