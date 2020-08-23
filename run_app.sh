
sh ./stop_app.sh

echo "starting the applciation .Plesse wait ......"
docker-compose up -d

echo "waiting for application to start in 30 secs .."
sleep 30
echo "waiting for application to start in 10 secs .."
sleep 10
echo "waiting for application to start in 5 secs .."
sleep 5
echo "Please check logs if you cannot access the service .."

