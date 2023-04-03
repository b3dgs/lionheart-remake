@echo off
call mvn --file lionheart-parent/pom.xml org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout