package liveplugin.pluginrunner

import com.intellij.openapi.util.io.FileUtil
import liveplugin.pluginrunner.kotlin.KotlinPluginRunner
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static liveplugin.pluginrunner.AnError.*
import static liveplugin.pluginrunner.GroovyPluginRunnerTest.*
import static liveplugin.pluginrunner.Result.*

// Ignore for now, because it's hard to setup KotlinPluginRunner classloaders and load EmbeddedCompilerRunnerKt.
@Ignore
class KotlinPluginRunnerTest {
	private final pluginRunner = new KotlinPluginRunner(emptyEnvironment)
	private File rootFolder
	private File libPackageFolder

	@Test void "minimal kotlin script"() {
		def scriptCode = "println(123)"
		createFile("plugin.kts", scriptCode, rootFolder)

		def result = pluginRunner.runPlugin(rootFolder.absolutePath, "someId", noBindings, runOnTheSameThread)

		assert result instanceof Success
	}

	@Test void "kotlin script which uses IJ API"() {
		def scriptCode = "println(com.intellij.openapi.project.Project::class.java)"
		createFile("plugin.kts", scriptCode, rootFolder)

		def result = pluginRunner.runPlugin(rootFolder.absolutePath, "someId", noBindings, runOnTheSameThread)

		assert result instanceof Success
	}
	
	@Test void "kotlin script which uses function from another file"() {
		def scriptCode = """
			import lib.libFunction
			println(libFunction())
		"""
		def libScriptCode = """
			package lib
			fun libFunction(): Long = 42
		"""
		createFile("plugin.kts", scriptCode, rootFolder)
		createFile("lib.kt", libScriptCode, libPackageFolder)

		def result = pluginRunner.runPlugin(rootFolder.absolutePath, "someId", noBindings, runOnTheSameThread)

		assert result instanceof Success
	}

	@Test void "kotlin script with errors"() {
		def scriptCode = "abc"
		createFile("plugin.kts", scriptCode, rootFolder)

		def result = pluginRunner.runPlugin(rootFolder.absolutePath, "someId", noBindings, runOnTheSameThread)

		assert result instanceof Failure
		assert (result.reason as LoadingError).pluginId == "someId"
		assert (result.reason as LoadingError).throwable.toString().contains("unresolved reference: abc")
	}

	@Before void setup() {
		rootFolder = FileUtil.createTempDirectory("", "")
		libPackageFolder = new File(rootFolder, "lib")
		libPackageFolder.mkdir()
	}

	@After void teardown() {
		FileUtil.delete(rootFolder)
	}
}
