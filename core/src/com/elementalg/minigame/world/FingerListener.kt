package com.elementalg.minigame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class FingerListener(private val finger: Finger, private val world: World) : ClickListener() {
    private val lastPosition: Vector2 = Vector2()

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        world.start()

        lastPosition.set(x, y)
        //Gdx.app.log("TOUCH-DOWN", "TouchDown: $x, $y, $pointer, $button")
        return super.touchDown(event, x, y, pointer, button)
    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        world.gameOver()

        Gdx.app.log("TOUCH-UP", "Touch Up")
        super.touchUp(event, x, y, pointer, button)
    }

    override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
        if (world.isStarted()) {
            finger.updatePosition(x, y)
        }

        //Gdx.app.log("TOUCH-DRAGGED", "TouchDragged: $x, $y, $pointer")
        super.touchDragged(event, x, y, pointer)
    }
}