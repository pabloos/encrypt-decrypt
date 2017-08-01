The "jar-with-dependencies" JAR can be used as a standalone utility to decrypt a file, encrypted by the AEM connector.

Arguments:
-s keystore file path
-w keystore pass
-i path of encrypted
-a key alias
-p key password

Sample usage:
> java -cp target\decrypt-translation-util-2.4.7-SNAPSHOT-jar-with-dependencies.jar com.claytablet.cq5.ctctranslation.Utils.Crypt.Crypt -s "C:\work\aem\keystore\ctt-keystore" -w mystorepass -i "C:\work\aem\ctctranslation_data\files\source\5fbbfc09-970c-47ab-b376-7c4f81a1f8e7.xml.cpt" -a cttkey -p secretpassword
<*** Decrypted output dumped on STDOUT ****>
