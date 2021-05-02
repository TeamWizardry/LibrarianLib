@file:Suppress("PublicApiImplicitType")

plugins {
    id("java-library")
    id("kotlin-conventions")
}
/*
configurations {
    val testmod = create("testmod") {
        description = "The test mod"

        attributes {
            attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.testmod)
            attribute(Category.CATEGORY_ATTRIBUTE, namedAttribute(Category.LIBRARY))
            attribute(Usage.USAGE_ATTRIBUTE, namedAttribute(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, namedAttribute(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JavaVersion.VERSION_1_8.majorVersion.toInt())
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, namedAttribute(LibraryElements.JAR))
        }

        outgoing.variants {
            create("classes") {
                attributes {
                    attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.testmod)
                    attribute(Category.CATEGORY_ATTRIBUTE, namedAttribute(Category.LIBRARY))
                    attribute(Usage.USAGE_ATTRIBUTE, namedAttribute(Usage.JAVA_RUNTIME))
                    attribute(Bundling.BUNDLING_ATTRIBUTE, namedAttribute(Bundling.EXTERNAL))
                    attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JavaVersion.VERSION_1_8.majorVersion.toInt())
                    attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, namedAttribute(LibraryElements.CLASSES))
                }
            }

            create("resources") {
                attributes {
                    attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.testmod)
                    attribute(Category.CATEGORY_ATTRIBUTE, namedAttribute(Category.LIBRARY))
                    attribute(Usage.USAGE_ATTRIBUTE, namedAttribute(Usage.JAVA_RUNTIME))
                    attribute(Bundling.BUNDLING_ATTRIBUTE, namedAttribute(Bundling.EXTERNAL))
                    attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JavaVersion.VERSION_1_8.majorVersion.toInt())
                    attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, namedAttribute(LibraryElements.RESOURCES))
                }
            }
        }

        canBe(consumed = true, resolved = false)
        extendsFrom(implementation.get(), runtimeOnly.get(), testImplementation.get(), testRuntimeOnly.get())
    }
}

val testmodJar = tasks.register<Jar>("testmodJar") {
    archiveClassifier.set("testmod")
    from(sourceSets.main.map { it.output })
    from(sourceSets.test.map { it.output })
}

artifacts {
    add("testmod", testmodJar.map { it.archiveFile.get() })
}

afterEvaluate {
    val mainOut = sourceSets.main.get().output
    val testOut = sourceSets.test.get().output
    val allClasses = mainOut.classesDirs + testOut.classesDirs + mainOut.dirs + testOut.dirs
    val allResources = files(mainOut.resourcesDir, testOut.resourcesDir)

    configurations {
        named("testmod") {
            outgoing.variants.named("classes") {
                allClasses.forEach {
                    artifact(it) {
                        builtBy(tasks.classes)
                        type = "java-classes-directory"
                    }
                }
            }

            outgoing.variants.named("resources") {
                allResources.forEach {
                    artifact(it) {
                        builtBy(tasks.processResources)
                        type = "java-resources-directory"
                    }
                }
            }
        }
    }
}
 */