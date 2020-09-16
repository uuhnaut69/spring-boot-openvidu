# Video Conference Example

An PoC "Video Conference" implement with Spring Boot, OpenVidu Java Client, Nuxt Js.

## Prerequisites

- Java 11+
- Node 10+
- Docker
- Docker-compose

## Manual Start

- Clone this project

```
git clone https://github.com/uuhnaut69/spring-boot-openvidu.git
```

- Setup environment

    - Inject env
    ```
  source dev.env
    ```
  
    - Start docker-compose
    
    ```
    docker-compose -f environment.yml up -d
    ```
    
    - Start server
    
    ```
    mvn spring-boot:run
    ```
  
    - Start client
    
    ```
    cd openvidu-fe && npm i && npm run dev
    ```
  
    Client endpoint: http://localhost:3000  

## (WIP) Docker Container Start

Start docker-compose

```
docker-compose -f all-in-one.yml up -d
```
