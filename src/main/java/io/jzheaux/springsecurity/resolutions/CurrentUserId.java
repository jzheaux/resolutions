package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.core.annotation.CurrentSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@CurrentSecurityContext(expression="authentication.tokenAttributes['user_id']")
public @interface CurrentUserId {
}
