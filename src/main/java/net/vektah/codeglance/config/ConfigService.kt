package net.vektah.codeglance.config

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import java.lang.ref.WeakReference

@State(
        name = "CodeGlance3",
        storages = [
            Storage( "CodeGlance.xml")
        ]
)
class ConfigService : PersistentStateComponent<Config> {
    private val observers : MutableList<WeakReference<() -> Unit>> = arrayListOf()
    private val config = Config()

    override fun getState(): Config? = config
    public fun onChange(f :() -> Unit) = observers.add(WeakReference<() -> Unit>(f))

    public fun notifyChange() {
        val it = observers.listIterator()
        while(it.hasNext()) {
            val f = it.next().get()

            if (f == null) {
                it.remove()
            } else {
                f()
            }
        }
    }

    override fun loadState(config: Config) {
        XmlSerializerUtil.copyBean(config, this.config)
    }
}

