package com.example.schoolmanagement;// com.example.schoolmanagement.Course.java
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
public class Course implements Serializable {
    private String courseID;
    private String title;
    private int credits;
    private Teacher assignedTeacher;
    private List<Student> enrolledStudents;
    private List<Double> grades;

    // Static counter
    private static int courseCount = 0;

    public Course(String title, int credits) {
        this.courseID = "C" + String.format("%03d", ++courseCount);
        this.title = title;
        this.credits = credits;
        this.enrolledStudents = new ArrayList<>();
        this.grades = new ArrayList<>();
        University.incrementCourseCount();
    }

    public String getCourseID() {
        return courseID;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public Teacher getAssignedTeacher() {
        return assignedTeacher;
    }

    public void setAssignedTeacher(Teacher teacher) {
        this.assignedTeacher = teacher;
    }

    public void addStudent(Student student , double grade) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            grades.add(grade); // Initialize grade
            System.out.println("com.example.schoolmanagement.Student " + student.getStudentID() + " added to " + title + ".");
        } else {
            System.out.println("com.example.schoolmanagement.Student " + student.getStudentID() + " is already enrolled in " + title + ".");
        }
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public void setGrades(List<Double> grades) {
        this.grades = grades;
    }

    public static void setCourseCount(int courseCount) {
        Course.courseCount = courseCount;
    }

    public void removeStudent(Student student) {
        int index = enrolledStudents.indexOf(student);
        if (index != -1) {
            enrolledStudents.remove(index);
            grades.remove(index);
            System.out.println("Student " + student.getStudentID() + " removed from " + title + ".");
        } else {
            System.out.println("Error: Student " + student.getStudentID() + " is not enrolled in " + title + ".");
        }
    }

    public double calculateAverageGrade() {
        if (grades.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (Double grade : grades) {
            sum += grade;
        }
        double average = sum / grades.size();
        return average;
    }

    public double calculateMedianGrade() {
        if (grades.isEmpty()) {
            System.out.println("No grades available for " + title + ".");
            return 0.0;
        }
        List<Double> sortedGrades = new ArrayList<>(grades);
        Collections.sort(sortedGrades);
        int size = sortedGrades.size();
        if (size % 2 == 0) {
            return (sortedGrades.get(size / 2 - 1) + sortedGrades.get(size / 2)) / 2;
        } else {
            return sortedGrades.get(size / 2);
        }
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }
    public List<Double> getGrades() {
        return grades;
    }

    @Override
    public String toString() {
        StringBuilder studentIDs = new StringBuilder();
        for (Student student : enrolledStudents) {
            studentIDs.append(student.getStudentID()).append(", ");
        }
        if (studentIDs.length() > 0) {
            studentIDs.setLength(studentIDs.length() - 2);
        }
        return "com.example.schoolmanagement.Course: " + courseID + ", Title: " + title + ", Credits: " + credits + ", Assigned com.example.schoolmanagement.Teacher: " +
                (assignedTeacher != null ? assignedTeacher.name : "None") + ", Enrolled Students: [" + studentIDs + "]";
    }
}
