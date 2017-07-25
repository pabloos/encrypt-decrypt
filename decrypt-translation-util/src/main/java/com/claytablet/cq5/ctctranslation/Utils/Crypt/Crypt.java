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
public class Crypt {
    public static final String CRYPT_FILE_EXT = "cpt";

    public static boolean isFileEncrypted(String path) {
        //return FilenameUtils.isExtension(path, Crypt.CRYPT_FILE_EXT);
        return path.endsWith(Crypt.CRYPT_FILE_EXT);
    }
/*
    public static String getJobIdFromAssetId(String assetID, CTCDataService ctcDataService, CTLogService ctcLogService) {

        List<TranslationItem> projectItems = ctcDataService.getTranslatedItemsByAssetId(assetID);
        if (projectItems == null || projectItems.size() == 0) {
            ctcLogService.LogStatusDebug("getJobIdFromAssetId: Can't find related TranslationItems.  Job should be deleted. Igonre this.");
            return null;
        }

        String jobID = projectItems.get(0).getJobId();
        return jobID;
    }

    private static CTC_LSP getLspFromTranslationJob(CTCConfigService ctcConfigService, TranslationJob job) {
        CTC_LSP lsp = ctcConfigService.getCTC_LSP_ById(job.getLSPId());
        return lsp;
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

    }*/

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

    /*public static Key getKeyFromTranslationJob(TranslationJob job, CTCConfigService ctcConfigService, CTLogService ctcLogService) {
        CTC_LSP lsp = getLspFromTranslationJob(ctcConfigService, job);
        String tmsGuid = lsp.getLSP_IsTMS() ? job.getTmsConfigId() : null;

        return getCryptKeyByLspId(lsp.getLSP_id(), tmsGuid, ctcConfigService, ctcLogService);
    }


    private static Key getCryptKeyByLspId(String lspId,
                                         String tmsGuid, CTCConfigService ctcConfigService, CTLogService ctcLogService) {
        CTC_LSP lsp = ctcConfigService.getCTC_LSP_ById(lspId);
        CryptKeyInfo keyInfo = ctcConfigService.getCryptKeyInfoByLsp(lsp, tmsGuid);

        if (keyInfo == null) {
            ctcLogService.LogDebug("getCryptKeyByLspId keyInfo not found, for lsp id:"
                            + lspId + tmsGuid != null ? ",tmsGuid=" + tmsGuid : null
            );
            return null;
        }
        KeyStore keyStore = null;
        if (keyInfo.getKeystoreInfo() == null) {
            keyInfo.setKeystoreInfo(ctcConfigService.getCryptKeyStore());
        }
        try {
            InputStream keystoreStream = new FileInputStream(keyInfo.getKeystoreInfo().getPath());
            keyStore = AESEncryption.loadKeyStore(keyInfo.getKeystoreInfo().getKeystorePass(), keystoreStream);
        } catch (GeneralSecurityException e) {
            ctcLogService.LogError(
                    "Unable to load keystore, path=" + keyInfo.getKeystoreInfo().getPath()
                            + ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            ctcLogService.LogError(
                    "Unable to load keystore, path=" + keyInfo.getKeystoreInfo().getPath()
                            + ExceptionUtils.getStackTrace(e));
        }
        if (keyStore == null) {
            return null;
        }
        ctcLogService.LogDebug("Loaded keystore from path:" + keyInfo.getKeystoreInfo().getPath());
        Key foundKey = null;

        try {
            foundKey = keyStore.getKey(keyInfo.getAlias(), keyInfo.getKeyPass().toCharArray());
        } catch (GeneralSecurityException e) {
            if (ctcLogService != null) {
                ctcLogService.LogError("Failed to getKey on alias " + keyInfo.getAlias() + ". Exception:"
                        + ExceptionUtils.getStackTrace(e));
            }
        }

        return foundKey;
    }*/

    public static String encryptFile(File file, Key key)
            throws GeneralSecurityException, IOException {
        String path = file.getAbsolutePath();
        byte[] base64EncodedEncryptedMsg = AESEncryption.getEncryptedBytes(key, new FileInputStream(file));
//        String origAssetFilePath = path;
        String newPath = path + "." + Crypt.CRYPT_FILE_EXT;

        FileUtils.writeByteArrayToFile(new File(newPath),base64EncodedEncryptedMsg);

        return newPath;
    }

/*
    public static byte[] encryptByteArray(byte[] inputByteArray, Key key)
            throws GeneralSecurityException, IOException {

        byte[] base64EncodedEncryptedMsg = AESEncryption.getEncryptedBytes(key, new ByteArrayInputStream(inputByteArray));
//        String origAssetFilePath = path;
//        String newPath = path + "." + Crypt.CRYPT_FILE_EXT;
//
//        if (logService != null) {
//            logService.LogUploadDebug("writing to path:" + newPath);
//        }
//        FileUtils.writeByteArrayToFile(new File(newPath),base64EncodedEncryptedMsg);

        return base64EncodedEncryptedMsg;
    }
*/
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
        
        String encrypted = encryptFile(payloadFile, key);
    }
}
