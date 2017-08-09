package com.claytablet.cq5.ctctranslation.Utils.Crypt;

import com.claytablet.cq5.ctctranslation.Utils.XMLUtil;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

/**
 * Created by kche on 03/11/2014.
 */

public class Encrypt extends Crypt{
    public static final String CRYPT_FILE_EXT = "cpt";

    public static String encryptFile(File file, Key key)
            throws GeneralSecurityException, IOException {
        String path = file.getAbsolutePath();
        byte[] base64EncodedEncryptedMsg = AESEncryption.getEncryptedBytes(key, new FileInputStream(file));
        String newPath = path.replace(".xml", "") + "." + Crypt.CRYPT_FILE_EXT;

        FileUtils.writeByteArrayToFile(new File(newPath),base64EncodedEncryptedMsg);

        return newPath;
    }

    public static void main(String [] args) throws FileNotFoundException, ParseException, GeneralSecurityException, IOException {
        Options options = new Options();

        options.addOption("s", true, "keystore path");
        options.addOption("w", true, "keystore password");
        options.addOption("i", true, "input file path");
        options.addOption("a", true, "key alias");
        options.addOption("p", true, "key password");

        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse( options, args);

        String s = cmd.getOptionValue("s");
        String w = cmd.getOptionValue("w");
        String i = cmd.getOptionValue("i");
        String a = cmd.getOptionValue("a");
        String p = cmd.getOptionValue("p");

        File keystoreFile = new File(s);
        Key key = Crypt.getKeyFromKeystoreFile(keystoreFile, w, a, p);

        File payloadFile = new File(i);
        
        String encrypted = encryptFile(payloadFile, key);
    }
}
