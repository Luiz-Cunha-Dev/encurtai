FROM node:18-alpine

WORKDIR /app

COPY package.json ./
RUN npm install --production

COPY client.js ./

EXPOSE 5678

CMD ["npm", "start"]