//"Timestamp (add actual timestamp): uptime <X> hours, free disk in root: <X> MBytes"

//HTTP POST => Storage

const fs = require('node:fs');
const express = require('express');
const app = express();
const port = 8080;

// Define a route for GET requests to the root URL
app.get('/', (req, res) => {
  res.send('Hello World from Express!');
});

// Start the server
app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`);
});

fs.writeFile('docker-status.txt', 'I too like cats', { flag: 'a' }, (err) => {
    if (err) throw err;
})