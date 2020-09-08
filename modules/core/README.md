# Core
There are numerous tiny features, but these are the most interesting ones

### Math
- `CoordinateSpace2D` – Nested 2D coordinate space transforms, with the ability to arbitrarily convert between them.
- `Matrix3d`/`MutableMatrix3d`/`Matrix3dStack` (plus `*4d` variants) – Matrices, with mutable, mutable view, and stack 
variants. Unlike Minecraft, our matrix stacks are matrices in and of themselves, with the ability to push and pop their
current state.
- `Easing` – animation easing functions.
- `Animation`/`SimpleAnimation`/`KeyframeAnimation` – easy to use, abstract animations.
- `Vec2d`/`Vec2i` – 2d vectors
- `MathUtils.vec`/`MathUtils.ivec`/`MathUtils.block` – Constructors for vectors and block positions that cache and reuse 
a set of small integer instances. For example, getting any `Vec3d` with integer x, y, and z coordinates in the range 
`[-8, 8)` will returned a cached instance. 
- `SidedRunnable`/`ClientRunnable`/`ServerRunnable` – `Runnable`, `Consumer`, `Supplier`, and `Function` interfaces that
easily allow client-only/server-only/separate code.
- `AWTTextureUtil` – Because while `NativeImage` is alright, sometimes you need to do something other than scaling, and
so need to convert between a `BufferedImage` and a `NativeImage`
- `Client` – Your one-stop-shop for accessing anything relating to the Minecraft client. No more 
`Minecraft.getInstance().textureManager`, now just use `Client.getTextureManager()`. This also provides access to the 
time in ticks since game launch, both in pause-with-the-world and real-time varieties.
- `GlResourceGc` – Sick of trying to manage memory and GL/native resources? Don't overuse it, but with this you can use
garbage collection to do the managing for you. This should be more efficient, and in general a better idea, then using
the `finalize` method.
- `ISimpleReloadListener` – Resource reload listeners have become an absolute pain in the ass now, but this should 
simplify them significantly. Just register it with `Client.getResourceReloadHandler()` and you're on your way! Remember,
resource loading is parallel now, so be careful.
