package common.annotations;

import common.extensions.EnableAgentExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(EnableAgentExtension.class)
public @interface EnableAgent {

    boolean enabled() default true;
}