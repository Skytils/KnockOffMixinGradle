package com.github.skytils.gradle.plugin.knockoffmixingradle

import org.gradle.api.provider.Property

abstract class KnockoffMixinGradleExtension {
    abstract val refmapName: Property<String>
}