package com.elementalg.minigame.world.cells

import com.elementalg.minigame.world.Finger

/**
 * Defines the requirement of each Obstacle, and that is detecting if a finger is colliding with the cell's
 * obstacle body.
 *
 * @author Gabriel Amihalachioaie.
 */
abstract class Obstacle(parentCell: CellHolder?, type: Type, size: Float) : Cell(parentCell, type, size) {
    /**
     * @param finger finger to be checked.
     *
     * @return whether or not the passed [finger] is colliding with the cell's obstacle body.
     */
    abstract fun isFingerCollidingWithObstacle(finger: Finger): Boolean
}