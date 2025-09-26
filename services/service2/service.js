//"Timestamp (add actual timestamp): uptime <X> hours, free disk in root: <X> MBytes"

//HTTP POST => Storage

const express = require('express');
const { time } = require('node:console');
const app = express();
const port = 8199;

// Define a route for GET requests to the root URL
app.get('/status', (req, res) => {
  try{
    timestamp = new Date().toISOString();
    console.log(timestamp);
    data = timestamp + ': Uptime 5 hours, free disk in root: 20000 MBytes';
    var response = express.post('/log', {
      method: 'POST', message: data});
    console.log(data);
    res.set('Content-Type', 'text/plain');
    res.send(200, data);
  } catch (error) {
    res.status(400);
  }
});

// Start the server
app.listen(port, () => {
  console.log(`It's working!`);
});