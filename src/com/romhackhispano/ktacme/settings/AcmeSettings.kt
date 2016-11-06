package com.romhackhispano.ktacme.settings

import com.romhackhispano.ktacme.serialization.Props
import java.io.File

class AcmeSettings {
    var gitUserName: String = ""
    var gitEmail: String = ""

    fun hasValidGitConfig(): Boolean {
        return gitUserName.isNotEmpty() && gitEmail.isNotEmpty()
    }
}

object AcmeSettingsStorage {
    private val settingsFile = File("ktacme.properties")
    var settings = AcmeSettings()

    init {
        load()
    }

    fun load() {
        try {
            if (settingsFile.exists()) {
                settings = Props.parseTyped<AcmeSettings>(settingsFile.readText())
            } else {
                save()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            settingsFile.writeText(Props.stringify(settings))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}


/*
class AcmeSettings {
    @JvmField var projects: ArrayList<String> = arrayListOf()
}
*/