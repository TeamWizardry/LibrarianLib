apply<LibrarianLibModulePlugin>()

configure<LibrarianLibModule>() {
    dependencies = listOf("core", "utilities")
}

dependencies {
    contained("org.magicwerk:brownies-collections:0.9.13")
}
