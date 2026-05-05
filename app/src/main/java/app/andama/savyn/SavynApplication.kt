package app.andama.savyn

import android.app.Application
import app.andama.savyn.data.SavynDatabase
import app.andama.savyn.data.SavynRepository

class SavynApplication : Application() {
    val database by lazy { SavynDatabase.getDatabase(this) }
    val repository by lazy { SavynRepository(database) }
}
