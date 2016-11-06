package com.romhackhispano.ktacme.forms;

import javax.swing.*;
import java.awt.event.*;

public class GitSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JTextField userNameTextField;
    public JTextField emailTextField;

    public GitSettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        saved = false;
    }

    public boolean saved;

    protected void onOK() {
// add your code here
        saved = true;
        dispose();
    }

    protected void onCancel() {
        saved = false;
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        GitSettingsDialog dialog = new GitSettingsDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
