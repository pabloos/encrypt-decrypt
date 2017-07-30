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

public abstract class Crypt {
    public static final String CRYPT_FILE_EXT = "cpt";

    public static String encryptFile(File file, Key key)
            throws GeneralSecurityException, IOException {
        String path = file.getAbsolutePath();
        byte[] base64EncodedEncryptedMsg = AESEncryption.getEncryptedBytes(key, new FileInputStream(file));
//        String origAssetFilePath = path;
        String newPath = path + "." + Crypt.CRYPT_FILE_EXT;

        FileUtils.writeByteArrayToFile(new File(newPath),base64EncodedEncryptedMsg);

        return newPath;
    }

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
        } catch (GeneralSecurityException e) {
        }

        return foundKey;
    }
}