<h1 align="center">LibrarianLib</h1>

<h4 align="center">
    A powerful and varied suite of libraries for Minecraft modding
</h4>


<div align="center">
… readme is very wip …
</div>


#### Creating a module


1. Run
```
./gradlew createModule --name=PizzaMaker --description="The LibrarianLib Pizza Maker module, your one-stop shop for anything pizza-related."
```

The passed name should be in `UpperCamelCase`, and will be used in several places:  
- The name of your module, e.g. `Pizza Maker`
- The module package, e.g. `com.teamwizardry.librarianlib.pizzamaker.*`
- The main module class, e.g. `LibPizzaMakerModule`
- The modid, e.g. `librarianlib-pizzamaker`
The passed description will be used verbatim in the module's `mods.toml` file.

2. Run `./gradlew genIntellijRuns`
3. Open up the run configurations in Intellij IDEA and fix their modules (they should be set to `librarianlib.main`)
3. If you want, uncheck `Allow parallel run` in the top-right. The Intellij default has changed to this being disabled, 
however the ForgeGradle default [hasn't been changed](https://github.com/MinecraftForge/ForgeGradle/issues/602). 
As LexManos pointed out, ForgeGradle does [have an option for this](https://github.com/MinecraftForge/ForgeGradle/blob/bd92a0d384b987be361ed3f7df28b1980f7fae1e/src/common/java/net/minecraftforge/gradle/common/util/RunConfig.java#L194), but this option doesn't work.

