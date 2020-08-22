
sh ./stop_app.sh

echo "pulling event-service from docker hub"
docker pull anusheelchandra/event-service

echo "pulling employee-service from docker hub"
docker pull anusheelchandra/employee-service

echo "starting the applciation .Plesse wait ......"
docker-compose up -d

cnt=`docker ps | grep event-service_employee-service | awk '{print $1}'`
d=`docker logs $cnt| grep "Started EmployeeServiceApplication"|wc -l`
echo "waiting for application to start in 3 secs .."
sleep 1
echo "waiting for application to start in 2 secs .."
sleep 1
echo "waiting for application to start in 1 secs .."
sleep 1

