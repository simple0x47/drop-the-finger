package com.elementalg.client.managers

import com.badlogic.gdx.assets.AssetDescriptor
import kotlin.jvm.Throws

/**
 * Represents the dependencies of a game in an abstract way in order to allow the aggregation of assets by the
 * place where they are being used. Each <b>ID</b> must be unique to a <i>Dependency</i>.
 */
internal class Dependency private constructor(private val ID: String,
                                              private val assets: HashMap<String, AssetDescriptor<*>>,
                                              private val forceLoad: Boolean) {
    enum class State {
        UNAVAILABLE,
        LOADING,
        AVAILABLE,
    }

    private var state: State = State.UNAVAILABLE

    internal fun setState(state: State) {
        this.state = state
    }

    internal fun getState(): State {
        return state
    }

    internal fun getID(): String {
        return ID
    }

    internal fun getAssets(): HashMap<String, AssetDescriptor<*>> {
        return assets
    }

    internal fun isForceLoaded(): Boolean {
        return forceLoad
    }

    internal companion object Builder {
        private val dependencies: HashMap<String, Dependency> = HashMap()

        /**
         * Returns an instance of [Dependency] which already has been built with [ID].
         *
         * @param ID identifier of the dependency, which has already been built.
         *
         * @throws IllegalArgumentException if [ID] is empty or there's not a dependency built with that [ID].
         *
         * @return the instance of [Dependency] with the desired [ID].
         */
        @Throws(IllegalArgumentException::class)
        internal fun getByID(ID: String): Dependency {
            require(ID.isNotEmpty()) {"'ID' is empty."}
            require(dependencies.containsKey(ID)) { "Dependency with 'ID' ($ID) has not been built yet." }

            return dependencies[ID]!!
        }

        /**
         * @return [HashMap] containing all the built dependencies.
         */
        internal fun getAll(): HashMap<String, Dependency> {
            return dependencies
        }

        /**
         * Builds an instance of Dependency.
         *
         * @param ID unique identification for the dependency, normally the name of for what the dependency is going
         * to be used.
         * @param assets list of [AssetDescriptor]s.
         * @param forceLoad if assets must all be loaded <i>freezing</i> the thread until then.
         *
         * @throws IllegalArgumentException if [ID] or [assets] is empty, or a dependecy has been created already
         * with that [ID].
         *
         * @return an instance of [Dependency].
         */
        @Throws(IllegalArgumentException::class)
        internal fun build(ID: String, assets: HashMap<String, AssetDescriptor<*>>, forceLoad: Boolean): Dependency {
            require(ID.isNotEmpty()) {"'ID' is empty."}
            require(!dependencies.containsKey(ID)) {"Dependency with 'ID' ($ID) has been built already."}
            require(assets.isNotEmpty()) {"'assets' is empty."}

            val dependency: Dependency = Dependency(ID, assets, forceLoad)
            dependencies[ID] = dependency

            return dependency
        }
    }
}