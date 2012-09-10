package org.ugate.service.entity;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Matcher;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.ugate.service.entity.Email.EmailValidator;

/**
 * <p>
 * Email bean validator
 * </p>
 * Description: annotation to validate an email address (by pattern)<br/>
 */
// @Pattern(regexp = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$",
// message="valid.email.format")
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
		private static String ATOM = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
		private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
		private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
		public static final java.util.regex.Pattern DEFAULT_EMAIL_PATTERN;
		static {
			DEFAULT_EMAIL_PATTERN = java.util.regex.Pattern.compile(
					"^" + ATOM + "+(\\." + ATOM + "+)*@" + DOMAIN + "|"
							+ IP_DOMAIN + ")$",
					java.util.regex.Pattern.CASE_INSENSITIVE);
		}

		public boolean isValid(CharSequence value,
				ConstraintValidatorContext context) {
			if (value == null) {
				return true;
			}
			if (value.length() == 0) {
				return true;
			}
			final Matcher m = DEFAULT_EMAIL_PATTERN.matcher(value);
			return m.matches();
		}

		public void initialize(Email parameters) {
			// do nothing (as long as Email has no properties)
		}
	}
}
