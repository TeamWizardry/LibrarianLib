import java.net.URI

import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.ProjectLocalConfigurations

plugins {
    `maven-publish`
    signing
}

apply<ModPublishingPlugin>()
val modPublishing = the<ModPublishingExtension>()

configurations {
    create("publishedApi") {
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes {
            attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.public)
            attribute(Category.CATEGORY_ATTRIBUTE, namedAttribute(Category.LIBRARY))
            attribute(Usage.USAGE_ATTRIBUTE, namedAttribute(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, namedAttribute(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JavaVersion.VERSION_1_8.majorVersion.toInt())
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, namedAttribute(LibraryElements.JAR))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(ProjectLocalConfigurations.ATTRIBUTE, ProjectLocalConfigurations.PUBLIC_VALUE)
        }
    }
    create("publishedRuntime") {
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes {
            attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.public)
            attribute(Category.CATEGORY_ATTRIBUTE, namedAttribute(Category.LIBRARY))
            attribute(Usage.USAGE_ATTRIBUTE, namedAttribute(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, namedAttribute(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JavaVersion.VERSION_1_8.majorVersion.toInt())
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, namedAttribute(LibraryElements.JAR))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(ProjectLocalConfigurations.ATTRIBUTE, ProjectLocalConfigurations.PUBLIC_VALUE)
        }
    }
    create("publishedSources") {
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes {
            attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.public)
            attribute(Category.CATEGORY_ATTRIBUTE, namedAttribute(Category.DOCUMENTATION))
            attribute(Usage.USAGE_ATTRIBUTE, namedAttribute(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, namedAttribute(Bundling.EXTERNAL))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, namedAttribute(DocsType.SOURCES))
        }
    }
    create("publishedJavadoc") {
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes {
            attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.public)
            attribute(Category.CATEGORY_ATTRIBUTE, namedAttribute(Category.DOCUMENTATION))
            attribute(Usage.USAGE_ATTRIBUTE, namedAttribute(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, namedAttribute(Bundling.EXTERNAL))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, namedAttribute(DocsType.JAVADOC))
        }
    }
    create("publishedObf") {
        isCanBeResolved = false
        isCanBeConsumed = true
        attributes {
            attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.public)
        }
    }
}

val modComponent = components["mod"] as AdhocComponentWithVariants

modComponent.addVariantsFromConfiguration(configurations["publishedApi"]) {
    mapToMavenScope("compile")
}
modComponent.addVariantsFromConfiguration(configurations["publishedRuntime"]) {
    mapToMavenScope("runtime")
}
modComponent.addVariantsFromConfiguration(configurations["publishedSources"]) {
}
modComponent.addVariantsFromConfiguration(configurations["publishedJavadoc"]) {
}
modComponent.addVariantsFromConfiguration(configurations["publishedObf"]) {
}

val commonConfig = rootProject.the<CommonConfigExtension>()

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = "com.teamwizardry.librarianlib"
            version = commonConfig.version

            from(components["mod"])

            pom {
                name.set(project.property("maven_name") as String)
                description.set(project.property("maven_description") as String)
                url.set("https://github.com/TeamWizardry/LibrarianLib")

                licenses {
                    license {
                        name.set("LGPL-3.0")
                        url.set("https://opensource.org/licenses/LGPL-3.0")
                    }
                }
                developers {
                    developer {
                        id.set("thecodewarrior")
                        name.set("Pierce Corcoran")
                        email.set("code@thecodewarrior.dev")
                        url.set("https://thecodewarrior.dev")
                    }

                    developer {
                        id.set("librarianlib-contributors")
                        name.set("LibrarianLib Contributors")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/TeamWizardry/LibrarianLib.git")
                    developerConnection.set("scm:git:ssh://github.com:TeamWizardry/LibrarianLib.git")
                    url.set("https://github.com/TeamWizardry/LibrarianLib")
                }
            }
        }
    }

    repositories {
        maven {
            name = "ossrh"

            val stagingRepo = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotRepo = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = URI(if(commonConfig.version.endsWith("SNAPSHOT")) snapshotRepo else stagingRepo)
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME") ?: "N/A"
                password = project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD") ?: "N/A"
            }
        }
    }
}

signing {
    if(System.getenv("SIGNING_KEY") != null) {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY_ID"),
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_KEY_PASSWORD")
        )
    } else {
        useGpgCmd()
    }

    sign(publishing.publications["maven"])
}
