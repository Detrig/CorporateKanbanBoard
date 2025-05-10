package github.detrig.corporatekanbanboard.domain.model

data class BoardMember(
    val user: User,
    val access: BoardAccess = BoardAccess.VIEWER
)

enum class BoardAccess{
    ADMIN, LEADER, WORKER, VIEWER
}
