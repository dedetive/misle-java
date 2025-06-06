@echo off
REM Remove previous build folder
rd /s /q out
mkdir out

echo Listing sources...
dir /B /S src\*.java > sources.txt

echo Compiling...
javac -d out -cp src @sources.txt

echo Copying resources...
xcopy /E /I /Y src\resources out\resources
mkdir out\resources\savefiles

echo Building jar...
jar cfe Misle.jar com.ded.misle.Launcher -C out .

echo.
echo Build done! You can now run the game by double-clicking Misle.jar.
pause