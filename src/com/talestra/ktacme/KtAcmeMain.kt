package com.talestra.ktacme

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.CredentialItem
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish
import java.io.File
import javax.swing.JFrame
import javax.swing.UIManager

fun main(args: Array<String>) {
	/*
	File("c:/temp/gittest2").deleteRecursively()

	//GitTools.clone(repo = "git@github.com:soywiz/gittest.git", folder = File("c:/temp/gittest2"))

	val client = GitClient.create(File("c:/temp/gittest2"))

	//val client = GitTools.open(File("c:/temp/gittest2"))

	client.folder["demo.txt"].writeText("HELLO")
	client.add(client.folder["demo.txt"])
	client.commit("lol")
	//client.push("git@github.com:soywiz/gittest.git")
	*/


	val folder = File("C:/projects/criminalgirls/text")
	val project = TranslationProject(folder)

	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
	val mainForm = object : MainForm() {
		var lastSelectedSection: TranslationSection? = null

		init {
			sectionComboBox.addActionListener {
				selectedSection()
			}
			saveButton.addActionListener {
				lastSelectedSection?.save()
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