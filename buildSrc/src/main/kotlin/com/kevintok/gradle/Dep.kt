package com.kevintok.gradle

object Dep {
    const val Forge = "net.minecraftforge:forge:${Version.minecraft}-${Version.forge}"
    const val ForgeGradle = "net.minecraftforge.gradle:ForgeGradle:${Version.forgeGradle}"
    const val KotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
    const val KotlinForForge = "thedarkcolour:kotlinforforge:${Version.kotlinForForge}"
}
