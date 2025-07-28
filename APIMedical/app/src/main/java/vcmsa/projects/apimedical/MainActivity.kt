package vcmsa.projects.apimedical

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.kittinunf.fuel.core.Handler
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = android.os.Handler(Looper.getMainLooper())
    private lateinit var outputTextView: TextView
    private lateinit var inputIdEditText: EditText


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

        findViewById<Button>(R.id.btnGetAll).setOnClickListener {
            hideKeyboard()
            getAllLoans()
        }

        findViewById<Button>(R.id.btnGetById).setOnClickListener {
            hideKeyboard()
            var inputText = inputIdEditText.text.toString()
            if (inputText.isNotEmpty()) {
                try {
                    // Try to convert the input to an integer
                    val idAsInt = inputText.toInt()
                    getLoanById(idAsInt)
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        this,
                        "Please enter a valid numeric Loan ID.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            inputIdEditText.text = null
        }

        findViewById<Button>(R.id.btnGetByMember).setOnClickListener {
            hideKeyboard()
            getLoansByMemberId(inputIdEditText.text.toString())
            inputIdEditText.text = null
        }

        findViewById<Button>(R.id.btnCreate).setOnClickListener {
            hideKeyboard()
            createNewLoan()
            inputIdEditText.text = null
        }
    }

    private fun getAllLoans() {

        // Define the API endpoint URL.
        val url = "https://opsc.azurewebsites.net/loans/"
        outputTextView.text = "Fethcing all loans..."

        // Execute the network request on a background thread.
        executor.execute {
            // Use Fuel's httpGet for a GET request
            url.httpGet().responseString { _, _, result ->
                // Switch to the main thread to update the UI.
                handler.post {
                    when (result) {
                        is Result.Success -> {
                            // On success, deserialize the JSON string into a list of loan objects.
                            val json = result.get()
                            try {
                                val loans = Gson().fromJson(json, Array<Loan>::class.java).toList()
                                if (loans.isNotEmpty()) {
                                    val formattedOutput =
                                        loans.joinToString(separator = "\n\n") { loan ->
                                            "Loan ID: ${loan.loanID} \n" +
                                                    "Amount: ${loan.amount}\n" +
                                                    "Member ID: ${loan.memberID}\n" +
                                                    "Message: ${loan.message}"
                                        }

                                    outputTextView.text = formattedOutput
                                } else {
                                    outputTextView.text = "No loans found."
                                }
                            } catch (e: JsonSyntaxException) {
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

    private fun getLoanById(Id: Int) {

        // Define the API endpoint URL.
        val url = "https://opsc.azurewebsites.net/loans/$Id"
        outputTextView.text = "Fethcing loan with ID: $Id..."

        // Execute the network request on a background thread.
        executor.execute {
            // Use Fuel's httpGet for a GET request
            url.httpGet().responseString { _, response, result ->
                // Switch to the main thread to update the UI.
                handler.post {

                    if (response.statusCode == 404) {
                        outputTextView.text = "Loan with ID $Id not found."
                        return@post
                    }


                    when (result) {
                        is Result.Success -> {
                            // On success, deserialize the JSON string into a list of loan objects.
                            val json = result.get()
                            try {
                                val loan = Gson().fromJson(json, Loan::class.java)

                                val formattedOutput = "Loan ID: ${loan.loanID} \n" +
                                        "Amount: ${loan.amount}\n" +
                                        "Member ID: ${loan.memberID}\n" +
                                        "Message: ${loan.message}"

                                outputTextView.text = formattedOutput
                            } catch (e: JsonSyntaxException) {
                                // Handle cases where the server response is not valid.
                                Log.e("GetLoanById", "JSON parsing error: ${e.message}")
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

    private fun getLoansByMemberId(Id: String) {

        // Define the API endpoint URL.
        val url = "https://opsc.azurewebsites.net/loans/member/$Id"
        outputTextView.text = "Fetching loans for member with ID: $Id..."

        // Execute the network request on a background thread.
        executor.execute {
            // Use Fuel's httpGet for a GET request
            url.httpGet().responseString { _, response, result ->
                // Switch to the main thread to update the UI.
                handler.post {

                    if (response.statusCode == 404) {
                        outputTextView.text =
                            "Loans for member with ID $Id not found.\n" + "Response from server:\n ${response.toString()}"
                        return@post
                    }


                    when (result) {
                        is Result.Success -> {
                            // On success, deserialize the JSON string into a list of loan objects.
                            val json = result.get()
                            try {
                                val loans = Gson().fromJson(json, Array<Loan>::class.java).toList()
                                if (loans.isNotEmpty()) {
                                    val formattedOutput =
                                        loans.joinToString(separator = "\n\n") { loan ->
                                            "Loan ID: ${loan.loanID} \n" +
                                                    "Amount: ${loan.amount}\n" +
                                                    "Member ID: ${loan.memberID}\n" +
                                                    "Message: ${loan.message}"
                                        }

                                    outputTextView.text = formattedOutput
                                } else {
                                    outputTextView.text = "No loans found."
                                }
                            } catch (e: JsonSyntaxException) {
                                // Handle cases where the server response is not valid.
                                Log.e("GetLoanById", "JSON parsing error: ${e.message}")
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

    private fun createNewLoan() {
        val url = "https://opsc.azurewebsites.net/loans/"
        outputTextView.text = "Creating a new loan..."
        executor.execute {
            // Create a new loan object to send as the request body.
            val newLoan = LoanPost(
                "15.99", "M6001",
                "Added by the Android app"
            )
            val jsonBody = Gson().toJson(newLoan)

            url.httpPost()
                .jsonBody(jsonBody) // Set the request body.
                .responseString { _, response, result ->
                    handler.post {
                        when (result) {
                            // A 201 Created status code indicates success.
                            is Result.Success -> {
                                if (response.statusCode == 201) {
                                    try {
                                        val createdLoan = Gson().fromJson(
                                            result.get(),
                                            Loan::class.java
                                        )
                                        outputTextView.text =
                                            "Successfully created loan:\n\nLoan ID:" +
                                                    " ${createdLoan.loanID}\nAmount: ${createdLoan.amount}"
                                    } catch (e: JsonSyntaxException) {
                                        Log.e("CreateNewLoan", "JSON parsing error: ${e.message}")
                                        outputTextView.text =
                                            "Loan created, but failed to parse response."
                                    }
                                } else {
                                    outputTextView.text =
                                        "Failed to create loan. Status: ${response.statusCode}"
                                }
                            }

                            is Result.Failure -> {
                                val ex = result.getException()
                                Log.e("CreateNewLoan", "API Error: ${ex.message}")
                                outputTextView.text = "Error: Could not create loan."
                            }
                        }
                    }
                }
        }
    }


    private fun createNEwLoan_old() {

        // Define the API endpoint URL.
        val url = "https://opsc.azurewebsites.net/loans/"
        outputTextView.text = "Creating new loan"

        // Execute the network request on a background thread.
        executor.execute {

            // Create a new loan object and serialize it to JSON
            val newLoan = LoanPost("15.99", "M6001", "Added by the Android app")
            val jsonBodyData = Gson().toJson(newLoan)

            Log.d("monte", "Json body I am sending ${jsonBodyData}")
            var newRequest: Request = url.httpPost()
            newRequest.jsonBody(jsonBodyData)
            Log.d("monte", "Request: ${newRequest.toString()}")

            newRequest.responseString { _, response, result ->
                // Switch to the main thread to update the UI.
                handler.post {

                    when (result) {

                        is Result.Success -> {

                            if (response.statusCode == 201) {
                                // On success, deserialize the JSON string into a list of loan objects.
                                try {
                                    val createdLoan =
                                        Gson().fromJson(result.get(), Loan::class.java)
                                    outputTextView.text = "Successfully created loan: \n\n" +
                                            "Loan ID: ${createdLoan.loanID}\n\n" +
                                            "Amount: ${createdLoan.amount}"
                                } catch (e: JsonSyntaxException) {
                                    // Handle cases where the server response is not valid.
                                    Log.e("CreateNewLoan", "JSON parsing error: ${e.message}")
                                    outputTextView.text = "Error: Could not parse server response."

                                }
                            } else {
                                outputTextView.text =
                                    "Failed to create loan. Status: ${response.statusCode}"

                            }
                        }

                        is Result.Failure -> {
                            // On failure, log the error and show a user-friendly message.
                            val ex = result.getException()
                            Log.e(
                                "GetAllLoans",
                                "API Error: ${ex.message}\n\nAPI Response: ${response.toString()}"
                            )
                            outputTextView.text =
                                "API Error. Please check logs.\n\nAPI Response: ${response.toString()}\n\nResult: ${result.toString()}"
                        }
                    }
                }


            }
        }

    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputIdEditText.windowToken, 0)
    }


}