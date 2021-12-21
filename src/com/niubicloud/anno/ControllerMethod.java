package com.niubicloud.anno;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ControllerMethod {
	String name() default "";
	String contentType() default "";
	String headers() default "";
	boolean GET() default true;
	boolean POST() default false;
	boolean HEAD() default false;
	boolean PUT() default false;
	boolean OPTIONS() default false;
	boolean DELETE() default false;
}
