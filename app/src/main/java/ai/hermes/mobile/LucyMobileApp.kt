package ai.hermes.mobile

import android.app.Application

class LucyMobileApp : Application() {
    lateinit var database: ai.hermes.mobile.data.AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = ai.hermes.mobile.data.DatabaseProvider.get(this)
    }
}
