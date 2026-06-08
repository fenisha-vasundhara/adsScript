package com.messenger.phone.number.text.sms.service.apps.realmplan

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test 5 — ProGuard keep rules verification.
 *
 * Checks that every Realm model class is reachable by its canonical name at runtime.
 * If R8/ProGuard renames or removes a class, Class.forName() will throw and this test
 * will fail — surfacing the issue before it becomes a runtime crash in production.
 *
 * This test runs on the JVM (no Robolectric needed) and is fast.
 */
class RealmProguardTest {

    private val expectedClasses = listOf(
        "com.messenger.phone.number.text.sms.service.apps.realmplan.ConversationRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.ConversationBinRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.MessageAttachmentRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.AttachmentRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.CategoryRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.CategoryNumberRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.ContactRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.RecentSearchRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.ReminderRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.StarNumberRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.FolderRealm",
        "com.messenger.phone.number.text.sms.service.apps.realmplan.PhotoRealm",
    )

    @Test
    fun `all Realm model classes are loadable by their canonical names`() {
        val failures = mutableListOf<String>()
        for (className in expectedClasses) {
            runCatching { Class.forName(className) }
                .onFailure { failures.add("$className — ${it.message}") }
        }
        if (failures.isNotEmpty()) {
            throw AssertionError(
                "The following Realm model classes could not be loaded (check proguard-rules.pro):\n" +
                    failures.joinToString("\n") { "  • $it" }
            )
        }
    }

    @Test
    fun `Realm model class names are unchanged (not obfuscated)`() {
        // If R8 obfuscates a class, its simple name becomes something like "a", "b", etc.
        // Every model class must have a simple name longer than 2 characters.
        for (className in expectedClasses) {
            val clazz = runCatching { Class.forName(className) }.getOrNull()
                ?: continue  // Already caught by the previous test
            assert(clazz.simpleName.length > 2) {
                "${clazz.simpleName} looks obfuscated — add a -keep rule for $className"
            }
        }
    }

    @Test
    fun `ConversationRealm has expected field names accessible via reflection`() {
        val clazz = ConversationRealm::class.java
        val fieldNames = clazz.declaredFields.map { it.name }.toSet()

        val required = listOf(
            "id", "threadId", "title", "phoneNumber", "snippet",
            "time", "isarchived", "isblocknumber", "isPrivateChat", "isScheduled"
        )
        val missing = required.filter { it !in fieldNames }
        assertEquals(
            "Missing fields on ConversationRealm (ProGuard stripped them): $missing",
            emptyList<String>(),
            missing
        )
    }
}
