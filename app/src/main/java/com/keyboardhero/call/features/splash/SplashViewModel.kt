package com.keyboardhero.call.features.splash

import com.keyboardhero.call.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : BaseViewModel<SplashViewState, SplashEvent>() {
    override fun initState(): SplashViewState = SplashViewState()
}
