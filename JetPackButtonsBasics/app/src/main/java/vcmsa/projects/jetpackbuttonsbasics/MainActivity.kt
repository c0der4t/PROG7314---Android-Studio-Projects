package vcmsa.projects.jetpackbuttonsbasics

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vcmsa.projects.jetpackbuttonsbasics.ui.theme.JetPackButtonsBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetPackButtonsBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    EchoChamber()
//                    SecretMessage()
                    LoginApp()
                }
            }
        }
    }

    @Composable
    fun EchoChamber(modifier: Modifier = Modifier) {

        // 1. Create state to hold the text
        var text by remember { mutableStateOf("") }

        Column(modifier = modifier.padding(16.dp)) {
            TextField(
                value = text,
                onValueChange = {
                    newText ->
                    text = newText
                },
                label = {
                    Text("Type something...")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. This text compoasable simply reads the state
            Text(text = "You are typing: $text")
        }
    }

    @Composable
    fun SecretMessage(modifier: Modifier = Modifier){
        //State to track visibility
        var showMessage by remember {mutableStateOf(false)}

        Column(modifier = modifier.padding(16.dp)){
            Button(onClick = {showMessage = !showMessage}) {
                Text(if (showMessage) "Hide Message" else "Show Message")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // THE CORE LOGIC:
            // If showMessage is true, this Text composable is added to the UI.
            // If it becomes false, Compose removes it automatically
            if (showMessage){
                Text("You have discovered the secret! There is no ICE today, but if you leave, you will never know")
            }
        }
    }


    // Login app example:

    // Use object to define routes
    object Screen{
        const val LOGIN = "login"
        const val WELCOME = "welcome"
    }

    @Composable
    fun LoginApp(){
        //State to track the current screen
        var currentScreen by remember {mutableStateOf(Screen.LOGIN)}

        var loggedInUsername by remember { mutableStateOf("") }

        when(currentScreen){
            Screen.LOGIN -> {
                LoginForm(
                    onLoginSuccess = { username ->
                        // WHen login is successful:
                        // 1. Save the username
                        loggedInUsername = username

                        // 2. CHange the current screen
                        currentScreen = Screen.WELCOME
                    }
                )
            }

            Screen.WELCOME -> {
                WelcomeScreen(username = loggedInUsername)
            }
        }
    }

    @Composable
    fun WelcomeScreen(username: String, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Welcome $username",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }

    @Composable
    fun LoginForm(onLoginSuccess: (username : String) -> Unit,
                  modifier: Modifier = Modifier
    ) {
        // 1. Two state variables
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        // 2. THis is "derived state". We calculate it from our other state variables
        val isButtonEnabled = username.isNotEmpty() && password.isNotEmpty()

        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = {Text("Password")}
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onLoginSuccess(username) },
                enabled = isButtonEnabled
            ){
                Text("Login")
            }
        }
    }


}
