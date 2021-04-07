plugins {
    id("minecraft-conventions")
    id("org.spongepowered.mixin")
}

mixin {
    add(sourceSets.main.get(), "ll-${project.name}.refmap.json")
    add(sourceSets.test.get(), "ll-${project.name}-test.refmap.json")
}

val mixin_version: String by project

dependencies {
    annotationProcessor("org.spongepowered:mixin:$mixin_version:processor")
    testAnnotationProcessor("org.spongepowered:mixin:$mixin_version:processor")
}