package vcmsa.projects.jetpackapi.ui.api

import vcmsa.projects.jetpackapi.LoanResponse

sealed interface LoanUIState {
    data object Loading: LoanUIState
    data class Success(val loans:List<LoanResponse>) : LoanUIState
    data class Error(val message: String) : LoanUIState
}