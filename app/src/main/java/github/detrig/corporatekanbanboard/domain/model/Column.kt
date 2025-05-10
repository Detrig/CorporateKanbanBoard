package github.detrig.corporatekanbanboard.domain.model

data class Column(
    val id: String = "",
    val title: String = "",
    val tasksId: List<String> = emptyList<String>()
)