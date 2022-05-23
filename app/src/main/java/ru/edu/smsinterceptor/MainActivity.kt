package ru.edu.smsinterceptor

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ru.edu.smsinterceptor.databinding.ActivityMainBinding

/**
 * Класс главной активности приложения
 */
class MainActivity: AppCompatActivity() {

    /**
     * Обработчик широковещательных запросов,
     * настроенный на обработку перехвата СМС
     */
    private var receiver = object: BroadcastReceiver() {
        /**
         * Объект необходимый для извлечения перехваченных данных
         */
        private lateinit var bundle: Bundle

        /**
         * Объект СМС сообщения
         */
        private lateinit var currentSMS: SmsMessage

        /**
         * Функция обработки события получения СМС сообщения
         */
        override fun onReceive(context: Context?, data: Intent?) {
            if (context != null &&
                data != null &&
                data.action.equals("android.provider.Telephony.SMS_RECEIVED")
            ) {
                bundle = data.extras!!
                val pduObjects = bundle.get("pdus") as Array<*>
                for (aObject in pduObjects) {
                    currentSMS = getIncomingMessage(aObject, bundle)
                    handleResult(
                        MessageFilter.classifyMessage(
                            mainContext,
                            currentSMS.displayOriginatingAddress,
                            currentSMS.displayMessageBody
                        )
                    )
                }
            }
        }
    }

    /**
     * Объект привязки, который содержит в себе элементы верстки экрана
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Обработка создания активности
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Связываем верстку с объектом класса MainActivity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainContext = this
        // Запрос разрешений
        requestPermissions()
    }

    /**
     * Извлечение объекта SmsMessage из пакета данных
     */
    fun getIncomingMessage(aObject: Any?, bundle: Bundle): SmsMessage {
        val format = bundle.getString("format")
        return SmsMessage.createFromPdu(aObject as ByteArray, format)
    }

    /**
     * Обработчик результата перехвата СМС сообщений
     *
     * @param result данные об сообщении классифицированные
     */
    @SuppressLint("SetTextI18n")
    fun handleResult(result: Message) {
        Log.d(
            "[ПЕРЕХВАЧЕНО]",
            "onReceive: " +
                    "Номер: ${result.num}, " +
                    "Текст сообщения: ${result.messageText}"
        )
        binding.phoneNumberTextView.text = "Номер: \n" + result.num
        binding.messageTextView.text = "Текст сообщения: \n" + result.messageText
        when (result.mainGroup) {
            Constants.MAIN_GROUP_CONTACTS -> binding.groupTextView.text = Constants.GROUP_TITLE_CONTACT
            Constants.MAIN_GROUP_FINANCE -> binding.groupTextView.text = Constants.GROUP_TITLE_BANK
            Constants.MAIN_GROUP_SCAMMERS -> binding.groupTextView.text = Constants.GROUP_TITLE_SCAMMER
            else -> binding.groupTextView.text = ""
        }
        when (result.subGroup) {
            Constants.CONTACTS_SUBGROUP_FAMILY -> binding.subGroupTextView.text = Constants.SUBGROUP_TITLE_CONTACT_FAMILY
            Constants.CONTACTS_SUBGROUP_FRIENDS -> binding.subGroupTextView.text = Constants.SUBGROUP_TITLE_CONTACT_FRIEND
            Constants.FINANCE_SUBGROUP_MY_BANK -> binding.subGroupTextView.text = Constants.SUBGROUP_TITLE_CONTACT_BANK
            Constants.FINANCE_SUBGROUP_SPAMMERS -> binding.subGroupTextView.text = Constants.SUBGROUP_TITLE_CONTACT_SPAM
            else -> binding.subGroupTextView.text = ""
        }
    }

    /**
     * Обработка возобновления активности
     */
    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    /**
     * Обработка паузы активности
     */
    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    /**
     * Запуск перехватчика СМС сообщений
     */
    private fun registerReceiver() {
        try {
            this.registerReceiver(receiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Выключение перехватчика СМС сообщений
     */
    private fun unregisterReceiver() {
        try {
            this.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Обработка результатов процесса запроса разрешений приложения
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && requestCode == 322) {
            var isGranted = true
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    isGranted = false
                    break
                }
            }
            if (isGranted) {
                registerReceiver()
            }
        }
    }

    /**
     * Запрос разрешений, необходимые для работы приложения
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.BROADCAST_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
            ), 322
        )
    }

    /**
     * Объект компаньон (синглтон объект)
     */
    companion object {
        /**
         * Контекст, необходимый для работы с системными функциями
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var mainContext: Context
    }
}