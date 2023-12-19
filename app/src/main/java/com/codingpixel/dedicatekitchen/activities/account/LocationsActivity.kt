package com.codingpixel.dedicatekitchen.activities.account

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.LocationsAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityLocationsBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.TwoButtonsDialogListener
import com.codingpixel.dedicatekitchen.models.UserLocation
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


class LocationsActivity : BaseActivity(), ItemClickListener {

    private lateinit var mBinding: ActivityLocationsBinding

    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var viewModel: UserViewModel
    private val locations = ArrayList<UserLocation>()
    private var placesClient: PlacesClient? = null
    private var defaultAddressPosition: Int = 0
    private var address: String = ""
    private var isDefaultLocation: Int = 0
    private var deletedLocationPosition: Int = 0
    private var geoCoder = Geocoder(this)
    private var placesField: List<Place.Field> =
        listOf(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_locations)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
        geoCoder = Geocoder(this)
        initAdapter()
        initPlaces()
        initClickListener()
        initApiResponseObserver()
        getLocations(showLoading = true)
    }

    private fun getLocations(showLoading: Boolean = false) {
        if (showLoading)
            AppProgressDialog.showProgressDialog(this)
        viewModel.getAddress()
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.GET_LOCATIONS_SUCCESS -> {
                    hideLoading()
                    val location = JsonManager.getInstance().getLocationsList(it.resultObject)
                    locations.clear()
                    locations.addAll(location)
                    val index = locations.indexOfFirst { it.is_default == 1 }
                    if (index != -1) {
                        locations[index].isChecked = true
                        locationsAdapter.notifyDataSetChanged()
                    }
                }


                ApplicationEnum.LOCATION_DELETED_ERROR -> {
                    hideLoading()
                    showWarningToast(message = it.message)
                }

                ApplicationEnum.LOCATION_DELETED_SUCCESS -> {
//                    locations.removeAt(deletedLocationPosition)
//                    locations.clear()
                    getLocations(showLoading = false)
//                    locationsAdapter.notifyDataSetChanged()
                }

                ApplicationEnum.ADD_LOCATION_ERROR -> {
                    hideLoading()
                    showWarningToast(message = it.message)
                }

                ApplicationEnum.ADD_LOCATION_SUCCESS -> {
//                    hideLoading()
                    showShortToast(it.message)
//                    locations.clear()
//                    showLoading()
                    getLocations(showLoading = false)
//                    val location = JsonManager.getInstance().getAddedLocation(it.resultObject)
//                    if (location != null) {
//                        locations.add(0, location)
//                    }
//                    locationsAdapter.notifyDataSetChanged()
                }

                ApplicationEnum.DEFAULT_ADDRESS_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                    locations.forEach { userLocation ->
                        userLocation.isChecked = false
                        userLocation.is_default = 0
                    }
                    locations[defaultAddressPosition].is_default = 1
                    locations[defaultAddressPosition].isChecked = true

                    locationsAdapter.notifyDataSetChanged()

                    viewModel.updateDataCandyAddress(
                        url = RequestBodyUtil.updateDataCandyCreateProfileAddressUrl(
                            cardId = getLoggedInUser()?.dc_card_id ?: "",
                            address = locations[defaultAddressPosition].address,
                            city = locations[defaultAddressPosition].city ?: "",
                            state = locations[defaultAddressPosition].state ?: "",
                            zip = locations[defaultAddressPosition].postal_code ?: ""
                        )
                    )

                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                }
            }
        }
    }

    private fun initClickListener() {

        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.tvLocation.setOnClickListener {
            if (Places.isInitialized()) {
                startActivityForResult(
                    Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, placesField
                    ).setCountry("CA")
                        .build(this), 12
                )
            }
        }
    }

    private fun initPlaces() {
        Places.initialize(this, Constants.GOOGLE_PLACES_API_KEY)
        placesClient = Places.createClient(this)
    }

    private fun initAdapter() {
//        for (i in 0 until 4) {
//            locations.add(UserLocation().apply {
//                is_default = false
//            })
//        }
        locationsAdapter = LocationsAdapter(
            locations = locations,
            itemClickListener = this,
            viewType = Constants.VIEW_TYPE_DISPLAY,
            itemType = Constants.VIEW_TPYE_LARGE
        )
        mBinding.rvLocations.adapter = locationsAdapter
    }

    override fun itemClicked(position: Int) {
        locations.removeAt(position)
        locationsAdapter.notifyDataSetChanged()
    }

    override fun brandTapped(position: Int, title: String) {

    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {
        //radio button tapped
        if (locations[position].is_default == 1) {
            showWarningToast(message = "Already Default Location")
        } else {
            defaultAddressPosition = position
            showLoading()
            makeLocationDefaultApi(addressId)
        }
    }

    override fun deleteLocation(position: Int, addressId: Int) {
        if (locations[position].is_default == 1)
            errorDialogue(
                "Default location will not be deleted",
                this@LocationsActivity
            )
        else {

            CommonMethods.showTwoButtonsAlertDialog(
                fragmentManager = supportFragmentManager,
                title = "Delete Location",
                message = "Are you sure you want to delete this location?",
                rightButtonText = "Yes, Delete",
                leftButtonText = "No",
                twoButtonsDialogListener = object : TwoButtonsDialogListener {
                    override fun leftButtonTapped() {

                    }

                    override fun rightButtonTapped() {
                        showLoading()
                        viewModel.deleteLocation("addresses/delete-address/$addressId")
                    }
                }
            )
        }
    }

    override fun cancelOrder(position: Int, orderId: Int) {
        TODO("Not yet implemented")
    }

    private fun makeLocationDefaultApi(addressId: Int) {
        viewModel.makeDefaultLocation(address_id = addressId)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    // mBinding.tvLocation.text = place.address!!.toString()
                    address = place.address ?: ""
//                    place.latLng
//                    val lat = place.latLng?.latitude.toString()
//                    val lng = place.latLng?.longitude.toString()
//                    val addresses: List<Address> = geoCoder.getFromLocation(
//                        place.latLng?.latitude ?: 0.0,
//                        place.latLng?.longitude ?: 0.0,
//                        1
//                    )
//                    val city = addresses[0].locality
//                    val country = addresses[0].countryName

                    var pickedCity = ""
                    var pickedState = ""
                    var pickedZip = ""

                    if ((place.addressComponents?.asList() ?: listOf()).isNotEmpty()) {
                        for (component in place.addressComponents?.asList() ?: listOf()) {
                            if (component != null) {

                                if (component.types.contains("locality") && component.types.contains(
                                        "political"
                                    )
                                ) {
                                    pickedCity = component.name
                                }

                                if (component.types.contains("administrative_area_level_1") && component.types.contains(
                                        "political"
                                    )
                                ) {
                                    pickedState = component.shortName ?: "CA"
                                }


                                if (component.types.contains("postal_code") || component.types.contains(
                                        "postal_code"
                                    )
                                ) {
                                    if (pickedZip.isEmpty())
                                        pickedZip = component.shortName ?: "CA"
                                    else
                                        pickedZip += " " + (component.shortName ?: "CA")
                                }


                            }
                        }
                    }
                    showLoading()
                    addLocationApi(
                        address,
                        (place.latLng?.latitude ?: 0.0).toString(),
                        (place.latLng?.longitude ?: 0.0).toString(),
                        pickedCity,
                        "CA",
                        pickedZip,
                        pickedState
                    )
                }
                AutocompleteActivity.RESULT_ERROR -> {

                }
                RESULT_CANCELED -> {
                }
            }
        }
    }

    private fun addLocationApi(
        address: String,
        lat: String,
        lng: String,
        city: String?,
        country: String?,
        zipCode: String,
        state: String
    ) {
        if (country != null) {
            if (city != null) {
                viewModel.addLocation(
                    address, lat, lng, city, country,
                    zipCode, state
                )
            }
        }
    }

    override fun orderCompleted(position: Int) {

    }
}