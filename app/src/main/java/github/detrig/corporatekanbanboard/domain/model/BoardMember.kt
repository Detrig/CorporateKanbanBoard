package github.detrig.corporatekanbanboard.domain.model

data class BoardMember(
    val user: User = User(),
    val access: BoardAccess = BoardAccess.VIEWER
) {
    companion object {
        fun sortedByRole(workers: List<BoardMember>): List<BoardMember> {
            return workers.sortedBy { worker ->
                when (worker.access) {
                    BoardAccess.ADMIN -> 0
                    BoardAccess.LEADER -> 1
                    BoardAccess.WORKER -> 2
                    BoardAccess.VIEWER -> 3
                }
            }
        }
    }
}

enum class BoardAccess {
    ADMIN, LEADER, WORKER, VIEWER
}
