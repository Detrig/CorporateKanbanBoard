package github.detrig.corporatekanbanboard.domain.model

import java.util.UUID

data class Column(
    val id: String = "",
    var title: String = "",
    val taskIds: Map<String, String> = emptyMap<String, String>() //id - title
) {
    companion object {
        fun newInstance(title: String): Column {
            return Column(
                id = UUID.randomUUID().toString(),
                title = title,
                taskIds = emptyMap()
            )
        }
    }
}