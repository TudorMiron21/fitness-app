const { Server } = require("socket.io");
const { validateToken } = require("../utils/auth");
const MessageModel = require("../model/message");
const { useAzureSocketIO } = require("@azure/web-pubsub-socket.io");

async function initializeSocket(server) {
  const connectionString = process.argv[2];
  if (!connectionString) {
    throw new Error(
      "Azure Web PubSub connection string is required as a command-line argument."
    );
  }
  const io = new Server(server, {
    cors: {
      origin: ["http://localhost:3000","https://www.fit-stack.online"],
      methods: "*",
    },
  });

  await useAzureSocketIO(io, {
    hub: "Hub",
    connectionString: connectionString,
  });

  // io.engine.use(async (req, res, next) => {
  //   const isHandshake = req._query.sid === undefined;
  //   if (!isHandshake) {
  //     return next();
  //   }

  //   const header = req.headers["authorization"];

  //   if (!header) {
  //     return next(new Error("no token"));
  //   }

  //   if (!header.startsWith("Bearer ")) {
  //     return next(new Error("invalid token"));
  //   }

  //   const token = header.substring(7);

  //   const isTokenValid = await validateToken(token);
  //   if (!isTokenValid) return next(new Error("invalid token"));
  //   else next();
  // });

  io.on("connection", (socket) => {
    console.log(`User Connected: ${socket.id}`);

    socket.on("error", (error) => {
      console.error(`Error on socket ${socket.id}:`, error);
    });

    socket.on("join_private_chat", async (data) => {
      await socket.join(data);
      console.log(`User with ID: ${socket.id} joined room: ${data}`);
    });

    socket.on("send_message", async (data) => {
      const newMessage = new MessageModel({
        source_email: data.source_email,

        destination_email: data.destination_email,
        text_content: data.text_content,
        image_content: "",
        timeStamp: data.timeStamp,
        read: false,
      });

      try {
        await newMessage.save();
        socket.to(data.room).emit("receive_message", data);
      } catch (error) {
        console.error("Error saving message to MongoDB:", error);
      }
    });

    socket.on("disconnect", () => {
      console.log("User Disconnected", socket.id);
    });
  });

  return io;
}

module.exports = { initializeSocket };
