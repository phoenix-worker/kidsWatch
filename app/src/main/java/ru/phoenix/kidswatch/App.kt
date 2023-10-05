package ru.phoenix.kidswatch

import android.app.Application

class App : Application() {

    init {
        instance = this
    }

    companion object {

        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }

    }

}