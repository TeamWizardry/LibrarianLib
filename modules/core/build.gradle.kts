apply<LibrarianLibModulePlugin>()

module {
    includeCoreDependencies = false
}

dependencies {
    compile("com.github.TeamWizardry:Mirror:-SNAPSHOT")
    compile(kotlin("reflect"))
}