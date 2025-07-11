package com.example.schoolmanagement; // AdminPanel.java

import com.example.schoolmanagement.AdministrativeStaff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminPanel extends JPanel {
    private University university;
    private JTable staffTable;
    private DefaultTableModel tableModel;

    // Buttons
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton generateReportButton;
    private JButton exportButton; // New Button

    public AdminPanel(University university) {
        this.university = university;
        setLayout(new BorderLayout(10, 10));

        initializeButtonsPanel();
        initializeStaffTable();
    }

    private void initializeButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Staff");
        deleteButton = new JButton("Delete Staff");
        updateButton = new JButton("Update Staff");
        generateReportButton = new JButton("Generate Report");
        exportButton = new JButton("Export as File");

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(generateReportButton);
        buttonsPanel.add(exportButton);
        addButton.addActionListener(e -> openAddStaffFrame());
        deleteButton.addActionListener(e -> openDeleteFrame());
        updateButton.addActionListener(e -> openUpdateFrame());
        generateReportButton.addActionListener(e -> generateStaffReport());
        exportButton.addActionListener(e -> openExportFrame());

        add(buttonsPanel, BorderLayout.NORTH);
    }

    private void initializeStaffTable() {
        String[] columns = {"Staff ID", "Name", "Email", "Date of Birth", "Role", "Department"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        staffTable = new JTable(tableModel);
        staffTable.setFillsViewportHeight(true);
        loadStaffIntoTable();

        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Administrative Staff"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStaffIntoTable() {
        tableModel.setRowCount(0);
        List<AdministrativeStaff> staffList = university.getAdminStaffRepository().getAll();
        for (AdministrativeStaff staff : staffList) {
            tableModel.addRow(new Object[]{
                    staff.getStaffID(),
                    staff.name,
                    staff.email,
                    staff.dateOfBirth,
                    staff.getRole(),
                    staff.getDepartment()
            });
        }
    }
    private void openAddStaffFrame() {
        JFrame frame = new JFrame("Add Administrative Staff");
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(6, 2, 10, 10));
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

        frame.add(new JLabel("Role:"));
        JTextField roleField = new JTextField();
        frame.add(roleField);

        frame.add(new JLabel("Department:"));
        JTextField departmentField = new JTextField();
        frame.add(departmentField);

        JButton submitButton = new JButton("Add Staff");
        frame.add(submitButton);
        frame.add(new JLabel());
        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dobText = dobField.getText().trim();
            String role = roleField.getText().trim();
            String department = departmentField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || dobText.isEmpty() || role.isEmpty() || department.isEmpty()) {
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

            AdministrativeStaff staff = new AdministrativeStaff(name, email, dob, role, department);
            university.getAdminStaffRepository().add(staff); // Correctly add to admin repository

            // Add to table
            tableModel.addRow(new Object[]{staff.getStaffID(), staff.name, staff.email, staff.dateOfBirth, staff.getRole(), staff.getDepartment()});

            // Clear form fields
            nameField.setText("");
            emailField.setText("");
            dobField.setText("");
            roleField.setText("");
            departmentField.setText("");

            JOptionPane.showMessageDialog(frame, "Administrative Staff added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openDeleteFrame() {
        JFrame frame = new JFrame("Delete Administrative Staff");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(3, 2, 5, 10));
        frame.setTitle("Delete Administrative Staff");

        frame.add(new JLabel("Delete By ID:"));
        JTextField staffIdField = new JTextField(20);
        frame.add(staffIdField);

        JButton deleteBtn = new JButton("Delete");
        frame.add(deleteBtn);

        // Empty labels for spacing
        frame.add(new JLabel());
        frame.add(new JLabel());

        deleteBtn.addActionListener(e -> {
            String staffID = staffIdField.getText().trim();
            if (staffID.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Staff ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean removed = deleteStaff(staffID);
            if (removed) {
                JOptionPane.showMessageDialog(frame, "Administrative Staff deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStaffIntoTable();
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Staff ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private boolean deleteStaff(String staffID) {
        AdministrativeStaff staffToRemove = findStaffByID(staffID);
        if (staffToRemove != null) {
            university.getAdminStaffRepository().remove(staffToRemove);
            return true;
        }
        return false;
    }

    private void openUpdateFrame() {
        JFrame frame = new JFrame("Update Administrative Staff Information");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(7, 2, 10, 10));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JLabel("Staff ID:"));
        JTextField staffIdField = new JTextField(20);
        frame.add(staffIdField);

        frame.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(20);
        frame.add(nameField);

        frame.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(20);
        frame.add(emailField);

        frame.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        JTextField dobField = new JTextField(20);
        frame.add(dobField);

        frame.add(new JLabel("Role:"));
        JTextField roleField = new JTextField(20);
        frame.add(roleField);

        frame.add(new JLabel("Department:"));
        JTextField departmentField = new JTextField(20);
        frame.add(departmentField);

        JButton updateBtn = new JButton("Update");
        frame.add(updateBtn);
        frame.add(new JLabel(""));

        updateBtn.addActionListener(e -> {
            String staffID = staffIdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dobText = dobField.getText().trim();
            String role = roleField.getText().trim();
            String department = departmentField.getText().trim();

            if (staffID.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter the Staff ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AdministrativeStaff staff = findStaffByID(staffID);
            if (staff == null) {
                JOptionPane.showMessageDialog(frame, "Staff ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!name.isEmpty()) {
                staff.name = name;
            }
            if (!email.isEmpty()) {
                staff.email = email;
            }
            if (!dobText.isEmpty()) {
                try {
                    LocalDate dob = LocalDate.parse(dobText);
                    staff.dateOfBirth = dob;
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (!role.isEmpty()) {
                staff.setRole(role);
            }
            if (!department.isEmpty()) {
                staff.setDepartment(department);
            }

            loadStaffIntoTable();
            JOptionPane.showMessageDialog(frame, "Administrative Staff information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private AdministrativeStaff findStaffByID(String staffID) {
        for (AdministrativeStaff staff : university.getAdminStaffRepository().getAll()) {
            if (staff.getStaffID().equals(staffID)) {
                return staff;
            }
        }
        return null;
    }

    private void generateStaffReport() {
        List<AdministrativeStaff> staffList = university.getAdminStaffRepository().getAll();
        StringBuilder report = new StringBuilder("=== Administrative Staff Report ===\n");
        for (AdministrativeStaff staff : staffList) {
            report.append("ID: ").append(staff.getStaffID())
                    .append(", Name: ").append(staff.name)
                    .append(", Email: ").append(staff.email)
                    .append(", DOB: ").append(staff.dateOfBirth)
                    .append(", Role: ").append(staff.getRole())
                    .append(", Department: ").append(staff.getDepartment())
                    .append("\n");
        }
        if (staffList.isEmpty()) {
            report.append("No administrative staff to display.");
        }

        JFrame reportFrame = new JFrame("Administrative Staff Report");
        reportFrame.setSize(600, 500);
        reportFrame.setLocationRelativeTo(this);
        reportFrame.setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setText(report.toString());
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportFrame.add(scrollPane, BorderLayout.CENTER);

        reportFrame.setVisible(true);
    }

    private void openExportFrame() {
        JFrame frame = new JFrame("Export Staff Report");
        frame.setSize(300, 150);
        frame.setLocationRelativeTo(this);
        frame.setLayout(new GridLayout(3, 2, 5, 10));

        frame.add(new JLabel("Staff ID:"));
        JTextField staffIdField = new JTextField(20);
        frame.add(staffIdField);

        JButton exportBtn = new JButton("Export");
        frame.add(exportBtn);

        frame.add(new JLabel());
        frame.add(new JLabel());

        exportBtn.addActionListener(e -> {
            String staffID = staffIdField.getText().trim();
            if (staffID.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Staff ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AdministrativeStaff staff = findStaffByID(staffID);
            if (staff != null) {
                staff.exportToFile();
                JOptionPane.showMessageDialog(frame, "Export successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Staff ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        loadStaffIntoTable();
    }
}
