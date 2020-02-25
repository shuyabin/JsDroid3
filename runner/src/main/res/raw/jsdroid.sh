# path, abi64 should have been defined
if [ -z $abi64 ]; then
    echo "ERROR: please open JsDroid to make a new jsdroid.sh" >&2
    exit 1
fi
rm -rf /data/local/tmp/*
cp $server_apk /data/local/tmp/jsd_server.apk
cp $sdk_apk /data/local/tmp/sdk.apk
mkdir /data/local/tmp/jsd_opt
mkdir /data/local/tmp/jsd_lib
mkdir /data/local/tmp/jsd_classes
mkdir /data/local/tmp/jsd_classes_lib
jsdroid=/data/local/tmp/jsd_server

# some os cannot execute $path directly
if [ -f $path ]; then
    rm -rf $jsdroid
    cp $path $jsdroid
    if [ -f $jsdroid ]; then
        chmod 0755 $jsdroid
    else
        echo "WARNING: /data/local/tmp is not writable" >&2
    fi
elif [ ! -x $jsdroid ]; then
    echo "ERROR: please open jsdroid to make a new jsdroid.sh" >&2
    exit 1
fi

# some os is 64bit, but load 32bit library(and binary)
if [ x"$abi64" == x"false" -a -x /system/bin/app_process64 ]; then
    rm -rf /data/local/tmp/app_process
    ln -s /system/bin/app_process32 /data/local/tmp/app_process
    export PATH=/data/local/tmp:$PATH
fi


if [ -x $jsdroid ]; then
    exec $jsdroid
elif [ -x $path ]; then
    exec $path
else
    echo "cannot execute jsdroid" >&2
    exit 1
fi
