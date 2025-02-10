package loginandsignup;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.print.*;
import java.sql.*;

public class studentList extends javax.swing.JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public studentList() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Liste des Étudiants");
        setSize(1200, 800);

        // Main panel with gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(240, 245, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header panel setup
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Liste des Étudiants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 149, 255));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Button panel setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Create all buttons
        JButton returnButton = createStyledButton("Retour", new Color(128, 128, 128));
        JButton addButton = createStyledButton("Ajouter Étudiant", new Color(0, 149, 255));
        JButton modifyButton = createStyledButton("Modifier Étudiant", new Color(255, 153, 0));
        JButton deleteButton = createStyledButton("Supprimer Étudiant", new Color(220, 53, 69));
        JButton printButton = createStyledButton("Imprimer", new Color(40, 167, 69));

        // Return button action
        returnButton.addActionListener(e -> {
            Home homeFrame = new Home();
            homeFrame.setVisible(true);
            homeFrame.setLocationRelativeTo(null);
            dispose();
        });

        // Other button actions
        addButton.addActionListener(e -> {
            new student();
            dispose();
        });

        modifyButton.addActionListener(e -> handleModify());
        deleteButton.addActionListener(e -> handleDelete());
        printButton.addActionListener(e -> printTable());

        // Add all buttons
        buttonPanel.add(returnButton);
        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(printButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 250, 255));
                }
                return comp;
            }
        };

        String[] columns = {
                "ID", "Nom", "Prénom", "Date de Naissance", "Genre", "Email",
                "Téléphone", "Adresse", "Module", "Note 1", "Note 2", "Note 3",
                "Moyenne Générale", "Mention"
        };

        for (String column : columns) {
            tableModel.addColumn(column);
        }

        // Table styling
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(0, 149, 255));
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(10, 10));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 149, 255));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Cell renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 149, 255, 30), 1),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        loadTableData();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    // Handle modify student functionality
    private void handleModify() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant à modifier",
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get the ID from the first column
            Object idObject = table.getValueAt(selectedRow, 0);
            int studentId;

            // Convert the ID to integer, handling different possible formats
            if (idObject instanceof Integer) {
                studentId = (Integer) idObject;
            } else {
                studentId = Integer.parseInt(idObject.toString());
            }

            // Debug print
            System.out.println("Opening ModifierStudent with ID: " + studentId);

            // Create and show the ModifierStudent form
            SwingUtilities.invokeLater(() -> {
                ModifierStudent modifierForm = new ModifierStudent(studentId);
                modifierForm.setVisible(true);
                dispose(); // Close the current window
            });

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de format d'ID: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Handle delete student functionality
    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant à supprimer",
                    "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cet étudiant ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String studentId = table.getValueAt(selectedRow, 0).toString();
            deleteStudent(studentId);
        }
    }

    // Delete student from the database
    private void deleteStudent(String studentId) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/java_user_database", "root", "")) {
            String query = "DELETE FROM student WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, studentId);
            int result = pstmt.executeUpdate();

            if (result > 0) {
                tableModel.removeRow(table.getSelectedRow());
                JOptionPane.showMessageDialog(this,
                        "Étudiant supprimé avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load table data from database
    private void loadTableData() {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/java_user_database", "root", "")) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM student");

            while (resultSet.next()) {
                double note1 = resultSet.getDouble("note1");
                double note2 = resultSet.getDouble("note2");
                double note3 = resultSet.getDouble("note3");

                double moyenneGenerale = (note1 + note2 + note3) / 3;

                String mention;
                if (moyenneGenerale >= 16) {
                    mention = "Très Bien";
                } else if (moyenneGenerale >= 14) {
                    mention = "Bien";
                } else if (moyenneGenerale >= 12) {
                    mention = "Assez Bien";
                } else {
                    mention = "Passable";
                }

                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("date_naissance"),
                        resultSet.getString("gender"),
                        resultSet.getString("email"),
                        resultSet.getString("telephone"),
                        resultSet.getString("adresse"),
                        resultSet.getString("module"),
                        resultSet.getString("note1"),
                        resultSet.getString("note2"),
                        resultSet.getString("note3"),
                        moyenneGenerale,
                        mention
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Print the table
    private void printTable() {
    try {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat pageFormat = printerJob.defaultPage();
        
        // Adjust page orientation to landscape if needed
        if (table.getWidth() > table.getHeight()) {
            pageFormat.setOrientation(PageFormat.LANDSCAPE);
        }
        
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pf, int pageIndex) throws PrinterException {
                if (pageIndex >= 1) {
                    return Printable.NO_SUCH_PAGE;
                }
                
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                
                // Scale the table to fit the printable area
                double scaleX = pf.getImageableWidth() / table.getWidth();
                double scaleY = pf.getImageableHeight() / table.getHeight();
                double scale = Math.min(scaleX, scaleY);
                g2d.scale(scale, scale);
                
                table.print(g2d);
                return Printable.PAGE_EXISTS;
            }
        });
        
        boolean userAccepted = printerJob.printDialog();
        if (userAccepted) {
            printerJob.print();
        }
    } catch (PrinterException e) {
        JOptionPane.showMessageDialog(this,
                "Error while printing: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new studentList());
    }
}