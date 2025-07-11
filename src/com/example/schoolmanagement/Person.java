package com.example.schoolmanagement;// com.example.schoolmanagement.Person.java
import java.io.Serializable;
import java.time.LocalDate;

public abstract class Person implements Serializable{
    protected String name;
    protected String email;
    protected LocalDate dateOfBirth;

    public Person(String name, String email, LocalDate dateOfBirth) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public abstract void displayDetails();

    @Override
    public abstract String toString();
}
