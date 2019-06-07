package gen;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import util.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipfile {

    final static Logger logger = Logger.getLogger(Zipfile.class);

    public ZipOutputStream zip(Properties prop) {

//        byte[] buffer = new byte[1024];
        DateFormat dateFormat = new SimpleDateFormat(prop.getProperty("file.name.dateformat"));
        String dateStr = dateFormat.format(new Date());
        ZipOutputStream zos = null;

        try{

            File file = new File(prop.getProperty("file.share.path"));
            List<File> files = Arrays.stream(file.listFiles()).filter(f->fileCon(f,prop,dateFormat,dateStr))
                    .collect(Collectors.toList());


            logger.info("Get Zip file");
            logger.debug("From Path "+file.getAbsolutePath());

            if (files.size() > 0){
                String out = prop.getProperty("file.name.prefix")
                        +dateStr+"."+Constant.TypeFile.ZIP;
                FileOutputStream fos = new FileOutputStream(out);
                zos = new ZipOutputStream(fos);

                for (File f : file.listFiles()) {

                        ZipEntry ze= new ZipEntry(f.getName());
                        logger.debug("Add ZipEntry : "+ze.getName());
                        zos.putNextEntry(ze);

                        FileInputStream in = new FileInputStream(f.getAbsoluteFile());
                        IOUtils.write(IOUtils.toByteArray(in),zos);
                        in.close();

                }

                zos.closeEntry();
                //remember close it
                zos.close();

                logger.info("Zip File Complete!!");
                logger.debug("Output to Zip : " +out+" Complete!!");
            }else {
                throw new IOException("File in folder is empty");
            }

        }catch(IOException e){
            logger.error(e);
            e.printStackTrace();
        }

        return zos;
    }

    private boolean fileCon(File f, Properties prop, DateFormat dateFormat, String dateStr) {
        String fileDate = dateFormat.format(f.lastModified());
        return f.isFile()
                &&FilenameUtils.getExtension(f.getName()).equalsIgnoreCase(prop.getProperty("file.type.zip"))
                && fileDate.equalsIgnoreCase(dateStr);
    }

}
