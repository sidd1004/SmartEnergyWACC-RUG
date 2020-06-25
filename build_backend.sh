cd ./back-end && sudo sbt docker:publishLocal
echo "---------------------------------------"
echo "---------STARTING DOCKER BUILD---------"
echo "---------------------------------------"
docker tag back-end swastikrug/back-end-test:latest
echo "---------------------------------------"
echo "---------PUSHING DOCKER BUILD---------"
echo "---------------------------------------"
docker push swastikrug/back-end-test:latest
echo "---------------------------------------"
echo "----------------DONE-------------------"
echo "---------------------------------------"