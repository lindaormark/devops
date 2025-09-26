FROM node:current-alpine3.20

WORKDIR /app

COPY package*.json ./
RUN npm install --production

COPY . .

EXPOSE 8199
CMD ["npm", "start"]