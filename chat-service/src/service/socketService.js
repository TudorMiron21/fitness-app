const { Server } = require("socket.io");
const { validateToken } = require("../utils/auth");
const MessageModel = require('../model/message'); 

function initializeSocket(server) {
  const io = new Server(server, {
    cors: {
      origin: "http://localhost:3000",
      methods: "*",
    },
  });

  io.engine.use(async (req, res, next) => {
    const isHandshake = req._query.sid === undefined;
    if (!isHandshake) {
      return next();
    }

    const header = req.headers["authorization"];

    if (!header) {
      return next(new Error("no token"));
    }

    if (!header.startsWith("Bearer ")) {
      return next(new Error("invalid token"));
    }

    const token = header.substring(7);

    const isTokenValid = await validateToken(token);
    if (!isTokenValid) return next(new Error("invalid token"));
    else next();
  });

  io.on("connection", (socket) => {
    console.log(`User Connected: ${socket.id}`);

    socket.on("error", (error) => {
      console.error(`Error on socket ${socket.id}:`, error);
    });

    socket.on("join_private_chat", (data) => {
      socket.join(data);
      console.log(`User with ID: ${socket.id} joined room: ${data}`);
    });

    socket.on("send_message", async (data) => {
      const newMessage = new MessageModel({
        source_email: data.source_email,
        destination_email: data.destination_email,
        text_content: data.text_content,
        image_content:"",
        time: data.time,
      });
    
      try {
        await newMessage.save();        
        socket.to(data.room).emit("receive_message", data);

      } catch (error) {
        console.error('Error saving message to MongoDB:', error);
      }
    });

    socket.on("disconnect", () => {
      console.log("User Disconnected", socket.id);
    });
  });

  return io;
}

module.exports = { initializeSocket };
