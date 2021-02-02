package com.elementalg.client.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.XmlReader
import com.elementalg.managers.IManager
import java.util.*

/**
 * Simplifies the implementation of different languages into a game, by making usage of a language file (XML).
 *
 * @author Gabriel Amihalachioaie.
 *
 * @constructor initializes the instance to the passed [dataXML] language file.
 * @param dataXML file handle pointing to the XML file which will be used to retrieve strings from.
 */
class LocaleManager @Throws(IllegalArgumentException::class) private constructor(private var dataXML: FileHandle) :
    IManager {

    init {
        require(dataXML.length() > 0L) { "'dataXML' is empty." }
    }

    override fun create() {

    }

    override fun dispose() {

    }

    /**
     * Changes the file used to retrieve strings from.
     *
     * @param dataXML file containing the language data.
     * @throws IllegalArgumentException if [dataXML] is empty.
     */
    @Throws(IllegalArgumentException::class)
    fun set(dataXML: FileHandle) {
        require(dataXML.length() > 0L) { "'dataXML' is empty." }

        this.dataXML = dataXML
    }

    /**
     * Returns the message identified by [stringID].
     *
     * @param stringID ID of the desired message.
     * @throws IllegalArgumentException if [stringID] is empty.
     */
    @Throws(IllegalArgumentException::class)
    fun get(stringID: String): String {
        require(stringID.isNotEmpty()) { "'stringID' is empty." }

        val xmlRoot: XmlReader.Element = XmlReader().parse(dataXML)
        val xmlChildren: Array<XmlReader.Element> = xmlRoot.getChildrenByName("string")

        for (xmlChild: XmlReader.Element in xmlChildren) {
            if (xmlChild.getAttribute("id").equals(stringID, true)) {
                return xmlChild.text
            }
        }

        return ""
    }

    /**
     * Returns the message identified by the [code].
     *
     * @param code code of the desired message.
     * @throws IllegalArgumentException if [code] is empty.
     */
    @Throws(IllegalArgumentException::class)
    fun getByCode(code: String): String {
        require(code.isNotEmpty()) { "'code' is empty." }

        val xmlRoot: XmlReader.Element = XmlReader().parse(code)
        val xmlChildren: Array<XmlReader.Element> = xmlRoot.getChildrenByName("string")

        for (xmlChild: XmlReader.Element in xmlChildren) {
            if (xmlChild.hasAttribute("code")) {
                if (xmlChild.getAttribute("code").equals(code, true)) {
                    return xmlChild.text
                }
            }
        }

        return ""
    }

    companion object Factory {
        private const val LANG_DIRECTORY: String = "lang/"
        private const val DEFAULT_LANG: String = "en"

        /**
         * Builds and instance of [LocaleManager] by trying to find a file named accordingly to the language code of the
         * passed locale, if not found [DEFAULT_LANG] is used.
         *
         * @throws IllegalStateException if [Gdx] has not been initialized yet.
         *
         * @return instance of [LocaleManager].
         */
        fun build(locale: Locale): LocaleManager {
            checkNotNull(Gdx.files) { "'Gdx' has not been initialized yet, and 'LocaleManager' depends on it." }

            var dataXML: FileHandle = Gdx.files.internal("$LANG_DIRECTORY/${locale.language}.xml")

            if (!dataXML.exists()) {
                dataXML = Gdx.files.internal("$LANG_DIRECTORY/${DEFAULT_LANG}.xml")
            }

            return LocaleManager(dataXML)
        }
    }
}