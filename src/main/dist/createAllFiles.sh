#!/usr/bin/env bash
#
# Europe PMC pipeline
#
. /etc/profile
APPNAME="europe-pmc-pipeline"
SERVER=`hostname -s | tr '[a-z]' '[A-Z]'`

APPDIR=/home/rgddata/pipelines/$APPNAME
cd $APPDIR

java -Dspring.config=$APPDIR/../properties/default_db2.xml \
    -Dlog4j.configuration=file://$APPDIR/properties/log4j.properties \
    -jar lib/$APPNAME.jar --rgdRef --genes --strains --qtls --ontRDO --ontGO --ontMamPhen --ontHumPhen --ontPathway"$@" > run.log 2>&1

mailx -s "[$SERVER] Europe PMC Pipeline Run" mtutaj@mcw.edu,llamers@mcw.edu < $APPDIR/logs/summary.log
