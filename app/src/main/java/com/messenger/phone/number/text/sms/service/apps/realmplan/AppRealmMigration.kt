package com.messenger.phone.number.text.sms.service.apps.realmplan

import io.github.xilinjia.krdb.dynamic.DynamicRealmObject
import io.github.xilinjia.krdb.dynamic.getNullableValue
import io.github.xilinjia.krdb.dynamic.getValue
import io.github.xilinjia.krdb.migration.AutomaticSchemaMigration

object AppRealmMigration : AutomaticSchemaMigration {

    override fun migrate(migrationContext: AutomaticSchemaMigration.MigrationContext) {
        migrateConversationLikeClass(
            migrationContext = migrationContext,
            className = ConversationRealm::class.simpleName.orEmpty(),
            defaultShowNotification = true,
            nullableIsNewMessage = true
        )
        migrateConversationLikeClass(
            migrationContext = migrationContext,
            className = ConversationBinRealm::class.simpleName.orEmpty(),
            defaultShowNotification = true,
            nullableIsNewMessage = false
        )
    }

    private fun migrateConversationLikeClass(
        migrationContext: AutomaticSchemaMigration.MigrationContext,
        className: String,
        defaultShowNotification: Boolean,
        nullableIsNewMessage: Boolean,
    ) {
        if (className.isBlank()) return

        migrationContext.enumerate(className) { oldObject, newObject ->
            newObject ?: return@enumerate

            newObject.set("threadId", oldObject.readNullableLongCompat("threadId"))
            newObject.set("messageId", oldObject.readNullableLongCompat("messageId"))
            newObject.set(
                "shownotification",
                oldObject.readBooleanCompat("shownotification", defaultShowNotification)
            )
            newObject.set(
                "isScheduled",
                oldObject.readBooleanCompat("isScheduled", false)
            )
            newObject.set(
                "isarchived",
                oldObject.readBooleanCompat("isarchived", false)
            )
            newObject.set(
                "isPrivateChat",
                oldObject.readBooleanCompat("isPrivateChat", false)
            )
            newObject.set(
                "isblocknumber",
                oldObject.readBooleanCompat("isblocknumber", false)
            )

            if (nullableIsNewMessage) {
                newObject.set(
                    "isnewmessage",
                    oldObject.readNullableBooleanCompat("isnewmessage") ?: false
                )
            } else {
                newObject.set(
                    "isnewmessage",
                    oldObject.readBooleanCompat("isnewmessage", false)
                )
            }
        }
    }
}

private fun DynamicRealmObject.readNullableLongCompat(propertyName: String): Long? {
    return readCompat(
        { getNullableValue(propertyName, Long::class) },
        { getValue(propertyName, Long::class) },
        { getNullableValue(propertyName, Int::class)?.toLong() },
        { getValue(propertyName, Int::class).toLong() },
        { getNullableValue(propertyName, String::class)?.toLongOrNull() },
        { getValue(propertyName, String::class).toLongOrNull() }
    )
}

private fun DynamicRealmObject.readBooleanCompat(
    propertyName: String,
    defaultValue: Boolean,
): Boolean {
    return readNullableBooleanCompat(propertyName) ?: defaultValue
}

private fun DynamicRealmObject.readNullableBooleanCompat(propertyName: String): Boolean? {
    return readCompat(
        { getNullableValue(propertyName, Boolean::class) },
        { getValue(propertyName, Boolean::class) },
        { getNullableValue(propertyName, Int::class)?.let(::intToBoolean) },
        { intToBoolean(getValue(propertyName, Int::class)) },
        { getNullableValue(propertyName, Long::class)?.let(::longToBoolean) },
        { longToBoolean(getValue(propertyName, Long::class)) },
        { getNullableValue(propertyName, String::class)?.let(::stringToBoolean) },
        { stringToBoolean(getValue(propertyName, String::class)) }
    )
}

private fun intToBoolean(value: Int): Boolean = value != 0

private fun longToBoolean(value: Long): Boolean = value != 0L

private fun stringToBoolean(value: String): Boolean? {
    return when (value.trim().lowercase()) {
        "1", "true", "yes", "y" -> true
        "0", "false", "no", "n" -> false
        else -> null
    }
}

private fun <T> DynamicRealmObject.readCompat(vararg readers: () -> T?): T? {
    readers.forEach { reader ->
        val value = runCatching { reader() }.getOrNull()
        if (value != null) {
            return value
        }
    }
    return null
}
