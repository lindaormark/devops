//"Timestamp (add actual timestamp): uptime <X> hours, free disk in root: <X> MBytes"

//HTTP POST => Storage

const express = require('express');
const { time } = require('node:console');
const app = express();
const port = 8199;

// Define a route for GET requests to the root URL
app.get('/', (req, res) => {
  try{
    timestamp = new Date().toISOString();
    data = timestamp + ': Uptime 5 hours, free disk in root: 20000 MBytes';

    console.log(data);
    res.set('Content-Type', 'text/plain');
    res.status(200).send(data);
    var response = express.post("http://storage:8199", {method: 'POST', message: data});
  } catch (error) {
    res.status(400);
  }
});

// Start the server
app.listen(port, () => {
  console.log(`It's working!`);
});