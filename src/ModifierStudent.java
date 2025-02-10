package loginandsignup;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ModifierStudent extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(0, 149, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final int studentId;

    public ModifierStudent(int studentId) {
        this.studentId = studentId;
        setTitle("Modifier étudiant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("MODIFIER ÉTUDIANT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] labels = {"Nom:", "Prénom:", "Date de naissance (YYYY-MM-DD):", "Genre:", "Email:", "Téléphone:", "Adresse:", "Filière:", "Note 1:", "Note 2:", "Note 3:"};
        JTextField[] textFields = new JTextField[11];
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Homme", "Femme", "Autre"});
        JComboBox<String> moduleCombo = new JComboBox<>(new String[]{"Java", "Nodejs", "Mongodb", "Mysql", "Reactjs"});

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            if (i == 3) {
                formPanel.add(genderCombo, gbc);
            } else if (i == 7) {
                formPanel.add(moduleCombo, gbc);
            } else {
                textFields[i] = new JTextField(20);
                formPanel.add(textFields[i], gbc);
            }
        }

        // Load existing student data
        loadStudentData(textFields, genderCombo, moduleCombo);

        // Update button
        JButton updateButton = new JButton("Mettre à jour");
        updateButton.addActionListener(e -> updateStudent(textFields, genderCombo, moduleCombo));
        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        formPanel.add(updateButton, gbc);

        // Back button
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> {
            new studentList();
            dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        formPanel.add(backButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadStudentData(JTextField[] fields, JComboBox<String> genderCombo, JComboBox<String> moduleCombo) {
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String query = "SELECT * FROM student WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, "root", "");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                fields[0].setText(rs.getString("nom"));
                fields[1].setText(rs.getString("prenom"));
                fields[2].setText(rs.getString("date_naissance"));
                genderCombo.setSelectedItem(rs.getString("gender"));
                fields[4].setText(rs.getString("email"));
                fields[5].setText(rs.getString("telephone"));
                fields[6].setText(rs.getString("adresse"));
                moduleCombo.setSelectedItem(rs.getString("module"));
                fields[8].setText(rs.getString("note1"));
                fields[9].setText(rs.getString("note2"));
                fields[10].setText(rs.getString("note3"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + ex.getMessage());
        }
    }

    private void updateStudent(JTextField[] fields, JComboBox<String> genderCombo, JComboBox<String> moduleCombo) {
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String query = "UPDATE student SET nom=?, prenom=?, date_naissance=?, gender=?, email=?, telephone=?, adresse=?, module=?, note1=?, note2=?, note3=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(url, "root", "");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fields[0].getText());
            pstmt.setString(2, fields[1].getText());

            // Parse and validate date
            java.sql.Date formattedDate = parseDate(fields[2].getText());
            if (formattedDate == null) {
                JOptionPane.showMessageDialog(this, "Date de naissance invalide. Utilisez le format YYYY-MM-DD.");
                return;
            }
            pstmt.setDate(3, formattedDate);

            pstmt.setString(4, genderCombo.getSelectedItem().toString());
            pstmt.setString(5, fields[4].getText());
            pstmt.setString(6, fields[5].getText());
            pstmt.setString(7, fields[6].getText());
            pstmt.setString(8, moduleCombo.getSelectedItem().toString());
            
            // Handle parsing of notes and validate them
            try {
                pstmt.setDouble(9, Double.parseDouble(fields[8].getText()));
                pstmt.setDouble(10, Double.parseDouble(fields[9].getText()));
                pstmt.setDouble(11, Double.parseDouble(fields[10].getText()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides pour les notes.");
                return;
            }
            
            pstmt.setInt(12, studentId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Étudiant mis à jour avec succès!");
                new studentList();
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour: " + ex.getMessage());
        }
    }

    private java.sql.Date parseDate(String dateInput) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(dateInput);
            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }
}
