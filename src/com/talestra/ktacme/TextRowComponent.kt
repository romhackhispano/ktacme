package com.talestra.ktacme

import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class TextRowComponent : JPanel() {
	private val idLabel = JLabel("ID")
	private val originalTextArea = JTextArea()
	private val translatedTextArea = JTextArea()

	init {
		preferredSize = Dimension(512, 128)
		layout = GridLayout(1, 3, 16, 0)

		add(JPanel().apply {
			add(idLabel.apply { maximumSize = Dimension(128, 128) })
		})

		originalTextArea.isEditable = false
		add(JScrollPane(originalTextArea).apply { removeMouseWheelListener(mouseWheelListeners[0]) })
		add(JScrollPane(translatedTextArea).apply { removeMouseWheelListener(mouseWheelListeners[0]) })
		//add(originalTextArea)
		//add(translatedTextArea)

		translatedTextArea.document.addDocumentListener(object : DocumentListener {
			override fun removeUpdate(e: DocumentEvent) = update()
			override fun insertUpdate(e: DocumentEvent) = update()
			override fun changedUpdate(e: DocumentEvent) = update()

			private fun update() {
				entry.translated = translatedTextArea.text
			}
		})
	}

	open class TextEntry(
		open var id: String,
		open var original: String,
		open var translated: String
	)

	var entry: TextEntry = TextEntry("ID", "ORIGINAL", "TRANSLATED")
		set(value) {
			field = value
			idLabel.text = field.id
			originalTextArea.text = field.original
			translatedTextArea.text = field.translated
		}
}