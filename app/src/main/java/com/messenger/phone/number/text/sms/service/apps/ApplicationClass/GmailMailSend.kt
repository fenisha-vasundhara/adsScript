package com.messenger.phone.number.text.sms.service.apps.ApplicationClass

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.messenger.phone.number.text.sms.service.apps.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GmailMailSend @Inject constructor(@ApplicationContext var context: Context) {

    val htmlContent = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Sent</title>
    <style>
        /* Reset CSS */
        body, h1, p {
            margin: 0;
            padding: 0;
        }
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            background-color: #f5f5f5;
            padding: 20px;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
            border-radius: 8px;
            padding: 40px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center; /* Center align content */
        }
        .app-header {
            text-align: center;
            margin-bottom: 20px;
        }
        .app-icon {
            width: 50px;
            height: 50px;
            margin-bottom: 10px;
        }
        .app-name {
            font-size: 24px;
            color: #333333;
        }
        .white-box {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .password-box {
            background-color: #f0f0f0;
            padding: 10px;
            border-radius: 5px;
            font-family: "Courier New", Courier, monospace;
            font-size: 16px;
            color: #333333;
        }
        .bottom-content {
            font-size: 14px;
            color: #666666;
            margin-top: 20px;
        }
        .center-text {
            text-align: center; /* Center align text */
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="app-header">
            <img src="https://play-lh.googleusercontent.com/lFzuzS_OOOcGdRBCL6-SEOPDaYsMY262yO1OMIdtGRQZ2Tod9ab9oeSoPpX3RiPbuUjQ=s48-rw" alt="App Icon" class="app-icon">
            <h1 class="app-name">Messages : SMS & Private Chat</h1>
        </div>
        <div class="white-box">
            <h2>Your Password</h2>
            <p>Hello,</p>
            <p>Your password has been sent successfully. Please find your password below:</p>
            <div class="password-box">
                <strong>{{PASSWORD}}</strong>
            </div>
            <p>Please remember to keep your password secure and do not share it with anyone.</p>
        </div>
        <div class="bottom-content">
            <p class="center-text">Contact us at profagnesh009@gmail.com for any assistance.</p>
            <p class="center-text">© 2024 Messages : SMS & Private Chat. All rights reserved.</p>
        </div>
    </div>
</body>
</html>


        """.trimIndent()

    var maillistener: ((Boolean) -> Unit)? = null

    suspend fun sendEmail(
        subject: String,
        receiverEmail: String,
        actualPassword: String,
        bottmtext: String,
        last: String,
        lasttop: String,
        passwordbelow: String,
        hellotxt: String,
        yourpasstxt: String
    ) {


        val modifiedHtmlContent = htmlContent.replace("{{PASSWORD}}", actualPassword)
            .replace("Please remember to keep your password secure and do not share it with anyone.", bottmtext)
            .replace("© 2024 Messages : SMS & Private Chat. All rights reserved.", last)
            .replace("Contact us at profagnesh009@gmail.com for any assistance.", lasttop)
            .replace("Your password has been sent successfully. Please find your password below:", passwordbelow)
            .replace("Hello,", hellotxt)
            .replace("Your Password", yourpasstxt)


        val senderEmail = "rohan.vasundhara19@gmail.com"
        val password = "hgmg jktl ookx pbqq"
        val stringHost = "smtp.gmail.com"
        val properties = System.getProperties()
        properties["mail.smtp.host"] = stringHost
        properties["mail.smtp.port"] = "465"
        properties["mail.smtp.ssl.enable"] = "true"
        properties["mail.smtp.auth"] = "true"

        try {
            withContext(Dispatchers.IO) {
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, password)
                    }
                })
                val mimeMessage = MimeMessage(session)
                mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(receiverEmail))
                mimeMessage.subject = subject
                mimeMessage.setContent(modifiedHtmlContent, "text/html; charset=utf-8")
                Transport.send(mimeMessage)
            }
            // Show "Sent Successfully" message
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.resources.getString(R.string.Sent_Successfully), Toast.LENGTH_SHORT).show()
                maillistener?.invoke(true)
            }
        } catch (e: AddressException) {
            withContext(Dispatchers.Main) {
                Log.d("AddressException", "sendEmail:AddressException 1 <--> ${e.localizedMessage}")
                Toast.makeText(context, context.resources.getString(R.string.Try_Again), Toast.LENGTH_SHORT).show()
                maillistener?.invoke(false)
            }
        } catch (e: MessagingException) {
            withContext(Dispatchers.Main) {
                Log.d("AddressException", "sendEmail:AddressException 2 <--> ${e.localizedMessage}")
                Toast.makeText(context,  context.resources.getString(R.string.Try_Again), Toast.LENGTH_SHORT).show()
                maillistener?.invoke(false)
            }
        }
    }

}