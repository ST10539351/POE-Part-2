package com.poe.part2;

public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    public Login() {
    }

    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public boolean checkUserName() {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }
        // Regex source: https://stackoverflow.com/questions/4058001/validate-south-africa-cell-phone-number
        String regex = "^\\+27\\d{1,10}$";
        return cellPhoneNumber.matches(regex);
    }

    public String registerUser() {
        StringBuilder message = new StringBuilder();

        if (!checkUserName()) {
            message.append("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
        } else {
            message.append("Username successfully captured.");
        }

        message.append("\n");

        if (!checkPasswordComplexity()) {
            message.append("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        } else {
            message.append("Password successfully captured.");
        }

        message.append("\n");

        if (!checkCellPhoneNumber()) {
            message.append("Cell phone number incorrectly formatted or does not contain international code.");
        } else {
            message.append("Cell phone number successfully added.");
        }

        if (checkUserName() && checkPasswordComplexity() && checkCellPhoneNumber()) {
            return message.toString() + "\nUser has been registered successfully.";
        }

        return message.toString();
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username != null
                && password != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);
    }

    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}