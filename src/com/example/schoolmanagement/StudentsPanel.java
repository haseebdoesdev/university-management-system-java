package com.example.schoolmanagement;

import com.example.schoolmanagement.Course;
import com.example.schoolmanagement.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StudentsPanel extends JPanel {
    private University university;
    private JButton addStudentButton;
    private JButton enrollButton;
    private JButton searchButton;
    private JButton deleteButton;
    private JButton generateReportButton; // Existing Button
    private JButton updateButton; // New Button
    private JTable studentsTable;
    private DefaultTableModel tableModel;

    public StudentsPanel(University university) {
        this.university = university;
        setLayout(new BorderLayout(10, 10));
        initializeButtonsPanel();
        initializeStudentsTable();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadStudentsIntoTable();
            }
        });
    }

    private void initializeButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addStudentButton = new JButton("Add Student");
        enrollButton = new JButton("Enroll in Course");
        searchButton = new JButton("Search Students");
        deleteButton = new JButton("Delete Student");
        generateReportButton = new JButton("Generate Report"); // Existing Button
        updateButton = new JButton("Update Student"); // New Button

        buttonsPanel.add(addStudentButton);
        buttonsPanel.add(enrollButton);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(generateReportButton);
        buttonsPanel.add(updateButton);

        // Action Listeners
        addStudentButton.addActionListener(e -> openAddStudentFrame());
        enrollButton.addActionListener(e -> openEnrollmentFrame());
        searchButton.addActionListener(e -> openSearchFrame());
        deleteButton.addActionListener(e -> openDeleteFrame());
        generateReportButton.addActionListener(e -> generateStudentReport());
        updateButton.addActionListener(e -> openUpdateFrame());

        add(buttonsPanel, BorderLayout.NORTH);
    }

    private void initializeStudentsTable() {
        String[] columns = {"Student ID", "Name", "Email", "Date of Birth", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        studentsTable = new JTable(tableModel);
        studentsTable.setFillsViewportHeight(true);
        loadStudentsIntoTable();
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Students"));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadStudentsIntoTable() {
        tableModel.setRowCount(0);
        List<Student> students = university.getStudentRepository().getAll();
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getStudentID(),
                    student.name,
                    student.email,
                    student.dateOfBirth,
                    student.getAddress()
            });
        }
    }
    private void openAddStudentFrame() {
        JFrame frame = new JFrame("Add New Student");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(5, 2, 10, 10));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Form Fields
        frame.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        frame.add(nameField);

        frame.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        frame.add(emailField);

        frame.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        JTextField dobField = new JTextField();
        frame.add(dobField);

        frame.add(new JLabel("Address:"));
        JTextField addressField = new JTextField();
        frame.add(addressField);

        JButton submitButton = new JButton("Add Student");
        frame.add(submitButton);

        // Empty label for spacing
        frame.add(new JLabel());

        // Action Listener for Submit Button
        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dobText = dobField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || dobText.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate dob;
            try {
                dob = LocalDate.parse(dobText);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = new Student(name, email, dob, address);
            university.getStudentRepository().add(student);

            // Add to table
            tableModel.addRow(new Object[]{
                    student.getStudentID(),
                    student.name,
                    student.email,
                    student.dateOfBirth,
                    student.getAddress()
            });

            // Clear form fields
            nameField.setText("");
            emailField.setText("");
            dobField.setText("");
            addressField.setText("");

            JOptionPane.showMessageDialog(frame, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openEnrollmentFrame() {
        JFrame frame = new JFrame("Enroll Student in Course");
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(4, 2, 5, 5));

        frame.add(new JLabel("Select Student:"));
        JComboBox<String> studentComboBox = new JComboBox<>();
        List<Student> students = university.getStudentRepository().getAll();
        for (Student student : students) {
            studentComboBox.addItem(student.getStudentID() + " - " + student.name);
        }
        frame.add(studentComboBox);

        frame.add(new JLabel("Select Course:"));
        JComboBox<String> courseComboBox = new JComboBox<>();
        List<Course> courses = university.getCourseRepository().getAll();
        for (Course course : courses) {
            courseComboBox.addItem(course.getCourseID() + " - " + course.getTitle());
        }
        frame.add(courseComboBox);

        frame.add(new JLabel("Grade:"));
        JTextField gradeField = new JTextField(20);
        frame.add(gradeField);

        JButton enrollBtn = new JButton("Enroll");
        enrollBtn.addActionListener(e -> {
            String selectedStudent = (String) studentComboBox.getSelectedItem();
            String selectedCourse = (String) courseComboBox.getSelectedItem();
            Double grade = null;
            try {
                grade = Double.parseDouble(gradeField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedStudent == null || selectedCourse == null) {
                JOptionPane.showMessageDialog(frame, "Please select both student and course.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String studentID = selectedStudent.split(" - ")[0];
            String courseID = selectedCourse.split(" - ")[0];

            Student student = findStudentByID(studentID);
            Course course = findCourseByID(courseID);

            if (student != null && course != null) {
                student.enrollInCourse(course, grade);
                JOptionPane.showMessageDialog(frame, "Student enrolled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Error enrolling student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(enrollBtn);
        frame.add(new JLabel(""));
        frame.setVisible(true);
    }

    private void openSearchFrame() {
        JFrame frame = new JFrame("Search Students");
        frame.setSize(600, 200);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Enter Student Name:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchBtn);
        frame.add(searchPanel, BorderLayout.NORTH);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a name to search.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String results = searchStudentsByName(query);
            resultArea.setText(results);
        });

        frame.setVisible(true);
    }

    private void openDeleteFrame() {
        JFrame frame = new JFrame("Delete a Student");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(3, 2, 5, 10));
        frame.setTitle("Delete a Student");

        frame.add(new JLabel("Delete By ID:"));
        JTextField studentIdField = new JTextField(20);
        frame.add(studentIdField);

        JButton deleteBtn = new JButton("Delete");
        frame.add(deleteBtn);

        // Empty labels for spacing
        frame.add(new JLabel());
        frame.add(new JLabel());

        deleteBtn.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Student ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean removed = deleteStudent(studentId);
            if (removed) {
                JOptionPane.showMessageDialog(frame, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudentsIntoTable();
                University.decrementStudentCount();
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public boolean deleteStudent(String studentId){
        Student studentToRemove = null;
        for(Student student : university.getStudentRepository().getAll()){
            if(student.getStudentID().equals(studentId)){
                studentToRemove = student;
                break;
            }
        }
        if(studentToRemove != null){
            university.getStudentRepository().remove(studentToRemove);

            return true;
        }
        return false;
    }

    private Student findStudentByID(String studentID) {
        for (Student student : university.getStudentRepository().getAll()) {
            if (student.getStudentID().equals(studentID)) {
                return student;
            }
        }
        return null;
    }

    public String searchStudentsByName(String studentName) {
        String baseStr = "";
        for (Student student : university.getStudentRepository().getAll()) {
            if (student.name.toLowerCase().contains(studentName.toLowerCase())) {
                baseStr += student.name + ", ";
            }
        }
        if (!baseStr.isEmpty()) {
            baseStr = baseStr.substring(0, baseStr.length() - 2);
        } else {
            baseStr = "No students found.";
        }
        return baseStr;
    }

    private Course findCourseByID(String courseID) {
        for (Course course : university.getCourseRepository().getAll()) {
            if (course.getCourseID().equals(courseID)) {
                return course;
            }
        }
        return null;
    }

    private void generateStudentReport() {
        List<Student> students = university.getStudentRepository().getAll();
        String report = "=== Student Report ===\n";
        for (Student student : students) {
            report += student.toString()
                    + "\n";
        }

        if (students.isEmpty()) {
            report ="No students to display.";
        }

        JFrame reportFrame = new JFrame("Student Report");
        reportFrame.setSize(600, 600);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setText(report);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportFrame.add(scrollPane, BorderLayout.CENTER);

        reportFrame.setVisible(true);
    }

    // New Method to Open Update Frame
    private void openUpdateFrame() {
        JFrame frame = new JFrame("Update Student Information");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(6, 2, 5, 5));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JLabel("Student ID:"));
        JTextField studentIdField = new JTextField(20);
        frame.add(studentIdField);

        frame.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(20);
        frame.add(nameField);

        frame.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(20);
        frame.add(emailField);

        frame.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        JTextField dobField = new JTextField(20);
        frame.add(dobField);

        frame.add(new JLabel("Address:"));
        JTextField addressField = new JTextField(20);
        frame.add(addressField);

        JButton updateBtn = new JButton("Update");
        frame.add(updateBtn);
        frame.add(new JLabel("")); // Empty label for spacing

        updateBtn.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dobText = dobField.getText().trim();
            String address = addressField.getText().trim();

            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter the Student ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = findStudentByID(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(frame, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update fields if they are not empty
            if (!name.isEmpty()) {
                student.name = name;
            }
            if (!email.isEmpty()) {
                student.email = email;
            }
            if (!dobText.isEmpty()) {
                try {
                    LocalDate dob = LocalDate.parse(dobText);
                    student.dateOfBirth = dob;
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (!address.isEmpty()) {
                student.setAddress(address);
            }

            loadStudentsIntoTable();
            JOptionPane.showMessageDialog(frame, "Student information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        loadStudentsIntoTable();
    }
}
