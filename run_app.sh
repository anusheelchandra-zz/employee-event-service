
sh ./stop_app.sh

echo "pulling event-service from docker hub"
docker pull anusheelchandra/event-service

echo "pulling employee-service from docker hub"
docker pull anusheelchandra/employee-service

echo "starting the applciation .Plesse wait ......"
docker-compose up -d

echo "waiting for application to start in 10 secs .."
sleep 10
echo "waiting for application to start in 5 secs .."
sleep 5
echo "waiting for application to start in 1 secs .."
sleep 1

