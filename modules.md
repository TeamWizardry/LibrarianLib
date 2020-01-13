# Module index

## Core
#### com.teamwizardry.librarianlib.math
- Nested 2D coordinate space point conversion
- Animation easing functions
- Kotlin helpers for fast sine/cosine/inverse square root
- Number clamping
- `Comparable` clamping
- `ceilInt`/`floorInt`
- `vec`/`veci`/`block` pooled vector creation methods
- Immutable and mutable 3x3/4x4 matrices
- Quaternion
- 2d and 2d rays with bounding box intercept tests
- `Rect2d`
- `Vec2d`/`Vec2i`
- `Vec3d`/`Vec3i`/`BlockPos` Kotlin math operators
#### com.teamwizardry.librarianlib.core.util
- `BufferBuilder` extensions for `pos`/`tex`/`color` that accept `Vec3d`/`Vec2d`/`Color`
- inline `BufferBuilder` extensions for `pos`/`tex` that accept `Number` parameters (number object creation is 
optimized away at compile time)
- Immutable/synchronized `Collection` creation extensions
- `KProperty0`/`KMutableProperty0` property delegates
- A constant that indicates whether the current environment is obfuscated
- A method to automatically select between srg and deobfuscated names
- A set of [20 visually distinct colors](https://sashat.me/2017/01/11/list-of-20-simple-distinct-colors/),
plus a method to color code objects using their hashcode
- A `ResourceLocation` extension method to create translation keys (e.g. `item.minecraft.foo`)
- Kotlin DSL for parsing JSON:
    ```json
    {
        "property1": 10,
        "property2": 10,
        "children_array": [
            "child1",
            { "name": "child2" }
        ],
        "optional": {
            "name": "optional_child"
        }
    }
    ```
    ```kotlin
    val parsedValue: ParsedType = jsonElement.parse("root") {
        val property1 = get("property1").asInt()
        val property2 = get("property2").asInt()
        val children: List<ChildType> = "children_array" {
            elements.map { 
                if(it.isString)
                    ChildType(it.asString())
                else
                    ChildType(it["name"].asString())
            }
        }
        val optionalChild: OptionalChild? = "optional" / {
            OptionalChild(get("name").asString())
        }
        ParsedType(property1, property2, children)
    }
    ```
#### com.teamwizardry.librarianlib.core
- Client/server side aware `Runnable`/`Consumer`/`Function`
- Easy to use resource reload listener registration through `ClientRunnable` 
- `Client` helper object
    - Easy access to various values without having to use `Minecraft.getInstance()`
    - Access game and world tick and partial tick time, with easy tick interpolation

## Particles
#### com.teamwizardry.librarianlib.particles
- Particle system

## Test Base
#### com.teamwizardry.librarianlib.testbase
- A base test mod class to be used in LibrarianLib module tests
- A DSL for creating test items, with client/common/server hooks for:
  - Right clicking
  - Right clicking air
  - Right clicking blocks
  - Holding right click
  - Releasing right click
  - Left clicking a block
  - Left clicking an entity
  - Right clicking an entity
  - Inventory ticks
  - Ticks while being held

## Utilities
#### com.teamwizardry.libarianlib.utilities
- Fast isAir checks
- Highly efficient ray-world collision detection

## Virtual Resources
#### com.teamwizardry.libarianlib.virtualresources
- A virtual resource pack, allowing custom resources to be injected at runtime
- Methods to directly inject a single resource location 
- Methods to add dynamic virtual resource pack objects
