package com.sanchit.timetable;

// Timetable Builder GUI - Complete Solution

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Main {
    public static void main(String[] args) {
        new RoleSelector();
    }
}

class RoleSelector extends JFrame {
    public RoleSelector() {
        setTitle("Select Role to Login");
        setSize(300, 200);
        setLayout(new GridLayout(4, 1, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton adminBtn = new JButton("Admin");
        JButton instructorBtn = new JButton("Instructor");
        JButton studentBtn = new JButton("Student");

        adminBtn.addActionListener(e -> new LoginWindow("Admin"));
        instructorBtn.addActionListener(e -> new LoginWindow("Instructor"));
        studentBtn.addActionListener(e -> new LoginWindow("Student"));

        add(new JLabel("Choose Role:", SwingConstants.CENTER));
        add(adminBtn);
        add(instructorBtn);
        add(studentBtn);
        setVisible(true);
    }
}

class LoginWindow extends JFrame {
    public LoginWindow(String role) {
        setTitle(role + " Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            dispose();
            switch (role) {
                case "Admin" -> new AdminDashboard();
                case "Instructor" -> new InstructorDashboard();
                case "Student" -> new StudentDashboard();
                default -> new Dashboard(role);
            }
        });

        add(emailLabel);
        add(emailField);
        add(passLabel);
        add(passField);
        add(new JLabel());
        add(loginButton);
        setVisible(true);
    }
}

class Dashboard extends JFrame {
    public Dashboard(String role) {
        setTitle(role + " Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to " + role + " Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        add(welcomeLabel, BorderLayout.CENTER);
        setVisible(true);
    }
}

class Course {
    String courseCode, courseName, lectureSection, labSection;
    boolean hasLab;
    int lectureCount;
    // Add timing-related fields
    String lectureDays = "MWF"; // Default days pattern
    String lectureTime = "10:00 - 11:00"; // Default time
    String labDays = "T"; // Default lab day
    String labTime = "2:00 - 4:00"; // Default lab time
    String instructorName = "Dr. Instructor"; // Default instructor name

    // Original constructor
    public Course(String code, String name, String lec, String lab, boolean hasLab, int lectures) {
        this.courseCode = code;
        this.courseName = name;
        this.lectureSection = lec;
        this.labSection = lab;
        this.hasLab = hasLab;
        this.lectureCount = lectures;
    }

    // Constructor with timing parameters and instructor (3-parameter version)
    public Course(String code, String name, String lec, String lab, boolean hasLab, int lectures,
            String lecDays, String lecTime, String instructor) {
        this(code, name, lec, lab, hasLab, lectures);
        this.lectureDays = lecDays;
        this.lectureTime = lecTime;
        this.instructorName = instructor;
    }

    // Full constructor with all parameters
    public Course(String code, String name, String lec, String lab, boolean hasLab, int lectures,
            String lecDays, String lecTime, String labDays, String labTime, String instructor) {
        this(code, name, lec, lab, hasLab, lectures);
        this.lectureDays = lecDays;
        this.lectureTime = lecTime;
        this.labDays = labDays;
        this.labTime = labTime;
        this.instructorName = instructor;
    }

    // Method to check for scheduling conflicts
    public boolean hasTimeConflict(Course other) {
        // Check lecture conflicts - if days overlap and time is the same
        for (char day : this.lectureDays.toCharArray()) {
            if (other.lectureDays.indexOf(day) >= 0 && this.lectureTime.equals(other.lectureTime)) {
                return true;
            }
        }

        // Check lab conflicts if both courses have labs
        if (this.hasLab && other.hasLab) {
            for (char day : this.labDays.toCharArray()) {
                if (other.labDays.indexOf(day) >= 0 && this.labTime.equals(other.labTime)) {
                    return true;
                }
            }
        }

        return false;
    }
}

class CourseRepository {
    // Static list to store all available courses in the system
    public static List<Course> allCourses = new ArrayList<>();
    // List of listeners to notify when courses change
    private static List<CourseRepositoryListener> listeners = new ArrayList<>();

    // Method to add a course to the repository
    public static void addCourse(Course course) {
        allCourses.add(course);
        // Notify all listeners that a course was added
        notifyListeners();
    }

    // Add a batch of courses
    public static void addCourses(List<Course> courses) {
        allCourses.addAll(courses);
        notifyListeners();
    }

    // Add a listener to be notified of changes
    public static void addListener(CourseRepositoryListener listener) {
        listeners.add(listener);
    }

    // Notify all listeners of changes
    private static void notifyListeners() {
        for (CourseRepositoryListener listener : listeners) {
            listener.coursesUpdated();
        }
    }

    // Interface for listeners
    public interface CourseRepositoryListener {
        void coursesUpdated();
    }

    // Check if a course with given code already exists
    public static boolean courseExists(String courseCode) {
        for (Course c : allCourses) {
            if (c.courseCode.equals(courseCode)) {
                return true;
            }
        }
        return false;
    }
}

class StudentDashboard extends JFrame {
    public static void addCourse(Course course) {
        myCourses.add(course);
    }

    public static List<Course> myCourses = new ArrayList<>(List.of(
            new Course("CS201", "Data Structures", "L5", "LB5", true, 3),
            new Course("CS301", "Operating Systems", "L6", "", false, 2)));

    public StudentDashboard() {
        setTitle("Student Dashboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(HeaderUtil.createHeader(this, "Student"), BorderLayout.NORTH);

        JPanel statsPanel = getStatsPanel();
        add(statsPanel, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("My Courses", getMyCoursesPanel());
        tabs.add("Other Portals", getOtherPortalsPanel());

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel getStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        int courseCount = myCourses.size();
        int totalLectures = myCourses.stream().mapToInt(c -> c.lectureCount).sum();
        int totalLabs = (int) myCourses.stream().filter(c -> c.hasLab).count();

        panel.add(new JLabel("Courses Enrolled: " + courseCount, SwingConstants.CENTER));
        panel.add(new JLabel("Lectures/Week: " + totalLectures, SwingConstants.CENTER));
        panel.add(new JLabel("Labs/Week: " + totalLabs, SwingConstants.CENTER));

        return panel;
    }

    private JScrollPane getMyCoursesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        for (Course c : myCourses) {
            JButton btn = new JButton(c.courseCode + ": " + c.courseName);
            btn.addActionListener(e -> new ViewCoursePage(c, "Student"));
            panel.add(btn);
        }
        return new JScrollPane(panel);
    }

    private JPanel getOtherPortalsPanel() {
        JPanel panel = new JPanel();

        JButton timetableBtn = new JButton("View Timetable");
        timetableBtn.addActionListener(e -> new StudentTimetablePage(myCourses));

        JButton addCourseBtn = new JButton("Add Course");
        addCourseBtn.addActionListener(e -> new StudentAddCoursePage());

        panel.add(timetableBtn);
        panel.add(addCourseBtn);
        return panel;
    }
}

class StudentAddCoursePage extends JFrame {
    public StudentAddCoursePage() {
        setTitle("Add Course");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Course selection panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Available Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create table model for courses
        String[] columnNames = { "Course Code", "Course Name", "Instructor", "Schedule", "Section" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Populate table with courses from repository
        for (Course course : CourseRepository.allCourses) {
            tableModel.addRow(new Object[] {
                    course.courseCode,
                    course.courseName,
                    course.instructorName,
                    formatDayPattern(course.lectureDays) + " " + course.lectureTime,
                    course.lectureSection
            });
        }

        JTable courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setRowHeight(25);

        // Set column widths
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(80); // Code
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Instructor
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Schedule
        courseTable.getColumnModel().getColumn(4).setPreferredWidth(60); // Section

        JScrollPane scrollPane = new JScrollPane(courseTable);

        // Course details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));

        JTextArea detailsArea = new JTextArea(8, 30);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        // Update details when a course is selected
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Course selectedCourse = CourseRepository.allCourses.get(selectedRow);
                    detailsArea.setText(
                            "Course: " + selectedCourse.courseCode + " - " + selectedCourse.courseName + "\n" +
                                    "Instructor: " + selectedCourse.instructorName + "\n" +
                                    "Schedule: " + formatDayPattern(selectedCourse.lectureDays) + " "
                                    + selectedCourse.lectureTime + "\n" +
                                    "Section: " + selectedCourse.lectureSection + "\n" +
                                    "Lab: " + (selectedCourse.hasLab ? "Yes (" + selectedCourse.labSection + ")" : "No")
                                    + "\n" +
                                    "Lectures per week: " + selectedCourse.lectureCount);
                }
            }
        });

        detailsPanel.add(new JScrollPane(detailsArea));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Course");
        JButton cancelButton = new JButton("Cancel");

        // Add button action
        addButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow >= 0) {
                Course selectedCourse = CourseRepository.allCourses.get(selectedRow);

                // Check if already enrolled
                boolean alreadyEnrolled = false;
                for (Course c : StudentDashboard.myCourses) {
                    if (c.courseCode.equals(selectedCourse.courseCode)) {
                        alreadyEnrolled = true;
                        break;
                    }
                }

                if (alreadyEnrolled) {
                    JOptionPane.showMessageDialog(this,
                            "You are already enrolled in this course.",
                            "Already Enrolled", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Check for scheduling conflicts
                    boolean hasConflict = false;
                    Course conflictingCourse = null;

                    for (Course existingCourse : StudentDashboard.myCourses) {
                        if (selectedCourse.hasTimeConflict(existingCourse)) {
                            hasConflict = true;
                            conflictingCourse = existingCourse;
                            break;
                        }
                    }

                    if (hasConflict) {
                        JOptionPane.showMessageDialog(this,
                                "Cannot add course due to scheduling conflict with: " +
                                        conflictingCourse.courseCode + " - " + conflictingCourse.courseName + "\n" +
                                        "Conflicting schedule: " + formatDayPattern(conflictingCourse.lectureDays) + " "
                                        +
                                        conflictingCourse.lectureTime,
                                "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Add to student's courses
                        StudentDashboard.addCourse(selectedCourse);
                        JOptionPane.showMessageDialog(this,
                                "Successfully enrolled in " + selectedCourse.courseCode,
                                "Enrollment Successful", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new StudentDashboard();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a course first.",
                        "No Course Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> {
            dispose();
            new StudentDashboard();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Assemble main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(detailsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper method to format day pattern for display
    private String formatDayPattern(String dayPattern) {
        StringBuilder formatted = new StringBuilder();
        for (char day : dayPattern.toCharArray()) {
            switch (day) {
                case 'M':
                    formatted.append("Mon");
                    break;
                case 'T':
                    if (dayPattern.contains("Th") && dayPattern.indexOf("Th") == dayPattern.indexOf("T")) {
                        // This T is part of Th, skip it
                        continue;
                    }
                    formatted.append("Tue");
                    break;
                case 'W':
                    formatted.append("Wed");
                    break;
                case 'h':
                    if (dayPattern.contains("Th") && dayPattern.indexOf("h") == dayPattern.indexOf("T") + 1) {
                        // This is the h in Th, already handled with T
                        continue;
                    }
                    break;
                case 'F':
                    formatted.append("Fri");
                    break;
            }
            formatted.append("/");
        }
        // Remove trailing slash
        if (formatted.length() > 0) {
            formatted.deleteCharAt(formatted.length() - 1);
        }
        return formatted.toString();
    }
}

class ViewCoursePage extends JFrame {
    public ViewCoursePage(Course course, String source) {
        setTitle("Course Details - " + course.courseCode);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(HeaderUtil.createHeader(this, source), BorderLayout.NORTH);

        JTextArea info = new JTextArea(
                "Course Code: " + course.courseCode + "\n" +
                        "Course Name: " + course.courseName + "\n" +
                        "Lecture Section: " + course.lectureSection + "\n" +
                        "Lab Section: " + (course.hasLab ? course.labSection : "None") + "\n" +
                        "Instructor: " + course.instructorName + "\n" +
                        "Credits: 3\n" +
                        "Lectures/Week: " + course.lectureCount + "\n" +
                        "Schedule: " + course.lectureDays + " " + course.lectureTime + "\n" +
                        "Venue: Room 101");
        info.setEditable(false);
        add(info, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            if (source.equals("Student"))
                new StudentDashboard();
            else if (source.equals("Instructor"))
                new InstructorDashboard();
            else
                new AdminDashboard();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}

class HeaderUtil {
    public static JPanel createHeader(Component parent, String userType) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(64, 224, 208));

        String currentTime = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date());
        JLabel dateLabel = new JLabel(currentTime);
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);

        JLabel dashboardLink = new JLabel("Dashboard");
        dashboardLink.setForeground(Color.BLUE);
        dashboardLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dashboardLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                ((Window) parent).dispose();
                switch (userType) {
                    case "Student" -> new StudentDashboard();
                    case "Instructor" -> new InstructorDashboard();
                    default -> new AdminDashboard();
                }
            }
        });

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(Color.RED);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            ((Window) parent).dispose();
            new RoleSelector();
        });

        rightPanel.add(dashboardLink);

        if (userType.equals("Student") || userType.equals("Instructor")) {
            JLabel timetableLink = new JLabel(" | Timetable");
            timetableLink.setForeground(Color.BLUE);
            timetableLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            timetableLink.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    ((Window) parent).dispose();
                    if (userType.equals("Student"))
                        new StudentTimetablePage(StudentDashboard.myCourses);
                    else
                        new InstructorTimetablePage(InstructorDashboard.myCourses);
                }
            });
            rightPanel.add(timetableLink);
        }

        rightPanel.add(logoutBtn);
        header.add(dateLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }
}

class StudentTimetablePage extends JFrame {
    private List<Course> courses;

    public StudentTimetablePage(List<Course> courses) {
        this.courses = courses;
        setTitle("Student Timetable");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(HeaderUtil.createHeader(this, "Student"), BorderLayout.NORTH);

        // Create timetable
        String[] columnNames = { "Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        String[] timeSlots = {
                "8:00 - 9:00", "9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 1:00", "1:00 - 2:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"
        };

        Object[][] data = new Object[timeSlots.length][6];
        for (int i = 0; i < timeSlots.length; i++) {
            data[i][0] = timeSlots[i];
            for (int j = 1; j < 6; j++) {
                data[i][j] = "";
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable timetableTable = new JTable(model);
        timetableTable.setRowHeight(60);
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        for (int i = 1; i < 6; i++) {
            timetableTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }

        // Fill the timetable with course info
        for (Course course : courses) {
            // Find the time slot row
            int rowIndex = -1;
            for (int i = 0; i < timeSlots.length; i++) {
                if (timeSlots[i].equals(course.lectureTime)) {
                    rowIndex = i;
                    break;
                }
            }

            if (rowIndex != -1) {
                // For each day in the pattern, add the course
                for (char day : course.lectureDays.toCharArray()) {
                    int colIndex;
                    switch (day) {
                        case 'M':
                            colIndex = 1;
                            break; // Monday
                        case 'T':
                            // Check if this T is part of Th
                            if (course.lectureDays.contains("Th") &&
                                    course.lectureDays.indexOf("T") == course.lectureDays.indexOf("Th")) {
                                colIndex = 4; // Thursday
                            } else {
                                colIndex = 2; // Tuesday
                            }
                            break;
                        case 'W':
                            colIndex = 3;
                            break; // Wednesday
                        case 'F':
                            colIndex = 5;
                            break; // Friday
                        default:
                            continue; // Skip unknown days
                    }

                    // Add course to cell
                    String cellContent = course.courseCode + "\n" + course.courseName;
                    model.setValueAt(cellContent, rowIndex, colIndex);
                }
            }

            // If has lab, add lab sessions too
            if (course.hasLab) {
                // Find lab time slot
                int labRowIndex = -1;
                for (int i = 0; i < timeSlots.length; i++) {
                    if (timeSlots[i].equals(course.labTime)) {
                        labRowIndex = i;
                        break;
                    }
                }

                if (labRowIndex != -1) {
                    // For each lab day, add to timetable
                    for (char day : course.labDays.toCharArray()) {
                        int colIndex;
                        switch (day) {
                            case 'M':
                                colIndex = 1;
                                break; // Monday
                            case 'T':
                                colIndex = 2;
                                break; // Tuesday
                            case 'W':
                                colIndex = 3;
                                break; // Wednesday
                            case 'h':
                                continue; // Part of Th, skip
                            case 'F':
                                colIndex = 5;
                                break; // Friday
                            default:
                                continue;
                        }

                        // Add lab to cell
                        String cellContent = course.courseCode + " (Lab)\n" + course.labSection;
                        model.setValueAt(cellContent, labRowIndex, colIndex);
                    }
                }
            }
        }

        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            dispose();
            new StudentDashboard();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

class InstructorTimetablePage extends JFrame {
    private List<Course> courses;

    public InstructorTimetablePage(List<Course> courses) {
        this.courses = courses;
        setTitle("Instructor Timetable");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(HeaderUtil.createHeader(this, "Instructor"), BorderLayout.NORTH);

        // Create timetable (similar to StudentTimetablePage)
        String[] columnNames = { "Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        String[] timeSlots = {
                "8:00 - 9:00", "9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 1:00", "1:00 - 2:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"
        };

        Object[][] data = new Object[timeSlots.length][6];
        for (int i = 0; i < timeSlots.length; i++) {
            data[i][0] = timeSlots[i];
            for (int j = 1; j < 6; j++) {
                data[i][j] = "";
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable timetableTable = new JTable(model);
        timetableTable.setRowHeight(60);
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        for (int i = 1; i < 6; i++) {
            timetableTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }

        // Fill the timetable with course info
        for (Course course : courses) {
            // Find the time slot row
            int rowIndex = -1;
            for (int i = 0; i < timeSlots.length; i++) {
                if (timeSlots[i].equals(course.lectureTime)) {
                    rowIndex = i;
                    break;
                }
            }

            if (rowIndex != -1) {
                // For each day in the pattern, add the course
                for (char day : course.lectureDays.toCharArray()) {
                    int colIndex;
                    switch (day) {
                        case 'M':
                            colIndex = 1;
                            break; // Monday
                        case 'T':
                            // Check if this T is part of Th
                            if (course.lectureDays.contains("Th") &&
                                    course.lectureDays.indexOf("T") == course.lectureDays.indexOf("Th")) {
                                colIndex = 4; // Thursday
                            } else {
                                colIndex = 2; // Tuesday
                            }
                            break;
                        case 'W':
                            colIndex = 3;
                            break; // Wednesday
                        case 'F':
                            colIndex = 5;
                            break; // Friday
                        default:
                            continue; // Skip unknown days
                    }

                    // Add course to cell
                    String cellContent = course.courseCode + "\n" + course.courseName;
                    model.setValueAt(cellContent, rowIndex, colIndex);
                }
            }

            // If has lab, add lab sessions too
            if (course.hasLab) {
                // Find lab time slot
                int labRowIndex = -1;
                for (int i = 0; i < timeSlots.length; i++) {
                    if (timeSlots[i].equals(course.labTime)) {
                        labRowIndex = i;
                        break;
                    }
                }

                if (labRowIndex != -1) {
                    // For each lab day, add to timetable
                    for (char day : course.labDays.toCharArray()) {
                        int colIndex;
                        switch (day) {
                            case 'M':
                                colIndex = 1;
                                break; // Monday
                            case 'T':
                                colIndex = 2;
                                break; // Tuesday
                            case 'W':
                                colIndex = 3;
                                break; // Wednesday
                            case 'h':
                                continue; // Part of Th, skip
                            case 'F':
                                colIndex = 5;
                                break; // Friday
                            default:
                                continue;
                        }

                        // Add lab to cell
                        String cellContent = course.courseCode + " (Lab)\n" + course.labSection;
                        model.setValueAt(cellContent, labRowIndex, colIndex);
                    }
                }
            }
        }

        add(new JScrollPane(timetableTable), BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            dispose();
            new InstructorDashboard();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

class InstructorDashboard extends JFrame {

    public static void addCourse(Course course) {
        myCourses.add(course);
        CourseRepository.addCourse(course); // Also add to global repository for students
    }

    public static List<Course> myCourses = new ArrayList<>(List.of(
            new Course("CS201", "Data Structures", "L5", "LB5", true, 3),
            new Course("CS301", "Artificial Intelligence", "L6", "", false, 2)));

    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(HeaderUtil.createHeader(this, "Instructor"), BorderLayout.NORTH);

        JPanel statsPanel = getStatsPanel();
        add(statsPanel, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("My Courses", getMyCoursesPanel());
        tabs.add("Other Portals", getOtherPortalsPanel());

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel getStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        int courseCount = myCourses.size();
        int totalLectures = myCourses.stream().mapToInt(c -> c.lectureCount).sum();
        int totalLabs = (int) myCourses.stream().filter(c -> c.hasLab).count();

        panel.add(new JLabel("Courses Teaching: " + courseCount, SwingConstants.CENTER));
        panel.add(new JLabel("Lectures/Week: " + totalLectures, SwingConstants.CENTER));
        panel.add(new JLabel("Labs/Week: " + totalLabs, SwingConstants.CENTER));

        return panel;
    }

    private JScrollPane getMyCoursesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        for (Course c : myCourses) {
            JButton btn = new JButton(c.courseCode + ": " + c.courseName);
            btn.addActionListener(e -> new ViewCoursePage(c, "Instructor"));
            panel.add(btn);
        }
        return new JScrollPane(panel);
    }

    private JPanel getOtherPortalsPanel() {
        JPanel panel = new JPanel();

        JButton timetableBtn = new JButton("View Timetable");
        timetableBtn.addActionListener(e -> new InstructorTimetablePage(myCourses));

        JButton addCourseBtn = new JButton("Add Course");
        addCourseBtn.addActionListener(e -> new InstructorAddCoursePage());

        panel.add(timetableBtn);
        panel.add(addCourseBtn);
        return panel;
    }
}

class InstructorAddCoursePage extends JFrame {
    public InstructorAddCoursePage() {
        setTitle("Add Course");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Course information panel
        JPanel basicInfoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        basicInfoPanel.setBorder(BorderFactory.createTitledBorder("Course Information"));

        JTextField courseIdField = new JTextField();
        JTextField courseNameField = new JTextField("Blockchain Technology");
        JTextField instructorNameField = new JTextField("Dr. "); // Pre-fill with Dr.
        JComboBox<String> lectureSectionDropdown = new JComboBox<>(new String[] { "L1", "L2", "L3" });
        JComboBox<String> labSectionDropdown = new JComboBox<>(new String[] { "", "LB1", "LB2", "LB3" });

        basicInfoPanel.add(new JLabel("Course ID:"));
        basicInfoPanel.add(courseIdField);
        basicInfoPanel.add(new JLabel("Course Name:"));
        basicInfoPanel.add(courseNameField);
        basicInfoPanel.add(new JLabel("Instructor Name:"));
        basicInfoPanel.add(instructorNameField);
        basicInfoPanel.add(new JLabel("Lecture Section:"));
        basicInfoPanel.add(lectureSectionDropdown);
        basicInfoPanel.add(new JLabel("Lab Section (if applicable):"));
        basicInfoPanel.add(labSectionDropdown);

        // Lecture schedule panel with alternating day patterns
        JPanel lectureSchedulePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        lectureSchedulePanel.setBorder(BorderFactory.createTitledBorder("Lecture Schedule"));

        // Lecture day pattern selection (alternating days only)
        JPanel dayPatternPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dayPatternPanel.add(new JLabel("Day Pattern:"));
        ButtonGroup patternGroup = new ButtonGroup();
        JRadioButton mwfPattern = new JRadioButton("Monday-Wednesday-Friday", true);
        JRadioButton tthPattern = new JRadioButton("Tuesday-Thursday");

        patternGroup.add(mwfPattern);
        patternGroup.add(tthPattern);
        dayPatternPanel.add(mwfPattern);
        dayPatternPanel.add(tthPattern);

        // Lecture time selection
        JPanel lectureTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lectureTimePanel.add(new JLabel("Select Time:"));
        String[] lectureTimes = {
                "8:00 - 9:00", "9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
                "12:00 - 1:00", "1:00 - 2:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00"
        };
        JComboBox<String> lectureTimeDropdown = new JComboBox<>(lectureTimes);
        lectureTimeDropdown.setSelectedItem("10:00 - 11:00"); // Default
        lectureTimePanel.add(lectureTimeDropdown);

        lectureSchedulePanel.add(dayPatternPanel);
        lectureSchedulePanel.add(lectureTimePanel);

        // Note about lunch break
        JPanel lunchNotePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lunchNoteLabel = new JLabel(
                "<html><b>Note:</b> Either 12:00-1:00 or 1:00-2:00 must remain free for lunch.</html>");
        lunchNoteLabel.setForeground(Color.RED);
        lunchNotePanel.add(lunchNoteLabel);
        lectureSchedulePanel.add(lunchNotePanel);

        // Lab timing panel
        JPanel labPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        labPanel.setBorder(BorderFactory.createTitledBorder("Lab Schedule (if applicable)"));

        // Lab days selection
        JPanel labDaysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labDaysPanel.add(new JLabel("Select Days:"));
        JCheckBox monLab = new JCheckBox("Monday");
        JCheckBox tueLab = new JCheckBox("Tuesday", true);
        JCheckBox wedLab = new JCheckBox("Wednesday");
        JCheckBox thuLab = new JCheckBox("Thursday");
        JCheckBox friLab = new JCheckBox("Friday");

        labDaysPanel.add(monLab);
        labDaysPanel.add(tueLab);
        labDaysPanel.add(wedLab);
        labDaysPanel.add(thuLab);
        labDaysPanel.add(friLab);

        // Lab time selection with longer time slots for labs
        JPanel labTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labTimePanel.add(new JLabel("Select Lab Time:"));
        String[] labTimes = {
                "8:00 - 10:00", "10:00 - 12:00", "1:00 - 3:00", "3:00 - 5:00"
        };
        JComboBox<String> labTimeDropdown = new JComboBox<>(labTimes);
        labTimePanel.add(labTimeDropdown);

        labPanel.add(labDaysPanel);
        labPanel.add(labTimePanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmBtn = new JButton("Confirm");
        JButton cancelBtn = new JButton("Cancel");

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);

        // Add components to main panel
        mainPanel.add(basicInfoPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lectureSchedulePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(labPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        // Hide/show lab panel based on lab section selection
        labSectionDropdown.addActionListener(e -> {
            String selected = (String) labSectionDropdown.getSelectedItem();
            labPanel.setVisible(!selected.isEmpty());
            pack();
        });

        // Set initial lab panel visibility based on default selection
        labPanel.setVisible(!((String) labSectionDropdown.getSelectedItem()).isEmpty());

        // Check lunch slot availability when selecting time
        lectureTimeDropdown.addActionListener(e -> {
            String selectedTime = (String) lectureTimeDropdown.getSelectedItem();

            // Only check if a lunch slot is selected
            if (selectedTime.equals("12:00 - 1:00") || selectedTime.equals("1:00 - 2:00")) {
                String otherLunchSlot = selectedTime.equals("12:00 - 1:00") ? "1:00 - 2:00" : "12:00 - 1:00";

                // Check if any existing course uses the other lunch slot
                for (Course course : InstructorDashboard.myCourses) {
                    if (course.lectureTime.equals(otherLunchSlot)) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Cannot select " + selectedTime + " as " + otherLunchSlot +
                                        " is already being used.\nAt least one lunch slot must remain free.",
                                "Lunch Break Constraint",
                                JOptionPane.WARNING_MESSAGE);

                        // Reset to a non-lunch time
                        lectureTimeDropdown.setSelectedItem("10:00 - 11:00");
                        break;
                    }
                }
            }
        });

        // Confirm button action
        confirmBtn.addActionListener(e -> {
            // Get course info
            String courseId = courseIdField.getText().trim();
            String courseName = courseNameField.getText().trim();
            String instructorName = instructorNameField.getText().trim();
            String lectureSection = (String) lectureSectionDropdown.getSelectedItem();
            String labSection = (String) labSectionDropdown.getSelectedItem();
            boolean hasLab = !labSection.isEmpty();

            // Validation
            if (courseId.isEmpty() || courseName.isEmpty() || instructorName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Course ID, Course Name, and Instructor Name cannot be empty",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get lecture days pattern based on selection
            String lectureDays = mwfPattern.isSelected() ? "MWF" : "TTh";

            // Get lecture time
            String lectureTime = (String) lectureTimeDropdown.getSelectedItem();

            // Check lunch time constraint
            if (lectureTime.equals("12:00 - 1:00") || lectureTime.equals("1:00 - 2:00")) {
                String otherLunchSlot = lectureTime.equals("12:00 - 1:00") ? "1:00 - 2:00" : "12:00 - 1:00";

                for (Course course : InstructorDashboard.myCourses) {
                    if (course.lectureTime.equals(otherLunchSlot)) {
                        JOptionPane.showMessageDialog(this,
                                "Cannot add course with " + lectureTime + " timing.\n" +
                                        "One lunch time slot (12:00-1:00 or 1:00-2:00) must always be free.\n" +
                                        "Another course already uses the " + otherLunchSlot + " slot.",
                                "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Define lab days and time variables with proper naming
            String labDays = "";
            String labTime = "";

            if (hasLab) {
                // Build lab days string from checkboxes
                StringBuilder labDaysBuilder = new StringBuilder();
                if (monLab.isSelected())
                    labDaysBuilder.append("M");
                if (tueLab.isSelected())
                    labDaysBuilder.append("T");
                if (wedLab.isSelected())
                    labDaysBuilder.append("W");
                if (thuLab.isSelected())
                    labDaysBuilder.append("Th");
                if (friLab.isSelected())
                    labDaysBuilder.append("F");

                labDays = labDaysBuilder.toString();

                if (labDays.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please select at least one day for lab",
                            "Invalid Selection", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get lab time from dropdown
                labTime = (String) labTimeDropdown.getSelectedItem();
            }

            // Create new course with correct parameter names
            Course newCourse = new Course(
                    courseId,
                    courseName,
                    lectureSection,
                    labSection,
                    hasLab,
                    lectureDays.length(), // Number of lectures based on pattern
                    lectureDays,
                    lectureTime,
                    labDays, // Use labDays (plural) instead of labDay
                    labTime, // Now properly defined
                    instructorName);

            // Check for conflicts with existing courses
            boolean conflict = false;
            Course conflictingCourse = null;

            for (Course existingCourse : InstructorDashboard.myCourses) {
                if (newCourse.hasTimeConflict(existingCourse)) {
                    conflict = true;
                    conflictingCourse = existingCourse;
                    break;
                }
            }

            if (conflict) {
                JOptionPane.showMessageDialog(this,
                        "Time conflict detected with existing course: " +
                                conflictingCourse.courseCode + " - " + conflictingCourse.courseName,
                        "Scheduling Conflict", JOptionPane.ERROR_MESSAGE);
            } else {
                InstructorDashboard.addCourse(newCourse);
                JOptionPane.showMessageDialog(this, "Course added successfully!");
                dispose();
                new InstructorDashboard();
            }
        });

        // Cancel button action
        cancelBtn.addActionListener(e -> {
            dispose();
            new InstructorDashboard();
        });

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        pack();
        setVisible(true);
    }
}

class UpdateCourseStatusPage extends JFrame {
    private Course currentCourse;

    public UpdateCourseStatusPage(Course course) {
        this.currentCourse = course;
        setTitle("Update Course Status");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(64, 224, 208));

        JLabel nameLabel = new JLabel("XYZ");
        JLabel scheduleLabel = new JLabel("Class Schedule");
        scheduleLabel.setForeground(Color.BLUE);
        scheduleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel timeLabel = new JLabel(new SimpleDateFormat("hh:mm a  EEEE, d/M/yyyy").format(new Date()));
        header.add(nameLabel, BorderLayout.WEST);
        header.add(scheduleLabel, BorderLayout.CENTER);
        header.add(timeLabel, BorderLayout.EAST);

        // Body Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Course info label
        if (currentCourse != null) {
            JLabel courseInfoLabel = new JLabel(
                    "Course: " + currentCourse.courseCode + " - " + currentCourse.courseName);
            courseInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            courseInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(courseInfoLabel);
            panel.add(Box.createVerticalStrut(10));
        }

        // Exam Section
        panel.add(createRow("Midsem Exam Date", new JTextField("03/03/25"),
                "Midsem Exam Time", new JTextField("9:30-11 am")));

        panel.add(createRow("Comprehensive Exam Date", new JTextField("02/05/25"),
                "Comprehensive Exam Time", new JTextField("2 - 5 pm")));

        // Lecture Hall & Lab Capacity + Requirements
        JPanel facilitiesPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        facilitiesPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Lecture Hall section
        JPanel lecturePanel = new JPanel();
        lecturePanel.setLayout(new BoxLayout(lecturePanel, BoxLayout.Y_AXIS));
        lecturePanel.setBorder(BorderFactory.createTitledBorder("Lecture Hall"));

        JTextField lecCapacity = new JTextField("100");
        lecturePanel.add(createFormRow("Capacity", lecCapacity));
        lecturePanel.add(createRadioGroup("Impartus Required", "lectureImpartus"));
        lecturePanel.add(createRadioGroup("Projector Required", "lectureProjector"));

        // Lab section
        JPanel labPanel = new JPanel();
        labPanel.setLayout(new BoxLayout(labPanel, BoxLayout.Y_AXIS));
        labPanel.setBorder(BorderFactory.createTitledBorder("Lab"));

        JTextField labCapacity = new JTextField("50");
        labPanel.add(createFormRow("Capacity", labCapacity));
        labPanel.add(createRadioGroup("Microphone Required", "labMic"));
        labPanel.add(createRadioGroup("Projector Required", "labProjector"));

        facilitiesPanel.add(lecturePanel);
        facilitiesPanel.add(labPanel);
        panel.add(facilitiesPanel);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton approveBtn = new JButton("Approve");
        approveBtn.setBackground(new Color(0, 204, 0));
        approveBtn.setForeground(Color.WHITE);

        JButton declineBtn = new JButton("Decline");
        declineBtn.setBackground(Color.RED);
        declineBtn.setForeground(Color.WHITE);

        approveBtn.addActionListener(e -> {
            // Remove course from pending courses
            if (currentCourse != null) {
                AdminDashboard.pendingCourses.removeIf(c -> c.courseCode.equals(currentCourse.courseCode) &&
                        c.courseName.equals(currentCourse.courseName));
            }
            JOptionPane.showMessageDialog(this, "Course Approved");
            dispose();
            new AdminDashboard();
        });

        declineBtn.addActionListener(e -> {
            // Remove course from pending courses
            if (currentCourse != null) {
                AdminDashboard.pendingCourses.removeIf(c -> c.courseCode.equals(currentCourse.courseCode) &&
                        c.courseName.equals(currentCourse.courseName));
            }
            JOptionPane.showMessageDialog(this, "Course Declined");
            dispose();
            new AdminDashboard();
        });

        btnPanel.add(approveBtn);
        btnPanel.add(declineBtn);

        panel.add(btnPanel);

        // Back Button
        JButton backBtn = new JButton("Back");
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(Color.BLUE);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            new AdminViewCoursePage(currentCourse);
        });

        panel.add(backBtn);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);
        setVisible(true);
    }

    // Constructor for backward compatibility
    public UpdateCourseStatusPage() {
        this(null);
    }

    private JPanel createRow(Object... components) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Object comp : components) {
            if (comp instanceof String) {
                row.add(new JLabel((String) comp));
            } else if (comp instanceof JComponent) {
                row.add((JComponent) comp);
            }
        }
        return row;
    }

    private JPanel createFormRow(String label, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(label + ":"));
        field.setPreferredSize(new Dimension(100, 25));
        row.add(field);
        return row;
    }

    private JPanel createRadioGroup(String label, String groupName) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbl = new JLabel(label + ":");
        JRadioButton yesBtn = new JRadioButton("Yes", true);
        JRadioButton noBtn = new JRadioButton("No");

        ButtonGroup group = new ButtonGroup();
        group.add(yesBtn);
        group.add(noBtn);

        panel.add(lbl);
        panel.add(yesBtn);
        panel.add(noBtn);
        return panel;
    }
}

class AdminViewCoursePage extends JFrame {
    private JPanel labSectionContainer;
    private JTextField labSectionCountField;
    private Course currentCourse;

    public AdminViewCoursePage() {
        this(AdminDashboard.pendingCourses.isEmpty() ? null : AdminDashboard.pendingCourses.get(0));
    }

    public AdminViewCoursePage(Course course) {
        this.currentCourse = course;
        setTitle("View Course");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(64, 224, 208));

        JLabel nameLabel = new JLabel("XYZ");
        JLabel scheduleLabel = new JLabel("Class Schedule");
        scheduleLabel.setForeground(Color.BLUE);
        scheduleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scheduleLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // redirect logic
            }
        });

        JLabel timeLabel = new JLabel(new SimpleDateFormat("hh:mm a  EEEE, d/M/yyyy").format(new Date()));
        header.add(nameLabel, BorderLayout.WEST);
        header.add(scheduleLabel, BorderLayout.CENTER);
        header.add(timeLabel, BorderLayout.EAST);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Use course data if available
        String courseId = currentCourse != null ? currentCourse.courseCode : "CS F213";
        String courseTitle = currentCourse != null ? currentCourse.courseName : "Object Oriented Programming";

        panel.add(createRow("Course ID:", new JTextField(courseId), "Course Title:", new JTextField(courseTitle)));
        panel.add(createRow("Number of lecture sections:", new JTextField("1")));
        panel.add(createRow("Lecture Section:", new JTextField("L1"),
                "Instructor:", new JTextField("Abhijit Das"),
                "Days:", new JTextField("MoWeFr"),
                "Time:", new JTextField("9:00 - 9:50 am")));

        ButtonGroup labGroup = new ButtonGroup();
        JButton backButton = new JButton("Back");
        backButton.setBounds(250, 200, 100, 30);
        panel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AdminDashboard();
            }
        });

        JRadioButton yesBtn = new JRadioButton("Yes", true);
        JRadioButton noBtn = new JRadioButton("No");

        labGroup.add(yesBtn);
        labGroup.add(noBtn);

        panel.add(createRow("Lab Required:", yesBtn, new JLabel(""), noBtn));

        labSectionCountField = new JTextField("2");
        panel.add(createRow("Number of lab sections:", labSectionCountField));

        labSectionContainer = new JPanel();
        labSectionContainer.setLayout(new BoxLayout(labSectionContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(labSectionContainer);
        scrollPane.setPreferredSize(new Dimension(800, 150));

        panel.add(scrollPane);

        labSectionCountField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                generateLabSections();
            }
        });

        generateLabSections();

        JButton nextBtn = new JButton("Next");
        nextBtn.addActionListener(e -> {
            dispose();
            new UpdateCourseStatusPage(currentCourse);
        });

        panel.add(Box.createVerticalStrut(10));
        panel.add(nextBtn);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createRow(Object... elements) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] instanceof String) {
                row.add(new JLabel((String) elements[i]));
            } else if (elements[i] instanceof JComponent) {
                row.add((JComponent) elements[i]);
            }
        }
        return row;
    }

    private void generateLabSections() {
        labSectionContainer.removeAll();
        int count;
        try {
            count = Integer.parseInt(labSectionCountField.getText().trim());
        } catch (NumberFormatException e) {
            count = 0;
        }

        for (int i = 1; i <= count; i++) {
            labSectionContainer.add(
                    createRow("Lab Section:", new JTextField("P" + i),
                            "Instructor:", new JTextField(i == 1 ? "Abhijit Das" : "Panda"),
                            "Day:", new JTextField(i == 1 ? "Wednesday" : "Tuesday"),
                            "Time:", new JTextField("3:00 - 4:50 pm")));
        }
        labSectionContainer.revalidate();
        labSectionContainer.repaint();
    }
}

class AdminDashboard extends JFrame {
    // Add static list to store pending courses
    public static List<Course> pendingCourses = new ArrayList<>(List.of(
            new Course("CS F213", "Object Oriented Programming", "L1", "P1", true, 3)));

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(getHeaderPanel(), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(getMainPanel(), BorderLayout.CENTER);
        contentPanel.add(getCSVImportPanel(), BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel getCSVImportPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Import Data from CSV"));
        panel.setLayout(new GridLayout(1, 2, 10, 10));

        JButton importCoursesBtn = new JButton("Import Courses");

        // Add action listener for import button
        importCoursesBtn.addActionListener(e -> importCoursesFromCSV());

        panel.add(new JLabel("Courses:"));
        panel.add(importCoursesBtn);

        return panel;
    }

    private void importCoursesFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Course CSV File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                int importedCount = parseCoursesFromCSV(selectedFile);
                JOptionPane.showMessageDialog(this,
                        importedCount + " courses imported successfully!",
                        "Import Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error importing courses: " + ex.getMessage(),
                        "Import Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private int parseCoursesFromCSV(File csvFile) throws IOException, CsvValidationException {
        int count = 0;

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            // Skip header line
            String[] header = reader.readNext();
            if (header == null) {
                throw new IOException("CSV file is empty");
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                // Expected format:
                // code,name,lectureSection,labSection,hasLab,lectures,lectureDays,lectureTime,labDays,labTime,instructorName
                if (line.length >= 6) {
                    String code = line[0];
                    String name = line[1];
                    String lectureSection = line[2];
                    String labSection = line[3];
                    boolean hasLab = Boolean.parseBoolean(line[4]);
                    int lectures = Integer.parseInt(line[5]);

                    Course course;

                    if (line.length >= 9) {
                        String lectureDays = line[6];
                        String lectureTime = line[7];
                        String instructorName = line[8];

                        if (line.length >= 11 && hasLab) {
                            String labDays = line[9];
                            String labTime = line[10];
                            course = new Course(code, name, lectureSection, labSection, hasLab, lectures,
                                    lectureDays, lectureTime, labDays, labTime, instructorName);
                        } else {
                            course = new Course(code, name, lectureSection, labSection, hasLab, lectures,
                                    lectureDays, lectureTime, instructorName);
                        }
                    } else {
                        course = new Course(code, name, lectureSection, labSection, hasLab, lectures);
                    }

                    // Add to course repository
                    CourseRepository.addCourse(course);
                    count++;
                }
            }
        }
        return count;
    }

    private JPanel getHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(64, 224, 208));
        header.setPreferredSize(new Dimension(900, 50));

        JLabel userLabel = new JLabel("XYZ");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.BLACK);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel classSchedule = new JLabel("🏠 Class Schedule");
        classSchedule.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        classSchedule.setForeground(Color.BLACK);
        classSchedule.setFont(new Font("Arial", Font.PLAIN, 14));
        classSchedule.setHorizontalAlignment(SwingConstants.CENTER);

        String currentTime = new SimpleDateFormat("h:mm a  EEEE, d/M/yyyy").format(new Date());
        JLabel timeLabel = new JLabel(currentTime);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        header.add(userLabel, BorderLayout.WEST);
        header.add(classSchedule, BorderLayout.CENTER);
        header.add(timeLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel getMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel approvedLabel = new JLabel("Courses Approved");
        JLabel declinedLabel = new JLabel("Courses Declined");
        JLabel pendingLabel = new JLabel("Courses Pending");

        JLabel approvedCount = new JLabel("35");
        JLabel declinedCount = new JLabel("3");
        // Update pending count dynamically
        JLabel pendingCount = new JLabel(String.valueOf(pendingCourses.size()));

        approvedCount.setFont(new Font("Arial", Font.BOLD, 24));
        declinedCount.setFont(new Font("Arial", Font.BOLD, 24));
        pendingCount.setFont(new Font("Arial", Font.BOLD, 24));

        leftPanel.add(createStatPanel(approvedLabel, approvedCount));
        leftPanel.add(createStatPanel(declinedLabel, declinedCount));
        leftPanel.add(createStatPanel(pendingLabel, pendingCount));

        JPanel pendingCoursesPanel = new JPanel(new BorderLayout());
        pendingCoursesPanel.setBorder(BorderFactory.createTitledBorder("Pending Courses"));
        pendingCoursesPanel.setPreferredSize(new Dimension(300, 100));

        // Create a panel to hold pending course buttons
        JPanel pendingButtonsPanel = new JPanel();
        pendingButtonsPanel.setLayout(new BoxLayout(pendingButtonsPanel, BoxLayout.Y_AXIS));

        // Add a button for each pending course
        if (pendingCourses.isEmpty()) {
            pendingButtonsPanel.add(new JLabel("No pending courses"));
        } else {
            for (Course course : pendingCourses) {
                JButton pendingCourseBtn = new JButton(course.courseName);
                pendingCourseBtn.setFocusPainted(false);
                pendingCourseBtn.setBackground(Color.WHITE);
                pendingCourseBtn.addActionListener(e -> new AdminViewCoursePage(course));
                pendingButtonsPanel.add(pendingCourseBtn);
            }
        }

        pendingCoursesPanel.add(new JScrollPane(pendingButtonsPanel), BorderLayout.CENTER);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(pendingCoursesPanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("All Courses"));

        JTextField searchBar = new JTextField("Search by Name, Department or ID");
        JPanel courseListPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        courseListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] courses = {
                "Machine Learning", "Operating Systems",
                "Foundations of Data Structure and Algorithms",
                "Database Management Systems"
        };

        for (String course : courses) {
            JButton btn = new JButton(course);
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            courseListPanel.add(btn);
        }

        rightPanel.add(searchBar, BorderLayout.NORTH);
        rightPanel.add(courseListPanel, BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatPanel(JLabel label, JLabel count) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        statPanel.setPreferredSize(new Dimension(250, 50));
        statPanel.add(label, BorderLayout.WEST);
        statPanel.add(count, BorderLayout.EAST);
        return statPanel;
    }
}
