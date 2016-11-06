package com.romhackhispano.ktacme;

import com.talestra.ktacme.TextRowsComponent;

import javax.swing.*;

public class MainForm extends JFrame {
    private JPanel contentPane;
    public JComboBox sectionComboBox;
    public JButton saveButton;
    public TextRowsComponent textRowsComponent;
    public JButton syncButton;

    public MainForm() {
        setContentPane(contentPane);
    }
}
