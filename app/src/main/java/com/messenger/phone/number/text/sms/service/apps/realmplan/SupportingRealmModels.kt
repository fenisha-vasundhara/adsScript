package com.messenger.phone.number.text.sms.service.apps.realmplan
//
//import io.realm.kotlin.types.RealmObject
//import io.realm.kotlin.types.annotations.Index
//import io.realm.kotlin.types.annotations.PrimaryKey
import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.Index
import io.github.xilinjia.krdb.types.annotations.PrimaryKey
class CategoryRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    @Index
    var catName: String = ""

    var filterName: String? = null
    var isDefaultCategory: Boolean = false
}

class CategoryNumberRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    @Index
    var catName: String = ""

    var date: String = ""
    var read: Boolean = false
    var title: String = ""
    var photoUri: String? = null
    var usesCustomTitle: Boolean = false

    @Index
    var phoneNumber: String = ""

    var snippet: String = ""

    @Index
    var time: Long? = null

    var type: Int? = null
    var isnumaric: Boolean = false
}

class ContactRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    var name: String = ""
    var number: String = ""
    var contactId: Int = 0

    @Index
    var onlynumber: String = ""

    @Index
    var catName: String = "No"
}

class RecentSearchRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    @Index
    var recentsearch: String = ""
}

class ReminderRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    var remindertitle: String? = null
    var reminderstartdate: String? = null
    var reminderenddate: String? = null
    var selected: Boolean = false
}

class StarNumberRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    var date: String = ""
    var read: Boolean = false
    var title: String = ""
    var photoUri: String? = null
    var usesCustomTitle: Boolean = false

    @Index
    var phoneNumber: String = ""

    var snippet: String = ""

    @Index
    var time: Long? = null

    var type: Int? = null
    var isnumaric: Boolean = false
}

class FolderRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    var foldername: String = ""
    var folderimgcount: String = ""
    var folderthumimage: String = ""

    @Index
    var folderpath: String? = null
}

class PhotoRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    @Index
    var path: String = ""

    var position: Int = 0
    var selected: Boolean = false
    var lastModifieddate: Long = 0L
}
