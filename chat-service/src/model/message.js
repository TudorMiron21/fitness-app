const { mongoose } = require("mongoose");

const MessageSchema = new mongoose.Schema({
  source_email: { type: String, required: true, index: true },
  destination_email: { type: String, required: true, index: true },
  text_content: { type: String },
  image_content: { type: String }, // Assuming this is a URL or file path
  timestamp: { type: Date, required: true, default: Date.now }, // Combines date and time
  read: { type: Boolean, default: false },
});

const MessageModel = mongoose.model("message", MessageSchema);

module.exports = MessageModel;
