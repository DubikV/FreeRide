package com.gmail.vanyadubik.freeride.activity


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.adapter.ResultSearchAdapter
import com.gmail.vanyadubik.freeride.adapter.ReviewListAdapter
import com.gmail.vanyadubik.freeride.common.Consts.EVALUATION_AVAILABLE
import com.gmail.vanyadubik.freeride.common.Consts.EVALUATION_INACCESSIBLE
import com.gmail.vanyadubik.freeride.common.Consts.EVALUATION_TROUBLESOME
import com.gmail.vanyadubik.freeride.common.Consts.POI_REVIEW_URL
import com.gmail.vanyadubik.freeride.common.Consts.TOKEN
import com.gmail.vanyadubik.freeride.model.dto.NewReviewRequest
import com.gmail.vanyadubik.freeride.model.dto.Poi
import com.gmail.vanyadubik.freeride.model.dto.PoiDetailed
import com.gmail.vanyadubik.freeride.model.dto.PoiReview
import com.gmail.vanyadubik.freeride.utils.ActivityUtils
import com.gmail.vanyadubik.freeride.utils.AnimUtils
import com.gmail.vanyadubik.freeride.utils.SharedStorage
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.toolbar_panel.*
import kotlinx.android.synthetic.main.map_bottom_panel.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MapsMVPContract.View{

    private lateinit var map: GoogleMap
    private lateinit var searchOption: MenuItem
    private lateinit var mapsRepository: MapsRepository
    private lateinit var adapterResult: ResultSearchAdapter
    private lateinit var poiBottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>
    private lateinit var reviewListAdapter: ReviewListAdapter

    private var selectedPoi: Poi? = null
    private var isSearchContainerVisible = false
    private var isRouteContainerVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        setSupportActionBar(toolbar)
        initClicklisteners()
        initStartData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.loc_picker_menu, menu)
        searchOption = menu.findItem(R.id.action_search)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_search) {
            if(selectedPoi!=null){
                selectedPoi = null
                hideRouteContainer()
                logoImage.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                searchResult.text = ""
                poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }else{
                showSearchContainer()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    override fun onBackPressed() {
        if (isSearchContainerVisible) {
            hideSearchContainer()
            return
        }
        if (isRouteContainerVisible) {
            hideRouteContainer()
            return
        }
        if(poiBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
            poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        super.onBackPressed()
    }

    override fun onShowPoiList(list: List<Poi>) {
        progressBar.hideAnimation()
        adapterResult.setList(list)
    }

    override fun onErrorApi(textError: String) {
        progressBar.hideAnimation()
        ActivityUtils.showMessage(this, "", null, textError)
    }

    override fun onStartLoadSearch() {
        progressBar.showAnimation()
    }

    override fun onStartLoadDetail() {
        poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        showContainer(false)
    }

    override fun onShowPoiDetail(poiDetailed: PoiDetailed) {
        showRouteContainer()
        showPoiDetailContainer(poiDetailed)
        selectedPoi?.id?.let { mapsRepository.getReviews(it, 0, 20) }
        reviewListAdapter.clear()
    }

    override fun onAddReview() {
        ActivityUtils.showMessage(this,
                getString(R.string.successfully),
                resources.getDrawable(R.drawable.ic_check_white),
                getString(R.string.rating_added))
    }

    override fun onShowListReviews(list: List<PoiReview>) {
        if(list.isNotEmpty() || reviewListAdapter.itemCount>0){
            listReview.visibility = View.VISIBLE
            listReviewEmpty.visibility = View.GONE
            reviewListAdapter.addist(list.toMutableList())
        }else{
            listReview.visibility = View.GONE
            listReviewEmpty.visibility = View.VISIBLE
        }
        listReviewProgressBar.visibility = View.GONE
    }

    override fun onStartLoadListReview() {
        listReviewEmpty.visibility = View.GONE
        listReviewProgressBar.visibility = View.VISIBLE
    }


    private fun initStartData() {

        progressBar.hideAnimation()

        mapsRepository = MapsRepository(this, this)

        searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0?.length!! >3){
                    mapsRepository.getPoiByName(p0.toString())
                }
            }
        })

        if(SharedStorage.getString(this, TOKEN, "")?.isEmpty()!!){
            SharedStorage.setString(this, TOKEN, UUID.randomUUID().toString())
        }

        adapterResult = ResultSearchAdapter(this, object : ResultSearchAdapter.ClickListener{
            override fun onItemClick(position: Int) {
                selectedPoi = adapterResult.getSelectedItem(position)
                onSelectedSearchPoi()
            }

            override fun onClickUpperText(position: Int) {
                searchET.setText(adapterResult.getSelectedItem(position)?.name)
            }

        })
        searchResultRV.layoutManager = LinearLayoutManager(this)
        searchResultRV.adapter = adapterResult

        poiBottomSheetBehavior = BottomSheetBehavior.from<CoordinatorLayout>(bottomSheet)
        poiBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        reviewListAdapter = ReviewListAdapter(this, object : ReviewListAdapter.ClickListener{
            override fun onItemClick(position: Int) {
            }

            override fun onClickUpperText(position: Int) {
            }

        })
        listReview.layoutManager = LinearLayoutManager(this)
        listReview.adapter = reviewListAdapter
    }

    private fun initClicklisteners() {
        clearSearchImage.setOnClickListener {
            progressBar.hideAnimation()
            adapterResult.clear()
            if(searchET.text.isEmpty()){
                hideSearchContainer();
            }else{
                searchET.text.clear()
            }
            poiBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        btnTraffic.setOnClickListener {
            if (map != null && btnTraffic != null) {
                map.isTrafficEnabled = !map.isTrafficEnabled
                btnTraffic.setImageResource(
                        if (map.isTrafficEnabled) R.drawable.ic_traffic_off else R.drawable.ic_traffic_on)
            }
        }

        btnMyLocation.setOnClickListener {
//            geocoderPresenter.getLastKnownLocation()
//            track(TrackEvents.didLocalizeMe)
        }

        reviewBtn.setOnClickListener {
            showAddReview(reviewBtn)
        }
    }

    private fun showSearchContainer() {
        AnimUtils.expand(searchContainer)
        isSearchContainerVisible = true
        searchOption.isVisible = false
    }

    private fun hideSearchContainer() {
        AnimUtils.collapse(searchContainer)
        isSearchContainerVisible = false
        searchOption.isVisible = true
        searchOption.setIcon(R.drawable.ic_search)
    }

    private fun showRouteContainer() {
        AnimUtils.expand(routeContainer)
        isRouteContainerVisible = true
        searchOption.setIcon(R.drawable.ic_clear)
    }

    private fun hideRouteContainer() {
        AnimUtils.collapse(routeContainer)
        isRouteContainerVisible = false
        searchOption.setIcon(R.drawable.ic_search)
    }

    private fun onSelectedSearchPoi() {
        if (isSearchContainerVisible) {
            hideSearchContainer()
        }
        selectedPoi?.id?.let { mapsRepository.getPoiDetail(it) }
        adapterResult.clear()

        logoImage.visibility = View.GONE
        progressBar.visibility = View.GONE
        searchResult.text = selectedPoi?.name
        searchET.setText("")
        searchOption.setIcon(R.drawable.ic_clear)
        ActivityUtils.hideKeyboard(this, searchET)
    }

    private fun showContainer(show: Boolean) {
        if (show) {
            bottomSheetContent.visibility = View.VISIBLE
            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
        } else {
            bottomSheetContent.visibility = View.GONE
            shimmerViewContainer.startShimmer()
            shimmerViewContainer.visibility = View.VISIBLE
        }
    }

    private fun showPoiDetailContainer(poiDet : PoiDetailed){

        when (poiDet.accessible) {
            EVALUATION_AVAILABLE -> {
                statusPoi.setTextColor(resources.getColor(R.color.weirdGreen))
                statusPoi.text = getString(R.string.available) + " " + getString(R.string.object_text)
            }
            EVALUATION_TROUBLESOME -> {
                statusPoi.setTextColor(resources.getColor(R.color.orangeYellow))
                statusPoi.text = getString(R.string.troublesome) + " " + getString(R.string.object_text)
            }
            EVALUATION_INACCESSIBLE -> {
                statusPoi.setTextColor(resources.getColor(R.color.coral))
                statusPoi.text = getString(R.string.inaccessible) + " " + getString(R.string.object_text)
            }
        }
        ratingsPoi.text = if(poiDet.reviews>0) getString(R.string.ratings) + " " + poiDet.reviews else getString(R.string.ratings_not_have)
        namePoi.text = poiDet.name
        typePoi.text = poiDet.types?.joinToString(separator = ", ")
        addressPoi.text = poiDet.address
        workinghoursPoi.text = poiDet.workinghours?.joinToString(separator = ", \n")

        showContainer(true)

    }

    private fun showAddReview(v: View) {

//        var popupWindow: PopupWindow? = null

        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val selectedView = layoutInflater.inflate(R.layout.layout_review, null)


//        if (popupWindow != null) {
//            if (popupWindow.isShowing()) {
//                popupWindow.dismiss()
//            }
//        }

        var popupWindow = PopupWindow(selectedView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        val available = selectedView.findViewById<LinearLayout>(R.id.available)
        available.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            showCommentReview(v, EVALUATION_AVAILABLE)
        }

        val troublesome = selectedView.findViewById<LinearLayout>(R.id.troublesome)
        troublesome.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            showCommentReview(v, EVALUATION_TROUBLESOME)
        }

        val inaccessible = selectedView.findViewById<LinearLayout>(R.id.inaccessible)
        inaccessible.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            showCommentReview(v, EVALUATION_INACCESSIBLE)
        }

        val rateLater = selectedView.findViewById<TextView>(R.id.rateLater)
        rateLater.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
        }

        popupWindow.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTrans)))
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.update()
        popupWindow.setOnDismissListener {
            if (popupWindow != null) {
                if (popupWindow.isShowing) {
                    AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
                }
            }
        }


        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        AnimUtils.enterAnimation(v, selectedView)

    }

    private fun showCommentReview(v: View, status: Int) {

        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val selectedView = layoutInflater.inflate(R.layout.layout_review_comment, null)

        var popupWindow = PopupWindow(selectedView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        popupWindow.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorTrans)))
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.update()
        popupWindow.setOnDismissListener {
            if (popupWindow != null) {
                if (popupWindow.isShowing) {
                    AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
                }
            }
        }

        val comment = selectedView.findViewById<TextView>(R.id.comment)

        val rateCommentLater = selectedView.findViewById<TextView>(R.id.rateCommentLater)
        rateCommentLater.setOnClickListener {
            if (popupWindow.isShowing) {
                AnimUtils.exitAnimation(selectedView, Runnable { popupWindow.dismiss() })
            }
            selectedPoi?.id?.let {
                it1 -> mapsRepository.addReview(it1, NewReviewRequest(status, comment.text.toString(), Calendar.getInstance().timeInMillis))
            }
        }

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        AnimUtils.enterAnimation(v, selectedView)

    }

}
