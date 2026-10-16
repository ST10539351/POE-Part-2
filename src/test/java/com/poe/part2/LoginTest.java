package com.poe.part2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LoginTest {

    private static final String FIRST = "Kyle";
    private static final String LAST = "Smith";
    private static final String GOOD_PASSWORD = "Ch&&sec@ke99!";
    private static final String GOOD_CELL = "+27838968976";

    private static Login make(String username, String password, String cell) {
        return new Login(FIRST, LAST, username, password, cell);
    }

    /** registerUser() returns three lines: username, password, cell. */
    private static String line(String registration, int index) {
        return registration.split("\n")[index];
    }

    // ---------- assertEquals ----------

    @Test
    public void usernameCorrectlyFormattedMessage() {
        String result = make("kyl_1", GOOD_PASSWORD, GOOD_CELL).registerUser();
        assertEquals("Username successfully captured.", line(result, 0));
    }

    @Test
    public void usernameIncorrectlyFormattedMessage() {
        String result = make("kyle!!!!!!!", GOOD_PASSWORD, GOOD_CELL).registerUser();
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                line(result, 0));
    }

    @Test
    public void passwordMeetsComplexityMessage() {
        String result = make("kyl_1", "Ch&&sec@ke99!", GOOD_CELL).registerUser();
        assertEquals("Password successfully captured.", line(result, 1));
    }

    @Test
    public void passwordDoesNotMeetComplexityMessage() {
        String result = make("kyl_1", "password", GOOD_CELL).registerUser();
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                line(result, 1));
    }

    @Test
    public void cellPhoneCorrectlyFormattedMessage() {
        String result = make("kyl_1", GOOD_PASSWORD, "+27838968976").registerUser();
        assertEquals("Cell phone number successfully added.", line(result, 2));
    }

    @Test
    public void cellPhoneIncorrectlyFormattedMessage() {
        String result = make("kyl_1", GOOD_PASSWORD, "08966553").registerUser();
        assertEquals("Cell phone number incorrectly formatted or does not contain international code.",
                line(result, 2));
    }

    @Test
    public void registrationSuccessfulWhenAllConditionsMet() {
        String result = make("kyl_1", GOOD_PASSWORD, GOOD_CELL).registerUser();
        assertTrue(result.endsWith("User has been registered successfully."));
    }

    @Test
    public void registrationNotSuccessfulWhenAnyConditionFails() {
        String result = make("kyle!!!!!!!", GOOD_PASSWORD, GOOD_CELL).registerUser();
        assertFalse(result.contains("User has been registered successfully."));
    }

    @Test
    public void loginStatusSuccessful() {
        Login login = make("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus("kyl_1", GOOD_PASSWORD));
    }

    @Test
    public void loginStatusFailed() {
        Login login = make("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus("kyl_1", "wrong"));
    }

    // ---------- assertTrue / assertFalse ----------

    @Test
    public void loginSuccessful() {
        assertTrue(make("kyl_1", GOOD_PASSWORD, GOOD_CELL).loginUser("kyl_1", GOOD_PASSWORD));
    }

    @Test
    public void loginFailed() {
        assertFalse(make("kyl_1", GOOD_PASSWORD, GOOD_CELL).loginUser("kyl_1", "wrong"));
    }

    @Test
    public void usernameCorrectlyFormatted() {
        assertTrue(make("kyl_1", GOOD_PASSWORD, GOOD_CELL).checkUserName());
    }

    @Test
    public void usernameIncorrectlyFormatted() {
        assertFalse(make("kyle!!!!!!!", GOOD_PASSWORD, GOOD_CELL).checkUserName());
    }

    @Test
    public void passwordMeetsComplexity() {
        assertTrue(make("kyl_1", "Ch&&sec@ke99!", GOOD_CELL).checkPasswordComplexity());
    }

    @Test
    public void passwordDoesNotMeetComplexity() {
        assertFalse(make("kyl_1", "password", GOOD_CELL).checkPasswordComplexity());
    }

    @Test
    public void cellPhoneCorrectlyFormatted() {
        assertTrue(make("kyl_1", GOOD_PASSWORD, "+27838968976").checkCellPhoneNumber());
    }

    @Test
    public void cellPhoneIncorrectlyFormatted() {
        assertFalse(make("kyl_1", GOOD_PASSWORD, "08966553").checkCellPhoneNumber());
    }
}
