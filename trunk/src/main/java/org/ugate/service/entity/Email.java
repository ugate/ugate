package org.ugate.service.entity;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.bval.routines.EMailValidationUtils;
import org.ugate.service.entity.Email.EmailValidator;

/**
 * <p>
 * Email bean validator
 * </p>
 * Description: annotation to validate an email address (by pattern)<br/>
 */
//@Pattern(regexp = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$", message="valid.email.format")
@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RUNTIME)
public @interface Email {
	Class<?>[] groups() default {};

	String message() default "{invalid.email.format}";

	Class<? extends Payload>[] payload() default {};

	public static class EmailValidator implements
			ConstraintValidator<Email, CharSequence> {

		public boolean isValid(CharSequence value,
				ConstraintValidatorContext context) {
			return EMailValidationUtils.isValid(value);
		}

		public void initialize(Email parameters) {
			// do nothing (as long as Email has no properties)
		}
	}
}
