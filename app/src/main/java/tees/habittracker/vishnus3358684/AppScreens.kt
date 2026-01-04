package tees.habittracker.vishnus3358684

sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash_route")
    object Login : AppScreens("login_route")
    object Register : AppScreens("register_route")

    object Home : AppScreens("home_screen")
    object Profile : AppScreens("profile_screen")
    object AboutUs : AppScreens("aboutus_screen")

    object AddHabit : AppScreens("add_habit")
    object ViewHabits : AppScreens("view_habit")
    object ViewHabitDetails : AppScreens("view_habit")

}