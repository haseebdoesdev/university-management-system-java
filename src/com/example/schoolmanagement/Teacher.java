package com.example.schoolmanagement;// com.example.schoolmanagement.Teacher.java
import com.example.schoolmanagement.Course;
import com.example.schoolmanagement.Person;
import com.example.schoolmanagement.Reportable;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person implements Reportable, Serializable {
    private String teacherID;
    private String specialization;
    private List<Course> coursesTaught;

    public String getSpecialization() {
        return specialization;
    }

    public static int getTeacherCount() {
        return teacherCount;
    }
    private static int teacherCount = 0;

    public Teacher(String name, String email, LocalDate dateOfBirth, String specialization) {
        super(name, email, dateOfBirth);
        this.teacherID = "T" + String.format("%03d", ++teacherCount);
        this.specialization = specialization;
        this.coursesTaught = new ArrayList<>();
        University.incrementTeacherCount();
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void assignCourse(Course course) {
        coursesTaught.add(course);
        course.setAssignedTeacher(this);
        System.out.println("Teacher " + teacherID + " assigned to teach " + course.getTitle() + ".");
    }

    public void displayCourses() {
        System.out.println("Courses taught by " + name + " (" + teacherID + "):");
        for (Course course : coursesTaught) {
            System.out.println("- " + course.getTitle() + " (" + course.getCourseID() + ")");
        }
    }

    @Override
    public void displayDetails() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder courseTitles = new StringBuilder();
        for (Course course : coursesTaught) {
            courseTitles.append(course.getTitle()).append(", ");
        }
        if (courseTitles.length() > 0) {
            courseTitles.setLength(courseTitles.length() - 2);
        }
        return "Teacher: " + teacherID + ", Name: " + name + ", Specialization: " + specialization + ", Courses Taught: [" + courseTitles + "]";
    }



    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setCoursesTaught(List<Course> coursesTaught) {
        this.coursesTaught = coursesTaught;
    }

    public static void setTeacherCount(int teacherCount) {
        Teacher.teacherCount = teacherCount;
    }

    public void getTaughtStudentNames(){
        for(Course course: coursesTaught){
            for(Student stud : course.getEnrolledStudents()){
                System.out.println(stud.name);
            }
        }
    }

    @Override
    public String generateReport() {
        String baseStr = "";
        baseStr+="Teacher Report\n";
        baseStr+="ID: " + teacherID + "\n";
        baseStr += "Name: " + name + "\n";
        baseStr += "Specialization: " + specialization + "\n";
        baseStr+= "Courses Taught:\n";
        for (Course course : coursesTaught) {
            baseStr+= "- " +  course.getTitle() + " ID: " + course.getCourseID() + "\n";
        }
        return baseStr;
    }



    @Override
    public void exportToFile() {
        String report = generateReport();
        String filePath = teacherID + "_teacher_report.txt";

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(report.getBytes());
            System.out.println("Export successful!");
        } catch (IOException e) {
            System.out.println("Exporting to File Failed!");
        }
    }

    public List<Course> getCoursesTaught() {
        return coursesTaught;
    }

    public boolean isAssignedToCourse(Course course) {
        for(Course cours: University.getCourseRepository().getAll()){
            if(this.equals(cours.getAssignedTeacher())){
                return true;
            }
        }
        return false;
    }
}
