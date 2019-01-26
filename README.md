# LibrarianLib

[![Build Status](https://travis-ci.org/TeamWizardry/LibrarianLib.svg?branch=1.12)](https://travis-ci.org/TeamWizardry/LibrarianLib)

## [Documentation WIP](https://docs.teamwizardry.com/index.php?title=Main_Page)

LibrarianLib is the library mod to end all library mods. It doesn't attempt to do much else than take the pain out of modding. It can handle a great deal of boilerplate code that you really shouldn't have to touch. Its largest components are an automatic serialization system, automatic model creation and registration, a powerful GUI library that takes out the magic numbers and complex control stuctures by making everything modular, and a powerful and flexable particle system.

LibrarianLib depends on [Shadowfacts' Forgelin](https://minecraft.curseforge.com/projects/shadowfacts-forgelin). Make sure you have it if you want to use it.


## Using LibrarianLib as a dev
Adding LibrarianLib to your dev workspace is easy when using gradle (or maven, or any other similar system relying on maven).

All you need to do is to add `http://maven.bluexin.be/repository/snapshots/` as remote maven repository.
For example, in gradle, you can add this :
```groovy
repositories {
  maven {
    url = "http://maven.bluexin.be/repository/snapshots/"
  }
  // Any other repo you may need
}
```

Then use the following artifact reference in your dependencies :

 * group-id: `com.teamwizardry.librarianlib`
 * artifact-id: `librarianlib-$mcversion`
 * version: `$liblibversion-SNAPSHOT`
 * classifier: `deobf` (this will ensure the sources link properly)

For example, this is what a gradle dependency on liblib 4.0 on minecraft 1.12 would look like :
```groovy
dependencies {
  compile "com.teamwizardry.librarianlib:librarianlib-1.12:4.0-SNAPSHOT:deobf"
}
```
