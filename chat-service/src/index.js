const express = require('express');
const { eurekaClient } = require('./eureka-config');
const app = express();
const PORT = 8084;

const chatServiceRoutes = require('./routes/chatServiceRoutes');

app.use('/', chatServiceRoutes);


const server = app.listen(PORT, () => {
  console.log(`Node.js service running on port ${PORT}`);
  // Start Eureka client
  eurekaClient.start(error => {
    if (error) {
      console.error('Failed to register with Eureka');
      console.error(error);
    }
  });
});

// Capture SIGINT signal and deregister the service from Eureka before shutting down
process.on('SIGINT', () => {
  eurekaClient.stop(() => {
    console.log('Deregistered service from Eureka, shutting down.');
    server.close(() => {
      process.exit(0);
    });
  });
});
