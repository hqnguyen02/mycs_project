FROM node:lts
# Set working directory
WORKDIR /app

# install node modules
COPY ./package.json .
RUN npm install

# Copy all files from current directory to working dir in image
COPY ./src/src ./src
COPY ./src/public ./public
COPY ./src/eslint.config.js ./
COPY ./src/index.html ./
COPY ./src/vite.config.js ./

ENV PORT 80

ENTRYPOINT [ "npm", "start"]