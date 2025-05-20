package github.detrig.corporatekanbanboard.presentation.addtask

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.ImageUtils
import github.detrig.corporatekanbanboard.databinding.RcViewWorkerItemBinding
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.BoardMember

class WorkersRcViewAdapter(
    private val listener: OnWorkerClickListener,
) :
    RecyclerView.Adapter<WorkersRcViewAdapter.ViewHolder>() {

    val list: ArrayList<BoardMember> = arrayListOf()
    var selectedWorkersList: ArrayList<BoardMember> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RcViewWorkerItemBinding.bind(view)

        fun bind(worker: BoardMember, listener: OnWorkerClickListener, selectedWorkersList: ArrayList<BoardMember>) = with(binding) {

            memberNameTextView.text = worker.user.name
            memberEmailTextView.text = worker.user.email
            selectCheckBox.isChecked = selectedWorkersList.contains(worker)

            when (worker.access) {
                BoardAccess.ADMIN -> memberRoleTextView.text = "Роль: Админ"
                BoardAccess.LEADER -> memberRoleTextView.text = "Роль: Лидер"
                BoardAccess.WORKER -> memberRoleTextView.text = "Роль: Работник"
                BoardAccess.VIEWER -> memberRoleTextView.text = "Роль: Наблюдатель"
            }

            val bitmap = ImageUtils.base64ToBitmap(worker.user.avatarBase64)
            val requestOptions = RequestOptions()
                .circleCrop() // или .transform(RoundedCorners(16)) для закругления углов
                .placeholder(R.drawable.user)
                .error(R.drawable.user)

            Glide.with(memberAvatarImageView.context)
                .load(bitmap ?: R.drawable.user)
                .apply(requestOptions)
                .into(memberAvatarImageView)


            itemView.setOnClickListener {
                listener.onClick(worker)
            }

            selectCheckBox.setOnClickListener {
                if (selectCheckBox.isChecked) {
                    selectedWorkersList.add(worker)
                } else {
                    selectedWorkersList.remove(worker)
                }
                listener.onSelectWorker(worker)
            }

        }
    }

    fun update(newList: ArrayList<BoardMember>) {
        val diffUtil = DiffUtilCallBack(list, newList)
        val diff = DiffUtil.calculateDiff(diffUtil)
        Log.d("alz-04", "newList rcView: ${newList.map{it.user.email}}")
        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rc_view_worker_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], listener, selectedWorkersList)
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

            return oldItem.user.id == newItem.user.id

        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = old[oldItemPosition]
            val newItem = new[newItemPosition]

            return oldItem.user == newItem.user
        }

    }

    interface OnWorkerClickListener {
        fun onClick(worker: BoardMember)
        fun onSelectWorker(worker: BoardMember)
    }
}