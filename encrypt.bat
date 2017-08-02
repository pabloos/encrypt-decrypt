@echo off

set root=%1
set direc=%cd%
set jar=%cd%\decrypt-translation-util-3.0.0.jar

set number=0

call :treeProcess
goto :eof

:treeProcess

for %%f in (*.xml) do (
    java -cp %jar% com.claytablet.cq5.ctctranslation.Utils.Crypt.Encrypt -s %direc%\ctt-keystore.jceks -w aJ3i49sjwF -i %%f -a clay-tablet-keys -p aJ3i49sjwF

    set /a "number=%number%+1"
)

for /D %%d in (*) do (
    cd %%d

    call :treeProcess

    cd ..
)

exit /b

echo "encriptados %number% archivo(s)"
cd %direc%