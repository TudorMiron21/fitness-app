const MessageModel = require("../model/message");

// const sendMessage = (req, res) => {
//   const { senderEmail, receiverEmail } = req.params;
//   const { message } = req.body;

//   // Your logic to handle the message sending goes here
//   // ...

//   // Send a response back
//   res
//     .status(200)
//     .json({ success: true, message: "Message sent successfully." });
// };

const getMessages = async (req, res) => {
  try {
    const { email1, email2 } = req.params;

    const messages = await MessageModel.find({
      $or: [
        { source_email: email1, destination_email: email2 },
        { source_email: email2, destination_email: email1 },
      ],
    });
    res.status(200).json({ success: true, messages: messages });
  } catch (error) {
    console.error("Error fetching messages:", error);
    res.status(500).json({ success: false, message: "Internal server error." });
  }
};

const getLastMessage = async (req, res) => {
  try {
    const { email1, email2 } = req.params;

    const lastMessage = await MessageModel.findOne({
      $or: [
        { source_email: email1, destination_email: email2 },
        { source_email: email2, destination_email: email1 },
      ],
    })
      .sort({ timestamp: -1 })
      .limit(1);

    res.status(200).json({ success: true, lastMessage: lastMessage });
  } catch (error) {
    console.error("Error fetching messages:", error);
    res.status(500).json({ success: false, message: "Internal server error." });
  }
};

const testController = (req, res) => {
  res.status(200).send("Welcome to the Chat Service API");
};

module.exports = {
  getMessages,
  testController,
  getLastMessage,
};
