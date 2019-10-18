package com.parcaudiovisualcatalunya.terrassaontour.MapView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.GravityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.parcaudiovisualcatalunya.terrassaontour.R
import kotlinx.android.synthetic.main.drawer_menu.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.rutes_nav_header.*

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, Animation.AnimationListener {

    private lateinit var mMap: GoogleMap

    private var popupAnimation: Animation? = null
    private var popOutAnimation: Animation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.drawer_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setUpAnimations()
        setCloseRuteButton()
        setUpRoutesDrawer()
    }

    private fun turnCloseRuteButton(On: Boolean){
        if (On) {
            closeRuteFab.show()
            closeRuteFab.startAnimation(popupAnimation)
            //startLoadingRouteAnimation(true)
        } else {
            closeRuteFab.startAnimation(popOutAnimation)
        }
    }

    private fun closeRutesMenu(){
        drawer_menu.closeDrawers()
    }

    private fun openRutesMenu(){
        drawer_menu.openDrawer(GravityCompat.START)
    }

    private fun setUpRoutesDrawer(){
        rutesButton.setOnClickListener {
            openRutesMenu()
        }
        closeButtonFromDrawer.setOnClickListener {
            closeRutesMenu()
        }
    }

    private fun setUpAnimations(){
        popupAnimation = AnimationUtils.loadAnimation(activity, R.anim.popup)
        popOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.popout)
        popOutAnimation?.setAnimationListener(this)
    }

    private fun setCloseRuteButton(){
        closeRuteFab.hide()
        closeRuteFab.setOnClickListener {
            turnCloseRuteButton(false)
            /*dbHelper.removeCurrentPreviousRouteOnAppStart()
            if (currentRoutePolyline != null){
                currentRoutePolyline!!.remove()
            }
            oldRoutePois.clear()*/
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val mapSettings = mMap.uiSettings
        mapSettings.isMapToolbarEnabled = false
        mapSettings.isMyLocationButtonEnabled = false
        mapSettings.isCompassEnabled = false

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.510161,0.3189047), 12f))

        mMap.setOnInfoWindowClickListener(this)
    }

    override fun onInfoWindowClick(p0: Marker?) {

    }

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        closeRuteFab.hide()
    }

    override fun onAnimationStart(animation: Animation?) {}
}
