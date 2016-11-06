import com.romhackhispano.ktacme.project.ProjectRepos
import org.junit.Assert
import org.junit.Test

class ProjectReposTest {
    @Test
    fun testGetNameFromRemoteUrl() {
        Assert.assertEquals("criminalgirls", ProjectRepos.getNameFromRemoteUrl("git@github.com:talestra/criminalgirls.git"))
        Assert.assertEquals("criminalgirls", ProjectRepos.getNameFromRemoteUrl("https://github.com/talestra/criminalgirls.git"))
        Assert.assertEquals("criminalgirls", ProjectRepos.getNameFromRemoteUrl("git@bitbucket.org:talestra/criminalgirls.git"))
        Assert.assertEquals("criminalgirls", ProjectRepos.getNameFromRemoteUrl("https://bitbucket.org/talestra/criminalgirls.git"))
        Assert.assertEquals("criminalgirls", ProjectRepos.getNameFromRemoteUrl("https://bitbucket.org/talestra/criminalgirls////"))
        Assert.assertEquals("criminalgirls", ProjectRepos.getNameFromRemoteUrl("https://bitbucket.org/talestra/criminalgirls"))
    }
}