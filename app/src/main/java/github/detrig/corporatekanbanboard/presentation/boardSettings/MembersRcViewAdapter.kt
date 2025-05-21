package github.detrig.corporatekanbanboard.presentation.boardSettings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.databinding.RcViewMemberItemBinding
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.User

class MembersRcViewAdapter(
    private val listener: OnMemberClickListener,
    private val usersList: List<User>
) :
    RecyclerView.Adapter<MembersRcViewAdapter.ViewHolder>() {

    val list: MutableSet<BoardMember> = mutableSetOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewMemberItemBinding.bind(view)

        fun bind(member: BoardMember, usersList: List<User>, listener: OnMemberClickListener) = with(binding) {

            memberNameTextView.text = member.name
            memberEmailTextView.text = member.email
            when (member.access) {
                BoardAccess.ADMIN -> {
                    deleteMemberButton.visibility = View.GONE
                    memberRoleTextView.text = "Роль: Админ"
                }

                BoardAccess.LEADER -> memberRoleTextView.text = "Роль: Лидер"
                BoardAccess.WORKER -> memberRoleTextView.text = "Роль: Работник"
                BoardAccess.VIEWER -> memberRoleTextView.text = "Роль: Наблюдатель"
            }

            val user = usersList.find { it.id == member.userId } ?: User()
            val bitmap = ImageUtils.base64ToBitmap(user.avatarBase64)

            val requestOptions = RequestOptions()
                .circleCrop() // или .transform(RoundedCorners(16)) для закругления углов
                .placeholder(R.drawable.user)
                .error(R.drawable.user)

            Glide.with(memberAvatarImageView.context)
                .load(bitmap ?: R.drawable.user)
                .apply(requestOptions)
                .into(memberAvatarImageView)

            itemView.setOnClickListener {
                listener.onClick(member)
            }

            deleteMemberButton.setOnClickListener {
                listener.deleteMemberClickListener(member)
            }
        }
    }

    fun update(newList: ArrayList<BoardMember>) {
        val diffUtil = DiffUtilCallBack(list.toList(), newList)
        val diff = DiffUtil.calculateDiff(diffUtil)

        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rc_view_member_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.toList()[position], usersList, listener)
    }

    class DiffUtilCallBack(
        private val old: List<BoardMember>,
        private val new: List<BoardMember>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem == newItem
        }

    }

    interface OnMemberClickListener {
        fun onClick(member: BoardMember)
        fun deleteMemberClickListener(member: BoardMember)
    }
}