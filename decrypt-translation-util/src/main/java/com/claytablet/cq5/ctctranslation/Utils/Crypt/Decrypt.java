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

public class Decrypt{
    public static final String CRYPT_FILE_EXT = "cpt";

    public static Key getKeyFromKeystoreFile (File keystoreFile, String keystorePass, String keyAlias, String keyPass) {
        KeyStore keyStore = null;

        try {
            InputStream keystoreStream = new FileInputStream(keystoreFile);
            keyStore = AESEncryption.loadKeyStore(keystorePass, keystoreStream);
        } catch (GeneralSecurityException e) {
        } catch (IOException e) {}

        if (keyStore == null) {
            return null;
        }

        Key foundKey = null;

        try {
            foundKey = keyStore.getKey(keyAlias, keyPass.toCharArray());
        } catch (GeneralSecurityException e) {}

        return foundKey;
    }

    public static String decryptInputStream(Key key, InputStream is) {
        if (key == null) {
            return null;
        }

        byte[] decryptedBytes = null;
        String xmlString = null;
        try {
            decryptedBytes = AESEncryption.getDecryptedBytes(key, is);
            xmlString = new String(decryptedBytes, "UTF-8");

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (xmlString == null) {
            return null;
        }
        xmlString = XMLUtil.removeBOM(xmlString);
        return xmlString;
    }

    public static String decryptFile(File file, Key key) {
        InputStream is = null;

        try {
            is = new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return decryptInputStream(key, is);
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
        Key key = getKeyFromKeystoreFile(keystoreFile, w, a, p);

        File payloadFile = new File(i);
        String decrypted = decryptFile(payloadFile, key);

        try{
            PrintWriter writer = new PrintWriter(i.replace(".cpt", ""), "UTF-8");
            writer.println(decrypted);
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }
}