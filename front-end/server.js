const io = require('socket.io')();

io.on('connection', (client) => {
  client.on('subscribeToTimer', (interval) => {
    console.log('client is subscribing to timer with interval ', interval);
    setInterval(() => {
      min = 1;
      max = 100;
      obj = [
        {
          "id": "ac",
          "label": "Heating & AC",
          "value": min + Math.random() * (max - min),
          "color": "hsl(219, 70%, 50%)"
        },
        {
          "id": "ev",
          "label": "EV Charge",
          "value": min + Math.random() * (max - min),
          "color": "hsl(218, 70%, 50%)"
        },
        {
          "id": "refrigeration",
          "label": "Refrigeration",
          "value": min + Math.random() * (max - min),
          "color": "hsl(126, 70%, 50%)"
        }
      ];
      client.emit('energyData', obj);
    }, interval);
  });
});

const port = 8000;
io.listen(port);
console.log('listening on port ', port);