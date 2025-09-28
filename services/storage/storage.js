import { createRequire } from "module";
const require = createRequire(import.meta.url);

//HTTP POST /log => append the incoming record persistently
//HTTP GET /log => gets the content of whole stored log

const fs = require('fs');
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const port = 8199;

app.use(bodyParser.json());

if (!fs.existsSync("/data")) fs.mkdirSync("/data", { recursive: true });
if (!fs.existsSync("/data/log.txt")) fs.writeFileSync("/data/log.txt", "");

app.post('/', (req, res) => {
    const postData = req;
    console.log('Received POST data:', postData);
    fs.writeFile("/data/log.txt", postData + '\n', { flag: 'a' }, (err) => {
        if (err) {
            console.error('Error writing to file:', err);
            return res.status(500, 'Internal Server Error');
        }
    res.status(200, 'Data received successfully');
    });
});

app.get('/', (req, res) => {
    const logData = fs.readFile('vstorage/log.txt', 'utf8', (err, data) => {
        if (err) {
            console.error('Error reading file:', err);
        }
    console.log('Log data:', logData);
    res.status(200);
    });
});

app.listen(port, () => {
    console.log(`Storage service listening at http://localhost:${port}`);
});