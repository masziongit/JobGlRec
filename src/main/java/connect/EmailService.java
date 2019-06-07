package connect;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import util.Constant;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmailService {

    final static org.apache.log4j.Logger logger = Logger.getLogger(EmailService.class);

    private Properties prop;

    public EmailService(Properties prop) throws FileNotFoundException {

        this.prop = prop;

        logger.info("Connecting to smtp sever");
        Session session = Session.getDefaultInstance(prop,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(prop.getProperty("mail.user")
                                ,prop.getProperty("mail.pass"));
                    }
                });
        logger.debug("host : "+prop.getProperty("mail.smtp.host"));
        logger.debug("port : "+prop.getProperty("mail.smtp.port"));

        session.setDebug(Boolean.parseBoolean(prop.getProperty("mail.debug")));

        try {
            logger.info("Connection complete !!");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(prop.getProperty("mail.sender")));
            if (!StringUtils.isEmpty(prop.getProperty("mail.to"))) {
                message.addRecipients(MimeMessage.RecipientType.TO,
                        splitEmailList(prop.getProperty("mail.to")));
            }
            if (!StringUtils.isEmpty(prop.getProperty("mail.cc"))) {
                message.addRecipients(MimeMessage.RecipientType.CC,
                        splitEmailList(prop.getProperty("mail.cc")));
            }
            if (!StringUtils.isEmpty(prop.getProperty("mail.bcc"))) {
                message.addRecipients(MimeMessage.RecipientType.BCC,
                        splitEmailList(prop.getProperty("mail.bcc")));
            }

            message.setSubject(replaceDateFormat(prop.getProperty("mail.subject")));

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            // Now set the actual message
            InputStream in = new FileInputStream(new File(prop.getProperty("mail.message.html.file")));
            messageBodyPart.setContent(replaceDateFormat(IOUtils.toString(in,Charset.forName(Constant.CharSet.UTF)))
                    ,Constant.CharSet.HTML_TYPE);

            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();

            DateFormat dateFormat = new SimpleDateFormat(prop.getProperty("file.name.dateformat"));
            String filename = prop.getProperty("file.name.prefix")
                    +dateFormat.format(new Date())+"."+Constant.TypeFile.ZIP;

            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            Transport.send(message);

            logger.info("Send email complete");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replaceDateFormat(String str){

        String pattern = prop.getProperty("mail.dateformat");
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return str.replace(pattern,dateFormat.format(new Date()));
    }

    private static InternetAddress[] splitEmailList(String email) {
        List<InternetAddress> emList = new ArrayList<>();
        InternetAddress cc[] = null;
        if (!StringUtils.isEmpty(email)) {
            String sp[] = email.split(",");
            if (sp != null && sp.length > 0) {
                for (String em : sp) {
                    if (!StringUtils.isEmpty((em))) {
                        try {
                            InternetAddress ipAddress = new InternetAddress(em);
                            emList.add(ipAddress);
                        } catch (AddressException ex) {
                            try {
                                throw ex;
                            } catch (AddressException e) {
                                logger.error(e);
                            }
                        }
                    }

                }

            }
            if (emList != null && emList.size() > 0) {
                cc = new InternetAddress[emList.size()];
                int idx = 0;
                for (InternetAddress em : emList) {
                    cc[idx] = em;
                    idx++;
                }
            }
        }
        return cc;
    }


}
