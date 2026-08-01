package com.jelyta.deviceguardian.core.di

import android.content.Context

class AppContainer(context: Context) {
    val appModule = AppModule(context)
    val databaseModule = DatabaseModule(context)
    val networkModule = NetworkModule()
    val aiModule = AiModule()

    val repositoryModule = RepositoryModule(
        appModule = appModule,
        databaseModule = databaseModule,
        networkModule = networkModule,
        aiModule = aiModule
    )

    val useCaseModule = UseCaseModule(repositoryModule, appModule)
}
