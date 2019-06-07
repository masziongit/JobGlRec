package main;

import connect.EmailService;
import connect.FileSFTP;
import gen.Zipfile;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import util.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        Properties prop = new Properties();

        try {

            String config = System.getProperty("config.file");
            if (StringUtils.isEmpty(System.getProperty("config.file"))) {
                config = "config.properties";
            }

            prop.load(new FileInputStream(config));

            //custom log file
            if (!StringUtils.isEmpty(prop.getProperty("log.config.file"))){
                PropertyConfigurator.configure(prop.getProperty("log.config.file"));
            }

            logger.info("Load configuration file");
            logger.debug("from "+config);

            ZipOutputStream zos = new Zipfile().zip(prop);

            if (zos != null){

                try {
                    new EmailService(prop);
                }catch (Exception e){
                    logger.error(e);
                }

                try {
                    new FileSFTP(prop);
                }catch (Exception e){
                    logger.error(e);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }finally {
            DateFormat dateFormat = new SimpleDateFormat(prop.getProperty("file.name.dateformat"));
            String fileName = prop.getProperty("file.name.prefix")
                    +dateFormat.format(new Date())+"."+Constant.TypeFile.ZIP;
            File file = new File(fileName);
            if (file.exists())
                file.delete();
            logger.debug("Remove local file");
        }

    }

    private static void usage() {
        System.out.println("Usage command");
        System.out.println("\tjava -Dconfig.file=${config.properties} -jar ${JobGlRec.jar}");
        System.out.println("\tUse -Dconfig.file=${config.properties} to get your config");
        System.out.println("\tUse -jar ${JobGlRec.jar} to get your jarfile to run");
    }

}
