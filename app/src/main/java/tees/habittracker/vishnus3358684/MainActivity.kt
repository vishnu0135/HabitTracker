package tees.habittracker.vishnus3358684

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import tees.habittracker.vishnus3358684.database.HabitCompletionRepository
import tees.habittracker.vishnus3358684.database.HabitDatabase
import tees.habittracker.vishnus3358684.database.HabitRepository
import tees.habittracker.vishnus3358684.database.HabitViewModel
import tees.habittracker.vishnus3358684.database.HabitViewModelFactory
import tees.habittracker.vishnus3358684.managehabits.AboutUsScreen
import tees.habittracker.vishnus3358684.managehabits.AnalyticsScreen
import tees.habittracker.vishnus3358684.managehabits.EditHabitScreen
import tees.habittracker.vishnus3358684.managehabits.HabitAnalyticsDetailScreen
import tees.habittracker.vishnus3358684.managehabits.HabitCheckInScreen
import tees.habittracker.vishnus3358684.managehabits.HabitDetailsScreen
import tees.habittracker.vishnus3358684.managehabits.TodayHabitsScreen
import tees.habittracker.vishnus3358684.managehabits.ViewHabitsScreen
import tees.habittracker.vishnus3358684.ui.theme.HabitTrackerTheme
import tees.habittracker.vishnus3358684.utils.NotificationHelper

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createHabitChannel(this)

        setContent {
            HabitTrackerTheme {
                HabitTrackerScreens()
            }
        }
    }
}

@Composable
fun HabitTrackerScreens() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val database = HabitDatabase.getDatabase(context)
    val repository = HabitRepository(database.habitDao())
    val completionRepository = HabitCompletionRepository(database.habitCompletionDao())

    val habitViewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(repository,completionRepository)
    )

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {
        composable(AppScreens.Splash.route) {
            HTStatusChecker(navController = navController)
        }

        composable("todayHabits") {
            TodayHabitsScreen(habitViewModel, navController)
        }


        composable(AppScreens.Login.route) {
            EnterAppScreen(navController = navController)
        }

        composable(AppScreens.Register.route) {
            AccountRegisterScreen(navController = navController)
        }

        composable(AppScreens.Home.route) {
            HomeScreen(navController = navController, viewModel = habitViewModel)
        }

        composable(AppScreens.AddHabit.route) {
            AddHabitScreen(viewModel = habitViewModel, navController = navController)
        }

        composable(AppScreens.ViewHabits.route) {
            ViewHabitsScreen(viewModel = habitViewModel, navController = navController)
        }

        composable(AppScreens.Profile.route)
        {
            ProfileScreen(navController = navController)
        }

        composable(AppScreens.AboutUs.route)
        {
            AboutUsScreen(navController = navController)
        }


        composable("analytics") {
            AnalyticsScreen(viewModel=habitViewModel, navController)
        }

        composable(
            route = "habitAnalytics/{habitId}"
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")!!.toInt()
            HabitAnalyticsDetailScreen(
                habitId = habitId,
                viewModel = habitViewModel,
                navController = navController
            )
        }




        composable(
            route = "habitDetails/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->

            val habitId = backStackEntry.arguments?.getInt("habitId") ?: 0

            HabitDetailsScreen(
                habitId = habitId,
                viewModel = habitViewModel,
                navController = navController,
                onEdit = {
                    navController.navigate("editHabit/$habitId")
                },
                onDelete = { habit ->
                    habitViewModel.deleteHabit(habit)
                }
            )
        }

        composable(
            route = "editHabit/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->

            val habitId = backStackEntry.arguments?.getInt("habitId") ?: 0

            EditHabitScreen(
                habitId = habitId,
                viewModel = habitViewModel,
                navController = navController
            )
        }


        composable("checkIn/{habitId}") {
            val habitId = it.arguments?.getInt("habitId") ?: 0
            HabitCheckInScreen(
                habitId = habitId,
                viewModel = habitViewModel,
                navController = navController
            )
        }


    }
}


@Composable
fun HTStatusChecker(navController: NavController) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000)

        if (UserPrefs.checkLoginStatus(context)) {
            navController.navigate(AppScreens.Home.route) {
                popUpTo(AppScreens.Splash.route) {
                    inclusive = true
                }
            }
        } else {
            navController.navigate(AppScreens.Login.route) {
                popUpTo(AppScreens.Splash.route) {
                    inclusive = true
                }
            }
        }

    }

    HTSplashScreen()
}

@Composable
fun HTSplashScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
            ),
    ) {

        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_habit),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )

        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Habit Tracker App",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),

            )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Vishnu",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),

            )

        Spacer(modifier = Modifier.height(18.dp))
        Spacer(modifier = Modifier.height(18.dp))
        Spacer(modifier = Modifier.height(18.dp))


    }

}

@Preview(showBackground = true)
@Composable
fun HTSplashScreenPreview() {
    HabitTrackerTheme {
        HTSplashScreen()
    }
}
