package github.detrig.corporatekanbanboard.domain.model

import java.util.UUID

data class Column(
    val id: String = "",
    var title: String = "",
    var responsiblePerson: List<BoardMember> = emptyList(),
    var workers: List<BoardMember> = emptyList(),
    val tasks: List<Task> = emptyList()
) {
    companion object {
        fun newInstance(title: String): Column {
            return Column(
                id = UUID.randomUUID().toString(),
                title = title,
                tasks = emptyList()
            )
        }
    }
}