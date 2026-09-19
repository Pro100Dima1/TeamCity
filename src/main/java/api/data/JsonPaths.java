package api.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JsonPaths {
    BUILDS("builds"),
    PROJECTS("projects"),
    USERS("users");
    private final String path;
}
