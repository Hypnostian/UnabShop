package me.sebastianlizcano.unabshop

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun NavigationAPP() {
    val mynavController = rememberNavController()
    var mystartDestination: String = "login"

    val auth = Firebase.auth
    val currentUser = auth.currentUser

    mystartDestination = if (currentUser != null) {
        "home"
    } else {
        "login"
    }

    NavHost(
        navController = mynavController,
        startDestination = mystartDestination
    ) {
        composable(route = "login") {
            LoginScreen(
                onClickRegister = {
                    mynavController.navigate("register")
                },
                onSuccesfulLogin = {
                    mynavController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable(route = "register") {
            RegisterScreen(
                onClickBack = {
                    mynavController.popBackStack()
                },
                onSuccesfulRegister = {
                    mynavController.navigate("home") {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(route = "home") {
            HomeScreen(
                onClickLogout = {
                    mynavController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
