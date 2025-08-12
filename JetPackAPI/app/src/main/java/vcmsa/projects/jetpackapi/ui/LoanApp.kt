package vcmsa.projects.jetpackapi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import vcmsa.projects.jetpackapi.ui.api.LoanViewModel

object LoanScreenRoute {
    const val LIST = "list"
    const val CREATE = "create"
}

@Composable
fun LoanApp(){
    var loanViewModel : LoanViewModel = viewModel()
    var currenScreen by remember { mutableStateOf(LoanScreenRoute.LIST) }


    when(currenScreen){
        LoanScreenRoute.LIST -> {
            LoanListScreen(
                loanViewModel = loanViewModel,
                onNavigateToCreate = {
                    currenScreen = LoanScreenRoute.CREATE
                }
            )
        }

        LoanScreenRoute.CREATE -> {
            CreateLoanScreen(
                loanViewModel = loanViewModel,
                onLoanCreated = {
                    currenScreen = LoanScreenRoute.LIST //navigate back to list
                }
            )
        }
    }
}