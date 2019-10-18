package com.parcaudiovisualcatalunya.terrassaontour.VuforiaView


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.parcaudiovisualcatalunya.terrassaontour.R

/**
 * A simple [Fragment] subclass.
 */
class VuforiaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vuforia, container, false)
    }


}
