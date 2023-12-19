package com.codingpixel.dedicatekitchen.activities.meal_prep

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.auth.SignInActivity
import com.codingpixel.dedicatekitchen.activities.product.ProductDetailActivity
import com.codingpixel.dedicatekitchen.adapters.ProductsAdapter
import com.codingpixel.dedicatekitchen.base.BaseFragment
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Product.Product
import com.codingpixel.dedicatekitchen.databinding.FragmentPreBuildMealBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.interfaces.ProductItemListener
import com.codingpixel.dedicatekitchen.models.Category
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class CustomMealFragment : BaseFragment() {
    private lateinit var binding: FragmentPreBuildMealBinding
    private lateinit var topProductsAdapter: ProductsAdapter
    private lateinit var viewModel: MenuViewModel
    private var products = ArrayList<MenuItemModel>()
    private var category = Category()
    private var catId: String = "20000037"
    private var categories = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pre_build_meal, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        getCategoryFromDB()
        initApiResponseObserver()
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

    private fun getCategoryFromDB() {
        val db = AppDatabase(requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val data = db.categoryDao().getCategoryWithProducts()
            if (data.isEmpty()) {
            } else {
                categories.clear()
                data.forEach {
                    if (it.category.id != catId) {
                        return@forEach
                    }
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
                            price = product.price!!
                            image = if (product.image.isNullOrEmpty()) "" else product.image
                            extraOptions = _productOptions
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
                Log.d("sorteddataaaa", sortedData.toString())
                for (i in 0 until categories.size) {
                    val sortedProducts = Collections.sort(categories[i].menuItems,
                        Comparator<MenuItemModel?> { v1, v2 -> v1!!.menuItemName.compareTo(v2!!.menuItemName) })
                }

                // Log.d("sorteddataaaa" , sortedData.toString())
                activity?.runOnUiThread {
                    products.clear()
                    products = categories[0].menuItems
                    getCategoryProductImages()
                    initAdapter()
                    // topProductsAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun getCategoryProductImages() {
        AppProgressDialog.showProgressDialog(requireContext())
        viewModel.getProductImages(
            categoryId = catId
        )
    }

    private fun initApiResponseObserver() {
        val db = AppDatabase(requireContext())
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
                    AppProgressDialog.dismissProgressDialog()
                    val index = JsonManager.getInstance().getIntValue(
                        jsonObject = it.resultObject,
                        keyName = "index"
                    )
                    if (index < products.size) {
                        products[index].isFavourite = !products[index].isFavourite
                        topProductsAdapter.notifyItemChanged(index)
                    }
                }

                ApplicationEnum.GET_PRODUCT_IMAGES_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    val productImagesListing = JsonManager.getInstance()
                        .getProductImages(jsonObject = it.resultObject)
                    products.forEachIndexed { index, menuItemModel ->
                        val matchedIndex =
                            productImagesListing.indexOfFirst {
                                it.product_id == menuItemModel.ID
                            }
                        if (matchedIndex != -1)
                            products[index].image =
                                productImagesListing[matchedIndex].image
                        else
                            products[index].image =
                                ApiUrls.DEFAULT_PRODUCT_IMAGE_URL


                        GlobalScope.launch(Dispatchers.IO) {
                            val item = db.productDao().get(products[index].ID)
                            db.productDao().update(
                                Product(
                                    item.pKey,
                                    item.id,
                                    item.name,
                                    item.itemDescription,
                                    item.price,
                                    products[index].image,
                                    item.categoryId,
                                    item.fkCategoryId
                                )
                            )
                        }
                    }
                    topProductsAdapter.notifyDataSetChanged()
                }

                ApplicationEnum.GET_PRODUCT_IMAGES_ERROR -> {
                    println("called->product images failure")
                    AppProgressDialog.dismissProgressDialog()
                    products.forEach {
                        it.image = ApiUrls.DEFAULT_PRODUCT_IMAGE_URL
                    }
                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                }
            }
        }
    }

    private fun initAdapter() {
        topProductsAdapter = ProductsAdapter(products = products, productItemListener = object :
            ProductItemListener {
            override fun productTapped(position: Int, product: MenuItemModel) {
                startActivity(
                    Intent(
                        requireContext(),
                        ProductDetailActivity::class.java
                    ).putExtra(IntentParams.PRODUCT, product)
                        .putExtra(IntentParams.TAG, IntentParams.PLACEORDER)
                )
            }

            override fun plusTapped(position: Int) {
//                startActivity(
//                    Intent(
//                        requireContext(),
//                        ProductDetailActivity::class.java
//                    )
//                )

                startActivity(
                    Intent(
                        requireContext(),
                        ProductDetailActivity::class.java
                    )   .putExtra(IntentParams.PRODUCT_ID, products[position].ID)
                        .putExtra(IntentParams.CATEGORY_ID, category.ID)
                        .putExtra(IntentParams.TAG, IntentParams.PLACEORDER)
                )

            }

            override fun minusTapped(position: Int) {

            }

            override fun heartTapped(position: Int) {
                if (isUserLoggedIn()) {
                    AppProgressDialog.showProgressDialog(context = requireContext())
                    viewModel.toggleFavourite(
                        productId = products[position].ID,
                        toggle = if (products[position].isFavourite)
                            0 else 1,
                        categoryId = catId,
                        index = position
                    )
                } else {
                    startActivity(Intent(requireContext(), SignInActivity::class.java))
                }
            }
        })
        binding.rvTopProducts.adapter = topProductsAdapter
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            CustomMealFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}