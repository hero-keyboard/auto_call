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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class AutoRunActivity : BaseActivity<ActivityAutoRunBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityAutoRunBinding
        get() = ActivityAutoRunBinding::inflate

    @Inject
    lateinit var refreshTokenUseCase: RefreshTokenUseCase

    private var isLogin = false

    private var retryCount = 5;

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            while (!isLogin && retryCount > 0) {
                delay(3000)
                if (this@AutoRunActivity.isConnectedToNetwork()) {
                    isLogin = refreshToken() ?: false
                    retryCount--
                }
            }
            if (isLogin) {
                openApp()
            } else {
                openLoginScreen()
            }
        }
    }

    private suspend fun refreshToken(): Boolean? {
        val result = refreshTokenUseCase(Unit)
        return result.data
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
