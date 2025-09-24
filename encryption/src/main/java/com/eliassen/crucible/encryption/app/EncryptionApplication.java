package com.eliassen.crucible.encryption.app;

import com.eliassen.crucible.encryption.EncryptionHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EncryptionApplication extends JFrame {
    public static void main(String[] args) {
        EncryptionApplication app = new EncryptionApplication();
    }

    public EncryptionApplication() {
        setTitle("Encryption App");

        JPanel panel = new JPanel();

        JLabel inputLabel = new JLabel("input");
        JLabel outputLabel = new JLabel("output");

        JTextField inputField = new JTextField(20);
        JTextField outputField = new JTextField(20);
        outputField.setEnabled(false);

        JButton encryptButton = new JButton("encrypt");
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputValue = inputField.getText();
                String encryptedValue = EncryptionHelper.encryptString(inputValue);

                outputField.setText(encryptedValue);
            }
        });

        JButton decryptButton = new JButton("decrypt");
        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputValue = inputField.getText();
                String decryptedValue = EncryptionHelper.decryptString(inputValue);

                outputField.setText(decryptedValue);
            }
        });

        JButton clearButton = new JButton("clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputField.setText("");
                inputField.setText("");
            }
        });

        JButton copyOutputButton = new JButton("copy output");
        copyOutputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(outputField.getText());
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
            }
        });

        panel.add(inputLabel);
        panel.add(inputField);
        panel.add(outputLabel);
        panel.add(outputField);
        panel.add(encryptButton);
        panel.add(decryptButton);
        panel.add(clearButton);
        panel.add(copyOutputButton);

        add(panel);
        pack();
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

}
