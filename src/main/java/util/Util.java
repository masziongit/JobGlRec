package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Util {
    public static void main( String[] args ) {


    }


    private static void zip(){

        byte[] buffer = new byte[1024];

        try{

            File file = new File("logs");

            FileOutputStream fos = new FileOutputStream("D:\\MyFile.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (File f : file.listFiles()) {
                ZipEntry ze= new ZipEntry(f.getName());
                zos.putNextEntry(ze);
                System.out.println(f.getAbsoluteFile());
                FileInputStream in = new FileInputStream(f.getAbsoluteFile());

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            System.out.println("Done");

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
