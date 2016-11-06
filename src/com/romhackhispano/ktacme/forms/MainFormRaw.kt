package com.romhackhispano.ktacme.forms

import com.romhackhispano.ktacme.TextRowsComponent
import com.romhackhispano.ktacme.TranslationSection
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame

open class MainFormRaw : JFrame() {
    lateinit var saveButton: JButton
    lateinit var syncButton: JButton
    lateinit var projectComboBox: JComboBox<String>
    lateinit var sectionComboBox: JComboBox<TranslationSection>
    lateinit var textRowsComponent: TextRowsComponent

    init {
        verticalPanel {
            horizontalPanel {
                projectComboBox = comboBox()
                sectionComboBox = comboBox()
                saveButton = button("Save")
                syncButton = button("Sync")
            }
            horizontalPanel {
                textRowsComponent = TextRowsComponent()
                add(textRowsComponent, BorderLayout.PAGE_END)
            }
        }
    }
}