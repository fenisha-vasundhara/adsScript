package com.messenger.phone.number.text.sms.service.apps.realmplan

import io.github.xilinjia.krdb.ext.realmListOf
import io.github.xilinjia.krdb.types.RealmList
//import io.realm.kotlin.ext.realmListOf
//import io.realm.kotlin.types.RealmList
//import io.realm.kotlin.types.RealmObject
//import io.realm.kotlin.types.annotations.PrimaryKey
import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey
class MessageAttachmentRealm : RealmObject {
    @PrimaryKey
    var id: Long = 0L

    var text: String = ""

    // RealmList holds links to AttachmentRealm objects (not embedded, so they can also be queried standalone).
    var attachments: RealmList<AttachmentRealm> = realmListOf()
}
