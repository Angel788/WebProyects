#!/bin/bash

for arg in "$@"; do
  if [[ "$arg" == tipo=* ]]; then
    tipo="${arg#*=}"
  fi
done

mvn clean install 
if [ "$tipo" = "server" ]; then
  echo "Ejecutando servidor..."
  mvn exec:java@server
elif [ "$tipo" = "cliente" ]; then
  echo "Ejecutando cliente..."
  mvn exec:java@cliente
else
  echo "Se ejecuto el ssh"
  exit 1
fi
