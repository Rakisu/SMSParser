package ru.edu.smsinterceptor

/**
 * Класс данных сообщения
 */
data class Message(
    /**
     * Телефонный номер отправителя
     */
    val num: String? = null,
    /**
     * Текст сообщения
     */
    val messageText: String? = null,
    /**
     * Основная категория
     */
    var mainGroup: Int? = null,
    /**
     * Подкатегория
     */
    var subGroup: Int? = null
)
