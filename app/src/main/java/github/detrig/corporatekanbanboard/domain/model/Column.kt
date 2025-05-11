package github.detrig.corporatekanbanboard.domain.model

data class Column(
    val id: String = "",
    var title: String = "",
    val tasksId: List<String> = emptyList<String>()
)