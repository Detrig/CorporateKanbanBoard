package com.example.disputer.authentication.presentation.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.disputer.R
import com.example.disputer.core.ProvideViewModel
import com.example.disputer.databinding.ActivityMainBinding
import com.example.disputer.notification.domain.utils.NotificationObserver
import com.example.disputer.notification.presentation.NotificationWorker
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), ProvideViewModel {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startNotificationWorker()
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        viewModel = viewModel(MainViewModel::class.java)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        hideHeaderBottomNav() // скрываем навигацию на время анимации

        animateLogoAndCheckAuth()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            NotificationObserver.observeNotifications(this, userId)
        }

        viewModel.liveData().observe(this) { screen ->
            screen.show(supportFragmentManager, R.id.main)
        }
        viewModel.init(savedInstanceState == null)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> viewModel.mainScreen()
                R.id.schedule -> viewModel.scheduleScreen()
                R.id.coach -> viewModel.coachScreen()
                R.id.info -> viewModel.infoScreen()
            }
            true
        }
    }


    fun hideHeaderBottomNav() {
        binding.headerImage.visibility = View.GONE
        binding.bottomNavigation.visibility = View.GONE
        binding.constraint.setBackgroundColor(resources.getColor(R.color.white))
    }

    fun showHeaderBottomNav() {
        binding.headerImage.visibility = View.VISIBLE
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.constraint.setBackgroundColor(resources.getColor(R.color.dark_blue))
    }

    private fun checkAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            NotificationObserver.observeNotifications(this, currentUser.uid)

            viewModel.initializeCurrentUserIfLoggedIn {
                showHeaderBottomNav()
                viewModel.mainScreen()
            }
        } else {
            viewModel.login()
        }
    }

    private fun animateLogoAndCheckAuth() {
        val logoView = binding.logoImageView
        val mainContainer = binding.mainUiContainer

        logoView.apply {
            scaleX = 0.8f
            scaleY = 0.8f
            alpha = 0f
            visibility = View.VISIBLE
        }
        mainContainer.visibility = View.GONE

        logoView.post {
            logoView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(1200)
                .withEndAction {
                    // Пауза перед исчезновением
                    logoView.postDelayed({
                        logoView.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .withEndAction {
                                logoView.visibility = View.GONE
                                mainContainer.alpha = 0f
                                mainContainer.visibility = View.VISIBLE
                                mainContainer.animate()
                                    .alpha(1f)
                                    .setDuration(500)
                                    .start()

                                checkAuth()
                            }
                            .start()
                    }, 700)
                }
                .start()
        }
    }

    override fun <T : ViewModel> viewModel(viewModelClass: Class<T>): T =
        (application as ProvideViewModel).viewModel(viewModelClass)

    private fun startNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notification_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}