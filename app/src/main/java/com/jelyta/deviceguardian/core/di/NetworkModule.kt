package com.jelyta.deviceguardian.core.di

import com.jelyta.deviceguardian.data.remote.FastApiService

class NetworkModule {
    val fastApiService: FastApiService by lazy {
        FastApiService.create()
    }
}
