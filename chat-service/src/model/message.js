const { mongoose } = require("mongoose");

const MessageSchema = new mongoose.Schema({
  source_email: { type: String, required: true },
  destination_email: { type: String, required: true },
  text_content: { type: String },
  image_content: { type: String },
  time: { type: String, required: true },
});

const MessageModel = mongoose.model("message", MessageSchema);

module.exports = MessageModel;
