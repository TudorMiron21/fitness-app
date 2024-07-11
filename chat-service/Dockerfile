FROM node:16.14.0-alpine 

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

WORKDIR /app/src

EXPOSE 8084

CMD ["node", "index.js", "Endpoint=https://chat-service.webpubsub.azure.com;AccessKey=HfnWj7tA4rTtZdelq1CpjUbF8LHto2LIa4n79ROG9Mg=;Version=1.0;"]