package com.romhackhispano.ktacme

import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class TextRowComponent : JPanel() {
	private val idLabel = JLabel("ID")
	private val originalTextArea = JTextArea()
	private val translatedTextArea = JTextArea()
	private val translatedCheckBox = JCheckBox("Translated")
	private val reviewedCheckBox = JCheckBox("Reviewed")

	init {
		preferredSize = Dimension(512, 128)
		layout = GridLayout(1, 3, 16, 0)

		add(JPanel().apply {
			add(idLabel.apply { maximumSize = Dimension(128, 128) })
			add(translatedCheckBox)
			add(reviewedCheckBox)
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
				cachedTranslatedText = translatedTextArea.text
				entry.translated = translatedTextArea.text
			}
		})
		translatedCheckBox.addActionListener {
			entry.isTranslated = translatedCheckBox.isSelected
			setTranslated(translatedCheckBox.isSelected)
		}
		reviewedCheckBox.addActionListener {
			entry.isReviewed = reviewedCheckBox.isSelected
			setReviewed(reviewedCheckBox.isSelected)
		}
	}

	private var cachedIdText: String = ""
	private var cachedOriginalText: String = ""
	private var cachedTranslatedText: String = ""

	open class TextEntry(
		open var id: String,
		open var original: String,
		open var translated: String
	) {
		var isTranslated: Boolean = false
		var isReviewed: Boolean = false
	}

	private fun updateColors(translated: Boolean, reviewed: Boolean) {
		val color = if (reviewed) {
			Color(0xDDFFDD)
		} else if (translated) {
			Color(0xDDDDFF)
		} else {
			Color(0xFFFFFF)
		}
		originalTextArea.background = color
		translatedTextArea.background = color
	}

	private fun updateColors() {
		updateColors(translatedCheckBox.isSelected, reviewedCheckBox.isSelected)
	}

	private fun setReviewed(checked: Boolean) {
		reviewedCheckBox.isSelected = checked
		updateColors()
	}

	private fun setTranslated(checked: Boolean) {
		translatedCheckBox.isSelected = checked
		reviewedCheckBox.isEnabled = checked
		updateColors()
	}

	var entry: TextEntry = TextEntry("ID", "ORIGINAL", "TRANSLATED")
		set(value) {
			field = value
			if (cachedIdText != field.id) {
				cachedIdText = field.id
				idLabel.text = field.id
			}
			if (cachedOriginalText != field.original) {
				cachedOriginalText = field.original
				originalTextArea.text = field.original
			}
			if (cachedTranslatedText != field.translated) {
				cachedTranslatedText = field.translated
				translatedTextArea.text = field.translated
			}
			setTranslated(field.isTranslated)
			setReviewed(field.isReviewed)
		}
}