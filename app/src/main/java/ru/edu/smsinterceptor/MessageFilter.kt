package ru.edu.smsinterceptor

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import java.util.regex.Pattern

/**
 * Объект, содержащий реализации функции, предназначены для анализа SMS сообщений
 */
object MessageFilter {

    /**
     * Функция классификации SMS сообщения
     *
     * @param context - контекст
     * @param number - телефонный номер отправителя
     * @param message - текст полученного сообщения
     *
     * @return - объект Message
     */
    fun classifyMessage(context: Context, number: String?, message: String?): Message {
        val messageData = Message(num = number, messageText = message)
        if (number != null && number != "") {
            val id = getContactId(context, number)
            var group: String? = null
            if (id != null) {
                group = getContactGroup(context, id)
            }
            if (group != null && group != "") {
                when (group) {
                    Constants.GROUP_NAME_FAMILY_1,
                    Constants.GROUP_NAME_FAMILY_2 -> {
                        messageData.mainGroup = Constants.MAIN_GROUP_CONTACTS
                        messageData.subGroup = Constants.CONTACTS_SUBGROUP_FAMILY
                    }
                    Constants.GROUP_NAME_FRIENDS_1,
                    Constants.GROUP_NAME_FRIENDS_2 -> {
                        messageData.mainGroup = Constants.MAIN_GROUP_CONTACTS
                        messageData.subGroup = Constants.CONTACTS_SUBGROUP_FRIENDS
                    }
                    Constants.GROUP_NAME_BANK_1,
                    Constants.GROUP_NAME_BANK_2,
                    Constants.GROUP_NAME_BANK_3,
                    Constants.GROUP_NAME_BANK_4 -> {
                        messageData.mainGroup = Constants.MAIN_GROUP_FINANCE
                        if (isBankSpam(messageData.messageText)) {
                            messageData.subGroup = Constants.FINANCE_SUBGROUP_SPAMMERS
                        } else {
                            messageData.subGroup = Constants.FINANCE_SUBGROUP_MY_BANK
                        }
                    }
                    else -> {
                        if (isContainsURL(messageData.messageText)) {
                            messageData.mainGroup = Constants.MAIN_GROUP_SCAMMERS
                        } else {
                            messageData.mainGroup = Constants.MAIN_GROUP_NONE
                        }
                    }
                }
            } else {
                if (isContainsURL(messageData.messageText)) {
                    messageData.mainGroup = Constants.MAIN_GROUP_SCAMMERS
                } else if (isBankSpam(messageData.messageText)) {
                    messageData.mainGroup = Constants.MAIN_GROUP_FINANCE
                    messageData.subGroup = Constants.FINANCE_SUBGROUP_SPAMMERS
                } else {
                    messageData.mainGroup = Constants.MAIN_GROUP_NONE
                }
            }
        }
        return messageData
    }

    /**
     * Получение идентификатора контакта по телефонному номеру
     *
     * @param context - контекст
     * @param number - телефонный номер
     *
     * @return - идентификатор контакта
     */
    private fun getContactId(context: Context, number: String?): String? {
        val cr = context.contentResolver
        val uri: Uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val cursor = cr.query(
            uri, arrayOf(
                ContactsContract.Contacts._ID
            ), null, null, null
        )
        var contactID: String? = null
        if (cursor!!.moveToFirst()) {
            try {
                contactID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        if (!cursor.isClosed) {
            cursor.close()
        }
        return contactID
    }

    /**
     * Получение организации
     *
     * @param context - контекст
     * @param contactID - идентификатор контакта
     *
     * @return - название организаии (группы)
     */
    private fun getContactGroup(context: Context, contactID: String): String? {
        val cr = context.contentResolver
        var orgName: String? = null
        val orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val orgWhereParams = arrayOf(
            contactID,
            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
        )
        val orgCur: Cursor? = cr.query(
            ContactsContract.Data.CONTENT_URI,
            null, orgWhere, orgWhereParams, null
        )
        if (orgCur != null) {
            if (orgCur.moveToFirst()) {
                orgName = orgCur.getString(orgCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DATA))
            }
            if (!orgCur.isClosed) {
                orgCur.close()
            }
        }
        return orgName
    }

    /**
     * Проверка на наличие URL ссылки в тексте сообщения
     *
     * @param messageData - текст сообщения
     *
     * @return - флаг наличия URL ссылки
     */
    private fun isContainsURL(messageData: String?): Boolean {
        if (messageData == null || messageData == "")
            return false
        val urlPattern: Pattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
        )
        val parts: List<String> = messageData.split(" ")
        for (item in parts) {
            if (urlPattern.matcher(item).matches()) {
                return true
            }
        }
        return false
    }

    /**
     * Проверка на наличие банковских шаблонов-фраз спама в тексте сообщения
     *
     * @param messageData - текст сообщения
     *
     * @return - флаг наличия банковских шаблонов-фраз в тексте сообщения
     */
    private fun isBankSpam(messageData: String?): Boolean {
        if (messageData == null || messageData == "")
            return false
        if (messageData.contains(Constants.BANK_SPAM_SAMPLE_1, ignoreCase = true) ||
            messageData.contains(Constants.BANK_SPAM_SAMPLE_2, ignoreCase = true) ||
            messageData.contains(Constants.BANK_SPAM_SAMPLE_3, ignoreCase = true) ||
            messageData.contains(Constants.BANK_SPAM_SAMPLE_4, ignoreCase = true) ||
            messageData.contains(Constants.BANK_SPAM_SAMPLE_5, ignoreCase = true) ||
            messageData.contains(Constants.BANK_SPAM_SAMPLE_6, ignoreCase = true) ||
            messageData.contains(Constants.BANK_SPAM_SAMPLE_7, ignoreCase = true)
        ) {
            return true
        }
        return false
    }
}