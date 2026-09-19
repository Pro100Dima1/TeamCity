package api.data;

import lombok.Getter;

@Getter
public enum BuildInfo {

    SUCCESS_STATUS("SUCCESS"),
    FINISHED_STATE("finished"),
    QUEUED_STATE("queued"),
    WAIT_REASON("There are no idle compatible agents which can run this build");

    private final String value;

    BuildInfo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}