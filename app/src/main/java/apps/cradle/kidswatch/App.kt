package apps.cradle.kidswatch

import android.app.Application
import android.util.Log
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeYandexAppMetrica()
    }

    private fun initializeYandexAppMetrica() {
        if (!BuildConfig.DEBUG) {
            try {
                val config = YandexMetricaConfig.newConfigBuilder(APP_METRICA_API_KEY).build()
                YandexMetrica.activate(this, config)
                YandexMetrica.enableActivityAutoTracking(this)
                log("Yandex AppMetrica initialized successfully.")
            } catch (exc: Exception) {
                log("Error in Yandex AppMetrica initialization.")
            }
        }
    }

    init {
        instance = this
    }

    companion object {

        private lateinit var instance: App
        private const val APP_METRICA_API_KEY = "d5b6819b-7e10-4988-b251-4d30506964cd"
        private val LOG_ENABLED = BuildConfig.DEBUG
        private const val DEBUG_TAG = "KIDS_WATCH_DEBUG"

        fun getInstance(): App {
            return instance
        }

        fun log(message: String) {
            if (LOG_ENABLED) Log.d(DEBUG_TAG, message)
        }
    }
}