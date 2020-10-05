package com.mozie.data

import com.mozie.data.network.NetworkHelper
import com.mozie.data.prefs.PrefsHelper
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class DataManager @Inject constructor(
    val networkHelper: NetworkHelper,
    val prefsHelper: PrefsHelper
)