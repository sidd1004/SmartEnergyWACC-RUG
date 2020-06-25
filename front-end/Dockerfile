# FROM node:alpine as builder
# WORKDIR '/app'
# COPY package*.json ./
# # COPY nginx.conf /etc/nginx/nginx.conf
# RUN npm install
# COPY . .
# RUN npm run build

# FROM nginx
# EXPOSE 80
# COPY --from=builder /app/build /usr/share/nginx/html
# ENTRYPOINT ["nginx","-g","daemon off;"]

# build environment
FROM node:alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json /app/package.json
RUN npm install
RUN npm install react-scripts@3.0.1 -g
COPY . /app
RUN npm run build

# production environment
FROM nginx:1.16.0-alpine
COPY --from=build /app/build /usr/share/nginx/html
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]