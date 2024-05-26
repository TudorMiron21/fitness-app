FROM node:16.14.0-alpine 

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

WORKDIR /app/src

EXPOSE 8084

CMD ["node", "index.js", "Endpoint=https://chat-service.webpubsub.azure.com;AccessKey=b+2hjMtYTonjLBg1uh1M663JGjkWdoigHVE2ugE5gp4=;Version=1.0;"]