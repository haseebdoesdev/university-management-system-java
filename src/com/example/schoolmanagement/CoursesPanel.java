package com.example.schoolmanagement;

import com.example.schoolmanagement.Course;
import com.example.schoolmanagement.Teacher;
import com.example.schoolmanagement.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Scanner;

public class CoursesPanel extends JPanel {
    private University university;
    private JTable coursesTable;
    private DefaultTableModel tableModel;

    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton filterButton;
    private JButton clearFilterButton;
    private JButton showEnrolledStudentButton;
    public CoursesPanel(University university) {
        this.university = university;
        setLayout(new BorderLayout(10, 10));

        initializeButtonsPanel();
        initializeCoursesTable();
    }

    private void initializeButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Course");
        deleteButton = new JButton("Delete Course");
        updateButton = new JButton("Update Course");
        filterButton = new JButton("Filter by Credits");
        clearFilterButton = new JButton("Clear Filter");

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(filterButton);
        buttonsPanel.add(clearFilterButton);
        add(buttonsPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> openAddCourseFrame());
        deleteButton.addActionListener(e -> openDeleteFrame());
        updateButton.addActionListener(e -> openUpdateFrame());
        filterButton.addActionListener(e -> openFilterFrame());
        clearFilterButton.addActionListener(e -> loadCoursesIntoTable());
    }

    private void initializeCoursesTable() {
        String[] columns = {"Course ID", "Title", "Credits", "Assigned Teacher", "Median Grade", "Average Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Courses"));
        add(scrollPane, BorderLayout.CENTER);

        loadCoursesIntoTable();
    }


    private Teacher findTeacherByID(String teacherID) {
        for (Teacher teacher : university.getTeacherRepository().getAll()) {
            if (teacher.getTeacherID().equals(teacherID)) {
                return teacher;
            }
        }
        return null;
    }

    public void loadCoursesIntoTable() {
        tableModel.setRowCount(0);
        List<Course> courses = university.getCourseRepository().getAll();
        for (Course course : courses) {
            Teacher assignedTeacher = findTeacherByCourse(course);
            String teacherName = (assignedTeacher != null) ? assignedTeacher.name : "Unassigned";
            tableModel.addRow(new Object[]{
                    course.getCourseID(),
                    course.getTitle(),
                    course.getCredits(),
                    teacherName,
                    course.calculateMedianGrade(),
                    course.calculateAverageGrade(),
            });
        }
    }

    private Teacher findTeacherByCourse(Course course) {
        for (Teacher teacher : university.getTeacherRepository().getAll()) {
            if (teacher.isAssignedToCourse(course)) {
                return teacher;
            }
        }
        return null;
    }

    private void openAddCourseFrame() {
        AddCourseFrame addCourseFrame = new AddCourseFrame(university, this);
        addCourseFrame.setVisible(true);
    }

    private void openFilterFrame() {
        JFrame frame = new JFrame("Filter Courses by Credits");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.add(new JLabel("Minimum Credits:"));
        JTextField minCreditsField = new JTextField(10);
        inputPanel.add(minCreditsField);
        frame.add(inputPanel, BorderLayout.NORTH);

        JButton applyFilterButton = new JButton("Apply Filter");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(applyFilterButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        applyFilterButton.addActionListener(e -> {
            String minCreditsText = minCreditsField.getText().trim();
            if (minCreditsText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a minimum credit value.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int minCredits;
            try {
                minCredits = Integer.parseInt(minCreditsText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            filterCoursesByCredits(minCredits);
            frame.dispose();
        });

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void filterCoursesByCredits(int minCredits) {
        tableModel.setRowCount(0);
        List<Course> courses = university.getCourseRepository().getAll();
        for (Course course : courses) {
            if (course.getCredits() >= minCredits) {
                Teacher assignedTeacher = findTeacherByCourse(course);
                String teacherName = (assignedTeacher != null) ? assignedTeacher.name : "Unassigned";
                tableModel.addRow(new Object[]{
                        course.getCourseID(),
                        course.getTitle(),
                        course.getCredits(),
                        teacherName
                });
            }
        }
    }

    private void openDeleteFrame() {
        JFrame frame = new JFrame("Delete a Course");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(3, 2, 5, 10));
        frame.setTitle("Delete a Course");

        frame.add(new JLabel("Delete By ID:"));
        JTextField courseIdField = new JTextField(20);
        frame.add(courseIdField);

        JButton deleteBtn = new JButton("Delete");
        frame.add(deleteBtn);

        frame.add(new JLabel());
        frame.add(new JLabel());

        deleteBtn.addActionListener(e -> {
            String courseID = courseIdField.getText().trim();
            if (courseID.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Course ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean removed = deleteCourse(courseID);
            if (removed) {
                JOptionPane.showMessageDialog(frame, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCoursesIntoTable();
                University.decrementCourseCount();
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public boolean deleteCourse(String courseId) {
        Course courseToRemove = null;
        for (Course course : university.getCourseRepository().getAll()) {
            if (course.getCourseID().equals(courseId)) {
                courseToRemove = course;
                break;
            }
        }
        if (courseToRemove != null) {
            university.getCourseRepository().remove(courseToRemove);
            for (Student student : university.getStudentRepository().getAll()) {
                if (student.getEnrolledCourses().contains(courseToRemove)) {
                    student.getEnrolledCourses().remove(courseToRemove);
                }
            }
            for (Teacher teacher : university.getTeacherRepository().getAll()) {
                if (teacher.getCoursesTaught().contains(courseToRemove)) {
                    teacher.getCoursesTaught().remove(courseToRemove);
                }
            }
            return true;
        }
        return false;
    }

    private void openUpdateFrame() {
        JFrame frame = new JFrame("Update Course Information");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(5, 2, 5, 5));
        frame.setTitle("Update Course Information");

        frame.add(new JLabel("Course ID:"));
        JTextField courseIdField = new JTextField(20);
        frame.add(courseIdField);

        frame.add(new JLabel("Title:"));
        JTextField titleField = new JTextField(20);
        frame.add(titleField);

        frame.add(new JLabel("Credits:"));
        JTextField creditsField = new JTextField(20);
        frame.add(creditsField);

        frame.add(new JLabel("Assign Teacher:"));
        JComboBox<String> teacherComboBox = new JComboBox<>();
        populateTeacherComboBoxForUpdate(teacherComboBox);
        frame.add(teacherComboBox);

        JButton updateBtn = new JButton("Update");
        frame.add(updateBtn);
        frame.add(new JLabel());

        updateBtn.addActionListener(e -> {
            String courseId = courseIdField.getText().trim();
            String title = titleField.getText().trim();
            String creditsText = creditsField.getText().trim();
            String selectedTeacher = (String) teacherComboBox.getSelectedItem();

            if (courseId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter the Course ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course course = findCourseByID(courseId);
            if (course == null) {
                JOptionPane.showMessageDialog(frame, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!title.isEmpty()) {
                course.setTitle(title);
            }

            if (!creditsText.isEmpty()) {
                try {
                    int credits = Integer.parseInt(creditsText);
                    course.setCredits(credits);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Credits must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (selectedTeacher != null && !selectedTeacher.equals("Unassigned")) {
                String newTeacherID = selectedTeacher.split(" - ")[0];
                Teacher newTeacher = findTeacherByID(newTeacherID);

                Teacher currentTeacher = findTeacherByCourse(course);
                if (currentTeacher != null && !currentTeacher.getTeacherID().equals(newTeacherID)) {
                    currentTeacher.getCoursesTaught().remove(course);
                }

                if (newTeacher != null) {
                    newTeacher.assignCourse(course);
                }
            } else if (selectedTeacher != null && selectedTeacher.equals("Unassigned")) {
                Teacher currentTeacher = findTeacherByCourse(course);
                if (currentTeacher != null) {
                    currentTeacher.getCoursesTaught().remove(course);
                }
            }

            loadCoursesIntoTable();
            JOptionPane.showMessageDialog(frame, "Course information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void populateTeacherComboBoxForUpdate(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem("Unassigned");
        List<Teacher> teachers = university.getTeacherRepository().getAll();
        for (Teacher teacher : teachers) {
            comboBox.addItem(teacher.getTeacherID() + " - " + teacher.name);
        }
    }

    private Course findCourseByID(String courseID) {
        for (Course course : university.getCourseRepository().getAll()) {
            if (course.getCourseID().equals(courseID)) {
                return course;
            }
        }
        return null;
    }

    private class AddCourseFrame extends JFrame {
        private JTextField titleField;
        private JTextField creditsField;
        private JComboBox<String> teacherComboBox;
        private JButton addCourseButton;

        public AddCourseFrame(University university, CoursesPanel coursesPanel) {
            setTitle("Add New Course");
            setSize(400, 250);
            setLocationRelativeTo(coursesPanel);
            setLayout(new BorderLayout(10, 10));
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            initializeForm();
        }

        private void initializeForm() {
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createTitledBorder("Add New Course"));

            formPanel.add(new JLabel("Course Title:"));
            titleField = new JTextField();
            formPanel.add(titleField);

            formPanel.add(new JLabel("Credits:"));
            creditsField = new JTextField();
            formPanel.add(creditsField);

            formPanel.add(new JLabel("Assign Teacher:"));
            teacherComboBox = new JComboBox<>();
            populateTeacherComboBoxForUpdate(teacherComboBox);
            formPanel.add(teacherComboBox);

            add(formPanel, BorderLayout.CENTER);

            addCourseButton = new JButton("Add Course");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            buttonPanel.add(addCourseButton);
            add(buttonPanel, BorderLayout.SOUTH);

            addCourseButton.addActionListener(e -> addCourse());
        }

        private void addCourse() {
            String title = titleField.getText().trim();
            String creditsText = creditsField.getText().trim();
            String selectedTeacher = (String) teacherComboBox.getSelectedItem();

            if (title.isEmpty() || creditsText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Credits must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course course = new Course(title, credits);
            university.getCourseRepository().add(course);

            if (selectedTeacher != null && !selectedTeacher.equals("Unassigned")) {
                String teacherID = selectedTeacher.split(" - ")[0];
                Teacher teacher = findTeacherByID(teacherID);
                if (teacher != null) {
                    teacher.assignCourse(course);
                    tableModel.addRow(new Object[]{course.getCourseID(), course.getTitle(), course.getCredits(), teacher.name});
                } else {
                    tableModel.addRow(new Object[]{course.getCourseID(), course.getTitle(), course.getCredits(), "Unassigned"});
                }
            } else {
                tableModel.addRow(new Object[]{course.getCourseID(), course.getTitle(), course.getCredits(), "Unassigned"});
            }

            titleField.setText("");
            creditsField.setText("");
            teacherComboBox.setSelectedIndex(0);

            JOptionPane.showMessageDialog(this, "Course added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}
