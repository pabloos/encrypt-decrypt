FROM openjdk:7
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
CMD java -cp crypt-translation-util-3.0.0-jar-with-dependencies.jar  com.claytablet.cq5.ctctranslation.Utils.Crypt.Encrypt -s ctt-keystore.jceks -w aJ3i49sjwF -i test.xml -a clay-tablet-keys -p aJ3i49sjwF