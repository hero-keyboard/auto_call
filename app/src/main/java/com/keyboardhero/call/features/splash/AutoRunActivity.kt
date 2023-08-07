package com.keyboardhero.call.features.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.keyboardhero.call.core.base.BaseActivity
import com.keyboardhero.call.core.utils.isConnectedToNetwork
import com.keyboardhero.call.databinding.ActivityAutoRunBinding
import com.keyboardhero.call.features.MainActivity
import com.keyboardhero.call.shared.domain.RefreshTokenUseCase
import com.keyboardhero.call.shared.domain.data
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class AutoRunActivity : BaseActivity<ActivityAutoRunBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityAutoRunBinding
        get() = ActivityAutoRunBinding::inflate

    @Inject
    lateinit var refreshTokenUseCase: RefreshTokenUseCase

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            while (true) {
                delay(2000)
                if (this@AutoRunActivity.isConnectedToNetwork()) {
                    refreshToken()
                }
            }
        }
    }

    private suspend fun refreshToken() {
        val result = refreshTokenUseCase(Unit)
        if (result.data == true) openApp() else openLoginScreen()
    }

    private fun openLoginScreen() {
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NAM", true)
        startActivity(intent)
        finish()
    }
}
