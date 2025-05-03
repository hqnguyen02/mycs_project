const express = require('express');

// Import the routes used for exercise plans
const apiRouter = require('./router/ApiRouter');

const cookieParser = require('cookie-parser');


const app = express();
const PORT = process.env.PORT;

app.use(express.json());
app.use(cookieParser());

app.use('/', apiRouter);


// As our server to listen for incoming connections
app.listen(PORT, () => console.log(`Server listening on port: ${PORT}`));