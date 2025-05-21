package github.detrig.corporatekanbanboard.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.databinding.ActivityMainBinding
import github.detrig.corporatekanbanboard.presentation.globalchat.GlobalChatScreen

class MainActivity : AppCompatActivity(), ProvideViewModel {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        viewModel = viewModel(MainViewModel::class.java)

        hideHeaderBottomNav() // скрываем навигацию на время анимации

        animateLogoAndCheckAuth()

        viewModel.navigationLiveData().observe(this) { screen ->
            screen.show(supportFragmentManager, R.id.main)
        }
        viewModel.init(savedInstanceState == null)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.boards -> viewModel.boardsScreen()
                R.id.globalChat -> viewModel.globalChatScreen()
                R.id.profile -> viewModel.profileEditScreen()
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
        binding.constraint.setBackgroundColor(resources.getColor(R.color.light_gray))
    }

    private fun checkAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModel.initializeCurrentUserIfLoggedIn {
                showHeaderBottomNav()
                viewModel.boardsScreen()
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
}