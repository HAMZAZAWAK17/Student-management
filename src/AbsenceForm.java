package loginandsignup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AbsenceForm extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JComboBox<String> studentComboBox;
    private JTextField moduleField, dateField, motifField, absenceHeuresField, commentaireField;
    private JComboBox<String> presenceComboBox, justifieComboBox;

    public AbsenceForm() {
        initializeFrame();
        createAndShowGUI();
    }

    private void initializeFrame() {
        setTitle("Ajouter une Absence");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
    }

    private void createAndShowGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Create title panel
        JPanel titlePanel = createTitlePanel();

        // Create form panel
        JPanel formPanel = createFormPanel();

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("AJOUTER UNE ABSENCE");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Initialize form components
        studentComboBox = createStyledComboBox(loadStudents().toArray(new String[0]));
        moduleField = createStyledTextField();
        dateField = createStyledTextField();
        motifField = createStyledTextField();
        absenceHeuresField = createStyledTextField();
        commentaireField = createStyledTextField();
        presenceComboBox = createStyledComboBox(new String[]{"Présent", "Absent"});
        justifieComboBox = createStyledComboBox(new String[]{"Oui", "Non"});

        // Add fields to form
        addFormField(formPanel, "Étudiant", studentComboBox);
        addFormField(formPanel, "Module", moduleField);
        addFormField(formPanel, "Date", dateField);
        addFormField(formPanel, "Présence", presenceComboBox);
        addFormField(formPanel, "Motif", motifField);
        addFormField(formPanel, "Heures d'absence", absenceHeuresField);
        addFormField(formPanel, "Justifiée", justifieComboBox);
        addFormField(formPanel, "Commentaire", commentaireField);

        // Add submit button
        JPanel buttonPanel = createButtonPanel();
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonPanel);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JButton submitButton = createStyledButton("Enregistrer", SUCCESS_COLOR);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAbsence();
            }
        });

        JButton backButton = createStyledButton("Retour", SECONDARY_COLOR);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Ferme la fenêtre actuelle
                new AbsenceList();  // Ouvre la fenêtre "AbsenceList"
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void saveAbsence() {
        try {
            // Validate form fields
            if (moduleField.getText().trim().isEmpty() ||
                    dateField.getText().trim().isEmpty() ||
                    (presenceComboBox.getSelectedItem().toString().equals("Absent") && (motifField.getText().trim().isEmpty() ||
                            absenceHeuresField.getText().trim().isEmpty()))) {
                showMessage("Veuillez remplir tous les champs obligatoires.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected student ID and other form fields
            String studentName = studentComboBox.getSelectedItem().toString();
            String[] studentNameParts = studentName.split(" ");  // Split to get first and last name
            String studentId = studentNameParts[0];  // Assuming the first part is the ID
            String module = moduleField.getText().trim();
            String date = dateField.getText().trim();
            String presence = presenceComboBox.getSelectedItem().toString();
            String motif = presence.equals("Absent") ? motifField.getText().trim() : "";
            String absenceHeures = presence.equals("Absent") ? absenceHeuresField.getText().trim() : "0";
            String justifie = presence.equals("Absent") ? justifieComboBox.getSelectedItem().toString() : "Non";
            String commentaire = presence.equals("Absent") ? commentaireField.getText().trim() : "";

            // Database connection and insertion
            String url = "jdbc:mysql://localhost:3306/java_user_database";
            String query = "INSERT INTO AbsenceList (nom, prenom, module, date, presence, motif, absence_heures, justifiee, commentaires) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(url, "root", "");
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, studentNameParts[1]);  // Last name
                pstmt.setString(2, studentNameParts[2]);  // First name
                pstmt.setString(3, module);
                pstmt.setString(4, date);
                pstmt.setString(5, presence);
                pstmt.setString(6, motif);
                pstmt.setString(7, absenceHeures);
                pstmt.setString(8, justifie);
                pstmt.setString(9, commentaire);

                pstmt.executeUpdate();
                showMessage("Absence enregistrée avec succès !", JOptionPane.INFORMATION_MESSAGE);

                // Clear the form after saving
                clearForm();
            }
        } catch (SQLException ex) {
            showMessage("Erreur lors de l'enregistrement : " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        moduleField.setText("");
        dateField.setText("");
        motifField.setText("");
        absenceHeuresField.setText("");
        commentaireField.setText("");
        presenceComboBox.setSelectedIndex(0);
        justifieComboBox.setSelectedIndex(0);
    }

    private void showMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, messageType == JOptionPane.ERROR_MESSAGE ? "Erreur" : "Succès", messageType);
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(REGULAR_FONT);
        comboBox.setPreferredSize(new Dimension(300, 35));
        return comboBox;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(REGULAR_FONT);
        field.setPreferredSize(new Dimension(300, 35));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText + ":");
        label.setFont(REGULAR_FONT);
        label.setPreferredSize(new Dimension(200, 35));

        fieldPanel.add(label);
        fieldPanel.add(field);
        panel.add(fieldPanel);
    }

    private ArrayList<String> loadStudents() {
        ArrayList<String> students = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String query = "SELECT id, nom, prenom FROM student";  // Adjust table name and columns as needed

        try (Connection connection = DriverManager.getConnection(url, "root", "");
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                students.add(id + " " + nom + " " + prenom);
            }
        } catch (SQLException ex) {
            showMessage("Erreur lors du chargement des étudiants : " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        return students;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AbsenceForm::new);
    }
}
