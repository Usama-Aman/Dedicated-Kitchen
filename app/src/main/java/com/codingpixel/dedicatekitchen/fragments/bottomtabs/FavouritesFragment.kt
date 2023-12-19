package com.codingpixel.dedicatekitchen.fragments.bottomtabs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.product.ProductDetailActivity
import com.codingpixel.dedicatekitchen.adapters.ProductsAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentFavouritesBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ProductItemListener
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.updates.generic_model.*
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouritesFragment : BaseFragment(),
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mBinding: FragmentFavouritesBinding
    private lateinit var viewModel: MenuViewModel
    private var _menuItems = ArrayList<MenuItemModel>()

    private lateinit var productsAdapter: ProductsAdapter
    private var productsList = ArrayList<MenuItemModel>()
    private var categories = ArrayList<Category>()
    private var favList = ArrayList<MenuItemModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    /*Usama Edits*/
    private var currentPageCount = 1
    private var lastPageCount = -1
    private var canLoadMore = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.pullRefresh.setOnRefreshListener(this)
//        productsAdapter =
//            ProductsAdapter(products = filteredProductList, productItemListener = this)
//        mBinding.rvFavouritesProducts.adapter = productsAdapter
        //  getCategoryFromDB()

        /*Edited by Usama*/
//        getCategoryFromDB()
        initApiResponseObserver()
/*        if (isUserLoggedIn())
            getFavorites()*/

        getFavoriteProductsCP(currentPageCount)
        initScrollListener()
        observeCPApiResponse()
        /*-------------*/
    }

    private fun getCategories() {
        viewModel.getMenuCategories(body = RequestBodyUtil.getMenuRequestBody())
    }

    private fun getCategoryFromDB() {
//        val db = context?.let { AppDatabase(it) }
//        GlobalScope.launch {
//            val data = db?.categoryDao()?.getCategoryWithProducts()
//            if (data != null) {
//                if (data.isEmpty()) {
//                    context?.let { AppProgressDialog.showProgressDialog(it) }
//                    getCategories()
//                    viewModel.getCategoriesImages()
//                } else {
//                    categories.clear()
//                    data.forEach {
//                        _menuItems = ArrayList()
//                        it.products.forEach { product ->
//                            val _productOptions = ArrayList<ExtraOptionsModel>()
//                            val productOptions = db.productOptionDao().getByID(product.pKey)
//                            //                        val productOptions = db.productOptionDao().getProductWithExtraOptions(product.pKey)
//                            //                        val productExtras = db.productExtraOptionDao().getAllById(productOptions.first().categoryId?: "0")
//                            println("productOptions -> $productOptions")
//                            productOptions.forEach { option ->
//                                val productExtras =
//                                    db.productExtraOptionDao().getAllById(option.id ?: "0")
//                                println("productExtrasproductExtras -> $productExtras")
//                                val extras = ArrayList<MenuItemModel>()
//                                productExtras.forEach { extra ->
//                                    extras.add(
//                                        MenuItemModel().apply {
//                                            ID = extra.id ?: ""
//                                            menuItemName = extra.name ?: ""
//                                            price = extra.price ?: ""
//                                            categoryId = extra.categoryId ?: ""
//                                        }
//                                    )
//                                }
//                                _productOptions.add(ExtraOptionsModel().apply {
//                                    name = option.name ?: ""
//                                    minSelection = option.minSelection ?: ""
//                                    maxSelection = option.maxSelection ?: ""
//                                    forceMaxSelection = option.forceMaxSelection ?: ""
//                                    categoryId = option.categoryId ?: ""
//                                    menuItems.addAll(extras)
//                                })
//                            }
//                            _menuItems.add(MenuItemModel().apply {
//                                ID = product.id!!
//                                menuItemName = product.name!!
//                                price = product.price ?: "0.0"
//                                itemDescription = product.itemDescription ?: ""
//                                image = if (product.image.isNullOrEmpty()) "" else product.image
//                                extraOptions = _productOptions
//                                categoryId = product.categoryId!!
//                            })
//                        }
//
//                        categories.add(Category().apply {
//                            masterCid = ""
//                            name = it.category.name!!
//                            ID = it.category.id!!
//                            image =
//                                if (it.category.image.isNullOrEmpty()) "" else it.category.image!!
//                            menuItems = _menuItems
//                        })
//                    }
//                    val sortedData = Collections.sort(categories,
//                        Comparator<Category?> { v1, v2 -> v1!!.name.compareTo(v2!!.name) })
//                    // Log.d("sorteddataaaa" , sortedData.toString())
//                    activity?.runOnUiThread {
//                        //  initAdapter()
//                        getFavorites()
//                    }
//                }
//            }
//        }
    }


    private fun initAdapter() {
        productsAdapter = ProductsAdapter(products = favList, productItemListener = object :
            ProductItemListener {
            override fun productTapped(position: Int, product: MenuItemModel) {
                startActivity(
                    Intent(
                        context,
                        ProductDetailActivity::class.java
                    )
                        .putExtra(IntentParams.PRODUCT_ID, favList[position].ID)
                        .putExtra(IntentParams.CATEGORY_ID, favList[position].menu_category_id.toString())
                        .putExtra(IntentParams.TAG, IntentParams.PLACEORDER)
                )
            }

            override fun plusTapped(position: Int) {
//                startActivity(
//                    Intent(
//                        context,
//                        ProductDetailActivity::class.java
//                    )
//                )

                startActivity(
                    Intent(
                        context,
                        ProductDetailActivity::class.java
                    ).putExtra(IntentParams.PRODUCT_ID, favList[position].ID)
                        .putExtra(IntentParams.CATEGORY_ID, favList[position].menu_category_id.toString())
                        .putExtra(IntentParams.TAG, IntentParams.PLACEORDER)
                )
            }

            override fun minusTapped(position: Int) {

            }

            override fun heartTapped(position: Int) {
                if (isUserLoggedIn()) {
                    context?.let { AppProgressDialog.showProgressDialog(context = it) }
                    viewModel.toggleFavourite(
                        productId = favList[position].ID,
                        toggle = if (favList[position].isFavourite)
                            0 else 1,
                        categoryId = favList[position].categoryId,
                        index = position
                    )
                }
            }
        })

        mBinding.rvFavList.adapter = productsAdapter
    }

    private fun initApiResponseObserver() {
        viewModel.getApiDBStatus().observe(this) {

            run {

                when (it.applicationEnum) {
                    ApplicationEnum.GET_FAVOURITES_LISTING_SUCCESS -> {
                        favList = ArrayList()
                        productsList = ArrayList()
                        AppProgressDialog.dismissProgressDialog()
                        mBinding.pullRefresh.isRefreshing = false
                        val favouritesListing = JsonManager.getInstance().getFavouriteListing(
                            jsonObject = it.resultObject
                        )

                        for (i in 0 until categories.size) {
                            productsList.addAll(categories[i].menuItems)
                        }

                        productsList.forEach { product ->
                            if (favouritesListing.any { it.product_id == product.ID }) {
                                product.isFavouriteFetched = true
                                product.isFavourite = true
                                favList.add(product)
                            }
                        }
                        initAdapter()
                    }
                    ApplicationEnum.TOGGLE_FAVOURITE_SUCCESS -> {
                        AppProgressDialog.dismissProgressDialog()
                        val index = JsonManager.getInstance().getIntValue(
                            jsonObject = it.resultObject,
                            keyName = "index"
                        )
                        if (index < favList.size) {
                            favList.removeAt(index)
                            productsAdapter.notifyDataSetChanged()
                        }
                    }
                    else -> {
                        AppProgressDialog.dismissProgressDialog()
                        showShortToast(it.message)
                    }
                }
                toggleEmptyData()
            }


        }
    }

    private fun toggleEmptyData() {
        if (favList.isEmpty()) {
            showView(view = mBinding.tvNoDataFound)
        } else {
            hideView(view = mBinding.tvNoDataFound)
        }
    }

    private fun getFavorites() {
        context?.let { AppProgressDialog.showProgressDialog(it) }
        viewModel.getFavouritesList()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

//    override fun productTapped(position: Int) {
//        startActivity(Intent(context , ProductDetailActivity::class.java))
//    }

    override fun onRefresh() {
        /*Edited by Usama*/
//        viewModel.getFavouritesList()

        favList.clear()
        getFavoriteProductsCP(1)

        /*-----------------*/
    }

    /*Edited by Usama*/

    private fun getFavoriteProductsCP(page: Int) {
        (activity!! as BaseActivity).showLoading()
        viewModel.getFavoriteProductsCP(page)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCPApiResponse() {
        if (::viewModel.isInitialized) {
            viewModel.getCPApiStatus().observe(activity!!) {
                mBinding.pullRefresh.isRefreshing = false
                (activity!! as BaseActivity).hideLoading()
                when (it.enum) {
                    ApplicationEnum.CP_GET_FAVORITE_PRODUCTS_SUCCESS -> {
                        if (currentPageCount == 1)
                            favList.clear()
                        val model = Gson().fromJson(it.data.toString(), FavoriteModelCP::class.java)
                        showView(view = mBinding.rvFavList)

                        if (model.result.data.isNotEmpty()) {
                            hideView(mBinding.tvNoDataFound)
                            for (i in model.result.data.indices) {
                                val m = model.result.data[i].product
                                favList.add(MenuItemModel().apply {
                                    this.image = image_base_url + if (m.image_url.isNullOrEmpty()) "" else m.image_url
                                    this.image_url = if (m.image_url.isNullOrEmpty()) "" else m.image_url
                                    this.ID = m.ID
                                    this.terminalId = m.ID
                                    this.attributes = m.attributes
                                    this.description = m.attributes.itemDescription
                                    this.itemDescription = m.attributes.itemDescription
                                    this.kitchen_id = m.kitchen_id
                                    this.menuItemName = m.attributes.name
                                    this.menu_category_id = m.menu_category_id
                                    this.menu_id = m.menu_id
                                    this.isFavouriteFetched = true
                                    this.isFavourite = true
                                    this.categoryId = m.categoryId
                                    this.price = m.attributes.price
//                                category_name = category.name

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

                                                    })

                                                }

                                            }
                                            is LinkedTreeMap<*, *> -> {
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
                        } else {
                            showView(mBinding.tvNoDataFound)
                        }

                        currentPageCount = model.result.current_page
                        lastPageCount = model.result.last_page

                        if (currentPageCount == 1)
                            initAdapter()
                        else
                            productsAdapter.notifyDataSetChanged()

                    }
                    ApplicationEnum.CP_GET_FAVORITE_PRODUCTS_ERROR -> {
                        showShortToast(it.message.toString())
                    }
                }
            }
        }
    }


    private fun initScrollListener() {
        mBinding.rvFavList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                if (!canLoadMore) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productsList.size - 1) {
                        recyclerView.post { loadMore() }
                        canLoadMore = true
                    }
                    canLoadMore = false
                }
            }
        })
    }

    private fun loadMore() {
        try {
            productsAdapter.notifyItemInserted(productsList.size - 1)
            if (currentPageCount < lastPageCount) {
                getFavoriteProductsCP(currentPageCount + 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}