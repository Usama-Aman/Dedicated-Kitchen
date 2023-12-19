package com.codingpixel.dedicatekitchen.activities.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.auth.RegisterActivity
import com.codingpixel.dedicatekitchen.activities.checkout.CartActivity
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Product.Product
import com.codingpixel.dedicatekitchen.database.ProductExtraOption.ProductExtraOption
import com.codingpixel.dedicatekitchen.database.ProductOption.ProductOption
import com.codingpixel.dedicatekitchen.databinding.ActivityMainBottomBarBinding
import com.codingpixel.dedicatekitchen.fragments.GuestAccountFragment
import com.codingpixel.dedicatekitchen.fragments.bottomtabs.*
import com.codingpixel.dedicatekitchen.fragments.dialogs.SelectKitchenDialog
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.SelectListener
import com.codingpixel.dedicatekitchen.interfaces.SessionListener
import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.updates.generic_model.GetAllKitchenModel
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainBottomBarActivity : BaseActivity()
//    BottomNavigationView.OnNavigationItemSelectedListener
{

    private lateinit var mBinding: ActivityMainBottomBarBinding
    private lateinit var currentFragment: Fragment
    private var doubleBackToExitPressedOnce = false
    private lateinit var viewModel: MenuViewModel
    private var cat = JSONObject()
    private var menuItem = JSONObject()
    private var products = ArrayList<MenuItemModel>()
    private var menuItemsSize: Int = 0
    private var categoriesImages = ArrayList<CategoryImage>()
    private lateinit var selectedCategory: Category
    private var optionsList = ArrayList<Category>()
    private var categories = ArrayList<Category>()

    private var currentKitchenIndex: Int = 0
    private var currentCategoryIndex: Int = 0

    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_bottom_bar)
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
        initTopText()
        getApiResponseObserver()
        initClickListener()

        /*Edited by Usama*/
//        setHeaderLocationTimeText()
        appDatabase = AppDatabase(this@MainBottomBarActivity)


            initHomeFragment()

        /*Edited by Usama*/
//        getCategoryFromDB()

        getAllKitchens()

//        val s =  "${ApiUrls.DATA_CANDY_LOYALTY_URL}/ms2v2/trx/${Constants.DC_ACCESS_KEY}/fit/?MID=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantID ?: Constants.DC_MID}&MPW=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantPassword ?: Constants.DC_MPW}&PRG=loy&TRX=acc&VER=${Constants.DC_VER}&LNG=en&WAN=${Constants.DC_WAN}&WSN=${Constants.DC_WSN}&CID=6360879999987560893&AMT=150&RAM=0&DAM=0&INV=150&ACT=1&PTS=1&IMI=${AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.merchantName ?: Constants.DC_MERCHANT_NAME}"
        observeCPApiResponse()

        /*------- */
    }

     fun initHomeFragment() {
        var fragment =
            supportFragmentManager.findFragmentByTag(MenuFragment::class.java.canonicalName)

        if (fragment == null)
            fragment = MenuFragment.newInstance()

        currentFragment = fragment
        changeFragmentWithoutReCreation(
            currentFragment,
            MenuFragment::class.java.canonicalName!!
        )
        setSelectedTabIcon(selectedIndex = 0)
        setTopBarVisibility(visibility = true)
    }

     fun initOrdersFragment() {
        var fragment =
            supportFragmentManager.findFragmentByTag(MyOrdersFragment::class.java.canonicalName)
        if (fragment == null)
            fragment = MyOrdersFragment.newInstance()
        currentFragment = fragment
        changeFragmentWithoutReCreation(
            currentFragment,
            MyOrdersFragment::class.java.canonicalName!!
        )
        setTopBarVisibility(visibility = false)
        setSelectedTabIcon(selectedIndex = 2)
    }


    private fun getCategoryFromDB() {
        println("categoriesImages -> categoriesImages.count " + categoriesImages.count())
        val db = AppDatabase(this@MainBottomBarActivity)
        GlobalScope.launch {
            val data = db.categoryDao().getCategoryWithProducts()
            //  Log.d("catData -> ", data.size.toString())
            if (data.isEmpty()) {
                this@MainBottomBarActivity.runOnUiThread {
                    AppProgressDialog.showProgressDialog(this@MainBottomBarActivity)
                }
                appDatabase.clearAllTables()
                viewModel.getCategoriesImages()
                //viewModel.getMenuCategories(body = RequestBodyUtil.getMenuRequestBody())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initTopText() {
        val c: Calendar = Calendar.getInstance()
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)
        var greetingsText = "Good Morning"
        greetingsText = when {
            timeOfDay < 12 -> {
                "Good Morning, "
            }
            timeOfDay in 12..15 -> {
                "Good Afternoon, "
            }
            timeOfDay in 16..20 -> {
                "Good Evening, "
            }
            timeOfDay in 21..23 -> {
                "Good Night, "
            }
            else -> "Good Morning, "
        }
        if (isUserLoggedIn())
            mBinding.tvGreetings.text = greetingsText + getLoggedInUser()?.fname
        else
            mBinding.tvGreetings.text = "$greetingsText Guest"
    }

    fun setUserPoints(userPoints: String) {
        mBinding.tvRegister.text = "Rewards Points : ${String.format("%.2f", userPoints.toFloat())}"
        getSavedAccountFragment()?.refreshUserPoints(points = userPoints)
    }

    fun initPackagesFragment() {
        setTopBarVisibility(visibility = false)
        setSelectedTabIcon(selectedIndex = 3)
        currentFragment = PackagesFragment.newInstance()
        changeFragmentWithoutReCreation(
            currentFragment,
            PackagesFragment::class.java.canonicalName!!
        )
    }

    private fun fetchDataForAllKitchens() {
        // TODO
//        if (currentKitchenIndex >= CommonMethods.getKitchensList().size) {
//            hideLoading()
//            AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = CommonMethods.getKitchensList()[0])
//            this@MainBottomBarActivity.runOnUiThread {
//                getSavedHomeFragment()?.refreshData()
//            }
//        } else {
//            menuItemsSize = 0
//            categories.clear()
//            cat = JSONObject()
//            menuItem = JSONObject()
//            optionsList.clear()
//            optionsList = ArrayList()
//            categories.clear()
//            categories = ArrayList()
//            AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = CommonMethods.getKitchensList()[currentKitchenIndex])
//            getCategoriesRecursive()
//        }
    }

    // 4
    private fun addMenuItemsOfExtraOptionsIntoDataBase(
        currentIndex: Int = 0,
        menuItemIndex: Int,
        extraOptionIndex: Int = 0
    ) {

        if (currentIndex >= categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].menuItems.size) {
            addExtraOptionsIntoDataBase(
                extraOptionIndex = extraOptionIndex + 1,
                menuItemIndex = menuItemIndex
            )
        } else {
            appDatabase.productExtraOptionDao().insert(
                ProductExtraOption(
                    0,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].menuItems[currentIndex].ID,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].menuItems[currentIndex].menuItemName,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].menuItems[currentIndex].price,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].menuItems[currentIndex].image,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].id.toString(),
                    appDatabase.productOptionDao()
                        .get(categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].name).pKey
                )
            )
            addMenuItemsOfExtraOptionsIntoDataBase(
                currentIndex = currentIndex + 1,
                menuItemIndex = menuItemIndex, extraOptionIndex = extraOptionIndex
            )
        }
    }

    // 3
    private fun addExtraOptionsIntoDataBase(extraOptionIndex: Int, menuItemIndex: Int) {

        if (extraOptionIndex >= categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions.size) {
            addMenuItemsIntoDataBase(menuItemIndex = menuItemIndex + 1)
        } else {
            appDatabase.productOptionDao().insert(
                ProductOption(
                    0,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].id.toString(),
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].name,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].minSelection,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].maxSelection,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].forceMaxSelection,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].extraOptions[extraOptionIndex].categoryId,
                    appDatabase.productDao()
                        .get(categories[currentCategoryIndex].menuItems[menuItemIndex].ID).pKey
                )
            )
            addMenuItemsOfExtraOptionsIntoDataBase(
                currentIndex = 0, menuItemIndex = menuItemIndex,
                extraOptionIndex = extraOptionIndex
            )
        }
    }

    // 2
    private fun addMenuItemsIntoDataBase(menuItemIndex: Int = 0) {

        if (menuItemIndex >= categories[currentCategoryIndex].menuItems.size) {
            ++currentCategoryIndex
            addCategoriesIntoDataBase()
        } else {
            appDatabase.productDao().insert(
                Product(
                    0,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].ID,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].menuItemName,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].itemDescription,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].price,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].image,
                    categories[currentCategoryIndex].menuItems[menuItemIndex].categoryId,
                    appDatabase.categoryDao().get(categories[currentCategoryIndex].ID).pKey
                )
            )
            addExtraOptionsIntoDataBase(extraOptionIndex = 0, menuItemIndex = menuItemIndex)
        }
    }

    // 1
    private fun addCategoriesIntoDataBase() {
        if (currentCategoryIndex >= categories.size) {
            this@MainBottomBarActivity.runOnUiThread {
                currentCategoryIndex = 0
                ++currentKitchenIndex
                fetchDataForAllKitchens()
            }
        } else {
            appDatabase.categoryDao().insert(
                com.codingpixel.dedicatekitchen.database.Category.Category(
                    0,
                    categories[currentCategoryIndex].ID,
                    categories[currentCategoryIndex].name,
                    categories[currentCategoryIndex].image,
                    groupName = categories[currentCategoryIndex].groupName
                )
            )
            addMenuItemsIntoDataBase(menuItemIndex = 0)
        }
    }

    private fun addDataOfKitchenIntoDataBase() {
        // add to local database
        currentCategoryIndex = 0
        GlobalScope.launch {
            addCategoriesIntoDataBase()
        }
    }


    private fun getApiResponseObserver() {
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        viewModel.getApiDBStatus().observe(this, Observer {
            run {

                when (it.applicationEnum) {
                    ApplicationEnum.GET_PRODUCT_IMAGES_SUCCESS -> {
                        AppProgressDialog.dismissProgressDialog()
                        val productImagesListing = JsonManager.getInstance()
                            .getProductImages(jsonObject = it.resultObject)

                        selectedCategory.menuItems.forEachIndexed { index, menuItemModel ->
                            val matchedIndex =
                                productImagesListing.indexOfFirst { it.product_id == menuItemModel.ID }
                            if (matchedIndex != -1)
                                selectedCategory.menuItems[index].image =
                                    productImagesListing[matchedIndex].image
                            else
                                selectedCategory.menuItems[index].image =
                                    ApiUrls.DEFAULT_PRODUCT_IMAGE_URL
                        }
                        //  goToProducts()
                    }

                    ApplicationEnum.GET_PRODUCT_IMAGES_ERROR -> {
                        AppProgressDialog.dismissProgressDialog()
                        selectedCategory.menuItems.forEach {
                            it.image = ApiUrls.DEFAULT_PRODUCT_IMAGE_URL
                        }
                        // goToProducts()
                    }

                    ApplicationEnum.GET_CATEGORIES_IMAGES_SUCCESS -> {
                        val categoriesImagesListing = JsonManager.getInstance().getCategoryImages_(
                            jsonObject = it.resultObject
                        )
                        categoriesImages.addAll(categoriesImagesListing)
                        currentKitchenIndex = 0
                        fetchDataForAllKitchens()
                    }

                    ApplicationEnum.GET_CATEGORIES_IMAGES_ERROR -> {
                        getCategories()
                    }

                    else -> {
//                        AppProgressDialog.dismissProgressDialog()
                    }
                }

            }
        })
        viewModel.getApiStatus().observe(this, Observer {
            run {
                when (it.enum) {

                    ApplicationEnum.GET_LOYALTY_POINTS_SUCCESS -> {
                        val dataObject = it.dataObject
                        if (dataObject.has("points")) {
                            setUserPoints(userPoints = dataObject.getString("points"))
                        } else {
                            setUserPoints(userPoints = "0.0")
                        }
                    }

                    ApplicationEnum.GET_LOYALTY_POINTS_ERROR -> {
                        setUserPoints(userPoints = "0.0")
                    }
/*Edited by Usama*/
//                    ApplicationEnum.GET_SESSION_ID_SUCCESS -> {
//                        AppPreferenceManager.setValue("sessionId", it.sessionId)
//                        getCategories()
//                    }
//
//                    ApplicationEnum.GET_MENU_SUCCESS -> {
//                        AppProgressDialog.dismissProgressDialog()
//                        val json = it.dataObject
//                        for (i in 0 until json.getJSONObject("groups").getJSONArray("group")
//                            .length()) {
//                            val data = json.getJSONObject("groups").getJSONArray("group")[i]
//                            val categoryObject = (data as JSONObject).getJSONObject("categories")
//                            if (categoryObject.has("category")) {
//                                if (categoryObject.get("category") is JSONArray) {
//                                    for (j in 0 until categoryObject.getJSONArray("category")
//                                        .length()) {
//                                        // val extraOptionsId = ArrayList<String>()
//                                        val extraOptionsId = ArrayList<ExtraOptionsModel>()
//                                        cat =
//                                            (categoryObject.getJSONArray("category")[j] as JSONObject)
//                                        val menu = cat.get("menuitems")
//                                        if (menu is String) {
//                                            ////  showShortToast("is string")
//                                        } else {
//                                            //   showShortToast("is not string")
//                                            val menuItemss = cat.getJSONObject("menuitems")
//                                            if (menuItemss.has("menuItem")) {
//                                                when {
//                                                    menuItemss.get("menuItem") is JSONArray -> {
//                                                        extraOptionsId.clear()
//                                                        val menuItemsLocalList =
//                                                            ArrayList<MenuItemModel>()
//                                                        for (k in 0 until menuItemss.getJSONArray("menuItem")
//                                                            .length()) {
//                                                            menuItemsSize++
//                                                            //add menuitem in category
//                                                            val menuItemJonObject =
//                                                                menuItemss.getJSONArray("menuItem")[k] as JSONObject
//                                                            if (menuItemJonObject.has("extraOptions")) {
//                                                                if (menuItemJonObject.get("extraOptions") is String)
//                                                                // showShortToast("is string")
//                                                                else {
//                                                                    val extraJson =
//                                                                        menuItemJonObject.getJSONObject(
//                                                                            "extraOptions"
//                                                                        ) as JSONObject
//                                                                    when {
//                                                                        extraJson.get("extraOption") is JSONArray -> {
//                                                                            extraOptionsId.clear()
//                                                                            for (l in 0 until extraJson.getJSONArray(
//                                                                                "extraOption"
//                                                                            ).length()) {
//                                                                                val optionId =
//                                                                                    extraJson.getJSONArray(
//                                                                                        "extraOption"
//                                                                                    )[l] as JSONObject
//                                                                                extraOptionsId.add(
//                                                                                    //optionId.getString("categoryId")
//                                                                                    ExtraOptionsModel().apply {
//                                                                                        minSelection =
//                                                                                            optionId.getString(
//                                                                                                "minSelection"
//                                                                                            )
//                                                                                        maxSelection =
//                                                                                            optionId.getString(
//                                                                                                "maxSelection"
//                                                                                            )
//                                                                                        forceMaxSelection =
//                                                                                            optionId.getString(
//                                                                                                "forceMaxSelection"
//                                                                                            )
//                                                                                        categoryId =
//                                                                                            optionId.getString(
//                                                                                                "categoryId"
//                                                                                            )
//                                                                                        id =
//                                                                                            optionId.getString(
//                                                                                                "ID"
//                                                                                            )
//                                                                                    }
//                                                                                )
//                                                                            }
//                                                                        }
//                                                                        extraJson.get("extraOption") is JSONObject -> {
//                                                                            extraOptionsId.clear()
//                                                                            val optionId =
//                                                                                extraJson.get("extraOption") as JSONObject
//                                                                            extraOptionsId.add(
////                                                                                optionId.getString(
////                                                                                    "categoryId"
////                                                                                )
//                                                                                ExtraOptionsModel().apply {
//                                                                                    minSelection =
//                                                                                        optionId.getString(
//                                                                                            "minSelection"
//                                                                                        )
//                                                                                    maxSelection =
//                                                                                        optionId.getString(
//                                                                                            "maxSelection"
//                                                                                        )
//                                                                                    forceMaxSelection =
//                                                                                        optionId.getString(
//                                                                                            "forceMaxSelection"
//                                                                                        )
//                                                                                    categoryId =
//                                                                                        optionId.getString(
//                                                                                            "categoryId"
//                                                                                        )
//                                                                                    id =
//                                                                                        optionId.getString(
//                                                                                            "ID"
//                                                                                        )
//                                                                                }
//                                                                            )
//                                                                        }
//                                                                    }
//                                                                }
//
//                                                            } else
//                                                                null
//
//                                                            if (menuItemJonObject.getString("ID") == "20000549") {
//                                                                Log.d(
//                                                                    "Menu Item Name",
//                                                                    menuItemJonObject.getString("menuItemName")
//                                                                )
//                                                                Log.d(
//                                                                    "Menu Item Description",
//                                                                    menuItemJonObject.getString("itemDescription")
//                                                                )
//                                                            }
//
//                                                            menuItemsLocalList.add(MenuItemModel().apply {
//                                                                menuItemName =
//                                                                    menuItemJonObject.getString("menuItemName")
//                                                                itemDescription =
//                                                                    menuItemJonObject.getString("itemDescription")
//                                                                price =
//                                                                    menuItemJonObject.getString("price")
//                                                                ID =
//                                                                    menuItemJonObject.getString("ID")
//                                                                categoryId = cat.getString("ID")
//                                                                // extraOptions.clear()
//                                                                extraOptions.addAll(extraOptionsId)
//                                                                //  extraOptionsId.clear()
//                                                            })
//                                                        }
//
//                                                        categories.add(Category().apply {
//                                                            masterCid = cat.getString("MasterCID")
//                                                            name = cat.getString("name")
//                                                            ID = cat.getString("ID")
//                                                            menuItems.clear()
//                                                            menuItems = arrayListOf()
//                                                            menuItems.addAll(menuItemsLocalList)
//                                                            groupName = data.getString("name")
//                                                        })
//                                                    }
//                                                    //menu item as json object
//                                                    menuItemss.get("menuItem") is JSONObject -> {
//                                                        extraOptionsId.clear()
//                                                        menuItemsSize++
//                                                        menuItem =
//                                                            (menuItemss.get("menuItem") as JSONObject)
//                                                        val menuItemsLocalList =
//                                                            ArrayList<MenuItemModel>()
//                                                        if (menuItem.has("extraOptions")) {
//                                                            if (menuItem.get("extraOptions") is String)
//                                                                Log.d("empty", "empty")
//                                                            else {
//                                                                val extraJson =
//                                                                    menuItem.getJSONObject("extraOptions") as JSONObject
//                                                                when {
//                                                                    extraJson.get("extraOption") is JSONArray -> {
//                                                                        extraOptionsId.clear()
//                                                                        for (l in 0 until extraJson.getJSONArray(
//                                                                            "extraOption"
//                                                                        ).length()) {
//                                                                            val optionId =
//                                                                                extraJson.getJSONArray(
//                                                                                    "extraOption"
//                                                                                )[l] as JSONObject
//                                                                            extraOptionsId.add(
//                                                                                //optionId.getString("categoryId")
//                                                                                ExtraOptionsModel().apply {
//                                                                                    minSelection =
//                                                                                        optionId.getString(
//                                                                                            "minSelection"
//                                                                                        )
//                                                                                    maxSelection =
//                                                                                        optionId.getString(
//                                                                                            "maxSelection"
//                                                                                        )
//                                                                                    forceMaxSelection =
//                                                                                        optionId.getString(
//                                                                                            "forceMaxSelection"
//                                                                                        )
//                                                                                    categoryId =
//                                                                                        optionId.getString(
//                                                                                            "categoryId"
//                                                                                        )
//                                                                                    id =
//                                                                                        optionId.getString(
//                                                                                            "ID"
//                                                                                        )
//                                                                                }
//                                                                            )
//                                                                        }
//                                                                    }
//                                                                    extraJson.get("extraOption") is JSONObject -> {
//                                                                        extraOptionsId.clear()
//                                                                        val optionId =
//                                                                            extraJson.get("extraOption") as JSONObject
//                                                                        extraOptionsId.add(
////                                                                            optionId.getString(
////                                                                                "categoryId"
////                                                                            )
//                                                                            ExtraOptionsModel().apply {
//                                                                                minSelection =
//                                                                                    optionId.getString(
//                                                                                        "minSelection"
//                                                                                    )
//                                                                                maxSelection =
//                                                                                    optionId.getString(
//                                                                                        "maxSelection"
//                                                                                    )
//                                                                                forceMaxSelection =
//                                                                                    optionId.getString(
//                                                                                        "forceMaxSelection"
//                                                                                    )
//                                                                                categoryId =
//                                                                                    optionId.getString(
//                                                                                        "categoryId"
//                                                                                    )
//                                                                                id =
//                                                                                    optionId.getString(
//                                                                                        "ID"
//                                                                                    )
//                                                                            }
//                                                                        )
//                                                                    }
//                                                                }
//                                                            }
//
//                                                        } else
//                                                            null
//
//                                                        menuItemsLocalList.add(MenuItemModel().apply {
//                                                            ID = menuItem.getString("ID")
//                                                            menuItemName =
//                                                                menuItem.getString("menuItemName")
//                                                            itemDescription =
//                                                                menuItem.getString("itemDescription")
//                                                            price = menuItem.getString("price")
//                                                            categoryId = cat.getString("ID")
//                                                            // extraOptions.clear()
//                                                            extraOptions.addAll(extraOptionsId)
//                                                            //  extraOptionsId.clear()
//                                                        })
//
//                                                        categories.add(Category().apply {
//                                                            masterCid = cat.getString("MasterCID")
//                                                            name = cat.getString("name")
//                                                            ID = cat.getString("ID")
//                                                            menuItems.clear()
//                                                            menuItems = menuItemsLocalList
//                                                            groupName = data.getString("name")
//
//                                                        })
//                                                    }
//
//                                                    menuItemss.get("menuItem") is String -> {
//                                                        extraOptionsId.clear()
//                                                        showShortToast("No menu items exist")
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                } else if (categoryObject.get("category") is JSONObject) {
//                                    // val extraOptionsIdCat = ArrayList<String>()
//                                    val extraOptionsIdCat = ArrayList<ExtraOptionsModel>()
//                                    cat = (categoryObject.get("category") as JSONObject)
////                                    categories.add(Category().apply {
////                                        masterCid = cat.getString("MasterCID")
////                                        name = cat.getString("name")
////                                        ID = cat.getString("ID")
////                                    })
//                                    //add menuitem code here
//                                    val menu = cat.get("menuitems")
//                                    if (menu is String) {
//                                        //showShortToast("is string")
//                                    } else {
//                                        // showShortToast("is not string")
//                                        val menuItemss = cat.getJSONObject("menuitems")
//                                        if (menuItemss.has("menuItem")) {
//                                            extraOptionsIdCat.clear()
//                                            if (menuItemss.get("menuItem") is JSONArray) {
//                                                extraOptionsIdCat.clear()
//                                                val menuItemsLocalList =
//                                                    ArrayList<MenuItemModel>()
//                                                for (k in 0 until menuItemss.getJSONArray("menuItem")
//                                                    .length()) {
//                                                    menuItemsSize++
//                                                    val menuItemJonObject =
//                                                        menuItemss.getJSONArray("menuItem")[k] as JSONObject
//                                                    if (menuItemJonObject.has("extraOptions")) {
//                                                        if (menuItemJonObject.get("extraOptions") is String)
//                                                        //  showShortToast("is string")
//                                                        else {
//                                                            val extraJson =
//                                                                menuItemJonObject.getJSONObject("extraOptions") as JSONObject
//                                                            when {
//
//                                                                extraJson.get("extraOption") is JSONArray -> {
//                                                                    extraOptionsIdCat.clear()
//                                                                    for (l in 0 until extraJson.getJSONArray(
//                                                                        "extraOption"
//                                                                    ).length()) {
//                                                                        val optionId =
//                                                                            extraJson.getJSONArray(
//                                                                                "extraOption"
//                                                                            )[l] as JSONObject
//                                                                        extraOptionsIdCat.add(
//                                                                            ExtraOptionsModel().apply {
//                                                                                minSelection =
//                                                                                    optionId.getString(
//                                                                                        "minSelection"
//                                                                                    )
//                                                                                maxSelection =
//                                                                                    optionId.getString(
//                                                                                        "maxSelection"
//                                                                                    )
//                                                                                forceMaxSelection =
//                                                                                    optionId.getString(
//                                                                                        "forceMaxSelection"
//                                                                                    )
//                                                                                categoryId =
//                                                                                    optionId.getString(
//                                                                                        "categoryId"
//                                                                                    )
//                                                                                id =
//                                                                                    optionId.getString(
//                                                                                        "ID"
//                                                                                    )
//                                                                            }
//                                                                            // optionId.getString("categoryId")
//                                                                        )
//                                                                    }
//                                                                }
//                                                                extraJson.get("extraOptions") is JSONObject -> {
//                                                                    val optionId =
//                                                                        extraJson.get("extraOption") as JSONObject
//                                                                    extraOptionsIdCat.clear()
//                                                                    extraOptionsIdCat.add(
////                                                                        optionId.getString(
////                                                                            "categoryId"
////                                                                        )
//                                                                        ExtraOptionsModel().apply {
//                                                                            minSelection =
//                                                                                optionId.getString(
//                                                                                    "minSelection"
//                                                                                )
//                                                                            maxSelection =
//                                                                                optionId.getString(
//                                                                                    "maxSelection"
//                                                                                )
//                                                                            forceMaxSelection =
//                                                                                optionId.getString(
//                                                                                    "forceMaxSelection"
//                                                                                )
//                                                                            categoryId =
//                                                                                optionId.getString(
//                                                                                    "categoryId"
//                                                                                )
//                                                                            id =
//                                                                                optionId.getString(
//                                                                                    "ID"
//                                                                                )
//                                                                        }
//                                                                    )
//                                                                }
//                                                            }
//                                                        }
//                                                    } else
//                                                        null
////                                                    menuItem =
////                                                        (menuItemss.getJSONArray("menuItem")[k] as JSONObject)
//                                                    menuItemsLocalList.add(MenuItemModel().apply {
//                                                        menuItemName =
//                                                            menuItemJonObject.getString("menuItemName")
//                                                        itemDescription =
//                                                            menuItemJonObject.getString("itemDescription")
//                                                        price =
//                                                            menuItemJonObject.getString("price")
//                                                        ID =
//                                                            menuItemJonObject.getString("ID")
//                                                        categoryId = cat.getString("ID")
//                                                        //extraOptions.clear()
//                                                        extraOptions.addAll(extraOptionsIdCat)
//                                                        // extraOptionsId.clear()
//                                                    })
//                                                    categories.add(Category().apply {
//                                                        masterCid = cat.getString("MasterCID")
//                                                        name = cat.getString("name")
//                                                        ID = cat.getString("ID")
//                                                        menuItems.clear()
//                                                        menuItems = arrayListOf()
//                                                        menuItems.addAll(menuItemsLocalList)
//                                                        groupName = data.getString("name")
//                                                    })
//                                                }
//
//                                            } else if (menuItemss.get("menuItem") is JSONObject) {
//                                                extraOptionsIdCat.clear()
//                                                menuItemsSize++
//                                                menuItem =
//                                                    (menuItemss.get("menuItem") as JSONObject)
//                                                val menuItemsLocalList =
//                                                    ArrayList<MenuItemModel>()
//                                                if (menuItem.has("extraOptions")) {
//                                                    if (menuItem.get("extraOptions") is String)
//                                                    //   showShortToast("is string")
//                                                    else {
//                                                        val extraJson =
//                                                            menuItem.getJSONObject("extraOptions") as JSONObject
//                                                        when {
//                                                            extraJson.get("extraOption") is JSONArray -> {
//                                                                // extraOptionsId.clear()
//                                                                for (l in 0 until extraJson.getJSONArray(
//                                                                    "extraOption"
//                                                                ).length()) {
//                                                                    val optionId =
//                                                                        extraJson.getJSONArray(
//                                                                            "extraOption"
//                                                                        )[l] as JSONObject
//                                                                    extraOptionsIdCat.add(
//                                                                        // optionId.getString("categoryId")
//                                                                        ExtraOptionsModel().apply {
//                                                                            minSelection =
//                                                                                optionId.getString(
//                                                                                    "minSelection"
//                                                                                )
//                                                                            maxSelection =
//                                                                                optionId.getString(
//                                                                                    "maxSelection"
//                                                                                )
//                                                                            forceMaxSelection =
//                                                                                optionId.getString(
//                                                                                    "forceMaxSelection"
//                                                                                )
//                                                                            categoryId =
//                                                                                optionId.getString(
//                                                                                    "categoryId"
//                                                                                )
//                                                                            id =
//                                                                                optionId.getString(
//                                                                                    "ID"
//                                                                                )
//                                                                        }
//                                                                    )
//                                                                }
//                                                            }
//                                                            extraJson.get("extraOption") is JSONObject -> {
//                                                                val optionId =
//                                                                    extraJson.get("extraOption") as JSONObject
//                                                                // extraOptionsId.clear()
//                                                                extraOptionsIdCat.add(
////                                                                    optionId.getString(
////                                                                        "categoryId"
////                                                                    )
//                                                                    ExtraOptionsModel().apply {
//                                                                        minSelection =
//                                                                            optionId.getString(
//                                                                                "minSelection"
//                                                                            )
//                                                                        maxSelection =
//                                                                            optionId.getString(
//                                                                                "maxSelection"
//                                                                            )
//                                                                        forceMaxSelection =
//                                                                            optionId.getString(
//                                                                                "forceMaxSelection"
//                                                                            )
//                                                                        categoryId =
//                                                                            optionId.getString(
//                                                                                "categoryId"
//                                                                            )
//                                                                        id =
//                                                                            optionId.getString(
//                                                                                "ID"
//                                                                            )
//                                                                    }
//                                                                )
//                                                            }
//                                                        }
//                                                    }
//                                                } else
//                                                    null
//
//                                                menuItemsLocalList.add(MenuItemModel().apply {
//                                                    ID = menuItem.getString("ID")
//                                                    menuItemName =
//                                                        menuItem.getString("menuItemName")
//                                                    itemDescription =
//                                                        menuItem.getString("itemDescription")
//                                                    price = menuItem.getString("price")
//                                                    //extraOptions.clear()
//                                                    categoryId = cat.getString("ID")
//                                                    extraOptions.addAll(extraOptionsIdCat)
//                                                    //  extraOptionsId.clear()
//                                                })
//                                                categories.add(Category().apply {
//                                                    masterCid = cat.getString("MasterCID")
//                                                    name = cat.getString("name")
//                                                    ID = cat.getString("ID")
//                                                    menuItems.clear()
//                                                    menuItems = arrayListOf()
//                                                    menuItems.addAll(menuItemsLocalList)
//                                                    groupName = data.getString("name")
//                                                })
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        val distinctList = categories.distinctBy { it.ID }
//                        categories.clear()
//                        categories = arrayListOf()
//                        categories.addAll(distinctList)
//                        categories.forEachIndexed { index, category ->
//                            val matchedIndex =
//                                categoriesImages.indexOfFirst { it.category_id == category.ID }
//                            if (matchedIndex != -1)
//                                categories[index].image =
//                                    ApiUrls.IMAGE_BASE_URL + categoriesImages[matchedIndex].image
//                            else
//                                categories[index].image =
//                                    ApiUrls.DEFAULT_CATEGORY_IMAGE_URL
//                        }
////                         addToDb()
//                        getExtrasFromDB()
//                    }
//
//                    ApplicationEnum.GET_OPTIONS_SUCCESS -> {
//                        //AppProgressDialog.dismissProgressDialog()
//                        val data = it.dataObject
//
//                        for (i in 0 until data.getJSONObject("options").getJSONArray("category")
//                            .length()) {
//                            val categoryJsonObject =
//                                data.getJSONObject("options")
//                                    .getJSONArray("category")[i] as JSONObject
//                            val catName = categoryJsonObject.getString("name")
//                            val catId = categoryJsonObject.getString("ID")
//                            val optionsJson = if (categoryJsonObject.has("option")) {
//                                when {
//                                    categoryJsonObject.get("option") is JSONObject -> {
//                                        categoryJsonObject.getJSONObject("option")
//                                    }
//                                    categoryJsonObject.get("option") is JSONArray -> {
//                                        categoryJsonObject.getJSONArray("option")
//                                    }
//                                    categoryJsonObject.get("option") is String -> {
//                                        categoryJsonObject.getString("option")
//                                    }
//                                    else -> {
//                                        null
//                                    }
//                                }
//                                //categoryJsonObject.getJSONArray("option")
//                            } else
//                                null
//
//                            val optionLocalList = ArrayList<MenuItemModel>()
//                            if (optionsJson != null) {
//                                when (optionsJson) {
//                                    is String -> {
//                                        optionsList.add(Category().apply {
//                                            name = catName
//                                            ID = catId
//                                            // No Options Available
//                                            menuItems.clear()
//                                        })
//                                    }
//
//                                    is JSONArray -> {
//                                        for (j in 0 until optionsJson.length()) {
//                                            val optionObj = optionsJson[j] as JSONObject
//                                            val itemName = optionObj.getString("menuItemName")
//                                            val description =
//                                                if (optionObj.has("itemDescription")) optionObj.getString(
//                                                    "itemDescription"
//                                                )
//                                                else
//                                                    ""
//                                            val priceItem = optionObj.getString("price")
//                                            val extraId = optionObj.getString("ID")
//                                            optionLocalList.add(MenuItemModel().apply {
//                                                menuItemName = itemName
//                                                itemDescription = description
//                                                price = priceItem
//                                                ID = extraId
//                                            })
//                                        }
//                                        optionsList.add(Category().apply {
//                                            name = catName
//                                            ID = catId
//                                            menuItems.clear()
//                                            optionLocalList.forEach { optionMenu ->
//                                                menuItems.add(MenuItemModel().apply {
//                                                    menuItemName = optionMenu.menuItemName
//                                                    itemDescription = optionMenu.itemDescription
//                                                    price = optionMenu.price
//                                                    ID = optionMenu.ID
//                                                })
//                                            }
//                                        })
//                                    }
//
//                                    is JSONObject -> {
//                                        val itemName = optionsJson.getString("menuItemName")
//                                        val priceItem = optionsJson.getString("price")
//                                        val description =
//                                            if (optionsJson.has("itemDescription")) optionsJson.getString(
//                                                "itemDescription"
//                                            )
//                                            else
//                                                ""
//                                        val id = optionsJson.getString("ID")
//                                        optionLocalList.add(MenuItemModel().apply {
//                                            menuItemName = itemName
//                                            itemDescription = description
//                                            price = priceItem
//                                            ID = id
//                                        })
//                                        optionsList.add(Category().apply {
//                                            name = catName
//                                            ID = catId
//                                            menuItems.clear()
//                                            optionLocalList.forEach { optionMenu ->
//                                                menuItems.add(MenuItemModel().apply {
//                                                    menuItemName = optionMenu.menuItemName
//                                                    itemDescription = optionMenu.itemDescription
//                                                    price = optionMenu.price
//                                                    ID = optionMenu.ID
//                                                })
//                                            }
//                                        })
//                                    }
//                                }
//                            } else {
//                                optionsList.add(Category().apply {
//                                    name = catName
//                                    ID = catId
//                                    // No Options Available
//                                    menuItems.clear()
//                                })
//                            }
//                        }
//                        //   Log.d("Categories Size", optionsList.size.toString())
////                        if (isUserLoggedIn())
////                            viewModel.getFavouritesList()
////                        else {
////                            AppProgressDialog.dismissProgressDialog()
////                            products.forEachIndexed { index, _ ->
////                                products[index].isFavourite = false
////                                products[index].isFavouriteFetched = true
////                            }
////                            //topProductsAdapter.notifyDataSetChanged()
////                        }
//                        //optionsArray = data.getJSONArray("category") as ArrayList<OptionModel>
////                        mapProductExtras()
//                        mapProductExtrasRecursive()
//                    }
/*=--------------------*/
                    else -> {
                        AppProgressDialog.dismissProgressDialog()
                    }
                }
            }
        })
    }

    private fun getCategories() {
        viewModel.getMenuCategories(body = RequestBodyUtil.getMenuRequestBody())
    }


    private fun getCategoriesRecursive() {
        viewModel.getMenuCategories(body = RequestBodyUtil.getMenuRequestBody())
    }

    private fun addToDb() {
        // add to local database
        val db = AppDatabase(this@MainBottomBarActivity)

        GlobalScope.launch {
            categories.forEach {
                db.categoryDao().insert(
                    com.codingpixel.dedicatekitchen.database.Category.Category(
                        0,
                        it.ID,
                        it.name,
                        it.image
                    )
                )
                it.menuItems.forEach { product ->
                    db.productDao().insert(
                        Product(
                            0,
                            product.ID,
                            product.menuItemName,
                            product.itemDescription,
                            product.price,
                            product.image,
                            product.categoryId,
                            db.categoryDao().get(it.ID).pKey
                        )
                    )
                    product.extraOptions.forEach { option ->
                        db.productOptionDao().insert(
                            ProductOption(
                                0,
                                option.id.toString(),
                                option.name,
                                option.minSelection,
                                option.maxSelection,
                                option.forceMaxSelection,
                                option.categoryId,
                                db.productDao().get(product.ID).pKey
                            )
                        )
                        option.menuItems.forEach { extra ->
                            db.productExtraOptionDao().insert(
                                ProductExtraOption(
                                    0,
                                    extra.ID,
                                    extra.menuItemName,
                                    extra.price,
                                    extra.image,
                                    option.id.toString(),
                                    db.productOptionDao().get(option.name).pKey
                                )
                            )
                        }
                    }
                }
            }

            AppProgressDialog.dismissProgressDialog()

            this@MainBottomBarActivity.runOnUiThread {
                getSavedHomeFragment()?.refreshData()
            }
        }
    }

    private fun getExtrasFromDB() {
        val db = AppDatabase(this@MainBottomBarActivity)
        GlobalScope.launch {
            val data = db.productExtraOptionDao().getAll()
            //    Log.d("extras -> ", data.size.toString())
            if (data.isEmpty()) {
                this@MainBottomBarActivity.runOnUiThread {
                    AppProgressDialog.showProgressDialog(this@MainBottomBarActivity)
                }
                viewModel.getOptions(body = RequestBodyUtil.getOptionsRequestBody())
            }
        }
    }

    private fun mapProductExtras() {
        categories.forEachIndexed { catIndex, cat ->
            cat.menuItems.forEachIndexed { menuItemIndex, product ->
                println("menuItems")
                product.extraOptions.forEachIndexed { index, option ->
                    println("extraOptions")
                    val matchedIndex = optionsList.indexOfFirst { it.ID == option.categoryId }
                    if (matchedIndex != -1) {
                        product.extraOptions[index].name = optionsList[matchedIndex].name
                        product.extraOptions[index].menuItems.clear()
                        product.extraOptions[index].menuItems = arrayListOf()
                        product.extraOptions[index].menuItems.addAll(optionsList[matchedIndex].menuItems)
                    }
//                    option.name = optionsList.first { it.ID == option.categoryId }.first().name
//                    println("optionName -> ${option.name}")
//                    option.menuItems =
//                        optionsList.filter { it.ID == option.categoryId }.first().menuItems
                }
            }
        }
        addToDb()
        // println("categoriesWithExtras -> ${categories.first().menuItems.first().extraOptions.first().name}")
    }

    private fun mapProductExtrasRecursive() {
        categories.forEachIndexed { catIndex, cat ->
            cat.menuItems.forEachIndexed { menuItemIndex, product ->
                println("menuItems")
                product.extraOptions.forEachIndexed { index, option ->
                    println("extraOptions")
                    val matchedIndex = optionsList.indexOfFirst { it.ID == option.categoryId }
                    if (matchedIndex != -1) {
                        product.extraOptions[index].name = optionsList[matchedIndex].name
                        product.extraOptions[index].menuItems.clear()
                        product.extraOptions[index].menuItems = arrayListOf()
                        product.extraOptions[index].menuItems.addAll(optionsList[matchedIndex].menuItems)
                    }
                }
            }
        }
        addDataOfKitchenIntoDataBase()
        // println("categoriesWithExtras -> ${categories.first().menuItems.first().extraOptions.first().name}")
    }

    private fun refreshSession(showLoading: Boolean = false) {
        if (showLoading)
            showLoading()
        initSessionViewModel(sessionListener = object : SessionListener {
            override fun sessionRestored() {
                hideLoading()
            }

            override fun sessionRestorationFailed() {
                showWarningToast("Session Failed. Please contact support")
//                refreshSession(showLoading = false)
            }
        })
    }

    private fun setHeaderLocationTimeText() {
        val regularTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
        ResourcesCompat.getFont(this, R.font.sf_pro_display_bold)

        val bySigningIn = ""
//        val termsCondition = "Eagle Ridge"
        val termsCondition =
            AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.name ?: "N/A"
        val and = " "
        val privacyPolicy = " - Change"
        val message = "${bySigningIn}${termsCondition}${and}${privacyPolicy}"

        val spannableString = SpannableString(message)

        // By Signing In
        spannableString.setSpan(
            regularTypeface,
            0,
            message.substring(0, bySigningIn.length).length,
            0
        )
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.black)),
            0,
            message.substring(0, bySigningIn.length).length,
            0
        )

        // Terms Conditions
//        val termsConditionsClickableSpan: ClickableSpan = object : ClickableSpan() {
//
//            override fun onClick(widget: View) {
//                //showShortToast(message = "Terms Condititions")
////                startActivity(Intent(this@MainBottomBarActivity, WebViewActivity::class.java).apply {
////                    putExtra(IntentParams.TITLE, "Terms & Conditions")
////                    putExtra(IntentParams.WEB_URL, Constants.TERM_CONDITIONS_URL)
////                })
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.isUnderlineText = false
//            }
//        }
//        spannableString.setSpan(
//            termsConditionsClickableSpan, message.substring(0, bySigningIn.length).length,
//            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
        spannableString.setSpan(
            RelativeSizeSpan(1.0f),
            message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(Typeface.NORMAL),
            message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.black)),
            message.substring(0, bySigningIn.length).length,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )


        // And
        spannableString.setSpan(
            regularTypeface,
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            message.substring(
                0,
                bySigningIn.length
            ).length + 1 + termsCondition.length + and.length,
            0
        )
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.black)),
            message.substring(0, bySigningIn.length).length + 1 + termsCondition.length,
            message.substring(
                0,
                bySigningIn.length
            ).length + 1 + termsCondition.length + and.length,
            0
        )


        // Privacy Policy
        val privacyPolicyClickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
//                showShortToast(message = "Change Kitchen")
//                startActivity(Intent(this@MainBottomBarActivity, WebViewActivity::class.java).apply {
//                    putExtra(IntentParams.TITLE, "Privacy Policy")
//                    putExtra(IntentParams.WEB_URL, Constants.PRIVACY_POLICY_URL)
//                })

                val eventPopup = SelectKitchenDialog.newInstance()
                eventPopup.selectListener(object : SelectListener {
                    override fun selectKitchen(kitchen: String, id: String) {

                    }

                    override fun changeKitchen(selectedKitchen: DedicateKitchen) {
                        GlobalScope.launch(Dispatchers.IO) {
                            getCartItems(selectedKitchen = selectedKitchen)
                            getCartItemsCount()
                        }

                    }

                    override fun selectTime(time: Int, timeValue: String) {

                    }
                })
                eventPopup.show(
                    supportFragmentManager,
                    SelectKitchenDialog::class.java.canonicalName
                )
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(
            privacyPolicyClickableSpan,
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            RelativeSizeSpan(1.0f),
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(Typeface.BOLD),
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.appColor)),
            message.indexOf(string = privacyPolicy, startIndex = 0, ignoreCase = false),
            message.indexOf(
                string = privacyPolicy,
                startIndex = 0,
                ignoreCase = false
            ) + privacyPolicy.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )



        mBinding.tvLocationTimeChange.setText(
            SpannableStringBuilder().append(spannableString),
            TextView.BufferType.SPANNABLE
        )

        mBinding.tvLocationTimeChange.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvLocationTimeChange.setHighlightColor(getColor(android.R.color.transparent));

    }


    @SuppressLint("SetTextI18n")
    private suspend fun getCartItems(selectedKitchen: DedicateKitchen) {
        showLoading()
        val db = AppDatabase(this@MainBottomBarActivity)
//        val data = db.cartDao().getAllWithOUtTerminal()
        val data = db.cartDao().getAll()
        withContext(Dispatchers.Main) {
            hideLoading()

            if (data.isNotEmpty()) {

                val alreadySelectedKitchen =
                    AppPreferenceManager.getSelectedKitchenFromSharedPreferences()

                if (alreadySelectedKitchen != null) {
                    /*Usama edits*/
//                    if (alreadySelectedKitchen.terminalId != selectedKitchen.terminalId) {
//                        errorDialogue(
//                            message = "You can't have items of 2 different stores in your bag. Please empty cart first to add this item into your bag.",
//                            context = this@MainBottomBarActivity,
//                            title = "Error"
//                        )
//                    } else {
                        AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = selectedKitchen)
                        setHeaderLocationTimeText()
                        getSavedHomeFragment()?.changeData(showLoading = true)

                        /*Edited by Usama*/
                        refreshSession(showLoading = false)
//                    }
                } else {
                    AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = selectedKitchen)
                    setHeaderLocationTimeText()
                    getSavedHomeFragment()?.changeData(showLoading = true)

                    /*Edited by Usama*/
                    refreshSession(showLoading = false)
                }

            } else {
                AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = selectedKitchen)
                setHeaderLocationTimeText()
                getSavedHomeFragment()?.changeData(showLoading = true)

                /*Edited by Usama*/
                refreshSession(showLoading = false)
            }


        }
    }

    private fun setBagFormattedText() {
        val regularTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
        val boldTypeface: Typeface? = ResourcesCompat.getFont(this, R.font.sf_pro_display_bold)


//        val message = mBinding.tvCartCount.text.toString()
//
//        val spannableString = SpannableString(message)
//
//        // By Signing In
//        spannableString.setSpan(
//            regularTypeface,
//            0,
//            message.indexOf(" (", 0, ignoreCase = false),
//            0
//        )
//        spannableString.setSpan(
//            ForegroundColorSpan(getColor(R.color.darkLightGrayLabelColor)),
//            0,
//            message.indexOf(" (", 0, ignoreCase = false),
//            0
//        )
//
//        spannableString.setSpan(
//            RelativeSizeSpan(1.0f),
//            message.indexOf(" (", 0, ignoreCase = false),
//            message.length,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
//        spannableString.setSpan(
//            boldTypeface,
//            message.indexOf(" (", 0, ignoreCase = false),
//            message.length,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
//        spannableString.setSpan(
//            ForegroundColorSpan(getColor(R.color.darkGrayTitleColor)),
//            message.indexOf(" (", 0, ignoreCase = false),
//            message.length,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
//
//        mBinding.tvCartCount.setText(
//            SpannableStringBuilder().append(spannableString),
//            TextView.BufferType.SPANNABLE
//        )
    }

    private fun initClickListener() {

        mBinding.tvRegister.setOnClickListener {
            if (isUserLoggedIn()) {

            } else
                startActivity(Intent(this@MainBottomBarActivity, RegisterActivity::class.java))
        }

        mBinding.tvGreetings.setOnClickListener {
//            startActivity(Intent(this@MainBottomBarActivity, MealPrepMenusActivity::class.java))
        }

//        mBinding.ivBag.setOnClickListener {
//            startActivity(Intent(this@MainBottomBarActivity, CartActivity::class.java))
//        }
//
//        mBinding.tvCartCount.setOnClickListener {
//            startActivity(Intent(this@MainBottomBarActivity, CartActivity::class.java))
//        }

        mBinding.btnCart.setOnClickListener {
            startActivity(Intent(this@MainBottomBarActivity, CartActivity::class.java))
        }

        mBinding.tabPackages.setOnClickListener {
//            if (isUserLoggedIn()){
            initPackagesFragment()

//        }else{
//                currentFragment = GuestAccountFragment.newInstance()
//                changeFragmentWithoutReCreation(
//                    currentFragment,
//                    GuestAccountFragment::class.java.canonicalName!!
//                )
//            }
        }


        mBinding.tabQrCode.setOnClickListener {

            if (isUserLoggedIn()) {
                startActivity(
                    Intent(
                        this@MainBottomBarActivity,
                        ScanQRCodeActivity::class.java
                    )
                )
            } else
                showWarningToast(message = "Login to continue")


        }


        mBinding.tabOurMenu.setOnClickListener {
            setTopBarVisibility(visibility = true)
            setSelectedTabIcon(selectedIndex = 0)
            currentFragment = MenuFragment.newInstance()
            changeFragmentWithoutReCreation(
                currentFragment,
                MenuFragment::class.java.canonicalName!!
            )
        }

        mBinding.tabMyAccount.setOnClickListener {
            setTopBarVisibility(visibility = false)
            setSelectedTabIcon(selectedIndex = 4)
            if (isUserLoggedIn()) {
                currentFragment = MyAccountFragment.newInstance()
                changeFragmentWithoutReCreation(
                    currentFragment,
                    MyAccountFragment::class.java.canonicalName!!
                )
            } else {
                currentFragment = GuestAccountFragment.newInstance()
                changeFragmentWithoutReCreation(
                    currentFragment,
                    GuestAccountFragment::class.java.canonicalName!!
                )
            }
        }

        mBinding.tabMyOrders.setOnClickListener {
            setTopBarVisibility(visibility = false)
            setSelectedTabIcon(selectedIndex = 2)
            if (isUserLoggedIn()) {
                currentFragment = MyOrdersFragment.newInstance()
                changeFragmentWithoutReCreation(
                    currentFragment,
                    MyOrdersFragment::class.java.canonicalName!!
                )
            } else {
                currentFragment = GuestAccountFragment.newInstance()
                changeFragmentWithoutReCreation(
                    currentFragment,
                    GuestAccountFragment::class.java.canonicalName!!
                )
            }
        }

        mBinding.tabFavourites.setOnClickListener {
            setTopBarVisibility(visibility = false)
            setSelectedTabIcon(selectedIndex = 1)
            if (isUserLoggedIn()) {
                currentFragment = FavouritesFragment.newInstance()
                changeFragmentWithoutReCreation(
                    currentFragment,
                    FavouritesFragment::class.java.canonicalName!!
                )
            } else {
                currentFragment = GuestAccountFragment.newInstance()
                changeFragmentWithoutReCreation(
                    currentFragment,
                    GuestAccountFragment::class.java.canonicalName!!
                )
            }
        }
    }

    private fun setTopBarVisibility(visibility: Boolean) {

        if (visibility) {
            showView(view = mBinding.tvGreetings)
            showView(view = mBinding.tvLocationTimeChange)
            showView(view = mBinding.ivBag)
            showView(view = mBinding.tvCartCount)
            showView(view = mBinding.tvRegister)
        } else {
            hideView(view = mBinding.tvGreetings)
            hideView(view = mBinding.tvLocationTimeChange)
            hideView(view = mBinding.ivBag)
            hideView(view = mBinding.tvCartCount)
            hideView(view = mBinding.tvRegister)
        }
    }

//    private fun initBottomBar() {
//        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
//        //mBinding.bottomNavigationView.itemIconTintList = null
////        mBinding.bottomNavigationView.itemIconTintList = ContextCompat.getColorStateList(this@MainBottomBarActivity , R.color.bottom_tab_nav_selector)
////        mBinding.bottomNavigationView.itemTextColor = ContextCompat.getColorStateList(this@MainBottomBarActivity , R.color.bottom_tab_nav_selector)
////        mBinding.bottomNavigationView.itemTextAppearanceActive = getColor(R.color.bottom_tab_nav_selector)
////        mBinding.bottomNavigationView.itemTextAppearanceInactive = 0
//        //mBinding.bottomNavigationView.animation = null
//    }

//    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
//        when (menuItem.itemId) {
//            R.id.bottom_navigation_home -> {
//                setTopBarVisibility(visibility = true)
//                setSelectedTabIcon(selectedIndex = 0)
//                currentFragment = MenuFragment.newInstance()
//                changeFragmentWithoutReCreation(
//                    currentFragment,
//                    MenuFragment::class.java.canonicalName!!
//                )
//            }
//
//            R.id.bottom_navigation_favourite -> {
//                setTopBarVisibility(visibility = false)
//                setSelectedTabIcon(selectedIndex = 1)
//                val bundle = Bundle()
//                bundle.putSerializable(IntentParams.CATEGORY, categories)
//                currentFragment = FavouritesFragment.newInstance()
//                currentFragment.arguments = bundle
//                changeFragmentWithoutReCreation(
//                    currentFragment,
//                    FavouritesFragment::class.java.canonicalName!!
//                )
//            }
//
//            R.id.bottom_navigation_my_orders -> {
//                setTopBarVisibility(visibility = false)
//                setSelectedTabIcon(selectedIndex = 2)
//                currentFragment = MyOrdersFragment.newInstance()
//                changeFragmentWithoutReCreation(
//                    currentFragment,
//                    MyOrdersFragment::class.java.canonicalName!!
//                )
//            }
//
//            R.id.bottom_navigation_my_account -> {
//                currentFragment = MyAccountFragment.newInstance()
//                changeFragmentWithoutReCreation(
//                    currentFragment,
//                    MyAccountFragment::class.java.canonicalName!!
//                )
//                setTopBarVisibility(visibility = false)
//                setSelectedTabIcon(selectedIndex = 3)
//
//            }
//
//
//        }
//        return false
//    }

    private fun setSelectedTabIcon(selectedIndex: Int) {

        when (selectedIndex) {
            0 -> {
                mBinding.ivTabMenuIcon.setImageResource(R.drawable.ic_bottom_home_selected_tab)
                mBinding.tvTabOurMenu.setTextColor(getColor(R.color.bottomTabSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabOurMenu,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
                )

                mBinding.ivTabFavouriteIcon.setImageResource(R.drawable.ic_bottom_favourite_un_selected_tab)
                mBinding.tvTabFavourites.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabFavourites,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )


                mBinding.ivTabMyOrdersIcon.setImageResource(R.drawable.ic_bottom_my_orders_un_selected_tab)
                mBinding.tvTabMyOrders.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyOrders,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )


                mBinding.ivTabMyAccountIcon.setImageResource(R.drawable.ic_bottom_my_account_un_selected_tab)
                mBinding.tvTabMyAccount.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyAccount,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )
                mBinding.ivTabPackagesIcon.setImageResource(R.drawable.ic_plans_un_selected)


            }
            1 -> {
                mBinding.ivTabMenuIcon.setImageResource(R.drawable.ic_bottom_home_un_selected_tab)
                mBinding.tvTabOurMenu.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabOurMenu,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
                )
                mBinding.ivTabFavouriteIcon.setImageResource(R.drawable.ic_bottom_favourite_selected_tab)
                mBinding.tvTabFavourites.setTextColor(getColor(R.color.bottomTabSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabFavourites,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyOrdersIcon.setImageResource(R.drawable.ic_bottom_my_orders_un_selected_tab)
                mBinding.tvTabMyOrders.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyOrders,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyAccountIcon.setImageResource(R.drawable.ic_bottom_my_account_un_selected_tab)
                mBinding.tvTabMyAccount.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyAccount,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabPackagesIcon.setImageResource(R.drawable.ic_plans_un_selected)


            }
            2 -> {
                mBinding.ivTabMenuIcon.setImageResource(R.drawable.ic_bottom_home_un_selected_tab)
                mBinding.tvTabOurMenu.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabOurMenu,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
                )


                mBinding.ivTabFavouriteIcon.setImageResource(R.drawable.ic_bottom_favourite_un_selected_tab)
                mBinding.tvTabFavourites.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabFavourites,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyOrdersIcon.setImageResource(R.drawable.ic_bottom_my_orders_selected_tab)
                mBinding.tvTabMyOrders.setTextColor(getColor(R.color.bottomTabSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyOrders,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyAccountIcon.setImageResource(R.drawable.ic_bottom_my_account_un_selected_tab)
                mBinding.tvTabMyAccount.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyAccount,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabPackagesIcon.setImageResource(R.drawable.ic_plans_un_selected)

            }

            3 -> {
                mBinding.ivTabMenuIcon.setImageResource(R.drawable.ic_bottom_home_un_selected_tab)
                mBinding.tvTabOurMenu.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabOurMenu,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
                )

                mBinding.ivTabFavouriteIcon.setImageResource(R.drawable.ic_bottom_favourite_un_selected_tab)
                mBinding.tvTabFavourites.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabFavourites,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyOrdersIcon.setImageResource(R.drawable.ic_bottom_my_orders_un_selected_tab)
                mBinding.tvTabMyOrders.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyOrders,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyAccountIcon.setImageResource(R.drawable.ic_bottom_my_account_un_selected_tab)
                mBinding.tvTabMyAccount.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyAccount,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabPackagesIcon.setImageResource(R.drawable.ic_plans_selected)
                mBinding.tvTabMyAccount.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
            }

            4 -> {
                mBinding.ivTabMenuIcon.setImageResource(R.drawable.ic_bottom_home_un_selected_tab)
                mBinding.tvTabOurMenu.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabOurMenu,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_medium)
                )

                mBinding.ivTabFavouriteIcon.setImageResource(R.drawable.ic_bottom_favourite_un_selected_tab)
                mBinding.tvTabFavourites.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabFavourites,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyOrdersIcon.setImageResource(R.drawable.ic_bottom_my_orders_un_selected_tab)
                mBinding.tvTabMyOrders.setTextColor(getColor(R.color.bottomTabUnSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyOrders,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabMyAccountIcon.setImageResource(R.drawable.ic_bottom_my_account_selected_tab)
                mBinding.tvTabMyAccount.setTextColor(getColor(R.color.bottomTabSelectedColor))
                setTextViewTypeface(
                    tv = mBinding.tvTabMyAccount,
                    typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_regular)
                )

                mBinding.ivTabPackagesIcon.setImageResource(R.drawable.ic_plans_un_selected)

            }

            //val menu = mBinding.bottomNavigationView.menu
            //        for (i in 0 until menu.size()) {
            //            val menuItem = menu.getItem(i)
            //            if (i == 0) {
            //                if (i == selectecIndex) {
            //                    menuItem.setIcon(R.drawable.ic_bottom_home_selected_tab)
            //                }
            //                else
            //                    menuItem.setIcon(R.drawable.ic_bottom_home_un_selected_tab)
            //            } else if (i == 1) {
            //                if (i == selectecIndex)
            //                    menuItem.setIcon(R.drawable.ic_bottom_favourite_selected_tab)
            //                else
            //                    menuItem.setIcon(R.drawable.ic_bottom_favourite_un_selected_tab)
            //            } else if (i == 2) {
            //                if (i == selectecIndex)
            //                    menuItem.setIcon(R.drawable.ic_bottom_my_orders_selected_tab)
            //                else
            //                    menuItem.setIcon(R.drawable.ic_bottom_my_orders_un_selected_tab)
            //            } else if (i == 3) {
            //                if (i == selectecIndex)
            //                    menuItem.setIcon(R.drawable.ic_bottom_my_account_selected_tab)
            //                else
            //                    menuItem.setIcon(R.drawable.ic_bottom_my_account_un_selected_tab)
            //            }
            //        }
        }

        //val menu = mBinding.bottomNavigationView.menu
//        for (i in 0 until menu.size()) {
//            val menuItem = menu.getItem(i)
//            if (i == 0) {
//                if (i == selectecIndex) {
//                    menuItem.setIcon(R.drawable.ic_bottom_home_selected_tab)
//                }
//                else
//                    menuItem.setIcon(R.drawable.ic_bottom_home_un_selected_tab)
//            } else if (i == 1) {
//                if (i == selectecIndex)
//                    menuItem.setIcon(R.drawable.ic_bottom_favourite_selected_tab)
//                else
//                    menuItem.setIcon(R.drawable.ic_bottom_favourite_un_selected_tab)
//            } else if (i == 2) {
//                if (i == selectecIndex)
//                    menuItem.setIcon(R.drawable.ic_bottom_my_orders_selected_tab)
//                else
//                    menuItem.setIcon(R.drawable.ic_bottom_my_orders_un_selected_tab)
//            } else if (i == 3) {
//                if (i == selectecIndex)
//                    menuItem.setIcon(R.drawable.ic_bottom_my_account_selected_tab)
//                else
//                    menuItem.setIcon(R.drawable.ic_bottom_my_account_un_selected_tab)
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        updateHeader()
        GlobalScope.launch(Dispatchers.IO) {
            getCartItemsCount()
        }
    }

    fun refreshLoyaltyPoints(caller: String = "MainBottomBar") {
        Log.d("RefreshLoyaltyPoints", caller)
        viewModel.getLoyaltyPoints(
            url = RequestBodyUtil.getDataCandyLoyaltyPointsURl(
                cardId = getLoggedInUser()?.dc_card_id ?: Constants.DEFAULT_CARD_ID
            )
        )

//        viewModel.getLoyaltyPoints(
//            url = RequestBodyUtil.getDataCandyLoyaltyPointsURl(
//                cardId =  Constants.DEFAULT_CARD_ID
//            )
//        )
    }


    private fun updateHeader() {
        if (isUserLoggedIn()) {
            //hideView(view = mBinding.tvRegister)

            refreshLoyaltyPoints()
//            mBinding.tvRegister.text = "Rewards: 1200 points"
        } else {
            //showView(view = mBinding.tvRegister)
            //showView(view = mBinding.tvRegister)
            mBinding.tvRegister.text = "Click here to Register"
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun getCartItemsCount() {
        val db = AppDatabase(this@MainBottomBarActivity)
        val cartItemsCount = db.cartDao().getCartCount()
//        val cartItems = db.cartDao().getAllWithOUtTerminal()
        val cartItems = db.cartDao().getAll()
        var localCount = 0
        cartItems.forEach {
            localCount += (it.quantity ?: 0)
        }
        Log.d("Quantitiy Based Count", localCount.toString())
        withContext(Dispatchers.Main) {
            // mBinding.tvCartCount.text = "Bag ($cartItemsCount)"
            mBinding.tvCartCount.text = "Bag ($localCount)"
            setBagFormattedText()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        showShortToast("Please click BACK again to exit")
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


    /*Edited by Usama*/

    companion object {
        val allKitchenList: MutableList<DedicateKitchen> = ArrayList()
    }

    private fun getAllKitchens() {
        Log.d("GetAllKitchens", "")

        showLoading()
        viewModel.getAllKitchens()
    }

    private fun observeCPApiResponse() {
        if (::viewModel.isInitialized) {
            viewModel.getCPApiStatus().observe(this@MainBottomBarActivity, androidx.lifecycle.Observer {
                hideLoading()
                when (it.enum) {
                    ApplicationEnum.CP_GET_KITCHEN_SUCCESS -> {

                        val model = Gson().fromJson(it.data.toString(), GetAllKitchenModel::class.java)

                        allKitchenList.clear()
                        for (i in model.result.indices)
                            allKitchenList.add(DedicateKitchen().apply {
                                this.name = model.result[i].name
                                this.terminalId = model.result[i].terminalId
                                this.did = model.result[i].did

                                if (model.result[i].terminalId == "20000008") {
                                    this.apiUrl = "CoPixDedicateKitchen-20000002"
                                    this.merchantID = "407917"
                                    this.merchantPassword = "71474481"
                                    this.merchantName = "486730"
                                    this.menuSetName = "CodingPixelOnlineMenu"
                                } else if (model.result[i].terminalId == "20000007") {
                                    this.apiUrl = "CoPixDedicateKitchen-20000005"
                                    this.merchantID = "407917"
                                    this.merchantPassword = "71474481"
                                    this.merchantName = "422308"
                                    this.menuSetName = "Calgary"
                                }


                            })

                        AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.let { it1 ->
                            AppPreferenceManager.addSelectedKitchenToSharedPreferences(
                                selectedKitchen = it1
                            )
                        }
                        setHeaderLocationTimeText()
                        getSavedHomeFragment()?.changeData(showLoading = false)
                        refreshSession(showLoading = false)
                    }
                    ApplicationEnum.CP_GET_KITCHEN_ERROR -> {
                        showShortToast(it.message.toString())
                    }
                }
            })
        }
    }

}