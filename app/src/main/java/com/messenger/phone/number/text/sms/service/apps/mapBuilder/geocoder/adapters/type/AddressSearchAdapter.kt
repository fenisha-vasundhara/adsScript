package com.messenger.phone.number.text.sms.service.apps.mapBuilder.geocoder.adapters.type

import android.location.Address
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LekuViewHolder
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.geocoder.adapters.base.LekuSearchAdapter

abstract class AddressSearchAdapter<T : LekuViewHolder> : LekuSearchAdapter<T, Address>()
