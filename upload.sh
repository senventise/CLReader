wget  -q https://github.com/timvisee/ffsend/releases/download/v0.2.52/ffsend-v0.2.52-linux-x64-static
mv ffsend-v0.2.52-linux-x64-static ffsend
chmod +x ffsend
./ffsend -q upload app/build/outputs/apk/debug/app-debug.apk > log.txt
#url="$SERVER_CHAN_URL`cat log.txt`"
curl --data-urlencode "desp=`cat log.txt`" $SERVER_CHAN_URL
echo "下载链接:"`cat log.txt`
