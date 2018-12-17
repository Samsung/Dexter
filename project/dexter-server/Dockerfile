FROM node:8.14.0-alpine


# Create app directory
ADD . /dexter
WORKDIR /dexter

# Install dependencies
RUN npm install

# Run app
CMD node server.js -database.host=$DBHOST -database.name=$DBNAME -database.user=$DBUSER -database.password=$DBPASSWORD

