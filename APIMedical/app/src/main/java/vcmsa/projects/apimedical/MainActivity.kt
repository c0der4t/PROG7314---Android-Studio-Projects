package vcmsa.projects.apimedical

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.kittinunf.fuel.core.Handler
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = android.os.Handler(Looper.getMainLooper())
    private lateinit var outputTextView : TextView
    private lateinit var inputIdEditText : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        outputTextView = findViewById(R.id.txtOutput)
        inputIdEditText = findViewById(R.id.edtInputId)


    }

    private fun getAllLoans(){

        // Define the API endpoint URL.
        val url = "https://opsc.azurewebsites.net/loans/"
        outputTextView.text = "Fethcing all loans..."

        // Execute the network request on a background thread.
        executor.execute {
            // Use Fuel's httpGet for a GET request
            url.httpGet().responseString { _, _, result ->
                // Switch to the main thread to update the UI.
                handler.post {
                    when (result){
                        is Result.Success -> {
                            // On success, deserialize the JSON string into a list of loan objects.
                            val json = result.get()
                            try {
                                val loans = Gson().fromJson(json, Array<Loan>::class.java).toList()
                                if (loans.isNotEmpty()){
                                    val formattedOutput = loans.joinToString( separator = "\n\n") {
                                        loan -> "Loan ID: ${loan.loanID} \n" +
                                            "Amount: ${loan.amount}\n" +
                                            "Member ID: ${loan.memberID}\n" +
                                            "Message: ${loan.message}"
                                    }

                                    outputTextView.text = formattedOutput
                                }else{
                                    outputTextView.text = "No loans found."
                                }
                            } catch (e : JsonSyntaxException) {
                                // Handle cases where the server response is not valid.
                                Log.e("GetAllLoans", "JSON parsing error: ${e.message}")
                                outputTextView.text = "Error: Could not parse server response."

                            }
                        }

                        is Result.Failure -> {
                            // On failure, log the error and show a user-friendly message.
                            val ex = result.getException()
                            Log.e("GetAllLoans", "API Error: ${ex.message}")
                        }
                    }
                }


            }
        }

    }


}