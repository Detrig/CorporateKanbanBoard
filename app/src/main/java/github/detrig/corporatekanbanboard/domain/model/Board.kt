package github.detrig.corporatekanbanboard.domain.model

import com.google.gson.Gson
import github.detrig.corporatekanbanboard.data.local.model.BoardEntity

data class Board(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val creatorEmail: String = "",
    val creatorId: String = "",
    val dateCreated: String = "",
    val photoBase64: String = "",
    val members: List<BoardMember> = emptyList(),
    val columns: List<Column> = emptyList()
) {
    fun toEntity(): BoardEntity {
        val gson = Gson()
        return BoardEntity(
            id = id,
            title = title,
            description = description,
            creatorEmail = creatorEmail,
            creatorId = creatorId,
            dateCreated = dateCreated,
            photoBase64 = photoBase64,
            membersJson = gson.toJson(members),
            columnsJson = gson.toJson(columns)
        )
    }
}