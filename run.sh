#!/bin/bash

mkdir -p target/classes

find src/main/java -name "*.java" > target/sources.txt

javac -encoding UTF-8 -d target/classes @target/sources.txt

if [ $? -ne 0 ]; then
    echo "Erro ao compilar o projeto."
    exit 1
fi

java -cp target/classes br.pucminas.matriculas.Aplicacao
