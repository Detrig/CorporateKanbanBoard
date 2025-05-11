package github.detrig.corporatekanbanboard.data.local.model

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.Column

class BoardTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromMembersList(members: List<BoardMember>): String =
        gson.toJson(members)

    @TypeConverter
    fun toMembersList(data: String): List<BoardMember> =
        gson.fromJson(data, object : TypeToken<List<BoardMember>>() {}.type)

    @TypeConverter
    fun fromColumnsList(columns: List<Column>): String =
        gson.toJson(columns)

    @TypeConverter
    fun toColumnsList(data: String): List<Column> =
        gson.fromJson(data, object : TypeToken<List<Column>>() {}.type)
}