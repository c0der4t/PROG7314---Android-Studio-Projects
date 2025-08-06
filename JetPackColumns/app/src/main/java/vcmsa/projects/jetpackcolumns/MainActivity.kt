package vcmsa.projects.jetpackcolumns

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import vcmsa.projects.jetpackcolumns.ui.theme.JetPackColumnsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetPackColumnsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    LazyNameList()
//                    InteractiveTodoList()
                    InteractiveTodoListv2()
                }
            }
        }
    }
}

@Composable
fun LazyNameList(modifier: Modifier = Modifier) {
    // We still have our list of 100 names
    val names = List(100) {
        "User Number ${it + 1}"
    }

    // Lazy Column to load few items at a time

    LazyColumn(modifier = modifier) {
        items(names) { name ->
            Text(
                text = name,
                modifier = Modifier
                    .padding(16.dp)
                    .rotate(170F)
            )
        }
    }
}


@Composable
fun InteractiveTodoList(modifier: Modifier = Modifier) {

    // 1. State for our data and for the selection

    val todoItems by remember {
        mutableStateOf(
            listOf(
                "Learn Jetpack Compose", "Do laundry",
                "Call mom", "Buy milk", "Clean Peanuts Room"
            )
        )
    }

    var selectedItem by remember {
        mutableStateOf("")
    }

    LazyColumn(modifier = modifier.offset(5.dp,60.dp)) {
        items(todoItems) { item ->
            // 4. Determine the background color based on selection state
            val backgroundColor = if (item == selectedItem) {
                Color.Red //highlight color
            } else {
                Color.Transparent // default color
            }

            Text(
                text = item,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Update the selected item state on click
                        selectedItem = item
                    }
                    .background(backgroundColor)
                    .padding(16.dp)
            )
        }
    }

}

data class TodoItem(val text: String, var isComplete : Boolean = false)

@Composable
fun TodoRow(
    item: TodoItem,
    onItemClicked : (TodoItem) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxSize().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        // The text now changes based on the completion state
        Text(
            text = item.text,
            style = LocalTextStyle.current.copy(
                textDecoration = if (item.isComplete)
                    TextDecoration.LineThrough else null
            ),
            color = if (item.isComplete) Color.Gray else Color.Black,
            modifier = Modifier.weight(1f)
            // Text takes up most of the space
        )

        //The button to toggle the state
        IconButton(onClick = {onItemClicked(item)}) {
            Icon(
                imageVector = if (item.isComplete) Icons.Default.CheckCircle else Icons.Outlined.Face,
                contentDescription = "Toggle completion",
                tint = if (item.isComplete) Color.Green else Color.Gray
            )
        }
    }
}

@Composable
fun InteractiveTodoListv2(modifier: Modifier = Modifier){
    // 2. Update the state to use a list of our new ToDoITems objects

    var todoItems by remember {
        mutableStateOf(listOf(
            TodoItem("Buy milk"),
            TodoItem("Walk the dog"),
            TodoItem("Do Luandry"),
            TodoItem("Call mom"),
            TodoItem("Learn Jetpack"),
        ))
    }

    LazyColumn(modifier = modifier.padding(8.dp)){
        items(todoItems) { item ->
            TodoRow(
                item = item,
                onItemClicked = {
                    clickedItem ->
                    // 3. THe logic to toggle the 'isCompleted' state
                    todoItems = todoItems.map {
                        if (it.text == clickedItem.text){
                            it.copy(isComplete = !it.isComplete)
                        }else{
                            it
                        }
                    }
                }
            )
        }
    }
}














