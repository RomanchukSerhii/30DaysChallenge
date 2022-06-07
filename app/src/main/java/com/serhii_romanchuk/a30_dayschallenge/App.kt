package com.serhii_romanchuk.a30_dayschallenge

import android.app.Application
import com.serhii_romanchuk.a30_dayschallenge.model.ChallengesService

class App : Application() {
    val challengesService = ChallengesService()
}