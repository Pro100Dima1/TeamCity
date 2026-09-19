package api.models.server;

import api.models.BaseModel;
import api.models.build_step.Properties;
import api.models.build_step.Property;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthSettingsRequest extends BaseModel {

    private boolean allowGuest;
    private String guestUsername;
    private boolean collapseLoginForm;
    private boolean perProjectPermissions;
    private boolean emailVerification;
    private String buildAuthenticationMode;
    private AuthModules modules;

    public static AuthSettingsRequest withPerProjectPermissions() {
        return AuthSettingsRequest.builder()
                .allowGuest(false)
                .guestUsername("guest")
                .collapseLoginForm(false)
                .perProjectPermissions(true)
                .emailVerification(false)
                .buildAuthenticationMode("strict")
                .modules(AuthModules.builder()
                        .module(List.of(
                                AuthModule.builder()
                                        .name("Default")
                                        .properties(Properties.builder()
                                                .count(3)
                                                .property(List.of(
                                                        Property.builder()
                                                                .name("usersCanResetOwnPasswords")
                                                                .value("true")
                                                                .build(),
                                                        Property.builder()
                                                                .name("usersCanChangeOwnPasswords")
                                                                .value("true")
                                                                .build(),
                                                        Property.builder()
                                                                .name("freeRegistrationAllowed")
                                                                .value("false")
                                                                .build()
                                                ))
                                                .build())
                                        .build(),
                                AuthModule.builder()
                                        .name("Token-Auth")
                                        .properties(Properties.builder()
                                                .count(0)
                                                .property(List.of())
                                                .build())
                                        .build(),
                                AuthModule.builder()
                                        .name("HTTP-Basic")
                                        .properties(Properties.builder()
                                                .count(0)
                                                .property(List.of())
                                                .build())
                                        .build()
                        ))
                        .build())
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AuthModules {
        private List<AuthModule> module;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AuthModule {
        private String name;
        private Properties properties;
    }
}
