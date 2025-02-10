package loginandsignup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class student extends JFrame {
    // Constants for UI styling
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Form fields
    private JTextField averageField;
    private JComboBox<String> mentionCombo;
    private JTextField note1Field;
    private JTextField note2Field;
    private JTextField note3Field;
    private JTextField[] personalFields;
    private JComboBox<String> genderCombo;
    private JComboBox<String> moduleCombo;

    public student() {
        initializeFrame();
        createAndShowGUI();
    }

    private void initializeFrame() {
        setTitle("Gestion des Étudiants");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createAndShowGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Create title panel
        JPanel titlePanel = createTitlePanel();

        // Create form panel
        JPanel formPanel = createFormPanel();

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        setVisible(true);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("AJOUTER UN NOUVEL ÉTUDIANT");
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
        initializeFormComponents();

        // Create section panels
        JPanel personalInfoPanel = createSectionPanel("Informations Personnelles");
        JPanel contactInfoPanel = createSectionPanel("Coordonnées");
        JPanel academicInfoPanel = createSectionPanel("Informations Académiques");
        JPanel resultsPanel = createSectionPanel("Résultats");

        // Add fields to panels
        addPersonalInfoFields(personalInfoPanel);
        addContactInfoFields(contactInfoPanel);
        addAcademicInfoFields(academicInfoPanel);
        addResultsFields(resultsPanel);

        // Add sections to form
        formPanel.add(personalInfoPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(contactInfoPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(academicInfoPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(resultsPanel);

        // Add buttons
        JPanel buttonPanel = createButtonPanel();
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonPanel);

        return formPanel;
    }

    private void initializeFormComponents() {
        personalFields = new JTextField[7];
        for (int i = 0; i < 7; i++) {
            personalFields[i] = createStyledTextField();
        }

        note1Field = createStyledTextField();
        note2Field = createStyledTextField();
        note3Field = createStyledTextField();
        averageField = createStyledTextField();
        averageField.setEditable(false);

        genderCombo = createStyledComboBox(new String[]{"Homme", "Femme"});
        moduleCombo = createStyledComboBox(new String[]{"Java", "Nodejs", "Mongodb", "Mysql", "Reactjs"});
        mentionCombo = createStyledComboBox(new String[]{"Très Bien", "Bien", "Assez Bien", "Passable", "Insuffisant"});
        mentionCombo.setEnabled(false);

        // Add listeners for grade calculations
        KeyAdapter noteListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateAverageAndMention();
            }
        };

        note1Field.addKeyListener(noteListener);
        note2Field.addKeyListener(noteListener);
        note3Field.addKeyListener(noteListener);
    }

    private void addPersonalInfoFields(JPanel panel) {
        addFormField(panel, "Nom", personalFields[0]);
        addFormField(panel, "Prénom", personalFields[1]);
        addFormField(panel, "Date de naissance (Y-M-D)", personalFields[2]);
        addFormField(panel, "Genre", genderCombo);
    }

    private void addContactInfoFields(JPanel panel) {
        addFormField(panel, "Email", personalFields[3]);
        addFormField(panel, "Téléphone", personalFields[4]);
        addFormField(panel, "Adresse", personalFields[5]);
    }

    private void addAcademicInfoFields(JPanel panel) {
        addFormField(panel, "module", moduleCombo);
        addFormField(panel, "Note 1", note1Field);
        addFormField(panel, "Note 2", note2Field);
        addFormField(panel, "Note 3", note3Field);
    }

    private void addResultsFields(JPanel panel) {
        addFormField(panel, "Moyenne", averageField);
        addFormField(panel, "Mention", mentionCombo);
    }

    private void calculateAverageAndMention() {
        try {
            // Validate that all required note fields have valid input
            if (note1Field.getText().trim().isEmpty() ||
                    note2Field.getText().trim().isEmpty() ||
                    note3Field.getText().trim().isEmpty()) {
                averageField.setText("");
                mentionCombo.setSelectedItem("Insuffisant");
                return;
            }

            // Parse notes with proper decimal handling
            double note1 = parseNote(note1Field.getText().trim().replace(",", "."));
            double note2 = parseNote(note2Field.getText().trim().replace(",", "."));
            double note3 = parseNote(note3Field.getText().trim().replace(",", "."));

            double average = (note1 + note2 + note3) / 3.0;
            averageField.setText(String.format("%.2f", average));

            // Determine mention based on average
            String mention;
            if (average >= 16) {
                mention = "Très Bien";
            } else if (average >= 14) {
                mention = "Bien";
            } else if (average >= 12) {
                mention = "Assez Bien";
            } else if (average >= 10) {
                mention = "Passable";
            } else {
                mention = "Insuffisant";
            }
            mentionCombo.setSelectedItem(mention);

        } catch (NumberFormatException e) {
            averageField.setText("");
            mentionCombo.setSelectedItem("Insuffisant");
        }
    }

    private double parseNote(String noteText) throws NumberFormatException {
        if (noteText == null || noteText.trim().isEmpty()) {
            throw new NumberFormatException("Note cannot be empty");
        }

        try {
            double note = Double.parseDouble(noteText);
            if (note < 0 || note > 20) {
                throw new NumberFormatException("Note must be between 0 and 20");
            }
            return note;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid note format");
        }
    }

    private JPanel createButtonPanel() {
        JButton submitButton = createStyledButton("Enregistrer", SUCCESS_COLOR);
        submitButton.addActionListener(e -> saveStudent());

        JButton backButton = createStyledButton("Retour", WARNING_COLOR);
        backButton.addActionListener(e -> {
            new studentList();
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void saveStudent() {
        try {
            // Validate required fields
            if (personalFields[0].getText().trim().isEmpty() ||
                    personalFields[1].getText().trim().isEmpty() ||
                    personalFields[2].getText().trim().isEmpty() ||
                    personalFields[3].getText().trim().isEmpty()) {
                showMessage("Veuillez remplir tous les champs obligatoires.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate date format
            java.sql.Date formattedDate = parseDate(personalFields[2].getText());
            if (formattedDate == null) {
                showMessage("Date de naissance invalide. Utilisez le format YYYY-MM-DD.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate notes
            if (note1Field.getText().trim().isEmpty() ||
                    note2Field.getText().trim().isEmpty() ||
                    note3Field.getText().trim().isEmpty()) {
                showMessage("Veuillez entrer toutes les notes.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse notes with proper decimal handling
            double note1 = parseNote(note1Field.getText().trim().replace(",", "."));
            double note2 = parseNote(note2Field.getText().trim().replace(",", "."));
            double note3 = parseNote(note3Field.getText().trim().replace(",", "."));
            double moyenne = Double.parseDouble(averageField.getText().replace(",", "."));

            // Database connection and insertion
            String url = "jdbc:mysql://localhost:3306/java_user_database";
            String query = "INSERT INTO student (nom, prenom, date_naissance, gender, email, telephone, adresse, module, note1, note2, note3, moyenne, mention) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(url, "root", "");
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, personalFields[0].getText());
                pstmt.setString(2, personalFields[1].getText());
                pstmt.setDate(3, formattedDate);
                pstmt.setString(4, genderCombo.getSelectedItem().toString());
                pstmt.setString(5, personalFields[3].getText());
                pstmt.setString(6, personalFields[4].getText());
                pstmt.setString(7, personalFields[5].getText());
                pstmt.setString(8, moduleCombo.getSelectedItem().toString());
                pstmt.setDouble(9, note1);
                pstmt.setDouble(10, note2);
                pstmt.setDouble(11, note3);
                pstmt.setDouble(12, moyenne);
                pstmt.setString(13, mentionCombo.getSelectedItem().toString());

                pstmt.executeUpdate();
                showMessage("Étudiant enregistré avec succès !", JOptionPane.INFORMATION_MESSAGE);

                // Clear form after successful save
                clearForm();
            }
        } catch (SQLException ex) {
            showMessage("Erreur lors de l'enregistrement : " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            showMessage("Veuillez entrer des valeurs numériques valides pour les notes.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        for (JTextField field : personalFields) {
            field.setText("");
        }
        note1Field.setText("");
        note2Field.setText("");
        note3Field.setText("");
        averageField.setText("");
        mentionCombo.setSelectedIndex(0);
        genderCombo.setSelectedIndex(0);
        moduleCombo.setSelectedIndex(0);
    }

    // Utility methods
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                HEADER_FONT,
                PRIMARY_COLOR
        ));
        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(REGULAR_FONT);
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(REGULAR_FONT);
        comboBox.setPreferredSize(new Dimension(300, 35));
        return comboBox;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    private void showMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(
                this,
                message,
                messageType == JOptionPane.ERROR_MESSAGE ? "Erreur" : "Succès",
                messageType
        );
    }

    private java.sql.Date parseDate(String dateInput) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date parsedDate = sdf.parse(dateInput);
            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show GUI on EDT
        SwingUtilities.invokeLater(student::new);
    }
}