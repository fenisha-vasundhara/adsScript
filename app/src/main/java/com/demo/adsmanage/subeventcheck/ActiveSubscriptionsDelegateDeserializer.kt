package com.demo.adsmanage.subeventcheck

import com.google.gson.*
import java.lang.reflect.Type

class ActiveSubscriptionsDelegateDeserializer : JsonDeserializer<ActiveSubscriptionsDelegate> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ActiveSubscriptionsDelegate {
        val jsonObject = json.asJsonObject
        val valueJsonArray = jsonObject.getAsJsonArray("_value")
        val value = valueJsonArray.map { it.asString }
        return ActiveSubscriptionsDelegate(value)
    }
}