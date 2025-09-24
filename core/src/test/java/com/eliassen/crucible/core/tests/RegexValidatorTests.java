package com.eliassen.crucible.core.tests;

import com.eliassen.crucible.core.helpers.RegexValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexValidatorTests
{
    @Test
    public void validate_shouldBeTrue_test() {
        String email = "test-email2@ThisIsATest.COM";
        assertTrue(RegexValidator.validate(email, RegexValidator.EMAIL_PATTERN));
    }

    @Test
    public void validate_shouldBeFalse_test() {
        String email = "test-email2atThisIsTest.COM";
        assertFalse(RegexValidator.validate(email, RegexValidator.EMAIL_PATTERN));
    }
}
