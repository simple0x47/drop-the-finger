package com.elementalg.minigame

import com.elementalg.client.managers.DependencyManager
import com.elementalg.minigame.cells.Cell
import com.elementalg.minigame.cells.CellHolder
import kotlin.jvm.Throws

class World {
    private val cellHolders: ArrayList<CellHolder> = ArrayList(CELL_HOLDERS)

    private lateinit var finger: Finger

    /**
     * Adds a finger to the world with the passed [fingerRadius].
     *
     * @param fingerRadius radius of the finger in pixels.
     *
     * @throws IllegalStateException if [finger] has been initialized already.
     */
    @Throws(IllegalStateException::class)
    private fun initializeFinger(fingerRadius: Float) {
        check(!this::finger.isInitialized) {"'finger' has already been added once."}

        finger = Finger(fingerRadius / UNIT_TO_PIXELS)
    }

    fun create(dependencyManager: DependencyManager, fingerRadius: Float) {
        initializeFinger(fingerRadius)
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