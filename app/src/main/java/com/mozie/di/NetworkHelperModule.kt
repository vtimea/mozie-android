package com.mozie.di

import com.mozie.data.network.NetworkHelper
import com.mozie.data.network.NetworkHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class NetworkHelperModule {
    @Binds
    abstract fun bindNetworkHelper(networkHelperImpl: NetworkHelperImpl): NetworkHelper
}