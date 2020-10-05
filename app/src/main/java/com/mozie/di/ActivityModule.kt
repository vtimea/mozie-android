package com.mozie.di

import com.mozie.data.network.NetworkHelper
import com.mozie.data.network.NetworkHelperImpl
import com.mozie.data.prefs.PrefsHelper
import com.mozie.data.prefs.PrefsHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent


@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun bindNetworkHelper(networkHelperImpl: NetworkHelperImpl): NetworkHelper

    @Binds
    abstract fun bindPrefsHelper(prefsHelperImpl: PrefsHelperImpl): PrefsHelper
}