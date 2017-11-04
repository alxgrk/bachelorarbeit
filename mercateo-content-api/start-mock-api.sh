#!/bin/bash

set -e

trap handle_ctrl_c INT

function handle_ctrl_c() {
	set +x
	echo "** CTRL+C called... cleaning up"
	rm -f $ALTERED
	exit
}

RAML=api.raml
PORT=3030

ALTERED=$(mktemp -p . .tmpXXXXXXXX)
PLUGIN=osprey-mock-service
OUT=mock-api
PATHTO=${OUT}/node_modules/${PLUGIN}

cat $RAML | sed 's/securedBy/#securedBy/g' | sed 's/^baseUri/#baseUri/g' > ${ALTERED}

npm i --prefix $OUT $PLUGIN
set -x
node ${PATHTO}/bin/${PLUGIN} -f ${ALTERED} -p $PORT --cors

