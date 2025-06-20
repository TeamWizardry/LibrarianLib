plugins {
    `liblib-shared-langs`
}

dependencies {
    implementation(project(":scribe:annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.28")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
}