package com.codingpixel.dedicatekitchen.fragments.bottomtabs

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.auth.RegisterActivity
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.activities.product.CategoryProductsActivity
import com.codingpixel.dedicatekitchen.activities.product.CategorySelectionActivity
import com.codingpixel.dedicatekitchen.adapters.home.MenuCategoriesAdapter
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Product.Product
import com.codingpixel.dedicatekitchen.databinding.FragmentMenuBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager.Companion.getSelectedKitchenFromSharedPreferences
import com.codingpixel.dedicatekitchen.interfaces.MealPrepPopupListener
import com.codingpixel.dedicatekitchen.interfaces.MenuInterface
import com.codingpixel.dedicatekitchen.interfaces.OrderTypeListener
import com.codingpixel.dedicatekitchen.interfaces.TwoButtonsDialogListener
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.updates.generic_model.GetAllKitchenModel
import com.codingpixel.dedicatekitchen.updates.generic_model.GetMenuCategoryByKitchenIdModel
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : BaseFragment() {

    private lateinit var mBinding: FragmentMenuBinding


    private lateinit var viewModel: MenuViewModel

    private lateinit var takOutCategoriesAdapter: MenuCategoriesAdapter
    private lateinit var mealPrepCategoriesAdapter: MenuCategoriesAdapter


    private lateinit var selectedCategory: Category


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*Edited by Usama*/
//        initViewModel()
        /*---------------*/
        initClickListener()
//        initTakeOutCategoriesAdapter()
        //initAdapters()

        /*Edited by Usama*/
//        getCategoryFromDB()
//        getMealPrepCategoriesFromDb()

        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

        observeCPApiResponse()
        initTakeOutCategoriesAdapter()
        initMealPrepAdapters()

        /*----------------*/
    }


    private fun initViewModel() {
        val db = AppDatabase(context!!)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        viewModel.getApiDBStatus().observe(activity!!, androidx.lifecycle.Observer {
            run {
                when (it.applicationEnum) {

                    ApplicationEnum.GET_PRODUCT_IMAGES_SUCCESS -> {
                        println("called->product images success")
                        AppProgressDialog.dismissProgressDialog()
                        val productImagesListing = JsonManager.getInstance()
                            .getProductImages(jsonObject = it.resultObject)
                        println("productImagesListing-> " + productImagesListing.count())
                        selectedCategory.menuItems.forEachIndexed { index, menuItemModel ->
                            val matchedIndex =
                                productImagesListing.indexOfFirst {
                                    println("productId -> " + it.product_id + " menuItemModel -> " + menuItemModel.ID)
                                    it.product_id == menuItemModel.ID
                                }
                            if (matchedIndex != -1)
                                selectedCategory.menuItems[index].image =
                                    productImagesListing[matchedIndex].image
                            else
                                selectedCategory.menuItems[index].image =
                                    ApiUrls.DEFAULT_PRODUCT_IMAGE_URL

                            println("matchedIndexmatchedIndex -> " + matchedIndex)
                            println("image -> " + selectedCategory.menuItems[index].image)

                            GlobalScope.launch(Dispatchers.IO) {
                                val item = db.productDao().get(selectedCategory.menuItems[index].ID)
                                db.productDao().update(
                                    Product(
                                        item.pKey,
                                        item.id,
                                        item.name,
                                        item.itemDescription,
                                        item.price,
                                        selectedCategory.menuItems[index].image,
                                        item.categoryId,
                                        item.fkCategoryId
                                    )
                                )
                            }
                        }
                        goToProducts()
                    }

                    else -> {

                    }


                }
            }
        })

    }

    private fun goToProducts() {
        startActivity(
            Intent(
                context!!,
                CategoryProductsActivity::class.java
            ).apply {
                putExtra(IntentParams.PRODUCTS, selectedCategory)
                putExtra(IntentParams.fromViewMenu, true)
            }
        )
    }

    fun refreshData(showLoading: Boolean = false) {
//        if (showLoading)
//            (activity as BaseActivity?)?.showLoading()
        /*Edited by Usama*/
//        getCategoryFromDB()
//        getMealPrepCategoriesFromDb()

        changeData(true)
        /*----------------*/
    }

    fun changeData(showLoading: Boolean = false) {
        if (showLoading)
            (activity as BaseActivity?)?.showLoading()
        takeOutCategories.clear()
        mealPrepCategories.clear()
        /*Edited by Usama*/
//        getCategoryFromDB()
//        getMealPrepCategoriesFromDb()

        getMenuCategoriesByKitchen()
        /*----------------*/
    }

    private fun getCategoryProductImages(categoryId: String = "") {
        viewModel.getProductImages(
            categoryId = categoryId
        )
    }

    private fun initTakeOutCategoriesAdapter() {
        takOutCategoriesAdapter = MenuCategoriesAdapter(categories = takeOutCategories,
            itemClickListener = object : MenuInterface {
                override fun menuItemTapped(products: Category, position: Int) {
                    selectedCategory = products

                    /*Edited by Usama*/
//                    AppProgressDialog.showProgressDialog(context = context!!)
//                    getCategoryProductImages(products.ID)

                    goToProducts()

                    /*---------------------*/

                }
            })
        mBinding.rvTakeOutCategories.adapter = takOutCategoriesAdapter
    }

    private fun initMealPrepAdapters() {
        mealPrepCategoriesAdapter = MenuCategoriesAdapter(categories = mealPrepCategories,
            itemClickListener = object : MenuInterface {
                override fun menuItemTapped(products: Category, position: Int) {
                    selectedCategory = products

                    /*Edited by Usama*/
//                    AppProgressDialog.showProgressDialog(context = context!!)
//                    getCategoryProductImages(products.ID)

                    goToProducts()

                    /*----------------------*/
                }
            })
        mBinding.rvMealPrepCategories.adapter = mealPrepCategoriesAdapter
    }

    private fun initClickListener() {

        mBinding.tvOrder.setOnClickListener {
            CommonMethods.showOrderSelectionDialog(fragmentManager = childFragmentManager,
                callback = object : OrderTypeListener {
                    override fun startOrder(type: String) {
                        when (type) {
                            "takeout" -> startTakeOut()
                            "mealprep" -> startMealPrep()
                            else -> {
                                showWarningToast(message = "Invalid Order Type")
                                Log.d("OrderType", "Invalid")
                            }
                        }
                    }

                    override fun openSkip() {
                        try {
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(ApiUrls.SKIP_URL))
                            startActivity(browserIntent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                })
        }

        mBinding.ivTakeOut.setOnClickListener {
            startTakeOut()
        }

        mBinding.ivMealPrep.setOnClickListener {
            startMealPrep()
        }
    }

    private fun startTakeOut() {
        val db = AppDatabase(context!!)
        GlobalScope.launch {
            val cartItemsExist =
                db.cartDao().exists(type = ApplicationEnum.MEAL_PREP.toString())
            if (cartItemsExist) {
                activity?.runOnUiThread {
                    context?.let { it1 ->
                        errorDialogue(
                            "Please complete your current order.",
                            it1
                        )
                    }
                }
            } else {
                AppPreferenceManager.setValue(IntentParams.ORDERTYPE, "Takeout Instant")
                MyApplication.selectedOrderType = ApplicationEnum.TAKE_OUT
                startActivity(
                    Intent(context, CategorySelectionActivity::class.java).apply {
                        putExtra(IntentParams.isMealPrep, false)
                        putExtra(IntentParams.fromViewMenu, false)
                        putExtra(IntentParams.KITCHEN_ID, getSelectedKitchenFromSharedPreferences())
                    }
                )
                //startActivity(Intent(context, PersonalInfo::class.java))
            }
        }
    }

    private fun startMealPrep() {
        if (isUserLoggedIn()) {
            val db = context?.let { it1 -> AppDatabase(it1) }
            GlobalScope.launch {
                val cartItemsExist =
                    db?.cartDao()?.exists(type = ApplicationEnum.TAKE_OUT.toString())
                if (cartItemsExist!!) {
                    activity?.runOnUiThread {
                        context?.let { it1 ->
                            errorDialogue(
                                "Please complete your current order.",
                                it1
                            )
                        }
                    }
                } else {

                    CommonMethods.showMealPrepDeliveryPopUp(
                        fragmentManager = childFragmentManager,
                        type = "new",
                        mealPrepListener = object : MealPrepPopupListener {
                            override fun deliverySelected(
                                date: String,
                                time: String,
                                location: String
                            ) {
                                MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
                                startActivity(
                                    Intent(
                                        context,
                                        CategorySelectionActivity::class.java
                                    ).apply {
                                        putExtra(IntentParams.isMealPrep, true)
                                        putExtra(IntentParams.fromViewMenu, false)
                                    }
                                )
                            }

                            override fun takeOutSelected(date: String, time: String) {
                                activity?.runOnUiThread {
                                    MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
                                    startActivity(
                                        Intent(
                                            context,
                                            CategorySelectionActivity::class.java
                                        ).apply {
                                            putExtra(IntentParams.isMealPrep, true)
                                            putExtra(IntentParams.fromViewMenu, false)
                                        }
                                    )
                                }
                            }
                        }
                    )

//                    val mealPrepPopup = MealPrepDeliveryTypePopUp.newInstance("new")
//                    mealPrepPopup.setListener(object : MealPrepPopupListener {
//                        override fun deliverySelected(
//                            date: String,
//                            time: String,
//                            location: String
//                        ) {
//                            MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
//                            startActivity(
//                                Intent(
//                                    context,
//                                    CategorySelectionActivity::class.java
//                                ).apply {
//                                    putExtra(IntentParams.isMealPrep, true)
//                                    putExtra(IntentParams.fromViewMenu, false)
//                                }
//                            )
//                        }
//
//                        override fun takeOutSelected(date: String, time: String) {
//                            activity?.runOnUiThread {
//                                MyApplication.selectedOrderType = ApplicationEnum.MEAL_PREP
//                                startActivity(
//                                    Intent(
//                                        context,
//                                        CategorySelectionActivity::class.java
//                                    ).apply {
//                                        putExtra(IntentParams.isMealPrep, true)
//                                        putExtra(IntentParams.fromViewMenu, false)
//                                    }
//                                )
//                            }
//                        }
//                    })
//                    mealPrepPopup.show(
//                        childFragmentManager,
//                        MealPrepDeliveryTypePopUp::class.java.canonicalName
//                    )
                }
            }
        }
        // startActivity(Intent(context, MealPrepActivity::class.java))
        else {
            CommonMethods.showTwoButtonsAlertDialog(fragmentManager = childFragmentManager,
                title = "Alert", message = "Want to order meal prep ?",
                leftButtonText = "Later", rightButtonText = "Register Now",
                twoButtonsDialogListener = object : TwoButtonsDialogListener {
                    override fun leftButtonTapped() {
                    }

                    override fun rightButtonTapped() {
//                        activity?.finish()
                        startActivity(Intent(context, RegisterActivity::class.java))
                    }
                })
        }
    }

    private fun getMealPrepCategoriesFromDb() {
        val db = AppDatabase(context!!)
        GlobalScope.launch(Dispatchers.IO) {
            val data = db.categoryDao().getMealPrepCategoryWithProducts()
            if (data.isEmpty()) {
//                AppProgressDialog.showProgressDialog(this@CategorySelectionActivity)
//                getCategories()
//                viewModel.getCategoriesImages()
            } else {
                mealPrepCategories.clear()
                data.forEach {
                    /* if (it.category.id == "20000233" || it.category.id == "20000037" || it.category.id == "20000300" || it.category.id == "20000301" || it.category.id == "20000302" || it.category.id == "20000039"
                         || it.category.id == "20000336" || it.category.id == "20000337"
                     ) {*/
                    val _menuItems = ArrayList<MenuItemModel>()
                    it.products.forEach { product ->
                        val _productOptions = ArrayList<ExtraOptionsModel>()
                        val productOptions = db.productOptionDao().getByID(product.pKey)
                        println("productOptions -> $productOptions")
                        productOptions.forEach { option ->
                            val productExtras =
                                db.productExtraOptionDao().getAllById(option.id ?: "0")
                            println("productExtrasproductExtras -> $productExtras")
                            val extras = ArrayList<MenuItemModel>()
                            productExtras.forEach { extra ->
                                extras.add(
                                    MenuItemModel().apply {
                                        ID = extra.id ?: ""
                                        menuItemName = extra.name ?: ""
                                        price = extra.price ?: ""
                                        categoryId = extra.categoryId ?: ""
                                    }
                                )
                            }
                            _productOptions.add(ExtraOptionsModel().apply {
                                name = option.name ?: ""
                                minSelection = option.minSelection ?: ""
                                maxSelection = option.maxSelection ?: ""
                                forceMaxSelection = option.forceMaxSelection ?: ""
                                categoryId = option.categoryId ?: ""
                                menuItems.addAll(extras)
                            })
                        }
                        _menuItems.add(MenuItemModel().apply {
                            ID = product.id ?: ""
                            menuItemName = product.name ?: ""
                            price = product.price ?: "0.0"
                            image = if (product.image.isNullOrEmpty()) "" else product.image
                            extraOptions = _productOptions
                        })
                    }
                    mealPrepCategories.add(Category().apply {
                        masterCid = ""
                        name = it.category.name ?: ""
                        ID = it.category.id ?: ""
                        image =
                            if (it.category.image.isNullOrEmpty()) "" else it.category.image!!
                        menuItems = _menuItems
                    })

                }
//                val sortedData = Collections.sort(categories,
//                    Comparator<Category?> { v1, v2 -> v1!!.name.compareTo(v2!!.name) })
                /*val sortedData = mealPrepCategories.toMutableList()
                sortedData.forEach {
                    when (it.name) {
                        "Pre-Built Meals" -> {
                            mealPrepCategories[0] = it
                        }
                        "Custom Meal Prep" -> {
                            mealPrepCategories[1] = it
                        }
                        "Protein Bundles" -> {
                            mealPrepCategories[2] = it
                        }
                        "Veggie Bundles" -> {
                            mealPrepCategories[3] = it
                        }
                        "Carb Bundles" -> {
                            mealPrepCategories[4] = it
                        }
                        "Treats & Snacks" -> {
                            mealPrepCategories[5] = it
                        }

                        "Wraps & Sandwiches" -> {
                            mealPrepCategories[6] = it
                        }

                        "Breakfast" -> {
                            if (mealPrepCategories.size >= 8)
                                mealPrepCategories[7] = it
                        }
                    }
                }*/
//                categories =  sortedData
                /*  for (i in 0 until mealPrepCategories.size) {
                      val sortedProducts = Collections.sort(mealPrepCategories[i].menuItems,
                          Comparator<MenuItemModel?> { v1, v2 ->
                              v1!!.menuItemName.compareTo(
                                  v2!!.menuItemName
                              )
                          })
                  }*/

                // Log.d("sorteddataaaa" , sortedData.toString())
                activity?.runOnUiThread {
                    hideView(view = mBinding.pbMealPrepCategoriesLoader)
                    val distinctCategories = mealPrepCategories.distinctBy { it.ID }
                    mealPrepCategories.clear()
                    mealPrepCategories = arrayListOf()
                    mealPrepCategories.addAll(distinctCategories)
                    mealPrepCategories.sortBy { it.name }
                    initMealPrepAdapters()

                    showView(view = mBinding.rvMealPrepCategories)
                }
            }
        }
    }

    private fun getCategoryFromDB() {
        val db = AppDatabase(context!!)
        GlobalScope.launch(Dispatchers.IO) {
            val data = db.categoryDao().getTakeoutCategoryWithProducts()
            if (data.isEmpty()) {
//                AppProgressDialog.showProgressDialog(this@CategorySelectionActivity)
//                getCategories()
//                viewModel.getCategoriesImages()
            } else {
                takeOutCategories.clear()
                data.forEach {
                    /*if (it.category.id == "20000233" || it.category.id == "20000037" || it.category.id == "20000300" || it.category.id == "20000301" || it.category.id == "20000302" || it.category.id == "20000039"
                        || it.category.id == "20000336" || it.category.id == "20000337"
                    ) {
                        return@forEach
                    }*/
                    val _menuItems = ArrayList<MenuItemModel>()
                    it.products.forEach { product ->
                        val _productOptions = ArrayList<ExtraOptionsModel>()
                        val productOptions = db.productOptionDao().getByID(product.pKey)
//                        val productOptions = db.productOptionDao().getProductWithExtraOptions(product.pKey)
//                        val productExtras = db.productExtraOptionDao().getAllById(productOptions.first().categoryId?: "0")
                        println("productOptions -> $productOptions")
                        productOptions.forEach { option ->
                            val productExtras =
                                db.productExtraOptionDao().getAllById(option.id ?: "0")
                            println("productExtrasproductExtras -> $productExtras")
                            val extras = ArrayList<MenuItemModel>()
                            productExtras.forEach { extra ->
                                extras.add(
                                    MenuItemModel().apply {
                                        ID = extra.id ?: ""
                                        menuItemName = extra.name ?: ""
                                        price = extra.price ?: ""
                                        categoryId = extra.categoryId ?: ""
                                    }
                                )
                            }
                            _productOptions.add(ExtraOptionsModel().apply {
                                name = option.name ?: ""
                                minSelection = option.minSelection ?: ""
                                maxSelection = option.maxSelection ?: ""
                                forceMaxSelection = option.forceMaxSelection ?: ""
                                categoryId = option.categoryId ?: ""
                                menuItems.addAll(extras)
                            })
                        }
                        _menuItems.add(MenuItemModel().apply {
                            ID = product.id!!
                            menuItemName = product.name!!
                            itemDescription = product.itemDescription ?: ""
                            price = product.price!!
                            image = if (product.image.isNullOrEmpty()) "" else product.image
                            extraOptions = _productOptions
                        })
                    }
                    takeOutCategories.add(Category().apply {
                        masterCid = ""
                        name = it.category.name!!
                        ID = it.category.id!!
                        image = if (it.category.image.isNullOrEmpty()) "" else it.category.image!!
                        menuItems = _menuItems
                    })
                }
//                val sortedData = Collections.sort(categories,
//                    Comparator<Category?> { v1, v2 -> v1!!.name.compareTo(v2!!.name) })

//                takeOutCategories.sortedBy { it.ID }

                // val sortedData = takeOutCategories.toMutableList()


                /*sortedData.forEach {
                    when (it.name) {
                        "All Day Breakfast" -> {
                            takeOutCategories[0] = it
                        }
                        "Protein Pancakes & Waffles" -> {
                            takeOutCategories[1] = it
                        }
                        "Wraps & Sandwiches" -> {
                            takeOutCategories[2] = it
                        }
                        "Burgers" -> {
                            takeOutCategories[3] = it
                        }
                        "Bowls & Stir Frys" -> {
                            takeOutCategories[4] = it
                        }
                        "Penne Pasta" -> {
                            takeOutCategories[5] = it
                        }
                        "Build Your Meal" -> {
                            takeOutCategories[6] = it
                        }
                        "Salads" -> {
                            takeOutCategories[7] = it
                        }
                        "Protein Smoothies" -> {
                            takeOutCategories[8] = it
                        }
                        "Fresh Juices" -> {
                            takeOutCategories[9] = it
                        }

                        "Plant Based" -> {
                            takeOutCategories[10] = it
                        }
                        "Kids" -> {
                            takeOutCategories[11] = it
                        }

                        "Pizza" -> {
                            takeOutCategories[12] = it
                        }
                    }
                }*/
                activity?.runOnUiThread {
                    hideView(view = mBinding.pbTakeOutCategoriesLoader)
                    val distinctCategories = takeOutCategories.distinctBy { it.ID }
                    takeOutCategories.clear()
                    takeOutCategories = arrayListOf()
                    takeOutCategories.addAll(distinctCategories)
                    takeOutCategories.sortBy { it.name }
                    initTakeOutCategoriesAdapter()
                    showView(view = mBinding.rvTakeOutCategories)
                }

            }
        }
    }

    companion object {

        const val takeOutType = "takeout"
        const val mealPrepType = "meal_prep"

        var mealPrepCategories = ArrayList<Category>()
        var takeOutCategories = ArrayList<Category>()

        @JvmStatic
        fun newInstance() =
            MenuFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }


    /*Edited by Usama*/

    private fun getMenuCategoriesByKitchen() {
        viewModel.getMenuCategoriesByKitchenId(getSelectedKitchenFromSharedPreferences()?.terminalId ?: "-1")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCPApiResponse() {
        if (::viewModel.isInitialized) {
            viewModel.getCPApiStatus().observe(activity!!, androidx.lifecycle.Observer {
                (activity as BaseActivity?)?.hideLoading()
                when (it.enum) {
                    ApplicationEnum.CP_GET_MENU_CATEGORIES_BY_KITCHEN_ID_SUCCESS -> {
                        val model = Gson().fromJson(it.data.toString(), GetMenuCategoryByKitchenIdModel::class.java)

                        takeOutCategories.clear()
                        mealPrepCategories.clear()
                        for (i in model.result.indices) {
                            if (model.result[i].type == takeOutType) {
                                takeOutCategories.add(model.result[i])
                            } else if (model.result[i].type == mealPrepType) {
                                mealPrepCategories.add(model.result[i])
                            }
                        }

                        takOutCategoriesAdapter.notifyDataSetChanged()
                        mealPrepCategoriesAdapter.notifyDataSetChanged()

                        mBinding.rvTakeOutCategories.visibility = View.VISIBLE
                        mBinding.rvMealPrepCategories.visibility = View.VISIBLE

                        mBinding.pbTakeOutCategoriesLoader.visibility = View.GONE
                        mBinding.pbMealPrepCategoriesLoader.visibility = View.GONE

                        mBinding.tvOrder.isEnabled = true
                        mBinding.tvOrder.isClickable = true


                        val dataIntent = (activity!! as MainBottomBarActivity).intent
                        if (dataIntent != null)
                            if (dataIntent.getBooleanExtra(
                                    IntentParams.ORDER_PLACED,
                                    false
                                )
                            ) {
                                dataIntent.putExtra(IntentParams.ORDER_PLACED, false)
                                (activity!! as MainBottomBarActivity).initOrdersFragment()
                            }


                    }
                    ApplicationEnum.CP_GET_MENU_CATEGORIES_BY_KITCHEN_ID_ERROR -> {
                        showShortToast(it.message.toString())
                    }
                }
            })
        }
    }
}