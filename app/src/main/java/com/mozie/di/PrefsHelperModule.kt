package com.mozie.di

import com.mozie.data.prefs.PrefsHelper
import com.mozie.data.prefs.PrefsHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class PrefsHelperModule {
    @Binds
    abstract fun bindNetworkHelper(prefsHelperImpl: PrefsHelperImpl): PrefsHelper
}