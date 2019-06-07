package connect;

import com.jcraft.jsch.*;
import org.apache.log4j.Logger;
import util.Constant;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author javagists.com
 */
public class FileSFTP {

    final static org.apache.log4j.Logger logger = Logger.getLogger(FileSFTP.class);


    public FileSFTP(Properties prop) {

        DateFormat dateFormat = new SimpleDateFormat(prop.getProperty("file.name.dateformat"));
        String dateStr = dateFormat.format(new Date());

        Session session = null;
        try {

            session = getSession(prop);
            ChannelSftp sftpChannel = getChanel(session);
            logger.info("Upload file..");
            String fileName = prop.getProperty("file.name.prefix")
                    +dateStr + "." + Constant.TypeFile.ZIP;

                sftpChannel.put(fileName, prop.getProperty("sftp.upload.path") + fileName);
                logger.debug("Upload to " + prop.getProperty("sftp.upload.path") + fileName);

            logger.info("Upload file complete!!");
            sftpChannel.exit();

        } catch (JSchException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (SftpException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            logger.info("Disconnect from SFTP");
            logger.debug(session.getHost());
            session.disconnect();
        }

    }

    private Session getSession(Properties prop) throws JSchException {

        JSch jsch = new JSch();
        logger.debug("Identity by key " + prop.getProperty("sftp.ssh.keyfile") +
                " passphrase is " + prop.getProperty("sftp.ssh.passphrase"));
        jsch.addIdentity(prop.getProperty("sftp.ssh.keyfile"), prop.getProperty("sftp.ssh.passphrase"));
        logger.info("Start Connection to SFTP");

        Session session = jsch.getSession(prop.getProperty("sftp.user")
                , prop.getProperty("sftp.host"), Integer.valueOf(prop.getProperty("sftp.port")));
        Properties config = new java.util.Properties();

        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
//        session.setPassword(prop.getProperty("sftp.password"));

        session.connect();

        logger.info("Connection to SFTP host");
        logger.debug(session.getHost());

        return session;
    }

    private ChannelSftp getChanel(Session session) throws JSchException {

        logger.debug("Session openChannel " + Constant.Session.OPEN_CHANNEL);
        Channel channel = session.openChannel(Constant.Session.OPEN_CHANNEL);
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        logger.debug("OpenChannel complete!!");
        return sftpChannel;
    }

}