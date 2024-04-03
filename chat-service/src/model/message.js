const { mongoose } = require("mongoose");

const MessageSchema = new mongoose.Schema({
  senderEmail: { type: String, required: true },
  receiverEmail: { type: String, required: true },
  textContent: { type: String },
  imageContent: { type: String },
});

const MessageModel = mongoose.model("message", MessageSchema);

module.exports = MessageModel;
