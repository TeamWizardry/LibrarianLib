<h1 align="center">LibrarianLib</h1>

<h4 align="center">
    A powerful and varied suite of libraries for Minecraft modding
</h4>


<div align="center">
… readme is very wip …
</div>


#### Creating a module


```
./gradlew createModule --name=PizzaMaker --description="The LibrarianLib Pizza Maker module, your one-stop shop for anything pizza-related."
```

The passed name should be in `UpperCamelCase`, and will be used in several places:  
- The name of your module, e.g. `Pizza Maker`
- The module package, e.g. `com.teamwizardry.librarianlib.pizzamaker.*`
- The main module class, e.g. `LibPizzaMakerModule`
- The modid, e.g. `librarianlib-pizzamaker`
The passed description will be used verbatim in the module's `mods.toml` file.
