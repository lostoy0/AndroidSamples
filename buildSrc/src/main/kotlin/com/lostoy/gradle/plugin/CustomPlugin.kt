package com.lostoy.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CustomPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("---------- project.name: ${project.name}")
    }
}
