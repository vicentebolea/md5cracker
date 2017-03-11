: ${PASSWD_LEN:= 2 4 6 }  # You can modify those var. E.G.: $ PASSWD_LEN=5 ./run.sh
: ${NTHREAD:= 1 2 4 6 8 } #

echo "nthread, time2, time4, time6" > output

for n_thr in ${NTHREAD[@]}; do
  record=`echo -n "$n_thr, "`
  for i in ${PASSWD_LEN[@]}; do
    secretkey=`cat /dev/urandom | tr -dc 'a-z0-9' | fold -w $i | head -n 1`
    encryptedkey=`echo -n $secretkey | openssl md5 -r | cut -d ' ' -f1`
    echo "Decrypting secret key $secretkey"
    record+=$((({ /usr/bin/time -f "%e" java -jar build/jar/PasswordCrackerMultiThread.jar $n_thr $i false $encryptedkey; } ) 1>>./log ) 2>&1)
    record+=", "
  done
  echo "$record" >> output
done
