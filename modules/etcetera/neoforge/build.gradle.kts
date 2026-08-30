plugins {
    `liblib-module-neoforge`
}

configureNeoForgeModsToml {
    mixin("ll/etcetera/etcetera.mixins.json")
}

dependencies {
    modApi(libs.modules.etcetera.sableCompanionYarn) // yarn mappings at compile time
    include(libs.modules.etcetera.sableCompanionNeoForge) // forge mappings when included
}
