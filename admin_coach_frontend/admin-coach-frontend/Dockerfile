FROM node:14-alpine as build

WORKDIR /app

COPY ./package.json ./
COPY ./package-lock.json ./

RUN npm install

COPY ./src /app/src
COPY ./public /app/public

RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]
