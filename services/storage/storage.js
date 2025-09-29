import { log } from "console";
import { createRequire } from "module";
const require = createRequire(import.meta.url);

//HTTP POST /log => append the incoming record persistently
//HTTP GET /log => gets the content of whole stored log

const fs = require('fs');
const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');
const port = 8199;

const app = express();

const logFile = path.join('/data', 'log.txt');

app.use(bodyParser.text({ type: 'text/plain' }));
app.use(bodyParser.text({ type: '*/*' }));

if (!fs.existsSync("/data")) fs.mkdirSync("/data", { recursive: true });
if (!fs.existsSync(logFile)) fs.writeFileSync(logFile, "");

app.post('/', (req, res) => {
    let postData = req.body;
    console.log('Received POST data:', postData);
    fs.writeFileSync(logFile, postData + '\n', { flag: 'a' }, (err) => {
        if (err) {
            console.error('Error writing to file:', err);
            return res.status(500, 'Internal Server Error');
        }
    res.status(200, 'Data received successfully');
    });
});

app.get('/', (req, res) => {
    if (fs.existsSync("/data/log.txt")) {
        let logData = fs.readFileSync("/data/log.txt", 'utf8');
        console.log('Log data:', logData);
        res.type("text/plain").send(logData);
    } else {
        console.log('No log data found');
        res.type("text/plain").send("No log data found");
    }
});

app.listen(port, () => {
    console.log(`Storage service listening at http://localhost:${port}`);
});