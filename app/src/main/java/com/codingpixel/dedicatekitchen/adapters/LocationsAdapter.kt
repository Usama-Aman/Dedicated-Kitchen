package com.codingpixel.dedicatekitchen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.databinding.SingleLocationItemBinding
import com.codingpixel.dedicatekitchen.databinding.SingleLocationToggleSmallItemBinding
import com.codingpixel.dedicatekitchen.databinding.SingleSelectAddressItemBinding
import com.codingpixel.dedicatekitchen.helpers.Constants
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.models.UserLocation
import com.codingpixel.dedicatekitchen.viewholders.ChooseLocationVH
import com.codingpixel.dedicatekitchen.viewholders.LocationSmallVH
import com.codingpixel.dedicatekitchen.viewholders.LocationVH

class LocationsAdapter(
    private val locations: ArrayList<UserLocation>,
    private val itemClickListener: ItemClickListener? = null,
    private val viewType: String = Constants.VIEW_TYPE_DISPLAY,
    private val itemType: String = Constants.VIEW_TPYE_LARGE
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (itemType) {
            Constants.VIEW_TPYE_LARGE -> {
                when (this.viewType) {
                    Constants.VIEW_TYPE_DISPLAY -> {
                        val binding: SingleLocationItemBinding =
                            DataBindingUtil.inflate(
                                inflater,
                                R.layout.single_location_item,
                                parent,
                                false
                            )
                        return LocationVH(binding = binding, adapter = this@LocationsAdapter)
                    }

                    Constants.VIEW_TYPE_TOGGLE -> {
                        val binding: SingleSelectAddressItemBinding =
                            DataBindingUtil.inflate(
                                inflater,
                                R.layout.single_select_address_item,
                                parent,
                                false
                            )
                        return ChooseLocationVH(binding = binding, adapter = this@LocationsAdapter)
                    }
                    else -> {
                        val binding: SingleLocationItemBinding =
                            DataBindingUtil.inflate(
                                inflater,
                                R.layout.single_location_item,
                                parent,
                                false
                            )
                        return LocationVH(binding = binding, adapter = this@LocationsAdapter)
                    }
                }
            }

            Constants.VIEW_TPYE_SMALL -> {
                val binding: SingleLocationToggleSmallItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.single_location_toggle_small_item,
                        parent,
                        false
                    )
                return LocationSmallVH(binding = binding, adapter = this@LocationsAdapter)
            }

            else -> {
                val binding: SingleLocationItemBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.single_location_item,
                        parent,
                        false
                    )
                return LocationVH(binding = binding, adapter = this@LocationsAdapter)
            }

        }


    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is LocationVH -> holder.bind(source = locations[position], position = position, viewAs = viewType)
            is LocationSmallVH -> holder.bind(source = locations[position], position = position , viewAs = viewType)
            is ChooseLocationVH -> holder.bind(source = locations[position], position = position , viewAs = viewType)
            else -> {
                (holder as LocationVH).bind(source = locations[position], position = position, viewAs = viewType)
            }
        }
    }

    fun itemTapped(position: Int) {
        itemClickListener?.deleteLocation(position = position , addressId = locations[position].id)
    }

    fun radioButtonTapped(position: Int)
    {
        itemClickListener?.makeDefaultLocation(position = position , addressId = locations[position].id)
    }
}