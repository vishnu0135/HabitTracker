package tees.habittracker.vishnus3358684

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen()
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        
    )
    {


        Row(
            modifier = Modifier
                .fillMaxWidth()
//                .background(color = colorResource(id = R.color.Violet))
                .padding(vertical = 6.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {

            Image(
                painter = painterResource(id = R.drawable.outline_account_circle_24),
                contentDescription = "Habit Tracker",
                modifier = Modifier
                    .size(44.dp)

            )
            Spacer(modifier = Modifier.weight(1f))
            Column(

                horizontalAlignment = Alignment.CenterHorizontally
            ){

                Text(
                    text = "Hello User",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Today 19 Nov",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

            }

            Spacer(modifier = Modifier.weight(1f))


            Image(
                painter = painterResource(id = R.drawable.outline_add_circle_24),
                contentDescription = "Recipe App",
                modifier = Modifier
                    .size(44.dp)

            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .height(200.dp),
            painter = painterResource(id = R.drawable.img),

            contentDescription = "Place Image",
            contentScale = ContentScale.FillBounds
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Today's Habits",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 6.dp)

        )
        Spacer(modifier = Modifier.height(12.dp))

        TimeChipsRow()

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                .background(color = colorResource(id = R.color.Violet))
                    .padding(vertical = 6.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text(
                    text = "Singing",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 6.dp)

                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "10:00-10:30 Am",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red,

                )

            }

        }

    }

}

@Composable
fun TimeChipsRow() {
    var selected by remember { mutableStateOf("Morning") }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        TimeChip("Morning", selected == "Morning") { selected = it }
        TimeChip("Mid-day", selected == "Mid-day") { selected = it }
        TimeChip("Afternoon", selected == "Afternoon") { selected = it }
    }
}

@Composable
fun TimeChip(
    label: String,
    isSelected: Boolean,
    onSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color.LightGray else Color.White)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onSelected(label) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}