@REM Maven wrapper that pins JAVA_HOME to the JDK 17 install this project builds against
@REM (avoids picking up an older default JDK on the PATH). Delegates to the standard mvnw.cmd.
@ECHO OFF
SET "JAVA_HOME=c:\jdk-17.0.8"
CALL "%~dp0mvnw.cmd" %*
