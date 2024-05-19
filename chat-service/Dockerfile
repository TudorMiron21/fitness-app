FROM node:16.14.0-alpine 

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

WORKDIR /app/src

EXPOSE 8084

CMD ["node", "index.js"]