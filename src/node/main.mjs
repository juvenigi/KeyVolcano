import WebSocket from 'ws'

// Connect to the server
const ws = new WebSocket('ws://localhost:8080/repeat');
ws.on('error', console.error);

ws.on('open', function open() {
    ws.send('something');
});

ws.on('message', (data, isBinary) => {
    let isBuffer = Buffer.isBuffer(data);
    console.log('isBuffer: ', isBuffer)
    console.log('isBinary: ', isBinary)
    const message = isBuffer ? data.toString() : data;
    console.log('received: ', message);
});

// use this to keep node running
// setInterval(()=>void console.log("node keep-alive"),30_000);