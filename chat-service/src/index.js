const express = require("express");
// const { eurekaClient } = require("./eureka-config");
const { mongoose } = require("mongoose");
const chatServiceRoutes = require("./routes/chatServiceRoutes");
const MessageModel = require("./model/message");
const cors = require("cors");
const http = require("http");
const { initializeSocket } = require("./service/socketService");
const app = express();
const PORT = 8084;

app.use(cors());

server = http.createServer(app);

initializeSocket(server);

server.listen(PORT, () => {
  console.log(`Node.js service running on port ${PORT}`);
  // Start Eureka client
  // eurekaClient.start((error) => {
  //   if (error) {
  //     console.error("Failed to register with Eureka");
  //     console.error(error);
  //   }
  // });
});

app.use("/", chatServiceRoutes);

// Capture SIGINT signal and deregister the service from Eureka before shutting down
// process.on("SIGINT", () => {
//   eurekaClient.stop(() => {
//     console.log("Deregistered service from Eureka, shutting down.");
//     server.close(() => {
//       process.exit(0);
//     });
//   });
// });

mongoose.connect(
  "mongodb+srv://tudormiron19:Parola21@chat-service-cluster.4ybged7.mongodb.net/?retryWrites=true&w=majority&appName=chat-service-cluster"
);
