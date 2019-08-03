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
- `BufferBuilder` extensions accepting `Vec3d`/`Vec2d`/`Color`
- Immutable/synchronized `Collection` creation extensions
- `KProperty0`/`KMutableProperty0` property delegates
#### com.teamwizardry.librarianlib.core
- Client/server side aware `Runnable`/`Consumer`/`Function`
- `Client` helper object
    - Easy access to various values without having to use `Minecraft.getInstance()`
    - Access game and world tick and partial tick time, with easy tick interpolation

## Particles
### com.teamwizardry.librarianlib.particles
- Particle system

## Utilities
### com.teamwizardry.libarianlib.utilities
- Fast isAir checks
- Highly efficient ray-world collision detection
