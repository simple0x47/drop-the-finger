package com.elementalg.minigame.cells

import com.badlogic.gdx.math.Vector2
import kotlin.jvm.Throws
import kotlin.math.max
import kotlin.math.min

class WallsDefinition {
    enum class Position {
        BOTTOM,
        RIGHT,
        TOP,
        LEFT,
    }

    private val wallObstructionMap: HashMap<Position, ArrayList<Vector2>> = HashMap()

    init {
        wallObstructionMap[Position.BOTTOM] = ArrayList()
        wallObstructionMap[Position.RIGHT] = ArrayList()
        wallObstructionMap[Position.TOP] = ArrayList()
        wallObstructionMap[Position.LEFT] = ArrayList()
    }

    @Throws(IllegalStateException::class)
    fun isThereAnyObstructionInRange(position: Position, range: Vector2): Boolean {
        check(wallObstructionMap.containsKey(position)) {
            "'position' is not yet contained within the obstruction map."
        }

        if (wallObstructionMap[position]?.size == 0) {
            return false
        }

        for (obstructedRange: Vector2 in wallObstructionMap[position]!!) {
            if (range.x > obstructedRange.y && range.y < obstructedRange.x) {
                return true
            }
        }

        return false
    }

    @Throws(IllegalArgumentException::class)
    fun addObstruction(position: Position, range: Vector2) {
        check(wallObstructionMap.containsKey(position)) {
            "'position' is not yet contained within the obstruction map."
        }

        if (isThereAnyObstructionInRange(position, range)) {
            for (obstructedRange: Vector2 in wallObstructionMap[position]!!) {
                if (range.x > obstructedRange.y && range.y < obstructedRange.x) {
                    // combine the obstructed range avoiding to create overlapping obstructions.
                    obstructedRange.set(min(obstructedRange.x, range.x), max(obstructedRange.y, range.y))

                    break
                }
            }
        } else {
            wallObstructionMap[position]?.add(range)
        }
    }
}