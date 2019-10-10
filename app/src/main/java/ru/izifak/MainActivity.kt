package ru.izifak

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.LayoutTransition
import android.annotation.SuppressLint
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
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.util.Log
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.izifak.api.AudioBookDatabaseApi


class MainActivity : AppCompatActivity() {

    private var mItemTouchHelper: ItemTouchHelper? = null
    private lateinit var sharedPreferences: SharedPreferences

    private val SELECT_FILE_CODE = 1

    private lateinit var adapter: AudioRecAdapter
    private var adapterData: ArrayList<AudioBook> = arrayListOf()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var v: View? = null
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        setSupportActionBar(toolbar)

        textTitleToolbar.typeface = Typeface.createFromAsset(assets, "font/Activist.ttf")
        mainNoAudioBooksText.typeface = Typeface.createFromAsset(assets, "font/Activist.ttf")

        mainAudioRec.layoutManager = LinearLayoutManager(this)
        adapter = AudioRecAdapter(
            adapterData,
            this,
            if (sharedPreferences.getInt("lastBooksAmount", 3) > 3)
                sharedPreferences.getInt("lastBooksAmount", 3)
            else
                3
        )

        GlobalScope.launch(Dispatchers.IO) {
            adapterData =
                AudioBookDatabaseApi.getInstance(this@MainActivity).audioBookDao().allAudioBooks as ArrayList<AudioBook>
            adapterData.sortBy {
                it.order
            }
            adapter.setBooks(adapterData)

            Log.d("MainActivity", adapterData.size.toString())
            adapter.notifyDataSetChanged()
        }

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

        if (v == null) {
            v = layoutInflater.inflate(R.layout.audio_book_creation_alert, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.edit().putInt("lastBooksAmount", adapterData.size).apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_FILE_CODE && data?.data != null) {
            //Toast.makeText(this, data?.data.toString(), Toast.LENGTH_LONG).show()
//            val mp = MediaPlayer.create(this, data?.data)
//            mp.start()
            MaterialDialog(this, BottomSheet()).show {
                (v?.findViewById<TextView>(R.id.alertAudioBookTitle))?.typeface =
                    Typeface.createFromAsset(assets, "font/Activist.ttf")
                customView(view = v)
                cornerRadius(16f)
                positiveButton(R.string.add_audio_book_pos_button) {

                }

                onDismiss {
                    GlobalScope.launch(Dispatchers.IO) {
                        val exemplr = AudioBook(
                            v?.findViewById<EditText>(R.id.audioBookNameEdit)?.text.toString(),
                            0,
                            "",
                            data.data.toString(),
                            System.currentTimeMillis()
                        )
                        AudioBookDatabaseApi.getInstance(this@MainActivity).audioBookDao().insertAll(
                            exemplr
                        )

                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Book successfully added to your bookshelf",
                                Toast.LENGTH_LONG
                            ).show()

                            adapterData.add(0, exemplr)

                            adapter.setBooks(adapterData)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        }
    }

}

class AudioRecAdapter(
    private var books: ArrayList<AudioBook>,
    private val fa: FragmentActivity,
    private var loadBooksAmound: Int
) :
    RecyclerView.Adapter<AudioRecAdapter.ViewHolder>(),
    ItemTouchHelperAdapter {

    private var lastPos = -1

    fun setBooks(books: ArrayList<AudioBook>) {
        this.books = books
    }

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
        return loadBooksAmound
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //plug
        Thread {
            Thread.sleep(1000)
            fa.runOnUiThread {
                if (position < books.size) {
                    holder.itemView.textTitleAudioItem.text = books[position].name
                    holder.itemView.imgAudioItem.setImageDrawable(fa.getDrawable(R.drawable.main_icon))
                }
                if (books.size == 0) {
                    fa.mainAudioRec.animate().alpha(0f).setDuration(500).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            fa.mainNoAudioBooks.visibility = View.VISIBLE
                        }
                    }).start()
                }
                loadBooksAmound = books.size
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
