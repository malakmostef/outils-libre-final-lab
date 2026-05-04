#!/bin/sh
# Gradle wrapper script
JAVACMD="java"
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
exec "$JAVACMD" \
  -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
  org.gradle.wrapper.GradleWrapperMain "$@"
