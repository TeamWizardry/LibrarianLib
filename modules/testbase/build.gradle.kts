apply<LibrarianLibModulePlugin>()

configure<LibrarianLibModule>() {
    includeCoreDependencies = false
    dependencies = listOf("core", "virtualresources")
}
