package com.codingpixel.dedicatekitchen.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.OrdersAdapter
import com.codingpixel.dedicatekitchen.databinding.SingleOrderItemBinding
import com.codingpixel.dedicatekitchen.helpers.ApiUrls
import com.codingpixel.dedicatekitchen.helpers.CommonMethods
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.models.Order


class OrderVH(binding: SingleOrderItemBinding, adapter: OrdersAdapter) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding: SingleOrderItemBinding = binding
    private val mAdapter: OrdersAdapter = adapter
    private val maxTimeForCancelOrder = 3 * 60 // in seconds
    private val maxOrderTime = 60 * 60 // in seconds
    private var maxTimeForPreparation = 3 * 60 * 60 // in seconds

    @SuppressLint("SetTextI18n")
    fun bind(source: Order, position: Int) {
        mBinding.source = source
        mBinding.position = position
        mBinding.adapter = mAdapter

//        mBinding.tvOrderDateTime.text =
//            CommonMethods.dateFormatter(source.created_at) + Constants.DOT + CommonMethods.timeFormatter(
//                source.created_at
//            )
        mBinding.tvOrderDateTime.text = source.deliveryDateTime

//        if (source.order_type == "mp_delivery") {
//            val prefFixDate = CommonMethods.orderDateOnlyFormatter(source.pickup_date_time)
//            val dateTimeSplitArr = source.pickup_date_time.split(" ")
//            if (dateTimeSplitArr.size >= 2) {
//                val dateStr = dateTimeSplitArr[0]
//                val timeStr = dateTimeSplitArr[1]
//                val timeArr = timeStr.split(":")
//                if (timeArr.isNotEmpty()) {
//                    val startingHour = timeArr[0].toInt()
//                    if (startingHour == 12) {
//                        mBinding.tvOrderDateTime.text =
//                            prefFixDate + " " + Constants.DOT + " 10 AM - 12 PM"
//                    } else {
//                        mBinding.tvOrderDateTime.text =
//                            prefFixDate + " " + Constants.DOT + " ${(startingHour - 2)} PM - $startingHour PM"
//                    }
//                } else {
//                    mBinding.tvOrderDateTime.text =
//                        prefFixDate + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
//                            source.pickup_date_time
//                        )
//                }
//            } else {
//                mBinding.tvOrderDateTime.text =
//                    CommonMethods.orderDateOnlyFormatter(source.pickup_date_time) + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
//                        source.pickup_date_time
//                    )
//            }
//
//
//        }
//        else {
//
//            mBinding.tvOrderDateTime.text =
//                CommonMethods.orderDateOnlyFormatter(source.pickup_date_time) + " " + Constants.DOT + " " + CommonMethods.orderTimeFormatter(
//                    source.pickup_date_time
//                )
//        }


//        mBinding.tvOrderDateTime.text =
//            CommonMethods.orderDateFormatter(source.pickup_date_time)


//        if (source.orderType == "past") {
//
//        }

//        maxTimeForPreparation = if(source.pickup_time.contains("hours"))
//            source.pickup_time.split("hours")[0].trim().toInt() * 60 * 60
//        else
//            source.pickup_time.toInt() * 60 * 60
        Glide.with(mBinding.root.context)
            .load(
                if (source.orderDetail().items_data.isEmpty()) ApiUrls.DEFAULT_PRODUCT_IMAGE_URL
                else
                    source.orderDetail().items_data[0].img
            )
            .placeholder(R.drawable.img_dk_placeholder).error(R.drawable.gray_rounded_rectangle)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding.ivOrder)

        // TODO: Timer Removal and Cancel Order Removal

//        if (source.orderType == "active") {
//            val timeDiff = CommonMethods.timeDifference(source.created_at)
//            when {
//                timeDiff <= maxTimeForCancelOrder -> {
//                    mBinding.btnCancelOrder.visibility = View.VISIBLE
//                    mBinding.tvCancelOrderCountdown.visibility = View.VISIBLE
//                    mBinding.tvOrderCancelled.visibility = View.GONE
//                    mBinding.tvOrderTimeLeft.visibility = View.GONE
//                    mBinding.tvTitle.visibility = View.GONE
//
//                    val timer =
//                        object : CountDownTimer((maxTimeForCancelOrder - timeDiff) * 1000, 1000) {
//                            override fun onTick(millisUntilFinished: Long) {
//                                //3 minute counter
//                                val remainingSeconds = millisUntilFinished / 1000
//                                Log.d("remainingSeconds", remainingSeconds.toString())
//                                if (remainingSeconds < 60) {
//                                    val reminderFormat =
//                                        if (remainingSeconds < 10) "0".plus(remainingSeconds.toString()) else remainingSeconds.toString()
//                                    mBinding.tvCancelOrderCountdown.text =
//                                        reminderFormat + "s left for cancel"
//                                } else {
//                                    val remainingMinutes = remainingSeconds / 60
//                                    val reminder = (remainingSeconds % 60)
//                                    val reminderFormat =
//                                        if (reminder < 10) "0".plus(reminder.toString()) else reminder.toString()
//                                    mBinding.tvCancelOrderCountdown.text =
//                                        "0$remainingMinutes:$reminderFormat left for cancel"
//                                }
//                            }
//
//                            override fun onFinish() {
//                                mBinding.btnCancelOrder.visibility = View.GONE
//                                mBinding.tvCancelOrderCountdown.visibility = View.GONE
//                                mBinding.tvOrderCancelled.visibility = View.GONE
//                                mBinding.tvOrderTimeLeft.visibility = View.VISIBLE
//
////                                mAdapter.refreshRow(position = position)
//
//                            }
//                        }
//                    timer.start()
//                }
//
//                timeDiff < maxTimeForPreparation -> {
//                    mBinding.btnCancelOrder.visibility = View.GONE
//                    mBinding.tvCancelOrderCountdown.visibility = View.GONE
//                    mBinding.tvOrderCancelled.visibility = View.GONE
//                    mBinding.tvOrderTimeLeft.visibility = View.VISIBLE
//                    mBinding.tvTitle.visibility = View.VISIBLE
//                    mBinding.ivInfo.visibility = View.VISIBLE
//
//                    val timer =
//                        object : CountDownTimer((maxTimeForPreparation - timeDiff) * 1000, 1000) {
//                            override fun onTick(millisUntilFinished: Long) {
//                                val remainingSeconds = (millisUntilFinished / 1000).toInt() % 60
//                                val remainingMinutes = (millisUntilFinished / (1000 * 60) % 60)
//                                val remainingHours = (millisUntilFinished / (1000 * 60 * 60) % 24)
//
//                                Log.d("remainingSeconds", remainingSeconds.toString())
//                                mBinding.tvOrderTimeLeft.text =
//                                    "${remainingHours}h ${remainingMinutes}m ${remainingSeconds}s left"
//
//                            }
//
//                            override fun onFinish() {
//                                mAdapter.orderCompleted(position = position)
//                            }
//                        }
//                    timer.start()
//                }
//
//                else -> {
//
//                }
//
//            }
//        } else {
//            mBinding.btnCancelOrder.visibility = View.GONE
//            mBinding.tvCancelOrderCountdown.visibility = View.GONE
//
//            if (source.payment_status == "cancel_by_guest") {
//                mBinding.tvOrderCancelled.visibility = View.VISIBLE
//            } else {
//                mBinding.tvOrderCancelled.visibility = View.GONE
//            }
//        }


//        mBinding.tvOrderDateTime.text = CommonMethods.timeDifference(source.created_at).toString()
//        when (source.orderStatus) {
//            Constants.ORDER_STATUS_IN_PROGRESS -> {
//                setDrawableTiniOfTextView(
//                    textView = mBinding.tvOrderStatus, color = ContextCompat.getColor(
//                        mBinding.root.context,
//                        R.color.orderInProgressColor
//                    ),
//                    context = mBinding.root.context
//                )
//            }
//
//            Constants.ORDER_STATUS_CANCELLED -> {
//                setDrawableTiniOfTextView(
//                    textView = mBinding.tvOrderStatus, color = ContextCompat.getColor(
//                        mBinding.root.context,
//                        R.color.orderCancelledColor
//                    ),
//                    context = mBinding.root.context
//                )
//            }
//
//            Constants.ORDER_STATUS_COMPLETED -> {
//                setDrawableTiniOfTextView(
//                    textView = mBinding.tvOrderStatus, color = ContextCompat.getColor(
//                        mBinding.root.context,
//                        R.color.orderCompletedColor
//                    ),
//                    context = mBinding.root.context
//                )
//            }
//
//            else -> {
//                setDrawableTiniOfTextView(
//                    textView = mBinding.tvOrderStatus, color = ContextCompat.getColor(
//                        mBinding.root.context,
//                        R.color.orderInProgressColor
//                    ),
//                    context = mBinding.root.context
//                )
//            }
//
//        }

        mBinding.tvOrderSubTitle.text = if (source.orderInfo.items_data.isNotEmpty())
            source.orderInfo.items_data[0].options.joinToString { it.name }
        else
            "No Items Available!"
    }

    private fun setDrawableTiniOfTextView(
        textView: AppCompatTextView,
        color: Int,
        context: Context
    ) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(
                    color, PorterDuff.Mode.SRC_IN
                )
            }
        }
    }
}