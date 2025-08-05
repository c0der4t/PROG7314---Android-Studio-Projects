package vcmsa.projects.jetpackbasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vcmsa.projects.jetpackbasics.ui.theme.JetPackBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetPackBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize())
                { innerPadding ->
                    Column(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(Color.Yellow)
                    )
                    {
                        Greeting("John smith")
                        Greeting("Tess smith")
                        Counter(10)
                        StatefulCounter()
                        Quote()
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetPackBasicsTheme {
        Greeting("Android")
    }
}

@Composable
fun Counter(startingValue : Int = 0){

    // We have to use remember for the variable state to be
    // the same across canvas changes. If we do not use remember,
    // the variable will be reset whenever we rotate the device etc.
    var count by remember{ mutableStateOf(startingValue) }
    Button(onClick = { count++ }) {
        Text(text="I've been clicked the button $count times")
    }
}


// 1. Create the "dumb" (stateless) component. It doesn't know what 'count' is,
// it just displays what it's given. It doesn't know what to do on click, it
// just calls a function it was given
@Composable
fun CounterDisplay(count: Int, OnIncrement : () -> Unit, modifier: Modifier = Modifier){
    Button(onClick = OnIncrement, modifier = modifier) {
        Text("You have clicked the button $count times")
    }
}

// 2. Create the "Smart" (stateful) component that holds the state.
// This is called a "Stateful" composable
@Composable
fun StatefulCounter(modifier: Modifier = Modifier){
    var count by remember { mutableStateOf(0) }
    CounterDisplay(
        count = count,
        OnIncrement = { count++ },
        modifier = modifier
    )
}

@Composable
fun QuoteCard(quote: Quote, modifier: Modifier = Modifier){
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = quote.text)
        Text(text = "- ${quote.author}")
    }
}

@Composable
fun Quote()
{

    // 1. Create the data source
    val quoteList = listOf(
        Quote("The best way to predict the future is to invent it", "Alan Kay"),
        Quote("Simplicity is the ultimate sophistication.", "Leonardo da Vinci"),
        Quote("It's not a bug. It's an undocumented feature!", "Anonymous")
    )

    // 2. Create the state - indext of the current quote
    var quoteIndex by remember { mutableStateOf(0) }

    // 3. Arrange the UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        // Display the current quote
        QuoteCard(quote = quoteList[quoteIndex])

        // Spacer for visual separation
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            // Logic to get the next quote, looping back to the start
            quoteIndex = (quoteIndex + 1) % quoteList.size
        }) {
            Text("New Quote")
        }
    }

}