@echo off

set root=%1

set direc=%cd%
set jar=%cd%\decrypt-translation-util-3.0.0.jar

cd %root%

call :treeProcess

cd %root%

goto :eof

:treeProcess

for %%f in (*.xml) do (
    echo entrando en %cd%
    java -cp %jar% com.claytablet.cq5.ctctranslation.Utils.Crypt.Decrypt -s %direc%\ctt-keystore.jceks -w aJ3i49sjwF -i %%f -a clay-tablet-keys -p aJ3i49sjwF
)

for /D %%d in (*) do (
    cd %%d

    call :treeProcess

    cd ..
)

exit /b