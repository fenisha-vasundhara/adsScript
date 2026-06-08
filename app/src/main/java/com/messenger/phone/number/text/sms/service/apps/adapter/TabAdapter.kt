package com.messenger.phone.number.text.sms.service.apps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createCustomDrawable
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.invisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setTextSyle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.AddCategoryDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.TabRecyclerItemBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.TabAdapterClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class TabAdapter @Inject constructor() : RecyclerView.Adapter<TabAdapter.TabAdapterViewHolder>() {

    lateinit var tabAdapterClick: TabAdapterClick

    var pos: Int = 0

    var isfisrtselected = true

    var startDrag: ((holder: TabAdapterViewHolder) -> Unit)? = null

    class TabAdapterViewHolder(var binding: TabRecyclerItemBinding) : ViewHolder(binding.root)

    @Inject
    lateinit var addCategoryDialog: AddCategoryDialog

    var whattabselect = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabAdapterViewHolder {
        return TabAdapterViewHolder(TabRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TabAdapterViewHolder, position: Int) {
        holder.itemView.context.setLocal()
        val context = holder.itemView.context
        val config = context.config
        with(holder.binding) {
            listdata = list[position]
        }
        holder.itemView.setOnClickListener {
            whattabselect = list[position].catName.toString()
            if (whattabselect == "Create Category") {
                addCategoryDialog.show((holder.itemView.context as AppCompatActivity).supportFragmentManager, "addCategoryDialog")
            } else {
                list[position].catName.let { it1 ->
                    tabAdapterClick.onClickTab(it1, position, list)
                }
                pos = position
                notifyDataSetChanged()
            }
        }

        if (list[position].catName == "otp") {

            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.OTPs)
            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.otp_selected_ic)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.otp_unselected_ic)
                    }
                }
            }

        } else if (list[position].catName == "All Messages") {

            holder.binding.number.text = " "+ holder.itemView.context.resources.getString(R.string.Recent_tab)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.offersselectedic)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.offersunselectedic)
                    }
                }
            }


        } else if (list[position].catName == "Personal") {

            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Personal)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.persnal_selected_ic)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.persnal_unselected_ic)
                    }
                }
            }


        } else if (list[position].catName == "Transaction") {

            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Transaction)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.transaction_selected_ic)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.transaction_unselected_ic)
                    }
                }
            }


        } else if (list[position].catName == "Offers") {
            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Offers)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.offersselectedic)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.offersunselectedic)
                    }
                }
            }


        } else if (list[position].catName == "Create Category") {

            holder.binding.number.text = holder.itemView.context.resources.getString(R.string.Create_Category)

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.con_cat_creat_btn)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.con_cat_creat_btn)
                    }
                }
            }


        } else {

            holder.binding.number.text = list[position].catName

            when (context.config.Message_full_App_Font_Style) {
                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.img.setImageResource(R.drawable.custom_selected_ic)
                    } else {
                        holder.binding.img.setImageResource(R.drawable.custom_unselected_ic)
                    }
                }
            }

        }

        if (whattabselect != "Create Category") {

            holder.binding.isselected = whattabselect == list[position].catName

            // ab testing <-------------->

            when (context.config.Message_full_App_Font_Style) {
                "1" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.number.setTextColor(context.resources.getColor(R.color.appcolor))
                    } else {
                        holder.binding.number.setTextColor(context.resources.getColor(R.color.tab_unselected_color))
                    }
                }

                "2" -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.number.setTextColor(context.resources.getColor(R.color.white))
                    } else {
                        if (config.activeThemeSelection == 1) {
                            holder.binding.number.setTextColor(context.resources.getColor(R.color.commanfornewtabselectionstock1))
                        } else if (config.activeThemeSelection == 2) {
                            holder.binding.number.setTextColor(context.resources.getColor(R.color.commanfornewtabselectiontxt))
                        } else if (config.activeThemeSelection == 3) {
                            holder.binding.number.setTextColor(context.resources.getColor(R.color.commanfornewtabselectiontxt))
                        } else {
                            holder.binding.number.setTextColor(context.resources.getColor(R.color.commanfornewtabselectionstock1))
                        }
                    }
                }

                else -> {
                    if (whattabselect == list[position].catName) {
                        holder.binding.number.setTextColor(context.resources.getColor(R.color.appcolor))
                    } else {
                        holder.binding.number.setTextColor(context.resources.getColor(R.color.tab_unselected_color))
                    }
                }
            }

            // ab testing <-------------->

        }

//        android:textColor="@{isselected ? @color/appcolor : @color/tab_unselected_color}"


        if (whattabselect == list[position].catName) {
            CoroutineScope(Dispatchers.IO).launch {

                val background = context.createCustomDrawable(
                    cornerRadiusResId = com.intuit.sdp.R.dimen._20sdp,
                    solidColorResId =

                    when (context.config.Message_full_App_Font_Style) {
                        "1" -> {
                            if (config.activeThemeSelection == 1) {
                                R.color.tablayoutbg
                            } else if (config.activeThemeSelection == 2) {
                                R.color.toolbarcolor2
                            } else if (config.activeThemeSelection == 3) {
                                R.color.toolbarcolor3new
                            } else {
                                R.color.toolbarcolor2
                            }
                        }

                        "2" -> {
                            R.color.commanfornewtabselection
                        }

                        else -> {
                            if (config.activeThemeSelection == 1) {
                                R.color.tablayoutbg
                            } else if (config.activeThemeSelection == 2) {
                                R.color.toolbarcolor2
                            } else if (config.activeThemeSelection == 3) {
                                R.color.toolbarcolor3new
                            } else {
                                R.color.toolbarcolor2
                            }
                        }
                    },
                    strokeColorResId =
                    when (context.config.Message_full_App_Font_Style) {
                        "1" -> {
                            R.color.appcolor
                        }

                        "2" -> {
                            R.color.commanfornewtabselectionstock
                        }

                        else -> {
                            R.color.appcolor
                        }
                    },
                    strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
                )
                CoroutineScope(Dispatchers.Main).launch { holder.binding.tabcard.background = background }

            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {

                val background = context.createCustomDrawable(
                    cornerRadiusResId = com.intuit.sdp.R.dimen._20sdp,
                    solidColorResId =

                    when (context.config.Message_full_App_Font_Style) {
                        "1" -> {
                            when (config.activeThemeSelection) {
                                1 -> {
                                    R.color.tablayoutbg
                                }
                                2 -> {
                                    R.color.toolbarcolor2
                                }
                                3 -> {
                                    R.color.toolbarcolor3new
                                }
                                else -> {
                                    R.color.toolbarcolor2
                                }
                            }
                        }

                        "2" -> {
                            if (config.activeThemeSelection == 1) {
                                R.color.white
                            } else if (config.activeThemeSelection == 2) {
                                R.color.commanfornewtabselection1
                            } else if (config.activeThemeSelection == 3) {
                                R.color.commanfornewtabselection2
                            } else {
                                R.color.white
                            }
                        }

                        else -> {
                            if (config.activeThemeSelection == 1) {
                                R.color.tablayoutbg
                            } else if (config.activeThemeSelection == 2) {
                                R.color.toolbarcolor2
                            } else if (config.activeThemeSelection == 3) {
                                R.color.toolbarcolor3new
                            } else {
                                R.color.toolbarcolor2
                            }
                        }
                    },
                    strokeColorResId =
                    when (context.config.Message_full_App_Font_Style) {
                        "1" -> {
                            if (config.activeThemeSelection == 1) {
                                R.color.tablayoutbg
                            } else if (config.activeThemeSelection == 2) {
                                R.color.toolbarcolor2
                            } else if (config.activeThemeSelection == 3) {
                                R.color.toolbarcolor3new
                            } else {
                                R.color.toolbarcolor2
                            }
                        }

                        "2" -> {
                            if (config.activeThemeSelection == 1) {
                                R.color.commanfornewtabselectionstock1
                            } else if (config.activeThemeSelection == 2) {
                                R.color.commanfornewtabselection1
                            } else if (config.activeThemeSelection == 3) {
                                R.color.commanfornewtabselection2
                            } else {
                                R.color.white
                            }
                        }

                        else -> {
                            if (config.activeThemeSelection == 1) {
                                R.color.tablayoutbg
                            } else if (config.activeThemeSelection == 2) {
                                R.color.toolbarcolor2
                            } else if (config.activeThemeSelection == 3) {
                                R.color.toolbarcolor3new
                            } else {
                                R.color.toolbarcolor2
                            }
                        }
                    },
                    strokeWidthResId = com.intuit.sdp.R.dimen._1sdp
                )

                CoroutineScope(Dispatchers.Main).launch { holder.binding.tabcard.background = background }
            }
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

}