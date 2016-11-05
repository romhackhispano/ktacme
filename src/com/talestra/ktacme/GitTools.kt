package com.talestra.ktacme

import org.eclipse.jgit.api.Git
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
			Git.cloneRepository().setCredentialsProvider(sdcp).setURI(repo).setDirectory(folder).call()
			return open(folder)
		}

		fun create(folder: File): GitClient {
			Git.init().setBare(false).setDirectory(folder).call()
			return open(folder)
		}

		fun open(folder: File) = GitClient(folder, Git(FileRepositoryBuilder().setGitDir(folder[".git"]).build()))
	}

	private var defaultRemote:String = ""
	private var defaultUserName:String = ""
	private var defaultUserEmail:String = ""

	fun setRemote(remote:String):GitClient {
		this.defaultRemote = remote
		return this
	}

	fun setAuthor(userName:String, userEmail:String):GitClient {
		this.defaultUserName = userName;
		this.defaultUserEmail = userEmail;
		return this
	}

	fun add(file: File) {
		git.add().addFilepattern(file.relativeTo(folder).path).setUpdate(true).call()
	}

	fun commit(message: String, userName: String = defaultUserName, userEmail:String = defaultUserEmail) {
		git.commit().setMessage(message).setAuthor(userName, userEmail).call()
	}

	fun pull(remote: String = defaultRemote) {
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
		//git.pull().setRemoteBranchName("master").setRemote(remote).setCredentialsProvider(sdcp).call()
		git.pull().setRemoteBranchName("master").setRemote("origin").setCredentialsProvider(sdcp).call()
	}

	fun push(remote: String = defaultRemote) {
		git.push().setRemote(remote).setCredentialsProvider(sdcp).call()
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

