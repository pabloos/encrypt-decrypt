package com.claytablet.cq5.ctctranslation.Utils.Crypt;

import com.claytablet.cq5.ctctranslation.Utils.FastBase64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by kche on 05/11/2014.
 */
public class AESEncryption {
    private static final Logger log = LoggerFactory.getLogger(AESEncryption.class);

    private static final char CRYPT_IV_MSG_SEPARATOR = ';';

    public static byte[] getDecryptedBytes(Key key, InputStream is) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
//        File file = new File(path);
//        if (!file.exists()) {
//            return null;
//        }
        byte[] encryptedOutput = IOUtils.toByteArray(is);
//        byte[] encryptedOutput = FileUtils.readFileToByteArray(file);
        int separatorIndex = -1;
        for (int i = 0; i < encryptedOutput.length; i++) {
            if (encryptedOutput[i] == CRYPT_IV_MSG_SEPARATOR) {
                separatorIndex = i;
            }
        }
        if (separatorIndex == -1) {
            throw new IOException("Unable to find separator from stream");
        }

        int encodedIvLen = separatorIndex;
        byte[] base64EncodedIv = new byte[encodedIvLen];
        System.arraycopy(encryptedOutput, 0, base64EncodedIv, 0, encodedIvLen);
        byte[] ivBytes = FastBase64.decodeFast(base64EncodedIv);

        int encodedMsgLen = encryptedOutput.length - separatorIndex - 1;
        byte[] base64EncodedMsg = new byte[encodedMsgLen];
        System.arraycopy(encryptedOutput, separatorIndex + 1, base64EncodedMsg, 0, encodedMsgLen);
        byte[] encryptedMessagesInBytes = FastBase64.decodeFast(base64EncodedMsg);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //  use IV for init
        IvParameterSpec iv = getIvFromBytes(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] fileBytes = cipher.doFinal(encryptedMessagesInBytes);

        return fileBytes;
    }

    public static byte[] getEncryptedBytes(Key key, InputStream is)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] base64EncodedEncryptedMsg;
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // use IV for init
        IvParameterSpec iv = generateNewIv();

        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
//        File assetFile = new File(assetFilePath);
        byte[] fileBytes = IOUtils.toByteArray(is);
//        byte[] fileBytes = FileUtils.readFileToByteArray(assetFile);
        byte[] encryptedMessageInBytes = cipher.doFinal(fileBytes);
        base64EncodedEncryptedMsg = FastBase64.encodeToByte(encryptedMessageInBytes,
                false);
        byte[] base64EncodedIv = FastBase64.encodeToByte(iv.getIV(), false);

        int ivLen = base64EncodedIv.length;
        int msgLen = base64EncodedEncryptedMsg.length;
        byte[] encryptedOutput = new byte[ivLen + 1 + msgLen];
        System.arraycopy(base64EncodedIv, 0, encryptedOutput, 0, ivLen);
        System.arraycopy(base64EncodedEncryptedMsg, 0, encryptedOutput, ivLen + 1, msgLen);
        encryptedOutput[ivLen] = CRYPT_IV_MSG_SEPARATOR;

        return encryptedOutput;
    }

    public static KeyStore loadKeyStore(String keystorePass, InputStream inputStream) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        InputStream keystoreStream = inputStream;
        try {
//            inputStream = new FileInputStream(keystoreLocation);
            KeyStore keystore = KeyStore.getInstance("JCEKS");
            keystore.load(keystoreStream, keystorePass.toCharArray());
            return keystore;
        } finally {
            if (keystoreStream != null)
                keystoreStream.close();
        }
    }

    private static IvParameterSpec generateNewIv() {
        SecureRandom random = new SecureRandom();
        // The IV is 16 bytes log, because each AES block is 16 bytes long.
        // This is true regardless of the selected key size.
        byte[] buffer = new byte[16];
        random.nextBytes(buffer);
        return getIvFromBytes(buffer);
    }

    private static IvParameterSpec getIvFromBytes(byte[] ivBytes) {
        return new IvParameterSpec(ivBytes);
    }
}
