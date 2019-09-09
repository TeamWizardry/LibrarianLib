
dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":utilities"))
    compile("org.magicwerk:brownies-collections:0.9.13")
}

kotlin.sourceSets {
    getByName("test") {
        this.dependencies {
            compileOnly(project(":testbase"))
        }
    }
}