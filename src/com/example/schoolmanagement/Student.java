package com.example.schoolmanagement;// com.example.schoolmanagement.Student.java

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person implements Serializable {
    private String studentID;

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public static void setStudentCount(int studentCount) {
        Student.studentCount = studentCount;
    }

    private String address;
    private List<Course> enrolledCourses;
    private static int studentCount = 0;

    public Student(String name, String email, LocalDate dateOfBirth, String address) {
        super(name, email, dateOfBirth);
        this.studentID = "S" + String.format("%03d", ++studentCount);
        this.address = address;
        this.enrolledCourses = new ArrayList<>();
        University.incrementStudentCount();
    }

    public String getStudentID() {
        return studentID;
    }
    public String getStudentName(){
        return name;
    }
    public void enrollInCourse(Course course, Double grade) {
        enrolledCourses.add(course);
        course.addStudent(this, grade);
        System.out.println("Student " + studentID + " successfully enrolled in " + course.getTitle() + ".");
    }

    public void displayCourses() {
        System.out.println("Courses enrolled by " + name + " (" + studentID + "):");
        for (Course course : enrolledCourses) {
            System.out.println("- " + course.getTitle() + " (" + course.getCourseID() + "), Credits: " + course.getCredits());
        }
    }

    @Override
    public void displayDetails() {
        System.out.println(this.toString());
    }

    public String getAddress() {

        return address;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    @Override
    public String toString() {
        String baseStr = "";
        for (Course course : enrolledCourses) {
            baseStr += course.getTitle() + ", ";
        }
        if (baseStr.length() > 0) {
            baseStr = baseStr.substring(0, baseStr.length() - 2);
        }
        return "Student: " + studentID + ", Name: " + name + ", Enrolled in: [" + baseStr + "]";
    }
}
