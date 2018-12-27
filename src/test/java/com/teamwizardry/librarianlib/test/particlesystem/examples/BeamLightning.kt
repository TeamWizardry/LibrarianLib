package com.teamwizardry.librarianlib.test.particlesystem.examples

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.CallbackBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.GlLineBeamRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.VelocityUpdateModule
import net.minecraft.util.math.Vec3d
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

// Creates a lightning effect around the block
// https://i.imgur.com/OMRSuHP.png
object BeamLightning: ParticleExample {
    override fun update(origin: Vec3d) {
        // details here aren't important. Just generate a list of points
        val points: List<Vec3d> = generateLightning(origin, origin + randomNormal() * rand(5.0, 8.0), 4)
        points.forEachIndexed { i, point ->
            BeamLightningSystem.spawn(
                10.0, // make particle last 10 ticks
                i == points.size-1, // if this is the last point, set isEnd to true (1.0)
                point, // the position along the lightning bolt
                vec(0, i*0.05/points.size, 0), // give the bolts an upward velocity proportional to their distance
                Color.WHITE // make the lightning white
            )
        }
    }

    private fun generateLightning(start: Vec3d, end: Vec3d, iterations: Int, list: MutableList<Vec3d> = mutableListOf()): List<Vec3d> {
        if(iterations == 0) return list

        var center = (start+end)/2
        val distance = (start-end).length()/8
        center += vec(
            rand(-distance, distance),
            rand(-distance, distance),
            rand(-distance, distance)
        )

        val isFirst = list.isEmpty()
        if(isFirst) list.add(start)
        generateLightning(start, center, iterations-1, list)
        list.add(center)
        generateLightning(center, end, iterations-1, list)
        if(isFirst) list.add(end)

        return list
    }

    private fun rand(min: Double, max: Double): Double {
        if(min == max) return min
        return ThreadLocalRandom.current().nextDouble(min, max)
    }

    private fun rand(max: Double): Double {
        return ThreadLocalRandom.current().nextDouble(0.0, max)
    }
}

object BeamLightningSystem: ParticleSystem() {

    override fun configure() {
        // bind values in the particle array
        val isEnd = bind(1)
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val color = bind(4)

        // this module does one simple thing, move based upon a velocity, updating pos and prevpos
        updateModules.add(VelocityUpdateModule(position, velocity, previousPosition))
        // the beam draw a GL_LINE from each point to the next until it encounters a particle where isEnd != 0.0,
        // at which point it will start over a new line
        renderModules.add(GlLineBeamRenderModule(
            isEnd = isEnd, blend = true,
            previousPosition = previousPosition, position = position,
            color = color, size = 2f,
            alpha = CallbackBinding(1) { particle, contents ->
                age.load(particle)
                lifetime.load(particle)
                contents[0] = 1.0 - (age.getValue(0) / lifetime.getValue(0))
            }
        ))
    }

    fun spawn(lifetime: Double, isEnd: Boolean, pos: Vec3d, velocity: Vec3d, color: Color) {
        this.addParticle(lifetime,
            if(isEnd) 1.0 else 0.0,
            pos.x, pos.y, pos.z,
            pos.x, pos.y, pos.z,
            velocity.x, velocity.y, velocity.z,
            color.red/255.0, color.green/255.0, color.blue/255.0, color.alpha/255.0
        )
    }
}
