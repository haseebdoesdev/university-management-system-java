package com.example.schoolmanagement;// com.example.schoolmanagement.AdministrativeStaff.java
import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class AdministrativeStaff extends Person implements Reportable, Serializable {
    private String staffID;
    private String role;
    private String department;

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public static void setStaffCount(int staffCount) {
        AdministrativeStaff.staffCount = staffCount;
    }

    // Static counter
    private static int staffCount = 0;

    public AdministrativeStaff(String name, String email, LocalDate dateOfBirth, String role, String department) {
        super(name, email, dateOfBirth);
        this.staffID = "A" + String.format("%03d", ++staffCount);
        this.role = role;
        this.department = department;
        University.incrementStaffCount();
    }

    public String getStaffID() {
        return staffID;
    }

    public String getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String generateReport() {
        String report = "Administrative Report\n";
        report += "Staff ID: " + staffID + "\n";
        report += "Name: " + name + "\n";
        report += "Role: " + role + "\n";
        report += "Department: " + department + "\n";
        return report;
    }



    @Override
    public void exportToFile() {
        String report = generateReport();
        String filePath = staffID + "_teacher_report.txt";

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(report.getBytes());
            System.out.println("Export successful!");
        } catch (IOException e) {
            System.out.println("Exporting to File Failed!");
        }
    }

    @Override
    public void displayDetails() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "Administrative Staff: " + staffID + ", Name: " + name + ", Role: " + role + ", Department: " + department;
    }
}
