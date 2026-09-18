package prog5121_poe;

public class Login {
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellNumber;
    private String firstName;
    private String lastName;

    // Username: must contain "_" and be no more than 5 characters
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // Password: 8+ chars, a capital letter, a number, a special character
    public boolean checkPasswordComplexity(String password) {
        boolean hasLength = password.length() >= 8;
        boolean hasCapital = password.chars().anyMatch(Character::isUpperCase);
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        return hasLength && hasCapital && hasNumber && hasSpecial;
    }

    // Cell number: international code + number, no more than 10 chars after the code
    public boolean checkCellPhoneNumber(String cellNumber) {
        return cellNumber.matches("^\\+27\\d{9}$");
    }

    public String registerUser(String username, String password, String cellNumber,
                                String fName, String lName) {
        StringBuilder result = new StringBuilder();

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username "
                 + "contains an underscore and is no more than five characters in length.";
        }
        result.append("Username successfully captured.\n");

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password "
                 + "contains at least eight characters, a capital letter, a number, "
                 + "and a special character.";
        }
        result.append("Password successfully captured.\n");

        if (!checkCellPhoneNumber(cellNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        result.append("Cell phone number successfully added.\n");

        this.registeredUsername = username;
        this.registeredPassword = password;
        this.registeredCellNumber = cellNumber;
        this.firstName = fName;
        this.lastName = lName;

        result.append("User registered successfully.");
        return result.toString();
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(this.registeredUsername)
            && enteredPassword.equals(this.registeredPassword);
    }

    public String returnLoginStatus(boolean isLoggedIn) {
        if (isLoggedIn) {
            return "Welcome " + this.firstName + ", " + this.lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
