package org.koreait.member.test.annotations;

import org.koreait.member.contants.Authority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockMember {
    long seq() default 1L;
    String email() default "user01@test.org";
    String name() default "사용자01";
    Authority[] authority() default { Authority.USER };
}
