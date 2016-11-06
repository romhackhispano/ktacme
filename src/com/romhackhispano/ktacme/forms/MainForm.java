package com.romhackhispano.ktacme.forms;

import com.romhackhispano.ktacme.TextRowsComponent;

import javax.swing.*;

public class MainForm extends JFrame {
    private JPanel contentPane;
    public JComboBox sectionComboBox;
    public JButton saveButton;
    public TextRowsComponent textRowsComponent;
    public JButton syncButton;
    public JButton buttonAddProject;
    public JComboBox projectComboBox;

    public MainForm() {
        setContentPane(contentPane);
    }
}
