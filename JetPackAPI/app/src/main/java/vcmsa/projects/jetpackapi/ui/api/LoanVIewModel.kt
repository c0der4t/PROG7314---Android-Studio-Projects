package vcmsa.projects.jetpackapi.ui.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import vcmsa.projects.jetpackapi.LoanRequest
import vcmsa.projects.jetpackapi.LoanResponse
import vcmsa.projects.jetpackapi.RetrofitClient
import java.util.concurrent.Executors

data class LoanScreenState(
    val loans: List<LoanResponse> = emptyList(),
    val isLoading : Boolean = false,
    val error: String? = null
)


class LoanViewModel: ViewModel() {
    private val _UIState = MutableStateFlow(LoanScreenState())
    val UiState: StateFlow<LoanScreenState> = _UIState

    // the init block immediately calls fetchloans to load data
    // when the viewmodel isi insantiate
    init{
        fetchLoans()
    }
    fun fetchLoans(){
        _UIState.value = _UIState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val loans = RetrofitClient.instance.getLoans()
                _UIState.value = _UIState.value.copy(isLoading = false, loans = loans)
            } catch (e: Exception){
                _UIState.value = _UIState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun createLoan(amount: Double, memberID: String, message: String){
        viewModelScope.launch {
            try {
                val newLoan = LoanRequest(amount, memberID, message)
                RetrofitClient.instance.createLoan(newLoan)

                //After successfully creating, refresh the list to show the new item
                fetchLoans()
            }catch (e: Exception){
                _UIState.value = _UIState.value.copy(isLoading = false, error = "Failed to create loan: ${e.message}")
            }
        }
    }


    fun deleteLoan(loanId: Int){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.deleteLoan(loanId)

                if (response.isSuccessful){
                    fetchLoans()
                }else{
                    // The server returned an error code (e.g., 404 not found, 500 server error)
                    _UIState.value = _UIState.value.copy(error = "Error: ${response.code()} ${response.message()}")
                }

            }catch (e: Exception){
                // This catches network errors, like no internet connection
                _UIState.value = _UIState.value.copy(error = "Failed to delete loan: ${e.message}")

            }
        }
    }
}