package com.romhackhispano.ktacme.forms

import java.awt.Frame
import javax.swing.*

//open class GitSettingsDialogRaw : JDialog(null as Frame?, true) {
open class GitSettingsDialogRaw : JDialog() {
    lateinit var nameTextField: JTextField
    lateinit var emailTextField: JTextField
    lateinit var okButton: JButton
    lateinit var cancelButton: JButton

    init {
        verticalPanel {
            grid(2, 2) {
                label("Name:")
                nameTextField = textField()
                label("E-Mail:")
                emailTextField = textField()
            }

            horizontalPanel {
                okButton = button("Ok").click { onOK() }.makeDefault()
                cancelButton = button("Cancel").click { onCancel() }
            }
        }

    }

    var saved: Boolean = false

    open fun onOK() {
        saved = true
        this.dispose()
    }

    open fun onCancel() {
        saved = false
        this.dispose()
    }
}