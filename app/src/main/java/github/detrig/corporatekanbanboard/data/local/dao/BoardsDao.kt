package github.detrig.corporatekanbanboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import github.detrig.corporatekanbanboard.data.local.model.BoardEntity
import github.detrig.corporatekanbanboard.domain.model.Board

@Dao
interface BoardsDao {
    @Query("SELECT * FROM boards")
    suspend fun getAllBoards(): List<BoardEntity>

    @Query("SELECT * FROM boards WHERE id = :id")
    suspend fun getBoardById(id: String): BoardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoards(boards: List<BoardEntity>)

    @Update
    suspend fun updateBoard(board: BoardEntity)

    @Query("DELETE FROM boards WHERE id = :id")
    suspend fun deleteBoardById(id: String)

    @Query("DELETE FROM boards")
    suspend fun clearAll()
}