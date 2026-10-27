
package com.attendance;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Teacher dashboard: shows today's total, present and absent students. */
public class DashboardWindow extends JFrame {

    private final DashboardDAO dao = new DashboardDAO();

    private final JLabel totalLabel = new JLabel("0");
    private final JLabel presentLabel = new JLabel("0");
    private final JLabel absentLabel = new JLabel("0");
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel presentModel = createModel("Roll No", "Name", "Time");
    private final DefaultTableModel absentModel = createModel("Roll No", "Name");

    public DashboardWindow() {
        setTitle("Attendance Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);   // closing this window keeps the menu running
        setSize(950, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 15));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        refresh();

        // Auto refresh every 10 seconds
        new Timer(10000, e -> refresh()).start();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 41, 59));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Attendance Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel date = new JLabel("Date: " + LocalDate.now());
        date.setForeground(new Color(203, 213, 225));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        header.add(title, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Three cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 0));
        cards.add(createCard("Total Students", totalLabel, new Color(59, 130, 246)));
        cards.add(createCard("Present Today", presentLabel, new Color(34, 197, 94)));
        cards.add(createCard("Absent Today", absentLabel, new Color(239, 68, 68)));

        // Two tables
        JPanel tables = new JPanel(new GridLayout(1, 2, 15, 0));
        tables.add(createTablePanel("Present", presentModel));
        tables.add(createTablePanel("Absent", absentModel));

        center.add(cards, BorderLayout.NORTH);
        center.add(tables, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(refreshButton, BorderLayout.EAST);
        return footer;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 44));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTablePanel(String title, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    /** Table model where cells cannot be edited. */
    private DefaultTableModel createModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Reads fresh data from MySQL and updates the screen. */
    private void refresh() {
        int total = dao.countStudents();
        List<String[]> present = dao.getPresentToday();
        List<String[]> absent = dao.getAbsentToday();

        totalLabel.setText(String.valueOf(total));
        presentLabel.setText(String.valueOf(present.size()));
        absentLabel.setText(String.valueOf(absent.size()));

        presentModel.setRowCount(0);
        for (String[] row : present) presentModel.addRow(row);

        absentModel.setRowCount(0);
        for (String[] row : absent) absentModel.addRow(row);

        statusLabel.setText("Last updated: " + LocalTime.now().withNano(0));
    }

    /** Call this to open the dashboard window. */
    public static void open() {
        FlatLightLaf.setup();   // modern look
        SwingUtilities.invokeLater(() -> new DashboardWindow().setVisible(true));
    }
}