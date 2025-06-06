@echo off
REM Remove previous build folder
rd /s /q out
mkdir out

echo Compiling...
for /R src %%f in (*.java) do (
    echo Compiling %%f
)
javac -d out -cp src @findstr /S /M /C:".java" src\*.java

echo Copying resources...
xcopy /E /I /Y src\resources out\resources
mkdir out\resources\savefiles

echo Building jar...
jar cfe Misle.jar com.ded.misle.Launcher -C out .

echo.
echo Build done! You can now run the game by double-clicking Misle.jar.
pause