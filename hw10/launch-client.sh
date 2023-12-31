#!/usr/bin/env bash

java \
--module-path jars \
--module client/ru.hse.consumer.Consumer \
$1 $2 $3
