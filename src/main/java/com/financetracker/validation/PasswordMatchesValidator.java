package com.financetracker.validation;

import com.financetracker.dto.RegisterDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterDto> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(RegisterDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are handled by @NotNull and @NotBlank
        }

        String password = value.getPassword();
        String confirmPassword = value.getConfirmPassword();

        // Both should be non-null at this point due to @NotBlank, but defensive check
        if (password == null || confirmPassword == null) {
            return false;
        }

        if (password.equals(confirmPassword)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("confirmPassword")
                .addConstraintViolation();
        return false;
    }
}
