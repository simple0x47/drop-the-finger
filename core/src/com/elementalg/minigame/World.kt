package com.elementalg.minigame

import com.elementalg.minigame.cells.CellHolder
import kotlin.jvm.Throws

class World {
    private val cellHolders: Array<CellHolder> = Array(CELL_HOLDERS) { CellHolder() }

    private lateinit var finger: Finger

    /**
     * Adds a finger to the world with the passed [fingerRadius].
     *
     * @param fingerRadius radius of the finger in pixels.
     *
     * @throws IllegalStateException if [finger] has already been added once.
     */
    @Throws(IllegalStateException::class)
    private fun addFinger(fingerRadius: Float) {
        check(!this::finger.isInitialized) {"'finger' has already been added once."}

        finger = Finger(fingerRadius / UNIT_TO_PIXELS)
    }

    fun create(fingerRadius: Float) {
        addFinger(fingerRadius)
    }

    fun render() {

    }

    fun dispose() {

    }

    companion object {
        const val CELL_HOLDERS: Int = 3
        const val UNIT_TO_PIXELS: Int = 100
        const val WIDTH: Float = 16f
        const val HEIGHT: Float = 16f
    }
}