package com.codingpixel.dedicatekitchen.activities.product

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.auth.SignInActivity
import com.codingpixel.dedicatekitchen.activities.checkout.CartActivity
import com.codingpixel.dedicatekitchen.adapters.ProductsAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.databinding.ActivityCategoryProuductsBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ProductInfoListener
import com.codingpixel.dedicatekitchen.interfaces.ProductItemListener
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.models.OptionModel
import com.codingpixel.dedicatekitchen.updates.generic_model.*
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.personal_info_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CategoryProductsActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mBinding: ActivityCategoryProuductsBinding
    private lateinit var topProductsAdapter: ProductsAdapter
    private lateinit var viewModel: MenuViewModel

    private var allProducts = ArrayList<MenuItemModel>()
    private var category = Category()
    private var optionsArray = ArrayList<OptionModel>()
    private var categoriesList = ArrayList<Category>()

    private var fromViewMenu: Boolean = false

    /*Edited by Usama*/
    private var products = ArrayList<MenuItemModel>()
    private lateinit var cpMenuItemModel: CPMenuItemModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_category_prouducts)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        changeStatusBarColor(statusBarColor = getColor(R.color.grayBgColor))

        getIntentData()
        initClickListener()
        initAdapters()
        initApiResponseObserver()


        /*Edited by Usama*/
//        getExtrasFromDB()

        getMenuItemByKitchenAndCategoryID()
        observeCPApiResponse()

        /*--------------------*/
    }

    private fun getExtrasFromDB() {
        val db = AppDatabase(this)
        GlobalScope.launch {
            val data = db.productExtraOptionDao().getAll()
            Log.d("extras -> ", data.size.toString())
            if (data.isEmpty()) {
                this@CategoryProductsActivity.runOnUiThread {
                    AppProgressDialog.showProgressDialog(this@CategoryProductsActivity)
                }
                viewModel.getOptions(body = RequestBodyUtil.getOptionsRequestBody())
            }
        }
    }

    private fun initApiResponseObserver() {

        viewModel.getApiDBStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.GET_FAVOURITES_LISTING_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    val favouritesListing = JsonManager.getInstance().getFavouriteListing(
                        jsonObject = it.resultObject
                    )
                    products.forEachIndexed { index, menuItemModel ->
                        products[index].isFavourite =
                            favouritesListing.any { it.product_id == menuItemModel.ID }
                        products[index].isFavouriteFetched = true
                    }
                    topProductsAdapter.notifyDataSetChanged()
                }

                ApplicationEnum.GET_FAVOURITES_LISTING_ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    products.forEachIndexed { index, menuItemModel ->
                        products[index].isFavourite = false
                        products[index].isFavouriteFetched = true
                    }
                    topProductsAdapter.notifyDataSetChanged()
                    //showShortToast(message = "Unable to fetch Favourites")
                }

                ApplicationEnum.TOGGLE_FAVOURITE_ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                }

                ApplicationEnum.TOGGLE_FAVOURITE_SUCCESS -> {
                    hideLoading()
                    val index = JsonManager.getInstance().getIntValue(
                        jsonObject = it.resultObject,
                        keyName = "index"
                    )
                    if (index < products.size) {

                        if (JsonManager.getInstance().getBoolValue(
                                jsonObject = it.resultObject,
                                keyName = "changeState"
                            )
                        ) {
                            products[index].isFavourite = !products[index].isFavourite

                        }
                        topProductsAdapter.notifyItemChanged(index)


                    }
                }
            }
        }

        /*Edited by Usama*/
//        viewModel.getApiStatus().observe(this) {
//
//            if (mBinding.pullRefresh.isRefreshing)
//                mBinding.pullRefresh.isRefreshing = false
//
//            when (it.enum) {
//                ApplicationEnum.SESSION_EXPIRED -> {
//                    AppPreferenceManager.setValue("sessionId", it.sessionId)
//                    viewModel.getMenu(body = RequestBodyUtil.getOptionsRequestBody())
//                }
//
//                ApplicationEnum.GET_SESSION_ID_SUCCESS -> {
//                    AppPreferenceManager.setValue("sessionId", it.sessionId)
//                    viewModel.getMenu(body = RequestBodyUtil.getOptionsRequestBody())
//                }
//                ApplicationEnum.GET_OPTIONS_SUCCESS, ApplicationEnum.GET_MENU_SUCCESS -> {
//                    //AppProgressDialog.dismissProgressDialog()
//                    val data = it.dataObject
//
//                    for (i in 0 until data.getJSONObject("options").getJSONArray("category")
//                        .length()) {
//                        val categoryJsonObject =
//                            data.getJSONObject("options").getJSONArray("category")[i] as JSONObject
//                        val catName = categoryJsonObject.getString("name")
//                        val catId = categoryJsonObject.getString("ID")
//                        val optionsJson = if (categoryJsonObject.has("option")) {
//                            when {
//                                categoryJsonObject.get("option") is JSONObject -> {
//                                    categoryJsonObject.getJSONObject("option")
//                                }
//                                categoryJsonObject.get("option") is JSONArray -> {
//                                    categoryJsonObject.getJSONArray("option")
//                                }
//                                categoryJsonObject.get("option") is String -> {
//                                    categoryJsonObject.getString("option")
//                                }
//                                else -> {
//                                    null
//                                }
//                            }
//                            //categoryJsonObject.getJSONArray("option")
//                        } else
//                            null
//
//                        val optionLocalList = ArrayList<MenuItemModel>()
//                        if (optionsJson != null) {
//                            when (optionsJson) {
//                                is String -> {
//                                    categoriesList.add(Category().apply {
//                                        name = catName
//                                        ID = catId
//                                        // No Options Available
//                                        menuItems.clear()
//                                    })
//                                }
//
//                                is JSONArray -> {
//                                    for (j in 0 until optionsJson.length()) {
//                                        val optionObj = optionsJson[j] as JSONObject
//                                        val itemName = optionObj.getString("menuItemName")
//                                        val priceItem = optionObj.getString("price")
//                                        val extraId = optionObj.getString("ID")
//                                        optionLocalList.add(MenuItemModel().apply {
//                                            menuItemName = itemName
//                                            price = priceItem
//                                            ID = extraId
//                                        })
//                                    }
//                                    categoriesList.add(Category().apply {
//                                        name = catName
//                                        ID = catId
//                                        menuItems.clear()
//                                        optionLocalList.forEach { optionMenu ->
//                                            menuItems.add(MenuItemModel().apply {
//                                                menuItemName = optionMenu.menuItemName
//                                                price = optionMenu.price
//                                                ID = optionMenu.ID
//                                            })
//                                        }
//                                    })
//                                }
//
//                                is JSONObject -> {
//                                    val itemName = optionsJson.getString("menuItemName")
//                                    val priceItem = optionsJson.getString("price")
//                                    val id = optionsJson.getString("ID")
//                                    optionLocalList.add(MenuItemModel().apply {
//                                        menuItemName = itemName
//                                        price = priceItem
//                                        ID = id
//                                    })
//                                    categoriesList.add(Category().apply {
//                                        name = catName
//                                        ID = catId
//                                        menuItems.clear()
//                                        optionLocalList.forEach { optionMenu ->
//                                            menuItems.add(MenuItemModel().apply {
//                                                menuItemName = optionMenu.menuItemName
//                                                price = optionMenu.price
//                                                ID = optionMenu.ID
//                                            })
//                                        }
//                                    })
//                                }
//                            }
//                        } else {
//                            categoriesList.add(Category().apply {
//                                name = catName
//                                ID = catId
//                                // No Options Available
//                                menuItems.clear()
//                            })
//                        }
//                    }
//                    Log.d("Categories Size", categoriesList.size.toString())
//                    if (isUserLoggedIn())
//                        viewModel.getFavouritesList()
//                    else {
//                        AppProgressDialog.dismissProgressDialog()
//                        products.forEachIndexed { index, menuItemModel ->
//                            products[index].isFavourite = false
//                            products[index].isFavouriteFetched = true
//                        }
//                        initAdapters()
////                        topProductsAdapter.notifyDataSetChanged()
//                    }
//                    //optionsArray = data.getJSONArray("category") as ArrayList<OptionModel>
//                }
//                else -> {
//
//                    AppProgressDialog.dismissProgressDialog()
//                }
//            }
//        }
    }

    private fun initSequenceOfMealPrepCategories() {
        /*Edited by Usama  only commented the code, did not comment the function calling*/
//        if (allProducts.isNotEmpty()) {
//
//            val localProducts = arrayListOf<MenuItemModel>()
//            val restProducts = arrayListOf<MenuItemModel>()
//            for (id in Constants.MEAL_PREP_CATS) {
//                val matchedIndex = allProducts.indexOfFirst { it.ID == id }
//                if (matchedIndex != -1) {
//                    localProducts.add(allProducts[matchedIndex])
//                } else {
//                    restProducts.add(allProducts[matchedIndex])
//                }
//            }
//            products.clear()
//            products = arrayListOf()
//            products.addAll(localProducts)
//            products.addAll(restProducts)
//        }
        /*-------------------------------*/
    }

    private fun getIntentData() {
        category = intent?.getSerializableExtra(IntentParams.PRODUCTS) as Category
        fromViewMenu = intent?.getBooleanExtra(IntentParams.fromViewMenu, false) ?: false
        mBinding.tvCategory.text = category.name


        /*Edited by Usama*/
//        products.clear()
//        products = arrayListOf()
//        products.addAll(category.menuItems.sortedBy { it.ID })
//
//        val productWithImages = ArrayList<MenuItemModel>()
//        val productWithOutImages = ArrayList<MenuItemModel>()
//        for (i in 0 until products.size) {
//            if (products[i].image.contains("product_default")) {
//                productWithOutImages.add(products[i])
//            } else {
//                productWithImages.add(products[i])
//            }
//        }
//
//        products.clear()
//        products.addAll(productWithImages)
//        products.addAll(productWithOutImages)
        /**/
    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }
        mBinding.ivBag.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        mBinding.pullRefresh.setOnRefreshListener(this)
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
        Log.d("Quantitiy Based Count", localCount.toString())
        withContext(Dispatchers.Main) {
            // mBinding.tvCartCount.text = "Bag ($cartItemsCount)"
            mBinding.tvCartCount.text = "Bag ($localCount)"
        }
    }

    private fun initAdapters() {
        /*Edited by Usama*/
//        if (products.isNotEmpty()) {
//            val distinctCategories = products.distinctBy { it.ID }
//            products.clear()
//            products = arrayListOf()
//            products.addAll(distinctCategories)
//        }
//        products.forEach {
//            Log.d("Product Id", it.ID)
//        }
        /*----------------------*/

        topProductsAdapter = ProductsAdapter(products = products, productItemListener = object :
            ProductItemListener {
            override fun productTapped(position: Int, product: MenuItemModel) {
                if (fromViewMenu) {
                    showProductInfoPopUp(product = products[position], index = position)
                } else {
                    startActivity(
                        Intent(
                            this@CategoryProductsActivity,
                            ProductDetailActivity::class.java
                        ).apply {
                            /*Edited by Usama*/
                            putExtra(IntentParams.PRODUCT_ID, products[position].terminalId)
                            putExtra(IntentParams.CATEGORY_ID, category.ID)
                            putExtra(IntentParams.TAG, IntentParams.PLACEORDER)
                        }
                    )
                }

            }

            override fun plusTapped(position: Int) {

                if (fromViewMenu) {
                    showProductInfoPopUp(product = products[position], index = position)
                } else {
                    startActivity(
                        Intent(
                            this@CategoryProductsActivity,
                            ProductDetailActivity::class.java
                        ).apply {
                            putExtra(IntentParams.PRODUCT_ID, products[position].ID)
                            putExtra(IntentParams.CATEGORY_ID, category.ID)
                            putExtra(IntentParams.TAG, IntentParams.PLACEORDER)
                        }
                    )
                }
            }

            override fun minusTapped(position: Int) {

            }

            override fun heartTapped(position: Int) {
                if (products.any { it.isFavouriteFetched }) {
                    if (isUserLoggedIn()) {
                        AppProgressDialog.showProgressDialog(context = this@CategoryProductsActivity)
                        viewModel.toggleFavourite(
                            productId = products[position].ID,
                            toggle = if (products[position].isFavourite)
                                0 else 1,
                            categoryId = category.ID,
                            index = position
                        )
                    } else {
                        startActivity(
                            Intent(
                                this@CategoryProductsActivity,
                                SignInActivity::class.java
                            )
                        )
                    }
                }

            }
        }, showPlusIcon = !fromViewMenu)
        mBinding.rvTopProducts.adapter = topProductsAdapter
        showView(view = mBinding.rvTopProducts)
    }

    private fun showProductInfoPopUp(product: MenuItemModel, index: Int) {
        CommonMethods.showProductInfoDialog(
            fragmentManager = supportFragmentManager,
            category = category,
            product = product,
            listener = object : ProductInfoListener {
                override fun toggle(catId: String, productId: String, toggle: Int) {
                    showLoading()
                    viewModel.toggleFavourite(
                        productId = productId,
                        toggle = toggle,
                        categoryId = category.ID,
                        index = index,
                        changeState = false
                    )
                }

            }
        )
    }

    override fun onRefresh() {

        /*Edited by Usama*/
//        viewModel.getOptions(body = RequestBodyUtil.getOptionsRequestBody())
        getMenuItemByKitchenAndCategoryID()
        /*------------------*/

    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            getCartItemsCount()
        }
    }

    /*Edited by Usama*/

    private fun getMenuItemByKitchenAndCategoryID() {
        showLoading()
        viewModel.getMenuItemByKitchenAndCategoryID(
            AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId ?: "-1",
            category.ID
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCPApiResponse() {
        if (::viewModel.isInitialized) {
            viewModel.getCPApiStatus().observe(this@CategoryProductsActivity) {
                mBinding.pullRefresh.isRefreshing = false
                hideLoading()
                when (it.enum) {
                    ApplicationEnum.CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID_SUCCESS -> {

                        products.clear()
                        val model = Gson().fromJson(it.data.toString(), CPMenuItemModel::class.java)
                        cpMenuItemModel = model
                        showView(view = mBinding.rvTopProducts)

                        for (i in model.result.menu_items.indices) {
                            val m = model.result.menu_items[i]
                            products.add(MenuItemModel().apply {
                                image = image_base_url + m.image_url
                                image_url = m.image_url
                                ID = m.ID
                                terminalId = ID
                                attributes = m.attributes
                                description = m.attributes.itemDescription
                                itemDescription = m.attributes.itemDescription
                                kitchen_id = m.kitchen_id
                                menuItemName = m.attributes.name
                                menu_category_id = m.menu_category_id
                                menu_id = m.menu_id
                                price = m.attributes.price
                                category_name = category.name

                                try {
                                    when (m.attributes.extra_options) {
                                        is ArrayList<*> -> {
                                            val list = m.attributes.extra_options as ArrayList<*>
                                            for (k in 0 until list.size) {

                                                val string = Gson().toJson(list[k])
                                                val eo = Gson().fromJson(string, CPMenuItemExtraOptionsModel::class.java)

                                                this.extraOptions.add(ExtraOptionsModel().apply {

                                                    this.id = eo.ID
                                                    this.maxSelection = eo.maxSelection
                                                    this.minSelection = eo.minSelection
                                                    this.forceMaxSelection = eo.maxSelection
                                                    this.cat_id = eo.masterCID

                                                })

                                            }

                                        }
                                        is LinkedTreeMap<*, *> -> {
//                                        val keyMap = m.attributes.extra_options as LinkedTreeMap<String, Any>
//                                        val keysSet = keyMap.keys
//
//                                        for (ks in keysSet.indices) {
//
//                                            val j = keyMap.getValue(ks.toString())
//                                            val eo = Gson().fromJson(j.toString(), CPMenuItemExtraOptionsModel::class.java)
//
//                                            this.extraOptions.add(ExtraOptionsModel().apply {
//
//                                                ID = eo.ID
//                                                maxSelection = eo.maxSelection
//                                                minSelection = eo.minSelection
//                                                forceMaxSelection = eo.maxSelection
//
//                                            })
//                                        }

                                            MyJson().read(m.attributes.extra_options as LinkedTreeMap<String, Any>)

                                            val jsonString = MyJsonBuilder.build()
                                            jsonString.substring(1, jsonString.length - 1)
                                            jsonString.dropLast(1)

                                            val eo = Gson().fromJson(jsonString, CPMenuItemExtraOptionsModel::class.java)

                                            this.extraOptions.add(ExtraOptionsModel().apply {

                                                this.id = eo.ID
                                                this.maxSelection = eo.maxSelection
                                                this.minSelection = eo.minSelection
                                                this.forceMaxSelection = eo.maxSelection
                                                this.cat_id = eo.masterCID
                                            })


                                        }
                                        else -> {
                                            Log.d("CategoryProductActivity", "It's an error in JSON")
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }


                            })
                        }

                        initAdapters()

                        getFavoriteProductList()
                    }
                    ApplicationEnum.CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID_ERROR -> {
                        showShortToast(it.message.toString())
                    }
                }
            }
        }
    }

    private fun getFavoriteProductList() {
        if (products.isNotEmpty())
            if (isUserLoggedIn())
                viewModel.getFavouritesList()
            else {
                AppProgressDialog.dismissProgressDialog()
                products.forEachIndexed { index, menuItemModel ->
                    products[index].isFavourite = false
                    products[index].isFavouriteFetched = true
                }
                topProductsAdapter.notifyDataSetChanged()
            }
    }
}
