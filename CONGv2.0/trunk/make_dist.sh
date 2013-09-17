#!/bin/sh
NAME=ConGv$1
mkdir $NAME
cp dist/*.jar $NAME
cp -r dist/lib $NAME
zip -r $NAME.zip $NAME
rm -r $NAME
