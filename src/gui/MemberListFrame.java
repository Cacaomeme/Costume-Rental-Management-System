package gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MemberListFrame extends JFrame {

    private JTable memberTable;
    private DefaultTableModel tableModel;
    private FileIO fileIO;

    public MemberListFrame() {
        this.fileIO = FileIO.getInstance();
        setTitle("Member Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize table model
        String[] columnNames = {"Member ID", "Full Name", "Email", "Phone Number", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        memberTable = new JTable(tableModel);
        
        add(new JScrollPane(memberTable), BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel();
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Event listeners
        editButton.addActionListener(e -> editSelectedMember());
        deleteButton.addActionListener(e -> deleteSelectedMember());
        refreshButton.addActionListener(e -> loadMembers());

        loadMembers(); // Initial data load
    }

    private void loadMembers() {
        tableModel.setRowCount(0); // Clear table
        List<FileIO.MemberData> members = fileIO.getAllMembers();
        for (FileIO.MemberData member : members) {
            Object[] row = {
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress()
            };
            tableModel.addRow(row);
        }
    }

    private void editSelectedMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to edit.");
            return;
        }
        String memberId = (String) tableModel.getValueAt(selectedRow, 0);
        FileIO.MemberData member = fileIO.getMemberData(memberId);
        
        if (member != null) {
            // This dialog is now created by the new EditMemberDialog.java file
            EditMemberDialog dialog = new EditMemberDialog(this, member, fileIO);
            dialog.setVisible(true);
            loadMembers(); // Reload data after the dialog is closed
        } else {
            JOptionPane.showMessageDialog(this, "Could not find the selected member's data.");
        }
    }

    private void deleteSelectedMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.");
            return;
        }
        String memberId = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete member ID: " + memberId + "?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (fileIO.deleteMember(memberId)) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully.");
                loadMembers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the member.");
            }
        }
    }
}