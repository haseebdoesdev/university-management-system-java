package com.example.schoolmanagement;// MainFrame.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Mainframe extends JFrame {
    private University university;
    private StudentsPanel studentsPanel;
    public JPanel teachersPanel;
    public CoursesPanel coursesPanel;
    private JPanel adminPanel;

    public Mainframe() {

        university = new University();
        studentsPanel = new StudentsPanel(university);
        teachersPanel = new TeachersPanel(university);
        coursesPanel = new CoursesPanel(university);
        adminPanel = new AdminPanel(university);
        setTitle("School Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        JMenuBar menuBar = new JMenuBar();
        JMenu manageMenu = new JMenu("Manage");
        JMenuItem studentsItem = new JMenuItem("Students");
        JMenuItem teachersItem = new JMenuItem("Teachers");
        JMenuItem coursesItem = new JMenuItem("Courses");
        JMenuItem adminItem = new JMenuItem("Administrative Staff");

        manageMenu.add(studentsItem);
        manageMenu.add(teachersItem);
        manageMenu.add(coursesItem);
        manageMenu.add(adminItem);
        JMenu dataMenu = new JMenu("Data");
        JMenuItem loadDataItem = new JMenuItem("Load Data");
        JMenuItem saveDataItem = new JMenuItem("Save Data");
        dataMenu.add(loadDataItem);
        dataMenu.add(saveDataItem);
        JMenu statsMenu = new JMenu("Stats");
        JMenuItem systemStatsItem = new JMenuItem("System Statistics");
        statsMenu.add(systemStatsItem);
        menuBar.add(manageMenu);
        menuBar.add(dataMenu);
        menuBar.add(statsMenu);

        setJMenuBar(menuBar);

        // Action Listeners for Menu Items
        studentsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(studentsPanel);
                revalidate();
            }
        });

        teachersItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(teachersPanel);
                revalidate();

            }
        });

        coursesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(coursesPanel);
//                coursesPanel.populateTeacherComboBox();
                coursesPanel.loadCoursesIntoTable();
                revalidate();

            }
        });

        adminItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(adminPanel);
                revalidate();

            }
        });

        loadDataItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                university.loadData("university_data.ser");
                studentsPanel.loadStudentsIntoTable();
                JOptionPane.showMessageDialog(Mainframe.this, "Data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        saveDataItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                university.saveData("university_data.ser");
                JOptionPane.showMessageDialog(Mainframe.this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);


            }
        });
        systemStatsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame uniStatsFrame = new JFrame();
                uniStatsFrame.setLayout(new BorderLayout());
                JTextArea uniStats = new JTextArea(University.giveSystemStats());
                uniStats.setEditable(false);
                uniStatsFrame.add(uniStats);
                uniStatsFrame.setTitle("University Stats");
                uniStatsFrame.setSize(400, 400);
                uniStatsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                uniStatsFrame.setVisible(true);

            }
        });
        setContentPane(studentsPanel);
    }

    public void getStudents(String id) {
        List<Course> courses = University.getCourseRepository().getAll();
        for (Course course : courses) {
            if (course.getCourseID() == id) {
                System.out.println(course.toString());
            }
        }
    }
    public static void main(String[] args) {
        Mainframe mainframe = new Mainframe();
        mainframe.setVisible(true);
        mainframe.setDefaultCloseOperation(mainframe.EXIT_ON_CLOSE);



        String id = "COO3";

    }
}
