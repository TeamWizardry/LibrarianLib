//apply<LibrarianLibModulePlugin>()

dependencies {
    compile("org.magicwerk:brownies-collections:0.9.13")
    compileOnly(project(":core"))
    testCompileOnly(project(":testbase"))
    compileOnly(project(":utilities"))
}
