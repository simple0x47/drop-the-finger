package com.elementalg.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.elementalg.managers.IUpdatableManager
import kotlin.jvm.Throws

/**
 * Provides an ability to load and retrieve assets blocks, called [Dependency] obtained from a [dependenciesFile], with
 * the [AssetManager].
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor provides an instance ready to load the dependencies defined within the [dependenciesFile].
 *
 * @param assetManager instance of [AssetManager] used for this game's instance.
 * @param dependenciesFile [FileHandle] pointed to the dependencies file (XML).
 */
class DependencyManager private constructor(private val assetManager: AssetManager,
                                            private val dependenciesFile: FileHandle) : IUpdatableManager {

    private val dependenciesLoadingCache: ArrayList<Dependency> = ArrayList()

    fun getAssetManager(): AssetManager {
        return assetManager
    }

    /**
     * Extracts all the dependencies within the [dependenciesFile] by making usage of [DependencyExtractor].
     */
    override fun create() {
        DependencyExtractor.getInstance().extractAll(dependenciesFile)
    }

    /**
     * Calls [AssetManager.update] method and updates the status of those dependencies that have been loaded completely.
     */
    override fun update() {
        if (assetManager.update() && dependenciesLoadingCache.size > 0) {
            for (dependency: Dependency in dependenciesLoadingCache) {
                dependency.setState(Dependency.State.AVAILABLE)
            }

            dependenciesLoadingCache.clear()
        }
    }

    override fun dispose() {
        assetManager.dispose()
    }

    /**
     * Adds to the queue the passed [dependencyID].
     *
     * @param dependencyID ID of the dependency to be loaded.
     *
     * @throws IllegalArgumentException if [dependencyID] is empty or there's not a dependency built with [dependencyID].
     */
    @Throws(IllegalArgumentException::class)
    fun loadDependencyID(dependencyID: String) {
        require(dependencyID.isNotEmpty()) {"'dependencyID' is empty."}

        val dependency: Dependency = Dependency.getByID(dependencyID)

        if (dependency.getState() == Dependency.State.UNAVAILABLE) {

            val iterator = dependency.getAssets().entries.iterator()

            while (iterator.hasNext()) {
                val asset: AssetDescriptor<*> = iterator.next().value

                assetManager.load(asset)
            }

            if (dependency.isForceLoaded()) {
                assetManager.finishLoading()
                dependency.setState(Dependency.State.AVAILABLE)
            } else {
                dependency.setState(Dependency.State.LOADING)
                dependenciesLoadingCache.add(dependency)
            }
        }
    }

    /**
     * Checks whether the dependency is available or not.
     *
     * @param dependencyID ID of the dependency.
     *
     * @throws IllegalArgumentException if [dependencyID] is empty or there's not a dependency built with [dependencyID].
     *
     * @return true if the dependency is available, false otherwise.
     */
    @Throws(IllegalArgumentException::class)
    fun isDependencyAvailable(dependencyID: String): Boolean {
        require(dependencyID.isNotEmpty()) {"'dependencyID' is empty."}

        return (Dependency.getByID(dependencyID).getState() == Dependency.State.AVAILABLE)
    }

    /**
     * Unloads the desired [dependencyID].
     *
     * @param dependencyID ID of the dependency.
     *
     * @throws IllegalArgumentException if [dependencyID] is empty or there's not a dependency built with [dependencyID].
     */
    @Throws(IllegalArgumentException::class)
    fun unloadDependencyID(dependencyID: String) {
        require(dependencyID.isNotEmpty()) {"'dependencyID' is empty."}

        val dependency: Dependency = Dependency.getByID(dependencyID)

        if (dependency.getState() == Dependency.State.AVAILABLE) {
            val iterator = dependency.getAssets().entries.iterator()

            while (iterator.hasNext()) {
                val asset: AssetDescriptor<*> = iterator.next().value

                assetManager.unload(asset.fileName)
            }

            dependency.setState(Dependency.State.UNAVAILABLE)
        }
    }

    /**
     * Retrieves the assets in their processed form.
     *
     * @param dependencyID ID of the dependency whose assets are going to be returned.
     *
     * @throws IllegalArgumentException if [dependencyID] is empty or there's not a dependency built with [dependencyID].
     * @throws IllegalStateException if [dependencyID] has not been loaded yet.
     *
     * @return map containing the processed assets objects as value and the assets ids as keys.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun retrieveAssets(dependencyID: String): HashMap<String, Any> {
        require(dependencyID.isNotEmpty()) {"'dependencyID' is empty."}

        val dependency: Dependency = Dependency.getByID(dependencyID)

        check(dependency.getState() == Dependency.State.AVAILABLE) {
            "'dependencyID' ($dependencyID) is not available yet."
        }

        val assets: HashMap<String, Any> = HashMap()

        val iterator = dependency.getAssets().entries.iterator()

        while (iterator.hasNext()) {
            val entry: MutableMap.MutableEntry<String, AssetDescriptor<*>> = iterator.next()

            assets[entry.key] = assetManager.get(entry.value)
        }

        return assets
    }

    companion object Factory {
        /**
         * Builds an instance of [DependencyManager], it requires [Gdx] to have been initialized already.
         *
         * @throws IllegalStateException if [Gdx] has not been initialized yet.
         *
         * @return instance of [DependencyManager].
         */
        @Throws(IllegalStateException::class)
        fun build(): DependencyManager {
            checkNotNull(Gdx.files){"'Gdx' has not been initialized yet, and 'DependencyManager' depends on it."}

            val assetManager: AssetManager = AssetManager()
            val dependenciesXML: FileHandle = Gdx.files.internal("dependencies.xml")

            return DependencyManager(assetManager, dependenciesXML)
        }
    }
}