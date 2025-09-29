import { createRequire } from "module";
const require = createRequire(import.meta.url);

const fs = require('fs');
const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const port = 8199;

const app = express();
const logFile = path.join('/data', 'log.txt');

// Middleware to parse bodies
app.use(bodyParser.text({ type: 'text/plain' }));
app.use(bodyParser.text({ type: '*/*' }));

// Ensure /data directory and log file exist
if (!fs.existsSync("/data")) fs.mkdirSync("/data", { recursive: true });
if (!fs.existsSync(logFile)) fs.writeFileSync(logFile, "");

// Handle POST requests
app.post('/', (req, res) => {
    let postData = req.body;
    fs.writeFileSync(logFile, postData + '\n', { flag: 'a' }, (err) => {
        if (err) {
            console.error('Error writing to file:', err);
            return res.status(500, 'Internal Server Error');
        }
    res.status(200, 'Data received successfully');
    });
});

// Handle GET requests
app.get('/', (req, res) => {
    if (fs.existsSync("/data/log.txt")) {
        let logData = fs.readFileSync("/data/log.txt", 'utf8');
        console.log('Sending log data:\n', logData);
        res.type("text/plain").send(logData);
    } else {
        console.log('No log data found');
        res.type("text/plain").send("No log data found");
    }
});

// Start the server
app.listen(port, () => {
    console.log(`Storage service listening at http://localhost:${port}`);
});