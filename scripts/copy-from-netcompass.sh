#!/bin/bash
#
# Copia os Pacotes de Interesse do netcompass para a Lib...
# @Author: Lucas Nishimura < lucas.nishimura at telefonica.com > 
#
#
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TMP_PATH="${DIR}/../../inventory-manager";

#
# Obtem o path do source do netcompass
#
NETCOMPASS_SRC="${1:-$TMP_PATH}"



#
# target packages
#

NC_SOURCE_PACKAGES=()
NC_SOURCE_PACKAGES+=("com.osstelecom.db.inventory.manager.dto")
NC_SOURCE_PACKAGES+=("com.osstelecom.db.inventory.manager.exception")
NC_SOURCE_PACKAGES+=("com.osstelecom.db.inventory.manager.jobs")
NC_SOURCE_PACKAGES+=("com.osstelecom.db.inventory.manager.request")
NC_SOURCE_PACKAGES+=("com.osstelecom.db.inventory.manager.resources")


echo "Netcompass SRC DIR: ${NETCOMPASS_SRC}"

if [ -d "${NETCOMPASS_SRC}" ]; then
   echo "Valid Directory";
else
   echo "Invalid Directory";
fi
