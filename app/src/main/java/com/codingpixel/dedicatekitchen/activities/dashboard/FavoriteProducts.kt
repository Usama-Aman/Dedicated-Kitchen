package com.codingpixel.dedicatekitchen.activities.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.product.ProductDetailActivity
import com.codingpixel.dedicatekitchen.adapters.ProductsAdapter
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.databinding.FavoriteProductsBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ProductItemListener
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class FavoriteProducts : BaseActivity() {
    private lateinit var mBinding: FavoriteProductsBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var productsAdapter: ProductsAdapter
    private var productsList = ArrayList<MenuItemModel>()
    private var categories = ArrayList<Category>()
    private var _menuItems = ArrayList<MenuItemModel>()
    private var favList = ArrayList<MenuItemModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.favorite_products)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        changeStatusBarColor(getColor(R.color.white))
        getCategoryFromDB()
        getFavorites()
        initApiResponseObserver()
    }

    private fun initAdapter() {
        productsAdapter = ProductsAdapter(products = favList, productItemListener = object :
            ProductItemListener {
            override fun productTapped(position: Int, product: MenuItemModel) {
                startActivity(
                    Intent(
                        this@FavoriteProducts,
                        ProductDetailActivity::class.java
                    ).putExtra(IntentParams.PRODUCT_ID, product.ID)
//                        .putExtra(IntentParams.OPTIONS, options)
                        .putExtra(IntentParams.TAG, IntentParams.PLACEORDER)
                )
            }

            override fun plusTapped(position: Int) {
//                startActivity(
//                    Intent(
//                        this@FavoriteProducts,
//                        ProductDetailActivity::class.java
//                    )
//                )

            }

            override fun minusTapped(position: Int) {

            }

            override fun heartTapped(position: Int) {
                if (isUserLoggedIn()) {
                    AppProgressDialog.showProgressDialog(context = this@FavoriteProducts)
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
            when (it.applicationEnum) {
                ApplicationEnum.GET_FAVOURITES_LISTING_SUCCESS -> {
                    favList = ArrayList()
                    productsList = ArrayList()
                    AppProgressDialog.dismissProgressDialog()
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
                        favList[index].isFavourite = !favList[index].isFavourite
                        productsAdapter.notifyItemChanged(index)
                    }
                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                }
            }
        }
    }

    private fun getFavorites() {
        AppProgressDialog.showProgressDialog(this)
        viewModel.getFavouritesList()
    }

    private fun getCategoryFromDB() {
        val db = AppDatabase(this)
        GlobalScope.launch {
            val data = db.categoryDao().getCategoryWithProducts()
            if (data.isEmpty()) {
                AppProgressDialog.showProgressDialog(this@FavoriteProducts)
                getCategories()
                viewModel.getCategoriesImages()
            } else {
                categories.clear()
                data.forEach {
                    _menuItems = ArrayList()
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
                            price = product.price!!
                            image = if (product.image.isNullOrEmpty()) "" else product.image
                            extraOptions = _productOptions
                            categoryId = product.categoryId!!
                        })
                    }

                    categories.add(Category().apply {
                        masterCid = ""
                        name = it.category.name!!
                        ID = it.category.id!!
                        image = if (it.category.image.isNullOrEmpty()) "" else it.category.image!!
                        menuItems = _menuItems
                    })
                }
                val sortedData = Collections.sort(categories,
                    Comparator<Category?> { v1, v2 -> v1!!.name.compareTo(v2!!.name) })
                // Log.d("sorteddataaaa" , sortedData.toString())
                this@FavoriteProducts.runOnUiThread {
                    //  initAdapter()
                    getFavorites()
                }
            }
        }
    }

    private fun getCategories() {
        viewModel.getMenuCategories(body = RequestBodyUtil.getMenuRequestBody())
    }
}
