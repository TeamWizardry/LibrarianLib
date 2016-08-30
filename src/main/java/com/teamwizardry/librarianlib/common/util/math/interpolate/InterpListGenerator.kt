package com.teamwizardry.librarianlib.common.util.math.interpolate

/**
 * Created by TheCodeWarrior
 */
object InterpListGenerator {

    @JvmStatic
    fun <T> getList(func: InterpFunction<T>, points: Int) : MutableList<T> {
        if(points <= 0)
            return mutableListOf()

        var list = mutableListOf<T>()

        if(points == 1) {
            list.add(func.get(0.5f))
        } else {
            val pointDist = 1f / (points-1)
            var currentCoord = 0f

            while (currentCoord <= 1) {
                list.add(func.get(currentCoord))
                currentCoord += pointDist
            }
        }

        return list
    }
}