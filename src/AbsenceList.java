package loginandsignup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class AbsenceList extends JFrame {
   private JTable absenceTable;
    private DefaultTableModel tableModel;
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    public AbsenceList() {
        setTitle("Liste D'absence");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Initialize components
        initializeComponents();
        loadAbsences();
        setVisible(true);
    }

    private void initializeComponents() {
        // Top Panel with gradient background
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table Panel
        setupTable();
        JScrollPane scrollPane = createTableScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        // Action buttons panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, 
                    new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 200));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setLayout(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(getWidth(), 100));
        topPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Gestion des Absences");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
       

      JButton returnButton = createStyledButton("Retour", new Color(128, 128, 128), "return");
    returnButton.addActionListener(e -> {
        Home homeFrame = new Home();
        homeFrame.setVisible(true);
        homeFrame.setLocationRelativeTo(null);
        dispose(); // Ferme la fenêtre actuelle
    });
           // Création d'un sous-panneau pour le titre et le bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(returnButton, BorderLayout.EAST);
        
        topPanel.add(headerPanel, BorderLayout.CENTER);

        return topPanel;
    }

    private void setupTable() {
        String[] columnNames = {"ID", "Nom", "Prénom", "Module", "Date", "Présence", "Motif", "Heures Absence", "Justifiée", "Commentaires"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        absenceTable = new JTable(tableModel);
        absenceTable.setFont(TABLE_FONT);
        absenceTable.setRowHeight(35);
        absenceTable.setShowGrid(true);
        absenceTable.setGridColor(new Color(230, 230, 230));
        absenceTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50));
        absenceTable.setSelectionForeground(Color.BLACK);

        // Style the header
        JTableHeader header = absenceTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
    }

    private JScrollPane createTableScrollPane() {
        JScrollPane scrollPane = new JScrollPane(absenceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton addButton = createStyledButton("Ajouter Absence", SUCCESS_COLOR, "add");
        JButton modifyButton = createStyledButton("Modifier Absence", WARNING_COLOR, "edit");
        JButton deleteButton = createStyledButton("Supprimer Absence", DANGER_COLOR, "delete");

        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color color, String action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(getModel().isPressed() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(180, 45));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        // Add action listeners
        switch (action) {
            case "add":
                button.addActionListener(e -> openAbsenceForm());
                break;
            case "edit":
                button.addActionListener(e -> modifySelectedAbsence());
                break;
            case "delete":
                button.addActionListener(e -> deleteSelectedAbsence());
                break;
        }

        return button;
    }

    private void openAbsenceForm() {
        // Your existing openAbsenceForm implementation
        new AbsenceForm();
    }

    private JDialog createStyledDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(245, 245, 245));
        dialog.setLayout(new BorderLayout(15, 15));
        return dialog;
    }

    private void modifySelectedAbsence() {
        int selectedRow = absenceTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une absence à modifier.");
            return;
        }

        JDialog dialog = createStyledDialog("Modifier Absence");
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Get current values
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String[] currentValues = {
            (String) tableModel.getValueAt(selectedRow, 3), // module
            tableModel.getValueAt(selectedRow, 4).toString(), // date
            (String) tableModel.getValueAt(selectedRow, 5), // presence
            (String) tableModel.getValueAt(selectedRow, 6), // motif
            String.valueOf(tableModel.getValueAt(selectedRow, 7)), // heures absence
            (String) tableModel.getValueAt(selectedRow, 8), // justifiee
            (String) tableModel.getValueAt(selectedRow, 9)  // commentaires
        };

        // Create form fields
        String[] labels = {"Module:", "Date:", "Présence:", "Motif:", "Heures Absence:", "Justifiée:", "Commentaires:"};
        JComponent[] fields = createFormFields(currentValues);
        
        // Add fields to form
        for (int i = 0; i < labels.length; i++) {
            addFormField(formPanel, gbc, labels[i], fields[i], i);
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        JButton saveButton = createStyledButton("Enregistrer", SUCCESS_COLOR, "");
        JButton cancelButton = createStyledButton("Annuler", DANGER_COLOR, "");

        saveButton.addActionListener(e -> {
            updateAbsence(id, fields, selectedRow);
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JComponent[] createFormFields(String[] currentValues) {
        JTextField moduleField = createStyledTextField(currentValues[0]);
        JTextField dateField = createStyledTextField(currentValues[1]);
        JComboBox<String> presenceComboBox = createStyledComboBox(new String[]{"Présent", "Absent"}, currentValues[2]);
        JTextField motifField = createStyledTextField(currentValues[3]);
        JTextField heuresField = createStyledTextField(currentValues[4]);
        JComboBox<String> justifieeComboBox = createStyledComboBox(new String[]{"Oui", "Non"}, currentValues[5]);
        JTextArea commentairesArea = createStyledTextArea(currentValues[6]);

        return new JComponent[]{moduleField, dateField, presenceComboBox, motifField, 
                              heuresField, justifieeComboBox, commentairesArea};
    }

    private JTextField createStyledTextField(String initialValue) {
        JTextField field = new JTextField(initialValue);
        field.setFont(TABLE_FONT);
        field.setPreferredSize(new Dimension(200, 35));
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items, String selectedItem) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setSelectedItem(selectedItem);
        comboBox.setFont(TABLE_FONT);
        comboBox.setPreferredSize(new Dimension(200, 35));
        return comboBox;
    }

    private JTextArea createStyledTextArea(String initialValue) {
        JTextArea area = new JTextArea(initialValue, 3, 20);
        area.setFont(TABLE_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0.0;
    }

    private void updateAbsence(int id, JComponent[] fields, int selectedRow) {
        String module = ((JTextField) fields[0]).getText();
        String date = ((JTextField) fields[1]).getText();
        String presence = (String) ((JComboBox<?>) fields[2]).getSelectedItem();
        String motif = ((JTextField) fields[3]).getText();
        int heuresAbsence = Integer.parseInt(((JTextField) fields[4]).getText());
        String justifiee = (String) ((JComboBox<?>) fields[5]).getSelectedItem();
        String commentaires = ((JTextArea) fields[6]).getText();

        updateAbsenceInDatabase(id, module, date, presence, motif, heuresAbsence, justifiee, commentaires);
        updateTableRow(selectedRow, module, date, presence, motif, heuresAbsence, justifiee, commentaires);
        showSuccessMessage("Absence modifiée avec succès !");
    }

    private void deleteSelectedAbsence() {
        int selectedRow = absenceTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Veuillez sélectionner une absence à supprimer.");
            return;
        }

        if (showConfirmDialog("Êtes-vous sûr de vouloir supprimer cette absence ?")) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            deleteAbsenceFromDatabase(id);
            tableModel.removeRow(selectedRow);
            showSuccessMessage("Absence supprimée avec succès.");
        }
    }

    private void updateTableRow(int row, String module, String date, String presence, 
                              String motif, int heuresAbsence, String justifiee, String commentaires) {
        tableModel.setValueAt(module, row, 3);
        tableModel.setValueAt(date, row, 4);
        tableModel.setValueAt(presence, row, 5);
        tableModel.setValueAt(motif, row, 6);
        tableModel.setValueAt(heuresAbsence, row, 7);
        tableModel.setValueAt(justifiee, row, 8);
        tableModel.setValueAt(commentaires, row, 9);
    }
    
 private void showSuccessMessage(String message) {
     JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private boolean showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirmation", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void loadAbsences() {
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String user = "root";
        String password = "";
        String query = "SELECT * FROM AbsenceList";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("id"));
                row.add(resultSet.getString("nom"));
                row.add(resultSet.getString("prenom"));
                row.add(resultSet.getString("module"));
                row.add(resultSet.getDate("date"));
                row.add(resultSet.getString("presence"));
                row.add(resultSet.getString("motif"));
                row.add(resultSet.getInt("absence_heures"));
                row.add(resultSet.getString("justifiee"));
                row.add(resultSet.getString("commentaires"));

                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            showErrorMessage("Erreur lors du chargement des absences : " + e.getMessage());
        }
    }

    private void deleteAbsenceFromDatabase(int id) {
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String user = "root";
        String password = "";
        String query = "DELETE FROM AbsenceList WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            showErrorMessage("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void updateAbsenceInDatabase(int id, String module, String date, String presence, 
                                       String motif, int heuresAbsence, String justifiee, String commentaires) {
        String url = "jdbc:mysql://localhost:3306/java_user_database";
        String user = "root";
        String password = "";
        String query = "UPDATE AbsenceList SET module = ?, date = ?, presence = ?, motif = ?, " +
                      "absence_heures = ?, justifiee = ?, commentaires = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, module);
            preparedStatement.setString(2, date);
            preparedStatement.setString(3, presence);
            preparedStatement.setString(4, motif);
            preparedStatement.setInt(5, heuresAbsence);
            preparedStatement.setString(6, justifiee);
            preparedStatement.setString(7, commentaires);
            preparedStatement.setInt(8, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            showErrorMessage("Erreur lors de la modification : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Set System Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(AbsenceList::new);
    }
}