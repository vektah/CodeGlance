package net.vektah.codeglance.config

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import net.vektah.codeglance.Observable



@State(
        name = "CodeGlance",
        storages = arrayOf(
            Storage(id = "other", file = StoragePathMacros.APP_CONFIG + "/CodeGlance.xml")
        )
)
//@State(name = "CodeGlance", reloadable = true, defaultStateAsResource = false, additionalExportFile = "", presentableName = "", storages = arrayOf(@Storage(id = "other", file = StoragePathMacros.APP_CONFIG + "/CodeGlance.xml")))
class ConfigService : Observable<ConfigChangeListener>(ConfigChangeListener::class.java), PersistentStateComponent<Config> {
    private val config = Config()

    override fun getState(): Config? {
        return config
    }

    override fun loadState(config: Config) {
        XmlSerializerUtil.copyBean(config, this.config)
    }
}

