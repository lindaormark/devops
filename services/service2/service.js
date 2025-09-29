const express = require('express');
const app = express();
const port = 8199;

// Handle GET requests from Service1
app.get('/', (req, res) => {
  try{
    // Generate uptime and free disk space data
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

    res.set('Content-Type', 'text/plain');
    res.status(200).send(data);
    
    // Send the data to Storage service
    var response = express.post("http://storage:8199", {method: 'POST', message: data});
  } catch (error) {
    res.status(400);
  }
});

// Start the server
app.listen(port, () => {
  console.log(`It's working!`);
});