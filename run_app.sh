echo "pulling event-service from docker hub"
docker pull anusheelchandra/event-service

echo "pulling employee-service from docker hub"
docker pull anusheelchandra/employee-service

echo "starting the applciation .PLesse wait ......"
docker-compose up -d