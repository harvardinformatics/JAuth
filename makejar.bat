set CLASSPATH=jars\i4jruntime.jar;jars\forms-1.1.0.jar;.

javac -cp %CLASSPATH% JAuth/AuthenticatorGUI.java

jar xf jars\forms-1.1.0.jar

echo Main-Class: JAuth.AuthenticatorGUI > manifest
echo Name: JAuth >> manifest
echo Implementation-Title: JAuth >> manifest
echo Implementation-Version: 1.0 >> manifest
echo Specification-Title: JAuth >> manifest
echo Specification-Version: 1.0 >> manifest
echo Created-By: Michele Clamp, James Cuff, John Brunelle >> manifest

jar cmf manifest JAuth.jar JAuth\*.class JAuth\fonts\*.ttf JAuth\logo\*.png JAuth\logo\*.icns com


