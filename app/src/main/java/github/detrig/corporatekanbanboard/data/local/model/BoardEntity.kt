package github.detrig.corporatekanbanboard.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.Column

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val creatorEmail: String,
    val creatorId: String,
    val dateCreated: String,
    val photoBase64: String,
    val membersJson: String,
    val columnsJson: String
) {
    fun toDomain() : Board {
        val gson = Gson()
        return Board(
            id = id,
            title = title,
            description = description,
            creatorEmail = creatorEmail,
            dateCreated = dateCreated,
            photoBase64 = photoBase64,
            members = gson.fromJson(membersJson, object : TypeToken<List<BoardMember>>() {}.type),
            columns = gson.fromJson(columnsJson, object : TypeToken<List<Column>>() {}.type)
        )
    }
}