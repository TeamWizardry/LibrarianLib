plugins {
    `liblib-module-fabric`
}

configureFabricModJson {
    mixin("ll/scribe/scribe.mixins.json")
}
