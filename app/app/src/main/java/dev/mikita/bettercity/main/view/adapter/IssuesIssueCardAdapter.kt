package dev.mikita.bettercity.main.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import dev.mikita.bettercity.main.view.MainActivity
import dev.mikita.bettercity.R
import dev.mikita.bettercity.main.data.IssueCardData
import dev.mikita.bettercity.main.view.HomeFragmentDirections
import javax.inject.Inject

class IssuesIssueCardAdapter @Inject constructor(private val context: Context?): RecyclerView.Adapter<IssuesIssueCardAdapter.IssueCardViewHolder>() {
    var issues: List<IssueCardData> = emptyList()

    class IssueCardViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        val issueImage: ImageView = view!!.findViewById(R.id.issue_card_image)
        val issueTitle: TextView = view!!.findViewById(R.id.issue_card_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.issue_card, parent, false)

        return IssueCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return issues.size
    }

    override fun onBindViewHolder(holder: IssueCardViewHolder, position: Int) {
        val item = issues[position]

        holder.issueImage.load(item.photo)
        holder.issueTitle.text = item.title

        holder.itemView.setOnClickListener {
            val activity = context as? MainActivity
            val mainNavController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
            val action = HomeFragmentDirections.actionHomeFragmentToIssueFragment(item.id)

            mainNavController.navigate(action)
        }
    }
}