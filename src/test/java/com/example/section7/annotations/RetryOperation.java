package com.example.section7.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(RetryOperations.class)
public @interface RetryOperation {

	Class<? extends Throwable>[] retryExceptions() default { Exception.class };

	long durationBetweenRetriesMs() default 0;

	String failureMessage() default "Operation failed after retrying";

	int numberOfRetries();

}
