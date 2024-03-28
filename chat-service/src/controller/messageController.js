const sendMessage = (req, res) => {
  const { senderId, receiverId } = req.params;
  const { message } = req.body;

  // Your logic to handle the message sending goes here
  // ...

  // Send a response back
  res
    .status(200)
    .json({ success: true, message: "Message sent successfully." });
};

const testController = (req, res) => {
  res.status(200).send("Welcome to the Chat Service API");
};

module.exports = {
  sendMessage,
  testController,
};
