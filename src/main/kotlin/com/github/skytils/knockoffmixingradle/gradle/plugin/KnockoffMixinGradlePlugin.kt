package com.github.skytils.knockoffmixingradle.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import java.io.File

class KnockoffMixinGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<KnockoffMixinGradleExtension>("mixin")

        project.afterEvaluate {
            with(project) {
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
                val mixinRefMap = File(project.buildDir, "tmp/mixins/${extension.refmapName}")
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
                        options.compilerArgs.addAll(
                            arrayOf(
                                "-AoutSrgFile=${mixinSrg.canonicalPath}",
                                "-AoutRefMapFile=${mixinRefMap.canonicalPath}",
                                "-AreobfSrgFile=${project.file("build/mcp-srg.srg").canonicalPath}"
                            )
                        )
                        dependsOn(copySrg.get())
                    }
                    @Suppress("unchecked_cast")
                    val taskSingleReobf = Class.forName("net.minecraftforge.gradle.user.TaskSingleReobf") as Class<Task>
                    withType(taskSingleReobf) {
                        @Suppress("unchecked_cast")
                        (taskSingleReobf.getDeclaredField("secondarySrgFiles").apply { isAccessible = true }.get(this) as MutableList<Any>).add(mixinSrg)
                    }
                }
            }
        }
    }
}