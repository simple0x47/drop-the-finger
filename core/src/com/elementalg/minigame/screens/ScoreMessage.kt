package com.elementalg.minigame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.elementalg.client.managers.LocaleManager
import com.elementalg.minigame.Game
import kotlin.math.max
import kotlin.random.Random

class ScoreMessage(private val type: Type) {
    enum class Type {
        GOOD,
        NEUTRAL,
        BAD
    }

    private val allIndexes: ArrayList<Int> = ArrayList()

    init {
        val maxIndex: Int = when(type) {
            Type.GOOD -> MAX_GOOD_MESSAGE
            Type.NEUTRAL -> MAX_NEUTRAL_MESSAGE
            Type.BAD -> MAX_BAD_MESSAGE
        }

        for (i: Int in 1..maxIndex) {
            allIndexes.add(i)
        }
    }

    fun getMessage(): String {
        val localeManager: LocaleManager = Game.instance().getLocaleManager()
        val messageHeader: String = when (type) {
            Type.GOOD -> GOOD_MESSAGE_HEADER
            Type.NEUTRAL -> NEUTRAL_MESSAGE_HEADER
            Type.BAD -> BAD_MESSAGE_HEADER
        }

        val index: Int = retrieveUnusedIndex()

        return localeManager.get(String.format("%s%d", messageHeader, index))
    }

    private fun retrieveUnusedIndex(): Int {
        val recentlyUsed: Preferences = Gdx.app.getPreferences(RECENTLY_USED_MESSAGES)

        val message: String = recentlyUsed.getString(type.name, NO_RECENT_MESSAGES)

        val usedIndexes: ArrayList<Int> = ArrayList()

        val index: Int = if (message != NO_RECENT_MESSAGES) {
            val jsonArrayList: JsonValue = JsonReader().parse(message)

            for (i: Int in 0 until jsonArrayList.size) {
                val value: JsonValue = jsonArrayList.get(i)
                usedIndexes.add(value.getInt(1))
            }

            val availableIndexes: Set<Int> = allIndexes.subtract(usedIndexes)

            if (availableIndexes.isEmpty()) {
                val dropIndex: Int = usedIndexes[Random.nextInt(0,
                        max(usedIndexes.size - 1, 1))]
                usedIndexes.remove(dropIndex)

                dropIndex
            }
            else {
                val randomSelection: Int = Random.nextInt(0, availableIndexes.size)

                availableIndexes.elementAt(randomSelection)
            }
        }
        else {
            Random.nextInt(1, allIndexes.last() + 1)
        }

        if (usedIndexes.size == MESSAGE_MEMORY) {
            usedIndexes.removeAt(Random.nextInt(0, max(usedIndexes.size - 1, 1)))
        }

        usedIndexes.add(index)

        recentlyUsed.putString(type.name, Json().toJson(usedIndexes))
        recentlyUsed.flush()

        return index
    }

    companion object {
        private const val MESSAGE_MEMORY: Int = 5
        private const val RECENTLY_USED_MESSAGES: String = "RUM"
        private const val NO_RECENT_MESSAGES: String = "-1"

        private const val GOOD_MESSAGE_HEADER: String = "RESTART_MESSAGE_GOOD_"
        private const val NEUTRAL_MESSAGE_HEADER: String = "RESTART_MESSAGE_NEUTRAL_"
        private const val BAD_MESSAGE_HEADER: String = "RESTART_MESSAGE_BAD_"

        private const val MAX_GOOD_MESSAGE: Int = 6
        private const val MAX_NEUTRAL_MESSAGE: Int = 6
        private const val MAX_BAD_MESSAGE: Int = 6
    }
}