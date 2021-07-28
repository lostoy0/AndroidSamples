package com.lostoy.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CustomPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        System.out.println("---------- project.name: " + project.getName());

    }
}
