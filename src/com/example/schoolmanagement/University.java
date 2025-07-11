package com.example.schoolmanagement;// com.example.schoolmanagement.University.java
import com.example.schoolmanagement.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class University {
    private Repository<Student> studentRepository;
    private Repository<Teacher> teacherRepository;
    private static Repository<Course> courseRepository;
    private Repository<AdministrativeStaff> adminStaffRepository;

    private static int totalStudents = 0;
    private static int totalTeachers = 0;
    private static int totalCourses = 0;

    public University() {
        studentRepository = new Repository<>();
        teacherRepository = new Repository<>();
        courseRepository = new Repository<>();
        adminStaffRepository = new Repository<>();
    }

    public Repository<Student> getStudentRepository() {
        return studentRepository;
    }

    public Repository<Teacher> getTeacherRepository() {
        return teacherRepository;
    }

    public static Repository<Course> getCourseRepository() {
        return courseRepository;
    }

    public static void incrementStudentCount() {
        totalStudents++;
    }

    public static void incrementTeacherCount() {
        totalTeachers++;
    }

    public static void incrementCourseCount() {
        totalCourses++;
    }

    public static void decrementStudentCount() {
        totalStudents--;
    }

    public static void decrementTeacherCount() {
        totalTeachers--;
    }

    public static void decrementCourseCount() {
        totalCourses--;
    }

    public Repository<AdministrativeStaff> getAdminStaffRepository() {
        return adminStaffRepository;
    }
    public static void incrementStaffCount() {
    }

    public static String giveSystemStats() {
        return "System Statistics:\n"+ "Total Students: " + totalStudents + "\n" +"Total Teachers: " + totalTeachers + "\n" + "Total Courses: " + totalCourses ;
    }
    public void loadData(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            studentRepository = (Repository<Student>) ois.readObject();
            teacherRepository = (Repository<Teacher>) ois.readObject();
            courseRepository = (Repository<Course>) ois.readObject();
            adminStaffRepository = (Repository<AdministrativeStaff>) ois.readObject();
            Student.setStudentCount( studentRepository.getAll().size());
            AdministrativeStaff.setStaffCount(adminStaffRepository.getAll().size());
            Course.setCourseCount(courseRepository.getAll().size());
            Teacher.setTeacherCount(teacherRepository.getAll().size());
            University.totalStudents = studentRepository.getAll().size();
            University.totalCourses = courseRepository.getAll().size();
            University.totalTeachers = teacherRepository.getAll().size();
            System.out.println("Data loaded successfully from " + filename + ".");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data from " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveData(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(studentRepository);
            oos.writeObject(teacherRepository);
            oos.writeObject(courseRepository);
            oos.writeObject(adminStaffRepository);
            System.out.println("Data saved successfully to " + filename + ".");
        } catch (IOException e) {
            System.out.println("Error saving data to " + filename + ": " + e.getMessage());
        }
    }

}
