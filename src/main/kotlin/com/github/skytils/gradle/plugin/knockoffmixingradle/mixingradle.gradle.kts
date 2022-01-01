import com.github.skytils.gradle.plugin.knockoffmixingradle.KnockoffMixinGradleExtension


val extension = project.extensions.create<KnockoffMixinGradleExtension>("mixin")

with(extension) {
    repositories {
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
    val annotationProcessor by configurations.named("annotationProcessor")
    val compileOnly by configurations.named("compileOnly")
    dependencies {
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
        compileOnly("org.spongepowered:mixin:0.8.5")
    }
    val mixinSrg = File(project.buildDir, "tmp/mixins/mixins.srg")
    val mixinRefMap = File(project.buildDir, "tmp/mixins/${refmapName}")
    project.extra.set("mixinSrg", mixinSrg)
    project.extra.set("mixinRefMap", mixinRefMap)

    tasks {
        val genSrgs = named("genSrgs").get()
        val copySrg = register<Copy>("copySrg") {
            from(genSrgs::class.java.getDeclaredMethod("getMcpToSrg").invoke(genSrgs))
            into("build")
        }
        withType<Jar> {
            from(mixinRefMap)
        }
        withType<JavaCompile> {
            options.compilerArgs.addAll(arrayOf(
                "-AoutSrgFile=${mixinSrg.canonicalPath}",
                "-AoutRefMapFile=${mixinRefMap.canonicalPath}",
                "-AreobfSrgFile=${project.file("build/mcp-srg.srg").canonicalPath}"
            ))
            dependsOn(copySrg.get())
        }
        @Suppress("unchecked_cast")
        val taskSingleReobf = Class.forName("net.minecraftforge.gradle.user.TaskSingleReobf") as Class<Task>
        withType(taskSingleReobf) {
            taskSingleReobf.getDeclaredMethod("addSecondarySrgFile").invoke(this, mixinSrg)
        }
    }
}
