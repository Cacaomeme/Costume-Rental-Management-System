package gui;

import java.awt.*;
import javax.swing.*;

public class EditMemberDialog extends JDialog {
    private JTextField nameField, emailField, phoneField;
    private JTextArea addressArea;
    private FileIO.MemberData member;
    private FileIO fileIO;

    public EditMemberDialog(Frame owner, FileIO.MemberData member, FileIO fileIO) {
        super(owner, "Edit Member Information", true);
        this.member = member;
        this.fileIO = fileIO;

        setLayout(new GridLayout(5, 2, 10, 10));
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Initialize fields
        nameField = new JTextField(member.getName());
        emailField = new JTextField(member.getEmail());
        phoneField = new JTextField(member.getPhone());
        addressArea = new JTextArea(member.getAddress());
        JScrollPane addressScrollPane = new JScrollPane(addressArea);

        add(new JLabel("Full Name:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Phone Number:"));
        add(phoneField);
        add(new JLabel("Address:"));
        add(addressScrollPane);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveChanges());
        add(saveButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void saveChanges() {
        // Create a new MemberData object with the updated info.
        // ID and password are not changed here.
        FileIO.MemberData updatedData = new FileIO.MemberData(
            nameField.getText(),
            member.getMemberId(), // ID is not changed
            emailField.getText(),
            phoneField.getText(),
            member.getPassword(), // Password is not changed in this dialog
            addressArea.getText(),
            member.getRegistrationDate() // Keep the original registration date
        );

        if (fileIO.updateMember(member.getMemberId(), updatedData)) {
            JOptionPane.showMessageDialog(this, "Member information updated successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update member information.");
        }
    }
}