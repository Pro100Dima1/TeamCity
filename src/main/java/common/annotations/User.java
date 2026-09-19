package common.annotations;

import common.extensions.UserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Creates a SYSTEM_ADMIN test user before the test, logs in via PAT ({@code userSpec}),
 * and deletes the user after the test.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(UserExtension.class)
public @interface User {
}
