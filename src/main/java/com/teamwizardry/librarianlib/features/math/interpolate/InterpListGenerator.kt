package com.teamwizardry.librarianlib.features.math.interpolate

/**
 * Created by TheCodeWarrior
 */
object InterpListGenerator {

    @JvmStatic
    fun <T> getList(func: InterpFunction<T>, points: Int): List<T> {
        return getIndexList(points).map { func.get(it) }
    }

    @JvmStatic
    fun getIndexList(points: Int): List<Float> {
        if (points <= 0)
            return mutableListOf()

        var list = mutableListOf<Float>()

        if (points == 1) {
            list.add(0.5f)
        } else {
            val pointDist = 1f / (points - 1)
            var currentCoord = 0f

            while (currentCoord <= 1) {
                list.add(currentCoord)
                currentCoord += pointDist
            }
        }

        return list
    }
}
