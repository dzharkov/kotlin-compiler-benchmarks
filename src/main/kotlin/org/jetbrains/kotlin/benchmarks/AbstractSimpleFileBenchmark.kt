package org.jetbrains.kotlin.benchmarks

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.context.SimpleGlobalContext
import org.jetbrains.kotlin.context.withModule
import org.jetbrains.kotlin.context.withProject
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.JvmBuiltIns
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.storage.ExceptionTracker
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.storage.StorageManager
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.infra.Blackhole
import java.io.File

private fun File.byRelativePath(relative: String) =
        File(this.path + File.separator + relative)

private fun createFile(shortName: String, text: String, project: Project): KtFile {
    val virtualFile = object : LightVirtualFile(shortName, KotlinLanguage.INSTANCE, text) {
        override fun getPath(): String {
            //TODO: patch LightVirtualFile
            return "/" + name
        }
    }

    virtualFile.charset = CharsetToolkit.UTF8_CHARSET
    val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl

    return factory.trySetupPsiForFile(virtualFile, KotlinLanguage.INSTANCE, true, false) as KtFile
}

private val JDK_PATH = File("/opt/java/jdk1.8.0_102/jre/lib/rt.jar")
private val COMPILER_DIR = File("/home/sufix/work/kotlin2")
private val RUNTIME_JAR = COMPILER_DIR.byRelativePath("dist/kotlinc/lib/kotlin-runtime.jar")

private fun newConfiguration(): CompilerConfiguration {
    val configuration = CompilerConfiguration()
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "benchmark")
    configuration.addJvmClasspathRoot(JDK_PATH)
    configuration.addJvmClasspathRoot(RUNTIME_JAR)
    return configuration
}

private val LANGUAGE_FEATURE_SETTINGS =
        LanguageVersionSettingsImpl(
                LanguageVersion.KOTLIN_1_2, ApiVersion.KOTLIN_1_2
        )

abstract class AbstractSimpleFileBenchmark {

    private var myDisposable: Disposable = Disposable { }
    private lateinit var env: KotlinCoreEnvironment
    private lateinit var file: KtFile

    @Setup(Level.Trial)
    fun setUp() {
        env = KotlinCoreEnvironment.createForTests(
                myDisposable, newConfiguration(),
                EnvironmentConfigFiles.JVM_CONFIG_FILES
        )

        file = createFile(
                "test.kt",
                buildText(),
                env.project
        )
    }

    protected fun analyzeGreenFile(bh: Blackhole) {
        val tracker = ExceptionTracker()
        val storageManager: StorageManager =
                LockBasedStorageManager.createWithExceptionHandling(tracker)

        val context = SimpleGlobalContext(storageManager, tracker)
        val module =
                ModuleDescriptorImpl(
                        Name.special("<benchmark>"), storageManager, JvmBuiltIns(storageManager)
                )
        val moduleContext = context.withProject(env.project).withModule(module)

        val result = TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
                moduleContext.project,
                listOf(file),
                CliLightClassGenerationSupport.NoScopeRecordCliBindingTrace(),
                env.configuration,
                { scope -> JvmPackagePartProvider(LANGUAGE_FEATURE_SETTINGS, scope) }
        )

        assert(result.shouldGenerateCode)

        bh.consume(result.shouldGenerateCode)
    }

    protected abstract fun buildText(): String
}
