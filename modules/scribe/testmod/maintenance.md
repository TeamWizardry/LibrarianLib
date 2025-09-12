# kotlin-compile-testing

To test the symbol processor we use the `kotlin-compile-testing` library, or specifically a fork of it that supports 
Kotlin 2.x.

The tests are run as standard unit tests instead of in-game tests, because serialization doesn't generally require the 
game to be running, it shortens the feedback loop, and the tooling is better.

## META-INF/analysis-api

The kotlin-compile-testing fork doesn't work in K2 mode out of the box. It crashes trying to start:
```
java.lang.NullPointerException: getService(...) must not be null
	at ksp.org.jetbrains.kotlin.asJava.KotlinAsJavaSupport$Companion.getInstance(KotlinAsJavaSupport.kt:67)
	at ksp.org.jetbrains.kotlin.asJava.finder.JavaElementFinder.<init>(JavaElementFinder.kt:33)
	at ksp.org.jetbrains.kotlin.analysis.api.standalone.base.projectStructure.FirStandaloneServiceRegistrar.registerProjectModelServices(FirStandaloneServiceRegistrar.kt:45)
	at com.google.devtools.ksp.impl.KotlinSymbolProcessing.registerProjectServices(KotlinSymbolProcessing.kt:279)
	at com.google.devtools.ksp.impl.KotlinSymbolProcessing.createAASession(KotlinSymbolProcessing.kt:244)
	at com.google.devtools.ksp.impl.KotlinSymbolProcessing.execute(KotlinSymbolProcessing.kt:454)
	at com.tschuchort.compiletesting.Ksp2PrecursorTool.execute(Ksp2.kt:110)
	at com.tschuchort.compiletesting.AbstractKotlinCompilation.compileKotlin(AbstractKotlinCompilation.kt:210)
```
The reason for this failure is that `getInstance()` looks up the `ksp.org.jetbrains.kotlin.asJava.KotlinAsJavaSupport` 
service from the project's dependency injection system, however the service isn't found. The dependency injection system
*does* however have an entry for `org.jetbrains.kotlin.asJava.KotlinAsJavaSupport` (note no leading `ksp.`).

After much digging, here are the conclusions:
- The services are loaded by `FirStandaloneServiceRegistrar.registerProjectServices()` 
- It loads these services from `META-INF/analysis-api/analysis-api-fir-standalone-base.xml`
- That file is present in both `com.google.devtools.ksp:symbol-processing-aa-embeddable` and `org.jetbrains.kotlin:kotlin-annotation-processing-embeddable`
- `symbol-processing-aa-embeddable` shades its dependencies under `ksp.*`, while `kotlin-annotation-processing-embeddable` doesn't shade at all.
- Because the service config isn't under a package, it doesn't get relocated, and they are both present at the same path
- Because the service loading system uses `ClassLoader.getResource()`, only returns one of them (`kotlin-annotation-processing-embeddable` in this case)

The solution was to copy *both* dependency injection configs into our own resources, and write a custom file at 
`META-INF/analysis-api/analysis-api-fir-standalone-base.xml` which includes both of the individual library configs
