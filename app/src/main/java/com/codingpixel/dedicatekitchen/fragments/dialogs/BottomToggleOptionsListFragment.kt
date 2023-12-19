package com.codingpixel.dedicatekitchen.fragments.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.adapters.local.ToggleOptionsAdapter
import com.codingpixel.dedicatekitchen.base.MyBottomSheetBaseFragment
import com.codingpixel.dedicatekitchen.databinding.FragmentBottomToggleOptionsListBinding
import com.codingpixel.dedicatekitchen.helpers.IntentParams
import com.codingpixel.dedicatekitchen.interfaces.ItemClickListener
import com.codingpixel.dedicatekitchen.interfaces.ToggleSelectionListener
import com.codingpixel.dedicatekitchen.models.local.ToggleOption


/**
 * A simple [Fragment] subclass.
 * Use the [BottomToggleOptionsListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomToggleOptionsListFragment : MyBottomSheetBaseFragment(), ItemClickListener {

    private lateinit var mBinding: FragmentBottomToggleOptionsListBinding

    private var title: String = ""
    private var showTitle: Boolean = false

    private lateinit var toggleOptionsAdapter: ToggleOptionsAdapter
    private var toggleList = ArrayList<ToggleOption>()

    private var toggleSelectionListener: ToggleSelectionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(IntentParams.TITLE, "")
            showTitle = it.getBoolean(IntentParams.SHOW_TITLE, false)
            toggleList =
                it.getSerializable(IntentParams.TOGGLE_OPTIOSN_LIST) as ArrayList<ToggleOption>
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDialog()?.getWindow()?.getAttributes()?.windowAnimations = R.style.BottomDialogAnimation;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_toggle_options_list,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.tvTitle.text = title
        if (showTitle)
            showView(view = mBinding.tvTitle)
        else
            hideView(view = mBinding.tvTitle)

        toggleOptionsAdapter =
            ToggleOptionsAdapter(toggleOptions = toggleList, itemClickListener = this)
        mBinding.rvToggleOptions.adapter = toggleOptionsAdapter
    }

    fun setCallBack(callBack: ToggleSelectionListener?) {
        this.toggleSelectionListener = callBack
    }


    companion object {
        @JvmStatic
        fun newInstance(
            title: String = "",
            showTitle: Boolean = true,
            toggleOptions: ArrayList<ToggleOption>
        ) =
            BottomToggleOptionsListFragment().apply {
                arguments = Bundle().apply {
                    putString(IntentParams.TITLE, title)
                    putBoolean(IntentParams.SHOW_TITLE, showTitle)
                    putSerializable(IntentParams.TOGGLE_OPTIOSN_LIST, toggleOptions)
                }
            }
    }

    override fun itemClicked(position: Int) {
        toggleSelectionListener?.selectedItem(selectedOption = toggleList[position].optionName)
        dismiss()
    }

    override fun brandTapped(position: Int, title: String) {
        TODO("Not yet implemented")
    }

    override fun makeDefaultLocation(position: Int, addressId: Int) {

    }

    override fun deleteLocation(position: Int, addressId: Int) {
        TODO("Not yet implemented")
    }

    override fun orderCompleted(position: Int) {

    }
}