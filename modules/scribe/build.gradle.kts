plugins {
    `liblib-module-root`
}

module {
    displayName = "Scribe"
    description = "Annotation-driven codec generation"
}

dependencies {
    shade(project(":scribe:annotations"))
}
