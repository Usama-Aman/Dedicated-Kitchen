package com.codingpixel.dedicatekitchen.activities.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.local.AppIntroSliderRVAdapter
import com.codingpixel.dedicatekitchen.adapters.local.ToggleDotIndicatorAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Product.Product
import com.codingpixel.dedicatekitchen.database.ProductExtraOption.ProductExtraOption
import com.codingpixel.dedicatekitchen.database.ProductOption.ProductOption
import com.codingpixel.dedicatekitchen.databinding.ActivityIntroSlidesBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.models.*
import com.codingpixel.dedicatekitchen.models.local.IntroSlider
import com.codingpixel.dedicatekitchen.models.local.ToggleIndicator
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import com.wajahatkarim3.easyflipviewpager.CardFlipPageTransformer2
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class IntroSlidesActivity : BaseActivity() {

    private lateinit var mBinding: ActivityIntroSlidesBinding
    private lateinit var introSliderAdapter: AppIntroSliderRVAdapter
    private var introSlidesList = ArrayList<IntroSlider>()

    private lateinit var toggleDotsAdapter: ToggleDotIndicatorAdapter
    private var togglesDots = ArrayList<ToggleIndicator>()

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
    private var locationBasedSelectedKitchen: DedicateKitchen? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro_slides)
        getIntentData()
        setFullScreen()
        initSliderAdapter()

        /*Edited by Usama*/
//        initViewModel()
//        showLoading()
//        fetchAllImages()
        /*----------------*/
//        fetchDataForAllKitchens()
//        currentKitchenIndex = 0
//        fetchDataForAllKitchens()
    }

    private fun getIntentData() {
        val dataIntent = intent
        locationBasedSelectedKitchen = if (dataIntent != null) {
            if (dataIntent.hasExtra(IntentParams.KITCHEN)) {
                try {
                    dataIntent.getSerializableExtra(IntentParams.KITCHEN) as DedicateKitchen?
                } catch (e: Exception) {
                    e.printStackTrace()
                    CommonMethods.getKitchensList()[0]
                }
            } else
                CommonMethods.getKitchensList()[0]
        } else
            CommonMethods.getKitchensList()[0]
    }

    private fun fetchAllImages() {
        viewModel.getCategoriesImages()
    }

    private fun fetchDataForAllKitchens() {
        if (currentKitchenIndex >= CommonMethods.getKitchensList().size) {
            hideLoading()
            if (locationBasedSelectedKitchen == null)
                AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = CommonMethods.getKitchensList()[0])
            else
                AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = locationBasedSelectedKitchen!!)
        } else {
            menuItemsSize = 0
            categories.clear()
            cat = JSONObject()
            menuItem = JSONObject()
            optionsList.clear()
            optionsList = ArrayList()
            categories.clear()
            categories = ArrayList()
            AppPreferenceManager.addSelectedKitchenToSharedPreferences(selectedKitchen = CommonMethods.getKitchensList()[currentKitchenIndex])
            getCategories()
        }
    }


    private fun getCategories() {
        viewModel.getMenuCategories(body = RequestBodyUtil.getMenuRequestBody())
    }

    private fun initViewModel() {
        appDatabase = AppDatabase(this@IntroSlidesActivity)

        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

        viewModel.getApiDBStatus().observe(this, Observer {
            run {

                when (it.applicationEnum) {
                    ApplicationEnum.GET_CATEGORIES_IMAGES_SUCCESS -> {
                        val categoriesImagesListing = JsonManager.getInstance().getCategoryImages_(
                            jsonObject = it.resultObject
                        )
                        categoriesImages.addAll(categoriesImagesListing)
                        currentKitchenIndex = 0
                        fetchDataForAllKitchens()
                    }

                    else -> fetchAllImages()


                }

            }
        })


        viewModel.getApiStatus().observe(this, Observer {
            run {
                when (it.enum) {

                    ApplicationEnum.GET_OPTIONS_ERROR -> fetchOptions()

                    ApplicationEnum.GET_MENU_ERROR -> getCategories()

                    ApplicationEnum.GET_MENU_SUCCESS -> {
                        val json = it.dataObject
                        for (i in 0 until json.getJSONObject("groups").getJSONArray("group")
                            .length()) {
                            val data = json.getJSONObject("groups").getJSONArray("group")[i]
                            val categoryObject = (data as JSONObject).getJSONObject("categories")
                            if (categoryObject.has("category")) {
                                if (categoryObject.get("category") is JSONArray) {
                                    for (j in 0 until categoryObject.getJSONArray("category")
                                        .length()) {
                                        // val extraOptionsId = ArrayList<String>()
                                        val extraOptionsId = ArrayList<ExtraOptionsModel>()
                                        cat =
                                            (categoryObject.getJSONArray("category")[j] as JSONObject)
                                        val menu = cat.get("menuitems")
                                        if (menu is String) {
                                            ////  showShortToast("is string")
                                        } else {
                                            //   showShortToast("is not string")
                                            val menuItemss = cat.getJSONObject("menuitems")
                                            if (menuItemss.has("menuItem")) {
                                                when {
                                                    menuItemss.get("menuItem") is JSONArray -> {
                                                        extraOptionsId.clear()
                                                        val menuItemsLocalList =
                                                            ArrayList<MenuItemModel>()
                                                        for (k in 0 until menuItemss.getJSONArray("menuItem")
                                                            .length()) {
                                                            menuItemsSize++
                                                            //add menuitem in category
                                                            val menuItemJonObject =
                                                                menuItemss.getJSONArray("menuItem")[k] as JSONObject
                                                            if (menuItemJonObject.has("extraOptions")) {
                                                                if (menuItemJonObject.get("extraOptions") is String)
                                                                // showShortToast("is string")
                                                                else {
                                                                    val extraJson =
                                                                        menuItemJonObject.getJSONObject(
                                                                            "extraOptions"
                                                                        ) as JSONObject
                                                                    when {
                                                                        extraJson.get("extraOption") is JSONArray -> {
                                                                            extraOptionsId.clear()
                                                                            for (l in 0 until extraJson.getJSONArray(
                                                                                "extraOption"
                                                                            ).length()) {
                                                                                val optionId =
                                                                                    extraJson.getJSONArray(
                                                                                        "extraOption"
                                                                                    )[l] as JSONObject
                                                                                extraOptionsId.add(
                                                                                    //optionId.getString("categoryId")
                                                                                    ExtraOptionsModel().apply {
                                                                                        minSelection =
                                                                                            optionId.getString(
                                                                                                "minSelection"
                                                                                            )
                                                                                        maxSelection =
                                                                                            optionId.getString(
                                                                                                "maxSelection"
                                                                                            )
                                                                                        forceMaxSelection =
                                                                                            optionId.getString(
                                                                                                "forceMaxSelection"
                                                                                            )
                                                                                        categoryId =
                                                                                            optionId.getString(
                                                                                                "categoryId"
                                                                                            )
                                                                                        id =
                                                                                            optionId.getString(
                                                                                                "ID"
                                                                                            )
                                                                                    }
                                                                                )
                                                                            }
                                                                        }
                                                                        extraJson.get("extraOption") is JSONObject -> {
                                                                            extraOptionsId.clear()
                                                                            val optionId =
                                                                                extraJson.get("extraOption") as JSONObject
                                                                            extraOptionsId.add(
//                                                                                optionId.getString(
//                                                                                    "categoryId"
//                                                                                )
                                                                                ExtraOptionsModel().apply {
                                                                                    minSelection =
                                                                                        optionId.getString(
                                                                                            "minSelection"
                                                                                        )
                                                                                    maxSelection =
                                                                                        optionId.getString(
                                                                                            "maxSelection"
                                                                                        )
                                                                                    forceMaxSelection =
                                                                                        optionId.getString(
                                                                                            "forceMaxSelection"
                                                                                        )
                                                                                    categoryId =
                                                                                        optionId.getString(
                                                                                            "categoryId"
                                                                                        )
                                                                                    id =
                                                                                        optionId.getString(
                                                                                            "ID"
                                                                                        )
                                                                                }
                                                                            )
                                                                        }
                                                                    }
                                                                }

                                                            } else
                                                                null

                                                            if (menuItemJonObject.getString("ID") == "20000549") {
                                                                Log.d(
                                                                    "Menu Item Name",
                                                                    menuItemJonObject.getString("menuItemName")
                                                                )
                                                                Log.d(
                                                                    "Menu Item Description",
                                                                    menuItemJonObject.getString("itemDescription")
                                                                )
                                                            }

                                                            menuItemsLocalList.add(MenuItemModel().apply {
                                                                menuItemName =
                                                                    menuItemJonObject.getString("menuItemName")
                                                                itemDescription =
                                                                    menuItemJonObject.getString("itemDescription")
                                                                price =
                                                                    menuItemJonObject.getString("price")
                                                                ID =
                                                                    menuItemJonObject.getString("ID")
                                                                categoryId = cat.getString("ID")
                                                                // extraOptions.clear()
                                                                extraOptions.addAll(extraOptionsId)
                                                                //  extraOptionsId.clear()
                                                            })
                                                        }

                                                        categories.add(Category().apply {
                                                            masterCid = cat.getString("MasterCID")
                                                            name = cat.getString("name")
                                                            ID = cat.getString("ID")
                                                            menuItems.clear()
                                                            menuItems = arrayListOf()
                                                            menuItems.addAll(menuItemsLocalList)
                                                            groupName = data.getString("name")

                                                        })
                                                    }
                                                    //menu item as json object
                                                    menuItemss.get("menuItem") is JSONObject -> {
                                                        extraOptionsId.clear()
                                                        menuItemsSize++
                                                        menuItem =
                                                            (menuItemss.get("menuItem") as JSONObject)
                                                        val menuItemsLocalList =
                                                            ArrayList<MenuItemModel>()
                                                        if (menuItem.has("extraOptions")) {
                                                            if (menuItem.get("extraOptions") is String)
                                                                Log.d("empty", "empty")
                                                            else {
                                                                val extraJson =
                                                                    menuItem.getJSONObject("extraOptions") as JSONObject
                                                                when {
                                                                    extraJson.get("extraOption") is JSONArray -> {
                                                                        extraOptionsId.clear()
                                                                        for (l in 0 until extraJson.getJSONArray(
                                                                            "extraOption"
                                                                        ).length()) {
                                                                            val optionId =
                                                                                extraJson.getJSONArray(
                                                                                    "extraOption"
                                                                                )[l] as JSONObject
                                                                            extraOptionsId.add(
                                                                                //optionId.getString("categoryId")
                                                                                ExtraOptionsModel().apply {
                                                                                    minSelection =
                                                                                        optionId.getString(
                                                                                            "minSelection"
                                                                                        )
                                                                                    maxSelection =
                                                                                        optionId.getString(
                                                                                            "maxSelection"
                                                                                        )
                                                                                    forceMaxSelection =
                                                                                        optionId.getString(
                                                                                            "forceMaxSelection"
                                                                                        )
                                                                                    categoryId =
                                                                                        optionId.getString(
                                                                                            "categoryId"
                                                                                        )
                                                                                    id =
                                                                                        optionId.getString(
                                                                                            "ID"
                                                                                        )
                                                                                }
                                                                            )
                                                                        }
                                                                    }
                                                                    extraJson.get("extraOption") is JSONObject -> {
                                                                        extraOptionsId.clear()
                                                                        val optionId =
                                                                            extraJson.get("extraOption") as JSONObject
                                                                        extraOptionsId.add(
//                                                                            optionId.getString(
//                                                                                "categoryId"
//                                                                            )
                                                                            ExtraOptionsModel().apply {
                                                                                minSelection =
                                                                                    optionId.getString(
                                                                                        "minSelection"
                                                                                    )
                                                                                maxSelection =
                                                                                    optionId.getString(
                                                                                        "maxSelection"
                                                                                    )
                                                                                forceMaxSelection =
                                                                                    optionId.getString(
                                                                                        "forceMaxSelection"
                                                                                    )
                                                                                categoryId =
                                                                                    optionId.getString(
                                                                                        "categoryId"
                                                                                    )
                                                                                id =
                                                                                    optionId.getString(
                                                                                        "ID"
                                                                                    )
                                                                            }
                                                                        )
                                                                    }
                                                                }
                                                            }

                                                        } else
                                                            null

                                                        menuItemsLocalList.add(MenuItemModel().apply {
                                                            ID = menuItem.getString("ID")
                                                            menuItemName =
                                                                menuItem.getString("menuItemName")
                                                            itemDescription =
                                                                menuItem.getString("itemDescription")
                                                            price = menuItem.getString("price")
                                                            categoryId = cat.getString("ID")
                                                            // extraOptions.clear()
                                                            extraOptions.addAll(extraOptionsId)
                                                            //  extraOptionsId.clear()
                                                        })

                                                        categories.add(Category().apply {
                                                            masterCid = cat.getString("MasterCID")
                                                            name = cat.getString("name")
                                                            ID = cat.getString("ID")
                                                            menuItems.clear()
                                                            menuItems = menuItemsLocalList
                                                            groupName = data.getString("name")
                                                        })
                                                    }

                                                    menuItemss.get("menuItem") is String -> {
                                                        extraOptionsId.clear()
                                                        showShortToast("No menu items exist")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else if (categoryObject.get("category") is JSONObject) {
                                    // val extraOptionsIdCat = ArrayList<String>()
                                    val extraOptionsIdCat = ArrayList<ExtraOptionsModel>()
                                    cat = (categoryObject.get("category") as JSONObject)
//                                    categories.add(Category().apply {
//                                        masterCid = cat.getString("MasterCID")
//                                        name = cat.getString("name")
//                                        ID = cat.getString("ID")
//                                    })
                                    //add menuitem code here
                                    val menu = cat.get("menuitems")
                                    if (menu is String) {
                                        //showShortToast("is string")
                                    } else {
                                        // showShortToast("is not string")
                                        val menuItemss = cat.getJSONObject("menuitems")
                                        if (menuItemss.has("menuItem")) {
                                            extraOptionsIdCat.clear()
                                            if (menuItemss.get("menuItem") is JSONArray) {
                                                extraOptionsIdCat.clear()
                                                val menuItemsLocalList =
                                                    ArrayList<MenuItemModel>()
                                                for (k in 0 until menuItemss.getJSONArray("menuItem")
                                                    .length()) {
                                                    menuItemsSize++
                                                    val menuItemJonObject =
                                                        menuItemss.getJSONArray("menuItem")[k] as JSONObject
                                                    if (menuItemJonObject.has("extraOptions")) {
                                                        if (menuItemJonObject.get("extraOptions") is String)
                                                        //  showShortToast("is string")
                                                        else {
                                                            val extraJson =
                                                                menuItemJonObject.getJSONObject("extraOptions") as JSONObject
                                                            when {

                                                                extraJson.get("extraOption") is JSONArray -> {
                                                                    extraOptionsIdCat.clear()
                                                                    for (l in 0 until extraJson.getJSONArray(
                                                                        "extraOption"
                                                                    ).length()) {
                                                                        val optionId =
                                                                            extraJson.getJSONArray(
                                                                                "extraOption"
                                                                            )[l] as JSONObject
                                                                        extraOptionsIdCat.add(
                                                                            ExtraOptionsModel().apply {
                                                                                minSelection =
                                                                                    optionId.getString(
                                                                                        "minSelection"
                                                                                    )
                                                                                maxSelection =
                                                                                    optionId.getString(
                                                                                        "maxSelection"
                                                                                    )
                                                                                forceMaxSelection =
                                                                                    optionId.getString(
                                                                                        "forceMaxSelection"
                                                                                    )
                                                                                categoryId =
                                                                                    optionId.getString(
                                                                                        "categoryId"
                                                                                    )
                                                                                id =
                                                                                    optionId.getString(
                                                                                        "ID"
                                                                                    )
                                                                            }
                                                                            // optionId.getString("categoryId")
                                                                        )
                                                                    }
                                                                }
                                                                extraJson.get("extraOptions") is JSONObject -> {
                                                                    val optionId =
                                                                        extraJson.get("extraOption") as JSONObject
                                                                    extraOptionsIdCat.clear()
                                                                    extraOptionsIdCat.add(
//                                                                        optionId.getString(
//                                                                            "categoryId"
//                                                                        )
                                                                        ExtraOptionsModel().apply {
                                                                            minSelection =
                                                                                optionId.getString(
                                                                                    "minSelection"
                                                                                )
                                                                            maxSelection =
                                                                                optionId.getString(
                                                                                    "maxSelection"
                                                                                )
                                                                            forceMaxSelection =
                                                                                optionId.getString(
                                                                                    "forceMaxSelection"
                                                                                )
                                                                            categoryId =
                                                                                optionId.getString(
                                                                                    "categoryId"
                                                                                )
                                                                            id =
                                                                                optionId.getString(
                                                                                    "ID"
                                                                                )
                                                                        }
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else
                                                        null
//                                                    menuItem =
//                                                        (menuItemss.getJSONArray("menuItem")[k] as JSONObject)
                                                    menuItemsLocalList.add(MenuItemModel().apply {
                                                        menuItemName =
                                                            menuItemJonObject.getString("menuItemName")
                                                        itemDescription =
                                                            menuItemJonObject.getString("itemDescription")
                                                        price =
                                                            menuItemJonObject.getString("price")
                                                        ID =
                                                            menuItemJonObject.getString("ID")
                                                        categoryId = cat.getString("ID")
                                                        //extraOptions.clear()
                                                        extraOptions.addAll(extraOptionsIdCat)
                                                        // extraOptionsId.clear()
                                                    })
                                                    categories.add(Category().apply {
                                                        masterCid = cat.getString("MasterCID")
                                                        name = cat.getString("name")
                                                        ID = cat.getString("ID")
                                                        menuItems.clear()
                                                        menuItems = arrayListOf()
                                                        menuItems.addAll(menuItemsLocalList)
                                                        groupName = data.getString("name")
                                                    })
                                                }

                                            } else if (menuItemss.get("menuItem") is JSONObject) {
                                                extraOptionsIdCat.clear()
                                                menuItemsSize++
                                                menuItem =
                                                    (menuItemss.get("menuItem") as JSONObject)
                                                val menuItemsLocalList =
                                                    ArrayList<MenuItemModel>()
                                                if (menuItem.has("extraOptions")) {
                                                    if (menuItem.get("extraOptions") is String)
                                                    //   showShortToast("is string")
                                                    else {
                                                        val extraJson =
                                                            menuItem.getJSONObject("extraOptions") as JSONObject
                                                        when {
                                                            extraJson.get("extraOption") is JSONArray -> {
                                                                // extraOptionsId.clear()
                                                                for (l in 0 until extraJson.getJSONArray(
                                                                    "extraOption"
                                                                ).length()) {
                                                                    val optionId =
                                                                        extraJson.getJSONArray(
                                                                            "extraOption"
                                                                        )[l] as JSONObject
                                                                    extraOptionsIdCat.add(
                                                                        // optionId.getString("categoryId")
                                                                        ExtraOptionsModel().apply {
                                                                            minSelection =
                                                                                optionId.getString(
                                                                                    "minSelection"
                                                                                )
                                                                            maxSelection =
                                                                                optionId.getString(
                                                                                    "maxSelection"
                                                                                )
                                                                            forceMaxSelection =
                                                                                optionId.getString(
                                                                                    "forceMaxSelection"
                                                                                )
                                                                            categoryId =
                                                                                optionId.getString(
                                                                                    "categoryId"
                                                                                )
                                                                            id =
                                                                                optionId.getString(
                                                                                    "ID"
                                                                                )
                                                                        }
                                                                    )
                                                                }
                                                            }
                                                            extraJson.get("extraOption") is JSONObject -> {
                                                                val optionId =
                                                                    extraJson.get("extraOption") as JSONObject
                                                                // extraOptionsId.clear()
                                                                extraOptionsIdCat.add(
//                                                                    optionId.getString(
//                                                                        "categoryId"
//                                                                    )
                                                                    ExtraOptionsModel().apply {
                                                                        minSelection =
                                                                            optionId.getString(
                                                                                "minSelection"
                                                                            )
                                                                        maxSelection =
                                                                            optionId.getString(
                                                                                "maxSelection"
                                                                            )
                                                                        forceMaxSelection =
                                                                            optionId.getString(
                                                                                "forceMaxSelection"
                                                                            )
                                                                        categoryId =
                                                                            optionId.getString(
                                                                                "categoryId"
                                                                            )
                                                                        id =
                                                                            optionId.getString(
                                                                                "ID"
                                                                            )
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                } else
                                                    null

                                                menuItemsLocalList.add(MenuItemModel().apply {
                                                    ID = menuItem.getString("ID")
                                                    menuItemName =
                                                        menuItem.getString("menuItemName")
                                                    itemDescription =
                                                        menuItem.getString("itemDescription")
                                                    price = menuItem.getString("price")
                                                    //extraOptions.clear()
                                                    categoryId = cat.getString("ID")
                                                    extraOptions.addAll(extraOptionsIdCat)
                                                    //  extraOptionsId.clear()
                                                })
                                                categories.add(Category().apply {
                                                    masterCid = cat.getString("MasterCID")
                                                    name = cat.getString("name")
                                                    ID = cat.getString("ID")
                                                    menuItems.clear()
                                                    menuItems = arrayListOf()
                                                    menuItems.addAll(menuItemsLocalList)
                                                    groupName = data.getString("name")
                                                })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val distinctList = categories.distinctBy { it.ID }
                        categories.clear()
                        categories = arrayListOf()
                        categories.addAll(distinctList)
                        categories.forEachIndexed { index, category ->
                            val matchedIndex =
                                categoriesImages.indexOfFirst { it.category_id == category.ID }
                            if (matchedIndex != -1)
                                categories[index].image =
                                    ApiUrls.IMAGE_BASE_URL + categoriesImages[matchedIndex].image
                            else
                                categories[index].image =
                                    ApiUrls.DEFAULT_CATEGORY_IMAGE_URL
                        }
//                        fetchOptions()
                        fetchOptionsRecursive()
                    }

                    ApplicationEnum.GET_OPTIONS_SUCCESS -> {
                        val data = it.dataObject

                        for (i in 0 until data.getJSONObject("options").getJSONArray("category")
                            .length()) {
                            val categoryJsonObject =
                                data.getJSONObject("options")
                                    .getJSONArray("category")[i] as JSONObject
                            val catName = categoryJsonObject.getString("name")
                            val catId = categoryJsonObject.getString("ID")
                            val optionsJson = if (categoryJsonObject.has("option")) {
                                when {
                                    categoryJsonObject.get("option") is JSONObject -> {
                                        categoryJsonObject.getJSONObject("option")
                                    }
                                    categoryJsonObject.get("option") is JSONArray -> {
                                        categoryJsonObject.getJSONArray("option")
                                    }
                                    categoryJsonObject.get("option") is String -> {
                                        categoryJsonObject.getString("option")
                                    }
                                    else -> {
                                        null
                                    }
                                }
                                //categoryJsonObject.getJSONArray("option")
                            } else
                                null

                            val optionLocalList = ArrayList<MenuItemModel>()
                            if (optionsJson != null) {
                                when (optionsJson) {
                                    is String -> {
                                        optionsList.add(Category().apply {
                                            name = catName
                                            ID = catId
                                            // No Options Available
                                            menuItems.clear()
                                        })
                                    }

                                    is JSONArray -> {
                                        for (j in 0 until optionsJson.length()) {
                                            val optionObj = optionsJson[j] as JSONObject
                                            val itemName = optionObj.getString("menuItemName")
                                            val description =
                                                if (optionObj.has("itemDescription")) optionObj.getString(
                                                    "itemDescription"
                                                )
                                                else
                                                    ""
                                            val priceItem = optionObj.getString("price")
                                            val extraId = optionObj.getString("ID")
                                            optionLocalList.add(MenuItemModel().apply {
                                                menuItemName = itemName
                                                itemDescription = description
                                                price = priceItem
                                                ID = extraId
                                            })
                                        }
                                        optionsList.add(Category().apply {
                                            name = catName
                                            ID = catId
                                            menuItems.clear()
                                            optionLocalList.forEach { optionMenu ->
                                                menuItems.add(MenuItemModel().apply {
                                                    menuItemName = optionMenu.menuItemName
                                                    itemDescription = optionMenu.itemDescription
                                                    price = optionMenu.price
                                                    ID = optionMenu.ID
                                                })
                                            }
                                        })
                                    }

                                    is JSONObject -> {
                                        val itemName = optionsJson.getString("menuItemName")
                                        val priceItem = optionsJson.getString("price")
                                        val description =
                                            if (optionsJson.has("itemDescription")) optionsJson.getString(
                                                "itemDescription"
                                            )
                                            else
                                                ""
                                        val id = optionsJson.getString("ID")
                                        optionLocalList.add(MenuItemModel().apply {
                                            menuItemName = itemName
                                            itemDescription = description
                                            price = priceItem
                                            ID = id
                                        })
                                        optionsList.add(Category().apply {
                                            name = catName
                                            ID = catId
                                            menuItems.clear()
                                            optionLocalList.forEach { optionMenu ->
                                                menuItems.add(MenuItemModel().apply {
                                                    menuItemName = optionMenu.menuItemName
                                                    itemDescription = optionMenu.itemDescription
                                                    price = optionMenu.price
                                                    ID = optionMenu.ID
                                                })
                                            }
                                        })
                                    }
                                }
                            } else {
                                optionsList.add(Category().apply {
                                    name = catName
                                    ID = catId
                                    // No Options Available
                                    menuItems.clear()
                                })
                            }
                        }
                        mapProductExtras()
                    }

                    else -> {
                        hideLoading()
                    }
                }
            }
        })
    }

    private fun fetchOptions() {
        viewModel.getOptions(body = RequestBodyUtil.getOptionsRequestBody())
    }

    private fun fetchOptionsRecursive() {
        viewModel.getOptions(body = RequestBodyUtil.getOptionsRequestBody())
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
                }
            }
        }
        addDataOfKitchenIntoDataBase()
        // println("categoriesWithExtras -> ${categories.first().menuItems.first().extraOptions.first().name}")
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
            this@IntroSlidesActivity.runOnUiThread {
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

    private fun initSliderAdapter() {
        introSlidesList.addAll(CommonMethods.getIntroSlidesList(this@IntroSlidesActivity))
        introSliderAdapter = AppIntroSliderRVAdapter(
            mContext = this@IntroSlidesActivity,
            introslides = introSlidesList
        )
        mBinding.vpSlider.adapter = introSliderAdapter

        val cardFlipPageTransformer2: CardFlipPageTransformer2 = CardFlipPageTransformer2()
        cardFlipPageTransformer2.isScalable = true
        mBinding.vpSlider.setPageTransformer(cardFlipPageTransformer2)
        togglesDots.clear()
        for (image in 0 until introSlidesList.size)
            togglesDots.add(ToggleIndicator().apply {
                isSelected = false
            })

        togglesDots[0].isSelected = true
        toggleDotsAdapter = ToggleDotIndicatorAdapter(toggles = togglesDots)
        mBinding.rvDots.adapter = toggleDotsAdapter

        mBinding.vpSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {


            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                val oldSelectedIndex = togglesDots.indexOfFirst { it.isSelected }
                if (oldSelectedIndex != -1)
                    togglesDots[oldSelectedIndex].isSelected = false
                togglesDots[position].isSelected = true
                toggleDotsAdapter.notifyItemChanged(oldSelectedIndex)
                toggleDotsAdapter.notifyItemChanged(position)
                if (position == 2) {
                    AppPreferenceManager.setBoolean(PrefFlags.FIRST_INSTALL, false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Your Code
                        startActivity(
                            Intent(
                                this@IntroSlidesActivity,
                                IntermediateActivity::class.java
                            )
                        )
                        finish()
                    }, 500)
                }

                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }

}