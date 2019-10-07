package ru.izifak

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.audio_item.view.*
import ru.izifak.models.AudioBook
import androidx.recyclerview.widget.ItemTouchHelper
import ru.izifak.core.helper.ItemTouchHelperAdapter
import ru.izifak.core.helper.ItemTouchHelperViewHolder
import ru.izifak.core.helper.SimpleItemTouchHelperCallback
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import android.content.Intent
import android.widget.Toast
import kotlin.random.Random
import android.media.MediaPlayer
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    private var mItemTouchHelper: ItemTouchHelper? = null

    private val SELECT_FILE_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        textTitleToolbar.typeface = Typeface.createFromAsset(assets, "font/Activist.ttf")

        mainAudioRec.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        val adapter = AudioRecAdapter(
            arrayListOf(
                AudioBook("lolkek", 123123, ""),
                AudioBook("lolkek2", 123123, "")
            ),
            this
        )

        mainAudioRec.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.main_rec_layout_item_appearence)
        mainAudioRec.adapter = adapter


        val callback = SimpleItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(mainAudioRec)

        addAudioFab.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(intent, SELECT_FILE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SELECT_FILE_CODE){
            //Toast.makeText(this, data?.data.toString(), Toast.LENGTH_LONG).show()
            val mp = MediaPlayer.create(this, data?.data)
            mp.start()
        }
    }

}

class AudioRecAdapter(private val books: ArrayList<AudioBook>, private val fa: FragmentActivity) :
    RecyclerView.Adapter<AudioRecAdapter.ViewHolder>(),
    ItemTouchHelperAdapter {

    private var lastPos = -1

    override fun onItemDismiss(position: Int) {
        books.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val prev = books.removeAt(fromPosition)
        books.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, prev)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.audio_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //plug
        Thread {
            Thread.sleep(3000)
            fa.runOnUiThread {
                holder.itemView.textTitleAudioItem.text = books[position].name
                holder.itemView.imgAudioItem.setImageDrawable(fa.getDrawable(R.drawable.main_icon))
            }
        }.start()
        holder.itemView.root.setOnClickListener {

        }
        holder.itemView.imageAudioDelete.setOnClickListener {

        }
        holder.itemView.imageAudioEdit.setOnClickListener {

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {

        override fun onItemSelected() {

        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }

    }
}
