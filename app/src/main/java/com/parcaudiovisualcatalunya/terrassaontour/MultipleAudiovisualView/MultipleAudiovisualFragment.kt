package com.parcaudiovisualcatalunya.terrassaontour.MultipleAudiovisualView


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.parcaudiovisualcatalunya.terrassaontour.R

/**
 * A simple [Fragment] subclass.
 */
class MultipleAudiovisualFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multiple_audiovisual, container, false)
    }


}
