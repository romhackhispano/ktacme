package com.romhackhispano.ktacme

import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JPasswordField

object PasswordPrompt {
	fun showPasswordDialog(msg: String): String? {
		val panel = JPanel()
		val passwordField = JPasswordField(10)
		panel.add(JLabel("Password"))
		panel.add(passwordField)
		val pane = object : JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
			override fun selectInitialValue() {
				passwordField.requestFocusInWindow()
			}
		}
		val dialog = pane.createDialog(null, msg)
		dialog.isVisible = true
		return if (pane.value == JOptionPane.OK_OPTION) String(passwordField.password) else null
	}
}