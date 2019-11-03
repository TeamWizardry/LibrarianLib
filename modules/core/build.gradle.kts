apply<LibrarianLibModulePlugin>()

configure<LibrarianLibModule>() {
}

dependencies {
    compile("com.github.TeamWizardry:Mirror:-SNAPSHOT")
    compile(kotlin("reflect"))
}