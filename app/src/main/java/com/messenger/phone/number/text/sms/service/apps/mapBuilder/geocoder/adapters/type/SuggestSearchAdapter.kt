package com.messenger.phone.number.text.sms.service.apps.mapBuilder.geocoder.adapters.type

import com.messenger.phone.number.text.sms.service.apps.mapBuilder.LekuViewHolder
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.geocoder.PlaceSuggestion
import com.messenger.phone.number.text.sms.service.apps.mapBuilder.geocoder.adapters.base.LekuSearchAdapter

abstract class SuggestSearchAdapter<T : LekuViewHolder> : LekuSearchAdapter<T, PlaceSuggestion>()
