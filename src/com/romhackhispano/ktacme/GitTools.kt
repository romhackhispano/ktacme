package com.romhackhispano.ktacme

import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.RefUpdate
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.CredentialItem
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish
import java.io.File

class GitClient(val folder: File, val git: Git) {
    companion object {
        private val sdcp = object : CredentialsProvider() {
            override fun isInteractive(): Boolean = true
            override fun supports(vararg items: CredentialItem): Boolean = true

            override fun get(uri: URIish, vararg items: CredentialItem): Boolean {
                for (item in items) {
                    when (item) {
                        is CredentialItem.StringType -> {
                            val result = PasswordPrompt.showPasswordDialog(item.promptText) ?: return false
                            item.value = result
                            return true
                        }
                    }
                }
                return true
            }
        }

        fun clone(repo: String, folder: File): GitClient {
            Git.cloneRepository()
                    .setCredentialsProvider(sdcp)
                    .setURI(repo)
                    .setDirectory(folder)
                    //.setBranch("HEAD")
                    //.setBranchesToClone(listOf("refs/heads/master"))
                    .setBare(false)
                    .call()
            val client = open(folder)
            //client.setOrigin(repo)
            /*
            client.git
                    .checkout()
                    .setCreateBranch(true)
                    .setName("master")
                    .setForce(true)
                    .setStartPoint("origin/master")
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .call()
                    */
            //client.updateHead("refs/heads/master")
            return client
        }

        fun create(folder: File): GitClient {
            Git.init().setBare(false).setDirectory(folder).call()
            return open(folder)
        }

        fun open(folder: File): GitClient {
            val afolder = folder.absoluteFile
            val client = GitClient(afolder, Git(FileRepositoryBuilder().setGitDir(afolder[".git"]).readEnvironment().findGitDir().build()))
            println("Opened git: " + afolder)
            println("Opened git repository: " + client.git.repository.directory)
            return client
        }
    }

    private var defaultRemote: String? = null
    private var defaultUserName: String = ""
    private var defaultUserEmail: String = ""

    fun setRemote(remote: String?): GitClient {
        this.defaultRemote = remote
        return this
    }

    fun setAuthor(userName: String, userEmail: String): GitClient {
        this.defaultUserName = userName;
        this.defaultUserEmail = userEmail;
        return this
    }

    fun add(file: File) {
        git.add().addFilepattern(file.absoluteFile.relativeTo(folder.absoluteFile).path).setUpdate(true).call()
    }

    fun commit(message: String, userName: String = defaultUserName, userEmail: String = defaultUserEmail) {
        //updateHead("refs/for/master")
        git.commit().setMessage(message).setAuthor(userName, userEmail).call()
    }

    fun updateHead(newHead: String, force: Boolean = true, detach: Boolean = false): Boolean {
        val repo = this.git.repository
        val refUpdate = repo.refDatabase.newUpdate(Constants.HEAD, detach)
        refUpdate.isForceUpdate = force
        return refUpdate.link(newHead) != RefUpdate.Result.REJECTED
    }

    fun setOrigin(remote: String) {
        if (git.remoteList().call().firstOrNull { it.name == "origin" } == null) {
            val remoteUrl = git.remoteAdd()
            remoteUrl.setName("origin")
            remoteUrl.setUri(URIish(remote))
            remoteUrl.call()
        } else {
            val remoteUrl = git.remoteSetUrl()
            remoteUrl.setName("origin")
            remoteUrl.setUri(URIish(remote))
            remoteUrl.call()
        }
    }

    fun pull(remote: String? = defaultRemote) {
        if (remote != null) setOrigin(remote)
        //git.pull().setRemoteBranchName("master").setRemote(remote).setCredentialsProvider(sdcp).call()
        git.pull().setRemoteBranchName("master").setRemote("origin").setCredentialsProvider(sdcp).call()
    }

    fun push(remote: String? = defaultRemote) {
        if (remote != null) setOrigin(remote)
        git.push().setRemote("origin").setCredentialsProvider(sdcp).call()
    }

    fun hasChanges(): Boolean {
        return !isClean();
    }

    fun isClean(): Boolean {
        //val status = git.status().addPath("/").call()
        val status = git.status().call()
        return status.isClean
        //println(status)
    }
}

