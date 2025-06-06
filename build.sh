rm -rf out
mkdir -p out

echo "Compiling..."
javac -d out -cp src $(find src -name "*.java")

echo "Copying resources..."
cp -r src/resources out/
mkdir -p out/resources/savefiles

echo "Building jar..."
jar cfe Misle.jar com.ded.misle.Launcher -C out .

echo "Build done! You can now run the game by executing Misle.jar."