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
    const uptimeSeconds = process.uptime();
    const uptimeHours = Math.floor(uptimeSeconds / 3600);
    let freeDisk = -1;
    try {
      let output = require('child_process').execSync("df -m / | tail -1 | awk '{print$4}'").toString();
      freeDisk = parseInt(output, 10);
    } catch (error) {
      console.error('Could not read disk space:', error);
    }
    data = timestamp + `: Uptime ${uptimeHours} hours, free disk in root: ${freeDisk} MBytes`;

    console.log('Data created! Sending it now:');
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