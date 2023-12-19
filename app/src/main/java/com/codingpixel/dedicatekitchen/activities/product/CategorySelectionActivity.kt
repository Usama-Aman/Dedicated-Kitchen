package com.codingpixel.dedicatekitchen.activities.product

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.checkout.CartActivity
import com.codingpixel.dedicatekitchen.adapters.CategoriesAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Product.Product
import com.codingpixel.dedicatekitchen.databinding.ActivityCategorySelectionBinding
import com.codingpixel.dedicatekitchen.fragments.bottomtabs.MenuFragment
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.MenuInterface
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.CategoryImage
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class CategorySelectionActivity : BaseActivity(), MenuInterface {

    private lateinit var mBinding: ActivityCategorySelectionBinding

    private lateinit var categoriesAdapter: CategoriesAdapter

    private lateinit var viewModel: MenuViewModel
    private var cat = JSONObject()
    private var isMealPrep: Boolean = false
    private var menuItem = JSONObject()
    private var menuItemsSize: Int = 0
    // private var extraOptionsId = ArrayList<String>()

    private var categoriesImages = ArrayList<CategoryImage>()

    private var categories = ArrayList<Category>()

    private lateinit var selectedCategory: Category

//    private var menuItemList = ArrayList<MenuItemModel>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_category_selection)
        changeStatusBarColor(statusBarColor = getColor(R.color.grayBgColor))

        initAdapter()
        getIntentData()
        initClickListener()
        getApiResponseObserver()


        /*Edited by Usama*/
//        if (!isMealPrep) {
//            getCategoryFromDB()
//        } else {
//            getMealPrepCategoriesFromDb()
//        }

        if (isMealPrep) {
            categories.addAll(MenuFragment.mealPrepCategories)
        } else {
            categories.addAll(MenuFragment.takeOutCategories)
        }
        showView(view = mBinding.rvCategories)
        categoriesAdapter.notifyDataSetChanged()
        /*------------------------*/

    }

    private fun getIntentData() {
        isMealPrep = intent.getBooleanExtra(IntentParams.isMealPrep, false)
    }

    /*Edited by Usama*/
    private fun getMealPrepCategoriesFromDb() {
//        showLoading()
//        val db = AppDatabase(this)
//        GlobalScope.launch(Dispatchers.IO) {
//            val data = db.categoryDao().getMealPrepCategoryWithProducts()
//            if (data.isEmpty()) {
//                AppProgressDialog.showProgressDialog(this@CategorySelectionActivity)
//                getCategories()
////                viewModel.getCategoriesImages()
//            } else {

//                categories.clear()
//                data.forEach {
        /*  if (it.category.id == "20000233" || it.category.id == "20000037" || it.category.id == "20000300" || it.category.id == "20000301" || it.category.id == "20000302" || it.category.id == "20000039"
              || it.category.id == "20000336" || it.category.id == "20000337"
//                      ) {*/
//                    val _menuItems = ArrayList<MenuItemModel>()
//                    it.products.forEach { product ->
//                        val _productOptions = ArrayList<ExtraOptionsModel>()
//                        val productOptions = db.productOptionDao().getByID(product.pKey)
////                        val productOptions = db.productOptionDao().getProductWithExtraOptions(product.pKey)
////                        val productExtras = db.productExtraOptionDao().getAllById(productOptions.first().categoryId?: "0")
//                        println("productOptions -> $productOptions")
//                        productOptions.forEach { option ->
//                            val productExtras =
//                                db.productExtraOptionDao().getAllById(option.id ?: "0")
//                            println("productExtrasproductExtras -> $productExtras")
//                            val extras = ArrayList<MenuItemModel>()
//                            productExtras.forEach { extra ->
//                                extras.add(
//                                    MenuItemModel().apply {
//                                        ID = extra.id ?: ""
//                                        menuItemName = extra.name ?: ""
//                                        price = extra.price ?: ""
//                                        categoryId = extra.categoryId ?: ""
//                                    }
//                                )
//                            }
//                            _productOptions.add(ExtraOptionsModel().apply {
//                                name = option.name ?: ""
//                                minSelection = option.minSelection ?: ""
//                                maxSelection = option.maxSelection ?: ""
//                                forceMaxSelection = option.forceMaxSelection ?: ""
//                                categoryId = option.categoryId ?: ""
//                                menuItems.addAll(extras)
//                            })
//                        }
//                        _menuItems.add(MenuItemModel().apply {
//                            ID = product.id!!
//                            menuItemName = product.name!!
//                            price = product.price!!
//                            image = if (product.image.isNullOrEmpty()) "" else product.image
//                            extraOptions = _productOptions
//                        })
//                    }
//                    categories.add(Category().apply {
//                        masterCid = ""
//                        name = it.category.name ?: ""
//                        ID = it.category.id!!
//                        image =
//                            if (it.category.image.isNullOrEmpty()) "" else it.category.image!!
//                        menuItems = _menuItems
//                        groupName = it.category.groupName
//                    })
//                    /*  } else {
//                          Log.d("notexist", "not")
//                      }*/
//                }
////                val sortedData = Collections.sort(categories,
////                    Comparator<Category?> { v1, v2 -> v1!!.name.compareTo(v2!!.name) })
//                /*val sortedData = categories.toMutableList()
//                sortedData.forEach {
//                    when (it.name) {
//                        "Pre-Built Meals" -> {
//                            categories[0] = it
//                        }
//                        "Custom Meal Prep" -> {
//                            categories[1] = it
//                        }
//                        "Protein Bundles" -> {
//                            categories[2] = it
//                        }
//                        "Veggie Bundles" -> {
//                            categories[3] = it
//                        }
//                        "Carb Bundles" -> {
//                            categories[4] = it
//                        }
//                        "Treats & Snacks" -> {
//                            categories[5] = it
//                        }
//
//                        "Wraps & Sandwiches" -> {
//                            categories[6] = it
//                        }
//
//                        "Breakfast" -> {
//                            categories[7] = it
//                        }
//                    }
//                }*/
////                categories =  sortedData
////                for (i in 0 until categories.size) {
////                    val sortedProducts = Collections.sort(categories[i].menuItems,
////                        Comparator<MenuItemModel?> { v1, v2 ->
////                            v1!!.menuItemName.compareTo(
////                                v2!!.menuItemName
////                            )
////                        })
////                }
//
//                // Log.d("sorteddataaaa" , sortedData.toString())
//                this@CategorySelectionActivity.runOnUiThread {
//                    hideLoading()
//                    initAdapter()
//                    showView(view = mBinding.rvCategories)
//                }
//            }
//        }
    }

    private fun getCategoryFromDB() {
//        val db = AppDatabase(this)
//        GlobalScope.launch(Dispatchers.IO) {
//            val data = db.categoryDao().getTakeoutCategoryWithProducts()
//            if (data.isEmpty()) {
//                AppProgressDialog.showProgressDialog(this@CategorySelectionActivity)
//                getCategories()
////                viewModel.getCategoriesImages()
//            } else {
//                categories.clear()
//                categories = arrayListOf()
//                data.forEach {
//                    /*  if (it.category.id == "20000233" || it.category.id == "20000037" || it.category.id == "20000300" || it.category.id == "20000301" || it.category.id == "20000302" || it.category.id == "20000039"
//                          || it.category.id == "20000336" || it.category.id == "20000337"
//                      ) {
//                          return@forEach
//                      }*/
//                    val _menuItems = ArrayList<MenuItemModel>()
//                    it.products.forEach { product ->
//                        val _productOptions = ArrayList<ExtraOptionsModel>()
//                        val productOptions = db.productOptionDao().getByID(product.pKey)
////                        val productOptions = db.productOptionDao().getProductWithExtraOptions(product.pKey)
////                        val productExtras = db.productExtraOptionDao().getAllById(productOptions.first().categoryId?: "0")
//                        println("productOptions -> $productOptions")
//                        productOptions.forEach { option ->
//                            val productExtras =
//                                db.productExtraOptionDao().getAllById(option.id ?: "0")
//                            println("productExtrasproductExtras -> $productExtras")
//                            val extras = ArrayList<MenuItemModel>()
//                            productExtras.forEach { extra ->
//                                extras.add(
//                                    MenuItemModel().apply {
//                                        ID = extra.id ?: ""
//                                        menuItemName = extra.name ?: ""
//                                        price = extra.price ?: ""
//                                        categoryId = extra.categoryId ?: ""
//                                    }
//                                )
//                            }
//                            _productOptions.add(ExtraOptionsModel().apply {
//                                name = option.name ?: ""
//                                minSelection = option.minSelection ?: ""
//                                maxSelection = option.maxSelection ?: ""
//                                forceMaxSelection = option.forceMaxSelection ?: ""
//                                categoryId = option.categoryId ?: ""
//                                menuItems.addAll(extras)
//                            })
//                        }
//                        _menuItems.add(MenuItemModel().apply {
//                            ID = product.id!!
//                            menuItemName = product.name!!
//                            itemDescription = product.itemDescription ?: ""
//                            price = product.price!!
//                            image = if (product.image.isNullOrEmpty()) "" else product.image
//                            extraOptions = _productOptions
//                        })
//                    }
//                    categories.add(Category().apply {
//                        masterCid = ""
//                        name = it.category.name!!
//                        ID = it.category.id!!
//                        image = if (it.category.image.isNullOrEmpty()) "" else it.category.image!!
//                        menuItems = _menuItems
//                        groupName = it.category.groupName
//                    })
//                }
////                val sortedData = Collections.sort(categories,
////                    Comparator<Category?> { v1, v2 -> v1!!.name.compareTo(v2!!.name) })
//                /*  val sortedData = categories.toMutableList()
//                  sortedData.forEach {
//                      when (it.name) {
//                          "All Day Breakfast" -> {
//                              categories[0] = it
//                          }
//                          "Protein Pancakes & Waffles" -> {
//                              categories[1] = it
//                          }
//                          "Wraps & Sandwiches" -> {
//                              categories[2] = it
//                          }
//                          "Burgers" -> {
//                              categories[3] = it
//                          }
//                          "Bowls & Stir Frys" -> {
//                              categories[4] = it
//                          }
//                          "Penne Pasta" -> {
//                              categories[5] = it
//                          }
//                          "Build Your Meal" -> {
//                              categories[6] = it
//                          }
//                          "Salads" -> {
//                              categories[7] = it
//                          }
//                          "Protein Smoothies" -> {
//                              categories[8] = it
//                          }
//                          "Fresh Juices" -> {
//                              categories[9] = it
//                          }
//
//                          "Plant Based" -> {
//                              categories[10] = it
//                          }
//                          "Kids" -> {
//                              categories[11] = it
//                          }
//
//                          "Pizza" -> {
//                              categories[12] = it
//                          }
//                      }
//                  }*/
////                categories =  sortedData
////                for (i in 0 until categories.size) {
////                    val sortedProducts = Collections.sort(categories[i].menuItems,
////                        Comparator<MenuItemModel?> { v1, v2 -> v1!!.menuItemName.compareTo(v2!!.menuItemName) })
////                }
//
//                // Log.d("sorteddataaaa" , sortedData.toString())
//                this@CategorySelectionActivity.runOnUiThread {
//                    hideLoading()
//                    initAdapter()
//                    showView(view = mBinding.rvCategories)
//                }
//
//            }
//        }
    }
    /*-------------------------------------------*/

    private fun getCategories() {
        viewModel.getMenuCategories(body = RequestBodyUtil.getMenuRequestBody())
    }

    private fun getCategoryProductImages(categoryId: String = "") {

        viewModel.getProductImages(
            categoryId =
            categoryId
        )
    }

    /*Edited by Usama*/
    private fun getApiResponseObserver() {
//        val db = AppDatabase(this@CategorySelectionActivity)
//        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
//        viewModel.getApiDBStatus().observe(this, Observer {
//            run {
//                when (it.applicationEnum) {
//                    ApplicationEnum.GET_PRODUCT_IMAGES_SUCCESS -> {
//                        println("called->product images success")
//                        AppProgressDialog.dismissProgressDialog()
//                        val productImagesListing = JsonManager.getInstance()
//                            .getProductImages(jsonObject = it.resultObject)
//                        println("productImagesListing-> " + productImagesListing.count())
//                        try {
//                            selectedCategory.menuItems.forEachIndexed { index, menuItemModel ->
//                                val matchedIndex =
//                                    productImagesListing.indexOfFirst {
//                                        println("productId -> " + it.product_id + " menuItemModel -> " + menuItemModel.ID)
//                                        it.product_id == menuItemModel.ID
//                                    }
//                                if (matchedIndex != -1)
//                                    selectedCategory.menuItems[index].image =
//                                        productImagesListing[matchedIndex].image
//                                else
//                                    selectedCategory.menuItems[index].image =
//                                        ApiUrls.DEFAULT_PRODUCT_IMAGE_URL
//
//                                println("matchedIndexmatchedIndex -> " + matchedIndex)
//                                println("image -> " + selectedCategory.menuItems[index].image)
//
//                                GlobalScope.launch(Dispatchers.IO) {
//                                    val item = db.productDao().get(selectedCategory.menuItems[index].ID)
//                                    selectedCategory.menuItems[index].itemDescription =
//                                        item.itemDescription ?: "N/A"
//                                    db.productDao().update(
//                                        Product(
//                                            item.pKey,
//                                            item.id,
//                                            item.name,
//                                            item.itemDescription,
//                                            item.price,
//                                            selectedCategory.menuItems[index].image,
//                                            item.categoryId,
//                                            item.fkCategoryId
//                                        )
//                                    )
//                                }
//                            }
//                            goToProducts()
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//
//
//                    }
//
//                    ApplicationEnum.GET_PRODUCT_IMAGES_ERROR -> {
//                        println("called->product images failure")
//                        AppProgressDialog.dismissProgressDialog()
//                        selectedCategory.menuItems.forEach {
//                            it.image = ApiUrls.DEFAULT_PRODUCT_IMAGE_URL
//                        }
//                        goToProducts()
//                    }
//
//                    ApplicationEnum.GET_CATEGORIES_IMAGES_SUCCESS -> {
//                        val categoriesImagesListing = JsonManager.getInstance().getCategoryImages_(
//                            jsonObject = it.resultObject
//                        )
//                        categoriesImages.addAll(categoriesImagesListing)
//                        getCategories()
//                    }
//
//                    ApplicationEnum.GET_CATEGORIES_IMAGES_ERROR -> {
//                        getCategories()
//                    }
//                }
//
//
//            }
//        })
//
//        viewModel.getApiStatus().observe(this, Observer {
//            run {
//                when (it.enum) {
//                    ApplicationEnum.GET_SESSION_ID_SUCCESS -> {
//                        AppPreferenceManager.setValue("sessionId", it.sessionId)
//                        getCategories()
//                    }
//                    ApplicationEnum.GET_MENU_SUCCESS -> {
//                        hideLoading()
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
//                                                                                }
//                                                                            )
//                                                                        }
//                                                                    }
//                                                                }
//
//                                                            } else
//                                                                null
//
//                                                            menuItemsLocalList.add(MenuItemModel().apply {
//                                                                menuItemName =
//                                                                    menuItemJonObject.getString("menuItemName")
//                                                                price =
//                                                                    menuItemJonObject.getString("price")
//                                                                ID =
//                                                                    menuItemJonObject.getString("ID")
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
//                                                            menuItems = menuItemsLocalList
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
//                                                            //  showShortToast("is string")
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
//                                                                                menuItem.getJSONArray(
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
//                                                            price = menuItem.getString("price")
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
//                                                        price =
//                                                            menuItemJonObject.getString("price")
//                                                        ID =
//                                                            menuItemJonObject.getString("ID")
//                                                        //extraOptions.clear()
//                                                        extraOptions.addAll(extraOptionsIdCat)
//                                                        // extraOptionsId.clear()
//                                                    })
//                                                    categories.add(Category().apply {
//                                                        masterCid = cat.getString("MasterCID")
//                                                        name = cat.getString("name")
//                                                        ID = cat.getString("ID")
//                                                        menuItems.clear()
//                                                        menuItems = menuItemsLocalList
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
//                                                    price = menuItem.getString("price")
//                                                    //extraOptions.clear()
//                                                    extraOptions.addAll(extraOptionsIdCat)
//                                                    //  extraOptionsId.clear()
//                                                })
//                                                categories.add(Category().apply {
//                                                    masterCid = cat.getString("MasterCID")
//                                                    name = cat.getString("name")
//                                                    ID = cat.getString("ID")
//                                                    menuItems.clear()
//                                                    menuItems = menuItemsLocalList
//                                                    groupName = data.getString("name")
//                                                })
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
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
//                        initAdapter()
//                        showView(view = mBinding.rvCategories)
////                        categoriesAdapter.notifyDataSetChanged()
//                    }
//                    else -> {
//                        AppProgressDialog.dismissProgressDialog()
//                    }
//                }
//            }
//        })
    }
    /*-----------------------------*/

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }
        mBinding.ivBag.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun initAdapter() {
        if (categories.isNotEmpty()) {
            val distinctList = categories.distinctBy { it.ID }
            categories.clear()
            categories.addAll(distinctList)
            categories.sortBy { it.name }
        }
        categoriesAdapter = CategoriesAdapter(categories = categories, itemClickListener = this)
        mBinding.rvCategories.adapter = categoriesAdapter
    }

    private fun goToProducts() {
        startActivity(
            Intent(
                this@CategorySelectionActivity,
                CategoryProductsActivity::class.java
            ).putExtra(IntentParams.PRODUCTS, selectedCategory)
        )
    }

    @SuppressLint("SetTextI18n")
    private suspend fun getCartItemsCount() {
        val db = AppDatabase(this)
        val cartItemsCount = db.cartDao().getCartCount()
//        val cartItems = db.cartDao().getAllWithOUtTerminal()
        val cartItems = db.cartDao().getAll()
        var localCount = 0
        cartItems.forEach {
            localCount += (it.quantity ?: 0)
        }
        Log.d("Quantity Based Count", localCount.toString())
        withContext(Dispatchers.Main) {
            // mBinding.tvCartCount.text = "Bag ($cartItemsCount)"
            mBinding.tvCartCount.text = "Bag ($localCount)"
        }
    }

    override fun menuItemTapped(products: Category, position: Int) {
        selectedCategory = products
        /*Edited by Usama*/
//        AppProgressDialog.showProgressDialog(context = this@CategorySelectionActivity)
//        getCategoryProductImages(selectedCategory.ID)

        goToProducts()

        /*-------------------*/

//        startActivity(
//            Intent(
//                this@CategorySelectionActivity,
//                CategoryProductsActivity::class.java
//            ).putExtra(IntentParams.PRODUCTS, products)
//        )
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            getCartItemsCount()
        }
    }
}