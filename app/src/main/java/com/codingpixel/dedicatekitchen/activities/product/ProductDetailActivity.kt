package com.codingpixel.dedicatekitchen.activities.product

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.mealprep.MealPrepSectionAdapter
import com.codingpixel.dedicatekitchen.application.MyApplication
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.database.AppDatabase
import com.codingpixel.dedicatekitchen.database.Cart.Cart
import com.codingpixel.dedicatekitchen.database.Cart.CartExtraOption
import com.codingpixel.dedicatekitchen.databinding.ActivityProductDetailBinding
import com.codingpixel.dedicatekitchen.helpers.AppPreferenceManager
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.IntentParams
import com.codingpixel.dedicatekitchen.helpers.PrefFlags
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.MealPrepListener
import com.codingpixel.dedicatekitchen.models.ExtraItemOrderModel
import com.codingpixel.dedicatekitchen.models.ExtraOptionsModel
import com.codingpixel.dedicatekitchen.models.MenuItemModel
import com.codingpixel.dedicatekitchen.models.local.ToggleOption
import com.codingpixel.dedicatekitchen.updates.generic_model.*
import com.codingpixel.dedicatekitchen.viewmodels.MenuViewModel
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailActivity : BaseActivity(), ItemClickListener, MealPrepListener {
    private lateinit var mBinding: ActivityProductDetailBinding

    private val ingredientsList = ArrayList<ToggleOption>()
    private lateinit var ingredientsAdapter: MealPrepSectionAdapter
    private var updatedPrice: Float = 0F
    private var isFavourite: Boolean = true
    private var price: Float = 0F
    private var orderExtraDetail = ArrayList<ExtraItemOrderModel>() // save extra info in this list
    private var itemQuantity: Int = 1
    private var originalPrice: Float = 0F
    private var extraOptionPrice: Float = 0F
    private var product = MenuItemModel()
    private var tag = IntentParams.PLACEORDER
    private var cartItemPKey = 0

    private var categoryID: String = ""
    private var currentProductKitchenTerminalId = ""

    /*Edited by Usama*/
    private lateinit var viewModel: MenuViewModel

    private lateinit var selectedProduct: MenuItemModel
    private var selectedProductId = ""

    /*------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        setFullScreen()
//        window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = Color.TRANSPARENT

        getIntentData()
        initClickListener()
        initScroller()
        //  mBinding.rvIngredients.itemAnimator!!.changeDuration = 0
        // updateQuantity()
    }

    @SuppressLint("SetTextI18n")
    private fun getIntentData() {
        tag = intent.getStringExtra(IntentParams.TAG)!!
        if (tag == IntentParams.EDITORDER) {
            hideView(mBinding.ivBackArrow)
        }
        /*Edited by Usama*/
//        val productId = intent.getStringExtra(IntentParams.PRODUCT_ID)

//        selectedProduct = intent.getSerializableExtra(IntentParams.PRODUCT) as MenuItemModel


        cartItemPKey = intent.getIntExtra(IntentParams.CART_ITEM_P_KEY, 0)
        selectedProductId = intent.getStringExtra(IntentParams.PRODUCT_ID) ?: "0"
        categoryID = intent.getStringExtra(IntentParams.CATEGORY_ID) ?: "0"


        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        observeCPApiResponse()
        getMenuItemByKitchenAndCategoryID()

        /*-------------------*/
    }

    @SuppressLint("SetTextI18n")
    private fun geProductFromDB(productId: String) {
        val db = AppDatabase(this)
        GlobalScope.launch {
            val _product = MenuItemModel()
            val _productOptions = ArrayList<ExtraOptionsModel>()

//            val product = db.productDao().get(productId)
            product = selectedProduct
            currentProductKitchenTerminalId = selectedProduct.terminalId ?: ""
//            val category = db.categoryDao().get(product.categoryId ?: "")
//            val options = db.productOptionDao().getByID(product.pKey)


            val cartItem = if (tag == IntentParams.EDITORDER)
                db.cartDao().getItem(cartItemPKey)
            else
                db.cartDao().get(productId.toInt())


            println("ProductDetailActivity -> cartItem -> $cartItem")
            val cartItemOptions =
                db.cartExtraOptionDao()
                    .getAllById(if (cartItem != null && tag == IntentParams.EDITORDER) cartItem.pKey else 0)
            println("ProductDetailActivity -> cartItemOptions -> $cartItemOptions")

            if (cartItemOptions.isNotEmpty()) {
                cartItemOptions.forEach {
                    orderExtraDetail.add(ExtraItemOrderModel().apply {
                        name = it.name ?: ""
                        price = it.price ?: "0"
                        quantity = it.quantity?.toString() ?: "1"
                        isChecked = true
                        extra_option_id = it.id?.toString() ?: ""
                        category_id = it.categoryId ?: ""
                    })
                }
            }

            selectedProduct.extraOptions.forEach { option ->
//                val productExtras = db.productExtraOptionDao().getAllById(option.id ?: "0")
                val productExtras = option.menuItems
                println("productDetai/l -> productExtras -> $productExtras")
                val extras = ArrayList<MenuItemModel>()
                productExtras.forEach { extra ->
                    extras.add(
                        MenuItemModel().apply {
                            ID = extra.ID ?: ""
                            menuItemName = extra.menuItemName ?: ""
                            price = extra.price ?: ""
                            categoryId = extra.categoryId ?: ""

                            isChecked = cartItemOptions.any { it.id == extra.ID?.toInt() }
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

            _product.ID = product.ID ?: "0"
            _product.menu_id = selectedProduct.menu_id ?: "0"
            _product.menuItemName = product.attributes.menuItemName ?: ""
            _product.itemDescription = product.itemDescription ?: ""
            _product.price = product.price ?: "0"
            _product.image = product.image ?: ""
            _product.categoryId = product.categoryId ?: "0"
            _product.extraOptions.addAll(_productOptions.sortedBy { it.id.toString() })
//            _product.extraOptions.addAll(_productOptions)
            this@ProductDetailActivity.product = _product
            price = product.price?.toFloat() ?: 0F
            updatedPrice = product.price?.toFloat() ?: 0F

//            categoryID = category.id ?: ""
            val img = _product.image
            if (_product.categoryId == "20000233") {
                this@ProductDetailActivity.runOnUiThread {
                    hideView(mBinding.etInstructions)
                }
            }

            this@ProductDetailActivity.runOnUiThread {
//                mBinding.tvCategory.text = category.name ?: ""
//                mBinding.tvProductDescription.text = _product.itemDescription
//                mBinding.tvProductName.text = _product.menuItemName
                mBinding.tvAddToBag.text = "Add to Bag $" + _product.price
//                mBinding.tvQuantity.text = "1"
//                Glide.with(this@ProductDetailActivity)
//                    .load(img)
//                    .into(mBinding.ivProductBanner)
            }

            if (cartItem != null) {
                if (tag == IntentParams.EDITORDER) {
                    this@ProductDetailActivity.runOnUiThread {
                        mBinding.tvQuantity.text = (cartItem?.quantity ?: 1).toString()
                        itemQuantity = cartItem?.quantity ?: 1
                        mBinding.tvAddToBag.text = "Continue $" + (cartItem?.totalPrice ?: "0.00")
                        extraOptionPrice =
                            ((cartItem?.totalPrice
                                ?: "0.0").toFloat() - (_product.price.toFloat() * itemQuantity)) / itemQuantity
                        updatedPrice = (cartItem?.totalPrice ?: "0.0").toFloat()
                        mBinding.etInstructions.setText(cartItem?.notes ?: "")
                    }
                }
            }

            this@ProductDetailActivity.runOnUiThread {
                initIngredientsAdapter()
            }
        }
    }

    private fun initScroller() {
        mBinding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            isViewVisible(
                view = mBinding.dummy
            )
        })
    }

    private fun isViewVisible(view: View) {
        val scrollBounds = Rect(view.left, view.top, view.right, view.bottom)
        mBinding.nestedScrollView.getDrawingRect(scrollBounds)
        val top = view.y
        val bottom = top + view.height
        if (scrollBounds.top < top && scrollBounds.bottom > bottom) {
            Log.d("State", "true")
        } else {
            Log.d("State", "false")
        }
    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

        mBinding.ivHeart.setOnClickListener {
            if (isFavourite) {
                mBinding.ivHeart.setImageResource(R.drawable.ic_un_favourite)
            } else {
                mBinding.ivHeart.setImageResource(R.drawable.ic_favourite)
            }
            isFavourite = !isFavourite
        }
        mBinding.ivPlus.setOnClickListener {
            ++itemQuantity
            mBinding.tvQuantity.text = "$itemQuantity"
//            updateQuantity(extraOptionPrice.toString())
            updateFooterPrice()
            makeVibrate()
        }


        mBinding.ivMinus.setOnClickListener {
            if (itemQuantity > 1) {
                --itemQuantity
                mBinding.tvQuantity.text = "$itemQuantity"
//                updateQuantity(extraOptionPrice.toString())
                updateFooterPrice()
            }
        }


        mBinding.cvAddToBag.setOnClickListener {
            var verified: Boolean
            product.extraOptions.forEach { extraOption ->
                if (extraOption.forceMaxSelection.toInt() > 0) {
                    verified = extraOption.menuItems.any { it.isChecked }
                    if (!verified) {
                        showWarningToast("Please select mandatory options.")
                        return@setOnClickListener
                    }
                }
            }

            if (shouldBeAddedIntoCart()) {
                canBeAddedIntoCart()
            } else {
                showWarningToast(message = "Item with $0.0 can't be added into cart.")
            }
        }
    }

    private fun shouldBeAddedIntoCart(): Boolean {
        var subPrice = 0f
        for (option in product.extraOptions) {
            for (item in option.menuItems) {
                if (item.isChecked) {
                    val desiredQuantity = if (item.quantity.isEmpty()) {
                        1
                    } else {
                        item.quantity.toInt()
                    }
                    val itemPrice: Float = if (item.price.isEmpty()) {
                        0.00f
                    } else {
                        item.price.toFloat()
                    }
                    Log.d("Sub Price ", (itemPrice * desiredQuantity.toFloat()).toString())
                    subPrice += (itemPrice * desiredQuantity.toFloat())
                }
            }
        }

        return subPrice + price > 0.0f
    }


    private fun canBeAddedIntoCart() {
        GlobalScope.launch {
            fetchCart()
        }
    }

    private suspend fun fetchCart() {
        val appDb = AppDatabase(this@ProductDetailActivity)
//        val cartItems = appDb.cartDao().getAllWithOUtTerminal()
        val cartItems = appDb.cartDao().getAll()
        withContext(Dispatchers.Main) {
            if (cartItems.isEmpty()) {
                addItemIntoCart()
            } else {
                val currentItemsKitchenTerminalID =
                    AppPreferenceManager.getValue(PrefFlags.CURRENT_ITEMS_KITCHEN_ID)
                if (currentItemsKitchenTerminalID.isNotEmpty()) {
//                    if (currentItemsKitchenTerminalID == currentProductKitchenTerminalId) {
                    addItemIntoCart()
//                    }
//                    else {
//                        errorDialogue(
//                            message = "You can't have items of 2 different stores in your bag. Please empty cart first to add this item into your bag.",
//                            context = this@ProductDetailActivity,
//                            title = "Error"
//                        )
//                    }
                } else {
                    addItemIntoCart()
                }
            }
        }
    }

    private fun addItemIntoCart() {
        makeVibrate()
        val db = AppDatabase(this)
        GlobalScope.launch {
            if (tag == IntentParams.PLACEORDER) {
                val itemAlreadyInCart = db.cartDao().getByName(product.menuItemName)
                if (itemAlreadyInCart != null) {
                    val cartItemOptions = db.cartExtraOptionDao()
                        .getAllById(itemAlreadyInCart.pKey)
                    if (cartItemOptions.isEmpty() && orderExtraDetail.isEmpty()) {
                        println("item matched")
                        if ((itemAlreadyInCart.notes?.trim()
                                ?: "") == (mBinding.etInstructions.text?.toString()?.trim() ?: "")
                        )
                            updateCartItemQuantity(db, itemAlreadyInCart)
                        else
                            addToCart(db)
                    } else {
                        val itemOptionsMatched = cartItemOptions.any {
                            orderExtraDetail.any { item ->
                                item.name == it.name && item.quantity.toInt() == it.quantity
                            }
                        }
                        if (itemOptionsMatched) {
                            println("item matched")
                            if ((itemAlreadyInCart.notes?.trim()
                                    ?: "") == (mBinding.etInstructions.text?.toString()?.trim()
                                    ?: "")
                            )
                                updateCartItemQuantity(db, itemAlreadyInCart)
                            else
                                addToCart(db)
//                            updateCartItemQuantity(db, itemAlreadyInCart)
                        } else {
                            println("item not matched")
                            addToCart(db)
                        }
                    }
                } else {
                    addToCart(db)
                }
            } else {
                val cartItem = db.cartDao().getItem(cartItemPKey)
                val cartItemOptions = db.cartExtraOptionDao()
                    .getAllById(cartItem?.pKey ?: 0)
                db.cartDao().update(
                    Cart(
                        cartItem?.pKey ?: 0,
                        product.ID.toInt(),
                        product.menuItemName,
                        product.price,
                        selectedProduct.menu_category_id.toString(),
                        itemQuantity,
                        product.image,
                        updatedPrice.toString(),
                        MyApplication.selectedOrderType.toString(),
                        mBinding.etInstructions.text.toString().trim()
                    )
                )
                orderExtraDetail.forEach { option ->
                    val extraOptions =
                        cartItemOptions.filter { it.id == option.extra_option_id.toInt() }
                    if (extraOptions.isNotEmpty()) {
                        db.cartExtraOptionDao().update(
                            CartExtraOption(
                                extraOptions.first().pKey,
                                option.extra_option_id.toInt(),
                                option.name,
                                option.price,
                                option.quantity.toInt(),
                                option.category_id,
                                extraOptions.first().cartOptionId
                            )
                        )
                    } else {
                        db.cartExtraOptionDao().insert(
                            CartExtraOption(
                                0,
                                option.extra_option_id.toInt(),
                                option.name,
                                option.price,
                                option.quantity.toInt(),
                                option.category_id,
                                cartItem?.pKey ?: 0
                            )
                        )
                    }
                }
                cartItemOptions.forEach { option ->
                    val updatedOptions =
                        orderExtraDetail.filter { it.extra_option_id.toInt() == option.id }
                    if (updatedOptions.isNullOrEmpty()) {
                        db.cartExtraOptionDao().delete(
                            CartExtraOption(
                                option.pKey,
                                option.id,
                                option.name,
                                option.price,
                                option.quantity,
                                option.categoryId,
                                option.cartOptionId
                            )
                        )
                    }
                }
            }
        }
        finish()
    }


    private fun addToCart(db: AppDatabase) {
        //rowId -> auto generated primary key
//        val rawCategoryId = db.productDao().get(product.ID).categoryId.toString()
        val rawCategoryId = categoryID
        val rowId = db.cartDao().insert(
            Cart(
                0,
                product.menu_id.toInt(),
                product.menuItemName,
                product.price,
                rawCategoryId,
                itemQuantity,
                product.image,
                updatedPrice.toString(),
                MyApplication.selectedOrderType.toString(),
                if (mBinding.etInstructions.text.isNullOrEmpty()) "" else mBinding.etInstructions.text.toString()
                    .trim()
            )
        )
        orderExtraDetail.forEach { option ->
            db.cartExtraOptionDao().insert(
                CartExtraOption(
                    0,
                    option.extra_option_id.toInt(),
                    option.name,
                    option.price,
                    option.quantity.toInt(),
                    option.category_id,
                    rowId.toInt()
                )
            )
        }
        makeVibrate()

        AppPreferenceManager.setValue(
            PrefFlags.CURRENT_ITEMS_KITCHEN_ID,
            currentProductKitchenTerminalId
        )

    }

    private fun updateCartItemQuantity(db: AppDatabase, item: Cart) {
        db.cartDao().update(
            Cart(
                item.pKey,
                item.id,
                item.name,
                item.price,
                item.categoryId,
                item.quantity!! + itemQuantity,
                item.img,
                (updatedPrice / item.quantity * (item.quantity + itemQuantity)).toString(),
                item.orderType,
                if (mBinding.etInstructions.text.isNullOrEmpty()) "" else mBinding.etInstructions.text.toString()
                    .trim()
            )
        )
        makeVibrate()
        AppPreferenceManager.setValue(
            PrefFlags.CURRENT_ITEMS_KITCHEN_ID,
            currentProductKitchenTerminalId
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantity(extraOptionPrice: String) {
        mBinding.tvQuantity.text = "$itemQuantity"
        updatedPrice = (price + extraOptionPrice.toFloat()) * itemQuantity
        if (tag == IntentParams.PLACEORDER)
            mBinding.tvAddToBag.text = "Add to Bag $" + String.format("%.2f", updatedPrice)
        else
            mBinding.tvAddToBag.text = "Continue $" + String.format("%.2f", updatedPrice)

        updateFooterPrice()
    }

    @SuppressLint("SetTextI18n")
    private fun updateFooterPrice() {
        var subPrice = 0f
        for (option in product.extraOptions) {
            for (item in option.menuItems) {
                if (item.isChecked) {
                    val desiredQuantity = if (item.quantity.isEmpty()) {
                        1
                    } else {
                        item.quantity.toInt()
                    }
                    val itemPrice: Float = if (item.price.isEmpty()) {
                        0.00f
                    } else {
                        item.price.toFloat()
                    }
                    Log.d("Sub Price ", (itemPrice * desiredQuantity.toFloat()).toString())
                    subPrice += (itemPrice * desiredQuantity.toFloat())
                }
            }
        }

        mBinding.tvAddToBag.text =
            "Add to Bag $${String.format("%.2f", ((subPrice + price) * itemQuantity))}"
    }

    private fun initIngredientsAdapter() {
        var isMenuItemAvailable = false
        product.extraOptions.forEach {
            it.showSubMenu = false
            if (it.menuItems.isNotEmpty())
                if (it.forceMaxSelection.toInt() > 0)
                    it.apply {
                        // menuItems[0].isChecked = true
                        menuItems[0].isChecked = false
                        menuItems[0].quantity = "0"
                    }

            if (it.menuItems.isNotEmpty()) {
                isMenuItemAvailable = true
            }
        }

        mBinding.rvIngredients.itemAnimator = null
//        (mBinding.rvIngredients.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        ingredientsAdapter =
            MealPrepSectionAdapter(
                mealPrepSections = product.extraOptions,
                mealPrepListener = this
            )
        mBinding.rvIngredients.adapter = ingredientsAdapter

        if (product.extraOptions.isEmpty()) {
            if (categoryID == "20000233") // Show it only for Pre-Build Meal Category
                showView(view = mBinding.tvNoOptionsAvailable)
            else
                hideView(view = mBinding.tvNoOptionsAvailable)
        } else {
            if (isMenuItemAvailable)
                hideView(view = mBinding.tvNoOptionsAvailable)
            else
                showView(view = mBinding.tvNoOptionsAvailable)
        }

        updateFooterPrice()
    }

    override fun itemClicked(position: Int) {
        // Only One Selection
        showToast("item clicked")
        val alreadySelectedIndex = ingredientsList.indexOfFirst { it.isChecked }
        if (alreadySelectedIndex != -1) {
            ingredientsList[alreadySelectedIndex].isChecked = false
            ingredientsAdapter.notifyItemChanged(alreadySelectedIndex)
        }
        ingredientsList[position].isChecked = !ingredientsList[position].isChecked
        ingredientsAdapter.notifyItemChanged(position)
    }

    override fun brandTapped(position: Int, title: String) {
        showToast("brandTapped")
    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {}

    override fun deleteLocation(position: Int, addressId: Int) {
    }

    override fun headerTapped(position: Int) {
        product.extraOptions[position].showSubMenu = !product.extraOptions[position].showSubMenu
        ingredientsAdapter.notifyItemChanged(position)
    }

    override fun cantSelectMore(sectionIndex: Int, subSectionIndex: Int) {

        val lastCheckedIndex =
            product.extraOptions[sectionIndex].menuItems.indexOfFirst { it.isChecked }
        if (lastCheckedIndex != -1) {
            product.extraOptions[sectionIndex].menuItems[lastCheckedIndex].apply {
                isChecked = false
                quantity = "0"
            }
            ingredientsAdapter.notifyItemChanged(sectionIndex)
        }
        if (lastCheckedIndex != subSectionIndex) {
            product.extraOptions[sectionIndex].menuItems[subSectionIndex].apply {
                isChecked = true
                quantity = "1"
            }
            ingredientsAdapter.notifyItemChanged(sectionIndex)
        }
        updateFooterPrice()
    }

    override fun itemTapped(
        position: Int,
        itemId: String,
        categoryHeaderId: Int,
        itemDetail: MenuItemModel
    ) {
        product.extraOptions[categoryHeaderId].menuItems[position].isChecked =
            !product.extraOptions[categoryHeaderId].menuItems[position].isChecked

        val _isChecked = product.extraOptions[categoryHeaderId].menuItems[position].isChecked
        if (_isChecked) {
            val quantityInInt: Int =
                product.extraOptions[categoryHeaderId].menuItems[position].quantity.toInt() + 1
            product.extraOptions[categoryHeaderId].menuItems[position].quantity =
                quantityInInt.toString()
        } else {
//            if (product.extraOptions[categoryHeaderId].menuItems[position].quantity.toInt() > 1) {
//                originalPrice =
//                    (product.extraOptions[categoryHeaderId].menuItems[position].price).toFloat()
//                val singlePrice =
//                    originalPrice / product.extraOptions[categoryHeaderId].menuItems[position].quantity.toInt()
//                product.extraOptions[categoryHeaderId].menuItems[position].price =
//                    singlePrice.toString()
//            }

            product.extraOptions[categoryHeaderId].menuItems[position].quantity = "0"
        }
        //visibility of quantity changer.
        if (product.extraOptions[categoryHeaderId].forceMaxSelection.toInt() != 1 && product.extraOptions[categoryHeaderId].maxSelection.toInt() != 1) {
            product.extraOptions[categoryHeaderId].menuItems[position].showQuantityChanger =
                _isChecked
        }
        ingredientsAdapter.notifyItemChanged(categoryHeaderId)

        val menuItem =
            product.extraOptions[categoryHeaderId].menuItems[position]
        val selectedExtraPrice = menuItem.price.toFloat()
        if (menuItem.isChecked) {
            extraOptionPrice += selectedExtraPrice
            orderExtraDetail.add(ExtraItemOrderModel().apply {
                name = itemDetail.menuItemName
                price = itemDetail.price
                quantity = "1"
                isChecked = true
                extra_option_id = itemDetail.ID
                category_id =
                    product.extraOptions[categoryHeaderId].categoryId
            })
        } else {
            extraOptionPrice -= selectedExtraPrice
            orderExtraDetail.removeIf { it.extra_option_id == itemDetail.ID }
        }
//        updateQuantity(extraOptionPrice.toString())
        updateFooterPrice()
    }

    override fun plusTapped(sectionIndex: Int, subSectionIndex: Int) {
        val quantityInInt: Int =
            product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity.toInt() + 1
        product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity =
            quantityInInt.toString()

//        var priceInDouble = 0.0F
//        if (quantityInInt == 2)
//            originalPrice =
//                (product.extraOptions[sectionIndex].menuItems[subSectionIndex].price).toFloat()
//
//        if (originalPrice > 0.0) {
//            priceInDouble = (originalPrice * quantityInInt.toFloat())
//        }

//        product.extraOptions[sectionIndex].menuItems[subSectionIndex].price =
//            priceInDouble.toString()
        if (product.extraOptions[sectionIndex].forceMaxSelection.toInt() != 1 && product.extraOptions[sectionIndex].maxSelection.toInt() != 1) {
            product.extraOptions[sectionIndex].menuItems[subSectionIndex].showQuantityChanger =
                true
        }
        ingredientsAdapter.notifyItemChanged(sectionIndex)
        updateFooterPrice()
        makeVibrate()
    }

    override fun minusTapped(sectionIndex: Int, subSectionIndex: Int) {

        if (product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity.isNotEmpty()) {
            if (product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity.toInt() > 1) {
                product.extraOptions[sectionIndex].menuItems[subSectionIndex].apply {
                    quantity = (quantity.toInt() - 1).toString()
                    isChecked = true
                    if (product.extraOptions[sectionIndex].forceMaxSelection.toInt() != 1 && product.extraOptions[sectionIndex].maxSelection.toInt() != 1) {
                        product.extraOptions[sectionIndex].menuItems[subSectionIndex].showQuantityChanger =
                            true
                    }
                }
            } else {
                product.extraOptions[sectionIndex].menuItems[subSectionIndex].apply {
                    isChecked = false
                    quantity = "0"
                    if (product.extraOptions[sectionIndex].forceMaxSelection.toInt() != 1 && product.extraOptions[sectionIndex].maxSelection.toInt() != 1) {
                        product.extraOptions[sectionIndex].menuItems[subSectionIndex].showQuantityChanger =
                            false
                    }
                }
            }
        }
        ingredientsAdapter.notifyItemChanged(sectionIndex)
        updateFooterPrice()
//        originalPrice =
//            (product.extraOptions[sectionIndex].menuItems[subSectionIndex].price).toFloat()
//        val singlePrice =
//            originalPrice / product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity.toInt()
//        if (product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity.toInt() > 1) {
//            val quantityInInt: Int =
//                product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity.toInt() - 1
//            product.extraOptions[sectionIndex].menuItems[subSectionIndex].quantity =
//                quantityInInt.toString()
//
//            var priceInDouble = 0.0F
//            if (originalPrice > 0.0) {
//                priceInDouble = (singlePrice * quantityInInt.toFloat())
//            }
//
//            product.extraOptions[sectionIndex].menuItems[subSectionIndex].price =
//                priceInDouble.toString()
//            ingredientsAdapter.notifyItemChanged(sectionIndex)
//        }
    }

    override fun showToast(context: String) {
        showWarningToast(context)
    }

    override fun orderCompleted(position: Int) {

    }

    private fun getMenuItemByKitchenAndCategoryID() {
        showLoading()
        viewModel.getMenuItemByKitchenAndCategoryID(
            AppPreferenceManager.getSelectedKitchenFromSharedPreferences()?.terminalId ?: "-1",
            categoryID
        )
    }


    private fun getMenuOptionsByProductId(productId: String) {
        viewModel.getMenuOptionsByProductId(productId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeCPApiResponse() {
        if (::viewModel.isInitialized) {
            viewModel.getCPApiStatus().observe(this@ProductDetailActivity) { it ->
                when (it.enum) {
                    ApplicationEnum.CP_GET_MENU_OPTION_BY_PRODUCT_ID_SUCCESS -> {
                        hideLoading()
                        val model = Gson().fromJson(it.data.toString(), CPMenuItemOptions::class.java)

                        if (model.result.isEmpty()) {
                            mBinding.tvNoOptionsAvailable.visibility = View.VISIBLE
                            mBinding.rvIngredients.visibility = View.INVISIBLE
                            geProductFromDB(selectedProduct.ID)
                        } else {
                            mBinding.tvNoOptionsAvailable.visibility = View.INVISIBLE
                            mBinding.rvIngredients.visibility = View.VISIBLE

                            for (i in model.result.indices) {
                                product.extraOptions.add(ExtraOptionsModel().apply {
                                    this.name = model.result[i].name

                                    if (selectedProduct.extraOptions.any { it.cat_id == model.result[i].cat_id }) {
                                        this.maxSelection = selectedProduct.extraOptions.filter { it.cat_id == model.result[i].cat_id }[0].maxSelection
                                        this.minSelection = selectedProduct.extraOptions.filter { it.cat_id == model.result[i].cat_id }[0].minSelection
                                        this.forceMaxSelection = selectedProduct.extraOptions.filter { it.cat_id == model.result[i].cat_id }[0].forceMaxSelection
                                    }


                                    for (j in model.result[i].options.indices) {
                                        this.menuItems.add(MenuItemModel().apply {
                                            this.menuItemName = model.result[i].options[j].attributes.menuItemName
                                            this.price = model.result[i].options[j].attributes.price
                                            this.ID = model.result[i].options[j].attributes.ID
                                        })
                                    }

                                })
                            }

                            selectedProduct.extraOptions = product.extraOptions
                            initIngredientsAdapter()
                            geProductFromDB(selectedProduct.ID)
                        }
                    }
                    ApplicationEnum.CP_GET_MENU_OPTION_BY_PRODUCT_ID_ERROR -> {
                        hideLoading()
//                        showShortToast(it.message.toString())
                        mBinding.tvNoOptionsAvailable.visibility = View.VISIBLE
                        mBinding.rvIngredients.visibility = View.INVISIBLE
                        /*In case if the menu item is null*/

//                        if (it.message.toString().equals("Validation Error.", true))
                        geProductFromDB(selectedProduct.ID)
                    }

                    ApplicationEnum.CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID_SUCCESS -> {

                        val model = Gson().fromJson(it.data.toString(), CPMenuItemModel::class.java)

                        for (i in model.result.menu_items.indices) {
                            if (selectedProductId == model.result.menu_items[i].ID) {
                                val m = model.result.menu_items[i]
                                selectedProduct = MenuItemModel().apply {
                                    this.image = image_base_url + m.image_url
                                    this.image_url = m.image_url
                                    this.ID = m.ID
                                    this.terminalId = ID
                                    this.attributes = m.attributes
                                    this.description = m.attributes.itemDescription
                                    this.itemDescription = m.attributes.itemDescription
                                    this.kitchen_id = m.kitchen_id
                                    this.menuItemName = m.attributes.name
                                    this.menu_category_id = m.menu_category_id
                                    this.menu_id = m.menu_id
                                    this.price = m.attributes.price
                                    this.category_name = model.result.menu_category.name

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
                                                        this.forceMaxSelection = eo.forceMaxSelection
                                                        this.cat_id = eo.masterCID

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
                                                    this.forceMaxSelection = eo.forceMaxSelection
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


                                }
                            }
                        }

                        if (this::selectedProduct.isInitialized)
                            updateViews()
                        else
                            hideLoading()

                    }
                    ApplicationEnum.CP_GET_MENU_ITEMS_BY_KITCHEN_AND_CATEGORY_ID_ERROR -> {
                        hideLoading()
                        showShortToast(it.message.toString())
                    }
                }
            }
        }
    }

    private fun updateViews() {
        Glide.with(this@ProductDetailActivity)
            .load(selectedProduct.image_base_url + selectedProduct.image_url)
            .into(mBinding.ivProductBanner)

        mBinding.tvCategory.text = selectedProduct.category_name
        mBinding.tvProductName.text = selectedProduct.menuItemName
        mBinding.tvProductDescription.text = selectedProduct.description
        mBinding.tvAddToBag.text = "Add to Bag $" + selectedProduct.price
        mBinding.tvQuantity.text = "1"
        price = selectedProduct.price?.toFloat() ?: 0F
        updatedPrice = selectedProduct.price?.toFloat() ?: 0F

        getMenuOptionsByProductId(selectedProduct.ID ?: "-1")

    }

}