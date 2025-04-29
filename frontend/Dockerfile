FROM node:20 AS build
LABEL authors="MarcinSz1993"
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build --prod
FROM nginx:1.25-alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
