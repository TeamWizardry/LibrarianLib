apply<LibrarianLibModulePlugin>()

configure<LibrarianLibModule>() {
//    dependencies = listOf("core", "utilities")
}

//dependencies {
//    compile("com.github.TeamWizardry:Mirror:-SNAPSHOT")
//    compile(kotlin("reflect"))
//}

dependencies {
    librarianlib("core")
    librarianlib("utilities")
    contained("org.magicwerk:brownies-collections:0.9.13")
}
