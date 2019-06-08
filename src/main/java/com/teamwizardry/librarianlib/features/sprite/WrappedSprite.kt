package com.teamwizardry.librarianlib.features.sprite

abstract class WrappedSprite: ISprite {
    abstract val wrapped: ISprite?

    override fun bind() {
        wrapped?.bind()
    }

    override fun minU(animFrames: Int): Float = wrapped?.minU(animFrames) ?: 0f
    override fun minV(animFrames: Int): Float = wrapped?.minV(animFrames) ?: 0f
    override fun maxU(animFrames: Int): Float = wrapped?.maxU(animFrames) ?: 1f
    override fun maxV(animFrames: Int): Float = wrapped?.maxV(animFrames) ?: 1f

    override val width: Int get() = wrapped?.width ?: 1
    override val height: Int get() = wrapped?.height ?: 1
    override val uSize: Float get() = wrapped?.uSize ?: 1f
    override val vSize: Float get() = wrapped?.vSize ?: 1f
    override val frameCount: Int get() = wrapped?.frameCount ?: 1
    override val minUCap: Float get() = wrapped?.minUCap ?: 0f
    override val minVCap: Float get() = wrapped?.minVCap ?: 0f
    override val maxUCap: Float get() = wrapped?.maxUCap ?: 0f
    override val maxVCap: Float get() = wrapped?.maxVCap ?: 0f
    override val pinTop: Boolean get() = wrapped?.pinTop ?: true
    override val pinBottom: Boolean get() = wrapped?.pinBottom ?: true
    override val pinLeft: Boolean get() = wrapped?.pinLeft ?: true
    override val pinRight: Boolean get() = wrapped?.pinRight ?: true
    override val rotation: Int get() = wrapped?.rotation ?: 0
}