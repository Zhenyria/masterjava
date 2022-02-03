package ru.javaops.masterjava.model;

import java.util.Objects;

/**
 * Simple DTO which contains users main data
 */
public class UserData {
    private final String fullName;
    private final String email;

    public UserData(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equals(fullName, userData.fullName) && Objects.equals(email, userData.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email);
    }
}
