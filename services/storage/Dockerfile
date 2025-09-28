FROM node:current-alpine3.20

WORKDIR /app

COPY package*.json ./
RUN npm install --production

COPY . .
VOLUME ["/data"]

EXPOSE 8199
CMD ["npm", "start"]