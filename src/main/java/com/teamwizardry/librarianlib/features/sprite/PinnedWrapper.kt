package com.teamwizardry.librarianlib.features.sprite

class PinnedWrapper(
    val wrapped: ISprite,
    override val pinTop: Boolean,
    override val pinBottom: Boolean,
    override val pinLeft: Boolean,
    override val pinRight: Boolean
): ISprite {

    override fun bind() {
        wrapped.bind()
    }

    override fun minU(animFrames: Int): Float = wrapped.minU(animFrames)
    override fun minU(): Float = wrapped.minU()
    override fun minV(animFrames: Int): Float = wrapped.minV(animFrames)
    override fun minV(): Float = wrapped.minV()
    override fun maxU(animFrames: Int): Float = wrapped.maxU(animFrames)
    override fun maxU(): Float = wrapped.maxU()
    override fun maxV(animFrames: Int): Float = wrapped.maxV(animFrames)
    override fun maxV(): Float = wrapped.maxV()

    override fun pinnedWrapper(top: Boolean, bottom: Boolean, left: Boolean, right: Boolean): ISprite {
        return wrapped.pinnedWrapper(top, bottom, left, right)
    }

    override val width: Int get() = wrapped.width
    override val height: Int get() = wrapped.height
    override val uSize: Float get() = wrapped.uSize
    override val vSize: Float get() = wrapped.vSize
    override val frameCount: Int get() = wrapped.frameCount
    override val minUCap: Float get() = wrapped.minUCap
    override val minVCap: Float get() = wrapped.minVCap
    override val maxUCap: Float get() = wrapped.maxUCap
    override val maxVCap: Float get() = wrapped.maxVCap
    override val rotation: Int get() = wrapped.rotation
}