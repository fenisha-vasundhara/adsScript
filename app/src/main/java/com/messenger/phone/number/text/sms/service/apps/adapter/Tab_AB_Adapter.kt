package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.AddCategoryDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.TabRecyclerAbItemBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.TabAdapterClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import javax.inject.Inject


class Tab_AB_Adapter @Inject constructor() :
    RecyclerView.Adapter<Tab_AB_Adapter.TabAdapterViewHolder>() {

    lateinit var tabAdapterClick: TabAdapterClick

    var pos: Int = 0

    var isfisrtselected = true

    var startDrag: ((holder: TabAdapterViewHolder) -> Unit)? = null

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    class TabAdapterViewHolder(var binding: TabRecyclerAbItemBinding) : ViewHolder(binding.root)

    @Inject
    lateinit var addCategoryDialog: AddCategoryDialog

    var whattabselect = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabAdapterViewHolder {
        return TabAdapterViewHolder(
            TabRecyclerAbItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TabAdapterViewHolder, position: Int) {
        holder.itemView.context.setLocal()
        val context = holder.itemView.context
        val config = context.config

        with(holder) {
            fontSize10 = context.getTextSizeForeNormal10MS()
            fontSize13 = context.getTextSizeForeNormal13MS()
            fontSize18 = context.getTextSizeForeNormal18MS()
            fontSize8 = context.getTextSizeForeNormal8MS()
            fontSize15 = context.getTextSizeHometitleMS()

            binding.textsizechagefor10 = fontSize10
            binding.textsizechagefor13 = fontSize13 * 0.9f
            binding.textsizechagefor18 = fontSize18
            binding.textsizechagefor8 = fontSize8
            binding.textsizechagefor15 = fontSize15
        }


        with(holder.binding) {
            listdata = list[position]
        }
        holder.itemView.setOnClickListener {
//            whattabselect = list[position].catName.toString()


//            if (whattabselect == "Create Category") {
//                addCategoryDialog.show(
//                    (holder.itemView.context as AppCompatActivity).supportFragmentManager,
//                    "addCategoryDialog"
//                )
//            } else {
                list[position].catName.let { it1 ->
                    tabAdapterClick.onClickTab(it1, position, list)
                }
                pos = position
                notifyDataSetChanged()
//            }
        }

        if (list[position].catName == "otp") {

            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.OTPs)
            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_verified_user_rounded_selected)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_verified_user_rounded_unselected)
                    }
                }
            }

        } else if (list[position].catName == "All Messages") {

            holder.binding.number.text =
                "   " + holder.itemView.context.resources.getString(R.string.all_message) + " "

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_chat_bubble_rounded_selected)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_chat_bubble_rounded_unselected)
                    }
                }
            }


        } else if (list[position].catName == "Personal") {

            holder.binding.number.text =
                holder.itemView.context.resources.getString(R.string.Personal)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_favorite_rounded_selected)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_favorite_rounded_unselected)
                    }
                }
            }


        } else if (list[position].catName == "Transaction") {

            holder.binding.number.text =
                holder.itemView.context.resources.getString(R.string.Transaction)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_receipt_rounded_selected)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_receipt_rounded_unselected)
                    }
                }
            }


        } else if (list[position].catName == "Offers") {
            holder.binding.number.text =
                holder.itemView.context.resources.getString(R.string.Offers)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_offer_rounded_selected)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_offer_rounded_unselected)
                    }
                }
            }


        } else if (list[position].catName == "Create Category") {

            holder.binding.number.text =
                holder.itemView.context.resources.getString(R.string.Create_Category)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_create_folder_rounded_selected)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_create_folder_rounded_unselected)
                    }
                }
            }


        } else {

            holder.binding.number.text = list[position].catName

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_person_rounded_selected)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.home_material_ic_person_rounded_unselected)
                    }
                }
            }

        }

        val isSelected = whattabselect == list[position].catName
        val messageStyle = context.config.Message_full_App_Font_Style
        val selectedTabColor = getSelectedTabAccentColor(context, holder.binding.root)
        val unselectedTabColor = getUnselectedTabTextColor(context, messageStyle, holder.binding.root).adjustAlpha(0.8f)
        val tabStateColor = if (isSelected) selectedTabColor else unselectedTabColor

        if (whattabselect != "Create Category") {
            holder.binding.isselected = isSelected
            holder.binding.number.setTextColor(tabStateColor)
        }

        if (isSelected) {
            holder.binding.tabcard.background = createRoundedTabDrawable(
                context = context,
                fillColor = selectedTabColor.adjustAlpha(0.2f),
                strokeColor = selectedTabColor,
            )
        } else {

            val strokeColor: Int = if (messageStyle == "2") {
                unselectedTabColor
            } else {
                unselectedTabColor
            }

            holder.binding.tabcard.background =
                createRoundedTabDrawable(context = context, fillColor = Color.TRANSPARENT, strokeColor = strokeColor)
        }


        when (context.config.Message_full_App_Font_Style) {
            "1" -> {
                if (list[position].catName == "Create Category") {
                    holder.binding.img.visible()
                    holder.binding.ivDrag.gone()
                } else {
                    holder.binding.img.gone()
                    holder.binding.ivDrag.gone()
                }
            }

            "2" -> {
                if (list[position].catName != "All Messages") {
                    holder.binding.img.visible()
                    holder.binding.ivDrag.gone()
                } else {
                    holder.binding.img.gone()
                    holder.binding.ivDrag.gone()
                }
            }

            else -> {
                if (list[position].catName == "Create Category") {
                    holder.binding.img.visible()
                    holder.binding.ivDrag.gone()
                } else {
                    holder.binding.img.gone()
                    holder.binding.ivDrag.gone()
                }
            }
        }

        ImageViewCompat.setImageTintList(holder.binding.img, ColorStateList.valueOf(tabStateColor))

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var list = ArrayList<Category>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setinterface(abAdapterClick: TabAdapterClick) {
        this.tabAdapterClick = abAdapterClick
    }

    private fun getSelectedTabAccentColor(context: Context, anchor: View): Int {
        return context.getProperPrimaryColor()
    }

    private fun getUnselectedTabTextColor(context: Context, style: String, anchor: View): Int {
        return context.getProperSecondaryTextColor()
    }

    private fun createRoundedTabDrawable(context: Context, fillColor: Int, strokeColor: Int,strokeWidth : Int = context.resources.getDimension(com.intuit.sdp.R.dimen._1sdp).toInt()): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadius = context.resources.getDimension(com.intuit.sdp.R.dimen._5sdp)
            setColor(fillColor)
            setStroke(
                strokeWidth,
                strokeColor
            )
        }
    }

}
