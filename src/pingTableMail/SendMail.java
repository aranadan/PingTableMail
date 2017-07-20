package pingTableMail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import pingTableMail.controllers.MainController;

import java.util.Date;
import java.util.Properties;

public class SendMail {

	private final Properties props;

	public SendMail() {

		props = new Properties();
		props.put("mailTo.smtp.starttls.enable", "true");
		props.put("mailTo.smtp.auth", "true");
		props.put("mailTo.smtp.host", "smtp.gmail.com");
		props.put("mailTo.smtp.port", "587");
	}

	public void send(String ip, String status, String mail, final String username, final String password) {
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
			message.setSubject("Alarm");
			message.setText(ip + " - is " + status + " " + MainController.dateFormat.format(new Date()));

			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
