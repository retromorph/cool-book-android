package ru.izifak

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment


class FileOverviewFragment : Fragment() {

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.file_overview, container, false)
        v.findViewById<TextView>(R.id.fileOverviewTitle)?.typeface =
            Typeface.createFromAsset(activity?.assets, "font/Activist.ttf")
        return v
    }
}