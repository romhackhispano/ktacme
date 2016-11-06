package com.romhackhispano.ktacme

import com.apple.eawt.Application
import com.romhackhispano.ktacme.forms.GitSettingsDialogRaw
import com.romhackhispano.ktacme.forms.MainFormRaw
import com.romhackhispano.ktacme.forms.click
import com.romhackhispano.ktacme.forms.showDialog
import com.romhackhispano.ktacme.project.ProjectRepo
import com.romhackhispano.ktacme.project.ProjectRepos
import com.romhackhispano.ktacme.settings.AcmeSettingsStorage
import java.awt.Window
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

object KtAcmeMain {
    @JvmStatic fun main(args: Array<String>) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        //val client = GitClient.open(gitFolder).setAuthor("soywiz", "soywiz@gmail.com").setRemote("git@github.com:talestra/criminalgirls.git")
        //client.pull()


        //GitSettingsDialogExt().showDialog()

        val mainForm = MainFormExt()
        mainForm.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainForm.pack()
        mainForm.setLocationRelativeTo(null)
        mainForm.isVisible = true
    }
}

inline fun complementCatch(message: String, callback: () -> Unit) {
    try {
        callback()
    } catch (e: Throwable) {
        throw java.lang.RuntimeException(message + " : " + e.message, e)
    }
}

inline fun catchShowDialog(message: String = "Error", callback: () -> Unit) {
    try {
        callback()
    } catch (e: Throwable) {
        JOptionPane.showMessageDialog(null, "${e.message}\n\n${e.rootCause.stackTrace.take(8).joinToString("\n")}", message, JOptionPane.ERROR_MESSAGE)
        throw e
    }
}

val Throwable.rootCause: Throwable get() = this.cause?.rootCause ?: this

class MainFormExt : MainFormRaw() {
    var projectRepo: ProjectRepo? = null
    var project: TranslationProject? = null
        set(value) {
            field = value
            sectionComboBox.removeAllItems()
            for (section in project!!.sections) {
                sectionComboBox.addItem(section)
            }
            if (sectionComboBox.itemCount > 0) {
                sectionComboBox.selectedIndex = 0
            }
        }
    var lastSelectedSection: TranslationSection? = null

    private fun reloadProjectList() {
        this.projectComboBox.removeAllItems()
        for (projectName in ProjectRepos.listNames()) {
            this.projectComboBox.addItem(projectName)
        }
        if (this.projectComboBox.itemCount > 0) {
            this.projectComboBox.selectedIndex = 0
        }
    }

    fun addProjectAction() {
        catchShowDialog {
            val remoteUrl = JOptionPane.showInputDialog("Git Repository URL")
            if (!remoteUrl.isNullOrBlank()) {
                ProjectRepos.create(remoteUrl)
                reloadProjectList()
            }
        }
    }

    init {
        title = "ktacme - 0.1"
        this.jMenuBar = JMenuBar().apply {
            add(JMenu("File").apply {
                add(JMenuItem("Add project...").apply {
                    addActionListener {
                        addProjectAction()
                    }
                })
            })
            add(JMenu("Settings").apply {
                add(JMenuItem("Git Settings...").apply {
                    addActionListener {
                        GitSettingsDialogExt().showDialog()
                    }
                })
            })
        }

        this.projectComboBox.addActionListener {
            val projectRepo = ProjectRepos.getProject(this.projectComboBox.selectedItem.toString())
            this.projectRepo = projectRepo
            this.project = TranslationProject(projectRepo.textFolder)
        }

        //this.buttonAddProject.click { addProjectAction() }

        sectionComboBox.addActionListener {
            selectedSection()
        }
        saveButton.click {
            lastSelectedSection?.save()
        }
        syncButton.click {
            catchShowDialog {
                val projectRepo = projectRepo ?: invalidOp("No project selected")
                val project = project ?: invalidOp("No project selected")
                val settings = AcmeSettingsStorage.settings
                //val gitUserName = AcmeSettingsStorage.settings.gitUserName
                //val gitEmail = AcmeSettingsStorage.settings.gitEmail
                if (!settings.hasValidGitConfig()) GitSettingsDialogExt().showDialog()
                if (!settings.hasValidGitConfig()) invalidOp("No valid git configuration")

                val client = GitClient.open(projectRepo.folder).setAuthor(settings.gitUserName, settings.gitEmail)
                //val client = client ?: invalidOp("Internal Error: No GIT client configured")
                lastSelectedSection?.save()
                for (section in project.sections.filterIsInstance<FileTranslationSection>()) {
                    client.add(section.file)
                }
                val mustCommit = client.hasChanges()
                if (mustCommit) {
                    complementCatch("Error while Commiting (${client.folder})") {
                        client.commit("More work")
                    }
                }
                complementCatch("Error while Pulling") {
                    client.pull()
                }
                this.project = TranslationProject(project.folder)
                if (mustCommit) {
                    complementCatch("Error while Pushing") {
                        client.push()
                    }
                }
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

        reloadProjectList()
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
        //println("--------------------------")
        //println(section.texts)
        this.textRowsComponent.repository = section.repository
    }
}

class GitSettingsDialogExt : GitSettingsDialogRaw() {
    init {
        title = "Git settings"
        nameTextField.text = AcmeSettingsStorage.settings.gitUserName
        emailTextField.text = AcmeSettingsStorage.settings.gitEmail
    }

    override fun onOK() {
        catchShowDialog {
            if (nameTextField.text.isEmpty()) invalidOp("Username can't be empty")
            if (emailTextField.text.isEmpty()) invalidOp("Email can't be empty")
            AcmeSettingsStorage.settings.gitUserName = nameTextField.text
            AcmeSettingsStorage.settings.gitEmail = emailTextField.text
            AcmeSettingsStorage.save()
            super.onOK()
        }
    }
}
