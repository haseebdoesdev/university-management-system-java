package com.example.schoolmanagement;

import com.example.schoolmanagement.Teacher;
import com.example.schoolmanagement.Course;
import com.example.schoolmanagement.Student;
import com.example.schoolmanagement.University;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TeachersPanel extends JPanel {
    private University university;
    private JTable teachersTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton generateReportButton;
    private JButton updateButton;
    private JButton exportButton;
    private JButton generateWorkloadReportButton;

    public TeachersPanel(University university) {
        this.university = university;
        setLayout(new BorderLayout(10, 10));
        initializeButtonsPanel();
        initializeTeachersTable();
    }

    private void initializeButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Teacher");
        deleteButton = new JButton("Delete Teacher");
        generateReportButton = new JButton("Generate Report");
        updateButton = new JButton("Update Teacher");
        exportButton = new JButton("Export as File");
        generateWorkloadReportButton = new JButton("Generate Workload Report");

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(generateReportButton);
        buttonsPanel.add(generateWorkloadReportButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(exportButton);

        addButton.addActionListener(e -> openAddTeacherFrame());
        deleteButton.addActionListener(e -> openDeleteFrame());
        generateReportButton.addActionListener(e -> generateTeacherReport());
        generateWorkloadReportButton.addActionListener(e -> generateTeacherWorkloadReport());
        updateButton.addActionListener(e -> openUpdateFrame());
        exportButton.addActionListener(e -> openExportFrame());

        add(buttonsPanel, BorderLayout.NORTH);
    }

    private void initializeTeachersTable() {
        String[] columns = {"Teacher ID", "Name", "Email", "Date of Birth", "Specialization"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        teachersTable = new JTable(tableModel);
        teachersTable.setFillsViewportHeight(true);
        loadTeachersIntoTable();

        JScrollPane scrollPane = new JScrollPane(teachersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Teachers"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTeachersIntoTable() {
        tableModel.setRowCount(0);
        List<Teacher> teachers = university.getTeacherRepository().getAll();
        for (Teacher teacher : teachers) {
            tableModel.addRow(new Object[] {
                    teacher.getTeacherID(),
                    teacher.name,
                    teacher.email,
                    teacher.dateOfBirth,
                    teacher.getSpecialization()
            });
        }
    }

    private void openAddTeacherFrame() {
        JFrame frame = new JFrame("Add New Teacher");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(5, 2, 10, 10));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        frame.add(nameField);

        frame.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        frame.add(emailField);

        frame.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        JTextField dobField = new JTextField();
        frame.add(dobField);

        frame.add(new JLabel("Specialization:"));
        JTextField specializationField = new JTextField();
        frame.add(specializationField);

        JButton submitButton = new JButton("Add Teacher");
        frame.add(submitButton);

        frame.add(new JLabel());

        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dobText = dobField.getText().trim();
            String specialization = specializationField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || dobText.isEmpty() || specialization.isEmpty()) {
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

            Teacher teacher = new Teacher(name, email, dob, specialization);
            university.getTeacherRepository().add(teacher);

            tableModel.addRow(new Object[] {
                    teacher.getTeacherID(),
                    teacher.name,
                    teacher.email,
                    teacher.dateOfBirth,
                    teacher.getSpecialization()
            });

            nameField.setText("");
            emailField.setText("");
            dobField.setText("");
            specializationField.setText("");

            JOptionPane.showMessageDialog(frame, "Teacher added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openDeleteFrame() {
        JFrame frame = new JFrame("Delete a Teacher");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(3, 2, 5, 10));

        frame.add(new JLabel("Delete By ID:"));
        JTextField teacherIdField = new JTextField(20);
        frame.add(teacherIdField);

        JButton deleteBtn = new JButton("Delete");
        frame.add(deleteBtn);

        frame.add(new JLabel());
        frame.add(new JLabel());

        deleteBtn.addActionListener(e -> {
            String teacherId = teacherIdField.getText().trim();
            if (teacherId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Teacher ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean removed = deleteTeacher(teacherId);
            if (removed) {
                JOptionPane.showMessageDialog(frame, "Teacher deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTeachersIntoTable();
                University.decrementTeacherCount();
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Teacher ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private boolean deleteTeacher(String teacherId) {
        Teacher teacherToRemove = findTeacherByID(teacherId);
        if (teacherToRemove != null) {
            university.getTeacherRepository().remove(teacherToRemove);
            return true;
        }
        return false;
    }

    private void generateTeacherReport() {
        List<Teacher> teachers = university.getTeacherRepository().getAll();
        String report = "=== Teacher Report ===\n";
        for (Teacher teacher : teachers) {
            report += "ID: " + teacher.getTeacherID() +
                    ", Name: " + teacher.name +
                    ", Email: " + teacher.email +
                    ", DOB: " + teacher.dateOfBirth +
                    ", Specialization: " + teacher.getSpecialization() +
                    "\n";
        }
        if (teachers.isEmpty()) {
            report = "No teachers to display.";
        }

        JFrame reportFrame = new JFrame("Teacher Report");
        reportFrame.setSize(500, 400);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setText(report);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportFrame.add(scrollPane, BorderLayout.CENTER);

        reportFrame.setVisible(true);
    }

    private void openUpdateFrame() {
        JFrame frame = new JFrame("Update Teacher Information");
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(6, 2, 5, 5));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JLabel("Teacher ID:"));
        JTextField teacherIdField = new JTextField(20);
        frame.add(teacherIdField);

        frame.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(20);
        frame.add(nameField);

        frame.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(20);
        frame.add(emailField);

        frame.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        JTextField dobField = new JTextField(20);
        frame.add(dobField);

        frame.add(new JLabel("Specialization:"));
        JTextField specializationField = new JTextField(20);
        frame.add(specializationField);

        JButton updateBtn = new JButton("Update");
        frame.add(updateBtn);
        frame.add(new JLabel());

        updateBtn.addActionListener(e -> {
            String teacherId = teacherIdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dobText = dobField.getText().trim();
            String specialization = specializationField.getText().trim();

            if (teacherId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter the Teacher ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Teacher teacher = findTeacherByID(teacherId);
            if (teacher == null) {
                JOptionPane.showMessageDialog(frame, "Teacher ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!name.isEmpty()) {
                teacher.name = name;
            }
            if (!email.isEmpty()) {
                teacher.email = email;
            }
            if (!dobText.isEmpty()) {
                try {
                    LocalDate dob = LocalDate.parse(dobText);
                    teacher.dateOfBirth = dob;
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (!specialization.isEmpty()) {
                teacher.setSpecialization(specialization);
            }

            loadTeachersIntoTable();
            JOptionPane.showMessageDialog(frame, "Teacher information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openExportFrame() {
        JFrame frame = new JFrame("Export Teacher Report");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(3, 2, 5, 10));

        frame.add(new JLabel("Teacher ID:"));
        JTextField teacherIdField = new JTextField(20);
        frame.add(teacherIdField);

        JButton exportBtn = new JButton("Export");
        frame.add(exportBtn);

        frame.add(new JLabel());
        frame.add(new JLabel());

        exportBtn.addActionListener(e -> {
            String teacherId = teacherIdField.getText().trim();
            if (teacherId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Teacher ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Teacher teacher = findTeacherByID(teacherId);
            if (teacher != null) {
                teacher.exportToFile();
                JOptionPane.showMessageDialog(frame, "Export successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Teacher ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private Teacher findTeacherByID(String teacherID) {
        for (Teacher teacher : university.getTeacherRepository().getAll()) {
            if (teacher.getTeacherID().equals(teacherID)) {
                return teacher;
            }
        }
        return null;
    }

    public void generateTeacherWorkloadReport() {
        String report = generateTeacherWorkloadReportString();

        JFrame reportFrame = new JFrame("Teacher Workload Report");
        reportFrame.setSize(500, 400);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setText(report);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportFrame.add(scrollPane, BorderLayout.CENTER);

        reportFrame.setVisible(true);
    }

    public String generateTeacherWorkloadReportString() {
        String baseStr = "";
        for (Teacher teacher : university.getTeacherRepository().getAll()) {
            baseStr += teacher.name + " (" + teacher.getTeacherID() + "):\n";
            for (Course course : teacher.getCoursesTaught()) {
                baseStr += course.getTitle() + " (" + course.getCredits() + ")\n";
            }
            baseStr += "-----------------------------------\n";
        }
        return baseStr;
    }
}
