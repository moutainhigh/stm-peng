@echo off
set JAVA_HOME=D:\Java\jdk1.6.0_x64_45
"%JAVA_HOME%/bin/java" -cp "%JAVA_HOME%/lib/jconsole.jar;%JAVA_HOME%/lib/tools.jar;../third_lib/jmxremote_optional-1.0.1.03.jar" sun.tools.jconsole.JConsole