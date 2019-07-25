#VERSION=$(shell cat VERSION)
WAR=delimited.jar
DOCKER=src/main/docker/$(WAR)
TARGET=target/$(WAR)
REPO=docker.lappsgrid.org
GROUP=nlp
IMAGE=delimited
TAG=$(GROUP)/$(IMAGE):$(VERSION)
NAME=$(GROUP)-$(IMAGE)

jar:
	mvn package

clean:
	mvn clean
	if [ -e $(DOCKER) ] ; then rm $(DOCKER) ; fi

docker:
	@if [ ! -e $(DOCKER) ] ; then cp $(TARGET) $(DOCKER) ; fi
	@if [ $(TARGET) -nt $(DOCKER) ] ; then cp $(TARGET) $(DOCKER) ; fi
	cd src/main/docker && docker build -t $(GROUP)/$(IMAGE) .
	
run:
	java -Xmx4G -jar $(TARGET)

start:
	docker run -d --name $(NAME) -v /private/etc/lapps:/etc/lapps $(GROUP)/$(IMAGE)

test:
	src/test/lsd/integration.lsd

stop:
	docker rm -f $(NAME)

push:
	docker tag $(GROUP)/$(IMAGE) $(REPO)/$(GROUP)/$(IMAGE)
	docker push $(REPO)/$(GROUP)/$(IMAGE)

tag:
	docker tag $(GROUP)/$(IMAGE) $(REPO)/$(GROUP)/$(IMAGE):$(VERSION)
	docker push $(REPO)/$(GROUP)/$(IMAGE):$(VERSION)

update:
	echo "Not implemented yet."
	#curl -X POST http://129.114.17.83:9000/api/webhooks/71f3953f-1c43-42aa-99a6-ab1cd55dc85e

all: clean jar docker 

deploy: all push update
	
help:
	@./help.sh
	
