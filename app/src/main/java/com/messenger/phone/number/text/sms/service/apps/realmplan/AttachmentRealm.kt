package com.messenger.phone.number.text.sms.service.apps.realmplan

import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.Index
import io.github.xilinjia.krdb.types.annotations.PrimaryKey
//import io.realm.kotlin.types.RealmObject
//import io.realm.kotlin.types.annotations.Index
//import io.realm.kotlin.types.annotations.PrimaryKey

class AttachmentRealm : RealmObject {
    // Composite key because Room's id is nullable (auto-generated). Key is built from roomId|messageId|uri|filename.
    @PrimaryKey
    var primaryKey: String = ""

    var roomId: Long? = null

    @Index
    var messageId: Long = 0L

    var uriString: String = ""
    var mimetype: String = ""
    var width: Int = 0
    var height: Int = 0
    var filename: String = ""
}
