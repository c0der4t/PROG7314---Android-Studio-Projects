package vcmsa.projects.jetpackapi

data class LoanResponse(
    val loanId: Int,
    val amount: Double,
    val memberID: String,
    val message: String,
)


data class LoanRequest(
    val amount: Double,
    val memberID: String,
    val message: String
)