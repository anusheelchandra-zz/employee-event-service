cd ./event-service

echo "Building event-service....."

mvn clean install -DskipTests=true

echo "Built event-service jar successfully"

echo "Building event-service docker image...."

docker build -t event-service .

echo "Built event-service docker image successfully"


cd ./..

cd ./employee-service

echo "Building employee-service....."

mvn clean install -DskipTests=true

echo "Built employee-service jar successfully"

echo "Builing event-service docker image....."

docker build -t employee-service .

echo "Built event-service docker image successfully"


cd ./..

echo "Starting  employee-event-service....."

docker-compose up -d