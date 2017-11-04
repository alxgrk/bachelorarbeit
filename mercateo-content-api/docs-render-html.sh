#!/bin/bash

set -e

which raml2html || {
	echo "Tool 'raml2html' not found in your \$PATH - install it via 'npm i -g raml2html'"
	exit 1
}


set -x
RAML=api.raml
HTML=api.html
ALTERED=$(mktemp ./tmp.XXXXXXXX)
DATESTRING=$(date +%c)
GENERATED="generated at ${DATESTRING}"
echo $GENERATED

(
	cd $(dirname ${0})

	TITLE_LINE_NR=$(sed -n '/^title/=' ${RAML})

	# looks weird, but helps to easily use maven coordinates
	mvn -q clean package
	source target/maven-archiver/pom.properties

	# insert html generation timestamp
	sed "${TITLE_LINE_NR}s/(placeholder[^)]*)/(${GENERATED})/" ${RAML} > ${ALTERED}
	# insert maven artifact version, ".bak" is Mac-specific
	sed -i'.bak' "s/\${version}/${version}/g" ${ALTERED}

	raml2html ${ALTERED} > ${HTML}
	rm -f ${ALTERED}*
)
