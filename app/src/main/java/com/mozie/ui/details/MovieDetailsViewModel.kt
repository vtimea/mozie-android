package com.mozie.ui.details

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mozie.data.DataManager
import dagger.hilt.android.qualifiers.ApplicationContext

class MovieDetailsViewModel @ViewModelInject constructor(
    private val dataManager: DataManager,
    @ApplicationContext private val context: Context
) : ViewModel()