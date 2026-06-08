package com.messenger.phone.number.text.sms.service.apps.data;

import android.content.res.Resources;
import android.provider.ContactsContract;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.LongSparseArray;

public class AddressBookContact {
    private long id;
    private Resources res;
    private String name;
    private LongSparseArray<String> emails;
    private String phones;

    AddressBookContact(long id, String name, Resources res) {
        this.id = id;
        this.name = name;
        this.res = res;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Resources getRes() {
        return res;
    }

    public void setRes(Resources res) {
        this.res = res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LongSparseArray<String> getEmails() {
        return emails;
    }

    public void setEmails(LongSparseArray<String> emails) {
        this.emails = emails;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    @Override
    public String toString() {
        return "AddressBookContact{" +
                "id=" + id +
                ", res=" + res +
                ", name='" + name + '\'' +
                ", emails=" + emails +
                ", phones=" + phones +
                '}';
    }
//    @Override
//    public String toString() {
//        return toString(false);
//    }

//    public String toString(boolean rich) {
//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        if (rich) {
//            builder.append("id: ").append(Long.toString(id))
//                    .append(", name: ").append("\u001b[1m").append(name).append("\u001b[0m");
//        } else {
//            builder.append(name);
//        }
//
//        if (phones != null) {
//            builder.append("\n\tphones: ");
//            for (int i = 0; i < phones.size(); i++) {
//                int type = (int) phones.keyAt(i);
//                builder.append(ContactsContract.CommonDataKinds.Phone.getTypeLabel(res, type, ""))
//                        .append(": ")
//                        .append(phones.valueAt(i));
//                if (i + 1 < phones.size()) {
//                    builder.append(", ");
//                }
//            }
//        }
//
//        if (emails != null) {
//            builder.append("\n\temails: ");
//            for (int i = 0; i < emails.size(); i++) {
//                int type = (int) emails.keyAt(i);
//                builder.append(ContactsContract.CommonDataKinds.Email.getTypeLabel(res, type, ""))
//                        .append(": ")
//                        .append(emails.valueAt(i));
//                if (i + 1 < emails.size()) {
//                    builder.append(", ");
//                }
//            }
//        }
//        return builder.toString();
//    }

//    public void addEmail(int type, String address) {
//        if (emails == null) {
//            emails = new LongSparseArray<String>();
//        }
//        emails.put(type, address);
//    }

    public void addPhone(int type, String number) {
       try {
           if (phones == null) {
               phones = "00";
           }
           phones=number;
       }catch (Exception ignored){}
    }
}