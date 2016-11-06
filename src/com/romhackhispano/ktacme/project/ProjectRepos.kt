package com.romhackhispano.ktacme.project

import com.romhackhispano.ktacme.GitClient
import com.romhackhispano.ktacme.get
import com.romhackhispano.ktacme.invalidOp
import com.romhackhispano.ktacme.serialization.Props
import java.io.File

object ProjectRepos {
    val root = File("ktacme-repos")

    init {
        root.mkdirs()
    }

    // https://github.com/talestra/criminalgirls.git
    // git@github.com:talestra/criminalgirls.git
    fun getNameFromRemoteUrl(remote: String): String {
        return File(Regex("(\\w+)\\W*(\\.git)?\\W*$").find(remote)?.groups?.get(1)?.value ?: remote).name
    }

    fun create(remote: String) {
        val name = getNameFromRemoteUrl(remote)
        if (name.isNullOrBlank()) invalidOp("Name can't be null or blank: '$name'")
        val folder = getProject(name).folder
        if (folder.exists()) invalidOp("Folder '$name' already exists")
        try {
            GitClient.clone(remote, folder)
        } catch (e: Throwable) {
            if (folder.exists()) {
                folder.deleteRecursively()
                throw e
            }
        }
    }

    fun getProject(name: String) = ProjectRepo(root[name])

    fun listNames(): List<String> {
        return root.listFiles().filter { it.isDirectory }.map { it.name }
    }
}

class ProjectRepo(val folder: File) {
    val name = folder.name
    val settings by lazy {
        val projectKtacmeFile = folder["project.ktacme"]
        val settings = if (projectKtacmeFile.exists()) {
            Props.deserializeTyped<ProjectSettings>(projectKtacmeFile.readText())
        } else {
            ProjectSettings().apply {
                this.folder = "."
                this.format = "auto"
            }
        }
        if (settings.name.isEmpty()) settings.name = name
        settings
    }
    val textFolder by lazy { folder[settings.folder] }
}

class ProjectSettings {
    var name: String = ""
    var folder: String = ""
    var format: String = "auto"
}