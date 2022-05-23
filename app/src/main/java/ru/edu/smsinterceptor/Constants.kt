package ru.edu.smsinterceptor

/**
 * Константы приложения
 */
object Constants {
    /**
     * Индентификаторы категорий
     */
    const val MAIN_GROUP_NONE = 0
    const val MAIN_GROUP_CONTACTS = 1
    const val MAIN_GROUP_FINANCE = 2
    const val MAIN_GROUP_SCAMMERS = 3

    /**
     * Идентификаторы подкатегорий
     */
    const val CONTACTS_SUBGROUP_FAMILY = 1
    const val CONTACTS_SUBGROUP_FRIENDS = 2
    const val FINANCE_SUBGROUP_MY_BANK = 3
    const val FINANCE_SUBGROUP_SPAMMERS = 4

    /**
     * Фразы шаблоны для определения
     */
    const val BANK_SPAM_SAMPLE_1 = "вам предварительно одобрена"
    const val BANK_SPAM_SAMPLE_2 = "бесплатно выпущена карта"
    const val BANK_SPAM_SAMPLE_3 = "успейте оформить кредит"
    const val BANK_SPAM_SAMPLE_4 = "оформите рассрочку"
    const val BANK_SPAM_SAMPLE_5 = "выгодное предложение"
    const val BANK_SPAM_SAMPLE_6 = "вам одобрен кредит"
    const val BANK_SPAM_SAMPLE_7 = "срок предложения истекает"

    /**
     * Имена групп в контактах
     */
    const val GROUP_NAME_FAMILY_1 = "Family"
    const val GROUP_NAME_FAMILY_2 = "Семья"
    const val GROUP_NAME_FRIENDS_1 = "Friends"
    const val GROUP_NAME_FRIENDS_2 = "Друзья"
    const val GROUP_NAME_BANK_1 = "Finance"
    const val GROUP_NAME_BANK_2 = "Финансы"
    const val GROUP_NAME_BANK_3 = "Bank"
    const val GROUP_NAME_BANK_4 = "Банк"

    /**
     * Названия категорий, для отображения
     */
    const val GROUP_TITLE_CONTACT = "Категория: \nличные контакты"
    const val GROUP_TITLE_BANK = "Категория: \nбанки"
    const val GROUP_TITLE_SCAMMER = "Категория: \nвероятный скамер"

    /**
     * Названия подкатегорий, для отображения
     */
    const val SUBGROUP_TITLE_CONTACT_FAMILY = "Подкатегория: \nсемья"
    const val SUBGROUP_TITLE_CONTACT_FRIEND = "Подкатегория: \nдруг"
    const val SUBGROUP_TITLE_CONTACT_BANK = "Подкатегория: \nиспользуемый банк"
    const val SUBGROUP_TITLE_CONTACT_SPAM = "Подкатегория: \nбанк спамер"

}