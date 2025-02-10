package loginandsignup;

import javax.swing.*;
import java.awt.*;

public class Home extends javax.swing.JFrame {

    public Home() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Initialize components
        mainPanel = new javax.swing.JPanel();
        contentPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        studentListBtn = new javax.swing.JButton();
        absenceListBtn = new javax.swing.JButton();
        LogoutBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HOME - ESTEM Management System");
        setPreferredSize(new java.awt.Dimension(800, 500));
        setResizable(false);

        // Main Panel Setup with BorderLayout
        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(new BorderLayout());

        // Content Panel that will be centered
        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Welcome Message Setup
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48));
        jLabel1.setForeground(new java.awt.Color(0, 102, 204));
        jLabel1.setText("Welcome Back");
        jLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User Name Setup
        user.setFont(new java.awt.Font("Segoe UI", 1, 36));
        user.setForeground(new java.awt.Color(51, 51, 51));
        user.setText("sir");
        user.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button Panel Setup
        buttonPanel.setBackground(new java.awt.Color(255, 255, 255));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Student List Button
        studentListBtn.setBackground(new java.awt.Color(0, 102, 204));
        studentListBtn.setFont(new java.awt.Font("Segoe UI", 1, 14));
        studentListBtn.setForeground(new java.awt.Color(255, 255, 255));
        studentListBtn.setText("Liste des étudiants");
        studentListBtn.setPreferredSize(new Dimension(200, 40));
        studentListBtn.setMaximumSize(new Dimension(200, 40));
        studentListBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        studentListBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        studentListBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        studentListBtn.setFocusPainted(false);
        studentListBtn.addActionListener(evt -> studentListBtnActionPerformed(evt));

        // Absence List Button
        absenceListBtn.setBackground(new java.awt.Color(0, 153, 51));
        absenceListBtn.setFont(new java.awt.Font("Segoe UI", 1, 14));
        absenceListBtn.setForeground(new java.awt.Color(255, 255, 255));
        absenceListBtn.setText("Liste des absences");
        absenceListBtn.setPreferredSize(new Dimension(200, 40));
        absenceListBtn.setMaximumSize(new Dimension(200, 40));
        absenceListBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        absenceListBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        absenceListBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        absenceListBtn.setFocusPainted(false);
        absenceListBtn.addActionListener(evt -> absenceListBtnActionPerformed(evt));

        // Logout Button
        LogoutBtn.setBackground(new java.awt.Color(204, 0, 0));
        LogoutBtn.setFont(new java.awt.Font("Segoe UI", 1, 14));
        LogoutBtn.setForeground(new java.awt.Color(255, 255, 255));
        LogoutBtn.setText("Déconnexion");
        LogoutBtn.setPreferredSize(new Dimension(200, 40));
        LogoutBtn.setMaximumSize(new Dimension(200, 40));
        LogoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        LogoutBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        LogoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        LogoutBtn.setFocusPainted(false);
        LogoutBtn.addActionListener(evt -> LogoutBtnActionPerformed(evt));

        // Add components with vertical spacing
        contentPanel.add(Box.createVerticalGlue()); // Add space at top
        contentPanel.add(jLabel1);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between welcome and username
        contentPanel.add(user);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Space between username and buttons

        // Add buttons to button panel with spacing
        buttonPanel.add(studentListBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space between buttons
        buttonPanel.add(absenceListBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space between buttons
        buttonPanel.add(LogoutBtn);

        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalGlue()); // Add space at bottom

        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        getContentPane().add(mainPanel);

        pack();
    }

    private void LogoutBtnActionPerformed(java.awt.event.ActionEvent evt) {
        Login LoginFrame = new Login();
        LoginFrame.setVisible(true);
        LoginFrame.pack();
        LoginFrame.setLocationRelativeTo(null);
        this.dispose();
    }

    private void studentListBtnActionPerformed(java.awt.event.ActionEvent evt) {
        studentList studentListFrame = new studentList();
        studentListFrame.setVisible(true);
        studentListFrame.setLocationRelativeTo(null);
    }

    // Event handler for the absence list button
    private void absenceListBtnActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            new AbsenceList().setVisible(true);
            dispose(); // Fermer la fenêtre actuelle
        });
    }


    public void setUser(String name) {
        user.setText(name);
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new Home().setVisible(true);
        });
    }

    // Variables declaration
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton LogoutBtn;
    private javax.swing.JButton studentListBtn;
    private javax.swing.JButton absenceListBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel user;
}