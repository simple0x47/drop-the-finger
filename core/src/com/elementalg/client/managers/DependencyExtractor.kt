package com.elementalg.client.managers

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.utils.XmlReader
import kotlin.jvm.Throws

/**
 * Extracts dependencies from XML files.
 *
 * @author Gabriel Amihalachioaie.
 */
internal class DependencyExtractor private constructor() {

    /**
     * Extracts all dependencies held within the provided file.
     *
     * @param dependenciesFile XML file holding the dependencies' data.
     *
     * @throws IllegalArgumentException if [dependenciesFile] does not exist or is empty.
     * @throws IllegalStateException if [dependenciesFile] contains invalid data.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    internal fun extractAll(dependenciesFile: FileHandle) {
        require(dependenciesFile.exists()) {"'dependenciesFile' does not exist."}
        require(dependenciesFile.length() > 0L) {"'dependenciesFile' is empty."}

        val xmlRoot: XmlReader.Element = XmlReader().parse(dependenciesFile)

        for (xmlDependency: XmlReader.Element in xmlRoot.getChildrenByName(DEPENDENCY_TAG)) {
            val dependencyPath: String = xmlDependency.getAttribute(DEPENDENCY_PATH_ATTRIBUTE)

            val assetsMap: HashMap<String, AssetDescriptor<*>> = HashMap()
            for (xmlAsset: XmlReader.Element in xmlDependency.getChildrenByName(ASSET_TAG)) {
                checkNotNull(xmlAsset.text) {"'dependenciesFile' contains an empty asset."}
                check(xmlAsset.getAttribute("class").isNotEmpty()) {
                    "'dependenciesFile' contains an asset with a empty 'class' attribute."
                }

                val assetID: String = xmlAsset.getAttribute("id")

                check(!assetsMap.containsKey(assetID)) {"'dependenciesFile' contains duplicated asset ids."}

                val assetPath: String = dependencyPath + xmlAsset.text
                val assetClass: Class<*> = Class.forName(xmlAsset.getAttribute("class"))

                if (assetClass == BitmapFont::class.java) {
                    check(xmlAsset.getAttribute("fontSize").isNotEmpty()) {
                        "'dependenciesFile' contains an empty 'fontSize' attribute."
                    }

                    val fontParams: FreetypeFontLoader.FreeTypeFontLoaderParameter = FreetypeFontLoader
                        .FreeTypeFontLoaderParameter()

                    fontParams.fontFileName = assetPath
                    fontParams.fontParameters.size = xmlAsset.getIntAttribute("fontSize")
                    fontParams.fontParameters.magFilter = Texture.TextureFilter.Linear
                    fontParams.fontParameters.minFilter = Texture.TextureFilter.Linear

                    assetsMap[assetID] = AssetDescriptor(assetPath, BitmapFont::class.java, fontParams)
                } else {
                    assetsMap[assetID] = AssetDescriptor(assetPath, assetClass)
                }
            }

            val forceLoad: Boolean = xmlDependency.getBooleanAttribute("forceLoad", false)

            try {
                Dependency.build(xmlDependency.getAttribute(DEPENDENCY_ID_ATTRIBUTE), assetsMap, forceLoad)
            }
            catch (e: IllegalArgumentException) {
                throw IllegalStateException("'dependenciesFile' contains invalid data.")
            }
        }
    }

    /**
     * Builds a dependency by reading an assets file and retrieving the assets for [dependencyID].
     *
     * @param dependenciesFile XML file containing the assets data.
     * @param dependencyID dependency which is desired to be initialized.
     *
     * @throws IllegalArgumentException if [dependenciesFile] or [dependencyID] are empty, or if [dependencyID]
     * does not exist.
     * @throws IllegalStateException if [dependenciesFile] does not contain the [dependencyID] or if it has no content.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    internal fun extractSingle(dependenciesFile: FileHandle, dependencyID: String): Dependency {
        require(dependenciesFile.exists()) {"'dependenciesFile' does not exist."}
        require(dependenciesFile.length() > 0L) {"'dependenciesFile' is empty."}
        require(dependencyID.isNotEmpty()) {"'dependenciesFile' is empty."}

        val assetsMap: HashMap<String, AssetDescriptor<*>> = HashMap()
        var forceLoad: Boolean = false

        val xmlRoot: XmlReader.Element = XmlReader().parse(dependenciesFile)

        for (xmlDependency: XmlReader.Element in xmlRoot.getChildrenByName(DEPENDENCY_TAG)) {
            if (xmlDependency.getAttribute(DEPENDENCY_ID_ATTRIBUTE).equals(dependencyID, true)) {
                val dependencyPath: String = xmlDependency.getAttribute(DEPENDENCY_PATH_ATTRIBUTE)

                forceLoad = xmlDependency.getBooleanAttribute("forceLoad", false)

                for (xmlAsset: XmlReader.Element in xmlDependency.getChildrenByName(ASSET_TAG)) {
                    checkNotNull(xmlAsset.text) {"'dependenciesFile' contains an empty asset."}
                    check(xmlAsset.getAttribute("class").isNotEmpty()) {
                        "'dependenciesFile' contains an asset with a empty 'class' attribute."
                    }

                    val assetID: String = xmlAsset.getAttribute("id")

                    check(!assetsMap.containsKey(assetID)) {"'dependenciesFile' contains duplicated asset ids."}

                    val assetPath: String = dependencyPath + xmlAsset.text
                    val assetClass: Class<*> = Class.forName(xmlAsset.getAttribute("class"))

                    if (assetClass == BitmapFont::class) {
                        check(xmlAsset.getAttribute("fontSize").isNotEmpty()) {
                            "'dependenciesFile' contains an empty 'fontSize' attribute."
                        }

                        val fontParams: FreetypeFontLoader.FreeTypeFontLoaderParameter = FreetypeFontLoader
                            .FreeTypeFontLoaderParameter()

                        fontParams.fontFileName = assetPath
                        fontParams.fontParameters.size = xmlAsset.getIntAttribute("fontSize")
                        fontParams.fontParameters.magFilter = Texture.TextureFilter.Linear
                        fontParams.fontParameters.minFilter = Texture.TextureFilter.Linear

                        assetsMap[assetID] = AssetDescriptor(assetPath, assetClass.java, fontParams)
                    } else {
                        assetsMap[assetID] = AssetDescriptor(assetPath, assetClass.javaClass)
                    }
                }

                break
            }
        }

        check(assetsMap.isNotEmpty()) {"'assetsMap' is empty for 'dependencyID' ($dependencyID)"}

        return Dependency.build(dependencyID, assetsMap, forceLoad)
    }

    internal companion object Factory {
        private const val DEPENDENCY_TAG: String = "dependency"
        private const val DEPENDENCY_ID_ATTRIBUTE: String = "id"
        private const val DEPENDENCY_PATH_ATTRIBUTE: String = "path"
        private const val ASSET_TAG: String = "asset"

        private val instance: DependencyExtractor = DependencyExtractor()

        internal fun getInstance(): DependencyExtractor {
            return instance
        }
    }
}