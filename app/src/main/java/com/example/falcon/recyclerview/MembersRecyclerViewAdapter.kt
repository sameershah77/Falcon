package com.example.falcon.recyclerview
import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.MemberData
import com.example.falcon.view.MemberProfileActivity
import com.squareup.picasso.Picasso
open class MembersRecyclerViewAdapter(context: Context, arr: ArrayList<MemberData>) : RecyclerView.Adapter<MembersRecyclerViewAdapter.ViewHolder>(){

    var arr: ArrayList<MemberData> = arr
    val context:Context = context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.layout_member_name)
        val email = itemView.findViewById<TextView>(R.id.layout_member_email)
        val image = itemView.findViewById<ImageView>(R.id.layout_member_image)
        val member_card_id = itemView.findViewById<ConstraintLayout>(R.id.member_card_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.members_layout_design,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(arr.get(position).name)
        holder.email.setText(arr.get(position).email)
        Picasso.get().load(arr.get(position).img.toUri()).into(holder.image)

        val intent = Intent(context, MemberProfileActivity::class.java)

        holder.member_card_id.setOnClickListener {
            val member = arr.get(position)

            val uid = member.uid
            val name = member.name
            val email = member.email
            val img = member.img
            val date = member.registerDate
            val desc = member.desc

            intent.putExtra("uid",uid)
            intent.putExtra("name",name)
            intent.putExtra("email",email)
            intent.putExtra("img",img)
            intent.putExtra("date",date)
            intent.putExtra("desc",desc)
            context.startActivity(intent)
        }
    }
}
