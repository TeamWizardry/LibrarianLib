# Setup

## Importing
You don't need to do anything special to open the project, just open the gradle project in IDEA or Eclipse, and it 
should set everything up correctly. I don't use Eclipse, so the process there isn't as well tested, but from what 
testing I have done it works fine.

## Running LibrarianLib
To set up the run configuration for LibrarianLib, run the `genIntellijRuns` or `genEclipseRuns` for the `:zzz:runtime` 
subproject. 

## Enabling debug logging
To enable debug logging for modules add their comma-separated names to the `librarianlib.debug.modules` system property.
For example, to enable debug logging for the bootstrapper and the foundation module, you would add 
`-Dlibrarianlib.debug.modules=bootstrap,foundation` to the VM options.

# Design

## Be "lightweight"
Yes, LibarianLib being lightweight seems like an oxymoron. I want LibarianLib 5 to be lightweight in terms of 
*modifications,* even if it's anything but lightweight in terms of *additions.* 

- Don't replace built-in systems. Work *with* Minecraft/Forge, not *against* it. Do note that you don't always have to 
be compatible with stupid.
- If something is conceptually an independent feature that could be useful in isolation, consider creating a module.

## Be flexible
Don't box people into only using our base classes. For example, the Foundation module doesn't require people to use the
Foundation base classes. It has some special-casing to provide extended features to blocks that implement 
`IFoundationBlock`, but people can use any block class they want. Similarly, the Facade GUI is implemented as a separate 
widget that the base screen class delegates to, meaning other people can use it in their own screens if they need to.

## LibrarianLib is actually a Java library
While LibrarianLib is written in Kotlin, it's designed to be used from Java code, so things like typesafe DSLs, while
interesting additions, aren't ergonomic from Java.

- Use builders, not typesafe DSLs
- Add `@JvmSynthetic` to internal members, otherwise they may be visible from Java (you have to individually annotate 
the getters and setters for internal properties)
- Add `@JvmOverloads` to methods with default parameters, though doing that will only allow skipping the last 
parameters, so often it's best to use manual overloads.
- Use Java function types (`Runnable`, `Consumer`, etc.) when possible instead of Kotlin function types (`() -> Unit`, 
`(T) -> Unit`, etc.) for user-facing APIs. Note that Kotlin has its own `Function` type, so either manually import 
`java.util.Function` or use autocomplete and select the Java function type.

## The user is king
Your job as a LibrarianLib developer is to pamper the user. At its core LibrarianLib is designed to make modding 
*easier,* so your job is to (within reason) make their life as easy as possible. This does not mean you should do 
everything they want, you should still adhere to these design guidelines and try to make good design decisions, just 
think about the person using the API as you write it.
