package github.detrig.corporatekanbanboard.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val avatarBase64: String = "",
    val boardIds: List<String> = emptyList()
)