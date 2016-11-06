package com.romhackhispano.ktacme

import com.apple.eawt.Application
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.JFrame
import javax.swing.UIManager

fun main(args: Array<String>) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    //val folder = File("C:/projects/criminalgirls/text")
    val gitFolder = File("/Users/soywiz/Projects/criminalgirls")
    val folder = File("/Users/soywiz/Projects/criminalgirls/text")
    var project = TranslationProject(folder)

    val client = GitClient.open(gitFolder).setAuthor("soywiz", "soywiz@gmail.com").setRemote("git@github.com:talestra/criminalgirls.git")
    //client.pull()

    val mainForm = object : MainForm() {
        var lastSelectedSection: TranslationSection? = null

        init {
            sectionComboBox.addActionListener {
                selectedSection()
            }
            saveButton.addActionListener {
                lastSelectedSection?.save()
            }
            syncButton.addActionListener {
                lastSelectedSection?.save()
                for (section in project.sections.filterIsInstance<FileTranslationSection>()) {
                    client.add(section.file)
                }
                val mustCommit = client.hasChanges()
                if (mustCommit) {
                    client.commit("More work")
                }
                client.pull()
                project = TranslationProject(folder)
                if (mustCommit) {
                    client.push()
                }
            }
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    lastSelectedSection?.save()
                    super.windowClosing(e)
                }
            })
            Application.getApplication().setQuitHandler { quitEvent, quitResponse ->
                lastSelectedSection?.save()
                quitResponse.performQuit()
            }
        }

        fun selectedSection() {
            if (lastSelectedSection != null) {
                lastSelectedSection?.save()
            }
            lastSelectedSection = sectionComboBox.selectedItem as TranslationSection?
            if (lastSelectedSection != null) {
                setSection(lastSelectedSection!!)
            }
        }

        fun setSection(section: TranslationSection) {
            this.textRowsComponent.repository = section.repository
        }

        fun setProject(project: TranslationProject) {
            sectionComboBox.removeAllItems()
            for (section in project.sections) {
                sectionComboBox.addItem(section)
            }
            if (sectionComboBox.itemCount >= 1) {
                sectionComboBox.selectedIndex = 0
            }
            selectedSection()
        }
    }

    mainForm.setProject(project)

    mainForm.textRowsComponent.repository = TextRepository()
    mainForm.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    mainForm.pack()
    mainForm.setLocationRelativeTo(null)
    mainForm.isVisible = true
    //com.talestra.ktacme/TextRowComponent
}