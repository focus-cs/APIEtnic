BATCH_PATH="/path/to/API/"
BATCH_MAIN="APIEtnic.jar"
SCIFORMA_URL="http://www.psnext.etnic.be/sciforma"

ROOT_DIR=$BATCH_PATH
LIB_DIR=$ROOT_DIR/lib

rm -f $LIB_DIR/utilities.jar
rm -f $LIB_DIR/PSClient.jar
rm -f $LIB_DIR/PSClient_en.jar

wget -O $LIB_DIR/utilities.jar $SCIFORMA_URL/utilities.jar
wget -O $LIB_DIR/PSClient_en.jar $SCIFORMA_URL/PSClient_en.jar
wget -O $LIB_DIR/PSClient.jar $SCIFORMA_URL/PSClient.jar

cd $ROOT_DIR

JAVA_ARGS="-showversion"
JAVA_ARGS="$JAVA_ARGS -Xms1024m"
JAVA_ARGS="$JAVA_ARGS -Xmx2048m"
JAVA_ARGS="$JAVA_ARGS -jar"

java $JAVA_ARGS $BATCH_MAIN
