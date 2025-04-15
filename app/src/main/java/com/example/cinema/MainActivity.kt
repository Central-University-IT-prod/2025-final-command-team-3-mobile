package com.example.cinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cinema.data.api.AuthApiService
import com.example.cinema.data.api.FilmApiService
import com.example.cinema.data.api.ProfileApiService
import com.example.cinema.data.api.RetrofitClient
import com.example.cinema.data.local.AuthStorage
import com.example.cinema.data.repository.AuthRepositoryImpl
import com.example.cinema.data.repository.FilmRepositoryImpl
import com.example.cinema.data.repository.ProfileRepositoryImpl
import com.example.cinema.presentation.collections.CollectionsScreen
import com.example.cinema.presentation.collections.CollectionsViewModel
import com.example.cinema.presentation.collections.CollectionsViewModelFactory
import com.example.cinema.presentation.home.HomeScreen
import com.example.cinema.presentation.home.HomeViewModel
import com.example.cinema.presentation.home.HomeViewModelFactory
import com.example.cinema.presentation.profile.ProfileScreen
import com.example.cinema.presentation.profile.ProfileViewModel
import com.example.cinema.presentation.profile.ProfileViewModelFactory
import com.example.cinema.presentation.sign.signIn.SignInScreen
import com.example.cinema.presentation.sign.signIn.SignInViewModelFactory
import com.example.cinema.presentation.sign.signUp.SignUpScreen
import com.example.cinema.presentation.sign.signUp.SignUpViewModelFactory
import com.example.cinema.ui.theme.CinemaTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Главная", Icons.Filled.Home)
    data object Collections : Screen("collections", "Коллекции", Icons.Filled.Star)
    data object Profile : Screen("profile", "Профиль", Icons.Filled.Person)
}

class MainActivity : ComponentActivity() {
    private lateinit var authStorage: AuthStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authStorage = AuthStorage(this)
//        authStorage.clearToken()
        val isUserAuthenticated = !authStorage.isTokenExpired()

        // Базовый Retrofit клиент
        val commonRetrofit = RetrofitClient.create(authStorage)

        // API сервисы
        val filmsApiService = commonRetrofit.create(FilmApiService::class.java)

        val authApiService = commonRetrofit.create(AuthApiService::class.java)

        val profileApiService = commonRetrofit.create(ProfileApiService::class.java)

        // Репозитории
        val filmRepository = FilmRepositoryImpl(filmsApiService, authStorage)
        val authRepository = AuthRepositoryImpl(authApiService, authStorage)
        val profileRepository = ProfileRepositoryImpl(profileApiService, authStorage)

        // Фабрики ViewModel
        val homeViewModelFactory = HomeViewModelFactory(filmRepository)
        val profileViewModelFactory = ProfileViewModelFactory(profileRepository)
        val collectionsViewModelFactory = CollectionsViewModelFactory(filmRepository)
        val signUpViewModelFactory = SignUpViewModelFactory(authRepository)
        val signInViewModelFactory = SignInViewModelFactory(authRepository)

        setContent {
            CinemaTheme {
                var isAuthenticated by remember { mutableStateOf(isUserAuthenticated) }

                var showSignIn by remember { mutableStateOf(false) } // Показывать экран регистрации или входа

                val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
                val profileViewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
                val collectionsViewModel: CollectionsViewModel = viewModel(factory = collectionsViewModelFactory)

                if (isAuthenticated) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                listOf(Screen.Home, Screen.Collections, Screen.Profile).forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, screen.title) },
                                        label = { Text(screen.title) },
                                        selected = currentScreen.route == screen.route,
                                        onClick = { currentScreen = screen }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        when (currentScreen) {
                            Screen.Home -> HomeScreen(
                                innerPadding = innerPadding,
                                viewModel = homeViewModel
                            )
                            Screen.Collections -> CollectionsScreen(innerPadding, viewModel = collectionsViewModel)
                            Screen.Profile -> ProfileScreen(Modifier.padding(innerPadding), viewModel = profileViewModel,
                                onLogout = {
                                    isAuthenticated = false
                                    showSignIn = false
                                })
                        }
                    }
                } else {
                    if (!showSignIn) {
                        SignInScreen(
                            onNavigateToSignUp = { showSignIn = true }, // Переход на экран регистрации
                            onLoginSuccess = { isAuthenticated = true },
                            viewModel = viewModel(factory = signInViewModelFactory)
                        )
                    } else {
                        SignUpScreen(
                            onNavigateToSignIn = { showSignIn = false }, // Переход на экран входа
                            onSignUpSuccess = { isAuthenticated = true },
                            viewModel = viewModel(factory = signUpViewModelFactory)
                        )
                    }
                }
            }
        }
    }
}